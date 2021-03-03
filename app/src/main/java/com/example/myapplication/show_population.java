package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.softmoore.android.graphlib.*;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.sin;

public class show_population extends AppCompatActivity {
    String countryCode;
    TextView textViewGraph;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public boolean internetIsConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_population);

        this.internetIsConnected();

        textViewGraph = findViewById(R.id.textViewGraph);

        // get data

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            countryCode = extras.getString("getCountryCode");
        }
        else
        {
            Log.i("Get country code: ", "error");
        }
        // download
        Log.i("Get country code: ", countryCode);

        textViewGraph.setText(extras.getString("getCountryName"));

        // check internet connection

        if(this.internetIsConnected()) {
            try{
            new populationDataDownload().execute("http://api.worldbank.org/v2/country/" + countryCode + "/indicator/sp.pop.totl?format=json");}
            catch (Exception e){
                e.printStackTrace();
                startActivity(new Intent(this, demography.class));
                finish();
            }
        }
        else {
            Toast.makeText(this, " No internet connection ", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    public class populationDataDownload extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        List<Double> valDates = new ArrayList<>();
        List<Double> dateDates = new ArrayList<>();

        @Override
        protected String doInBackground(String... urls) {
            String result= "";
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responsecode = connection.getResponseCode();
                if(responsecode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                }else{
                    Scanner scanner = new Scanner(url.openStream());
                    while (scanner.hasNext()){
                        result+=scanner.nextLine();
                    }
                    Log.i("Result doInBackground: ", result);
                    scanner.close();
                }
                return result;

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(show_population.this, "Error ", Toast.LENGTH_SHORT).show();
                finish();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(show_population.this, "Please wait...", "Load data", true);
            progressDialog.setCancelable(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.i("Result: ", result);
                JSONArray jsonArray = new JSONArray(result);
                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                for(int i = 1; jsonArray1.length()> i; i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    String value = jsonObject.getString("value");
                    String date = jsonObject.getString("date");

                    this.valDates.add(Double.parseDouble(value));
                    this.dateDates.add(Double.parseDouble(date));

                }


                if(dateDates.size() > 0){

                    DataPoint[] values = new DataPoint[valDates.size()];
                    int arraySize = dateDates.size() - 1;
                    Log.i("array  ", String.valueOf(arraySize));
                    int j = 0;
                     for(int i = arraySize; i >= 0 ;i--){

                         DataPoint v = new DataPoint(dateDates.get(i), valDates.get(i));
                         values[j] = v;
                         j++;
                         Log.i("DataPoint  ", String.valueOf(dateDates.get(i) + " " + valDates.get(i)) + " Wartość i : " + i);
                     }

                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
                    graph.addSeries(series);

                    super.onPostExecute(result);}
                else{
                    Toast.makeText(show_population.this, "No data ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(show_population.this, " No data ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), main_menu.class));
                finish();
            }
            progressDialog.cancel();
            
        }

    }
}