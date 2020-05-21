package com.example.moviememoir.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AddMemoir extends Fragment {
    private Bitmap mImage;
    private String mName;
    private String mDate;

    NetworkConnection networkConnection=null;

    public AddMemoir() {
    }

    public AddMemoir(Bitmap mImage, String mName, String mDate) {
        this.mImage = mImage;
        this.mName = mName;
        this.mDate = mDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_memoir_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView movieName = view.findViewById(R.id.movieName);
        TextView releaseDate = view.findViewById(R.id.releaseDate);
        imageView.setImageBitmap(mImage);
        movieName.setText(mName);
        releaseDate.setText(mDate);

        final TextView watchDate = view.findViewById(R.id.watchDate);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                watchDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        watchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TextView watchTime = view.findViewById(R.id.watchTime);

        watchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        watchTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        networkConnection= new NetworkConnection();
        GetCinemas getCinemas = new GetCinemas();
        getCinemas.execute();

        Button addCinema = view.findViewById(R.id.addCinema);
        addCinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_cinema,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setView(promptsView);

//                final EditText userInput = (EditText) promptsView
//                        .findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
//                                        result.setText(userInput.getText());
                                        EditText cName = promptsView.findViewById(R.id.cName);
                                        EditText cPostcode = promptsView.findViewById(R.id.cPostcode);
                                        String cinemaName = cName.getText().toString().trim();
                                        String cinemaPostcode = cPostcode.getText().toString().trim();
                                        //Pattern pattern = Pattern.compile("\d\d\d\d");
                                        Log.i("cName",cinemaName);
                                        if(cinemaName.isEmpty()||cinemaPostcode.length()!=4||!cinemaPostcode.matches("\\d\\d\\d\\d")){
                                            Toast.makeText(getActivity(), "Please Enter Valid Cinema", Toast.LENGTH_SHORT).show();

                                        } else{
                                            AddCinema addCinema = new AddCinema();
                                            addCinema.execute(cinemaName,cinemaPostcode);

                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        });




        return view;
    }

    private class GetCinemas extends AsyncTask<Void,Void,ArrayList<String>>{


        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> cinemas = new ArrayList<>();
            String response = networkConnection.getCinema();
            if (!(response.contains("HTTP Status 404 - Not Found") ||response.isEmpty()||response==null) ) {
                JsonElement jsonElement = new JsonParser().parse(response);
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement e : jsonArray){
                    JsonObject jsonObject = e.getAsJsonObject();
                    String cinema = jsonObject.getAsJsonPrimitive("CName").getAsString() + " " + jsonObject.getAsJsonPrimitive("CPostcode").getAsString();
                    cinemas.add(cinema);
                }
            }

            return cinemas;
        }

        @Override
        protected void onPostExecute(ArrayList<String> cinemas) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cinemas);
            Spinner spinner = getView().findViewById(R.id.cinema);
            spinner.setAdapter(arrayAdapter);


        }
    }

    private class AddCinema extends AsyncTask<String, Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String res = networkConnection.addCinema(strings[0],strings[1]);
            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            if(res.isEmpty()){
                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
                GetCinemas getCinemas = new GetCinemas();
                getCinemas.execute();
            }else{
                Toast.makeText(getActivity(), "Failed, Sorry.", Toast.LENGTH_SHORT).show();
            }

        }
    }




}
