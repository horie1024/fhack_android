package com.example.horie.hack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nissiy on 2014/09/06.
 */
public class MainFragment extends Fragment {
    @InjectView(R.id.hello_world)
    TextView helloWorld;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.main_fragment, null);
        ButterKnife.inject(this, mainLayout);

        String url = "http://ec2-54-68-44-142.us-west-2.compute.amazonaws.com:8080/api";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = (JSONObject) response.getJSONArray("Results").get(0);
                            Log.v("hoge", String.valueOf(result));
                            updateLayout(result);
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
        return mainLayout;
    }

    private void updateLayout(JSONObject result) {
        try {
            helloWorld.setText(result.getString("Title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
