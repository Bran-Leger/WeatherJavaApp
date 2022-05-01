package com.example.weatherjavaapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;


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

        int[] days = {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7};
        int[] temps = {R.id.temperature1, R.id.temperature2, R.id.temperature3, R.id.temperature4, R.id.temperature5, R.id.temperature6, R.id.temperature7};
        int[] imgs = {R.id.forecastImage1, R.id.forecastImage2, R.id.forecastImage3, R.id.forecastImage4, R.id.forecastImage5, R.id.forecastImage6, R.id.forecastImage7};
        String[] weekday = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        int currday = c.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < 7; i++){
            String s = "";
            //if (i + currday % 7 == 0)
            // set the day of the week
            TextView temp = (TextView)findViewById(days[i]);
            temp.setText(weekday[((i + currday) - 1) % 7  ]);

            // set the temperature
            temp = (TextView)findViewById(temps[i]);
            temp.setText(forecast.temperature[i] + " ℉");

            ImageView img = (ImageView)findViewById(imgs[i]);
            String weather = forecast.forecast[i].toLowerCase();

            if (weather.contains("rain") || weather.contains("shower")) weather = "rainy";
            else if (weather.contains("cloud")) weather = "cloudy";
            else if (weather.contains("sun") || weather.contains("clear")) weather = "sunny";
            else weather = "sunny";

            switch (weather) {
                case "cloudy":
                    img.setImageResource(R.drawable.cloud);
                    break;
                case "sunny":
                    img.setImageResource(R.drawable.sun);
                    break;
                case "rainy":
                    img.setImageResource(R.drawable.rainy);
                    break;
            }

        }
        
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
