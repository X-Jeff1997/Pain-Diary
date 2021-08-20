package com.example.fit5046_a3.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.example.fit5046_a3.LocalDateConvert.getDate;


@Entity(primaryKeys = {"u_email","p_date"},tableName = "pain_diary")
public class Customer {
    @ColumnInfo(name = "u_email")
    @NonNull
    public String email;
    @ColumnInfo(name = "p_date")
    @NonNull
    public String date;
    @ColumnInfo(name = "pain_level")
    @NonNull
    public String painLevel;
    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;
    @ColumnInfo(name = "pain_rating")
    @NonNull
    public String painRating;
    @ColumnInfo(name = "total_step")
    @NonNull
    public String totalStep;

    public String goalStep;
    public String temperature;
    public String humidity;
    public String pressure;


    public Customer(@NonNull String email,@NonNull String date, @NonNull String painLevel, @NonNull String painLocation, @NonNull String painRating,
            @NonNull String totalStep, String goalStep, String temperature, String humidity, String pressure) {
        this.email = email;
        this.date = date;
        this.painLevel = painLevel;
        this.painLocation =painLocation;
        this.painRating = painRating;
        this.totalStep = totalStep;
        this.goalStep =goalStep;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public String getCustomer(){
        return email;
    }
}

