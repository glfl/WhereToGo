package com.company.wheretogo;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity
    implements OnMapReadyCallback {
    String name;
    double myLat;
    double myLng;
    double venueLat;
    double venueLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        myLat = intent.getDoubleExtra(MainActivity.EXTRA_MYLAT,0);
        myLng = intent.getDoubleExtra(MainActivity.EXTRA_MYLNG,0);
        venueLat = intent.getDoubleExtra(MainActivity.EXTRA_VLAT,0);
        venueLng = intent.getDoubleExtra(MainActivity.EXTRA_VLNG,0);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .mapToolbarEnabled(false)
                .scrollGesturesEnabled(true)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(true)
                .tiltGesturesEnabled(false)
                .camera(CameraPosition.builder().target(new LatLng(venueLat,venueLng)).zoom(14).build());
        MapFragment mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(venueLat,venueLng))
                .title(name));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(myLat,myLng))
                .title("Вы здесь")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
}
