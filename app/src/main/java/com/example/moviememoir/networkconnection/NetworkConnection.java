package com.example.moviememoir.networkconnection;


import android.util.Log;

import com.example.moviememoir.entity.Cinema;
import com.example.moviememoir.entity.Credentials;
import com.example.moviememoir.entity.Memoir;
import com.example.moviememoir.entity.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetworkConnection {
    private static final String BASE_URL = "http://10.0.2.2:8080/MovieMemoir_2/webresources/";
    private OkHttpClient client = null;
    private static final String API_KEY = "AIzaSyDKQbMuKHqeNK6-ZbpmNh8ARp9KNpZ7Uec";
    private static final String SEARCH_ID_cx = "012837362384735422434:vfrbrinp1ir";
    private static final String MDB_KEY = "fc4e821eb5f2109cd2d6e5e662acb7da";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    //private String results;

    public NetworkConnection() {
        client = new OkHttpClient();
    }
    //String[] details = {fName, lName, gdr, Dob, addr, sta, pcode, mail ,pword};

    public String addUser(String[] details){
        //Log.i("i: ","here");
        Date curDate = new Date(System.currentTimeMillis());
//        Date dob=null;
//        Person person = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//'T'HH:mm:ssZ
        Log.i("Date: ", curDate.toString());
        String strResponse = "";
        final String methodPath = "moviememoir.credentials/";
        try {
            Date dob = formatter.parse(details[3]);
            //curDate = formatter.parse(curDate.toString());
            Person person = new Person(details[0],details[1],details[2].toLowerCase().charAt(0),dob,details[4],details[5],details[6]);
            Credentials credentials = new Credentials(details[7],details[8], curDate);
            credentials.setPId(person);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
            String credentailsJson = gson.toJson(credentials);

            RequestBody body = RequestBody.create(credentailsJson, JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + methodPath).post(body)
                    .build();
            Log.i("Request: ",credentailsJson);
            try {
                Response response = client.newCall(request).execute();
                strResponse = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ParseException e){
            Log.i("parseDob: ",e.toString());

        }
        return strResponse;
    }

    public String[] login(String uname, String pwd){
        String[] results = {"",""};
        final String methodPath = "moviememoir.credentials/login/"+uname+"/"+pwd;
        Request.Builder builder = new Request.Builder();
        //boolean valid=false;
        builder.url(BASE_URL+methodPath);
        Request request = builder.build();
        //Gson gson = new Gson();
        try{
            Response response= client.newCall(request).execute();
            String resStr = response.body().string();
            Log.i("logic",resStr);
            JSONArray ja = new JSONArray(resStr);
            try{

            if(ja.getJSONObject(0).getString("valid").equals("true"))
            {
                results[0] = ja.getJSONObject(0).getString("first name");
                results[1]= ja.getJSONObject(0).getString("pid");
                Log.i("valid", results[0]);
            }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

//        if (results.equals("true")){
//            valid = true;
//        }


        return results;
    }

    public JSONArray getTopMovies(String pid){
        JSONArray ja=null;
        final String methodPath = "moviememoir.memoir/findRecentTopScoredMovies/"+pid;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL+methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            ja = new JSONArray(resStr);
        }catch(Exception e){
            e.printStackTrace();
        }
        //Log.i("url",methodPath);
        return ja;
    }

    public String searchMovie(String movie){
        String resStr = null;
        movie = movie.replace(" ", "+");
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" +
                SEARCH_ID_cx + "&q=movie+" + movie);
        Log.i("url", "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" +
               SEARCH_ID_cx + "&q=" + movie +"&num=3");
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            //jo = new JSONObject(resStr);
            //Log.i("Search Response: ", resStr);
            //writeFile("search.txt",resStr);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resStr;
    }

    public String moreMovie(String link, String path){
        String resStr = null;
        Request.Builder builder = new Request.Builder();
        builder.url("https://api.themoviedb.org/3/movie/" + link + path +"?api_key=" + MDB_KEY + "&external_source=imdb_id");
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            //jo = new JSONObject(resStr);
            //Log.i("Search Response: ", resStr);
            //writeFile("search.txt",resStr);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resStr;

    }

    public String getMovieByName (String name, String[] params, String[] values){
        String resStr = null;
        name = name.replace(" ", "+");
        Request.Builder builder = new Request.Builder();
        String query_parameter = "";
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        builder.url("https://api.themoviedb.org/3/search/movie/" + "?api_key=" + MDB_KEY + "&query=" + name + query_parameter);
        Log.i("getMovieByName:","https://api.themoviedb.org/3/search/movie/" + "?api_key=" + MDB_KEY + "&query=" + name + query_parameter);
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            //jo = new JSONObject(resStr);
            //Log.i("getMovieByName: ", resStr);
        }catch(Exception e){
            e.printStackTrace();
        }
        return resStr;

    }

    public String getCinema(){
        String resStr = null;
        Request.Builder builder = new Request.Builder();
        final String path = "moviememoir.cinema";
        builder.url( BASE_URL + path);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            //Log.i("Cinema Response: ", resStr);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resStr;

    }

    public String addCinema(String cName, String cPostcode){
        String resStr = null;
        Request.Builder builder = new Request.Builder();
        final String path = "moviememoir.cinema";
        //builder.url( BASE_URL + path);
        Cinema cinema = new Cinema(cName,cPostcode);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String cinemaJson = gson.toJson(cinema);
        //Log.i("Cinema:",cinemaJson);
        RequestBody body = RequestBody.create(cinemaJson, JSON);
        Request request = builder.url(BASE_URL + path).post(body).build();
        try{
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
        } catch(Exception e){
            e.printStackTrace();
        }

        return resStr;

    }

    public String addMemoir(String mName, float mRatingScore, Date mReleaseDate, Date mWatchDate, Date mWatchTime, String mComment ,int pId, int cId){
        String resStr = "";
        Request.Builder builder = new Request.Builder();
        final String path = "moviememoir.memoir";
        //builder.url( BASE_URL + path);
        //Cinema cinema = new Cinema(cName,cPostcode);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        Person person = new Person(pId);
        Cinema cinema = new Cinema(cId);
        Memoir memoir = new Memoir(mName, mRatingScore, mReleaseDate, mWatchDate, mWatchTime, mComment,person, cinema);
        String memoirJson= gson.toJson(memoir);
        Log.i("Memoir Json:",memoirJson);
        RequestBody body = RequestBody.create(memoirJson, JSON);
        Request request = builder.url(BASE_URL + path).post(body).build();
        try{
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
        } catch(Exception e){
            e.printStackTrace();
        }

        return resStr;

    }

    public String getMemoirs(String pId){
        String resStr = "";
        Request.Builder builder = new Request.Builder();
        final String path = "moviememoir.memoir/findByPId/" + pId;
        builder.url( BASE_URL + path);
        Log.i("memoir request url",BASE_URL + path);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            Log.i("Memoir Response: ", resStr);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resStr;

    }



}

//    public String getCast(String link){
//        String resStr = null;
//        Request.Builder builder = new Request.Builder();
//        builder.url("https://api.themoviedb.org/3/movie/" + link + "/credits" +"?api_key=" + MDB_KEY + "&external_source=imdb_id");
//                Request request = builder.build();
//        try {
//            Response response = client.newCall(request).execute();
//            resStr = response.body().string();
//            //jo = new JSONObject(resStr);
//            Log.i("Search Response: ", resStr);
//            //writeFile("search.txt",resStr);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        return resStr;
//
//    }




