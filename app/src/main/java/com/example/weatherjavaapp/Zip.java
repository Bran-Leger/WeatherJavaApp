package com.example.weatherjavaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherjavaapp.db.Zipcode;
import com.example.weatherjavaapp.db.ZipcodeDatabase;
import com.example.weatherjavaapp.db.ZipcodeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// hashmap of all the current zipcodes you have, so we don't need to make a  needless database call


public class Zip extends AppCompatActivity implements ZipcodeDialogInterface {
    private ZipcodeDialogFragment.ZipcodeDialogFragmentInner z = new ZipcodeDialogFragment.ZipcodeDialogFragmentInner();

    private ZipcodeViewModel zipcodeViewModel;
    JSONObject responseJSON;

    Forecast forecast = new Forecast();

    private RequestQueue queue;
    Handler handler;
    Thread newThread;
    String cityName = "";
    int selectedZip = 0;
    int addedZip = 0;
    boolean triggerOnce = false;

    String URL = "";
    HashSet<Integer> currZips = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zipcode_page);
        Intent intent = getIntent();
        queue = Volley.newRequestQueue(this);

        zipcodeViewModel = new ViewModelProvider(this).get(ZipcodeViewModel.class);
        ZipcodeDatabase.deleteAll();
        //ZipcodeDatabase.createZipcodeTable();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            forecast = new Forecast();
            forecast.city = extras.getString("city");
            forecast.temperature = extras.getIntArray("temperatures");
            forecast.forecast = extras.getStringArray("forecast");
        }


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        RecyclerView recyclerView = findViewById(R.id.lstZipcodes);
        ZipcodeListAdapter adapter = new ZipcodeListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        zipcodeViewModel.getAllZipcodes().observe(this, adapter::setZipcodes);
    }


    // method to dynamically create the rows
    public void fill() {


        TableLayout zipcodeTable = (TableLayout) findViewById(R.id.zipcodeTable);

        // table column names
        TableRow r0 = new TableRow(this);
        TextView t0 = new TextView(this);
        t0.setText("Zipcode");
        t0.setTextColor(Color.BLACK);
        r0.addView(t0);
        TextView t1 = new TextView(this);
        t1.setText("City");
        t1.setTextColor(Color.BLACK);
        r0.addView(t1);


        // dynamically fill all the zipcodes in the database
        for (int i = 0; i < 10; i++) {
            TableRow rX = new TableRow(this);
            TextView t2 = new TextView(this);
        }


    }

    public void gotoMagicZipPage(View view) {

        Intent i = new Intent(this, EightBall.class);
        i.putExtra("city", forecast.city);
        i.putExtra("temperatures", forecast.temperature);
        i.putExtra("forecasts", forecast.forecast);
        startActivity(i);
    }

    public void gotoForecast(View view){
        Intent i = new Intent(this, ForecastPage.class);
        i.putExtra("city", forecast.city);
        i.putExtra("temperatures", forecast.temperature);
        i.putExtra("forecasts", forecast.forecast);
        startActivity(i);
    }

    //FloatingActionButton fab = findViewById(R.id.fab);
    public void getZip(View view) {
        z.show(getSupportFragmentManager(), "zipcode_fragment");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        return;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {


        Toast toas = Toast.makeText(this, URL, Toast.LENGTH_LONG);
        toas.show();

        // get the zipcode
        EditText zipcode = (EditText) dialog.getDialog().findViewById(R.id.zipcodeBox);
        String zipcodeNum = zipcode.getText().toString();

        // check if the zipcode is in range
        int num = Integer.parseInt(zipcodeNum);

        // if in valid range
        if (num >= 1 && num <= 99950) {
            // check the hashmap to see if this Zipcode is already in the list
            if (currZips.contains(num)) {
                Toast toast = Toast.makeText(this, "Zipcode is already added", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // make an API call to get the zipcode information, then store it in the database


                try {

                    addedZip = num;
                    getWeatherPointURL(zipcodeNum);

                } catch (Exception e) {
                }


                return;
            }
        }
    }


    public class ZipcodeListAdapter extends RecyclerView.Adapter<ZipcodeListAdapter.ZipcodeViewHolder> {
        // If the JokeListAdapter were an outer class, the JokeViewHolder could be
        // a static class.  We want to be able to get access to the MainActivity instance,
        // so we want it to be an inner class


        class ZipcodeViewHolder extends RecyclerView.ViewHolder {
            private final TextView zipcodeView;
            private final TextView cityView;
            //private final ImageView selectedView;
            private Zipcode zipcode;

            // Note that this view holder will be used for different items -
            // The callbacks though will use the currently stored item

            private ZipcodeViewHolder(View itemView) {
                super(itemView);
                zipcodeView = itemView.findViewById(R.id.zipcodeNumber);
                cityView = itemView.findViewById(R.id.cityName);

                // TODO THIS IS WHERE YOU SELECT A ZIPCODE
                itemView.setOnClickListener(view -> {
                    // If the selected zipcode is false
                    if (!zipcode.selected) {
                        zipcode.selected = true;
                        Toast toast = Toast.makeText(Zip.this, "Zipcode set to " + zipcode.city, Toast.LENGTH_LONG);
                        toast.show();

                        // get the current selected zipcode and set it to false,
                        ZipcodeDatabase.getZipcode(0, currZip -> {
                            currZip.selected = false;
                            ZipcodeDatabase.update(currZip);
                        });
                        ZipcodeDatabase.update(zipcode);
                        selectedZip = zipcode.zip;
                        dontAddGetWeatherPointURL("" + selectedZip);

                    }
                });
            }
        }

        private final LayoutInflater layoutInflater;
        private List<Zipcode> zipcodes; // Cached copy of jokes

        ZipcodeListAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public ZipcodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new ZipcodeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ZipcodeViewHolder holder, int position) {
            if (zipcodes != null) {
                Zipcode current = zipcodes.get(position);
                holder.zipcode = current;
                holder.zipcodeView.setText(String.valueOf(current.zip));

                // add zip code to the hashmap
                currZips.add(current.zip);

                holder.cityView.setText("                                 " + current.city);
                if (current.selected) {
                    //holder.selectedView.setImageResource();
                    holder.itemView.setBackgroundColor(Color.YELLOW);
                    // the last selected zip of the user
                    selectedZip = current.zip;
                    if (!triggerOnce){
                        triggerOnce = true;
                        dontAddGetWeatherPointURL("" + selectedZip);
                    }

                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                // Covers the case of data not being ready yet.
                holder.zipcodeView.setText("...intializing...");
            }
        }

        void setZipcodes(List<Zipcode> zipcodes) {
            this.zipcodes = zipcodes;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (zipcodes != null)
                return zipcodes.size();
            else return 0;
        }


    }

    public void setJSONObject(JSONObject j) {
        this.responseJSON = j;
    }


    private void getWeatherPointURL(String zipcode) {
        StringRequest req = getData(zipcode, this);
        queue.add(req);
    }

    private void dontAddGetWeatherPointURL(String zipcode) {
        StringRequest req = getData2(zipcode, this);
        queue.add(req);
    }

    private void getForecastURL(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject obj = (JSONObject) response;
                            System.out.println("helllo");
                            JSONObject weatherPointsJSON = obj;
                            String forecastURL = weatherPointsJSON.getJSONObject("properties").getString("forecast");
                            URL = forecastURL;
                            getTemperatures(URL);
                        } catch (Exception je) {
                            System.out.print(je);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("User-Agent", "(myweatherapp.com , warhawkhero124@gmail.com)");
                // headers.put("User-Agent", "contaxt@myweatherapp");
                //headers.put("myweatherapp.com", "contact@myweatherapp.com");

                return headers;
            }
        };


        Volley.newRequestQueue(this).add(jsonObjectRequest.setTag("headerRequest"));
    }

    private void getTemperatures(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject forecastJSON = (JSONObject) response;
                            JSONArray weeklyForecast = forecastJSON.getJSONObject("properties").getJSONArray("periods");

                            forecast = new Forecast();
                            for (int i = 0; i < 7; i++) {
                                forecast.addForecast(i, Integer.parseInt(weeklyForecast.getJSONObject(i).getString("temperature")), weeklyForecast.getJSONObject(i).getString("shortForecast"));
                            }
                            forecast.setCity(cityName);

                            // put this information within the database
                            if (cityName == null || cityName.equals("")) cityName = "Unknown";


                            setForecast(forecast);

                        } catch (Exception je) {
                            System.out.print(je);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("User-Agent", "(myweatherapp.com , warhawkhero124@gmail.com)");
                // headers.put("User-Agent", "contaxt@myweatherapp");
                //headers.put("myweatherapp.com", "contact@myweatherapp.com");

                return headers;
            }
        };


        Volley.newRequestQueue(this).add(jsonObjectRequest.setTag("headerRequest"));
        StringRequest req = getWeatherPoints(url);
        queue.add(req);
    }

    private StringRequest getData(String zipcode, Activity activity) {
        return new StringRequest(Request.Method.GET, "http://open.mapquestapi.com/geocoding/v1/address?key=4krg3WCs4heLPdqsGXdDIkSeO80gmSYL&location=" + zipcode, new Response.Listener<String>() {

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

                    for (int i = 0; i < locations.length(); i++) {
                        if (locations.getJSONObject(i).getString("adminArea1").equals("US")) {
                            JSONObject x = locations.getJSONObject(i).getJSONObject("latLng");
//                        JSONObject a = x.getJSONObject("lat");
//                        JSONObject b = x.getJSONObject("lng");
                            latitude = x.getString("lat");
                            longitude = x.getString("lng");
                            city = locations.getJSONObject(i).getString("adminArea5");
                        }
                    }
                    // RequestQueue q = Volley.newRequestQueue(activity.getApplicationContext());

                    URL = urlWeatherPoints + latitude + "," + longitude;
                    cityName = city;
                    getForecastURL(URL);
                } catch (JSONException e) {
                    Toast.makeText(Zip.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Zip.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private StringRequest getWeatherPoints(String url) {
        return new StringRequest(Request.Method.GET, "" + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject weatherPointsJSON = new JSONObject(response);
                    String forecastURL = weatherPointsJSON.getJSONObject("properties").getString("forecast");
                    URL = forecastURL;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            URL, null,
                            new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    try {
                                        JSONObject obj = (JSONObject) response;
                                        System.out.println("helllo");
                                    } catch (Exception je) {
                                        System.out.print(je);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        public Map getHeaders() throws AuthFailureError {
                            HashMap headers = new HashMap();
                            headers.put("User-Agent", "(myweatherapp.com , warhawkhero124@gmail.com)");
                            // headers.put("User-Agent", "contaxt@myweatherapp");
                            //headers.put("myweatherapp.com", "contact@myweatherapp.com");

                            return headers;
                        }
                    };


                    Volley.newRequestQueue(Zip.this).add(jsonObjectRequest.setTag("headerRequest"));
                    getTemperatures(URL);
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

    private StringRequest forecastURL(String url) {
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
                    forecast.setCity(cityName);


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

    public void setForecast(Forecast f) {
        for (int i = 0; i < 7; i++) {
            forecast.addForecast(i, f.temperature[i], f.forecast[i]);
        }
        forecast.city = f.city;

        if (!currZips.contains(addedZip) && addedZip != 0) {
            Zipcode newZip = new Zipcode(addedZip, f.city, false);
            ZipcodeDatabase.insert(newZip);
        }
    }

    public void setForecast2(Forecast f) {
        for (int i = 0; i < 7; i++) {
            forecast.addForecast(i, f.temperature[i], f.forecast[i]);
        }
        forecast.city = f.city;
    }

    private StringRequest getData2(String zipcode, Activity activity) {
        return new StringRequest(Request.Method.GET, "http://open.mapquestapi.com/geocoding/v1/address?key=4krg3WCs4heLPdqsGXdDIkSeO80gmSYL&location=" + zipcode, new Response.Listener<String>() {

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

                    for (int i = 0; i < locations.length(); i++) {
                        if (locations.getJSONObject(i).getString("adminArea1").equals("US")) {
                            JSONObject x = locations.getJSONObject(i).getJSONObject("latLng");
                            //                        JSONObject a = x.getJSONObject("lat");
                            //                        JSONObject b = x.getJSONObject("lng");
                            latitude = x.getString("lat");
                            longitude = x.getString("lng");
                            city = locations.getJSONObject(i).getString("adminArea5");
                        }
                    }
                    // RequestQueue q = Volley.newRequestQueue(activity.getApplicationContext());

                    URL = urlWeatherPoints + latitude + "," + longitude;
                    cityName = city;
                    getForecastURL2(URL);
                } catch (JSONException e) {
                    Toast.makeText(Zip.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Zip.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getForecastURL2(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject obj = (JSONObject) response;
                            System.out.println("helllo");
                            JSONObject weatherPointsJSON = obj;
                            String forecastURL = weatherPointsJSON.getJSONObject("properties").getString("forecast");
                            URL = forecastURL;
                            getTemperatures(URL);
                        } catch (Exception je) {
                            System.out.print(je);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("User-Agent", "(myweatherapp.com , warhawkhero124@gmail.com)");
                // headers.put("User-Agent", "contaxt@myweatherapp");
                //headers.put("myweatherapp.com", "contact@myweatherapp.com");

                return headers;
            }
        };


        Volley.newRequestQueue(this).add(jsonObjectRequest.setTag("headerRequest"));
    }

    private void getTemperatures2(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject forecastJSON = (JSONObject) response;
                            JSONArray weeklyForecast = forecastJSON.getJSONObject("properties").getJSONArray("periods");

                            forecast = new Forecast();
                            for (int i = 0; i < 7; i++) {
                                forecast.addForecast(i, Integer.parseInt(weeklyForecast.getJSONObject(i).getString("temperature")), weeklyForecast.getJSONObject(i).getString("shortForecast"));
                            }
                            forecast.setCity(cityName);

                            // put this information within the database
                            if (cityName == null || cityName.equals("")) cityName = "Unknown";


                            setForecast2(forecast);

                        } catch (Exception je) {
                            System.out.print(je);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("User-Agent", "(myweatherapp.com , warhawkhero124@gmail.com)");
                // headers.put("User-Agent", "contaxt@myweatherapp");
                //headers.put("myweatherapp.com", "contact@myweatherapp.com");

                return headers;
            }
        };
    }
}