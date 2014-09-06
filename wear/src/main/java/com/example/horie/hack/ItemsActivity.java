package com.example.horie.hack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ProgressBar;

import com.example.horie.hack.data.Item;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nissiy on 2014/09/07.
 */
public class ItemsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = ItemsActivity.class.getSimpleName();

    private GestureDetectorCompat mGestureDetector;
    private DismissOverlayView mDismissOverlayView;
    private GoogleApiClient mGoogleApiClient;
    private InsetGridViewPager mGridViewPager;
    private ProgressBar mProgressBar;
    private boolean fetchedData = false;
    private Rect mInsets = new Rect(0, 0, 0, 0);

    private ArrayList<Item> mAttractions = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mGridViewPager = (InsetGridViewPager) findViewById(R.id.gridViewPager);
        mGridViewPager.setAdapter(new ItemGridPagerAdapter(this, mAttractions, mInsets));

        mGridViewPager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                mInsets.set(insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());

                return insets;
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();

        mDismissOverlayView = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlayView.setIntroText(getString(R.string.exit_intro_text));
        mDismissOverlayView.showIntroIfNecessary();
        mGestureDetector = new GestureDetectorCompat(this, new LongPressListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class LongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent event) {
            mDismissOverlayView.show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ArrayList<String> itemUris =
                getIntent().getStringArrayListExtra("extra_updated_items");

        // Only fetch data one time
        if (!fetchedData && itemUris != null && itemUris.size() > 0) {
            new FetchDataAsyncTask().execute(itemUris);
            fetchedData = true;
        } else {
            finish();
        }
    }

    private class FetchDataAsyncTask extends
            AsyncTask<ArrayList<String>, Void, ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(ArrayList<String>... params) {
            mAttractions.clear();
            Iterator<String> itr = params[0].iterator();
            while (itr.hasNext()) {
                Uri uri = Uri.parse(itr.next());
                DataApi.DataItemResult dataItemResult =
                        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).await();

                DataItem dataItem = dataItemResult.getDataItem();
                if (dataItem != null) {
                    DataMap itemDataMap =
                            DataMapItem.fromDataItem(dataItem).getDataMap();

                    Item item = new Item();
                    item.Title = itemDataMap.getString("item_title");
                    item.BrandName = itemDataMap.getString("item_brand_name");
                    item.Link = itemDataMap.getString("item_link");
                    item.DescLong = itemDataMap.getString("desc_long");
                    item.Price = itemDataMap.getString("item_price");
                    item.PlaceList = itemDataMap.getStringArrayList("place_list");
                    item.bitmapImage = loadBitmapFromAsset(
                            mGoogleApiClient, itemDataMap.getAsset("item_image"));

                    mAttractions.add(item);
                }
            }
            return mAttractions;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> result) {
            ItemGridPagerAdapter adapter =
                    new ItemGridPagerAdapter(ItemsActivity.this, result, mInsets);

            mGridViewPager.setAdapter(adapter);
            mGridViewPager.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
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
