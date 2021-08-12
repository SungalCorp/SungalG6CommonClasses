/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RFIDClasses;

import MFUtils.CodeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author danrothman
 *
 * provides hash map functionality for RFID scanners and their recorded sector
 * data
 *
 *
 */
public class GlobalRFIDScannerMap {

    //key is RFIDScannerID, value is Scanner Addresses list and sector data
    private class ScannerRecord {

        public String sectorData;
        public ArrayList<Integer> scannersFound = new ArrayList<>();

    }

    private HashMap<String, ScannerRecord> scannerHashmap = new HashMap<>();
    //private Integer keyComponentLength = 9;

    public void put(String RFIDTagID, Integer RFIDScannerAddress, String sectorData) {

        ScannerRecord scannerRecord = new ScannerRecord();

        if (scannerHashmap.get(RFIDTagID) == null) {
            // we have a new RFIDTagID
            scannerRecord.sectorData = sectorData;
        } else {
            scannerRecord = scannerHashmap.get(RFIDTagID);
        }
        scannerRecord.scannersFound.add(RFIDScannerAddress);
        scannerHashmap.put(RFIDTagID, scannerRecord);

    }

    public String get(String key) {
        return "";
    }

    public String getSectorData(String RFIDTagID) {
        if (this.scannerHashmap.get(RFIDTagID) == null) {
            return null;
        }
        return this.scannerHashmap.get(RFIDTagID).sectorData;
    }

    public String getRFIDTagID(String dataSector) {
        try {
            return dataSector.substring(6, 22);
        } catch (Exception e) {
            return "NONE";
        }
    }

    public Integer getrs485Address(String dataSector) {
        try {
            return CodeConverter.decodeBaseN(dataSector.substring(44, 46), 16);   //Integer.valueOf(dataSector.substring(44,46));
        } catch (Exception e) {
            return -99;
        }
    }

    public Integer getDeviceType(String dataSector) {
        try {
            return CodeConverter.decodeBaseN(dataSector.substring(38, 42), 16);
        } catch (Exception e) {

            return -99;
        }
    }

    public Integer printMap() {
        return printMap(0);
    }

    public Integer printMap(Integer processNumber) {
        // Iterator
        Integer itemsPrinted = 0;
        Iterator<Entry<String, ScannerRecord>> new_Iterator
                = this.scannerHashmap.entrySet().iterator();

        // Iterating every set of entry in the HashMap
        while (new_Iterator.hasNext()) {
            HashMap.Entry<String, ScannerRecord> new_Map
                    = (HashMap.Entry<String, ScannerRecord>) new_Iterator.next();

            // Displaying HashMap
            String processFlag = "Called by PROCESSNUMBER " + processNumber + " ";
            System.out.println(processFlag + " RFIDTagID = " + new_Map.getKey() + " FOUND IN SCANNER ADDRESSES: ");
            System.out.println(processFlag + "************************ Start of Scanner Addresses *************");
            System.out.println(new_Map.getValue().scannersFound.size() + " mapElements in map");

            for (int i = 0; i < new_Map.getValue().scannersFound.size(); i++) {
                System.out.println(processFlag + "scanner address = " + new_Map.getValue().scannersFound.get(i));
            }
            System.out.println(processFlag + "************************ End Scanner Addresses *************");

            itemsPrinted++;
        }
        return itemsPrinted;
    }

    public TagLocation getRFIDTagLocation(String tagID, Integer startingScannerAddress,
            Double ScannerWidth, Double ScannerFirstPosition) {

        // get scanners involved
        ArrayList<Integer> activatedScanners = this.scannerHashmap.get(tagID).scannersFound;
        if (activatedScanners.isEmpty()) {
            return null;
        }

        TagLocation tagLocation = new TagLocation();
        tagLocation.setTrackID(tagID);
        tagLocation.setInchesFromLeft(0.0);
        tagLocation.setScannersInComputation(activatedScanners);

        Double distanceLowerBound = 100000.0;
        Double distanceUpperBound = 0.0;
        System.out.println("Scanner Width = " + ScannerWidth);
        for (int i = 0; i < activatedScanners.size(); i++) {
            System.out.println("detected by scanner address: " + activatedScanners.get(i));
            Double currentLowerBound = (activatedScanners.get(i) - startingScannerAddress) * ScannerWidth;
            Double currentUpperBound = currentLowerBound + ScannerWidth;
            if (currentLowerBound < distanceLowerBound) {
                distanceLowerBound = currentLowerBound;
            }
            if (currentUpperBound > distanceUpperBound) {
                distanceUpperBound = currentUpperBound;
            }

        }

        if (distanceLowerBound < distanceUpperBound) {
            System.out.println("distanceLowerBound : " + distanceLowerBound);
            System.out.println("distanceUpperBound : " + distanceUpperBound);

            tagLocation.setInchesFromLeft(distanceLowerBound + (distanceUpperBound - distanceLowerBound) / 2.0);
            System.out.println("tag location Inches from left : " + tagLocation.getInchesFromLeft());
        }

        return tagLocation;

    }

    public int size() {
        return this.scannerHashmap.size();
    }

}
