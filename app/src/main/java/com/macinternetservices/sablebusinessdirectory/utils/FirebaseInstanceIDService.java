package com.macinternetservices.sablebusinessdirectory.utils;

import android.content.Intent;

/**
 * Sable Business Directory on 8/11/16.
 * Contact Email : admin@sablebusinessdirectory.com
 */
public class FirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {

        super.onNewToken(token);
        Utils.psLog("token : " + token);

        Intent in = new Intent();
        in.putExtra("message",token);
        in.setAction("NOW");

    }

}
