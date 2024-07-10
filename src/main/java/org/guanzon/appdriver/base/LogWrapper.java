package org.guanzon.appdriver.base;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogWrapper{
    private Logger logger;
    private FileHandler fileTxt;
    private SimpleFormatter formatterTxt;
    
    private String logf;
    
    public LogWrapper(String logr, String logfile){
        logf = logfile;
        logger = Logger.getLogger(logr);
        try{
            fileTxt = new FileHandler(logf, true);

            formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
    public void info(String msg){
        logger.log(Level.INFO, msg);   
    }
    
    public void warning(String msg){
        logger.log(Level.WARNING, msg);   
    }

    public void warning(String msg, Object param){
        logger.log(Level.WARNING, msg, param);   
    }

    public void warning(String msg, Object []param){
        logger.log(Level.WARNING, msg, param);   
    }
    
    public void warning(String msg, Throwable thrown){
        logger.log(Level.WARNING, msg, thrown);   
    }
    
    public void severe(String msg){
        logger.log(Level.SEVERE, msg);   
    }

    public void severe(String msg, Object param){
        logger.log(Level.SEVERE, msg, param);
    }

    public void severe(String msg, Object []param){
        logger.log(Level.SEVERE, msg, param);   
    }
    
    public void severe(String msg, Throwable thrown){
        logger.log(Level.SEVERE, msg, thrown);   
    }
}
