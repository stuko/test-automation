package com.auto.test.jmeter.plugin.common.data;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.auto.test.jmeter.plugin.common.function.FileJsonArrayListFunction;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileJsonArrayList {
    static Logger logger = LoggerFactory.getLogger(FileJsonArrayList.class);
    private String name;
    protected File testDataFilePath;
    protected File testDataFolder;
    private Scanner scanner;
    int size = 0;

    public FileJsonArrayList(){}

    public FileJsonArrayList(String folder){
        try {
            if(!new File(folder).exists()) new File(folder).mkdirs();
            boolean retry = true;
            while(retry) {
                try{
                    setTestDataFilePath(this.getFile(folder));
                    retry = false;
                }catch(Exception e){
                    Thread.sleep(100);
                    logger.error(e.toString());
                }
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    public FileJsonArrayList(String folder, String fileName) {
        try {
            setTestDataFilePath(this.getFile(folder, fileName));
        }catch(Exception e){logger.error(e.toString(),e); }
    }

    public File getFile(String folder){
        try {
            if(!new File(folder).exists()) new File(folder).mkdirs();
            this.setTestDataFolder(new File(folder));
            this.setName(TestPluginMessageFactorImplFactory.getRandomCharacter(10).toString() + "-" + System.nanoTime());
            return new File(this.getTestDataFolder(), this.getName());
        }catch(Exception e){logger.error(e.toString(),e); return null; }
    }

    public File getFile(String folder, String fileName){
        try {
            if(!new File(folder).exists()) new File(folder).mkdirs();
            this.setTestDataFolder(new File(folder));
            this.setName(fileName);
            return new File(this.getTestDataFolder(), this.getName());
        }catch(Exception e){logger.error(e.toString(),e); return null; }
    }

    public File newFile(File folder){
        try {
            this.setName(TestPluginMessageFactorImplFactory.getRandomCharacter(10).toString() + "-" + System.nanoTime());
            this.setTestDataFolder(folder);
            this.setTestDataFilePath(new File(this.getTestDataFolder(), this.getName()));
            return this.getTestDataFilePath();
        }catch(Exception e){logger.error(e.toString(),e); return null; }
    }

    public File getTestDataFilePath() {
        return testDataFilePath;
    }
    public void setTestDataFilePath(File testDataFilePath) {
        this.testDataFilePath = testDataFilePath;
    }

    public String next(){
         if(scanner == null) {
             try {
                 scanner = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(this.getTestDataFilePath()), TestPluginConstants.file_encoding)));
             } catch (FileNotFoundException | UnsupportedEncodingException e) {
                 logger.error(e.toString(),e);
             }
         }

         if(scanner.hasNext()){
             // logger.info("scanner has next()");
             return scanner.nextLine();
         }else{
             if(scanner == null) scanner.close();
             // logger.info("scanner not has next() , {} , {}" , this.getTestDataFilePath().getAbsolutePath(), this.getTestDataFilePath().length());
             scanner = null;
             return null;
         }
    }

    public void setScannerNull(){
        this.scanner = null;
    }

    public List<Map<String,String>> readAll(){
        List<Map<String,String>> result = new ArrayList<>();
        Gson gson = new Gson();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8)){
            String line = null;
            while((line = br.readLine()) != null){
                result.add(gson.fromJson(line,Map.class));
            }
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
        return result;
    }

    public Map<String,String> get(int idx){
        String line = null;
        Gson gson = new Gson();
        try {
            Scanner fileScanner = new Scanner(this.getTestDataFilePath());
            int cur = 0;
            while(cur <= idx && fileScanner.hasNext()) {
                line = fileScanner.nextLine();
                logger.info("read : {}" , line);
                cur++;
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        if(line != null) return gson.fromJson(line,Map.class);
        else return null;
    }

    public synchronized void add(Map<String,String> m){
        Gson gson = new Gson();
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8, StandardOpenOption.CREATE,StandardOpenOption.APPEND))){
            size++;
            logger.info("write : {}" , m);
            pw.println(gson.toJson(m));
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public synchronized void write(String line){
        Gson gson = new Gson();
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8, StandardOpenOption.CREATE,StandardOpenOption.APPEND))){
            size++;
            logger.info("write : {}" , line);
            pw.println(line);
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public void forEach(FileJsonArrayListFunction fnc){
        Gson gson = new Gson();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8)){
            String line = null;
            while((line = br.readLine()) != null){
                // fnc.read(gson.fromJson(line,Map.class));
                fnc.read(line);
            }
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public void writeAll(FileJsonArrayList listMap){
        Gson gson = new Gson();
        this.size = 0;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8, StandardOpenOption.CREATE,StandardOpenOption.APPEND))){
            listMap.forEach(m->{
                size++;
                logger.info("writeAll : {}", m);
                // pw.println(gson.toJson(m));
                pw.println(m);
            });
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public void writeMapAll(List<Map<String,String>> listMap){
        Gson gson = new Gson();
        this.size = 0;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8))){
            listMap.forEach(m->{
                try {
                    size++;
                    pw.println(gson.toJson(m));
                } catch (Exception e) {
                    logger.error(e.toString(),e);
                }
            });
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public void writeStringAll(List<String> listMap){
        Gson gson = new Gson();
        this.size = 0;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(this.getTestDataFilePath().getAbsolutePath()), StandardCharsets.UTF_8))){
            listMap.forEach(m->{
                try {
                    size++;
                    pw.println(m);
                } catch (Exception e) {
                    logger.error(e.toString(),e);
                }
            });
        } catch (Exception e) {
            logger.error("File {} does not exist", this.getTestDataFilePath().getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public int size(){
        return this.size;
    }

    public void clear(){
        getTestDataFilePath().delete();
        this.size = 0;
    }

    public File getTestDataFolder() {
        return testDataFolder;
    }

    public void setTestDataFolder(File testDataFolder) {
        this.testDataFolder = testDataFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
