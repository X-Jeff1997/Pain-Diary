package com.example.fit5046_a3;


import android.os.Build;

import java.time.LocalDate;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;
import org.jetbrains.annotations.Nullable;

public class LocalDateConvert {

    private LocalDateConvert() {
        throw new AssertionError();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static LocalDate fromLong(@Nullable Long epoch) {
        return epoch == null ? null : LocalDate.ofEpochDay(epoch);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static Long localDateToEpoch(@Nullable LocalDate localDate) {
        return localDate == null ? null : localDate.toEpochDay();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDate(){
        return fromLong(localDateToEpoch(LocalDate.now())).toString();
    }
}

