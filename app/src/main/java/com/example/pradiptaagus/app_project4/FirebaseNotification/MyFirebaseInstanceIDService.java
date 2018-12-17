package com.example.pradiptaagus.app_project4.FirebaseNotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //For registration of token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if (refreshedToken != null) {
            SharedPreferences userPreference = this.getSharedPreferences("fire_base", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userPreference.edit();

            //save token to shared preference
            editor.putString("fcm_token", refreshedToken);
            editor.apply();
        }

        //To displaying token on logcat
        Log.d("TOKEN: ", refreshedToken);
    }
}
