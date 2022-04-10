package com.kcb.fds.jmeter.plugin.common.data;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.function.FileJsonArrayListFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileJsonArrayListPlus extends FileJsonArrayList{

    static Logger logger = LoggerFactory.getLogger(FileJsonArrayListPlus.class);
    private Map<Integer,Long> filePointer = new HashMap<>();
    int current;

    public FileJsonArrayListPlus(){super();}

    public FileJsonArrayListPlus(String folder) {
        super(folder);
        current = 1;
        this.filePointer.put(current,0L);
    }

    public FileJsonArrayListPlus(String folder,String fileName) {
        super(folder,fileName);
        current = 1;
        this.filePointer.put(current,0L);
    }

    private void addCurrent(){
        current++;
    }

    public int getCurrent(){
        return this.current;
    }

    @Override
    public void forEach(FileJsonArrayListFunction fnc){
        try (RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");){
            for(int i = 1; i < current; i++){
                // logger.info("forEach : {} of {}" , i , current);
                file.seek(this.getFilePointer().get(i));
                String line = new String(file.readLine().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                fnc.read(line);
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public List<Map<String,String>> readAll(){
        List<Map<String,String>> result = new ArrayList<>();
        Gson gson = new Gson();
        try (
            RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");
        ){
            String line = null;
            while((line = file.readLine()) != null){
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                // System.out.println(line);
                result.add(gson.fromJson(line,Map.class));
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }

    @Override
    public synchronized void add(Map<String,String> m){
        Gson gson = new Gson();
        try (RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");){
            file.seek(file.length());
            this.getFilePointer().put(current, file.getFilePointer());
            file.write((gson.toJson(m)+"\n").getBytes(StandardCharsets.UTF_8));
            addCurrent();
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    @Override
    public Map<String,String> get(int idx){
        String line = null;
        Gson gson = new Gson();
        try (RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");){
            file.seek(this.getFilePointer().get(idx));
            line = file.readLine();
            line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        if(line != null) return gson.fromJson(line,Map.class);
        else return null;
    }

    @Override
    public synchronized void write(String line){
        Gson gson = new Gson();
        try (RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");){
            file.seek(file.length());
            this.getFilePointer().put(current, file.getFilePointer());
            file.write((line+"\n").getBytes(StandardCharsets.UTF_8));
            addCurrent();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    public void writeAll(FileJsonArrayListPlus listMap){
        Gson gson = new Gson();
        current = 1;
        try {
            RandomAccessFile file = new RandomAccessFile(this.getTestDataFilePath().getAbsolutePath(), "rw");
            listMap.forEach(m->{
                try {
                    this.getFilePointer().put(current, file.getFilePointer());
                    file.write((m+"\n").getBytes(StandardCharsets.UTF_8));
                    addCurrent();
                }catch(Exception ee){
                    logger.error(ee.toString(),ee);
                }
            });
            file.close();
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public Map<Integer, Long> getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(Map<Integer, Long> filePointer) {
        this.filePointer = filePointer;
    }

    public int size(){
        return this.current - 1;
    }

    @Override
    public void clear(){
        super.clear();
        this.current = 0;
    }

    public FileJsonArrayListPlus copy(){
        FileJsonArrayListPlus f = new FileJsonArrayListPlus();
        f.setTestDataFolder(this.getTestDataFolder());
        f.setTestDataFilePath(this.getTestDataFilePath());
        f.filePointer.put(current,0L);
        return f;
    }

    public void init(){
        this.size = 0;
        this.current = 0;
        this.setScannerNull();
    }

}
