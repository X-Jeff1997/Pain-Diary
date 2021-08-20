package com.example.fit5046_a3.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fit5046_a3.R;
import com.example.fit5046_a3.databinding.ReportFragmentBinding;
import com.example.fit5046_a3.entity.Customer;
import com.example.fit5046_a3.viewmodel.CustomerViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.fit5046_a3.LocalDateConvert.getDate;

public class Report extends Fragment {
    private ReportFragmentBinding addBinding;
    private CustomerViewModel customerViewModel;
    private int back, neck, head, knees, hips, abdomen, elbows, shoulders, shins, jaw, facial, totalStep, goalStep;
    private PieChart chart1, chart2;
    FirebaseAuth fAuth;
    private String startDateV, endDateV;
    private List<Customer> userData;
    private ArrayList<Customer> period;
    private List<Float> painLevel, temp, humi, pres;
    private LineChart lineChart;
    private XAxis x;
    private YAxis leftY, rightY;
    private Legend l;


    public Report() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        period = new ArrayList<Customer>();
        painLevel = new ArrayList<>();
        temp = new ArrayList<>();
        humi = new ArrayList<>();
        pres = new ArrayList<>();

        customerViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(CustomerViewModel.class);

        addBinding = ReportFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        lineChart = addBinding.lineC;

        TabHost tab = (TabHost) addBinding.tabHost;

        //初始化TabHost容器
        tab.setup();
        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        tab.addTab(tab.newTabSpec("tab1").setIndicator("Pain Location", null).setContent(R.id.tab1));
        tab.addTab(tab.newTabSpec("tab2").setIndicator("Total Step", null).setContent(R.id.tab2));
        tab.addTab(tab.newTabSpec("tab3").setIndicator("Pain & Weather", null).setContent(R.id.tab3));
        chart1 = addBinding.tab1;
        chart2 = addBinding.tab2;

        fAuth = FirebaseAuth.getInstance();
        String email = fAuth.getCurrentUser().getEmail();

        // Chart pain location

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<List<Customer>> customerCompletableFuture =
                    customerViewModel.getData(email);
            try {
                List<Customer> list = customerCompletableFuture.get();
                for (Customer temp : list) {
                    switch (temp.painLocation) {
                        case "back":
                            back += 1;
                            break;
                        case "neck":
                            neck += 1;
                            break;
                        case "head":
                            head += 1;
                            break;
                        case "knees":
                            knees += 1;
                            break;
                        case "hips":
                            hips += 1;
                            break;
                        case "abdomen":
                            abdomen += 1;
                            break;
                        case "elbows":
                            elbows += 1;
                            break;
                        case "shoulders":
                            shoulders += 1;
                            break;
                        case "shins":
                            shins += 1;
                            break;
                        case "jaw":
                            jaw += 1;
                            break;
                        case "facial":
                            facial += 1;
                            break;
                    }
                }

                userData = list;


                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                entries.add(new PieEntry(back, "back"));

                entries.add(new PieEntry(neck, "neck"));

                entries.add(new PieEntry(head, "head"));

                entries.add(new PieEntry(knees, "knees"));
                entries.add(new PieEntry(hips, "hips"));
                entries.add(new PieEntry(abdomen, "abdomen"));
                entries.add(new PieEntry(elbows, "elbows"));
                entries.add(new PieEntry(shoulders, "shoulders"));
                entries.add(new PieEntry(shins, "shins"));
                entries.add(new PieEntry(jaw, "jaw"));
                entries.add(new PieEntry(facial, "facial"));

                setData(chart1, entries, "Pain Location");
                chart1.setDrawHoleEnabled(false);


            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // chart step

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<Customer> customerCompletable =
                    customerViewModel.findByIDFuture(email, getDate());
            try {
                Customer c = customerCompletable.get();
                if (c != null) {
                    totalStep = Integer.parseInt(c.totalStep);
                    goalStep = Integer.parseInt(c.goalStep);
                } else {
                    totalStep = 0;
                    goalStep = 0;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            ArrayList<PieEntry> entries1 = new ArrayList<>();

            entries1.add(new PieEntry(totalStep, "Step taken"));
            entries1.add(new PieEntry((goalStep - totalStep), "Remaining step"));

            setData(chart2, entries1, "Step situation");
            chart2.setDrawHoleEnabled(true);
            chart2.setUsePercentValues(false);
        }

        // Line Chart

        // choose start date
        addBinding.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog start = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String sMonth = String.valueOf(month + 1);
                                String sDay = String.valueOf(dayOfMonth);
                                if ((month + 1) < 10) {
                                    sMonth = "0" + sMonth;
                                }
                                if (dayOfMonth < 10) {
                                    sDay = "0" + sDay;
                                }
                                addBinding.startDate.setText(year + "-" + sMonth + "-" + sDay);
                                startDateV = year + "-" + sMonth + "-" + sDay;
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                start.show();
            }
        });

        // choose end date
        addBinding.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog end = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String eMonth = String.valueOf(month + 1);
                                String eDay = String.valueOf(dayOfMonth);
                                if ((month + 1) < 10) {
                                    eMonth = "0" + eMonth;
                                }
                                if (dayOfMonth < 10) {
                                    eDay = "0" + eDay;
                                }
                                addBinding.endDate.setText(year + "-" + eMonth + "-" + eDay);
                                endDateV = year + "-" + eMonth + "-" + eDay;
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                end.show();
            }
        });



        String[] weather = {"Temperature", "Humidity", "Pressure"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(weather));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.style_layout, arrayList);
        addBinding.weather.setAdapter(arrayAdapter);

        // plot
        addBinding.plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get period data
                for (Customer c:userData) {
                    if (period.size() == 0) {
                        if (c.date.equals(startDateV)) {
                            period.add(c);
                        }
                    }
                    else {
                        if (c.date.equals(endDateV)) {
                            break;
                        } else {
                            period.add(c);
                        }
                    }
                }
                // set list data
                for (Customer p : period) {
                    painLevel.add(Float.parseFloat(p.painLevel));
                    temp.add(Float.parseFloat(p.temperature));
                    humi.add(Float.parseFloat(p.humidity));
                    pres.add(Float.parseFloat(p.pressure));
                }

                lineChartFormat();

                if (addBinding.weather.getSelectedItem().toString().equals("Temperature")) {
                    getLineChart(temp, "temperature", Color.BLACK);
                    addLine(painLevel, "pain level", Color.BLUE);
                }
                if (addBinding.weather.getSelectedItem().toString().equals("Humidity")) {
                    getLineChart(humi, "humidity", Color.BLACK);
                    addLine(painLevel, "pain level", Color.BLUE);
                }
                if (addBinding.weather.getSelectedItem().toString().equals("Pressure")) {
                    getLineChart(pres, "pressure", Color.BLACK);
                    addLine(painLevel, "pain level", Color.BLUE);
                }
            }

        });

        return view;
    }

    private void setData(PieChart chart, ArrayList<PieEntry> entries, String label) {

        chart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false


        chart.getDescription().setEnabled(false);//设置描述

        chart.setExtraOffsets(5, 10, 5, 5);//设置饼状图距离上下左右的偏移量

        chart.setDragDecelerationFrictionCoef(0.95f);

        PieDataSet dataSet = new PieDataSet(entries, label);

        dataSet.setSliceSpace(3f);

        dataSet.setSelectionShift(5f);

//数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)

            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        if (chart.equals(chart1)) {
            data.setValueFormatter(new PercentFormatter());
        }

        data.setValueTextSize(11f);

        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

//设置每个tab的显示位置

        Legend l = chart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        chart.setDrawEntryLabels(true);//设置是否绘制Label

        chart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色

        chart.setEntryLabelTextSize(12f);//设置绘制Label的字体大小

    }

    public void lineChartFormat() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setDragEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.animateY(2500);
        lineChart.animateX(1500);
        x = lineChart.getXAxis();
        leftY = lineChart.getAxisLeft();
        rightY = lineChart.getAxisRight();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setAxisMinimum(0f);
        x.setGranularity(1f);
        leftY.setAxisMinimum(0f);
        rightY.setAxisMinimum(0f);
        l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(12f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    public void setLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setDragEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.animateY(2500);
        lineChart.animateX(1500);
        x = lineChart.getXAxis();
        leftY = lineChart.getAxisLeft();
        rightY = lineChart.getAxisRight();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setAxisMinimum(0f);
        x.setGranularity(1f);
        leftY.setAxisMinimum(0f);
        rightY.setAxisMinimum(0f);
        l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(12f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    public void getLineChart(List<Float> list, String name, int color) {
        List<com.github.mikephil.charting.data.Entry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Float number = list.get(i);
            com.github.mikephil.charting.data.Entry entry = new com.github.mikephil.charting.data.Entry(i, number);
            entries.add(entry);
        }
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        setLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }

    public void addLine(List<Float> list, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Float number = list.get(i);
            com.github.mikephil.charting.data.Entry entry = new com.github.mikephil.charting.data.Entry(i, number);
            entries.add(entry);
        }
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        setLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }


}
