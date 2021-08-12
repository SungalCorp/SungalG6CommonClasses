/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RFIDClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author danrothman
 */
public class TagLocation {

    //public Integer shelfID;
    private String trackID;
    private String gondola;
    private String shelf;
    private String facing;
    private Double inchesFromLeft = -1.0;
    private ArrayList<Integer> scannersInComputation = new ArrayList<>();
    //public HashMap<String, String> scannerHashMap = new HashMap<String,String>();

    //Getters
    public String getTrackID() {
        return trackID;
    }

    public String getGondola() {
        return gondola;
    }

    public String getShelf() {
        return shelf;
    }

    public String getFacing() {
        return facing;
    }

    public String getInchesFromLeft() {
        return inchesFromLeft + "";
    }

    public String getScannersInComputation() {
        String rVal = "";
        boolean isFirstTime = true;
        Collections.sort(scannersInComputation);
        Integer lastScannerAddress = -1;
        for (Integer scannerAddress : this.scannersInComputation) {
            if (scannerAddress != lastScannerAddress) {
                rVal += ((!isFirstTime ? "," : "") + scannerAddress);
            }
            lastScannerAddress = scannerAddress;
            isFirstTime = false;
        }
        return rVal;
        // return scannersInComputation;
    }

    //setters
    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public void setGondola(String gondola) {
        this.gondola = gondola;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public void setInchesFromLeft(Double inchesFromLeft) {
        this.inchesFromLeft = inchesFromLeft;
    }

    public void setScannersInComputation(ArrayList<Integer> scannersInComputation) {
        this.scannersInComputation = scannersInComputation;
    }
}
