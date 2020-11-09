package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Cache;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class show_population extends AppCompatActivity {
    String countryCode, countryName;
    GraphView graph;
    TextView textViewDate1, textViewDate2, textViewDate3, nameOfCountry;
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_population);
        textViewDate1 = findViewById(R.id.textViewDate1);
        textViewDate2 = findViewById(R.id.textViewDate2);
        textViewDate3 = findViewById(R.id.textViewDate3);
        nameOfCountry = findViewById(R.id.nameOfCountry);

        // get data
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            countryCode = extras.getString("getCountryCode");
            nameOfCountry.setText(extras.getString("getCountryName"));
        }
        else
        {
            Log.i("Get country code: ", "error");
        }
        // download
        Log.i("Get country code: ", countryCode);
        new populationDataDownload().execute("http://api.worldbank.org/v2/country/"+countryCode+"/indicator/sp.pop.totl?format=json");

        graph = (GraphView) findViewById(R.id.graph);

    }
    public class populationDataDownload extends AsyncTask<String, Void, String>{
        String result= "";
        List<Integer> valDates = new ArrayList<>();
        List<Integer> dateDates = new ArrayList<>();
        @Override
        protected String doInBackground(String... urls) {
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
                    scanner.close();
                }
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }
            return null;
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

                    this.valDates.add(Integer.parseInt(value));
                    this.dateDates.add(Integer.parseInt(date));
                }



                if(dateDates.size() > 0){
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                    List<Integer> valDatesConverted = new ArrayList<>();
                    int midleValueofDateListSize;

                    if(dateDates.size()%2 == 0){
                        midleValueofDateListSize = dateDates.size() / 2;
                    }else{
                        midleValueofDateListSize = (dateDates.size() - 1) / 2;
                    }

                    textViewDate3.setText(dateDates.get(dateDates.size()-1).toString());
                    textViewDate2.setText(dateDates.get(midleValueofDateListSize).toString());
                    textViewDate1.setText(dateDates.get(0).toString());

                    Collections.sort(dateDates);
                    for (int i = 0; i < valDates.size(); i++){
                        series.appendData(new DataPoint(i ,valDates.get(i)), false, dateDates.size());
                    }
                    graph.addSeries(series);
                    series.setTitle("Population");
                    series.setColor(Color.GREEN);
                    series.setDrawDataPoints(true);
                    series.setDataPointsRadius(10);
                    series.setThickness(3);

                    // custom paint to make a dotted line
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(8);
                    paint.setPathEffect(new DashPathEffect(new float[]{jsonArray.length(), jsonArray.length()}, 0));
                    //series2.setCustomPaint(paint);
                    super.onPostExecute(result);}
            } catch (JSONException e) {
                e.printStackTrace();
            }

            
        }

    }
}