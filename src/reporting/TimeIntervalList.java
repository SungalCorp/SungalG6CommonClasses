/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporting;

import static gen.SysVars.LOCALDATETIME_FORMAT;
import static gen.Utils.dateToString;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Date;

/**
 *
 * @author danrothman
 * 
 * This class creates and returns a list of all times between startTime and endTime at specified 
 * interval.
 */
public class TimeIntervalList {
    private String startTime;
    private String endTime;
    private Integer intervalInSeconds;
    
    
    
    public TimeIntervalList(String startTime,String endTime,Integer intervalInSeconds){
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalInSeconds = intervalInSeconds;
    }
    
    public TimeIntervalList(Date startTime,Date endTime, Integer intervalInSeconds){
        this.startTime = dateToString(startTime,LOCALDATETIME_FORMAT );
        this.endTime = dateToString(endTime,LOCALDATETIME_FORMAT );
        this.intervalInSeconds = intervalInSeconds;
    }
    
    public TimeIntervalList(LocalDateTime startTime,LocalDateTime endTime, Integer intervalInSeconds){
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
        this.intervalInSeconds = intervalInSeconds;
    }
    
    
    public ArrayList<String> getTimeIntervalList(){
        ArrayList<String> timeStops = new ArrayList<String>();
        
        LocalDateTime currTime = LocalDateTime.parse(this.startTime);
         while(currTime.toString().compareTo(this.endTime)<=0){
            timeStops.add(currTime.toString());
            currTime = currTime.plusSeconds(this.intervalInSeconds);
        }
        
        return timeStops;
    }
    
}
