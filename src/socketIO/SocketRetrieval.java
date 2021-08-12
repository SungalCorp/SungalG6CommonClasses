/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketIO;

import static MFUtils.CodeConverter.decodeBaseN;
import static MFUtils.CodeConverter.encodeDec;
import gen.SysVars;
import static gen.SysVars.COMM_FAILURE_MESSAGE;
import static gen.Utils.showConfirmation;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author danrothman
 */
public class SocketRetrieval {
    //
    //  checksum values for RFID retrieve scanner sector info requests from socket
    public static HashMap<Integer,String> RFID_CRC16Table = new HashMap<Integer, String>() {
        {
                put(188,"95AB");
                put(189,"9457");
                put(190,"9413");
                put(191,"95EF");
                put(192,"8C3B");
                put(193,"8DC7");
                put(194,"8D83");
                put(195,"8C7F");
                put(196,"8D0B");
                put(197,"8CF7");
                put(198,"8CB3");
                put(199,"8D4F");
                put(200,"8E5B");
                put(201,"8FA7");
                put(202,"8FE3");
                put(203,"8E1F");
                put(204,"8F6B");
                put(205,"8E97");
                put(206,"8ED3");
                put(207,"8F2F");
                put(208,"88FB");
                put(209,"8907");
                put(210,"8943");
                put(211,"88BF");
                put(212,"89CB");
                put(213,"8837");
                put(214,"8873");
                put(215,"898F");
        }
    };
    
    public static String getDigitalValues(String rawValues, String initialValues, int adjustment,
            ArrayList<Long> sensorMap) {
        // takes data from getSensorValues and converts them to true/false (0,1) based on initial
        // value of track sensors recorded during instalation.
        // get sensor values returns reading in form of <<sensorNumber>>:<<value>>;
        adjustment = adjustment == -1 ? SysVars.DEFAULT_THRESHOLD_ADJUSTMENT : adjustment;
        String rVal = "";
        if (rawValues == null || rawValues.equals("")) {
            return COMM_FAILURE_MESSAGE;
        }

        String[] rawValArray = rawValues.substring(0, rawValues.length() - 1).split(";");
        String[] initialValArray = initialValues.substring(0, initialValues.length() - 1).split(";");

        for (int i = 0; i < rawValArray.length; i++) {
            int rawValue = Integer.parseInt(rawValArray[i].split(":")[1]);
            int initialValue = Integer.parseInt(initialValArray[i].split(":")[1]);
            int threshold = initialValue + adjustment;
            rVal += (rawValue > threshold ? "1" : "0");
        }
        System.out.println("************** rVal = " + rVal + " *********************");
        return getInterLeavedSensorValues(rVal, sensorMap);
    }

    public static String getInterLeavedRawSensorValues(String rawSensorVals, ArrayList<Long> sensorMap) {
        String rVal = "";
        String[] rawSensorValArray = rawSensorVals.split(";");
        for (int i = 0; i < sensorMap.size(); i++) {
            long index = rawSensorValArray.length - sensorMap.get(i);
            try {
                rVal = rawSensorValArray[(int) index] + ";" + rVal;
            } catch (ArrayIndexOutOfBoundsException e) {
                return "0";
            }
        }

        return rVal;
    }

    public static String getInterLeavedSensorValues(String sensorvals, ArrayList<Long> sensorInterLeaf) {
        //returns a sensor value string with selected sensors based on 
        // (product size):(space between sensors) ratio

        // if product size depth < 1st sensor spacing, we cannot return meaningfull results.
        if (sensorInterLeaf.get(0) == 0) {
            return SysVars.GENERAL_ERROR_MESSAGE;
        }

        String rVal = "";

        // loop through sensorInterLeaf list
        for (int i = 0; i < sensorInterLeaf.size(); i++) {
            long index = sensorvals.length() - sensorInterLeaf.get(i);
            try {
                rVal = sensorvals.charAt((int) index) + rVal;
            } catch (Exception e) {
                rVal = "";
                break;
            }
        }

        return rVal;
    }
    
    public static boolean setTrackIndicatorLight(String ipAddress,int port, int rs485ID,int brightnessSetting, int timeOut){
        boolean successfulCompletion = false;
        //checksum = startingSensor ^ endingSensor
        String checkSum = encodeDec((brightnessSetting), 16, 2).toUpperCase();
        String stringToSend = "1B7300" + encodeDec(rs485ID, 16, 2) + "0100"
                + encodeDec(brightnessSetting,16,2) 
                + checkSum;
        System.out.println("String sent to activate track light on facing " + rs485ID + " = " + stringToSend);
        // we "read" the socket to turn the indicator light on or off
        // readSocket() returns string indicating whether operation was successful
        readSocket(ipAddress, port, stringToSend, timeOut);
        
        return successfulCompletion;
    }
    
    public static String getRFIDScannerSector(String ipAddress,int port, int RFIDScannerAddress, int timeOut){

        String scannerAddr = encodeDec(RFIDScannerAddress,16,2).toUpperCase();
        String opCode = "03";
        String startingSector = "00";
        String endingSector  = "2B";
        String checkSum = RFID_CRC16Table.get(RFIDScannerAddress);
        String stringToSend = scannerAddr + opCode + startingSector + endingSector + checkSum;
        String socketOutput = readSocket(ipAddress, port, stringToSend, timeOut);

        return  socketOutput;
       
    }

    public static String getSensorValues(String ipAddress, int port, int rs485ID,
            int numberOfSensors, int startingSensor, int timeOut, boolean reverseOrder) {
        //this returns raw data from sensors that will later be digitized into O/1 On/Off
        //rVal = (sensorNumber + ":" + sensorData + ";")+ (sensorNumber + ":" + sensorData + ";"

        String rVal = "";
        int endingSensor = startingSensor + (numberOfSensors - 1);

        //checksum = startingSensor ^ endingSensor
        String startingSensorString = encodeDec(startingSensor, 16, 2) + "00";

        String endingSensorString = encodeDec(endingSensor, 16, 2) + "00";

        //checksum = startingSensor ^ endingSensor
        String checkSum = generateCheckSumForSensorRead(startingSensor, endingSensor);

        String stringToSend = "1B0600" + encodeDec(rs485ID, 16, 2) + "0400"
                + startingSensorString
                + endingSensorString
                + checkSum;

        System.out.println("");
        System.out.println("Requesting Data From IPAddress:" + ipAddress
                + " PORT:" + port + " ADDRESS:" + rs485ID
                + " REQUEST SIGNAL:" + stringToSend);
        System.out.println("");

        String socketOutput = readSocket(ipAddress, port, stringToSend, timeOut);

        int startingByte = 12;
        int byteLength = 2;
        int dataLength = 8;

        for (int i = startingByte; i < socketOutput.length() - dataLength; i += dataLength) {
            int sensorNumber
                    = decodeBaseN(socketOutput.substring(i, i + byteLength), 16)
                    + decodeBaseN(socketOutput.substring(i + byteLength, i + byteLength + 2), 16) * 256;
            int dataByte = i + dataLength / 2;
            int sensorData
                    = decodeBaseN(socketOutput.substring(dataByte, dataByte + byteLength), 16)
                    + decodeBaseN(socketOutput.substring(dataByte + byteLength, dataByte + byteLength + 2), 16) * 256;
            if (reverseOrder) {
                rVal = (sensorNumber + ":" + sensorData + ";") + rVal;
            } else {
                rVal += (sensorNumber + ":" + sensorData + ";");
            }

        }
        //////

        return rVal;
    }

    private static void waitForNextTime(Integer interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    d, shelfIPAddress,
                                                                                        shelfPort,
                                                                                        nSensors,
                                                                                        startingSensor,
     */
    
    
    
    public static String getSensorReadingsWithMultipleHits(JSONObject f,
            String shelfIPAddress,
            Integer port,
            Integer nSensors,
            Integer startingSensor,
            Integer numberOfHits,
            Integer interval) throws JSONException {

        //JSONObject f will always have facingID;
        JSONObject d = new JSONObject(f.toString());
        d.put("IPAddress", shelfIPAddress);
        d.put("port", port);
        d.put("numberOfSensors", nSensors);
        d.put("startingSensor", startingSensor);

        return getSensorReadingsWithMultipleHits(d, numberOfHits, interval);

    }
    


    public static String getSensorReadingsWithMultipleHits(JSONObject f, Integer numberOfHits, Integer interval)
            throws JSONException {
        // f is a record from activatedFacings view
        String sv = "";
        //System.out.println("f.getInt('facingID')=" + f.getInt("facingID"));
        String IPAddress = f.getString("IPAddress");
        int port = f.getInt("port");
        String facingAddress = f.getString("RS485Address");
        int numberOfSensors = f.getInt("numberOfSensors");
        int startingSensor = f.getInt("startingSensor");
        boolean reverseOrder = f.getInt("reverseOrder") > 0;
        // boolean reverseOrder = reverseOrderInt > 0;
        for (int i = 0; i < numberOfHits; i++) {
            sv = getSensorValues(IPAddress,
                    port, decodeBaseN(facingAddress, 10),
                    numberOfSensors, startingSensor, 3000, reverseOrder);  //change last param from true to be fienld in activatedfacings

            if (!sv.equalsIgnoreCase("")) {
                //System.out.println("For facingID " + f.getInt("facingID") + " hit " + (i + 1) + " = " + sv);
                waitForNextTime(interval);
            } else {
                return sv;
            }
        }

        return sv;
    }

    private static String generateCheckSumForSensorRead(int startingSensor, int endingSensor) {
        return encodeDec((startingSensor ^ endingSensor), 16, 2).toUpperCase();
    }

    public static String readSocket(String ipAddress, int port, String hexString, int timeOut) {
        // returns output from socket as hexstring. Depending on the request, the hexstring 
        // will contain data in a given format.
        // establish a connection 
        Socket socket = new Socket();
        DataInputStream input = null;
        DataOutputStream out = null;

        DataInputStream dIn = null;
        try {
            socket.connect(new InetSocketAddress(ipAddress, port), timeOut);
            socket.setSoTimeout(timeOut);
            // takes input from terminal 
            //input  = new DataInputStream(System.in); 
            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());

        } catch (UnknownHostException u) {
            return "NOHOST";
        } catch (IOException i) {
            return ("IOERROR");
        }

        // string to read message from input 
        String line = "";

        // keep reading until "Over" is input 
        try {

            //out.write(hexStringToByteArray("1B06000304000100080009"));
            out.write(hexStringToByteArray(hexString));
            //int length = dIn.readInt();                    // read length of incoming message
            //if (length > 0) {
            String outString = "";
            byte[] message = new byte[100];
            do {
                int x = dIn.read(); // read the message
                try {
                    outString += encodeDec(x, 16, 2);
                } catch (Exception e) {
                    return "IOERROR";
                }
            } while (dIn.available() > 0);

            //close the connecton
            try {
                //input.close(); 
                out.close();
                socket.close();
            } catch (IOException i) {
                System.out.println(i);
            }
            return outString;
            // }
        } catch (IOException i) {
            return "IOERROR";
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isDisarrayed(String mappedSensorState,
            String unmappedSensorState,
            JSONObject track,
            double productDepth) {

        // disarray detection by one or several methods
        boolean disarrayed = false;

        int lastExpectedSensor0Index = getLastExpectedSensor(mappedSensorState,
                track,
                productDepth) - 1;

        int firstUnexpectedSensor0Index = lastExpectedSensor0Index + 1;

        disarrayed = unmappedSensorState.charAt(lastExpectedSensor0Index) == '1'
                && unmappedSensorState.charAt(firstUnexpectedSensor0Index - 1) == '0';

        return disarrayed;
    }

    public static int getLastExpectedSensor(String mappedSensorState,
            JSONObject track,
            double productDepth) {

        int nItemsOnTrack = mappedSensorState.split("1").length - 1;
        double lastEdgeDistance = nItemsOnTrack * productDepth;
        return getSensorAt(lastEdgeDistance, track);
    }

    public static int getSensorAt(double lastEdgeDistance, JSONObject track) {
        return 1;
    }

    public static HashSet<Integer> getGapsInFacing(String sensorState) {

        HashSet<Integer> rVal = new HashSet<>();
        String s = sensorState.split("-")[0];
        //boolean lastWasOff = true;
        for (int i = s.length() - 1; i >= 0; i--) {
            char curChar = s.charAt(i);
            //if (lastWasOff && curChar == '0' && i > 0) {
            //if (curChar == '0' && i <= 0) {
            if (curChar == '0' && i > 0) {
                boolean isGap = false;
                for (int j = i - 1; j >= 0; j--) {
                    if (s.charAt(j) == '1') {
                        isGap = true;
                        break;
                    }
                }
                if (isGap) {
                    rVal.add(i);
                }
            }
            //lastWasOff = curChar == '0';
        }
        return rVal;
    }

    public static double getPercentOfFacingFilled(String sensorState) {
        // if there are 2 or more consecutive "off" sensors, then each sensor after the
        // 1s in the series will be subtracted from the total
        String s = sensorState.split("-")[0];
        double offCount = 0;
        boolean lastWasOff = true;
        for (int i = s.length() - 1; i >= 0; i--) {
            char curChar = s.charAt(i);
            //offCount += (lastWasOff && curChar == '0' ? 1 : 0 );
            offCount += (curChar == '0' ? 1 : 0);
            lastWasOff = curChar == '0';
        }
        return (s.length() - offCount) / s.length();
    }

    public static ArrayList<Long> getSensorMap(double trackDepth, double frontGap,int numberOfSensors, 
            double s1Dist, double stdSpaceBetweenSensors, double pDiam) {
        //given a product diameter and a track with n sensors, select sensors that fall
        //approximately where the middle of the product would be.
        //////////////
        /*boolean doActivate = showConfirmation("ACTIVATION",
                "TO ACTIVATE:\n\n"
                + "1. MOTHERBOARD (SHELF) MUST BE POWERED UP AND CONNECTED TO STORE LAN,WLAN or VLAN \n"
                + "2. THIS APP MUST BE CONNECTED TO THE STORE NETWORK AND THE INTERNET.\n"
                + "3. FOR CORRECT CALIBRATION, TRACK MUST BE CLEAR OF ALL ITEMS\n"
                + "           **** IMPORTANT *****\n"
                + "MAKE SURE TRACK IS CLEAR OF ALL ITEMS BEFORE CONTINUING",
                "Close Box Or Hit 'Cancel' to cancel");*/
        //////////////
        System.out.println("in getsensormap 1");
        double adjustedTrackDepth = trackDepth - frontGap;
        double adjustedS1Dist = s1Dist - frontGap;
        ArrayList<Long> sensorMatrix = new ArrayList<>();

        double RADIUS = pDiam / 2.0;
        double reqSensorDist = RADIUS; // initialize 1st distance to look for, radius of product base

        double ADJ_FOR_S1 = (adjustedS1Dist - stdSpaceBetweenSensors) / stdSpaceBetweenSensors;
        System.out.println("in getsensormap 2");
        double rawCurrSensor = reqSensorDist / stdSpaceBetweenSensors;
        rawCurrSensor = (rawCurrSensor <= 1 ? 1.0 : (rawCurrSensor - ADJ_FOR_S1));
        long currSensor = Math.round(rawCurrSensor);
        //int highestSensor = numberOfSensors + 2;
        int highestSensor = numberOfSensors;
        System.out.println("highestSensor=" + highestSensor);
        System.out.println("");
        while (currSensor <= highestSensor) {
            if (reqSensorDist + RADIUS > adjustedTrackDepth) {
                break;
            }
            
            //sensorMatrix.add((currSensor + 2));
            sensorMatrix.add((currSensor));
            reqSensorDist += pDiam;
            rawCurrSensor = (reqSensorDist / stdSpaceBetweenSensors);
            rawCurrSensor -= ADJ_FOR_S1;
            currSensor = Math.round(rawCurrSensor);
        }
        return sensorMatrix;
    }
    
    public static String getPredefinedSensorReadings(int startingSensor,int numberOfSensors,int presetSensorValue){
        String rVal="";
        for (int i = startingSensor; i < startingSensor + numberOfSensors;i++){
            String str = i + ":" + presetSensorValue + ";";
            rVal+=str;
        }
        
            
        return rVal;
    }

}
