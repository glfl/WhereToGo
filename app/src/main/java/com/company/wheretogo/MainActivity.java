package com.company.wheretogo;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.Random;

public class MainActivity extends ActionBarActivity implements LocationListener {
    private TextView latitudeField;
    private TextView longitudeField;
    private TextView venueField;
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeField = (TextView) findViewById(R.id.textView2);
        longitudeField = (TextView) findViewById(R.id.textView4);
        venueField = (TextView) findViewById(R.id.textView6);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);

            String url = "https://api.foursquare.com/v2/venues/search?ll="+
                    String.valueOf((double)location.getLatitude())+","+
                    String.valueOf((double)location.getLongitude())+
                    "&client_id=120NFTDAPLRBGQPI4Z5HW43HELWZZEOJORPSJAQVBFQ0F3LK&client_secret=42AMHHXLG1X2FGZFNXYYNBQUCALYGB3SG0BA2KG4HV1RSHM3&radius=1000&v=20150513&m=foursquare";

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // TODO Auto-generated method stub
                            try {
                                JSONObject responseObj = response.getJSONObject("response");
                                JSONArray venues = responseObj.getJSONArray("venues");
                                Random r = new Random();
                                int rand = r.nextInt(venues.length());
                                String randVenue = venues.getJSONObject(rand).getString("name");
                                venueField.setText(randVenue);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
            queue.add(jsObjRequest);
        } else {
            latitudeField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 1000, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    
    public void onClick(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
