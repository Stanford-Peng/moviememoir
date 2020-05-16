package com.example.moviememoir.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    NetworkConnection networkConnection=null;

    public HomeFragment( ) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        TextView welcome=view.findViewById(R.id.welcome);
        String firstName = getArguments().getString("firstName");
        welcome.setText("Welcome! " + firstName);

        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String date = formatter.format(curDate);
        TextView forDate = view.findViewById(R.id.date);
        forDate.setText(date);

        networkConnection = new NetworkConnection();
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);
        String pid = shared.getString("pid",null);
        GetTopMovies getTopMovies = new GetTopMovies();
        getTopMovies.execute(pid);
        return view;
    }

    private class GetTopMovies extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            JSONArray ja = networkConnection.getTopMovies(strings[0]);
            StringBuffer sb = new StringBuffer("Top Rated Movies Watched by You:\n \n");
            if (ja.length()<=5&& ja.length()>0){
            for (int i = 0; i<5; i++){
                try {
                    sb.append(ja.getJSONObject(i).getString("Top Movies")+"      ");
                    sb.append(ja.getJSONObject(i).getString("Released date")+"      ");
                    sb.append("Score: "+ja.getJSONObject(i).getString("rating Score")+"\n");
                    sb.append("\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            TextView tm=getView().findViewById(R.id.topMovies);
            tm.setText(s);
        }
    }

}
