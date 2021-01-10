package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> code = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.gdp:
                startActivity(new Intent(this, gdp_rank_class.class));
                break;
            case R.id.debt:
                startActivity(new Intent(this, debt_class.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewMain);
        countryCode();
        final Intent intent = new Intent(this, show_population.class);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("getCountryCode", code.get(i).toString());
                intent.putExtra("getCountryName", name.get(i).toString());
                startActivity(intent);
            }
        });
    }
    public void countryCode(){
        String codes = "";
        try {
            InputStream in = getAssets().open("countries_codes.json");
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            codes = new String(buffer);
            JSONArray jsonArray = new JSONArray(codes);
            for (int i = 0; jsonArray.length() > i; i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                code.add(jo.getString("Code"));
                name.add(jo.getString("Name"));
            }
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}