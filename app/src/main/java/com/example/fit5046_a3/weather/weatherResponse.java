package com.example.fit5046_a3.weather;

import com.example.fit5046_a3.Main;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class weatherResponse {
    @SerializedName("main")
    public Main main;

    public Main getMain(){
        return main;
    }

    public void setMain(Main main){
        this.main = main;
    }
}


