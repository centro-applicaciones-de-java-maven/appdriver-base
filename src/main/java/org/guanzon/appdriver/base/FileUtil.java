package org.guanzon.appdriver.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static boolean exists(String fsFile){
        File afile = new File(fsFile);
            
        return afile.exists() && !afile.isDirectory();
    }
    
    public static boolean copyFile(String fsOrgDir, String fsCopyDir){
        InputStream inStream = null;
	OutputStream outStream = null;
        
        try{            
    	    File afile = new File(fsOrgDir);
            
            //check if the original file exists
            if (afile.exists() && !afile.isDirectory()){
                File bfile = new File(fsCopyDir);
                
                //check if the copy path is not a directory
                if (bfile.isDirectory()) return false;
    		
                inStream = new FileInputStream(afile);
                outStream = new FileOutputStream(bfile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes 
                while ((length = inStream.read(buffer)) > 0){
                    outStream.write(buffer, 0, length);
                }

                inStream.close();
                outStream.close();
                
                return true;
            }    	    
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
        
        return false;
    }
    
    public static boolean moveFile(String fsOrgDir, String fsCopyDir){
        InputStream inStream = null;
	OutputStream outStream = null;
        
        try{            
    	    File afile = new File(fsOrgDir);
            
            //check if the original file exists
            if (afile.exists() && !afile.isDirectory()){
                File bfile = new File(fsCopyDir);
                
                //check if the copy path is not a directory
                if (bfile.isDirectory()) return false;
    		
                inStream = new FileInputStream(afile);
                outStream = new FileOutputStream(bfile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes 
                while ((length = inStream.read(buffer)) > 0){
                    outStream.write(buffer, 0, length);
                }

                inStream.close();
                outStream.close();

                //delete the original file
                afile.delete();

                return true;
            }    	    
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
        
        return false;
    }
    
    public static boolean fileDelete(String filename){
        File afile = new File(filename);
        if (afile.exists()) {
            return afile.delete();
        }
        
        return false;
    }
    
    public static String fileRead(String filename){
      StringBuilder lsText = new StringBuilder();
      try {
         FileReader reader = new FileReader(filename);
         BufferedReader bufferedReader = new BufferedReader(reader);

         String line;

         while ((line = bufferedReader.readLine()) != null) {
            lsText.append(line);
         }
         reader.close();

      } catch (IOException e) {
         return "";
      }
         return lsText.toString();
   }
   
    public static boolean fileWrite(String filename, String data){
        try {
            FileWriter writer = new FileWriter(filename, false);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }      
        
        return true;
    }
   
   public static boolean fileWrite(String filename, String data, boolean append){
        try {
            FileWriter writer = new FileWriter(filename, append);
            writer.write(append == true ? "\n" + data : "" + data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }   
}
