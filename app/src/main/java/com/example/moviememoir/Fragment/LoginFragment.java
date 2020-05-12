package com.example.moviememoir.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviememoir.HomeActivity;
import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginFragment extends Fragment {
    NetworkConnection networkConnection=null;
    String username;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        Button signup = view.findViewById(R.id.signupBtn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_container, new SignupFragment(),"signup_fragment").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        networkConnection = new NetworkConnection();
        Button signin = view.findViewById(R.id.loginBtn);
        final EditText uname = view.findViewById(R.id.editText1);
        final EditText pwd = view.findViewById(R.id.editText2);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=uname.getText().toString().trim();
                String password = getMd5(pwd.getText().toString().trim());
                Log.i("profile",username+" "+password);
                if(!(password.isEmpty() || username.isEmpty())) {
                    Login login = new Login();
                    login.execute(username, password);
                }else{
                    Toast.makeText(getActivity(), "No blank input", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }



    private class Login extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String message="Failed Login";
            String firstName = networkConnection.login(strings[0],strings[1]);
            if(!firstName.equals("")){
                message="Welcome";
                //to enter home page
                Intent intent= new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("user",username);
                intent.putExtra("firstName",firstName);
                startActivity(intent);
                getActivity().finish();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
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
