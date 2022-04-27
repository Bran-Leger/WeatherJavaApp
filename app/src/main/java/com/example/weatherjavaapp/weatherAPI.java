package com.example.weatherjavaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class weatherAPI extends AsyncTask<Void, Void, Void> {
   // private com.android.volley.toolbox.Volley Volley;
    JSONObject responseJSON;
    private RequestQueue queue;
    Activity a;
    Handler handler;

    String URL = "";
    String zipcode;
    int num;


    public weatherAPI(String z, Activity a, Handler handler) {
        zipcode = z;
        this.a = a;
        this.handler = handler;

    }

    public String getZip(){
        //doInBackground();
        return URL;
    }


    protected void onPreExecute() {
            /*
             *    do things before doInBackground() code runs
             *    such as preparing and showing a Dialog or ProgressBar
            */
       }

   public Void doInBackground(Void...params){
       queue = Volley.newRequestQueue(a.getApplicationContext());

       String mapquestURL = "http://open.mapquestapi.com/geocoding/v1/address?key=4krg3WCs4heLPdqsGXdDIkSeO80gmSYL&location=" + zipcode;


       StringRequest stringRequest = getData(mapquestURL, a);
       queue.add(stringRequest);
       //return null;
       return null;
  }
  @Override
     protected void onPostExecute(Void result) {
          /*
           *    do something with data here
           *    display it or send to mainactivity
           *    close any dialogs/ProgressBars/etc...
          */
     }
  public void setJSONObject(JSONObject j) {
     this.responseJSON = j;
  }
  private StringRequest getData(String url, Activity activity) {
      return new StringRequest(Request.Method.GET, "" + url, new Response.Listener<String>() {

          @Override
          public void onResponse(String response) {
              try {
                 String urlWeatherPoints = "https://api.weather.gov/points/";
                 String latitude = "";
                 String longitude = "";
                 String city = "";
                  JSONObject mapquestJSON = new JSONObject(response);

                 JSONArray results = mapquestJSON.getJSONArray("results");
                 JSONArray locations = results.getJSONObject(0).getJSONArray("locations");

                 for(int i = 0; i < locations.length(); i++) {
                    if (locations.getJSONObject(i).getString("adminArea1").equals("US")) {
                       JSONObject x = locations.getJSONObject(i).getJSONObject("latLng");
//                        JSONObject a = x.getJSONObject("lat");
//                        JSONObject b = x.getJSONObject("lng");
                        latitude = x.getString("lat");
                        longitude = x.getString("lng");
                        city = locations.getJSONObject(i).getString("adminArea5");
                    }
                 }
                 URL = urlWeatherPoints+latitude+","+longitude;
                 num = 123;
//                  Message msg = handler.obtainMessage();
//                  msg.arg1 = 123;
//                  handler.sendMessage(msg);
//                  RequestQueue q = Volley.newRequestQueue(activity.getApplicationContext());
//                     Intent i = new Intent(activity, Zip.class);
//                     i.putExtra("url", urlWeatherPoints+latitude+","+longitude);
//                     i.putExtra("city", city);
                //    startActivity(i);
//                   URL = urlWeatherPoints+latitude+","+longitude;
              } catch (JSONException e) {
                  //Toast.makeText(Zip.this, e.getMessage(), Toast.LENGTH_LONG).show();
              }
          }
      },
      new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
              //Toast.makeText(Zip.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
          }
      });
  }

  private StringRequest getWeatherPoints(String url, Activity activity, String city) {
        return new StringRequest(Request.Method.GET, "" + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject weatherPointsJSON = new JSONObject(response);
                   String forecastURL = weatherPointsJSON.getJSONObject("properties").getString("forecast");
                    RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
                     StringRequest stringRequest = forecastURL(forecastURL, activity, city);
                     queue.add(stringRequest);
                } catch (JSONException e) {

                }
            }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  //Toast.makeText(Zip.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
              }
          });
      }

      private StringRequest forecastURL(String url, Activity activity, String city) {
              return new StringRequest(Request.Method.GET, "" + url, new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                      try {
                         JSONObject forecastJSON = new JSONObject(response);
                         JSONArray weeklyForecast = forecastJSON.getJSONObject("properties").getJSONArray("periods");

                         Forecast forecast = new Forecast();
                         for (int i = 0; i < 7; i++) {
                            forecast.addForecast(i, Integer.parseInt(weeklyForecast.getJSONObject(i).getString("temperature")), weeklyForecast.getJSONObject(i).getString("shortForecast"));
                         }
                         forecast.setCity(city);
                         System.out.println("hi");
                      } catch (JSONException e) {

                      }
                  }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Zip.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }





