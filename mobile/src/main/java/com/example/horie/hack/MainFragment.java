package com.example.horie.hack;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nissiy on 2014/09/06.
 */
public class MainFragment extends Fragment implements LocationListener {
    private LocationManager locationManager;
    private String latitude;
    private String longitude;

    @InjectView(R.id.shinjuku)
    RelativeLayout shinjuku;
    @InjectView(R.id.shibuya)
    RelativeLayout shibuya;
    @InjectView(R.id.daikanyama)
    RelativeLayout daikanyama;
    @InjectView(R.id.roppongi)
    RelativeLayout roppongi;

    final private String[] shinjukuLatLon = {"35.658643", "139.7006439"};
    final private String[] shibuyaLatLon = {"35.6909959", "139.7037901"};
    final private String[] daikanyamaLatLon = {"35.6480324", "139.7029721"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.main_fragment, null);
        ButterKnife.inject(this, mainLayout);

        int displayWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int areaSize = (displayWidth - (int) (45 * getActivity().getResources().getDisplayMetrics().density)) / 2;

        LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(areaSize, areaSize);
        shinjuku.setLayoutParams(leftLp);
        daikanyama.setLayoutParams(leftLp);

        LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(areaSize, areaSize);
        int margin = (int) (15 * getActivity().getResources().getDisplayMetrics().density);
        rightLp.setMargins(margin, 0, 0, margin);
        shibuya.setLayoutParams(rightLp);
        roppongi.setLayoutParams(rightLp);

        return mainLayout;
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    private void intentToAreaDetail(String lat, String lon) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("PAGE", "AREA");
        intent.putExtra("LAT", lat);
        intent.putExtra("LON", lon);
        startActivity(intent);
    }

    @OnClick(R.id.shinjuku)
    public void tapShinjuku() {
        intentToAreaDetail(shinjukuLatLon[0], shinjukuLatLon[1]);
    }

    @OnClick(R.id.shibuya)
    public void tapShibuya() {
        intentToAreaDetail(shibuyaLatLon[0], shibuyaLatLon[1]);
    }

    @OnClick(R.id.daikanyama)
    public void tapDaikanyama() {
        intentToAreaDetail(daikanyamaLatLon[0], daikanyamaLatLon[1]);
    }

    @OnClick(R.id.roppongi)
    public void tapRoppongi() {
        String gpsStatus = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (gpsStatus.indexOf(LocationManager.NETWORK_PROVIDER) > 0) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        // 【重要】緯度経度が1度でも取得できた時点で処理を終了
        locationManager.removeUpdates(this);

        intentToAreaDetail(latitude, longitude);
    }

}
