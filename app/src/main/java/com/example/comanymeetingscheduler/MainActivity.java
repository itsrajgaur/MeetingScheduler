package com.example.comanymeetingscheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comanymeetingscheduler.Model.MeetingDetailsPojo;
import com.example.comanymeetingscheduler.Retrofit.ApiInterface;
import com.example.comanymeetingscheduler.Retrofit.RetrofitClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.GridLayout.HORIZONTAL;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lay_prev)
    LinearLayout lay_pre;
    @BindView(R.id.lay_next)
    LinearLayout lay_next;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtDayName) TextView txtDayName;
    @BindView(R.id.btn_schedule_meeting)
    Button btn_schedule_meeting;
    DateFormat dateFormatter;
    private RecyclerView recyclerView;
    private MeetingsAdapter meetingsAdapter;
    private List<MeetingDetailsPojo> detailsList = new ArrayList<>();
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        meetingsAdapter =new MeetingsAdapter(detailsList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingsAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      c = Calendar.getInstance();

        txtDate.setText(dateFormatter.format(c.getTime()));
        txtDayName.setText(getDayName(c.get(Calendar.DAY_OF_WEEK))+",");
        meetingsOnSelectedDate(dateFormatter.format(c.getTime()));


        lay_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c.add(Calendar.DAY_OF_MONTH, 1);
                txtDate.setText(dateFormatter.format(c.getTime()));
                txtDayName.setText(getDayName(c.get(Calendar.DAY_OF_WEEK))+",");
                meetingsOnSelectedDate(dateFormatter.format(c.getTime()));
            }
        });


        lay_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c.add(Calendar.DAY_OF_MONTH, -1);
                txtDate.setText(dateFormatter.format(c.getTime()));
                txtDayName.setText(getDayName(c.get(Calendar.DAY_OF_WEEK))+",");
                meetingsOnSelectedDate(dateFormatter.format(c.getTime()));
            }
        });

        btn_schedule_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddMeeting.class);
                intent.putExtra("DATE",dateFormatter.format(c.getTime()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        /*
            Intent intent = new Intent(MainActivity.this, AddMeeting.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/



    }

    public void meetingsOnSelectedDate(String date)
    {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
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
                    detailsPojo.setParticipants(meetingDetailsPojo.getParticipants());
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
                meetingsAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<MeetingDetailsPojo>> call, Throwable t) {

                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

     public String getDayName(int day)
     {
         String dayName ="";
         switch (day) {
             case 1:
                 dayName = "Sunday";
                 break;
             case 2:
                 dayName = "Monday";
                 break;
             case 3:
                 dayName = "Tuesday";
                 break;
             case 4:
                 dayName = "Wednesday";
                 break;
             case 5:
                 dayName =  "Thursday";
                 break;
             case 6:
                 dayName = "Friday";
                 break;
             case 7:
                 dayName ="Saturday";
                 break;
         }

         return dayName;
     }

}
