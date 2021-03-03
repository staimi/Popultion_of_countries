package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class notes extends AppCompatActivity {


    SQLiteDatabase notes_sql;
    EditText editTextNotes;

    ///   menu in toolbar, options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                startActivity(new Intent(this, demography.class));
                break;
            case R.id.item_2:
                startActivity(new Intent(this, gdp_rank_class.class));
                break;
            case R.id.item_3:
                startActivity(new Intent(this, main_menu.class));
                break;
            case R.id.item_4:
                startActivity(new Intent(this, debt_class.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.menu, menu);

        MenuItem item_population = menu.findItem(R.id.item_1);
        MenuItem item_gdp = menu.findItem(R.id.item_2);
        MenuItem item_menu = menu.findItem(R.id.item_3);
        MenuItem item_debt = menu.findItem(R.id.item_4);

        item_population.setTitle("Population of Countries");
        item_gdp.setTitle("GDP Rank");
        item_menu.setTitle("Main menu");
        item_debt.setTitle("Debt of Countries");

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
        setContentView(R.layout.activity_notes);

        editTextNotes = findViewById(R.id.editTextNotes);

        try {
            notes_sql = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            notes_sql.execSQL("CREATE TABLE IF NOT EXISTS notes (note VARCHAR) ");

            Cursor cursor = notes_sql.rawQuery("SELECT * FROM notes", null);
            int noteIndex = cursor.getColumnIndex("note");
            cursor.moveToFirst();
            if(cursor.getString(noteIndex) != null){
                editTextNotes.setText(cursor.getString(noteIndex));}
            else{
                notes_sql.execSQL("INSERT INTO notes (note) VALUES ('New note')");
                editTextNotes.setText(cursor.getString(noteIndex));
            }

        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(this, main_menu.class));
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTextNotes.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Intent mainIntent = new Intent(getApplicationContext(), main_menu.class);
                try {
                    Cursor cursor = notes_sql.rawQuery("SELECT * FROM notes", null);
                    cursor.moveToFirst();
                    notes_sql.execSQL("UPDATE notes " +
                            "SET note =" + "'" + String.valueOf(charSequence)+"'");
                }catch (SQLException sqlException){
                    sqlException.printStackTrace();
                    startActivity(mainIntent);
                    Toast.makeText(notes.this, "Error", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    }