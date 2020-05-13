package com.example.moviememoir.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;

public class SearchFragment extends Fragment {
    NetworkConnection networkConnection = null;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        final EditText inputMovie = view.findViewById(R.id.editText);

        Button searchBtn = view.findViewById(R.id.search);
        networkConnection = new NetworkConnection();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputMovie.getText().toString().trim().toLowerCase();
                Log.i("input",input);
                if(!input.isEmpty()){
                    SearchTask searchTask = new SearchTask();
                    searchTask.execute(new String[]{input});
                }
                else{
                    Toast.makeText(getActivity(), "Please input a movie first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private class SearchTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            //Log.i("strings",strings[0]);

            return networkConnection.searchMovie(strings[0]).toString();
        }

        @Override
        protected void onPostExecute(String s) {
            TextView results = getView().findViewById(R.id.results);
            results.setText(s);
        }
    }

}
