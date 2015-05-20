package com.company.wheretogo;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 111 on 18.05.2015.
 */
public class SynchronousGet {
    private final OkHttpClient client = new OkHttpClient();

    public String run(String s) throws Exception {
        Request request = new Request.Builder()
                .url(s)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        //System.out.println(response.body().string());
        //Log.d("cool","colcolcol");
        return response.body().string();
    }
}
