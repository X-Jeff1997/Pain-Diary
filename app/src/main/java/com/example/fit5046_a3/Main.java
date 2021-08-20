package com.example.fit5046_a3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    @Expose
    private double temp;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("pressure")
    @Expose
    private Integer pressure;

    public double getTemp() {
        return temp;
    }

    public Integer getHumi() {
        return humidity;
    }

    public Integer getPres() {
        return pressure;
    }
}
