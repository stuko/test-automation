package com.auto.test.jmeter.plugin.common.data;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;

public class FileJsonArrayListQueue extends FileJsonArrayListPlus{
    static Logger logger = LoggerFactory.getLogger(FileJsonArrayListQueue.class);
    static Map<String,FileJsonArrayListQueue> queueMap = new HashMap<>();
    LinkedList<FileJsonArrayListPlus> readable_queue = new LinkedList<>();
    int MAX = 1000;
    FileJsonArrayListPlus current_queue;
    static FileJsonArrayListQueue queueManager;

    private FileJsonArrayListQueue(String folder){
        super(folder);
    }
    private FileJsonArrayListQueue(String folder, String fileName) {
        super(folder, fileName);
    }

    public static FileJsonArrayListQueue getInstanceMap(String name, String folder){
        if(!queueMap.containsKey(name)){
            queueMap.put(name,new FileJsonArrayListQueue(folder));
        }
        return queueMap.get(name);
    }
    
    public static FileJsonArrayListQueue getInstance(String folder){
        if(queueManager == null) queueManager = new FileJsonArrayListQueue(folder);
        return queueManager;
    }

    public static FileJsonArrayListQueue getInstance(String folder, String fileName){
        if(queueManager == null) queueManager = new FileJsonArrayListQueue(folder,fileName);
        return queueManager;
    }

    public void setUnlimitMax(){
        this.MAX = Integer.MAX_VALUE;
    }
    
    @Override
    public synchronized void add(Map<String,String> m){
        this.changeQueue();
        super.add(m);
    }

    @Override
    public synchronized void write(String line){
        this.changeQueue();
        super.write(line);
    }

    @Override
    public void writeAll(FileJsonArrayListPlus listMap){
        this.changeQueue();
        super.writeAll(listMap);
    }
    
    public FileJsonArrayListPlus addCurrentQueue(){
        FileJsonArrayListPlus f = this.copy();
        this.readable_queue.add(f);
        this.newFile(this.getTestDataFolder());
        this.init();
        return f;
    }
    
    private void changeQueue(){
        if(this.size() == this.MAX){
            try {
                FileJsonArrayListPlus f = addCurrentQueue();
                logger.info("add readable_queue path : {} , queue-size : {}", f.getTestDataFilePath().getAbsolutePath(), this.readable_queue.size());
                logger.info("new writable_queue path : {} , size : {}", this.testDataFilePath.getAbsolutePath(), this.size());
            }catch(Exception e){
                logger.error(e.toString(),e);
            }
        }
    }

    public LinkedList<FileJsonArrayListPlus> getReadable_queue() {
        return readable_queue;
    }

    public void setReadable_queue(LinkedList<FileJsonArrayListPlus> readable_queue) {
        this.readable_queue = readable_queue;
    }

    @Override
    public String next(){
        return this.next(false);
    }
    
    public String peekFirst(){
        if(this.size() > 0) this.addCurrentQueue();
        current_queue = this.getReadable_queue().peekFirst();
        return current_queue.next();
    }
    
    public String next(boolean peek){
        // if(this.size() > 0) this.addCurrentQueue();
        String current = null;
        if(current_queue == null){
            if(peek){
                current_queue = this.getReadable_queue().peek();
            }else{
            	int tryCnt = 0;
                while(((current_queue = this.getReadable_queue().poll()) == null) && tryCnt++ < 1000){
                    try{logger.info("poll wait....");Thread.sleep(200);}catch(Exception e){logger.error(e.toString());}
                }
                if(current_queue == null) return null;
            }
        }
        current = current_queue.next();
        if(current == null){
            if(peek){
                current_queue = this.getReadable_queue().peek();
            }else{
            	int tryCnt = 0;
            	while(((current_queue = this.getReadable_queue().poll()) == null) && tryCnt++ < 10){
                    try{logger.info("poll re-wait....");Thread.sleep(200);}catch(Exception e){logger.error(e.toString());}
                }
            	if(current_queue == null) return null;
            }
            current = current_queue.next();
            // System.out.println("["+this.getTestDataFilePath().getAbsolutePath()+"]current: " + current);
        }
        return current;
    }

    public void writeLast(){
        FileJsonArrayListPlus f = this.copy();
        this.readable_queue.add(f);
    }

    public synchronized void removeAll(){
        readable_queue = new LinkedList<>();
        current_queue = null;
        queueManager = null;
        this.init();
    }

}
