package com.auto.test.jmeter.plugin.common.util;

import com.auto.test.jmeter.plugin.common.function.HttpCallBack;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

import java.io.IOException;

public class HttpUtil {
    static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    static OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void call(String url , String json, HttpCallBack callBack){
        try {
            RequestBody requestBody = null;
            if(json != null && json.trim().length() != 0){
                requestBody = RequestBody.Companion.create(json, JSON);
            }else{
                requestBody = RequestBody.Companion.create(json,MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
            }
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    callBack.response(body.string());
                    body.close();
                }else{
                    logger.error("No response...");
                }
            }
            try {
                if (response.body() != null) response.body().close();
                if (response != null) response.close();
            }catch(Exception ee){}
        } catch (IOException e) {
            logger.error(e.toString(),e);
        }
    }
    
    public static void uploadFileFromOkhttp(String url, String filePath){
        try{
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            File file = new File(filePath);
            builder.addFormDataPart("file" , file.getName() , RequestBody.create(file,MediaType.parse("application/octet-stream")));
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
            Response response = client.newCall(request).execute();
            try {
                if (response.body() != null) response.body().close();
                if (response != null) response.close();
            }catch(Exception ee){}
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }
}
