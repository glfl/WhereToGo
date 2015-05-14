package com.company.wheretogo;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends ActionBarActivity implements LocationListener {
    private TextView latitudeField;
    private TextView longitudeField;
    private EditText radiusField;
    private TextView venueField;
    private LocationManager locationManager;
    private String provider;
    private int radius;
    private double myLatitude;
    private double myLongitude;
    private double venueLatitude;
    private double venueLongitude;
    private String venueName;
    public final static String EXTRA_NAME = "com.company.wheretogo.NAME";
    public final static String EXTRA_MYLAT = "com.company.wheretogo.MYLAT";
    public final static String EXTRA_MYLNG = "com.company.wheretogo.MYLNG";
    public final static String EXTRA_VLAT = "com.company.wheretogo.VLAT";
    public final static String EXTRA_VLNG = "com.company.wheretogo.VLNG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radius = 100;
        radiusField = (EditText) findViewById(R.id.radius);
        latitudeField = (TextView) findViewById(R.id.textView2);
        longitudeField = (TextView) findViewById(R.id.textView4);
        venueField = (TextView) findViewById(R.id.textView6);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        // here
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
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            String url = String.format("https://api.foursquare.com/v2/venues/search?ll=%s,%s&client_id=120NFTDAPLRBGQPI4Z5HW43HELWZZEOJORPSJAQVBFQ0F3LK&client_secret=42AMHHXLG1X2FGZFNXYYNBQUCALYGB3SG0BA2KG4HV1RSHM3&radius=%d&v=20150513&m=foursquare",
                    String.valueOf(myLatitude),String.valueOf(myLongitude),radius);
            RequestQueue queue = Volley.newRequestQueue(this);
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, (String)null, future, future);
            queue.add(jsObjRequest);
            try {
                JSONObject response = future.get(30, TimeUnit.SECONDS);
                JSONObject responseObj = response.getJSONObject("response");
                JSONArray venues = responseObj.getJSONArray("venues");
                Random r = new Random();
                int rand = r.nextInt(venues.length());
                JSONObject randVenue = venues.getJSONObject(rand);
                venueName = randVenue.getString("name");
                venueField.setText(venueName);
                JSONObject venueLocation = randVenue.getJSONObject("location");
                venueLatitude = Double.parseDouble(venueLocation.getString("lat"));
                venueLongitude = Double.parseDouble(venueLocation.getString("lng"));
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(EXTRA_NAME,venueName);
                intent.putExtra(EXTRA_MYLAT,myLatitude);
                intent.putExtra(EXTRA_MYLNG,myLongitude);
                intent.putExtra(EXTRA_VLAT,venueLatitude);
                intent.putExtra(EXTRA_VLNG,venueLongitude);
                startActivity(intent);
            } catch (InterruptedException | TimeoutException |
                    ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            latitudeField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }
}
