package com.scriptsbundle.nokri.manager.notification;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.squareup.picasso.Picasso;

/**
 * Created by GlixenTech on 3/16/2018.
 */

public class Nokri_NotificationPopup {
    public static void showNotificationDialog(Context context,String title, String message, String image) {

        final Dialog dialog;
        dialog = new Dialog(context, R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_notification_layout);
        dialog.findViewById(R.id.header).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        ImageView imageView = dialog.findViewById(R.id.notificationImage);
        TextView tv_title = dialog.findViewById(R.id.notificationTitle);
        TextView tV_message = dialog.findViewById(R.id.notificationMessage);
        Button button = dialog.findViewById(R.id.cancel_button);
        button.setText("Cancel");
        button.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        //button.setBackgroundColor(Color.parseColor("#E1E2E2"));


      //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));


        if (!TextUtils.isEmpty(image)) {
            Picasso.get().load(image)


                    .into(imageView);
        Log.d("tagggg",image);
        }

        tv_title.setText(title);
        tV_message.setText(message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
