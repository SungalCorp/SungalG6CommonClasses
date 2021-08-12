/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MFUtils;



import static MFUtils.CodeConverter.encodeDec;
import static MFUtils.CodeConverter.generateSN;
import databaseIO.DataRetrieval;
import gen.SysVars;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author danrothman
 */
public class mfUtils {

    public static Integer getLastDeviceType(String mode) {
               
        String api = SysVars.API_HOST_DOMAIN + "dbCannedview_" + 
                       mode.substring(0, 1).toLowerCase()+"lastdevicetypeid";

        JSONArray devices = DataRetrieval.getRecords(api);
        if (devices.length() == 0) {
            return 0;
        }
        try {
            return devices.getJSONObject(0).getInt("lastdevicetypeid");
        } catch (JSONException ex) {
            Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static Integer getLastDeviceAddr(String mode) {
        mode = mode.toUpperCase();
        String api = SysVars.API_HOST_DOMAIN + "dbgetlasthardwareid_" + mode.substring(0, 1);

        JSONArray devices = DataRetrieval.getRecords(api);
        if (devices.length() == 0) {
            return 0;
        }
        try {
            return devices.getJSONObject(0).getInt("address");
        } catch (JSONException ex) {
            Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static Integer getLastCounter(String mfDate, Integer deviceType) {
        //mfDate must be in the form of yyyy-mm-dd

        String api = SysVars.API_HOST_DOMAIN + "dbgetlastofbatch?mfdate='"
                + mfDate + "'" + "&devicetype=" + deviceType;
        JSONArray devices = DataRetrieval.getRecords(api);
        if (devices.length() == 0) {
            return 0;
        }
        try {
            return devices.getJSONObject(0).getInt("counter");
        } catch (JSONException ex) {
            Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static Integer getLastBatchNumber() {
        //highestdevicebatchnumber
        String api = SysVars.API_HOST_DOMAIN + "dbCannedview_lastdevicebatchnumber";
        JSONArray lastBatchNumber = DataRetrieval.getRecords(api);
        if (lastBatchNumber.length() == 0) {
            return 0;
        }
        try {
            return lastBatchNumber.getJSONObject(0).getInt("lastbatchnumber");
        } catch (JSONException ex) {
            //Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static ArrayList<String> generateHardwareList(Integer quantity, String mDate,
            Integer deviceType, Integer lastAddress,
            Integer lastCounter, Integer batchNumber, boolean isMotherBoard,
            ObservableList<IPZone> IPZonesData) {

        ArrayList<String> rVal = new ArrayList<>();
        //////////
        //these variables are for motherboard IP Address generation only
        Integer zoneIndex = 0;
        IPZone currentZoneObj = IPZonesData.get(0);
        Integer IPADDR_1 = Integer.parseInt(currentZoneObj.getZone());
        Integer IPADDR_2 = Integer.parseInt(currentZoneObj.getLowestSubIP());
        Integer IP_2_Limit = Integer.parseInt(currentZoneObj.getHighestSubIP());

        if (isMotherBoard) {
            quantity = getQuantity(IPZonesData);
            lastAddress = (IPADDR_1 * 1000 + IPADDR_2) - 1;
        }
        //////////// end of vars for IP Address generation
        /////////

        for (int i = 0; i < quantity; i++) {
            //public static String generateSN(String mfDate, Integer deviceType, Integer addr, Integer counter) {
            String outItem = "SN:" + generateSN(mDate, deviceType, ++lastAddress, ++lastCounter);
            outItem += ("     dType:" + deviceType);
            outItem += ("     mfDate:" + mDate);
            outItem += (isMotherBoard ? "     IP addr:" : "       RS485:");

            if (isMotherBoard) {
                //////////
                outItem += (SysVars.IPADDR_PREFIX + IPADDR_1 + "."
                        + (IPADDR_2++));

                if (IPADDR_2 > IP_2_Limit) {
                    zoneIndex++;
                    if (zoneIndex < IPZonesData.size()) {
                        currentZoneObj = IPZonesData.get(zoneIndex);
                        IPADDR_1 = Integer.parseInt(currentZoneObj.getZone());
                        IPADDR_2 = Integer.parseInt(currentZoneObj.getLowestSubIP());
                        IP_2_Limit = Integer.parseInt(currentZoneObj.getHighestSubIP());
                        lastAddress = (IPADDR_1 * 1000 + IPADDR_2) - 1;
                    }
                }
                //////////

            } else {
                
                outItem += encodeDec(lastAddress, 16, 2).toUpperCase();
                if (Objects.equals(lastAddress, SysVars.MAX_RS485ADDR)) {
                    lastAddress = 0;
                }
            }

            outItem += "   Batch#:" + batchNumber;

            rVal.add(outItem);
            if (lastCounter == 255) {
                lastCounter = 0;
            }

        }
        return rVal;
    }

    public static JSONArray generateHardwareIDRecs(Integer quantity, String mDate,
            Integer deviceType, Integer lastAddress,
            Integer lastCounter, Integer batchNumber, boolean isMotherBoard,
            ObservableList<IPZone> IPZonesData) {

        //these variables are for motherboard IP Address generation only
        Integer zoneIndex = 0;
        IPZone currentZoneObj = IPZonesData.get(0);
        Integer IPADDR_1 = Integer.parseInt(currentZoneObj.getZone());
        Integer IPADDR_2 = Integer.parseInt(currentZoneObj.getLowestSubIP());
        Integer IP_2_Limit = Integer.parseInt(currentZoneObj.getHighestSubIP());

        if (isMotherBoard) {
            quantity = getQuantity(IPZonesData);
            lastAddress = (IPADDR_1 * 1000 + IPADDR_2) - 1;
        }
        //////////// end of vars for IP Address generation

        JSONArray rVal = new JSONArray();

        for (int i = 0; i < quantity; i++) {
            try {
                JSONObject rec = new JSONObject();
                // date should be in the form of yyyy/mm/dd
                rec.put("serialnumber", generateSN(mDate, deviceType, ++lastAddress, ++lastCounter));
                rec.put("counter", lastCounter);
                rec.put("deviceType", deviceType);
                rec.put("mfdate", mDate);

                if (isMotherBoard) {

                    rec.put("address", lastAddress);

                    if (IPADDR_2 > IP_2_Limit) {
                        zoneIndex++;
                        if (zoneIndex < IPZonesData.size()) {
                            currentZoneObj = IPZonesData.get(zoneIndex);
                            IPADDR_1 = Integer.parseInt(currentZoneObj.getZone());
                            IPADDR_2 = Integer.parseInt(currentZoneObj.getLowestSubIP());
                            IP_2_Limit = Integer.parseInt(currentZoneObj.getHighestSubIP());
                            lastAddress = (IPADDR_1 * 1000 + IPADDR_2) - 1;
                        }
                    }

                } else {
                    rec.put("address", lastAddress);
                    //rec.put("address", encodeDec(lastAddress, 16, 2));
                    //rec.put("address", encodeDec(lastAddress, 16, 2));
                    if (Objects.equals(lastAddress, SysVars.MAX_RS485ADDR)) {
                        lastAddress = 0;
                    }
                }

                rec.put("batchnumber", batchNumber);
                if (lastCounter == 255) {
                    lastCounter = 0;
                }
                rVal.put(rec);

            } catch (JSONException ex) {
                Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return rVal;
    }

    public static boolean saveHardwareIDrecsToDatabase(JSONArray recs) {
        try {
            JSONObject j = new JSONObject();
            j.put("recs", recs);
            String APICall = SysVars.API_HOST_DOMAIN + "dbMultiInsert?tablename=hardwareids&fields="
                    + j.toString();

            DataRetrieval.insertRecord(APICall);
            return true;
        } catch (JSONException ex) {
            Logger.getLogger(mfUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static Integer getQuantity(ObservableList<IPZone> IPZonesData) {
        Integer quantity = 0;
        for (int i = 0; i < IPZonesData.size(); i++) {
            IPZone zoneObj = IPZonesData.get(i);
            Integer zone = Integer.parseInt(zoneObj.getZone());
            Integer highVal = Integer.parseInt(zoneObj.getHighestSubIP());
            Integer lowVal = Integer.parseInt(zoneObj.getLowestSubIP()); 
            if (zone != 0 && highVal != 0 && lowVal !=0) {
                quantity += ((highVal - lowVal) + 1);
            }
        }
        return quantity;
    }
    
    
}
