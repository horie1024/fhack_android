package com.example.horie.hack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

/**
 * Created by nissiy on 2014/09/06.
 */
public class AreaFragment extends Fragment {
    private int displayWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView mainLayout = (ScrollView) inflater.inflate(R.layout.area_fragment, null);
        ButterKnife.inject(this, mainLayout);
        displayWidth = getActivity().getResources().getDisplayMetrics().widthPixels;

        Bundle args = getArguments();
        if (args != null) {
            requestHttp(args.getString("LAT"), args.getString("LON"));
        }

        return mainLayout;
    }

    private void requestHttp(String lat, String lon) {
        Log.v("hoge", lat);
        Log.v("hoge", lon);
        String url = "http://ec2-54-68-44-142.us-west-2.compute.amazonaws.com:8080/api?lat=" + lat + "&lng=" + lon;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = (JSONObject) response.getJSONArray("Results").get(0);
                            updateMobileLayout(result);
                            updateWearLayout(result);
                            Log.v("hoge", String.valueOf(result));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: error.networkResponseを確認してエラー処理を書く
                    }
                }));
    }

    private void updateMobileLayout(JSONObject result) {
        // TODO: ここでモバイル版のレイアウトを開発
    }

    private void updateWearLayout(JSONObject result) {
        // TODO: ここでWearのレイアウトを開発
    }

}
