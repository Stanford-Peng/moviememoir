package com.example.moviememoir.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviememoir.R;
import com.example.moviememoir.adaptor.MemoirRecycleAdaptor;
import com.example.moviememoir.model.CachedMemoir;
import com.example.moviememoir.model.SearchedMovie;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MovieMemoir extends Fragment {
    NetworkConnection networkConnection = null;
    private RecyclerView memoirRecycleView;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog mProgressDialog;
    private ArrayList<CachedMemoir> cachedMemoirs;
    private MemoirRecycleAdaptor memoirRecycleAdaptor;
    private Set<String> genresOption;
    private String sort = "Default";
    private String filter = "Default";
    private  Spinner spinnerSort;
    private  Spinner spinnerFilter;

    //List<CachedMemoir> sortedMemoirs;
    public MovieMemoir() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_memoir_fragment, container, false);
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);
        String pId = shared.getString("pid", null);
        memoirRecycleView = view.findViewById(R.id.memoirRecycleView);
        networkConnection = new NetworkConnection();
        GetMemoirs getMemoirs = new GetMemoirs();
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait patiently");
        layoutManager = new LinearLayoutManager(getContext());
        spinnerSort = view.findViewById(R.id.sort);
        spinnerFilter = view.findViewById(R.id.filter);
        getMemoirs.execute(pId);

        return view;
    }

    private class GetMemoirs extends AsyncTask<String, Void, ArrayList<CachedMemoir>> {
        @Override
        protected void onPreExecute() {

            mProgressDialog.show();
        }

        @Override
        protected ArrayList<CachedMemoir> doInBackground(String... strings) {
            String res = networkConnection.getMemoirs(strings[0]);
            cachedMemoirs = new ArrayList<>();
            if (!res.contains("HTTP Status 404 - Not Found")) {
                JsonElement jsonElement = new JsonParser().parse(res);
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    String memoirName = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MName").getAsString();
                    Log.i("memoirName", memoirName);
                    String rating = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MRatingScore").getAsString();
                    String memoirComment = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MComment").getAsString();
                    String releaseDate = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MReleaseDate").getAsString().substring(0, 10);
                    String watchDate = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MWatchDate").getAsString().substring(0, 10);
                    String watchTime = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MWatchTime").getAsString().substring(11, 16);
                    String imageLink = "https://image.tmdb.org/t/p/w500";
                    String publicRating = "0";
                    String imdbLink = "";
                    String[] genres = null;
                    String moreAboutMovie = networkConnection.getMovieByName(memoirName, new String[]{"primary_release_year"}, new String[]{releaseDate.substring(0, 4)});
                    if (!(moreAboutMovie.contains("\"status_code\":") || moreAboutMovie.isEmpty() || moreAboutMovie == null)) {
                        JsonElement jsonElementMore = new JsonParser().parse(moreAboutMovie);
                        JsonObject jObject = jsonElementMore.getAsJsonObject();
                        JsonArray results = jObject.getAsJsonArray("results");
                        Log.i("results", results.toString());
                        if (results.size() > 0) {
                            JsonObject result = results.get(0).getAsJsonObject();
                            String tmdbId = result.getAsJsonPrimitive("id").getAsString();
                            //String movieViewResult = networkConnection.moreMovie(tmdbId,"");
                            if (!tmdbId.isEmpty()) {
                                String mdbMovie = networkConnection.moreMovie(tmdbId, "");
                                Log.i("tmdbId", tmdbId);

                                JsonElement forMore = new JsonParser().parse(mdbMovie);
                                JsonObject objectForMore = forMore.getAsJsonObject();
                                if (objectForMore.has("vote_average") && !objectForMore.get("vote_average").isJsonNull()) {
                                    publicRating = objectForMore.getAsJsonPrimitive("vote_average").getAsString();
                                }
                                if (objectForMore.has("backdrop_path") && !objectForMore.get("backdrop_path").isJsonNull()) {
                                    imageLink = "https://image.tmdb.org/t/p/w500" + objectForMore.getAsJsonPrimitive("backdrop_path").getAsString();
                                }
                                if (objectForMore.has("imdb_id") && !objectForMore.get("imdb_id").isJsonNull()) {
                                    imdbLink = objectForMore.getAsJsonPrimitive("imdb_id").getAsString();
                                }
                                if (objectForMore.has("genres") && !objectForMore.get("genres").isJsonNull()) {
                                    JsonArray genresArray = objectForMore.getAsJsonArray("genres");
                                    genres = new String[genresArray.size()];
                                    for (int j = 0; j < genresArray.size(); j++) {
                                        genres[j] = genresArray.get(j).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
                                    }
                                } else {
                                    genres = new String[]{""};
                                }
                            } else {
                                genres = new String[]{""};
                            }
                        } else {
                            genres = new String[]{""};
                        }
                    } else {
                        genres = new String[]{""};
                    }
                    CachedMemoir cachedMemoir = new CachedMemoir(memoirName, memoirComment, releaseDate, watchDate, watchTime, imageLink, publicRating, imdbLink, genres, rating);
                    cachedMemoirs.add(cachedMemoir);
                }
            }

            return cachedMemoirs;

        }

        @Override
        protected void onPostExecute(ArrayList<CachedMemoir> results) {
            memoirRecycleAdaptor = new MemoirRecycleAdaptor(results, getActivity(),sort,filter);//getActivity()
            memoirRecycleView.setAdapter(memoirRecycleAdaptor);
            //layoutManager = new LinearLayoutManager(getContext());
            memoirRecycleView.setLayoutManager(layoutManager);
            //collect genres
            genresOption = new LinkedHashSet<>();
            genresOption.add("Default");
            for (int i = 0; i < cachedMemoirs.size(); i++) {
                genresOption.addAll(Arrays.asList(cachedMemoirs.get(i).getGenres()));
            }
            Log.i("genres:", genresOption.toString());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<String>(genresOption));
            final Spinner spinnerFilter = getView().findViewById(R.id.filter);
            spinnerFilter.setAdapter(arrayAdapter);
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sort = spinnerSort.getItemAtPosition(position).toString();
                    memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs, getActivity(), sort, filter);
                    memoirRecycleView.setAdapter(memoirRecycleAdaptor);
                    memoirRecycleView.setLayoutManager(layoutManager);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filter = spinnerFilter.getItemAtPosition(position).toString();

                        memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs, getActivity(), sort, filter);
                        memoirRecycleView.setAdapter(memoirRecycleAdaptor);
                        memoirRecycleView.setLayoutManager(layoutManager);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            mProgressDialog.dismiss();
        }
    }


}
//                    Log.i("filter",filter);
//                 if (filter.equals("Default")) {
//                     sortedMemoirs = cachedMemoirs;
//                 } else {
//                     sortedMemoirs = new ArrayList<CachedMemoir>();
//                     Iterator<CachedMemoir> iter = cachedMemoirs.iterator();
//                     while(iter.hasNext()) {
//                         CachedMemoir cm = iter.next();
//                         Log.i("my genre",Arrays.asList(cm.getGenres()).toString());
//                         if (Arrays.asList(cm.getGenres()).toString().contains(filter)) {
//                             sortedMemoirs.add(cm);
//                         }
//                     }
//                 }
//                 Log.i("sortedMemoirs",sortedMemoirs.toString());
//                if(selected.equals("User Rating")){
//                    memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs,getActivity(),"User Rating","");
//                    memoirRecycleView.setAdapter(memoirRecycleAdaptor);
//                    //layoutManager = new LinearLayoutManager(getContext());
//                    memoirRecycleView.setLayoutManager(layoutManager);
//                }
//                if(selected.equals("Public Rating")){
//                    memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs,getActivity(),"Public Rating","");
//                    memoirRecycleView.setAdapter(memoirRecycleAdaptor);
//                    //layoutManager = new LinearLayoutManager(getContext());
//                    memoirRecycleView.setLayoutManager(layoutManager);
//                }
//                if(selected.equals("Default")){
//                    memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs,getActivity(),"","");
//                    memoirRecycleView.setAdapter(memoirRecycleAdaptor);
//                    //layoutManager = new LinearLayoutManager(getContext());
//                    memoirRecycleView.setLayoutManager(layoutManager);
//                }

//                    imageLink = "https://image.tmdb.org/t/p/w500" + result.getAsJsonPrimitive("backdrop_path").getAsString();
//                    tmdbLink = result.getAsJsonPrimitive("id").getAsString();
//                    publicRating = result.getAsJsonPrimitive("vote_average")
//                    if (!(moreAboutMovie.contains("\"status_code\":") ||moreAboutMovie.isEmpty()||moreAboutMovie==null) ){
//                        JsonElement jsonElementMore = new JsonParser().parse(moreAboutMovie);
//                        JsonObject jObject = jsonElementMore.getAsJsonObject();
//                        JsonArray items = jObject.getAsJsonArray("items");
//                        Log.i("items",items.toString());
//                        JsonElement e0= items.get(0).getAsJsonObject().getAsJsonPrimitive("title"); //bug
//                        JsonObject e1 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap");
//                        JsonArray e2 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("cse_thumbnail");
//                        JsonArray e3 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags");
//                        JsonElement e4 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags").get(0).getAsJsonObject().getAsJsonPrimitive("pageid");
//                        if(!(e1==null||e2==null||e3==null||e4==null)) {
//                            //String movie = e0.getAsString();
//                            imageLink = e2.get(0).getAsJsonObject().getAsJsonPrimitive("src").getAsString();
//                            mdbLink = e4.getAsString();
//                            //Log.i("Array", e1.getAsJsonArray("aggregaterating").get(0).toString());
//                            //publicRating = e1.getAsJsonArray("aggregaterating").get(0).getAsJsonObject().getAsJsonPrimitive("ratingvalue").getAsString();
//
//                        }
//                        //String[] parsed= new String[]{};
//                        if(!mdbLink.startsWith("tt")){
//                            String mdbMovie = networkConnection.moreMovie(mdbLink,"");
//                            JsonElement forPubRating = new JsonParser().parse(mdbMovie);
//                            JsonObject forPubRatingAsJsonObject = forPubRating.getAsJsonObject();
//                            publicRating=forPubRatingAsJsonObject.getAsJsonPrimitive("vote_average").getAsString();
//                        }
//
//                    }

//private class SearchMovie extends AsyncTask<String,Void,String>{
//    @Override
//    protected String doInBackground(String... strings) {
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//    }
//}
//                    String memoirComment ;
//                    String releaseDate ;
//                    String watchDate ;
//                    String watchTime ;
//                    String imageLink;
//                    String publicRating;
//                    String mdbLink;
//                    String mdbMovie = networkConnection.moreMovie(mdbLink,"");
//                    if(mdbMovie.contains("\"status_code\":")){
//                        JsonElement jsonElement = new JsonParser().parse(networkConnection.moreMovie(strings[0],""));
//                        JsonObject jobject = jsonElement.getAsJsonObject();
//                    }
//                                URL url = null;
//                                try {
//                                    url = new URL(image);
//                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                    SearchedMovie searchedMovie = new SearchedMovie(movie, bmp, link);
//                                    results.add(searchedMovie);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }