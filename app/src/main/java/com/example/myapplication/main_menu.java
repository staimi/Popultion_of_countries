package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main_menu extends AppCompatActivity {

    Button button_debt, button_demography, button_gdp, button_notes;
    Intent debt_class_intent, demography_class_intent, gdp_class_intent, notes_class_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        button_debt = findViewById(R.id.button_debt);
        button_demography = findViewById(R.id.button_domography);
        button_gdp = findViewById(R.id.button_gdp);
        button_notes = findViewById(R.id.button_notes);

        // create intents
        debt_class_intent = new Intent(this, debt_class.class);
        demography_class_intent = new Intent(this, demography.class);
        gdp_class_intent = new Intent(this, gdp_rank_class.class);
        notes_class_intent = new Intent(this, notes.class);

        // start new activity

        button_debt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(debt_class_intent);
            }
        });

        button_demography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(demography_class_intent);
            }
        });

        button_gdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(gdp_class_intent);
            }
        });

        button_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(notes_class_intent);
            }
        });
    }
}