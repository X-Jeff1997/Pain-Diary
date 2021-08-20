package com.example.fit5046_a3.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.fit5046_a3.Login;
import com.example.fit5046_a3.Main;
import com.example.fit5046_a3.weather.Weather;
import com.example.fit5046_a3.databinding.HomeFragmentBinding;
import com.example.fit5046_a3.weather.weatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home extends Fragment {
    public static String BaseUrl = "https://api.openweathermap.org/data/2.5/";
    public static String AppId = "8684c63e23cc32deb6f6b919f85ffadc";

    private TextView mTemp, mHumi, mPres;
    public String latitude, longitude;
    LocationManager mLoctionManager;
    LocationListener mLocationListener;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.example.fit5046_a3.databinding.HomeFragmentBinding addBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        mTemp = addBinding.temp;
        mHumi = addBinding.humidity;
        mPres = addBinding.pressure;

        // check location access permission
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            mLoctionManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extra) {

                }
                @Override
                public void onProviderEnabled(String provider) {
                }
            };
            mLoctionManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, mLocationListener);
        }

        //initiate retrofit
        Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
        Weather service = retrofit.create(Weather.class);
        //retrieve current weather data from the API
        Call<weatherResponse> call = service.getCurrentWeatherData(latitude,longitude,AppId);
        call.enqueue(new Callback<weatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<weatherResponse> call, @NonNull Response<weatherResponse> response) {

                // get weather information
                weatherResponse weatherR = response.body();
                assert weatherR != null;
                Main main = weatherR.getMain();
                String temp = "Temperature: " + String.valueOf(Math.rint(main.getTemp()-273.5)) +"°C";
                String humi = "Humidity: " + String.valueOf(main.getHumi());
                String pres = "Pressure: " + String.valueOf(main.getPres());
                mTemp.setText(temp);
                mHumi.setText(humi);
                mPres.setText(pres);

                String[] weather = {String.valueOf(Math.rint(main.getTemp()-273.5)),String.valueOf(main.getHumi()),String.valueOf(main.getPres())};
                SharedPreferences sharedPref= requireActivity().
                        getSharedPreferences("name", Context.MODE_PRIVATE);
                SharedPreferences.Editor spEditor = sharedPref.edit();
                spEditor.putString("temp", weather[0]);
                spEditor.putString("humi",weather[1]);
                spEditor.putString("pres",weather[2]);
                spEditor.apply();
            }
            @Override
            public void onFailure(@NonNull Call<weatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        addBinding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                startActivity(new Intent(getContext(), Login.class));
            }
        });
        return view;
    }

    

}