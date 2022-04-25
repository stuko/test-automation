package com.auto.test.jmeter.plugin.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpUtil {
    static Logger logger = LoggerFactory.getLogger(TcpUtil.class);

    public static void send(String ip, String port, String msg){
        Socket socket = null;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        PrintWriter pw = null ;
        try{
            socket = new Socket(ip,Integer.parseInt(port));
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            pw = new PrintWriter(osw);
            pw.print(msg);
            if(pw != null) pw.close();
            if(osw != null) osw.close();
            if(os != null) os.close();
            if(socket != null) socket.close();
            pw = null;
            osw = null;
            os = null;
            socket = null;
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            try{if(pw != null) pw.close();}catch(Exception e){}
            try{if(osw != null) osw.close();}catch(Exception e){}
            try{if(os != null) os.close();}catch(Exception e){}
            try{if(socket != null) socket.close();}catch(Exception e){}
        }
    }

}
