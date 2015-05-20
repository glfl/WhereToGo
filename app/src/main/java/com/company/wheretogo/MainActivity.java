package com.company.wheretogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

public class MainActivity extends ActionBarActivity implements LocationListener {
    private EditText radiusField;
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
    String clientId = "120NFTDAPLRBGQPI4Z5HW43HELWZZEOJORPSJAQVBFQ0F3LK";
    String clientSecret = "42AMHHXLG1X2FGZFNXYYNBQUCALYGB3SG0BA2KG4HV1RSHM3";
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radiusField = (EditText) findViewById(R.id.radius);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
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
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
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
        final Location location = locationManager.getLastKnownLocation(provider);
        onLocationChanged(location);
        if (location != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();
                    radius = Integer.parseInt(radiusField.getText().toString());
                    final String url = String.format("https://api.foursquare.com/v2/venues/explore?ll=%s,%s&client_id=%s&client_secret=%s&radius=%d&v=20150513&m=foursquare",
                            String.valueOf(myLatitude), String.valueOf(myLongitude), clientId, clientSecret, radius);
                    String data = null;
                    try {
                        data = new SynchronousGet().run(url);
                        JSONObject response = null;
                        try {
                            response = new JSONObject(data);
                            JSONArray venues = response.getJSONObject("response")
                                    .getJSONArray("groups")
                                    .getJSONObject(0)
                                    .getJSONArray("items");
                            Random r = new Random();
                            int rand = r.nextInt(venues.length());
                            JSONObject randVenue = venues.getJSONObject(rand).getJSONObject("venue");
                            venueName = randVenue.getString("name");
                            JSONObject venueLocation = randVenue.getJSONObject("location");
                            venueLatitude = Double.parseDouble(venueLocation.getString("lat"));
                            venueLongitude = Double.parseDouble(venueLocation.getString("lng"));
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            intent.putExtra(EXTRA_NAME,venueName);
                            intent.putExtra(EXTRA_MYLAT,myLatitude);
                            intent.putExtra(EXTRA_MYLNG,myLongitude);
                            intent.putExtra(EXTRA_VLAT,venueLatitude);
                            intent.putExtra(EXTRA_VLNG,venueLongitude);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Неизвестное местоположение")
                                            .setMessage("Проверьте подключение к интернету")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Неизвестное местоположение")
                                        .setMessage("Проверьте подключение к интернету")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                    }
                }});
            ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", null, true);
            ringProgressDialog.setCancelable(false);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ringProgressDialog.hide();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Неизвестное местоположение")
                    .setMessage("Проверьте подключение к интернету")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
