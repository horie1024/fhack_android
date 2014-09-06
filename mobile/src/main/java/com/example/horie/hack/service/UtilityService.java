package com.example.horie.hack.service;

import android.app.IntentService;
import android.content.Intent;

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
        // TODO: ゴリゴリ実装

    }


}
