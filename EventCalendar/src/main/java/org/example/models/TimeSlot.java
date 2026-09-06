package org.example.models;

import java.util.Comparator;
import java.util.Date;

public class TimeSlot implements Comparable<TimeSlot> {
    private Date startTime;
    private Date endTime;

    public TimeSlot(Date startTime, Date endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime(){
        return startTime;
    }

    public Date getEndTime(){
        return endTime;
    }

    @Override
    public int compareTo(TimeSlot o){
        return this.startTime.compareTo(o.startTime);
    }

}
