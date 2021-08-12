/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import static gen.SysVars.DATE_FORMAT;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author danrothman
 */
public class Utils {

    public static String addPads(String s, Integer spaces) {
        // creates a string from original string with enough space characters at the end to be the length specified by spaces param.
        String rVal = s.trim() + String.join("", Collections.nCopies(spaces, " "));
        return rVal.substring(0, spaces) + "  ";
    }

 
    public static boolean showAlert(String title, String header, String message) {
        Alert alert = showDialogue(new Alert(AlertType.INFORMATION),
                title, header, message);
        alert.show();
        return true;
    }

    public static boolean showConfirmation(String title, String header, String message) {
        Alert alert = showDialogue(new Alert(AlertType.CONFIRMATION),
                title, header, message);

        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent()) && (result.get() == ButtonType.OK);
    }

    private static Alert showDialogue(Alert alert, String title,
            String header, String message) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static String dateToString(Date date) {
        return dateToString(date, DATE_FORMAT);
    }

    public static String dateToString(Date date, String format) {

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);
        System.out.println("Converted String: " + strDate);
        return strDate;
    }

    public static Date stringToDate(String sDate) {
        return stringToDate(sDate, DATE_FORMAT);
    }

    public static Date stringToDate(String sDate, String format) {
        try {
            //return new SimpleDateFormat("yyyy-mm-dd").parse(sDate.substring(0, 10));
            //return new SimpleDateFormat(format).parse(sDate.substring(0, 10));

            //2019-05-01T04:00:00.000Z what the date could look like
            String sDateClean = cleanDateString(sDate);
            return new SimpleDateFormat(format).parse(sDateClean.substring(0, format.length()));
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String cleanDateString(String dateString) {
        return dateString.replaceAll("[a-zA-Z]", " ");
    }

    public static HashMap<String, String> getIniVals(String iniFile) {
        // get all the properties listed in an inifile
        HashMap<String, String> configValsReturned = new HashMap<>();
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(iniFile));
        } catch (Exception e) {
            configValsReturned.put("NOFILE", "NOFILE");
            return configValsReturned;
        }
        String[] propertiesToFind = new String[p.size()];

        Set<String> keys = p.stringPropertyNames();
        int i = 0;
        for (String key : keys) {
            propertiesToFind[i++] = key.toUpperCase();
        }
        return getIniVals(propertiesToFind,iniFile);

    }

    public static HashMap<String, String> getIniVals(String propertyToFind, String iniFile) {
        String[] propertiesToFind = {propertyToFind};
        return getIniVals(propertiesToFind, iniFile);
    }

    public static HashMap<String, String> getIniVals(String[] propertiesToFind, String iniFile) {
        // takes an array containing property names
        // and return a hashmap of ini file values.
        // key = valuePairs[i];

        HashMap<String, String> configValsReturned = new HashMap<>();
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(iniFile));
        } catch (Exception e) {
            configValsReturned.put("NOFILE", "NOFILE");
            return configValsReturned;
        }

        //loop through all the value pairs and return array
        for (int i = 0; i < propertiesToFind.length; i++) {
            String keyField = propertiesToFind[i].toUpperCase();
            String valueField = p.getProperty(keyField);
            configValsReturned.put(keyField, valueField);
        }
        return configValsReturned;
    }
    
    public static double getPercent(String s){
      //takes a string of 0s and 1s and returns percent as 1s/(total chars in string)
       long count = s.chars().filter(ch -> ch == '1').count();
 
      double percent =  ((double)count/(double) s.length());
      System.out.println("??? items in facing =" + (double)count);
      System.out.println("??? facing capacity = " + (double) s.length());
      System.out.println("??? inventory percent = " + percent);
      return percent * 100;
    }
    public static boolean isZerosAndOnes(String s){ 
        return ((s.split("1").length - 1) + (s.split("0").length - 1) == s.length());
    }
    
    ///////////
    

    public static int getSecondsSinceMidnight() {
         Calendar c = Calendar.getInstance();
        return 3600 * c.get(Calendar.HOUR_OF_DAY) + 60
                * c.get(Calendar.MINUTE) + c.get(Calendar.SECOND);
    }
    /////////
    
    
}
