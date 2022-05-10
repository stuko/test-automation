package com.auto.test.jmeter.plugin.common.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ShellServer {

    ServerSocket serverSocket;

    public static ShellServer shellServer;

    static{
        shellServer = new ShellServer();
        if(System.getProperty("SHELL_PORT") != null){
            shellServer.start(Integer.parseInt(System.getProperty("SHELL_PORT")));
        }else{
            shellServer.start(9999);
        }
    }

    private ShellServer(){}

    public static ShellServer getInstance(){
        if(shellServer == null){
            shellServer = new ShellServer();
        }
        return shellServer;
    }

    public void start(int port){
        try {
            if(serverSocket == null) {
                serverSocket = new ServerSocket(port);
                // serverSocket.setSoTimeout(10000);
                System.out.println("Listening... " + port);
                Socket socket = serverSocket.accept();
                // socket.setSoTimeout(10000);
                InputStream input = null;
                BufferedReader reader = null;
                try {
                    input = socket.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    String line = reader.readLine();
                    System.out.println(line);
                    executeShell(line);
                    socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (reader != null) reader.close();
                    } catch (Exception ee) {
                    }
                    try {
                        if (input != null) input.close();
                    } catch (Exception ee) {
                    }
                    try {
                        if (socket != null) socket.close();
                    } catch (Exception ee) {
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{if(serverSocket != null) serverSocket.close();}catch(Exception ee){}
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeShell(String cmd) {

        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        StringBuffer successOutput = new StringBuffer(); // 성공 스트링 버퍼
        StringBuffer errorOutput = new StringBuffer(); // 오류 스트링 버퍼
        BufferedReader successBufferReader = null; // 성공 버퍼
        BufferedReader errorBufferReader = null; // 오류 버퍼
        String msg = null; // 메시지

        List<String> cmdList = new ArrayList<String>();

        // 운영체제 구분 (window, window 가 아니면 무조건 linux 로 판단)
        if (System.getProperty("os.name").indexOf("Windows") > -1) {
            cmdList.add("cmd");
            cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        }
        // 명령어 셋팅
        cmdList.add(cmd);
        String[] array = cmdList.toArray(new String[cmdList.size()]);

        try {

            System.out.println("command : " + cmd);
            // 명령어 실행
            process = runtime.exec(array);

            // shell 실행이 정상 동작했을 경우
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));

            while ((msg = successBufferReader.readLine()) != null) {
                successOutput.append(msg + System.getProperty("line.separator"));
            }

            // shell 실행시 에러가 발생했을 경우
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
                errorOutput.append(msg + System.getProperty("line.separator"));
            }

            // 프로세스의 수행이 끝날때까지 대기
            process.waitFor();

            // shell 실행이 정상 종료되었을 경우
            if (process.exitValue() == 0) {
                System.out.println("성공");
                System.out.println(successOutput.toString());
            } else {
                // shell 실행이 비정상 종료되었을 경우
                System.out.println("비정상 종료");
                System.out.println(errorOutput.toString());
            }

            // shell 실행시 에러가 발생
            if (errorOutput.toString() != null && errorOutput.toString().length() > 0 ) {
                // shell 실행이 비정상 종료되었을 경우
                System.out.println("오류");
                System.out.println(errorOutput.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        ShellServer.getInstance();
    }
}
