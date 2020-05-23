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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportsFragment extends Fragment {

    NetworkConnection networkConnection = null;
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;

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
            pieData = new PieData(pieDataSet);
            Log.i("pie data:", pieData.toString());

            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setSliceSpace(2f);
            pieDataSet.setValueTextColor(Color.BLUE);
            //pieDataSet.setColor(Color.BLACK);
            pieDataSet.setValueTextSize(12f);
//            pieDataSet.set
            pieChart.setUsePercentValues(true);
            pieChart.setData(pieData);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setEntryLabelTextSize(10f);
            pieChart.setHoleRadius(5f);
            pieChart.getLegend().setEnabled(true);
            pieChart.getDescription().setText("Watch Times per Suburb");
            pieChart.setDrawEntryLabels(true);
            //pieDataSet.setSliceSpace(5f);
            pieChart.animateXY(1000, 1000);
        }
    }

}
