package com.example.fit5046_a3.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Weather {
        @GET("weather?lat=11&lon=39&appid=8684c63e23cc32deb6f6b919f85ffadc")
        Call<weatherResponse> getCurrentWeatherData(@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String app_id);
    }

