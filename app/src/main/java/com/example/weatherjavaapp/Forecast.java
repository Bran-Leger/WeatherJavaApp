package com.example.weatherjavaapp;

class Forecast {
    String city;
    int[] temperature;
    String[] forecast;
    Forecast() {
         city = "";
         temperature = new int[7];
         forecast = new String[7];
    }

    public void setCity(String c) {
         city = c;
    }
    public void addForecast(int i, int t, String f) {
       temperature[i] = t;
       forecast[i] = f;
    }
}