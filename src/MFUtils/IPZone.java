/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MFUtils;

public class IPZone implements Comparable<IPZone>{
    
    private String zone;
    private String lowestSubIP;
    private String highestSubIP;

    public IPZone(String zone, String lowestSubIP,String highestSubIP){
        this.zone = zone;
        this.lowestSubIP = lowestSubIP;
        this.highestSubIP = highestSubIP;
    }
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getLowestSubIP() {
        return lowestSubIP;
    }

    public void setLowestSubIP(String lowestSubIP) {
        this.lowestSubIP = lowestSubIP;
    }

    public String getHighestSubIP() {
        return highestSubIP;
    }

    public void setHighestSubIP(String highestSubIP) {
        this.highestSubIP = highestSubIP;
    }

    @Override
    public int compareTo(IPZone o) {
        return Integer.parseInt(this.getZone()) - 
                Integer.parseInt(o.getZone());
     }

   
    
    
}
