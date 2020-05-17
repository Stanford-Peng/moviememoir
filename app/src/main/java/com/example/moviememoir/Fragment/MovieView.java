package com.example.moviememoir.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieView extends Fragment {
    private String link;
    private NetworkConnection networkConnection = null;
    public MovieView(String link) {
        this.link=link;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_view_fragment, container, false);
//        TextView linkView = view.findViewById(R.id.link);
//        linkView.setText(link);
        Log.i("link",link);

        networkConnection = new NetworkConnection();
        GetDetails getDetails = new GetDetails();
        getDetails.execute(link);

        Button addWatch = view.findViewById(R.id.addList);
        Button addMemoir = view.findViewById(R.id.addMemoir);
        addWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        addMemoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    private class GetDetails extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... strings) {


//            ArrayList<String> genres = new ArrayList<String>();
            JsonElement jsonElement = new JsonParser().parse(networkConnection.moreMovie(strings[0],""));
            JsonObject jobject = jsonElement.getAsJsonObject();
            String name = jobject.getAsJsonPrimitive("original_title").getAsString();
            String date = jobject.getAsJsonPrimitive("release_date").getAsString();
            String genre = "";
            String countries ="";
            for (JsonElement e :jobject.getAsJsonArray("genres")){
                genre = genre + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString()+"/";
            };
            genre = genre.substring(0, genre.length() - 1);

            for (JsonElement e :jobject.getAsJsonArray("production_countries")){
                countries = countries + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString()+"/";
            };
            countries = countries.substring(0, countries.length() - 1);

            String plot = jobject.getAsJsonPrimitive("overview").getAsString();
            String image = "https://image.tmdb.org/t/p/w500" + jobject.getAsJsonPrimitive("backdrop_path").getAsString();

            JsonObject cast = new JsonParser().parse(networkConnection.moreMovie(strings[0],"/credits")).getAsJsonObject();
            String director="";
            String actors="";
            for (JsonElement e : cast.getAsJsonArray("cast"))
            {
                    actors = actors + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + " : " +e.getAsJsonObject().getAsJsonPrimitive("character").getAsString() + "\n";
            }

            for (JsonElement e : cast.getAsJsonArray("crew")){
                ;
                if(e.getAsJsonObject().getAsJsonPrimitive("job").getAsString().equals("Director")){
                    director = director + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + "/";
                }

            }
            director = director.substring(0,director.length()-1);
            String rating = jobject.getAsJsonPrimitive("vote_average").getAsString();
            String[] result = new String[]{name,date,genre,plot,image,countries,director,actors,rating};
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            TextView name = getView().findViewById(R.id.name);
            TextView date = getView().findViewById(R.id.date);
            TextView genre = getView().findViewById(R.id.genre);
            TextView plot = getView().findViewById(R.id.plot);
            ImageView image = getView().findViewById(R.id.image);
            TextView countries = getView().findViewById(R.id.country);
            TextView director = getView().findViewById(R.id.director);
            TextView actors = getView().findViewById(R.id.cast);
            RatingBar rating = getView().findViewById(R.id.simpleRatingBar);
            rating.setIsIndicator(true);
            name.setText(result[0]);
            date.setText(result[1]);
            genre.setText(result[2]);
            plot.setText(result[3]);
            Picasso.get().load(result[4]).into(image);
            countries.setText(result[5]);
            director.setText(result[6]);
            actors.setText(result[7]);
            rating.setRating(Float.valueOf(result[8])/2);
        }

    }

}
