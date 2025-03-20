package com.scriptsbundle.nokri.manager;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Glixen Technologies on 11/01/2018.
 */

public class Nokri_ToastManager {
    public static void showLongToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
