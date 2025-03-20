package com.scriptsbundle.nokri.manager.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.employeer.dashboard.Nokri_EmployerDashboardActivity;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
//    SettingsMain settingsMain;
    private Context mContext;

    private String CHANNEL_ID = "scriptsbundlet";
    private String CHANNEL_NAME = "Notifications";
    NotificationUtils(Context mContext) {
        this.mContext = mContext;
  //      settingsMain = new SettingsMain(mContext);
    }

    /**
     * Method checks if the app is in background or not
     */
//    public static boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//
//        return isInBackground;
//    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

//    void showNotificationMessage(final String title, final String message, final Date timeStamp, String notification_id, String imageURL) {
////        if (TextUtils.isEmpty(message))
////            return;
//
//        // notification icon
//        PendingIntent resultPendingIntent = null;
//        final int icon = R.drawable.logo;
//            if (Nokri_Utils.isAppinBackground(mContext)) {
//                Intent in = null;
//                if(Nokri_SharedPrefManager.isAccountCandidate(mContext)) {
//                    in = new Intent(mContext, Nokri_CandidateDashboardActivity.class);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                }
//                else if(Nokri_SharedPrefManager.isAccountEmployeer(mContext)) {
//                    in = new Intent(mContext, Nokri_EmployeerDashboardActivity.class);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                }
//                else if(Nokri_SharedPrefManager.isAccountPublic(mContext)) {
//                    in = new Intent(mContext, Nokri_GuestDashboardActivity.class);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                }
//                //Intent in = new Intent(mContext, HomeActivity.class);
//                //settingsMain.setNotificationTitle(title);
//                Nokri_SharedPrefManager.saveFirebaseNotificationTitle(title,mContext);
//                //settingsMain.setNotificationMessage(message);
//                Nokri_SharedPrefManager.saveFirebaseNotificationMessage(message,mContext);
//                resultPendingIntent = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
//
//          }
//            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                    mContext);
//            if (!TextUtils.isEmpty(imageURL)) {
//
//                if (imageURL != null && imageURL.length() > 4 && Patterns.WEB_URL.matcher(imageURL).matches()) {
//
//                    Bitmap bitmap = getBitmapFromURL(imageURL);
//
//                    if (bitmap != null) {
//                        showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
//                    } else {
//                        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
//                    }
//                }
//            } else {
//                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
//            }
//
//    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, Date timeStamp, PendingIntent resultPendingIntent, String ad_ID) {

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        inboxStyle.bigText(message);
//        inboxStyle.addLine(message);

        Notification notification;

     if(!Nokri_Utils.isAppinBackground(mContext)){

         notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                 .setAutoCancel(true)
                 .setContentTitle(title)

                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setStyle(inboxStyle)
                 .setWhen(timeStamp.getTime())
                 .setSmallIcon(R.drawable.logo)
                 .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                 .setContentText(message)
//                .setContentInfo("contentinfo")
//                .setSubText(message)
                 .build();
     }

     else { Log.d("tagggg","not running");
         notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                 .setAutoCancel(true)
                 .setContentTitle(title)
                 .setContentIntent(resultPendingIntent)
                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setStyle(inboxStyle)
                 .setWhen(timeStamp.getTime())
                 .setSmallIcon(R.drawable.logo)
                 .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                 .setContentText(message)
//                .setContentInfo("contentinfo")
//                .setSubText(message)
                 .build();
     }
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(ad_ID), notification);
        Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = true;
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title,
                                     String message, Date timeStamp, PendingIntent resultPendingIntent, String notification_id) {

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        if(!Nokri_Utils.isAppinBackground(mContext))
        {
            Log.d("tagggg","running");
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)

                    .setStyle(bigPictureStyle)
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();

        }
        else { Log.d("tagggg","not running");
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(bigPictureStyle)
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(notification_id), notification);
        Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = true;
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    void playNotificationSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mContext, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + mContext.getPackageName() + "/raw/notification");
//            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    public void createChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // create android channel
            NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);
        } else {

        }
    }


    void showNotificationMessage(final String title, final String message, final Date timeStamp, String notification_id, String imageURL) {
//        if (TextUtils.isEmpty(message))
//            return;

        // notification icon
        PendingIntent resultPendingIntent = null;
        final int icon = R.drawable.logo;
        if (Nokri_Utils.isAppinBackground(mContext)) {
            Intent in = null;
            if(Nokri_SharedPrefManager.isAccountCandidate(mContext)) {
                in = new Intent(mContext, Nokri_CandidateDashboardActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            else if(Nokri_SharedPrefManager.isAccountEmployeer(mContext)) {
                in = new Intent(mContext, Nokri_EmployerDashboardActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            else if(Nokri_SharedPrefManager.isAccountPublic(mContext)) {
                in = new Intent(mContext, Nokri_GuestDashboardActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
//                Intent in = new Intent(mContext, HomeActivity.class);
            //settingsMain.setNotificationTitle(title);
            Nokri_SharedPrefManager.saveFirebaseNotificationTitle(title,mContext);
            //settingsMain.setNotificationMessage(message);
            Nokri_SharedPrefManager.saveFirebaseNotificationMessage(message,mContext);
            resultPendingIntent = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext, CHANNEL_ID);
        createChannels();

        if (!TextUtils.isEmpty(imageURL)) {

            if (imageURL != null && imageURL.length() > 4 && Patterns.WEB_URL.matcher(imageURL).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageURL);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
                }
            }
        } else {

            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
        }

    }
    NotificationManager mManager;
    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}
