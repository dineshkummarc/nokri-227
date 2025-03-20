//package com.scriptsbundle.nokri.manager.notification;
//
//import android.content.Intent;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.google.firebase.messaging.FirebaseMessagingService;
//
//import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
//import com.scriptsbundle.nokri.utils.Nokri_Config;
//
//
///**
// * Created by apple on 11/23/17.
// */
//
//public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
//    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
//
//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        // Saving reg id to shared preferences
//         Nokri_SharedPrefManager.saveFirebaseNotificationToken(refreshedToken,getApplicationContext());
//        // sending reg id to your server
//        sendRegistrationToServer(refreshedToken);
//
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(Nokri_Config.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
//        Log.e("FireBase",refreshedToken);
//
//    }
//
//    private void sendRegistrationToServer(final String token) {
//        // sending gcm token to server
//        Log.e(TAG, "sendRegistrationToServer: " + token);
//    }
//
//   /* private void storeRegIdInPref(String token) {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF,0);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("Firebase Regid", token);
//        Log.e("FireBase",token);
//        editor.commit();
//    }*/
//
//}