package com.kcb.fds.jmeter.plugin.common.util;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SecurityUtil {

    static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    static Gson gson = new Gson();

    public static String sendByHttpClient(String url, Map<String,Object> parameter){
        return callRest(url, gson.toJson(parameter));
    }

    public static String callRest(String url, String parameter) {
        String body = "";
        try{
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost postRequest = new HttpPost(url); //POST 메소드 URL 새성
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Connection", "keep-alive");
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setEntity(new StringEntity(parameter)); //json 메시지 입력
            HttpResponse response = client.execute(postRequest);
            //Response 출력
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                body = handler.handleResponse(response);
                System.out.println(body);
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return body;
    }

    public static String encode(String data){
        Map<String,Object> m = new HashMap<>();
        try {
            m.put("decrypted", data);
            String json = sendByHttpClient("http://192.168.57.254:19990/api/security/encrypt", m);
            m = gson.fromJson(json, Map.class);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        if(m != null && m.containsKey("encrypted")) return m.get("encrypted")+"";
        else return "";
    }

}
