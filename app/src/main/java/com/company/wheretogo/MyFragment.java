package com.company.wheretogo;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by 111 on 20.05.2015.
 */
public class MyFragment extends MapFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static MyFragment newInstance(GoogleMapOptions options) {
        MyFragment f = new MyFragment();
        Bundle args = new Bundle();
        args.putParcelable("MapOptions", options);
        f.setArguments(args);
        return f;
    }
}
