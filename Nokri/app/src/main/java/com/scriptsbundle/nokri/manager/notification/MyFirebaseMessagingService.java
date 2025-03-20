package com.scriptsbundle.nokri.manager.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;


import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by apple on 11/23/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                handleDataMessage(remoteMessage);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(RemoteMessage remoteMessage) {

        try {
            FireBaseNotificationModel model = new FireBaseNotificationModel();

            String title, message, topicId, topic, image = null;
            Date currentTime;

            JSONObject broadcast = new JSONObject(remoteMessage.getData().get("data"));
            Log.d("info broadcat", broadcast.toString());
            topic = remoteMessage.getData().get("topic");


            if (topic.equals("individual")) {
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("message");
                topicId = "null";
                image = "";
            } else {
                topicId = remoteMessage.getData().get("topic_id");
                title = broadcast.getString("title");
                message = broadcast.getString("message");
                image = broadcast.getString("image");
            }
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());

            model.setTitle(title);
            model.setMessage(message);
            model.setImage(image);
            model.setTopic(remoteMessage.getData().get("topic"));
            currentTime = Calendar.getInstance().getTime();


            if (TextUtils.isEmpty(image)) {
                //        settingsMain.setNotificationImage("");
                Nokri_SharedPrefManager.saveFirebaseNotificationImage(null, getApplicationContext());
                showNotificationMessage(getApplicationContext(), title, message, currentTime, remoteMessage.getData().get("topic_id"), image);
            } else {
                //       settingsMain.setNotificationImage(broadcast.getString("image_full"));
                Nokri_SharedPrefManager.saveFirebaseNotificationImage(broadcast.getString("image_full"), getApplicationContext());
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, currentTime, remoteMessage.getData().get("topic_id")
                        , image);
            }


            Nokri_SharedPrefManager.saveFireBaseNotification(model, getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Date timeStamp, String notification_id
            , String imageURL) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(title, message, timeStamp, notification_id, imageURL);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, Date timeStamp, String notification_id
            , String imageURL) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(title, message, timeStamp, notification_id, imageURL);
    }

}