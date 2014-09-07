package com.example.horie.hack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nissiy on 2014/09/06.
 */
public class AreaFragment extends Fragment {
    private int displayWidth;
    private LayoutInflater inflater;

    @InjectView(R.id.area_container)
    LinearLayout areaContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
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
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            LinearLayout areaParts = (LinearLayout) inflater.inflate(R.layout.area_parts, null);
            RelativeLayout imageParentLayout = (RelativeLayout) areaParts.findViewById(R.id.image_parent_layout);
            final ImageView imageView = (ImageView) areaParts.findViewById(R.id.image_view);
            TextView brandName = (TextView) areaParts.findViewById(R.id.brand_name);
            TextView itemTitle = (TextView) areaParts.findViewById(R.id.item_title);
            TextView price = (TextView) areaParts.findViewById(R.id.price);
            TextView shopList = (TextView) areaParts.findViewById(R.id.shop_list);

            areaParts.setPadding(0, 0, 0, 2);

            LinearLayout.LayoutParams imageParentLp = new LinearLayout.LayoutParams(displayWidth / 3, displayWidth / 3);
            imageParentLayout.setLayoutParams(imageParentLp);

            final String imageUrl = item.ImageUrl;
            AsyncTask<Void, Void, Bitmap> imageLoadTask = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return getBitmapFromUrl(imageUrl);
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    try {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            imageLoadTask.execute();

            brandName.setText(item.BrandName);
            itemTitle.setText(item.Title);
            price.setText(item.Price + "円");

            // TODO: 多分parseからdataに突っ込むときに失敗している
            // TODO: ！！！致命的！！！
            String shopListText = "";
            for (int j = 0; j < item.PlaceList.size(); j++) {
                shopListText = shopListText + item.PlaceList.get(j);
                if (j != item.PlaceList.size() - 1) {
                    shopListText = shopListText + ", ";
                }
            }
            shopList.setText(shopListText);

            areaParts.setTag(item.Link);
            areaParts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = (String) v.getTag();
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            areaContainer.addView(areaParts);
        }
    }

    private Bitmap getBitmapFromUrl(String url) throws Exception {
        InputStream input = new URL(url).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    private void updateWearLayout(ArrayList<Item> items) {
        Intent intent = new Intent(getActivity(), UtilityService.class);
        intent.putExtra("ITEMS", items);
        getActivity().startService(intent);
    }

}
