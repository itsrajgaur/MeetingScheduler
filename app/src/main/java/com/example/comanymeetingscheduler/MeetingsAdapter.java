package com.example.comanymeetingscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comanymeetingscheduler.Model.MeetingDetailsPojo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MyViewHolder> {
    private List<MeetingDetailsPojo> meetingsList;
    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
    public MeetingsAdapter(List<MeetingDetailsPojo> meetingsList) {
        this.meetingsList = meetingsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtStartTime, txtEndTime, txtDescription, txtAttendance;

        public MyViewHolder(View view) {
            super(view);
            txtStartTime = (TextView) view.findViewById(R.id.txtMeetingStartTime);
            txtEndTime = (TextView) view.findViewById(R.id.txtMeetingEndTime);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            txtAttendance = (TextView) view.findViewById(R.id.txtAttendance);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_scheduled_meetings, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MeetingDetailsPojo meetingDetailsPojo = meetingsList.get(position);
        holder.txtStartTime.setText(dateFormat.format(meetingDetailsPojo.getStartTimeinDate()));
        holder.txtEndTime.setText(dateFormat.format(meetingDetailsPojo.getStartTimeinDate()));
        holder.txtDescription.setText(meetingDetailsPojo.getDescription());
        String attendacne = "";
        if (meetingDetailsPojo.getParticipants().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : meetingDetailsPojo.getParticipants()) {
                sb.append(s).append(",");
            }
            attendacne = sb.deleteCharAt(sb.length() - 1).toString();
        }

        holder.txtAttendance.setText(attendacne);
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }
}