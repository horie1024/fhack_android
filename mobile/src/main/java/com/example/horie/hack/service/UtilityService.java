package com.example.horie.hack.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.horie.hack.data.Item;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nissiy on 2014/09/06.
 */
public class UtilityService extends IntentService {
    private static final String TAG = UtilityService.class.getSimpleName();

    public UtilityService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<Item> items = (ArrayList<Item>) intent.getSerializableExtra("ITEMS");
        sendDataToWearable(items);
    }

    private void sendDataToWearable(List<Item> items) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                10, TimeUnit.SECONDS);

        HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            String imageUrl = item.ImageUrl;
            try {
                Bitmap smallImage = getBitmapFromUrl(imageUrl);
                images.put(item.Title, smallImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
            ArrayList<String> itemIds = new ArrayList<String>();

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                Asset imageAsset = createAssetFromBitmap(images.get(item.Title));

                PutDataMapRequest dataMap = PutDataMapRequest.create(
                        "/item/" + Uri.encode(item.Title));

                dataMap.getDataMap().putString("item_title", item.Title);
                dataMap.getDataMap().putString("item_brand_name", item.BrandName);
                dataMap.getDataMap().putString("item_link", item.Link);
                dataMap.getDataMap().putString("item_price", item.Price);
                dataMap.getDataMap().putStringArrayList("place_list", item.PlaceList);
                dataMap.getDataMap().putAsset("item_image", imageAsset);
                PutDataRequest request = dataMap.asPutDataRequest();

                DataApi.DataItemResult result =
                        Wearable.DataApi.putDataItem(googleApiClient, request).await();

                if (result.getStatus().isSuccess()) {
                    itemIds.add(result.getDataItem().getUri().toString());
                }
            }

            DataMap dataMap = new DataMap();
            dataMap.putStringArrayList("extra_updated_items", itemIds);

            byte[] attractionIdBytes = dataMap.toByteArray();

            Iterator<String> itr = getNodes(googleApiClient).iterator();
            while (itr.hasNext()) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, itr.next(), "/start", attractionIdBytes);
            }
        }
        googleApiClient.disconnect();
    }

    public static Bitmap getBitmapFromUrl(String url) throws Exception {
        InputStream input = new URL(url).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    private Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    private Collection<String> getNodes(GoogleApiClient client) {
        Collection<String> results= new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(client).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

}
