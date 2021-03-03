package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class demography extends AppCompatActivity {
    ListView listView;
    ArrayList<String> code = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_1:
                startActivity(new Intent(this, gdp_rank_class.class));
                break;
            case R.id.item_2:
                startActivity(new Intent(this, debt_class.class));
                break;
            case  R.id.item_3:
                startActivity(new Intent(this, main_menu.class));
                break;
            case R.id.item_4:
                startActivity(new Intent(this, notes.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item_gdp = menu.findItem(R.id.item_1);
        MenuItem item_debt = menu.findItem(R.id.item_2);
        MenuItem item_menu = menu.findItem(R.id.item_3);
        MenuItem item_notes =menu.findItem(R.id.item_4);

        item_gdp.setTitle("GDP Rank");
        item_debt.setTitle("Debt of Countries");
        item_menu.setTitle("Main menu");
        item_notes.setTitle("Notes");

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, main_menu.class));
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demography);

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
            InputStream in = getAssets().open("country_codes.json");
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