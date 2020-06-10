package com.example.moviememoir.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static com.github.mikephil.charting.animation.Easing.EaseInSine;

public class ReportsFragment extends Fragment {

    NetworkConnection networkConnection = null;
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reports_fragment, container, false);
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);
        final String pId = shared.getString("pid", null);
        networkConnection = new NetworkConnection();
        final Calendar myCalendar = Calendar.getInstance();
        final EditText startDate = view.findViewById(R.id.watchStartDate);
        final EditText endDate = view.findViewById(R.id.watchEndDate);
        final DatePickerDialog.OnDateSetListener dateStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                startDate.setText(sdf.format(myCalendar.getTime()));

            }
        };
        final DatePickerDialog.OnDateSetListener dateEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                endDate.setText(sdf.format(myCalendar.getTime()));

            }
        };
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateStartListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateEndListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setNoDataText("Please pick the date to fill this pie chart");

        Button showPie = view.findViewById(R.id.showPie);
        showPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = startDate.getText().toString().trim();
                String to = endDate.getText().toString().trim();
                if(!(from.isEmpty()||to.isEmpty())){
                    GetPieData getPieData = new GetPieData();
                    getPieData.execute(pId,from,to);}
                else{
                    Toast.makeText(getActivity(), "Please Enter the Date first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final Spinner yearSpinner = view.findViewById(R.id.spinnerYear);

        //ArrayList<Integer> years = new ArrayList<>();
        Calendar forYears = Calendar.getInstance();
        ArrayList<String> years = new ArrayList();
        //{String.valueOf(forYears.get(Calendar.YEAR)),String.valueOf(forYears.get(Calendar.YEAR-1)),String.valueOf(forYears.get(Calendar.YEAR-2)),String.valueOf(forYears.get(Calendar.YEAR-3)),String.valueOf(forYears.get(Calendar.YEAR-4))};
        years.add(String.valueOf(forYears.get(Calendar.YEAR)));
        years.add(String.valueOf(forYears.get(Calendar.YEAR)-1));
        years.add(String.valueOf(forYears.get(Calendar.YEAR)-2));
        years.add(String.valueOf(forYears.get(Calendar.YEAR)-3));
        years.add(String.valueOf(forYears.get(Calendar.YEAR)-4));
        years.add(String.valueOf(forYears.get(Calendar.YEAR)-5));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,years);
        yearSpinner.setAdapter(arrayAdapter);

        //bar chart
        barChart = view.findViewById(R.id.barChart);
        barChart.setNoDataText("Please select the year to fill this bar chart");
        Button showBar = view.findViewById(R.id.showBar);
        showBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedYear = yearSpinner.getSelectedItem().toString();
                GetBarData getBarData = new GetBarData();
                getBarData.execute(pId,selectedYear);
            }
        });





        return view;

    }

    private class GetPieData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String rawData = networkConnection.getForPieChart(strings[0],strings[1],strings[2]);
            Log.i("rawData",rawData);
            return rawData;
        }

        @Override
        protected void onPostExecute(String s) {
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            pieEntries = new ArrayList<PieEntry>();
            for(JsonElement je:jsonArray){
                float times = je.getAsJsonObject().getAsJsonPrimitive("total_number").getAsFloat();
                String postcode = times + " times" + " on " + je.getAsJsonObject().getAsJsonPrimitive("c_postcode").getAsString();
                pieEntries.add(new PieEntry(times,postcode));
            }
            pieDataSet = new PieDataSet(pieEntries,"");
            //Log.i("pie data:", pieData.toString());
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            //pieDataSet.setSliceSpace(2f);
            pieDataSet.setValueTextColor(Color.BLUE);
            //pieDataSet.setColor(Color.BLACK);
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setSliceSpace(5f);

            pieData = new PieData(pieDataSet);
            pieChart.setUsePercentValues(true);
            pieChart.setData(pieData);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setEntryLabelTextSize(10f);
            pieChart.setHoleRadius(5f);
            pieChart.setTransparentCircleRadius(10f);
            pieChart.getLegend().setEnabled(true);
            pieChart.getDescription().setText("Watch Times and Percentage(%) per Suburb");
            pieChart.setDrawEntryLabels(true);
            pieChart.animateXY(1000, 1000);
            //pieChart.animateY(1000, EaseInSine);
        }
    }

    private class GetBarData extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String rawData = networkConnection.getForBarChart(strings[0],strings[1]);
            return rawData;
        }

        @Override
        protected void onPostExecute(String s) {
            JsonElement jsonElement = new JsonParser().parse(s);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            ArrayList<BarEntry> barEntries= new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            //LinkedHashSet<String> labels = new LinkedHashSet<>();
            labels.add("JANUARY");
            labels.add("FEBRUARY");
            labels.add("MARCH");
            labels.add("APRIL");
            labels.add("MAY");
            labels.add("JUNE");
            labels.add("JULY");
            labels.add("AUGUST");
            labels.add("SEPTEMBER");
            labels.add("OCTOBER");
            labels.add("NOVEMBER");
            labels.add("DECEMBER");

            for (int j = 0; j< labels.size();j++) {
            for(int i = 0;i<jsonArray.size();i++){
                float times = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("Watch Times").getAsFloat();
                String month = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("Month").getAsString();
                if(labels.get(j).equals(month)) {
                    barEntries.add(new BarEntry(j, times));
                    //labels.add(month);
                }else{
                    barEntries.add(new BarEntry(j, 0f));
                }
            }
            }
            BarDataSet barDataSet = new BarDataSet(barEntries, "Watch Times");
            //barDataSet.setStackLabels(labels.toArray(new String[labels.size()]));
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            BarData data = new BarData(barDataSet);
            data.setBarWidth(0.5f);

            barChart.setData(data);
            barChart.getLegend().setEnabled(true);
            barChart.getDescription().setText("");//Watch Times Per Month

            //barChart.set
            XAxis xAxis = barChart.getXAxis();
            YAxis yAxis = barChart.getAxisRight();
            YAxis yAxis1 = barChart.getAxisLeft();

            yAxis1.setAxisMinimum(0);
            yAxis.setEnabled(false);
            Log.i("label",labels.toString());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setTextSize(8f);
            xAxis.setDrawAxisLine(false);
            //xAxis.setL
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            barChart.setDoubleTapToZoomEnabled(false);
            xAxis.setGranularity(1f);
            barChart.animateXY(1000,1000);

        }
    }

}
