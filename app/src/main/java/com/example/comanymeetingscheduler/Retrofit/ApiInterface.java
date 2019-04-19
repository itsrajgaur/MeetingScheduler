package com.example.comanymeetingscheduler.Retrofit;


import com.example.comanymeetingscheduler.Model.MeetingDetailsPojo;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

 //   @Headers("Content-Type: application/json")
    @GET("schedule")
    Call<List<MeetingDetailsPojo>> getList_of_scheduels(@Query("date") String date);
}