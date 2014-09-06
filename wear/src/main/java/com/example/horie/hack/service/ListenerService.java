package com.example.horie.hack.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.horie.hack.MyActivity;
import com.example.horie.hack.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nissiy on 2014/09/06.
 */
public class ListenerService extends WearableListenerService {
    private static final String TAG = ListenerService.class.getSimpleName();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Not doing anything here but logging
                Uri uri = event.getDataItem().getUri();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                String title = dataMapItem.getDataMap().getString("item_title");
                Log.v(TAG, "Data changed: " + uri + ", " + title);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        if (!"/start".equals(messageEvent.getPath())) {
            return;
        }

        DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                10, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess() || !googleApiClient.isConnected()) {
            Log.e(TAG, "Error");
            return;
        }

        ArrayList<String> items = dataMap.getStringArrayList("extra_updated_items");

        Intent intent = new Intent(this, MyActivity.class);
        Log.v("hoge", String.valueOf(items));
        intent.putExtra("extra_updated_items", items);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int count = items.size();

        Uri uri = Uri.parse(items.get(0));
        DataApi.DataItemResult dataItemResult =
                Wearable.DataApi.getDataItem(googleApiClient, uri).await();

        DataItem dataItem = dataItemResult.getDataItem();
        if (dataItem != null) {
            DataMap attractionDataMap =
                    DataMapItem.fromDataItem(dataItem).getDataMap();

            Bitmap bitmap = loadBitmapFromAsset(
                    googleApiClient, attractionDataMap.getAsset("item_image"));

            Notification notification = new Notification.Builder(this)
                    .setContentText(
                            getResources().getQuantityString(R.plurals.attractions_found, count, count))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .addAction(R.drawable.ic_full_explore,
                            "もっと見る",
                            pendingIntent)
                    .extend(new Notification.WearableExtender()
                                    .setBackground(bitmap)
                    )
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(200, notification);
        }

        googleApiClient.disconnect();
    }

     private Bitmap loadBitmapFromAsset(GoogleApiClient googleApiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                googleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }

}
