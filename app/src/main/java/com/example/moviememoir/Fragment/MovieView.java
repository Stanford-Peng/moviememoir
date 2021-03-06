package com.example.moviememoir.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviememoir.R;
import com.example.moviememoir.entity.Movie;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.example.moviememoir.viewmodel.MovieViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MovieView extends Fragment {
    private String link;
    private NetworkConnection networkConnection = null;
    private MovieViewModel movieViewModel;
    private Boolean addWatchButton;
    private ProgressDialog mProgressDialog;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    public MovieView(String link) {
        this.link=link;
        this.addWatchButton=true;
    }

    public MovieView(String link, Boolean addWatchButton) {
        this.link=link;
        this.addWatchButton = addWatchButton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_view_fragment, container, false);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
//        TextView linkView = view.findViewById(R.id.link);
//        linkView.setText(link);
        Log.i("link",link);

        networkConnection = new NetworkConnection();
        GetDetails getDetails = new GetDetails();
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait patiently");
        getDetails.execute(link);


        final TextView name = view.findViewById(R.id.name);
        final TextView date = view.findViewById(R.id.date);
//        TextView genre = view.findViewById(R.id.genre);
//        TextView plot = view.findViewById(R.id.plot);
        final ImageView image = view.findViewById(R.id.image);
//        TextView countries = view.findViewById(R.id.country);
//        TextView director = view.findViewById(R.id.director);
//        TextView actors = view.findViewById(R.id.cast);
//        RatingBar rating = view.findViewById(R.id.simpleRatingBar);

        final Button addWatch = view.findViewById(R.id.addList);
        Button addMemoir = view.findViewById(R.id.addMemoir);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movieViewModel.initializeVars(getActivity().getApplication());
//        final TextView test = view.findViewById(R.id.test);
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);
        final String username = shared.getString("username", null);
        if(!addWatchButton){
            addWatch.setEnabled(false);
        }else {
            addWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String col1 = name.getText().toString();
                    String col2 = date.getText().toString();
                    if (movieViewModel.findByNameReleaseDateUser(col1, col2, username) == null) {
                        Date curDate = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = formatter.format(curDate);
                        Movie movie = new Movie(link, col1, col2, date, username);
                        movieViewModel.insert(movie);
                        Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        addWatch.setEnabled(false);
                        //container.removeView(addWatch);
                    } else {
                        Toast.makeText(getActivity(), "It has been added before", Toast.LENGTH_SHORT).show();
                        addWatch.setEnabled(false);
                    }
                }
            });
        }

        //add Memoir
        addMemoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = ((BitmapDrawable)image.getDrawable()).getBitmap();
                String mName = name.getText().toString();
                String mDate = date.getText().toString();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment addMemoir = new AddMemoir(bmp,mName,mDate);
                fragmentTransaction.replace(R.id.content_frame, addMemoir).addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        callbackManager = CallbackManager.Factory.create();
        final ShareButton share = view.findViewById(R.id.share);

        share.setFragment(this);
        share.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("FaceBook:","OKAY");
                Toast.makeText(getActivity(), "You have successfully shared this movie", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "You have cancelled the sharing", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "There is some error during sharing", Toast.LENGTH_LONG).show();

            }
        });
        ShareLinkContent linkContent = new ShareLinkContent.Builder().setQuote("My Movie").
                setContentUrl(Uri.parse("https://www.imdb.com/title/" + link)).
                setShareHashtag(new ShareHashtag.Builder().setHashtag("#Movie").
                        build()).
                build();
        share.setShareContent(linkContent);

        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private class GetDetails extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute() {

            mProgressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... strings) {


//            ArrayList<String> genres = new ArrayList<String>();
            JsonElement jsonElement = new JsonParser().parse(networkConnection.moreMovie(strings[0],""));
            JsonObject jobject = jsonElement.getAsJsonObject();
            String name = jobject.getAsJsonPrimitive("original_title").getAsString();
            String date = jobject.getAsJsonPrimitive("release_date").getAsString();
            String genre = "";
            String countries ="";
            String plot="";
            if(jobject.has("genres")&&!jobject.get("genres").isJsonNull()) {
                for (JsonElement e : jobject.getAsJsonArray("genres")) {
                    genre = genre + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + "/";
                }
                ;
            }
            genre = genre.substring(0, genre.length() - 1);
            if(jobject.has("production_countries")&&!jobject.get("production_countries").isJsonNull()) {
                for (JsonElement e : jobject.getAsJsonArray("production_countries")) {
                    countries = countries + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + "/";
                }
                ;
            }
            countries = countries.substring(0, countries.length() - 1);
            if(jobject.has("overview")&&!jobject.get("overview").isJsonNull()) {
                 plot = jobject.getAsJsonPrimitive("overview").getAsString();
            }

            String image="https://image.tmdb.org/t/p/w500";
            if(jobject.has("backdrop_path")&&!jobject.get("backdrop_path").isJsonNull()&&jobject.getAsJsonPrimitive("backdrop_path")!= null) {
                image = "https://image.tmdb.org/t/p/w500" + jobject.getAsJsonPrimitive("backdrop_path").getAsString();
            }

            JsonObject cast = new JsonParser().parse(networkConnection.moreMovie(strings[0],"/credits")).getAsJsonObject();
            String director="";
            String actors="";
            if(cast.has("cast")&&!cast.get("cast").isJsonNull()) {
                for (JsonElement e : cast.getAsJsonArray("cast")) {
                    actors = actors + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + " : " + e.getAsJsonObject().getAsJsonPrimitive("character").getAsString() + "\n";
                }
            }
            if(cast.has("crew")&&!cast.get("crew").isJsonNull()) {
                for (JsonElement e : cast.getAsJsonArray("crew")) {

                    if (e.getAsJsonObject().getAsJsonPrimitive("job").getAsString().equals("Director")) {
                        director = director + e.getAsJsonObject().getAsJsonPrimitive("name").getAsString() + "/";
                    }

                }
            }
            director = director.substring(0,director.length()-1);
            String rating = "0";
            if(jobject.has("vote_average")&&!jobject.get("vote_average").isJsonNull()) {
                rating = jobject.getAsJsonPrimitive("vote_average").getAsString();
            }
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
            Picasso.get().load(result[4]).placeholder(R.mipmap.ic_launcher).into(image);
            countries.setText(result[5]);
            director.setText(result[6]);
            actors.setText(result[7]);
            rating.setRating(Float.valueOf(result[8])/2);
            mProgressDialog.dismiss();
        }

    }

}
//shareDialog = new ShareDialog(getActivity());

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//
//                    @Override
//                    public void onSuccess(Sharer.Result result) {
//                        Toast.makeText(getActivity(), "You have successfully shared this movie", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(getActivity(), "You have cancelled the sharing", Toast.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Toast.makeText(getActivity(), "There is some error during sharing", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//                getActivity().setResult(RESULT_OK);
//                ShareLinkContent linkContent = new ShareLinkContent.Builder().setQuote("My Movie").
//                        setContentUrl(Uri.parse("https://www.imdb.com/title/" + link)).
//                        setShareHashtag(new ShareHashtag.Builder().setHashtag("#Movie").
//                                build()).
//                        build();
//                if (ShareDialog.canShow(ShareLinkContent.class)) {
//                    shareDialog.show(linkContent);
//                }
//
//                share.setShareContent(linkContent);
//            }
//        });
//