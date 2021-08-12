package gen;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author danrothman
 */
public class SysVars {
    
    
    /*
    String home = System.getProperty("user.home");

    // inserts correct file path separator on *nix and Windows
    // works on *nix
    // works on Windows
    java.nio.file.Path path = java.nio.file.Paths.get(home, "my", "app", "dir")
    boolean directoryExists = java.nio.file.Files.exists(path);
    */
    public static String home = System.getProperty("user.home");
    

    // make this configurable
    //public static String PRODUCT_IMAGE_FOLDER = "file:///C:/Users/User/Desktop/product_pics/";
    //public static String PRODUCT_IMAGE_FOLDER = "file:///Users/danrothman/Documents/SUNGALAPP/product_pics/";
    
    // this is now in the config file
    public static String PRODUCT_IMAGE_FOLDER;

    //public static String PRODUCT_IMAGE_FOLDER = "file:../product_pics/";
   
    public static String API_HOST_DOMAIN = "http://localhost:8080/";
    //public static String API_HOST_DOMAIN   = "https://lpog-api.uk.r.appspot.com/";

    public static Integer MIN_MOTHERBOARD_DEVICETYPEID = 3000;
    public static String IPADDR_PREFIX = "192.168.";
    public static Integer MAX_IPADDR =  255255;
    public static Integer MAX_RS485ADDR = 254;
    
    public static Integer DEFAULT_THRESHOLD_ADJUSTMENT = 1000;
    public static String COMM_FAILURE_MESSAGE = "COMM_FAILURE";
    public static String GENERAL_ERROR_MESSAGE = "ERROR";
    public static String DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
    public static String LOCALDATETIME_FORMAT = "yyyy-mm-dd'T'hh:mm:ss";
    
    public static double SENSOR_SPACING_STD = .792;
    
    
    public static int SECONDS_PER_MINUTE = 60;
    public static int MINUTES_PER_HOUR = 60;
    public static int HOURS_PER_DAY=24;
    public static int DAYS_PER_WEEK=7;
    public static int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    public static int SECONDS_PER_WEEK = SECONDS_PER_DAY * DAYS_PER_WEEK;
    
    
    
}