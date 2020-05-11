package com.example.moviememoir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.accounts.NetworkErrorException;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moviememoir.networkconnection.NetworkConnection;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.security.MessageDigest;

public class signup extends AppCompatActivity {
//    String firstName="";
//    String lastName="";
//    Character gender="";
//    date Dob="";
//    String address="";
//    String state="";
//    String postcode="";
//    String password="";
    // datePicker;
    NetworkConnection networkConnection=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        final EditText dob = findViewById(R.id.dob);
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                dob.setText(sdf.format(myCalendar.getTime()));
            }
        };

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(signup.this, dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final EditText firstName= findViewById(R.id.firstName);
        final EditText lastName = findViewById(R.id.lastName);
        final RadioGroup gender = findViewById(R.id.gender);
        //dob found before
        final EditText address = findViewById(R.id.address);
        final Spinner state = findViewById(R.id.state);
        final EditText postcode = findViewById(R.id.postcode);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.signup);
        networkConnection = new NetworkConnection();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName= firstName.getText().toString().trim();
                String lName=lastName.getText().toString().trim();
                String gdr= ((RadioButton)findViewById(gender.getCheckedRadioButtonId())).getText().toString().substring(0,1);
                String Dob=dob.getText().toString().trim();
                String addr=address.getText().toString().trim();
                String sta=state.getSelectedItem().toString().trim();
                String pcode=postcode.getText().toString().trim();
                String mail = email.getText().toString().trim();
                String pword=password.getText().toString().trim();
                if(!(fName.isEmpty()||lName.isEmpty()||gdr.isEmpty()||Dob.isEmpty()||addr.isEmpty()||sta.isEmpty()||pcode.isEmpty()||pword.isEmpty())){
                    pword = getMd5(pword);
                    String[] details = {fName, lName, gdr, Dob, addr, sta, pcode, mail ,pword};
                    AddCredentialsTask addCredentialsTask = new AddCredentialsTask();
                    addCredentialsTask.execute(details);
                } else {
                    Toast.makeText(signup.this, "Please fill up the form first.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private class AddCredentialsTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String message = "You have registered successfully";
            String res = networkConnection.addUser(strings);
            Log.i("Register: ",res);
            if(!res.isEmpty()){
                message="Register Failed, Email has been registered";
            }
            return message;
        }
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(signup.this, s, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);

        }
    }

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }




}


//public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//    Calendar myCalendar = Calendar.getInstance();
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        // Create a new instance of DatePickerDialog and return it
//        return new DatePickerDialog(getActivity(), this, year, month, day);
//    }
//
//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        // Do something with the date chosen by the user
//        myCalendar.set(Calendar.YEAR, year);
//        myCalendar.set(Calendar.MONTH, monthOfYear);
//        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        String myFormat = "MM/dd/yy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        datePicker.setText(sdf.format(myCalendar.getTime()));
//    }
//}
