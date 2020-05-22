package com.example.moviememoir.Fragment;

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

public class MovieMemoir extends Fragment {
    NetworkConnection networkConnection = null;
    private RecyclerView memoirRecycleView;
    private RecyclerView.LayoutManager layoutManager;
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
        getMemoirs.execute(pId);

        return view;
    }

    private class GetMemoirs extends AsyncTask<String,Void, ArrayList<CachedMemoir>>{
        @Override
        protected ArrayList<CachedMemoir> doInBackground(String... strings) {
            String res = networkConnection.getMemoirs(strings[0]);
            ArrayList<CachedMemoir> cachedMemoirs = new ArrayList<>();
            if(!res.contains("HTTP Status 404 - Not Found")){
                JsonElement jsonElement = new JsonParser().parse(res);
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    String memoirName = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MName").getAsString();
                    String memoirComment = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MComment").getAsString();
                    String releaseDate = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MReleaseDate").getAsString().substring(0,10);
                    String watchDate = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MWatchDate").getAsString().substring(0,10);
                    String watchTime = jsonArray.get(i).getAsJsonObject().getAsJsonPrimitive("MWatchTime").getAsString().substring(11,16);;
                    String imageLink ="";
                    String publicRating="";
                    String mdbLink="";
                    String moreAboutMovie = networkConnection.searchMovie(memoirName);
                    if (!(moreAboutMovie.contains("\"status_code\":") ||moreAboutMovie.isEmpty()||moreAboutMovie==null) ){
                        JsonElement jsonElementMore = new JsonParser().parse(moreAboutMovie);
                        JsonObject jObject = jsonElementMore.getAsJsonObject();
                        JsonArray items = jObject.getAsJsonArray("items");
                        Log.i("items",items.toString());
                        JsonElement e0= items.get(0).getAsJsonObject().getAsJsonPrimitive("title"); //bug
                        JsonObject e1 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap");
                        JsonArray e2 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("cse_thumbnail");
                        JsonArray e3 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags");
                        JsonElement e4 = items.get(0).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags").get(0).getAsJsonObject().getAsJsonPrimitive("pageid");
                        if(!(e1==null||e2==null||e3==null||e4==null)) {
                            //String movie = e0.getAsString();
                            imageLink = e2.get(0).getAsJsonObject().getAsJsonPrimitive("src").getAsString();
                            mdbLink = e4.getAsString();
                            //Log.i("Array", e1.getAsJsonArray("aggregaterating").get(0).toString());
                            //publicRating = e1.getAsJsonArray("aggregaterating").get(0).getAsJsonObject().getAsJsonPrimitive("ratingvalue").getAsString();

                        }
                        //String[] parsed= new String[]{};
                        if(!mdbLink.startsWith("tt")){
                            String mdbMovie = networkConnection.moreMovie(mdbLink,"");
                            JsonElement forPubRating = new JsonParser().parse(mdbMovie);
                            JsonObject forPubRatingAsJsonObject = forPubRating.getAsJsonObject();
                            publicRating=forPubRatingAsJsonObject.getAsJsonPrimitive("vote_average").getAsString();
                        }

                    }

                    CachedMemoir cachedMemoir  = new CachedMemoir(memoirName,memoirComment,releaseDate, watchDate,watchTime,imageLink,publicRating,mdbLink);
                    cachedMemoirs.add(cachedMemoir);


                }


            }
            return cachedMemoirs;

        }

        @Override
        protected void onPostExecute(ArrayList<CachedMemoir> cachedMemoirs) {
            MemoirRecycleAdaptor memoirRecycleAdaptor = new MemoirRecycleAdaptor(cachedMemoirs,getActivity());//getActivity()
            memoirRecycleView.setAdapter(memoirRecycleAdaptor);
            layoutManager = new LinearLayoutManager(getContext());
            memoirRecycleView.setLayoutManager(layoutManager);

            //super.onPostExecute(s);
        }
    }



}

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