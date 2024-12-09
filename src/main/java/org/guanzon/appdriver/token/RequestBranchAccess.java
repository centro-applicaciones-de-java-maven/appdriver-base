/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.appdriver.token;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GProperty;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.base.WebClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author sayso
 */
public class RequestBranchAccess {
    public static GProperty gProp; 
    public static String url = "https://restgk.guanzongroup.com.ph/x-api/v1.0/auth/access_request.php";
    
    public static void main(String[] args) throws IOException{
        String sproperty="";
        if(args.length == 0){
            sproperty = "D:/GGC_Maven_Systems/config/Binlog";
        }
        else if(args.length == 1){
            sproperty = args[0];
        }

        //load property
        loadConfig(sproperty);

        //Sample code snippet of executing a program in java
        //String xa[] = new String[] {"IntegSys", "GGC_BM001", "GAP0190001"};
        //RequestToken.main(xa);
        
        Map<String, String> headers = 
                        new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        String args_0 = gProp.getConfig("sys.default.branch.token");
        String args_1 = gProp.getConfig("sys.default.branch.access");
        
        JSONParser oParser = new JSONParser();

        try {
            JSONObject oJson = (JSONObject)oParser.parse(new FileReader(args_0));
            headers.put("g-client-key", (String)oJson.get("client_key"));
        } catch (ParseException ex) {
            Logger.getLogger(RequestAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        //JSONParser oParser = new JSONParser();
        //JSONObject json_obj = null;

        //String response = WebClient.httpPostJSon(sURL, param.toJSONString(), (HashMap<String, String>) headers);
        //System.out.println("Try...");
        String response = WebClient.sendHTTP(url, null, (HashMap<String, String>) headers);
        //System.out.println("After try...");
        if(response == null){
            System.out.println("HTTP Error detected: " + System.getProperty("store.error.info"));
            //return null;
            System.exit(1);
        }

        try {
            
            JSONObject oJson = (JSONObject) oParser.parse(response);
            
            if(((String)oJson.get("result")).equalsIgnoreCase("success")){
                JSONObject oData = new JSONObject();
                
                oData.put("access_key", (String)((JSONObject)oJson.get("payload")).get("token"));
                //oData.put("client_key", (String)((JSONObject) oParser.parse((String)oJson.get("payload"))).get("token"));
                oData.put("created", dateFormat(Calendar.getInstance().getTime(), SQLUtil.FORMAT_TIMESTAMP));
                oData.put("parent", args_0);

                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(args_1),"UTF-8");
                out.write(oData.toJSONString());
                out.flush();
                out.close();
            }
            
        } catch (ParseException ex) {
            //Logger.getLogger(RequestToken.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        //json_obj = (JSONObject) oParser.parse(response);
        //System.out.println(json_obj.toJSONString());
        System.out.println(response);
    }
    
   private static String dateFormat(Object date, String format){
      SimpleDateFormat sf = new SimpleDateFormat(format);
      String ret;
      if ( date instanceof Timestamp )
         ret = sf.format((Date)date);
      else if ( date instanceof Date )
         ret = sf.format(date);
      else if ( date instanceof Calendar ){
         Calendar loDate = (Calendar) date;
         ret = sf.format(loDate.getTime());
         loDate = null;
      }
      else
         ret = null;

      sf = null;
      return ret;
    }    
   private static void loadConfig(String sprop){
         gProp = new GProperty(sprop);
   }
    
}
