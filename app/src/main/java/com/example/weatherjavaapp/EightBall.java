package com.example.weatherjavaapp;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Bitmap;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.seismic.ShakeDetector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

//public class SoundPoolPlayer {
//    private SoundPool mShortPlayer= null;
//    private HashMap mSounds = new HashMap();
//
//    public SoundPoolPlayer(Context pContext)
//    {
//        // setup Soundpool
//        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//
//
//        mSounds.put(R.raw.<magic>, this.mShortPlayer.load(pContext, R.raw.<magic>, 1));
//        //mSounds.put(R.raw.<sound_2_name>, this.mShortPlayer.load(pContext, R.raw.<sound_2_name>, 1));
//    }
//
//    public void playShortResource(int piResource) {
//        int iSoundId = (Integer) mSounds.get(piResource);
//        this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
//    }
//
//    // Cleanup
//    public void release() {
//        // Cleanup
//        this.mShortPlayer.release();
//        this.mShortPlayer = null;
//    }
//}

public class EightBall extends AppCompatActivity implements ShakeDetector.Listener {

    HashSet<Integer> currZips = new HashSet<>();
    Forecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);

        // delay for something
        int sensorDelay = SensorManager.SENSOR_DELAY_GAME;

        // start the sensor
        sd.start(sensorManager, sensorDelay);

        // hide the action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        actionBar.hide();
        setContentView(R.layout.eight_ball);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            forecast = new Forecast();
            forecast.city = extras.getString("city");
            for (int i = 0; i < 7; i++){
                forecast.addForecast(i, extras.getIntArray("temperatures")[i], extras.getStringArray("forecasts")[i]);
            }
        }

        final int screenWidth = getScreenDimensions(this).x;
        final int waveImgWidth = getResources().getDrawable(R.drawable.wave).getIntrinsicWidth();
        int animatedViewWidth = 0;
        while (animatedViewWidth < screenWidth) {
            animatedViewWidth += waveImgWidth;
        }
        animatedViewWidth += waveImgWidth;


        View animatedView = findViewById(R.id.animated_view);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) animatedView.getLayoutParams();
        layoutParams.width = animatedViewWidth;
        animatedView.setLayoutParams(layoutParams);


        Animation waveAnimation = new TranslateAnimation(0, -waveImgWidth, 0, 0);
        waveAnimation.setInterpolator(new LinearInterpolator());
        waveAnimation.setRepeatCount(Animation.INFINITE);
        waveAnimation.setDuration(10000);

        animatedView.startAnimation(waveAnimation);


    }

    public void gotoZipcode(){
        Intent i = new Intent(this, Zip.class);
        startActivity(i);
    }

    public void gotoForecast(){
        //Intent i = new Intent(this)

    }

    @Override
    public void hearShake() {
        Toast.makeText(this, "Don't shake me, bro!", Toast.LENGTH_SHORT).show();
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.magic);
        mp.start();
        getPrediction();
    }

    public static Point getScreenDimensions(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        return new Point(width, height);
    }

    public void getPrediction() {
        // make API call to get the next day's weather
        // parse it to get the weather

        System.out.println("sdfasadf");

        String weather = forecast.forecast[1].toLowerCase();

        if (weather.contains("cloud")) weather = "cloudy";
        else if (weather.contains("rain") || weather.contains("shower")) weather = "rainy";
        else if (weather.contains("sun") || weather.contains("clear")) weather = "sunny";
        else weather = "sunny";


        String[] cloudyPredictions = new String[]{
                "YOUR FUTURE IS CLOUDY", "HEAD IN THE CLOUDS", "GREY SKIES ARE COMING"
        };

        String[] sunnyPredictions = new String[]{
                "TOMORROW LOOKS BRIGHT", "YOUR FUTURE IS SUNNY", "YOU WILL BASK IN THE SUN"
        };

        String[] rainyPredictions = new String[]{
                "RAIN        AND         TEARS", "RAIN ON YOUR PARADE", "CAREFUL YOU DON'T SLIP"
        };


        int rand = (int) Math.random() * 3 + 1;
        String prediction = "";

        switch (weather) {
            case "cloudy":
                prediction = cloudyPredictions[rand];
                break;
            case "sunny":
                prediction = sunnyPredictions[rand];
                break;
            case "rainy":
                prediction = rainyPredictions[rand];
                break;
        }

        TextView predictionTV = (TextView)findViewById(R.id.predictionTV);
        predictionTV.setText(prediction);

    }

}



