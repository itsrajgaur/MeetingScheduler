package com.example.comanymeetingscheduler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.comanymeetingscheduler.Model.MeetingDetailsPojo;
import com.example.comanymeetingscheduler.Retrofit.ApiInterface;
import com.example.comanymeetingscheduler.Retrofit.RetrofitClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddMeeting extends AppCompatActivity {

    @BindView(R.id.lay_back)
    LinearLayout lay_back;
    @BindView(R.id.edtStartTime)
    EditText edtStartTime;
    @BindView(R.id.edtEndTime)
    EditText edtEndTime;
    @BindView(R.id.edtDescription) EditText edtDescription;
    static final int DILOG_ID_MeetingDate = 0;
    int year_x, month_x, day_x;
    DateFormat dateFormatter;
    String strMeetingDate="",strStartTime="",strEndTime="";
    @BindView(R.id.edtMeetingDate)
    EditText edtMeetingDate;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    Date dStartTime,dEndTime;
    private List<MeetingDetailsPojo> detailsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        dateFormatter = new SimpleDateFormat("HH:mm");


        edtMeetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(DILOG_ID_MeetingDate);
            }
        });

        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMeeting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edtStartTime.setText(selectedHour + ":" + selectedMinute);
                        strStartTime =  selectedHour + ":" + selectedMinute;
                        dStartTime = convertStrToDate(strStartTime);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }

        });

        edtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMeeting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edtEndTime.setText(selectedHour + ":" + selectedMinute);
                        strEndTime = selectedHour + ":" + selectedMinute;
                        dEndTime = convertStrToDate(strEndTime);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }

        });
        lay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddMeeting.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtMeetingDate.getText().toString().length()==0)
                {
                    Toast.makeText(AddMeeting.this,"Please select Meeting Date",Toast.LENGTH_LONG).show();
                    return;
                }else if(edtStartTime.getText().toString().length()==0)
                {
                    Toast.makeText(AddMeeting.this,"Please select Start Time",Toast.LENGTH_LONG).show();
                    return;

                }else if(edtEndTime.getText().toString().length()==0)
                {
                    Toast.makeText(AddMeeting.this,"Please select End Time",Toast.LENGTH_LONG).show();
                    return;

                }else if (edtDescription.getText().toString().length()==0)
                {
                    Toast.makeText(AddMeeting.this,"Please enter description",Toast.LENGTH_LONG).show();
                    return;
                }else if (dEndTime.before(dStartTime))
                {
                    Toast.makeText(AddMeeting.this,"Start time should be less than End Time",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("Meeting time",strMeetingDate);
                checkAvailableSlot(strMeetingDate);
            }
        });

    }


    public void checkAvailableSlot(String date)
    {
        final ProgressDialog progressDialog = new ProgressDialog(AddMeeting.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait.."); // set message
        progressDialog.show(); // show progress dialog
        Retrofit retrofit = RetrofitClient.getRetrofitClient(this);
        ApiInterface getMeetings = retrofit.create(ApiInterface.class);

        getMeetings.getList_of_scheduels(date).enqueue(new Callback<List<MeetingDetailsPojo>>() {
            @Override
            public void onResponse(Call<List<MeetingDetailsPojo>> call, Response<List<MeetingDetailsPojo>> response) {

                List<MeetingDetailsPojo> respo = response.body();
                // Log.d("Response",response.body().get(0).getDescription());
                //Toast.makeText(MainActivity.this,response.body().toString(),Toast.LENGTH_SHORT).show();
                MeetingDetailsPojo detailsPojo;
                detailsList.clear();
                for (MeetingDetailsPojo meetingDetailsPojo:response.body()) {

                    detailsPojo = new MeetingDetailsPojo();
                    detailsPojo.setStartTime(meetingDetailsPojo.getStartTime());
                    detailsPojo.setEndTime(meetingDetailsPojo.getEndTime());
                    detailsPojo.setDescription(meetingDetailsPojo.getDescription());
                    detailsList.add(detailsPojo);
                }

                Collections.sort(
                        detailsList,
                        new Comparator<MeetingDetailsPojo>()
                        {
                            public int compare(MeetingDetailsPojo lhs, MeetingDetailsPojo rhs)
                            {
                                return lhs.getStartTimeinDate().compareTo(rhs.getStartTimeinDate());
                            }
                        }
                );

                boolean slotAvailable = false;
                MeetingDetailsPojo meetingDetailsPojo;
                for (int i=0;i<detailsList.size();i++)
                {
                    meetingDetailsPojo = detailsList.get(i);
                    if(i==0) {
                        if (dEndTime.before(meetingDetailsPojo.getStartTimeinDate())) {
                            slotAvailable =true;
                            Toast.makeText(AddMeeting.this, "Slot available", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }else if (i<=detailsList.size()-2 && i!=0)
                    {
                          if (dStartTime.after(detailsList.get(i-1).getEndTimeinDate()) && dEndTime.before(detailsList.get(i).getStartTimeinDate()))
                          {
                              slotAvailable = true;
                              Toast.makeText(AddMeeting.this, "Slot available", Toast.LENGTH_LONG).show();
                              break;
                          }
                    }else if (i == detailsList.size()-1)
                    {

                        if (dStartTime.after(meetingDetailsPojo.getEndTimeinDate()))
                        {
                            slotAvailable = true;
                            Toast.makeText(AddMeeting.this, "Slot available", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }

                if (!slotAvailable)
                {
                    Toast.makeText(AddMeeting.this, "Slot Not available", Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<MeetingDetailsPojo>> call, Throwable t) {

                Toast.makeText(AddMeeting.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DILOG_ID_MeetingDate) {
            // return new DatePickerDialog(this, , ,,).getDatePicker().setMaxDate();
            DatePickerDialog dialog = new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            Calendar calendar = Calendar.getInstance();//get the current day
            return dialog;
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfyear, int dayOfmonth) {


            edtMeetingDate.setText(String.format("%d-%s-%d", dayOfmonth, monthOfyear + 1, year));
            strMeetingDate = edtMeetingDate.getText().toString();

        }
    };


    public Date convertStrToDate(String date)
    {
        Date convertedDate = new Date();
        try {
             convertedDate = dateFormatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

}
