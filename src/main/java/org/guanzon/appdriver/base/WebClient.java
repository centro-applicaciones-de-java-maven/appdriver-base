package org.guanzon.appdriver.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public static boolean SendSystemPanaloRaffleNotification(String app,
                                                       String userid,
                                                       String title,
                                                       String message){
        try{
            String sURL = "https://restgk.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);
            JSONObject param = new JSONObject();
            param.put("type", "00000");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", null);

            String response = sendHTTP(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     *
     * @param app set recipient product id
     * @param userid set recipient user id
     * @param title notification title
     * @param message notification message
     * type of Panalo notification 0 = raffle, 1 = reward, 2 = claim, 3 = redeemed, 4 = warning
     * @param status status of panalo raffle 0 = No Status, 1 = Starting Soon, 2 = Started, 3 = Ended
     * @return returns true if process has been successfully executed.
     */
    public static boolean SendSystemPanaloRaffleStartEndNotification(String app,
                                                       String userid,
                                                       String title,
                                                       String message,
                                                       int status){
        try{
            String sURL = "https://restgk.guanzongroup.com.ph/notification/send_request_system.php";
            Calendar calendar = Calendar.getInstance();
            //Create the header section needed by the API
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", "IntegSys");
            headers.put("g-api-imei", "356060072281722");
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-user", "GAP0190001");
            headers.put("g-api-mobile", "09171870011");
            headers.put("g-api-token", "cPYKpB-pPYM:APA91bE82C4lKZduL9B2WA1Ygd0znWEUl9rM7pflSlpYLQJq4Nl9l5W4tWinyy5RCLNTSs3bX3JjOVhYnmCpe7zM98cENXt5tIHwW_2P8Q3BXI7gYtEMTJN5JxirOjNTzxWHkWDEafza");

            JSONArray rcpts = new JSONArray();
            JSONObject rcpt = new JSONObject();
            rcpt.put("app", app);
            rcpt.put("user", userid);
            rcpts.add(rcpt);

            JSONObject loInfo = new JSONObject();
            loInfo.put("module", "002");
            loInfo.put("panalo", "0");

            JSONObject loData = new JSONObject();
            loData.put("status", status);

            loInfo.put("data", loData);

            String lsInfoxx = loInfo.toJSONString();

            JSONObject param = new JSONObject();
            param.put("type", "00008");
            param.put("parent", null);
            param.put("title", title);
            param.put("message", message);
            param.put("rcpt", rcpts);
            param.put("infox", lsInfoxx);

            String response = sendHTTP(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
                System.exit(1);
            }

            System.out.println(response);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
