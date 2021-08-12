/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseIO;

import gen.SysVars;
import static gen.SysVars.API_HOST_DOMAIN;
import static gen.Utils.stringToDate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author danrothman
 */
public class DataRetrieval {

    public static JSONArray getRecords(JSONArray recList, String lookupField, Integer recID,
            boolean ordered) throws JSONException {

        JSONArray selectedFile = new JSONArray();
        boolean found = false;
        for (int i = 0; i < recList.length(); i++) {
            if (recList.getJSONObject(i).getInt(lookupField) == recID) {
                selectedFile.put(recList.getJSONObject(i));
                found = true;
            } else {
                if (ordered && found) {
                    return selectedFile;
                }
            }
        }

        return selectedFile;
    }

    public static JSONObject getHardwareID(String serialNumber) {

        try {
            String s = SysVars.API_HOST_DOMAIN + "dbGet_hardwareids_by_id?id=ALL&filter=serialnumber='"
                    + serialNumber + "'";
            JSONObject rVal = getRecords(s).getJSONObject(0);
            return rVal;

        } catch (JSONException ex) {
            return null;
        }
    }

    public static JSONArray getRecords(String mURL) {
        //
        //*******************Establish a connection
        HttpURLConnection con;
        try {
            
            con = getConnection(mURL);
            System.out.println("ATTEMPTING TO EXECUTE API:" + mURL);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                //System.out.println("line=" + inputLine);
                content.append(inputLine);
            }

            JSONArray jsonRet = new JSONArray(content.toString());
            in.close();
            System.out.println("SUCCESSFULY EXECUTED:" + mURL);

            return jsonRet;

        } catch (MalformedURLException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }

        //**********************Set request params
        ///////
        return null;
    }

    private static HttpURLConnection getConnection(String url) throws MalformedURLException, ProtocolException, IOException {
        URL url2 = new URL(url);
        //System.out.println(url);
        HttpURLConnection con;
        con = (HttpURLConnection) url2.openConnection();
        con.setRequestMethod("GET");
        con.getResponseMessage();
        return con;
    }

    public static boolean insertRecord(String mURL) {
        //*******************Establish a connection
        URL url2;
        HttpURLConnection con;
        try {
            con = getConnection(mURL);
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ProtocolException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (Exception ex) {
            Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public static JSONArray getDeviceTypes(String deviceTypeID) {
        // if deviceTypeID = ALL return full dataset otherwise select for keyfield 
        // deviceTypeID
        //dbGet_hardwareids_by_id?id=ALL
        return getRecords(SysVars.API_HOST_DOMAIN
                + "dbGet_devicetypes_by_id?id=" + deviceTypeID);
    }

    public static JSONObject getLatestFacingMerchandiseLink(Integer facingID, Date asOfDate) {
        // here here
        JSONArray oArray = getRecords(SysVars.API_HOST_DOMAIN
                + "dbGet_facingmerchandiselinks_by_id?id=ALL&filter=facingID=" + facingID); //FML_MAP.get(facingID);
        if (oArray == null) {
            return null;
        }

        for (int i = oArray.length() - 1; i >= 0; i--) {
            try {
                Date recDate = stringToDate(oArray.getJSONObject(i).getString("fromDate"));
                if (recDate.compareTo(asOfDate) <= 0) {
                    return oArray.getJSONObject(i);
                }
            } catch (JSONException ex) {
                Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;

    }   
    
    public static HashMap<Integer,String> getSensorValsFromDB(int storeID){
        HashMap<Integer,String> rVal = new HashMap<>();
        // get latest sensor values, last one entered into database
        String APIString = API_HOST_DOMAIN + 
                "dbGet_sensorstates1_by_id=ALL&filter=storeID=" + storeID;
        JSONArray sensorVals = databaseIO.DataRetrieval.getRecords(APIString);
        if (sensorVals == null){
        //    return null;
        }
        for (int i = 0; i < sensorVals.length();i++ ){
            
            try {
                JSONObject svRec = sensorVals.getJSONObject(i);
                rVal.put(svRec.getInt("facingID"), svRec.getString("state"));
            } catch (JSONException ex) {
                Logger.getLogger(DataRetrieval.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return rVal;
    }



    
}