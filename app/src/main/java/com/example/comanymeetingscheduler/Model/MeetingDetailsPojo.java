package com.example.comanymeetingscheduler.Model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MeetingDetailsPojo /*implements Comparable<MeetingDetailsPojo>*/  {

    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("description")
    private String description;
    @SerializedName("participants")
    private List<String> participants = null;

    public String getStartTime() {

        return startTime;
    }

    public Date getStartTimeinDate()
    {
        try {
            return new SimpleDateFormat("hh:mm").parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Date getEndTimeinDate()
    {
        try {
            return new SimpleDateFormat("hh:mm").parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }



    /*@Override
    public int compareTo(MeetingDetailsPojo meetingDetailsPojo) {
        if(meetingDetailsPojo == null) {
            return 1;
        } else if(startTime == null) {
            return 0;
        } else {
            return startTime.compareTo(meetingDetailsPojo.startTime);
        }
    }*/
}
