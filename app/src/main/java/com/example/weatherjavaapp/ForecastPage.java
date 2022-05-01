package com.example.weatherjavaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class ForecastPage extends AppCompatActivity {

    Forecast forecast = new Forecast();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);
        Intent intent = getIntent();

        // checking if the forecast has been saved
        if (savedInstanceState != null){
            forecast = new Forecast();
            forecast.city = savedInstanceState.getString("city");
            forecast.forecast = savedInstanceState.getStringArray("forecasts");
            forecast.temperature = savedInstanceState.getIntArray("temperatures");
        }
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            forecast = new Forecast();
            forecast.city = extras.getString("city");
            forecast.temperature = extras.getIntArray("temperatures");
            forecast.forecast = extras.getStringArray("forecasts");
        }
        
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        
    }
    @Override
    protected void onPause(){
        Log.d("life_cycle", "onPause invoked");
        super.onPause();
    }
    @Override
    protected void onStop(){
        Log.d("life_cycle", "onStop invoked");
        super.onStop();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("life_cycle", "onRestoreInstanceState invoked");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.d("life_cycle", "onSaveInstanceState invoked");

        // save the forecast
        outState.putString("city", forecast.city);
        outState.putStringArray("forecasts", forecast.forecast);
        outState.putIntArray("temperatures", forecast.temperature);
    }

    public void gotoZipcode(View view){
        Intent i = new Intent(this, Zip.class);
        startActivity(i);
    }

    public void gotoMagicZipPage(View view) {

        Intent i = new Intent(this, EightBall.class);
        i.putExtra("city", forecast.city);
        i.putExtra("temperatures", forecast.temperature);
        i.putExtra("forecasts", forecast.forecast);
        startActivity(i);
    }
}
