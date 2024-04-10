package org.guanzon.appdriver.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class WebClient {
    public static String sendHTTP(String sURL, String sJSon, HashMap<String, String> headers) throws IOException{
        if (sURL.substring(0, 5).equalsIgnoreCase("https")){
            return httpsPostJSon(sURL, sJSon, headers);
        } else {
            return httpPostJSon(sURL, sJSon, headers);
        }        
    }
    
    private static String httpsPostJSon(String sURL, String sJSon, HashMap<String, String> headers) throws MalformedURLException, IOException {
        HttpsURLConnection conn = null;
        StringBuilder lsResponse = new StringBuilder();
        URL url = null;

        //Open network IO
        url = new URL(sURL);

        //opens a connection, then sends POST & set HTTP header nicely
        conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        if(headers != null){
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();

            for(Map.Entry<String, String> entry : entrySet) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        bw.write(sJSon);
        bw.flush();
        bw.close();

        if (!(conn.getResponseCode() == HttpsURLConnection.HTTP_CREATED ||
                conn.getResponseCode() == HttpsURLConnection.HTTP_OK)) {
            System.setProperty("store.error.info", String.valueOf(conn.getResponseCode()));
            System.out.println(System.getProperty("store.error.info"));
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
            lsResponse.append(output);
        }
        conn.disconnect();

        return lsResponse.toString();
    }
    
    private static String httpPostJSon(String sURL, String sJSon, HashMap<String, String> headers) throws MalformedURLException, IOException{
        HttpURLConnection conn = null;
        StringBuilder lsResponse = new StringBuilder();
        URL url = null;
        
        //Open network IO 
        url = new URL(sURL);

        //opens a connection, then sends POST & set HTTP header nicely
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("Content-Type", "application/json");
        
        if(headers != null){
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();

            for(Map.Entry<String, String> entry : entrySet) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

        }
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        bw.write(sJSon);
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
                lsResponse.append(output);
        }
        conn.disconnect(); 

        return lsResponse.toString();
    }

}
