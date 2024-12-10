package org.guanzon.appdriver.base;

import com.sun.rowset.CachedRowSetImpl;
import org.guanzon.appdriver.iface.GEntity;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.sql.rowset.CachedRowSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Contains miscellaneous methods for JDBC objects.
 * 
 * @author kalyptus
 */
public class MiscUtil {
    /**
     * Gets the name of the computer.
     * 
     * @return the computer name.
     */
   public static String getPCName(){
      try{
         return InetAddress.getLocalHost().getHostName();
      }catch (UnknownHostException ex){
         ex.printStackTrace();
         return "restgk";
      }
   }

   /**
    * Creates a Connection object for a MySQL server.
    * 
    * @param fsURL      the IP/Hostname of the MySQL server.
    * @param fsDatabase the name of the database to be used in the connection
    * @param fsUserID   the MySQL user's name
    * @param fsPassword the MySQL user's password 
    * @return           instance of the connection class.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword) throws SQLException, ClassNotFoundException{
      return getConnection(fsURL, fsDatabase, fsUserID, fsPassword, "3306");
   }

   /**
    * Connect to the MySQL Data using custom set port
    * 
    * @param fsURL      the IP/Hostname of the MySQL server.
    * @param fsDatabase the name of the database to be used in the connection
    * @param fsUserID   the MySQL user's name
    * @param fsPassword the MySQL user's password 
    * @param fsPort     the MySQL server's listening port.
    * @return           instance of the connection class.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword, String fsPort) throws SQLException, ClassNotFoundException{
      Connection oCon = null;
      Class.forName("com.mysql.jdbc.Driver");
      oCon = DriverManager.getConnection("jdbc:mysql://" + fsURL + ":" + fsPort + "/" + fsDatabase + "?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=ISO-8859-1", fsUserID, fsPassword);
      return oCon;
   }
   
   /**
    * Connect to the SQLite database.
    * 
    * @param fsURL      the location of the SQLite file
    * @param fsDatabase the name of the SQLite file
    * @return           the BasicDataSource instance.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
   public static Connection getConnection(String fsURL, String fsDatabase) throws SQLException, ClassNotFoundException{
        //Connection oCon = null;
        //Class.forName("org.sqlite.JDBC");
        //oCon = DriverManager.getConnection("jdbc:sqlite:" + fsURL + fsDatabase);
        //return oCon;
        Connection conn;
        try {
            String url = "jdbc:sqlite:" + fsURL + fsDatabase;
            conn = DriverManager.getConnection(url);
            
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
   }    
    
    /**
     * Closes the ResultSet object.
     * 
     * @param foRs   The ResultSet to close.
     */
   public static void close(java.sql.ResultSet foRs) {
      if ( foRs == null) {
         return;
      }
      try {
         foRs.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
   }

    /**
     * Closes the Statement object.
     * 
     * @param foStmt The statement object to close.
     */
   public static void close(java.sql.Statement foStmt) {
      if (foStmt == null) {
         return;
      }
      try {
         foStmt.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
          //Ignore the error
      }
      finally{
         foStmt = null;
      }
   }

    /**
     * Closes the PreparedStatement object.
     * 
     * @param foPstmt The PreparedStatement to close. 
     */
   public static void close(java.sql.PreparedStatement foPstmt) {
      if (foPstmt == null) {
         return;
      }
      try {
         foPstmt.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
      finally{
         foPstmt = null;
      }
   }

   /**
    * Closes the Connection object.
    * 
    * @param foConn The connection object to close.
    */
   public static void close(java.sql.Connection foConn) {
      System.out.println("close:" + foConn.toString());
      if (foConn == null) {
         return;
      }
      try {
         foConn.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
         //Ignore the error
      }
      finally{
         foConn = null;
      }
   }
   /**
    * Count the number of rows within the ResultSet.
    * 
    * @param rs   The ResultSet to count.
    * @return     The number of rows within the ResultSet.
    */ 
   public static long RecordCount(java.sql.ResultSet rs) {
      long pos;
      long ctr;
      boolean frst = false;
      boolean last = false;
      try {
         frst = rs.isBeforeFirst();
         last = rs.isAfterLast();
         pos = rs.getRow();

         rs.beforeFirst();

         ctr = 0;
         while(rs.next())
            ctr++;

         if(pos > 0) {
            rs.absolute((int) pos);
         }
         else if(frst){
            rs.beforeFirst();
         }
         else{
            rs.afterLast();
         }
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         ctr=0;
      }
    	
      return ctr;
    }

   /**
    * Creates a SQL INSERT statement from a POJO instance.
    * 
    * @param foNewEntity   The POJO to convert into SQL INSERT statement.
    * @return              The SQL INSERT equivalent of GEntity.
    */ 
    public static String makeSQL(GEntity foNewEntity){
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        for(int lnCol = 1; lnCol <= foNewEntity.getColumnCount();lnCol++){
           lsSQL.append(", " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
           lsNme.append(", " + foNewEntity.getColumn(lnCol));
        }

        return "INSERT INTO " + foNewEntity.getTable() + "(" + lsNme.toString().substring(1) + ") VALUES (" +  
            lsSQL.toString().substring(1) + ")";
    }
    
    public static String makeSQL(GEntity foNewEntity, String fsExclude){
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        for(int lnCol = 1; lnCol <= foNewEntity.getColumnCount();lnCol++){
            if(!fsExclude.contains(foNewEntity.getColumn(lnCol))){
               lsSQL.append(", " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
               lsNme.append(", " + foNewEntity.getColumn(lnCol));
            }
        }
            
        return "INSERT INTO " + foNewEntity.getTable() + "(" + lsNme.toString().substring(1) + ") VALUES (" +  
            lsSQL.toString().substring(1) + ")";
    }
    
    public static String makeSQL(GEntity foNewEntity, GEntity foPrevEntity, String fsCondition){
        StringBuilder lsSQL = new StringBuilder();
        int lnCol1 = 0;
        int lnCol2 = 0;

        for(int lnCol = 1; lnCol <= foNewEntity.getColumnCount();lnCol++){
            if(lnCol1 == 0 || lnCol2 == 0){
                if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("smodified"))
                    lnCol1 = lnCol;
            else if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("dmodified"))
               lnCol2 = lnCol;
            else{
               if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                  lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
            }
         }
         else{
            if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
               lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
         }
      }

      //If no update was detected return an empty string
      if(lsSQL.toString().equals(""))
         return "";

      //Add the value of smodified if the field is available
      if(lnCol1 > 0)
         lsSQL.append( ", sModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol1)));
      //Add the value of dmodified if the field is available
      if(lnCol2 > 0)
         lsSQL.append( ", dModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol2)));

      return "UPDATE " + foNewEntity.getTable() + " SET" +
                  lsSQL.toString().substring(1) +
            " WHERE " + fsCondition;
   }   
    
    public static String makeSQL(GEntity foNewEntity, GEntity foPrevEntity, String fsCondition, String fsExclude){
        StringBuilder lsSQL = new StringBuilder();
        int lnCol1 = 0;
        int lnCol2 = 0;

        for(int lnCol = 1; lnCol <= foNewEntity.getColumnCount();lnCol++){
            if(!fsExclude.contains(foNewEntity.getColumn(lnCol))){
                if(lnCol1 == 0 || lnCol2 == 0){
                    if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("smodified"))
                        lnCol1 = lnCol;
                else if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("dmodified"))
                   lnCol2 = lnCol;
                else{
                   if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                      lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
                }
            }
        } else{
            if(!fsExclude.contains(foNewEntity.getColumn(lnCol))){
                if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                    lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
                }
            }
        }

        //If no update was detected return an empty string
        if(lsSQL.toString().equals(""))
            return "";

        //Add the value of smodified if the field is available
        if(lnCol1 > 0)
            lsSQL.append( ", sModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol1)));
        //Add the value of dmodified if the field is available
        if(lnCol2 > 0)
            lsSQL.append( ", dModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol2)));

        return "UPDATE " + foNewEntity.getTable() + " SET" +
                    lsSQL.toString().substring(1) +
                " WHERE " + fsCondition;
    }   

   public static String addCondition(String SQL, String condition){
      int lnIndex;
      StringBuffer lsSQL = new StringBuffer(SQL);
      if(lsSQL.indexOf("WHERE") > 0){
         //inside
         if(lsSQL.indexOf("GROUP BY") > 0){
            lnIndex = lsSQL.indexOf("GROUP BY");
            lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }   
         else if(lsSQL.indexOf("HAVING") > 0){
           lnIndex = lsSQL.indexOf("HAVING");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else if(lsSQL.indexOf("ORDER BY") > 0){
           lnIndex = lsSQL.indexOf("ORDER BY");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else if(lsSQL.indexOf("LIMIT") > 0){
           lnIndex = lsSQL.indexOf("LIMIT");
           lsSQL.insert(lnIndex, "AND (" + condition + ") ");
         }
         else
            lsSQL.append(" AND (" + condition + ")");
      //inside
      }
      else if(lsSQL.indexOf("GROUP BY") > 0){
         lnIndex = lsSQL.indexOf("GROUP BY");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("HAVING") > 0){
         lnIndex = lsSQL.indexOf("HAVING");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("ORDER BY") > 0){
         lnIndex = lsSQL.indexOf("ORDER BY");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else if(lsSQL.indexOf("LIMIT") > 0){
         lnIndex = lsSQL.indexOf("LIMIT");
         lsSQL.insert(lnIndex, "WHERE " + condition + " ");
      }
      else{
         lsSQL.append(" WHERE " + condition);
      }   
      return lsSQL.toString();
    }

    public static String getNextCode(
        String fsTableNme,
        String fsFieldNme,
        boolean fbYearFormat,
        java.sql.Connection foCon,
        String fsBranchCd){
        String lsNextCde="";
        int lnNext;
        String lsPref = fsBranchCd;

        String lsSQL = null;
        Statement loStmt = null;
        ResultSet loRS = null;

        if(fbYearFormat){
            try {
                if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }          
            
                loStmt = foCon.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                lsPref = lsPref + loRS.getString(1).substring(2);
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
            finally{
                close(loRS);
                close(loStmt);
            }
        }
      
        lsSQL = "SELECT " + fsFieldNme
                + " FROM " + fsTableNme
                + " ORDER BY " + fsFieldNme + " DESC "
                + " LIMIT 1";

        if(!lsPref.isEmpty())
            lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
      
        try {
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            if(loRS.next()){
               lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            }
            else
               lnNext = 0;

            lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            lsNextCde = "";
        }
        finally{
            close(loRS);
            close(loStmt);
        }

        return lsNextCde;
    }

    public static String getNextCode(
      String fsTableNme,
      String fsFieldNme,
      boolean fbYearFormat,
      java.sql.Connection foCon,
      String fsBranchCd,
      String fsFilter){
      String lsNextCde="";
      int lnNext;
      String lsPref = fsBranchCd;

      String lsSQL = null;
      Statement loStmt = null;
      ResultSet loRS = null;

      if(fbYearFormat){
         try {
            if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
               lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
            }else{
               //assume that default database is MySQL ODBC
               lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
            }          
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            loRS.next();
            lsPref = lsPref + loRS.getString(1).substring(2);
         } 
         catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            return "";
         }
         finally{
            close(loRS);
            close(loStmt);
         }
      }

      lsSQL = "SELECT " + fsFieldNme
           + " FROM " + fsTableNme
           + " ORDER BY " + fsFieldNme + " DESC "
           + " LIMIT 1";

      if(!lsPref.isEmpty())
         lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
         
      lsSQL = addCondition(lsSQL, fsFilter);
      
      try {
         loStmt = foCon.createStatement();
         loRS = loStmt.executeQuery(lsSQL);
         if(loRS.next()){
            lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
         }
         else
            lnNext = 0;


         lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

      } 
      catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
         lsNextCde = "";
      }
      finally{
         close(loRS);
         close(loStmt);
      }

      return lsNextCde;
   }

   public static String makeSelect(GEntity foObject) {
      StringBuilder lsSQL = new StringBuilder();
      lsSQL.append("SELECT ");
      lsSQL.append(foObject.getColumn(1));

      for(int lnCol=2; lnCol<=foObject.getColumnCount(); lnCol++){
          lsSQL.append(", " + foObject.getColumn(lnCol));
      }

      lsSQL.append( " FROM " + foObject.getTable());
      return lsSQL.toString();
   }
   
    public static String makeSelect(GEntity foObject, String fsExclude) {
        StringBuilder lsSQL = new StringBuilder();
        lsSQL.append("SELECT ");
        lsSQL.append(foObject.getColumn(1));

        for(int lnCol=2; lnCol<=foObject.getColumnCount(); lnCol++){
            if(!fsExclude.contains(foObject.getColumn(lnCol))){
                lsSQL.append(", " + foObject.getColumn(lnCol));
            }
        }

        lsSQL.append( " FROM " + foObject.getTable());
        return lsSQL.toString();
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

   public static String[] splitName(String fsName){
      String laNames[] = {"", "", ""};
      fsName = fsName.trim();

      if(fsName.length() > 0){
         String laNames1[] = fsName.split(",");
         laNames[0] = laNames1[0].trim();
         laNames[1] = laNames1[1].trim();
         if(laNames1.length > 1){
            String lsFrstName = laNames1[1].trim();
            if(lsFrstName.length() > 0){
               laNames1 = lsFrstName.split("»");
               laNames[1] = laNames1[0];
               if(laNames1.length > 1)
                  laNames[2] = laNames1[1];
            }
         }

         if(laNames[0].trim().length() == 0)
           laNames[0] = "%";
         if(laNames[1].trim().length() == 0)
           laNames[1] = "%";
         if(laNames[2].trim().length() == 0)
           laNames[2] = "%";
      }
      return laNames;
   }
   
   public static Map row2Map(ResultSet rs){
      Map map = new HashMap();
            
      try {
         if(rs.isAfterLast() || rs.isBeforeFirst()) return null;
            
         ResultSetMetaData rsmd = rs.getMetaData();
         int count = rsmd.getColumnCount();
            
         for (int i = 1; i <= count; i++) {
            String key = rsmd.getColumnName(i);
                
            switch(rsmd.getColumnType(i)){
               case java.sql.Types.ARRAY:
                  map.put(key, rs.getArray(i));
                  break;
               case java.sql.Types.BIGINT:
                  map.put(key, rs.getLong(i));
                  break;
               case java.sql.Types.REAL:
                  map.put(key, rs.getFloat(i));
                  break;
               case java.sql.Types.BOOLEAN:
               case java.sql.Types.BIT:    
                  map.put(key, rs.getBoolean(i));
                  break;
               case java.sql.Types.BLOB:
                  map.put(key, rs.getBlob(i));
                  break;
               case java.sql.Types.DOUBLE:
               case java.sql.Types.FLOAT:
                  map.put(key, rs.getDouble(i));
                  break;
               case java.sql.Types.INTEGER:
                  map.put(key, rs.getInt(i));
                  break;
               case java.sql.Types.NVARCHAR:
                  map.put(key, rs.getNString(i));
                  break;
               case java.sql.Types.VARCHAR:
               case java.sql.Types.CHAR:
               case java.sql.Types.LONGVARCHAR:
                  map.put(key, rs.getString(i));
                  break;
               case java.sql.Types.NCHAR:
               case java.sql.Types.LONGNVARCHAR:
                  map.put(key, rs.getNString(i));
                  break;
               case java.sql.Types.TINYINT:
                  map.put(key, rs.getByte(i));
                  break;
               case java.sql.Types.SMALLINT:
                  map.put(key, rs.getShort(i));
                  break;
               case java.sql.Types.DATE:
                  map.put(key, rs.getDate(i));
                  break;
               case java.sql.Types.TIME:
                  map.put(key, rs.getTime(i));
                  break;
               case java.sql.Types.TIMESTAMP:
                  map.put(key, rs.getTimestamp(i));
                  break;
               case java.sql.Types.BINARY:
               case java.sql.Types.VARBINARY:
                  map.put(key, rs.getBytes(i));
                  break;
               case java.sql.Types.LONGVARBINARY:
                  map.put(key, rs.getBinaryStream(i));
                  break;
               case java.sql.Types.CLOB:
                  map.put(key, rs.getClob(i));
                  break;
               case java.sql.Types.NUMERIC:
               case java.sql.Types.DECIMAL:
                  map.put(key, rs.getBigDecimal(i));
                  break;
               case java.sql.Types.DATALINK:
                  map.put(key, rs.getURL(i));
                  break;
               case java.sql.Types.REF:
                  map.put(key, rs.getRef(i));
                  break;
               case java.sql.Types.STRUCT:
               case java.sql.Types.DISTINCT:
               case java.sql.Types.JAVA_OBJECT:
                  map.put(key, rs.getObject(i));
                  break;
               default:
                  map.put(key, rs.getString(i));
               }
         }
            
      } catch (SQLException ex) {
         map = null;
      }
        
      return map;
   } 

    
   public static List rows2Map(ResultSet rs){
      List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

      try {
         while(rs.next()){
            Map map = row2Map(rs);
            if(map != null)
               list.add(map);
         }
      } catch (SQLException ex) {
         list = null;
      }

      return list;
   }
   
   public static String makeSQL(ResultSetMetaData rsmd, Map foMap, String fsTable){
      StringBuilder lsSQL = new StringBuilder();
      StringBuilder lsNme = new StringBuilder();
      String column;
      try {
         int count = rsmd.getColumnCount();
         for(int i=1; i <= count; i++){
            column = rsmd.getColumnName(i);
               lsNme.append(", " + column);
               lsSQL.append(", " + SQLUtil.toSQL(foMap.get(column)));
         }
      } catch (SQLException ex) {
          lsSQL = new StringBuilder();
      }

      if(lsSQL.toString().isEmpty())
         return "";
      else
         return "INSERT INTO " 
               + fsTable + " (" + lsNme.toString().substring(1) + ")" 
               + " VALUES (" + lsSQL.toString().substring(1) + ")";
   }
    
   //kalyptus - 2017.09.06 03:05pm
   //Converted into General SQL Insert
   public static String makeSQL(ResultSetMetaData rsmd, Map foMap, String fsTable, String fsExclude){
      StringBuilder lsSQL = new StringBuilder();
      StringBuilder lsNme = new StringBuilder();
      String column;
      try {
         int count = rsmd.getColumnCount();
         for(int i=1; i <= count; i++){
            column = rsmd.getColumnName(i);
            if(!fsExclude.contains(column)){
               lsNme.append(", " + column);
               lsSQL.append(", " + SQLUtil.toSQL(foMap.get(column)));
            }
         }
      } catch (SQLException ex) {
          lsSQL = new StringBuilder();
      }

      if(lsSQL.toString().isEmpty())
         return "";
      else
         return "INSERT INTO " 
               + fsTable + " (" + lsNme.toString().substring(1) + ")" 
               + " VALUES (" + lsSQL.toString().substring(1) + ")";

//            return "INSERT INTO " + fsTable + " SET" + lsSQL.toString().substring(1);
   }

   public static String makeSQL(ResultSetMetaData rsmd, Map foNewMap, Map foOldMap, String fsTable, String fsWhere, String fsExclude){
      StringBuilder lsSQL = new StringBuilder();
      String column;
      try {
         int count = rsmd.getColumnCount();
         for(int i=1; i <= count; i++){
            column = rsmd.getColumnName(i);
            if(!fsExclude.contains(column)){
               if(!SQLUtil.equalValue(foNewMap.get(column), foOldMap.get(column)))
                  lsSQL.append(", " + column + " = " + SQLUtil.toSQL(foNewMap.get(column)));
            }
         }
      } catch (SQLException ex) {
         lsSQL = new StringBuilder();
      }

      if(lsSQL.toString().isEmpty())
          return "";
      else
          return "UPDATE " + fsTable + " SET" + lsSQL.toString().substring(1) + " WHERE " + fsWhere;
  }
   
   //kalyptus - 2018.04.06 11:06am
   //create an instance of an object using the CLASSNAME...
   //Caveat - object's constructor should have an empty parameter...
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
    
   //kalyptus - 2019.06.01 04:45pm
   //create an random number 
    public static int getRandom(int num){
        Random rand = new Random();
        return rand.nextInt(num) + 1;
    }
    
    public static int getRandom(int fnLow, int fnHigh){
        Random r = new Random();
        return r.nextInt(fnHigh - fnLow) + fnLow;
    }
    
    public static String StringToHex(String str) {
        char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(chars);
    }
    
    public static String HexToString(String hex) {
        String result = "";
        try {
            byte[] bytes = Hex.decodeHex(hex);
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Invalid Hex format!");
        }
        return result;
    }
    
    //mac
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
        }
        
        return loArray;
    }
    
    //mac
    public static long monthDiff(String date1, String date2){
        Period diff = Period.between(
                LocalDate.parse(date1).withDayOfMonth(1),
                LocalDate.parse(date2).withDayOfMonth(1));
        
        return diff.toTotalMonths();
    }
    
    //mac
    public static long dateDiff(Date date1, Date date2){
        return (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);
    }
    
    //mac
    public static int getDateMonth(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }
    
    //mac
    public static int getDateDay(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }
    
    //mac
    public static int getDateYear(Date foValue){
        LocalDate localDate = foValue.toInstant().atZone(ZoneId.systemDefault().systemDefault()).toLocalDate();
        return localDate.getYear();
    }
    
    //mac
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
            ex.printStackTrace();
        }
        return null;
    }
    
    //mac
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
            ex.printStackTrace();
        }
        return null;
    }
    
    //mac
    public static String getSystemConfiguration(GRider foGRider, String fsConfigID){
        String lsSQL = "SELECT sConfigVl sValuexxx" +
                            " FROM xxxSysConfig" + 
                            " WHERE sConfigCd = " + SQLUtil.toSQL(fsConfigID);
        
        ResultSet loRS = foGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next()){
                return loRS.getString("sValuexxx");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    //mac
    public static JSONArray RS2JSONArray(CachedRowSet foSource) throws SQLException{        
        JSONArray loArray = new JSONArray();
        JSONObject loJSON;
        
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
        
        return loArray;
    }
    
    //mac
    public static JSONObject RS2JSONObject(CachedRowSet foSource) throws SQLException{        
        JSONObject loJSON;
        
        if (foSource.isBeforeFirst() || foSource.isAfterLast()) return null;

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

        return loJSON;
    }
    
    //mac
    public static JSONObject RSFindRow(CachedRowSet foSource, String fsField, Object foValue) throws SQLException{
        foSource.beforeFirst();
        
        while (foSource.next()){
            if (String.valueOf(foSource.getObject(fsField)).equals(String.valueOf(foValue))) 
                return RS2JSONObject(foSource);
        }
        
        return null;
    }
    
    //mac
    public static int getColumnIndex(CachedRowSet loRS, String fsValue) throws SQLException{
        int lnIndex = 0;
        int lnRow = loRS.getMetaData().getColumnCount();
        
        for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
            if (fsValue.equals(loRS.getMetaData().getColumnLabel(lnCtr))){
                lnIndex = lnCtr;
                break;
            }
        }
        
        return lnIndex;
    }
    
    //mac
    public static String getColumnLabel(CachedRowSet loRS, int fnIndex) throws SQLException{
        return loRS.getMetaData().getColumnLabel(fnIndex);
    }
    
    //mac
    public static void getSchema(CachedRowSet loRS) throws SQLException{        
        int lnRow = loRS.getMetaData().getColumnCount();
        
        System.out.println("----------------------------------------");
        System.out.println("MAIN TABLE: " + loRS.getTableName());
        System.out.println("----------------------------------------");
        System.out.println("Total number of columns: " + lnRow);
        System.out.println("----------------------------------------");
        
        for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
            System.out.println("Column index: " + (lnCtr) + " --> Name : " + loRS.getMetaData().getColumnName(lnCtr));
            System.out.println("Column index: " + (lnCtr) + " --> Label: " + loRS.getMetaData().getColumnLabel(lnCtr));
            System.out.println("Column index: " + (lnCtr) + " --> Type : " + loRS.getMetaData().getColumnType(lnCtr));
            
            if (loRS.getMetaData().getColumnType(lnCtr) == Types.CHAR ||
                loRS.getMetaData().getColumnType(lnCtr) == Types.VARCHAR){
                
                System.out.println("Column index: " + (lnCtr) + " --> Size: " + loRS.getMetaData().getColumnDisplaySize(lnCtr));
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("END: MAIN TABLE");
        System.out.println("----------------------------------------");
    }
    
    //marlon
    public static void initRowSet(CachedRowSet rowset) throws SQLException{
        java.sql.ResultSetMetaData cols = rowset.getMetaData();
        for(int n=1;n<=cols.getColumnCount();n++){
            switch(cols.getColumnType(n)){
                case java.sql.Types.BIGINT:
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                    rowset.updateObject(n, 0);
                    break;
                case java.sql.Types.DECIMAL:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.FLOAT:
                case java.sql.Types.NUMERIC:
                case java.sql.Types.REAL:
                    rowset.updateObject(n, 0.00);
                    break;
                case java.sql.Types.CHAR:
                case java.sql.Types.NCHAR:
                case java.sql.Types.NVARCHAR:
                case java.sql.Types.VARCHAR:
                    rowset.updateObject(n, "");
                    break;
                default:
                    rowset.updateObject(n, null);
            }
        }
    }
    
    //marlon
    public static String rowset2SQL(CachedRowSet rowset, String table, String exclude) throws SQLException{
        String sql = "";
        int col2 = 0;
        java.sql.ResultSetMetaData cols = rowset.getMetaData();
        
        exclude = exclude.toLowerCase();
        
        for(int n=1; n<= cols.getColumnCount(); n++){
            if(cols.getColumnName(n).equalsIgnoreCase("dModified")){
                col2 = n;
            }
            else if(!exclude.contains(cols.getColumnName(n).toLowerCase())){
                sql += ", " + cols.getColumnName(n) + " = " + SQLUtil.toSQL(rowset.getObject(n));
            }
        }

        if(col2 > 0){
            sql += ", dModified = " + SQLUtil.toSQL(Calendar.getInstance().getTime());
        }
        
        sql = "INSERT INTO " + table + " SET " + sql.substring(2);
        
        return sql;
    }
    
    //marlon
    public static String rowset2SQL(CachedRowSet rowset, String table, String exclude, String filter) throws SQLException{
        String sql = "";
        int col2 = 0;
        java.sql.ResultSetMetaData cols = rowset.getMetaData();

        exclude = exclude.toLowerCase();
        
        for(int n=1; n<= cols.getColumnCount(); n++){
            if(cols.getColumnLabel(n).equalsIgnoreCase("dModified")){
                col2 = n;
            }
            else if(!exclude.contains(cols.getColumnLabel(n).toLowerCase())){
                if(rowset.columnUpdated(n)){
                    sql += ", " + cols.getColumnLabel(n) + " = " + SQLUtil.toSQL(rowset.getObject(n));
                }
            }
        }
    
        if(!sql.isEmpty()){
            if(col2 > 0){
                sql += ", dModified = " + SQLUtil.toSQL(Calendar.getInstance().getTime());
            }
            
            sql = "UPDATE " + table + " SET " + sql.substring(2) + " WHERE " + filter;
        }
        
        return sql;
    }
    
    /**
     * Converts ResultSet metadata to XML file.<br><br>
     * 
     * Author: Michael Cuison<br>
     * Date: 2024.04.06
     * 
     * @param foRS
     * @param fsFileName
     * @return true if successful, else false
     * @throws SQLException 
     */
    public static boolean resultSet2XML(GRider foGRider, ResultSet foRS, String fsFileName, String fsTableNme, String fsExcluded) throws SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fsFileName))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<metadata>\n");
            writer.write("\t<table>" + fsTableNme + "</table>\n");

            int lnRow = foRS.getMetaData().getColumnCount();
            
            String lsSQL = "SELECT" +
                                "  sFormatxx" +
                                ", sRegTypex" +
                                ", sValueFrm" +
                                ", sValueThr" +
                                ", sValueLst" + 
                            " FROM xxxSysColumn";
            
            String lsFormatxx;
            String lsRegTypex;
            String lsValueFrm;
            String lsValueThr;
            String lsValueLst;
            
            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                lsFormatxx = null;
                lsRegTypex = null;
                lsValueFrm = null;
                lsValueThr = null;
                lsValueLst = null;
                
                if (!fsExcluded.contains(foRS.getMetaData().getColumnLabel(lnCtr))){
                    String sql =  addCondition(lsSQL, "sTableNme = " + SQLUtil.toSQL(fsTableNme) +
                                                        " AND sColumnNm = " + SQLUtil.toSQL(foRS.getMetaData().getColumnName(lnCtr)));
                
                    ResultSet oRS = foGRider.executeQuery(sql);

                    if (oRS.next()){
                        lsFormatxx = oRS.getString("sFormatxx");
                        lsRegTypex = oRS.getString("sRegTypex");
                        lsValueFrm = oRS.getString("sValueFrm");
                        lsValueThr = oRS.getString("sValueThr");
                        lsValueLst = oRS.getString("sValueLst");
                    }
                }
                
                writer.write("\t<column>\n");
                writer.write("\t\t<COLUMN_NAME>" + foRS.getMetaData().getColumnName(lnCtr) + "</COLUMN_NAME>\n");
                writer.write("\t\t<COLUMN_LABEL>" + foRS.getMetaData().getColumnLabel(lnCtr) + "</COLUMN_LABEL>\n");
                writer.write("\t\t<DATA_TYPE>" + foRS.getMetaData().getColumnType(lnCtr) + "</DATA_TYPE>\n");
                writer.write("\t\t<NULLABLE>" + foRS.getMetaData().isNullable(lnCtr) + "</NULLABLE>\n");
                writer.write("\t\t<LENGTH>" + foRS.getMetaData().getColumnDisplaySize(lnCtr) + "</LENGTH>\n");
                writer.write("\t\t<PRECISION>" + foRS.getMetaData().getPrecision(lnCtr) + "</PRECISION>\n");
                writer.write("\t\t<SCALE>" + foRS.getMetaData().getScale(lnCtr) + "</SCALE>\n");
                writer.write("\t\t<FORMAT>" + lsFormatxx + "</FORMAT>\n");
                writer.write("\t\t<REGTYPE>" + lsRegTypex + "</REGTYPE>\n");
                writer.write("\t\t<FROM>" + lsValueFrm + "</FROM>\n");
                writer.write("\t\t<THRU>" + lsValueThr + "</THRU>\n");
                writer.write("\t\t<LIST>" + lsValueLst + "</LIST>\n");
                writer.write("\t</column>\n");
            }

            writer.write("</metadata>");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * Converts XML file to CachedRowSet<br><br> 
    * 
     * Author: Michael Cuison<br>
     * Date: 2024.04.06
     * 
     * @param fsFileName
     * @param fsTableName
     * @return CachedRowSet
     */
    public static CachedRowSet xml2ResultSet(String fsFileName, String fsTableName) {
        CachedRowSet cachedRowSet = null;
        try {
            File file = new File(fsFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("column");
            int lnRow = nodeList.getLength();
            
            RowSetMetaData meta = new RowSetMetaDataImpl();
            meta.setColumnCount(lnRow);
            
            for (int i = 0; i < lnRow; i++) {
                Element element = (Element) nodeList.item(i);
                String columnName = element.getElementsByTagName("COLUMN_NAME").item(0).getTextContent();
                String columnLabel = element.getElementsByTagName("COLUMN_LABEL").item(0).getTextContent();
                String dataType = element.getElementsByTagName("DATA_TYPE").item(0).getTextContent();
                String nullable = element.getElementsByTagName("NULLABLE").item(0).getTextContent();
                String length = element.getElementsByTagName("LENGTH").item(0).getTextContent();
                String precision = element.getElementsByTagName("PRECISION").item(0).getTextContent();
                String scale = element.getElementsByTagName("SCALE").item(0).getTextContent();
                
                meta.setColumnName(i + 1, columnName);
                meta.setColumnLabel(i + 1, columnLabel);
                meta.setColumnType(i + 1, Integer.parseInt(dataType));
                meta.setColumnType(i + 1, Integer.parseInt(nullable));
                meta.setColumnDisplaySize(i + 1, Integer.parseInt(length));
                meta.setPrecision(i + 1, Integer.parseInt(precision));
                meta.setScale(i + 1, Integer.parseInt(scale));
            }
            
            cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.setMetaData(meta);
        } catch (IOException | SQLException | ParserConfigurationException | DOMException | SAXException e) {
            e.printStackTrace();
        }
        return cachedRowSet;
    }
    
    public static JSONObject validateColumnValue(String fsXMLFile, String fsColumnNm, Object foValue){
        JSONObject loJSON = new JSONObject();
        
        try {
            File file = new File(fsXMLFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("column");
            int lnRow = nodeList.getLength();
            
            RowSetMetaData meta = new RowSetMetaDataImpl();
            meta.setColumnCount(lnRow);
            
            for (int i = 0; i < lnRow; i++) {
                Element element = (Element) nodeList.item(i);
                String columnName = element.getElementsByTagName("COLUMN_LABEL").item(0).getTextContent();
                
                if (columnName.equals(fsColumnNm)){
                    String dataType = element.getElementsByTagName("DATA_TYPE").item(0).getTextContent();
                    String nullable = element.getElementsByTagName("NULLABLE").item(0).getTextContent();
                    String length = element.getElementsByTagName("LENGTH").item(0).getTextContent();
                    String precision = element.getElementsByTagName("PRECISION").item(0).getTextContent();
                    String scale = element.getElementsByTagName("SCALE").item(0).getTextContent();
                
                    int columnType = Integer.parseInt(dataType);
                    int columnDisplaySize = Integer.parseInt(length);
                    int columnScale = Integer.parseInt(scale);
                    int columnPrecision = Integer.parseInt(precision);
                    boolean isNullable = nullable.equals("1");
                    
                    return validMetadata(fsColumnNm, columnType, columnDisplaySize, columnScale, columnPrecision, isNullable, foValue);
                }
            }
            
            loJSON.put("result", "error");
            loJSON.put("message", "Unable to find column to validate.");
        } catch (IOException | SQLException | ParserConfigurationException | DOMException | SAXException e) {
            loJSON.put("result", "error");
            loJSON.put("message", e.getMessage());
        }
        
        return loJSON;
    }
    
    public static JSONObject validateColumnValue(GRider foGRider, String fsTableName, String fsColumnNm, Object foValue){
        String lsSQL = "SELECT * FROM xxxSysColumn";
        
        lsSQL = addCondition(lsSQL, "sTableNme = " + SQLUtil.toSQL(fsTableName) +
                            " AND sColLabel = " + SQLUtil.toSQL(fsColumnNm)) ;
        
        ResultSet loRS = foGRider.executeQuery(lsSQL);
        
        JSONObject loJSON = new JSONObject();
        
        try {
            if (loRS.next()){
                int columnType = loRS.getInt("nColumnTp");
                int columnDisplaySize = loRS.getInt("nLengthxx");
                int columnScale = loRS.getInt("nScalexxx");
                int columnPrecision = loRS.getInt("nPrecisnx");
                boolean isNullable = loRS.getString("cIsNullxx").equals("1");
                
                return validMetadata(fsColumnNm, columnType, columnDisplaySize, columnScale, columnPrecision, isNullable, foValue);
                
                //todo: validate column value
            } else {
                loJSON.put("result", "success");
                loJSON.put("message", "Column has no validation.");
            }
        } catch (SQLException e) {
            loJSON.put("result", "error");
            loJSON.put("message", e.getMessage());
        }
        
        return loJSON;
    }
    
    private static JSONObject validMetadata(String fsColumnNm, int columnType, int columnDisplaySize, int columnScale, int columnPrecision, boolean isNullable, Object foValue){
        JSONObject loJSON = new JSONObject();

        //Check if the value is null and the column is not nullable
        if (foValue == null && !isNullable) {
            loJSON.put("result", "error");
            loJSON.put("message", "Value cannot be null.");
            return loJSON;
        }

        switch (columnType) {
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.SMALLINT:
            case Types.TINYINT:
                if (!(foValue instanceof Number)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + "must be a number.");
                    return loJSON;
                }

                long longValue = ((Number) foValue).longValue();
                if (longValue < -Math.pow(10, columnPrecision) || longValue > Math.pow(10, columnPrecision) - 1) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " is out of range.");
                    return loJSON;
                }
                break;
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                if (!(foValue instanceof Number)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a number.");
                    return loJSON;
                }

                double doubleValue = ((Number) foValue).doubleValue();
                if (doubleValue < -Math.pow(10, columnPrecision - columnScale) || doubleValue > Math.pow(10, columnPrecision - columnScale)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " is out of range.");
                    return loJSON;
                }
                break;
            case Types.DECIMAL:
                if (!(foValue instanceof Number)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a number.");
                    return loJSON;
                }
                double decimalValue = ((Number) foValue).doubleValue();
                if (decimalValue < -Math.pow(10, columnPrecision - columnScale) || decimalValue > Math.pow(10, columnPrecision - columnScale)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " is out of range.");
                    return loJSON;
                }
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                if (!(foValue instanceof String)) {
                    loJSON.put("success", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a string.");
                    return loJSON;
                }

                String stringValue = (String) foValue;
                if (stringValue.length() > columnDisplaySize) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " exceeds maximum length for the field.");
                    return loJSON;
                }
                break;
            case Types.DATE:
                if (!(foValue instanceof java.sql.Date || foValue instanceof java.util.Date)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a date object.");
                    return loJSON;
                }
                break;
            case Types.TIME:
                if (!(foValue instanceof java.sql.Time)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a time object.");
                    return loJSON;
                }
                break;
            case Types.TIMESTAMP:
                if (!(foValue instanceof java.sql.Timestamp)) {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Value for " + fsColumnNm + " must be a java.sql.Timestamp object.");
                    return loJSON;
                }
                break;
            default:
                // Unsupported data type
                loJSON.put("result", "error");
                loJSON.put("message", "Unsupported data type for validation.");
                return loJSON;
        }
        
        loJSON.put("result", "success");
        loJSON.put("message", "Value is valid for this field.");
        return loJSON;
    }
    
    public static GRider Connect(){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        try {
            Properties po_props = new Properties();
            po_props.load(new FileInputStream(path + "/config/cas.properties"));
            
            if (po_props.getProperty("developer.mode").equals("1")){
                GRider instance = new GRider("gRider");
        
                if (!instance.logUser("gRider", "M001000001")){
                    System.err.println(instance.getErrMsg());
                    System.exit(1);
                }

                return instance;
            }
        } catch (IOException e) {
            System.exit(1);
        }
        
        return null;
    }
}