package com.example.fit5046_a3;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.fit5046_a3.entity.Customer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.fit5046_a3.LocalDateConvert.getDate;

public class WorkManager extends Worker {
    private static final String TAG = "WorkManager";
    DatabaseReference dRecord;
    FirebaseDatabase database;


    public WorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {

        Data inputData = getInputData();
        String email = inputData.getString("email");
        database = FirebaseDatabase.getInstance();
        dRecord =database.getReference();
        dRecord.setValue("User");
        dRecord.setValue(email);
        Log.d(TAG,"Daily record sent");
        return Result.success();
    }
}
