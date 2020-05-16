package com.example.moviememoir.networkconnection;


import android.os.Environment;
import android.util.Log;

import com.example.moviememoir.entity.Credentials;
import com.example.moviememoir.entity.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Date;
//import java.text.SimpleDateFormat;
//import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

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

            RequestBody body = RequestBody.create(credentailsJson, MediaType.get("application/json; charset=utf-8"));
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
        Log.i("url",methodPath);
        return ja;
    }

    public String searchMovie(String movie){
        String resStr = null;
        movie = movie.replace(" ", "+");
        Request.Builder builder = new Request.Builder();
        builder.url("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" +
                SEARCH_ID_cx + "&q=movie+" + movie +"&num=3");
//        Log.i("url", "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" +
//                SEARCH_ID_cx + "&q=" + movie +"&num=3");
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            resStr = response.body().string();
            //jo = new JSONObject(resStr);
            Log.i("Search Response: ", resStr);
            //writeFile("search.txt",resStr);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resStr;
    }

    public static void writeFile(String filepath, String content)
    {
        //FileWriter fw = null;
        try
        {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File file = new File(path, "/" + filepath);
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.close();

        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }

}
