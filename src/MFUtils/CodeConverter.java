/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MFUtils;

import gen.SysVars;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author danrothman
 */
public class CodeConverter {

    private static SimpleDateFormat myFormat = new SimpleDateFormat("mm/dd/yyyy");
    private static SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-mm-dd");
    private static SimpleDateFormat myFormat3 = new SimpleDateFormat("yyyy/MM/dd");

    private static String INITIAL_DATE = "2015/01/01"; //yyyy/mm/dd
    private static String baseNTokenArray = "0123456789"
            + "abcdefghij"
            + "klmnopqrst"
            + "uvwxyzABCD"
            + "EFGHIJKLMN"
            + "OPQRSTUVWX"
            + "YZ";
    private static Integer CONVERTER_BASE = 62;
    private static Integer STARTING_MOTHERBOARDID = 3000;

    //
    public static String encodeDec(Integer n, Integer base, Integer places) {
        //input decimal number and returns its baseX equivalent

        int nLeftToProcess = n;
        String currentDigit = "";
        String rVal = "";
        do {
            Integer index = nLeftToProcess % base;
            currentDigit = baseNTokenArray.substring(index, index + 1);
            nLeftToProcess /= base;
            rVal = currentDigit + rVal;
        } while (nLeftToProcess > 0);

        Integer padding = places - rVal.length();
        if (padding <= 0) {
            return rVal.substring(0, places);
        }
        return String.join("", Collections.nCopies(places - rVal.length(), "0")) + rVal;
    }

    public static String addPadding(Object s, int places, String character, String orientation) {
        // returns a string with stringified s + padding chars of a length specified
        // by parameter "places"
        String s2 = s + "";
        Integer padding = places - s2.length();
        if (padding <= 0) {
            return s2.substring(0, places);
        }
        String rVal = s2 + String.join("", Collections.nCopies(padding, character));;
        if (orientation.equalsIgnoreCase("R")) {
            rVal = String.join("", Collections.nCopies(padding, character)) + s2;
        }
        return rVal;
    }

    public static String addPadding(Object s, int places, String character) {
        // returns a string with stringified s + padding chars of a length specified
        // by parameter "places"
        return addPadding(s, places, character, "");
    }

    public static Integer decodeBaseN(String baseN, Integer base) {
        //input base N number and returns its base 10 equivalent

        Integer rVal = 0;
        Integer p = 1;
        for (int i = baseN.length() - 1; i >= 0; i--) {
            String token = baseN.substring(i, i + 1);
            rVal += (baseNTokenArray.indexOf(token) * p);
            p *= base;
        }
        return rVal;
    }

    public static String encodeDate(String dateString, Integer places) {
        //input date in stringform, return 
        long diff = 0;
        try {
            Date date1 = myFormat3.parse(INITIAL_DATE);
            Date date2 = myFormat3.parse(dateString);
            diff = date2.getTime() - date1.getTime();
            int days = (int) (TimeUnit.DAYS.convert(diff,
                    TimeUnit.MILLISECONDS));

            String rVal = encodeDec(days, CONVERTER_BASE, places);
            return rVal;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeBNDate(String baseNDate, Integer base) {
        Integer nDays = decodeBaseN(baseNDate, base);
        //////
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(myFormat3.parse(INITIAL_DATE)); // Now use today date.
            c.add(Calendar.DATE, nDays); // Adding 5 days
            return myFormat3.format(c.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String generateSN(String mfDate, Integer deviceType, Integer addr, Integer counter) {
        // generate SN number using mfDate, devicetype, RS485/IP, counter 
        //                           XXX        XX       XXXX      XXX
        String sDate = encodeDate(mfDate, 3);
        String sDeviceType = encodeDec(deviceType, CONVERTER_BASE, 2);
        String sAddr = encodeDec(addr, CONVERTER_BASE, 4);
        String sCounter = encodeDec(counter, CONVERTER_BASE, 3);

        return sDate + sDeviceType + sAddr + sCounter;

    }

    public static JSONObject decodeSN(String sn) throws JSONException {
        JSONObject rVal = new JSONObject();
        String mfDateString = sn.substring(0, 3);
        String deviceTypeString = sn.substring(3, 5);
        String addressString = sn.substring(5, 9);
        rVal.put("mfdate", decodeBNDate(mfDateString, CONVERTER_BASE));
        rVal.put("deviceType", decodeBaseN(deviceTypeString, CONVERTER_BASE));
        rVal.put("address", decodeBaseN(addressString, CONVERTER_BASE));
        return rVal;
        /*create json object 
        {mfDate:xxxx,
         devicetype:xxxxx,
        address:xxx
        };
        
        return encodeDate(mfDate,3)+ 
               encodeDec(deviceType,CONVERTER_BASE,2) +
               encodeDec(addr,CONVERTER_BASE,4) +
               encodeDec(counter,CONVERTER_BASE,3); 
         */

    }

    public static String convertToIPString(String address) {
        Integer iAddress = Integer.parseInt(address);
        return SysVars.IPADDR_PREFIX + (iAddress / 1000) + "." + (iAddress % 1000);
    }
}
