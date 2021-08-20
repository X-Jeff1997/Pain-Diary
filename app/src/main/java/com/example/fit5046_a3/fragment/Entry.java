package com.example.fit5046_a3.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

import com.example.fit5046_a3.alarm.AlarmReceiver;
import com.example.fit5046_a3.R;
import com.example.fit5046_a3.WorkManager;
import com.example.fit5046_a3.databinding.EntryFragmentBinding;

import com.example.fit5046_a3.entity.Customer;
import com.example.fit5046_a3.viewmodel.CustomerViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.fit5046_a3.LocalDateConvert.getDate;


public class Entry extends Fragment {
    private EntryFragmentBinding addBinding;
    private CustomerViewModel customerViewModel;
    public Spinner painLocation;
    public SeekBar painLevel;
    public SmileRating smileR;
    public TextView painL, mTimer;
    int tHour, tMinute;
    FirebaseAuth fAuth;


    public Entry() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBinding = EntryFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();

//        set timer
        mTimer = addBinding.timer;
        mTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int HourOfDay, int minute) {
                                tHour = HourOfDay;
                                tMinute = minute;

                                String time = HourOfDay + ":" + minute;

                                SimpleDateFormat f24h = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = f24h.parse(time);
                                    SimpleDateFormat f12h = new SimpleDateFormat("hh:mm aa");
                                    mTimer.setText(f12h.format(date));

                                    startAlarm(getCalender(tHour,tMinute));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour, tMinute);
//                timePickerDialog.setButton(-1, "Set Alarm", (dialog, which) -> startAlarm(getCalender(tHour, tMinute)));
                timePickerDialog.show();
            }
        });


//        pain level slider
        painLevel = addBinding.painBar;
        painL = addBinding.painLevelDisplay;
        painLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                painL.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        pain location spinner
        painLocation = addBinding.spinnerPainLocation;
        String[] painL = {"back", "neck", "head", "knees", "hips", "abdomen", "elbows", "shoulders", "shins", "jaw", "facial"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(painL));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.style_layout, arrayList);
        painLocation.setAdapter(arrayAdapter);

//        smile rating
        smileR = (SmileRating) addBinding.smileRating;
        smileR.setNameForSmile(BaseRating.TERRIBLE, "very low");
        smileR.setNameForSmile(BaseRating.BAD, "low");
        smileR.setNameForSmile(BaseRating.OKAY, "average");
        smileR.setNameForSmile(BaseRating.GOOD, "good");
        smileR.setNameForSmile(BaseRating.GREAT, "very good");

//      check customer
        customerViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(CustomerViewModel.class);


        fAuth = FirebaseAuth.getInstance();
        String email = fAuth.getCurrentUser().getEmail();
        String date = getDate();

        addBinding.saveButton.setEnabled(true);
        addBinding.editButton.setEnabled(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<Customer> customerCompletableFuture =
                    customerViewModel.findByIDFuture(email, date);
            try {  Customer customer = customerCompletableFuture.get();
                if (customer!=null) {
                    addBinding.saveButton.setEnabled(false);
                    addBinding.editButton.setEnabled(true);

                    addBinding.painBar.setEnabled(false);
                    addBinding.painBar.setProgress(Integer.parseInt(customer.painLevel));

                    addBinding.spinnerPainLocation.setEnabled(false);
                    addBinding.spinnerPainLocation.setSelection(customer.painLocation.indexOf(customer.painLocation));

                    switch (customer.painRating){
                        case "very low":
                            smileR.setSelectedSmile(BaseRating.TERRIBLE);
                            break;
                        case "low":
                            smileR.setSelectedSmile(BaseRating.BAD);
                            break;
                        case "average":
                            smileR.setSelectedSmile(BaseRating.OKAY);
                            break;
                        case "good":
                            smileR.setSelectedSmile(BaseRating.GOOD);
                            break;
                        case "very good":
                            smileR.setSelectedSmile(BaseRating.GREAT);
                            break;
                    }
                    smileR.setEnabled(false);



                    addBinding.setGoal.setEnabled(false);
                    addBinding.setGoal.setText(customer.goalStep);

                    addBinding.totalStep.setEnabled(false);
                    addBinding.totalStep.setText(customer.totalStep);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        addBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String goalS = addBinding.setGoal.getText().toString();
                    String totalS = addBinding.totalStep.getText().toString();
                    String painLe = String.valueOf(painLevel.getProgress());
                    String painLo = painLocation.getSelectedItem().toString();
                    String painR = smileR.getSmileName(smileR.getSelectedSmile());
                    SharedPreferences sharedPref= requireActivity().
                            getSharedPreferences("name", Context.MODE_PRIVATE);
                    String temp = sharedPref.getString("temp","");
                    String humi = sharedPref.getString("humi","");
                    String pres = sharedPref.getString("pres","");

                    if (goalS.isEmpty()) {
                        Toast.makeText(getContext(), "Goal cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    if (totalS.isEmpty()) {
                        Toast.makeText(getContext(), "please enter the total steps", Toast.LENGTH_SHORT).show();

                    }
                    if (painLo.isEmpty()) {
                        Toast.makeText(getContext(), "please select a pain level", Toast.LENGTH_SHORT).show();

                    }
                    if (painR == null) {
                        Toast.makeText(getContext(), "please rate the pain", Toast.LENGTH_SHORT).show();

                    }
                    if (!goalS.isEmpty() && !totalS.isEmpty() && !painLo.isEmpty() && painR != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            CompletableFuture<Customer> customerCompletableFuture =
                                    customerViewModel.findByIDFuture(email, date);
                            Customer customer = customerCompletableFuture.get();
                            if (customer != null) {
                                customer.painLevel = painLe;
                                customer.painLocation = painLo;
                                customer.painRating = painR;
                                customer.totalStep = totalS;
                                customer.goalStep =goalS;
                                customer.temperature = temp;
                                customer.humidity = humi;
                                customer.pressure = pres;

                                customerViewModel.update(customer);
                                Toast.makeText(getContext(), "Edit saved", Toast.LENGTH_SHORT).show();

                            } else {
                                Customer c = new Customer(email, date, painLe, painLo, painR, totalS,goalS, temp, humi, pres);
                                customerViewModel.insert(c);

                                Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();

                            }
                            addBinding.painBar.setEnabled(false);
                            addBinding.spinnerPainLocation.setEnabled(false);
                            addBinding.smileRating.setEnabled(false);
                            addBinding.setGoal.setEnabled(false);
                            addBinding.totalStep.setEnabled(false);
                            addBinding.saveButton.setEnabled(false);
                            addBinding.editButton.setEnabled(true);

                            //sent record to firebase
                            Data data = new Data.Builder()
                                    .putString("Email",email)
                                    .putString("Date",date)
                                    .putString("Pain Level",painLe)
                                    .putString("Pain Location",painLo)
                                    .putString("Pain Rate",painR)
                                    .putString("Total Step", totalS)
                                    .putString("Goal Step",goalS)
                                    .putString("Temperature",temp)
                                    .putString("Humidity", humi)
                                    .putString("Pressure",pres).build();

//
                            long currentTime = System.currentTimeMillis();
// calculate the timestamp for next dat 9AM
                            android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();

                            ((android.icu.util.Calendar) calendar).set(android.icu.util.Calendar.HOUR_OF_DAY, 10);
                            ((android.icu.util.Calendar) calendar).set(android.icu.util.Calendar.MINUTE, 0);
                            ((android.icu.util.Calendar) calendar).set(android.icu.util.Calendar.SECOND, 0);
// next day
                            calendar.add(android.icu.util.Calendar.DAY_OF_MONTH, 1);

                            long tomorrowTime = calendar.getTimeInMillis();
                            long timeDiffBetweenNowAndTomorrow = tomorrowTime - currentTime;
// set work manager
                            Constraints constraints = new Constraints.Builder()
                                    .setRequiresBatteryNotLow(true)
                                    .build();

                            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(
                                    WorkManager.class)
                                    .setInputData(data)
                                    .setInitialDelay(timeDiffBetweenNowAndTomorrow, TimeUnit.MILLISECONDS)
                                    .setConstraints(constraints)
                                    .build();

                            androidx.work.WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);
                        }
                    }
                }catch (Exception e) {
                    e.getStackTrace();
            }
            }
        });

        addBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBinding.painBar.setEnabled(true);
                addBinding.spinnerPainLocation.setEnabled(true);
                addBinding.smileRating.setEnabled(true);
                addBinding.setGoal.setEnabled(true);
                addBinding.totalStep.setEnabled(true);
                addBinding.saveButton.setEnabled(true);
                addBinding.editButton.setEnabled(false);
            }
        });
        return view;
    }

//    public void timePickerDialog()

    public Calendar getCalender(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public void startAlarm(Calendar calendar) {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 101, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getContext(), "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
}
