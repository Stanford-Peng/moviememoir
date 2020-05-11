package com.example.moviememoir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moviememoir.networkconnection.NetworkConnection;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    NetworkConnection networkConnection=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signup = findViewById(R.id.signupBtn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, signup.class);
                startActivity(intent);
            }
        });

        networkConnection = new NetworkConnection();
        Button signin = findViewById(R.id.loginBtn);
        final EditText uname = findViewById(R.id.editText1);
        final EditText pwd = findViewById(R.id.editText2);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=uname.getText().toString();
                String password = getMd5(pwd.getText().toString());
                Log.i("profile",username+" "+password);
                Login login = new Login();
                login.execute(username,password);

            }
        });
    }

    private class Login extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String message="";
            if(networkConnection.login(strings[0],strings[1])){
                message="Welcome";
                //to do
            }else{
                message="Failed";
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
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
