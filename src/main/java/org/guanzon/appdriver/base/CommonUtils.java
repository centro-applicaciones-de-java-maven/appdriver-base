package org.guanzon.appdriver.base;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javax.sql.rowset.CachedRowSet;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONArray;

public class CommonUtils {
    private final static String pxeModuleName = CommonUtils.class.getSimpleName();
    
    public static String getEnv(String fsValue){
        String lsValue = System.getenv(fsValue);
        return lsValue == null ? "" : lsValue;
    }
    
    public static String getConfiguration(GRider foGRider, String fsConfigID){        
        String lsSQL = "SELECT" + 
                            "  sValuexxx" +
                        " FROM xxxOtherConfig" +
                        " WHERE sProdctID = " + SQLUtil.toSQL(foGRider.getProductID()) +
                            " AND sConfigId = " + SQLUtil.toSQL(fsConfigID);
        
        ResultSet loRS = foGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next()){
                return loRS.getString("sValuexxx");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getConfiguration(GRider foGRider, String fsConfigID, String fsBranchCd){
        String lsSQL = "SELECT " + fsConfigID + " sValuexxx" +
                            " FROM xxxOtherInfo a" +
                               " LEFT JOIN xxxSysClient b" +
                                  " ON a.sClientID = b.sClientID" +
                            " WHERE sBranchCd = " + SQLUtil.toSQL(fsBranchCd);
        
        ResultSet loRS = foGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next()){
                return loRS.getString("sValuexxx");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getNextReference(Connection foConn, String fsTableNme, String fsFieldNme, boolean bByYear){
        if (foConn == null) return "";
        if (fsTableNme.isEmpty()) return "";
        if (fsFieldNme.isEmpty()) return "";
        
        String lsSQL = ""; 
        String lsPref = "";
        int lnNext = 0;
        
        Statement loStmt = null;
        ResultSet loRS = null;
        
        try {
            if (bByYear){
                if(foConn.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }  
                
                loStmt = foConn.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                lsPref = lsPref + loRS.getString(1).substring(2);
            }          
            
            lsSQL = "SELECT " + fsFieldNme + 
                        " FROM " + fsTableNme +
                        " ORDER BY " + fsFieldNme + " DESC LIMIT 1"; 
            
            loStmt = foConn.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            
            if (loRS.next()) lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            
            lsSQL = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");;
            
        } catch (SQLException ex) {
            System.err.print(ex.getMessage());
            lsSQL = "";
        } finally{
            MiscUtil.close(loRS);
            MiscUtil.close(loStmt);
        }
        
        return lsSQL;
    }
    
    public static String getNextBarcode(Connection foConn, String fsTableNme, String fsFieldNme, boolean bByYear){
        if (foConn == null) return "";
        if (fsTableNme.isEmpty()) return "";
        if (fsFieldNme.isEmpty()) return "";
        
        String lsSQL = ""; 
        String lsPref = "";
        int lnNext = 0;
        
        Statement loStmt = null;
        ResultSet loRS = null;
        
        try {
            if (bByYear){
                if(foConn.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }  
                
                loStmt = foConn.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                lsPref = lsPref + loRS.getString(1).substring(2);
            }          
            
            lsSQL = "SELECT " + fsFieldNme + 
                        " FROM " + fsTableNme +
                        " HAVING " +  fsFieldNme + " REGEXP '^-?[0-9]+$'" +
                        " ORDER BY " + fsFieldNme + " DESC LIMIT 1"; 
            
            loStmt = foConn.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            
            if (loRS.next()) lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            
            lsSQL = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");;
            
        } catch (SQLException ex) {
            System.err.print(ex.getMessage());
            lsSQL = "";
        } finally{
            MiscUtil.close(loRS);
            MiscUtil.close(loStmt);
        }
        
        return lsSQL;
    }
    
    public static void showModal(Application foObj) throws Exception{
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        foObj.start(stage);     
    }
    
    public static String loadData(ResultSet foSource){
        String lsValue = "";
        
        try {
            foSource.absolute(1);
            for (int lnCtr = 1; lnCtr <= foSource.getMetaData().getColumnCount(); lnCtr++){
                lsValue = lsValue + foSource.getString(lnCtr) + "»";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return lsValue.substring(0, lsValue.length()-1);
    }
    
    public static JSONObject loadJSON(ResultSet foSource){
        JSONObject loJSON = new JSONObject();

        try {
            if (MiscUtil.RecordCount(foSource) <= 0){
                return null;
            }
            
            foSource.absolute(1);
            for (int lnCtr = 1; lnCtr <= foSource.getMetaData().getColumnCount(); lnCtr++){
                //getColumnName = returns the column name
                //loJSON.put(foSource.getMetaData().getColumnName(lnCtr), foSource.getString(lnCtr));

                //getColumnLabel = returns the column alias
                loJSON.put(foSource.getMetaData().getColumnLabel(lnCtr), foSource.getString(lnCtr));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return loJSON;
    }   
    
    public static JSONArray RS2JSON(CachedRowSet foSource){        
        JSONArray loArray = new JSONArray();
        JSONObject loJSON;
        
        try {
            foSource.beforeFirst();
            while (foSource.next()){
                loJSON = new JSONObject();
                
                for (int lnCtr = 1; lnCtr <= foSource.getMetaData().getColumnCount(); lnCtr++){
                    switch(foSource.getMetaData().getColumnType(lnCtr)){
                        case java.sql.Types.TIMESTAMP:
                            if (foSource.getObject(lnCtr) != null)
                                loJSON.put(lnCtr, SQLUtil.dateFormat(foSource.getObject(lnCtr), SQLUtil.FORMAT_TIMESTAMP));
                            else
                                loJSON.put(lnCtr, foSource.getObject(lnCtr));
                            
                            break;
                        case java.sql.Types.DATE:
                            if (foSource.getObject(lnCtr) != null)
                                loJSON.put(lnCtr, SQLUtil.dateFormat(foSource.getObject(lnCtr), SQLUtil.FORMAT_SHORT_DATE));
                            else
                                loJSON.put(lnCtr, foSource.getObject(lnCtr));
                            
                            break;
                        default:
                            loJSON.put(lnCtr, foSource.getObject(lnCtr));
                    }
                }
                loArray.add(loJSON);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return loArray;
    }
    
    public static JSONArray RS2JSON(ResultSet foSource){        
        JSONArray loArray = new JSONArray();
        JSONObject loJSON;
        
        try {
            while (foSource.next()){
                loJSON = new JSONObject();
                
                for (int lnCtr = 1; lnCtr <= foSource.getMetaData().getColumnCount(); lnCtr++){
                    loJSON.put(foSource.getMetaData().getColumnLabel(lnCtr), foSource.getString(lnCtr));
                }
                loArray.add(loJSON);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return loArray;
    }

    public static void SetNextFocus(TextField foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    public static void SetNextFocus(TextArea foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    public static void SetNextFocus(ComboBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }    
    
    public static void SetNextFocus(TabPane foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
  
    public static void SetNextFocus(CheckBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    
    public static void SetPreviousFocus(TextField foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();
        }
    }
    
    public static void SetPreviousFocus(TabPane foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();
        }
    }
    
    public static void SetPreviousFocus(TextArea foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    public static void SetPreviousFocus(ComboBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    public static void SetPreviousFocus(CheckBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    
    public static String TitleCase(String fsValue){
        if (fsValue == null || fsValue.isEmpty()){ return "";}
        
        fsValue = fsValue.toLowerCase().trim();
        String [] arrValue;
        String fsReturn = "";
        
        arrValue = fsValue.split(" ");
        for (int lnCtr = 0; lnCtr <= arrValue.length -1; lnCtr++){
            fsReturn = fsReturn + 
                        " " + 
                        arrValue[lnCtr].substring(0, 1).toUpperCase() + 
                        arrValue[lnCtr].substring(1, arrValue[lnCtr].length());
        }
        return fsReturn.trim();
    }
    
    public static Date toDate(String date){
       Date loDate = null;
       try{
            //Be sure to follow the format specified
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            loDate = sf.parse(date);
       }
          catch(ParseException e){
          //Nothing to do;
       }

       return loDate;
    }
       
    public static boolean isDate(String fsValue, String fsFormat){	
        if(fsValue == null){return false;}

        SimpleDateFormat sdf = new SimpleDateFormat(fsFormat);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(fsValue);
            System.out.println(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
   
    public static String xsDateMedium(Date fdValue){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
	String date = sdf.format(fdValue); 
	return date;
    }
    
    public static String xsDateLong(Date fdValue){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
	String date = sdf.format(fdValue); 
	return date;
    }
    
    public static String classifyNetwork(String fsMobileNo){
        if (fsMobileNo == null) {
            System.err.println("NULL Mobile Number...");
            return "";
        }
        
        if (fsMobileNo.equals("")) {
            System.err.println("UNSET Mobile Number...");
            return "";
        }
        
        char lcStart = fsMobileNo.charAt(0);
        char lcPlus = '+';
        if(lcStart == lcPlus){
            if(fsMobileNo.substring(0, 3).equalsIgnoreCase("+63")){
                fsMobileNo = "0" + fsMobileNo.substring(3);
            } else
                fsMobileNo = fsMobileNo.replace("+", fsMobileNo);
        } else {
            if (fsMobileNo.length() < 11) {
                System.err.println("Invalid mobile number. " + fsMobileNo);
                return "";
            }
            
            if(fsMobileNo.substring(0, 3).equals("639")){
                fsMobileNo = "0" + fsMobileNo.substring(3);
            }
        }
        
        if (fsMobileNo.length() != 11){
            System.err.println("Invalid mobile number. " + fsMobileNo);
            return "";
        } else {
            switch(fsMobileNo.substring(0, 5)){ //check on the first 5 digits
                case "09173":
                case "09178":
                case "09175":
                case "09176":
                    return "0";
                default:
                    switch(fsMobileNo.substring(0, 4)){
                        case "0817":
                        case "0905":
                        case "0906":
                        case "0915":
                        case "0916":
                        case "0917":
                        case "0926":
                        case "0927":
                        case "0935":
                        case "0936":
                        case "0937":
                        case "0945":
                        case "0955":
                        case "0956":
                        case "0965":
                        case "0966":
                        case "0967":
                        case "0973":
                        case "0975":
                        case "0976":
                        case "0977":
                        case "0978":
                        case "0979":
                        case "0995":
                        case "0996":
                        case "0997":
                        case "0953": //mac 2020.08.14
                        case "0964": //mac 2020.08.14
                        case "0954": //mac 2020.08.14
                        case "0980": //mac 2020.08.14
                        case "0904":
                        case "0957":
                        case "0958":
                        case "0959":
                            return "0"; //globe
                        case "0813":
                        case "0907":
                        case "0908":
                        case "0909":
                        case "0910":
                        case "0911":
                        case "0912":
                        case "0913":
                        case "0914":
                        case "0918":
                        case "0919":
                        case "0920":
                        case "0921":
                        case "0928":
                        case "0929":
                        case "0930":
                        case "0938":
                        case "0939":
                        case "0940":
                        case "0946":
                        case "0947":
                        case "0948":
                        case "0949":
                        case "0950":
                        case "0951":
                        case "0970":
                        case "0981":
                        case "0989":
                        case "0998":
                        case "0999":
                        case "0961": //2019.09.28
                        case "0971": //2019.09.28
                        case "0982": //mac  2020.08.14
                        case "0900": //mac  2020.08.14
                        case "0968": //mac  2020.08.14
                        case "0960": //mac  2020.08.14
                        case "0974": //mac  2020.08.14
                        case "0963": //mac 2020.08.14
                        case "0962":
                        case "0985":
                        case "0972":
                            return "1"; //smart
                        case "0922":
                        case "0923":
                        case "0924":
                        case "0925":
                        case "0931":
                        case "0932":
                        case "0933":
                        case "0934":
                        case "0941":
                        case "0942":
                        case "0943":
                        case "0944":
                        case "0952": //mac 2020.08.14
                        case "0969": //mac 2022.07.28
                            return "2"; //sun
                        case "0991": //mac 2021.07.16
                        case "0895":
                        case "0896":
                        case "0897":
                        case "0898":
                        case "0992":
                        case "0993":
                        case "0994":
                            return "3"; //dito
                        default:
                            System.err.println("Prefix not registered... ->> " + fsMobileNo);
                            return "";
                    }
            }
        }
    }
    
    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public static String fixMobileNo(String fsMobileNo){
        if (fsMobileNo.length() == 11){
            if (fsMobileNo.substring(0, 1).equals("0"))
                return "+63" + fsMobileNo.substring(1, 11);
            else
                return "";
        } if (fsMobileNo.length() == 10){
            if (fsMobileNo.substring(0, 1).equals("9"))
                return "+63" + fsMobileNo.substring(0, 10);
            else
                return "";
        } if (fsMobileNo.length() < 10){
            return "";
        } if (fsMobileNo.length() == 12){
            if (fsMobileNo.substring(0, 2).equals("63"))
                return "+" + fsMobileNo.substring(0, 12);
            else
                return "";
        } if (fsMobileNo.length() == 13){
            if (fsMobileNo.substring(0, 3).equals("+63"))
                return fsMobileNo;
            else
                return "";
        } else
            return "";
    }
    
    public static boolean isValidEmail(String fsEmail)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";
                             
        Pattern pat = Pattern.compile(emailRegex);
        if (fsEmail == null)
            return false;
        return pat.matcher(fsEmail).matches();
    }
    
    public static String NumberFormat(double fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static String NumberFormat(BigDecimal fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static String NumberFormat(Number fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static void closeStage(Button foButton){
        Stage stage = (Stage) foButton.getScene().getWindow();
        stage.close();
    }
    
    public static void minimizeStage(Button foButton){
        Stage stage = (Stage) foButton.getScene().getWindow();
        stage.setIconified(true);
    }
    
    public static String UTF2Hex(String fsValue){
        try {
            return new String(Hex.encodeHex(fsValue.getBytes("utf-8")));
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String Win2UTF(String strHexWin) throws DecoderException, UnsupportedEncodingException{
        //convert back to array of byte
        byte[] win = Hex.decodeHex(strHexWin); 

        //get character set/encoding
        String enc = getEncoding(win);

        //convert byte to string using windows character encoding
        //String winstr = new String(win, "windows-1252");
        String winstr = new String(win, enc);

        //convert the string into array of byte using unicode as character set
        byte[] utf = winstr.getBytes("UTF-8");

        //convert byte to string using unicode character encoding...
        return new String(utf, "UTF-8");
    }
    
    public static String getEncoding(byte[] win){
        CharsetDetector detector = new CharsetDetector();
        detector.setText(win);
        
        CharsetMatch match = detector.detect();
        return match.getName();
    }
    
    public static String dateFormat(Object date, String format){
        SimpleDateFormat sf = new SimpleDateFormat(format);
        String ret;
        if ( date instanceof Timestamp)
            ret = sf.format((Date)date);
        else if ( date instanceof Date )
            ret = sf.format(date);
        else if ( date instanceof Calendar){
            Calendar loDate = (Calendar) date;
            ret = sf.format(loDate.getTime());
            loDate = null;
        }
        else
            ret = null;

        sf = null;
        return ret;
    }
    
    public static int getDateMonth(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }
    
    public static int getDateDay(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }
    
    public static int getDateYear(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getYear();
    }
    
    public static Date dateAdd(Date date, int toAdd){
        return dateAdd(date, Calendar.DATE, toAdd);
    }
   
    public static Date dateAdd(Date date, int field, int toAdd){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(field, toAdd);
        return c1.getTime();
    }
    
    public static long dateDiff(Date date1, Date date2){
        return (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);
    }
    
    public static long monthDiff(String date1, String date2){
        Period diff = Period.between(
                LocalDate.parse(date1).withDayOfMonth(1),
                LocalDate.parse(date2).withDayOfMonth(1));
        
        return diff.toTotalMonths();
    }
    
    public static boolean isURLOnline(String targetUrl) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl)
                    .openConnection();
 
            // A HEAD request is just like a GET request, except that it asks
            // the server to return the response headers only, and not the
            // actual resource (i.e. no message body).
            // This is useful to check characteristics of a resource without
            // actually downloading it,thus saving bandwidth. Use HEAD when
            // you don't actually need a file's contents.
            httpUrlConn.setRequestMethod("HEAD");
 
            // Set timeouts in milliseconds
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);
 
            // Print HTTP status code/message for your information.
            System.out.println("Response Code: "
                    + httpUrlConn.getResponseCode());
            System.out.println("Response Message: "
                    + httpUrlConn.getResponseMessage());
 
            return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    public static void createLog(String fsFileName, String fsData){
        fsData = dateFormat(new java.util.Date(), "yyyy-MM-dd hh:mm:ss") + "  " + fsData;
        FileUtil.fileWrite(fsFileName, fsData, true);
    }
    
    public static String md5Hex(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static void killProcess(String serviceName) {
        try {
            Runtime.getRuntime().exec("taskkill /IM " + serviceName);
        } catch (IOException e) {
            System.err.println("ERROR MESSAGE: " + e.getMessage());
        }

    }
    
    public static boolean isProcessRunning(String serviceName) {
        try {
            Process pro = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(serviceName)) {
                        return true;
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR MESSAGE: " + e.getMessage());
        }

        return false;
    }
    
    public static Object createInstance(String classname){
        Class<?> x;
        Object obj = null;
        try {
            x = Class.forName(classname);
            obj = x.newInstance();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
    
    public static String getPOSComptrID(Connection foCon){
        try {
            String lsSQL = "SELECT" +
                    "  sAccredtn" +
                    ", sPermitNo" +
                    ", sSerialNo" +
                    ", nPOSNumbr" +
                    ", nZReadCtr" +
                    " FROM Cash_Reg_Machine" +
                    " WHERE sIDNumber = " + SQLUtil.toSQL(getEnv("RMS-CRM-No"));
            
            Statement loStmt = foCon.createStatement();
            ResultSet loRS = loStmt.executeQuery(lsSQL);
            
            if (loRS.next()) return loRS.getString("nPOSNumbr");
        } catch (SQLException ex) {
            Logger.getLogger(CommonUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    
    public static int createEventLog(GRider foApp, String fsBranchCD, String fsEventIDx, String fsRemarksx, String fsCRMNmber){
        if (foApp == null) return -1;
        
        if (fsEventIDx.isEmpty() || fsCRMNmber.isEmpty()) return -1;
        
        String lsSQL = "INSERT INTO Event_Master SET" + 
                            "  sTransNox = " + SQLUtil.toSQL(MiscUtil.getNextCode("Event_Master", "sTransNox", true, foApp.getConnection(), fsBranchCD)) +
                            ", sEventIDx = " + SQLUtil.toSQL(fsEventIDx) + 
                            ", sRemarksx = " + SQLUtil.toSQL(fsRemarksx) + 
                            ", sUserIDxx = " + SQLUtil.toSQL(foApp.getUserID()) + 
                            ", sCRMNumbr = " + SQLUtil.toSQL(fsCRMNmber) +
                            ", dModified = " + SQLUtil.toSQL(foApp.getServerDate());
        
        return foApp.executeQuery(lsSQL, "Event_Master", foApp.getBranchCode(), "");
    }
    
    public static String getParameter(String fsValue){
        if (fsValue.contains(";")){
            String [] laType = fsValue.split(";");

            fsValue = "";
            for (int lnCtr = 0; lnCtr <= laType.length-1; lnCtr ++){
                fsValue = fsValue + ", " + SQLUtil.toSQL(laType[lnCtr]);
            }
            fsValue = "(" + fsValue.substring(2) + ")";
        } else {
            fsValue = "(" + SQLUtil.toSQL(fsValue) + ")";
        }
        
        return fsValue;
    }
    
    public static String createSalesTransaction(GRider foApp, String fsInvoicex, String fsSourceCd, String fsSourceNo, boolean fbRePrint, boolean fbReverse){
        if (foApp == null) return "";
        
        if (fsSourceCd.isEmpty() || fsSourceCd.isEmpty()) return "";
        
        String lsTerminal = System.getProperty("pos.clt.trmnl.no");
        
        if (lsTerminal == null) lsTerminal = "";
        
        String lsTransNox = MiscUtil.getNextCode("Sales_Transaction", "sTransNox", true, foApp.getConnection(), foApp.getBranchCode() + lsTerminal);
        String lcReversex = fbReverse ? "-" : "+";
        String lcRePrintx = fbRePrint ? "1" : "0";
        
        String lsSQL = "INSERT INTO Sales_Transaction SET" +
                            "  sTransNox = " + SQLUtil.toSQL(lsTransNox) +
                            ", sInvoicex = " + SQLUtil.toSQL(fsInvoicex) +
                            ", sSourceCd = " + SQLUtil.toSQL(fsSourceCd) +
                            ", sSourceNo = " + SQLUtil.toSQL(fsSourceNo) +
                            ", cReversex = " + SQLUtil.toSQL(lcReversex) +
                            ", cRePrintx = " + SQLUtil.toSQL(lcRePrintx) +
                            ", sModified = " + SQLUtil.toSQL(foApp.getUserID());
        
        if (foApp.executeQuery(lsSQL, "Sales_Transaction", foApp.getBranchCode(), "") <= 0){
            if (!foApp.getErrMsg().isEmpty()){
                return "";
            }
        }
        
        return lsTransNox;
    }
    
    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
    
    public static void addTextLimiter(final TextArea tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
}
