package com.example.horie.hack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.horie.hack.data.Item;
import com.example.horie.hack.service.UtilityService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
                            JSONObject iqon = response.getJSONObject("Iqon");
                            JSONArray results = iqon.getJSONArray("Results");
                            ArrayList<Item> items = new ArrayList<Item>();

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject result = (JSONObject) results.get(i);

                                ArrayList<String> placeList = new ArrayList<String>();
                                JSONArray places =  result.getJSONArray("Place");
                                for (int j = 0; j < places.length(); j++) {
                                    JSONObject place = (JSONObject) places.get(j);
                                    if (place.getBoolean("Flag")) {
                                        placeList.add(place.getString("Name"));
                                    }
                                }

                                Item item = new Item(
                                        result.getString("Title"),
                                        result.getString("Brand_name"),
                                        result.getString("Link"),
                                        result.getString("Desc_long"),
                                        result.getString("Price"),
                                        result.getJSONObject("Images").getString("L_image"),
                                        placeList);

                                items.add(item);
                            }

                            updateMobileLayout(items);
                            updateWearLayout(items);
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

    private void updateMobileLayout(ArrayList<Item> items) {
        // TODO: ここでモバイル版のレイアウトを開発
    }

    private void updateWearLayout(ArrayList<Item> items) {
        Intent intent = new Intent(getActivity(), UtilityService.class);
        intent.putExtra("ITEMS", items);
        getActivity().startService(intent);
    }

}
