package com.example.horie.hack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by nissiy on 2014/09/06.
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        Intent intent = getIntent();
        String page = (intent.getStringExtra("PAGE") != null) ? intent.getStringExtra("PAGE") : "";

        if (page.equals("AREA")) {
            Bundle argsArea = new Bundle();
            argsArea.putString("LAT", intent.getStringExtra("LAT"));
            argsArea.putString("LON", intent.getStringExtra("LON"));
            AreaFragment areaFragment = new AreaFragment();
            areaFragment.setArguments(argsArea);
            changeFragment(areaFragment);
        } else {
            changeFragment(new MainFragment());
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
