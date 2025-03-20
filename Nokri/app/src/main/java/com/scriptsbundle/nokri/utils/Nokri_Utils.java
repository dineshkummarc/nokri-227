package com.scriptsbundle.nokri.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.CustomBorderDrawable;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.manager.receiver.Nokri_NetwordStateManager;
import com.scriptsbundle.nokri.utils.models.Nokri_EditTextModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Glixen Technologies on 02/01/2018.
 */

public class Nokri_Utils {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0, NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 2;
    public static boolean isCallRunning = false;



    public static Drawable getColoredXml(Context context ,int id){
        Drawable drawable = context.getResources().getDrawable(id).mutate();
        drawable.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }


    public static Drawable getColoredXmlGreen(Context context ,int id){
        Drawable drawable = context.getResources().getDrawable(id).mutate();
        drawable.setColorFilter(Color.parseColor("#34C759"), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }
    public static void disableActions(Activity context){
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public static void enableActions(Activity context){
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void setBordederButton(Context context,Button button){
        button.setBackground(CustomBorderDrawable.customButton(130, 130, 130, 130, Nokri_Config.APP_COLOR, "#00000000",  Nokri_Config.APP_COLOR, 2));
        button.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
    }
    // A class for all utiltiy methods
    public static  void setEditBorderButton(Context context,Button button){
        Drawable drawable = context.getResources().getDrawable(R.drawable.forward_arrow).mutate();
        drawable.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_ATOP);
        drawable.setBounds(
                0, // left
                0, // top
                drawable.getIntrinsicWidth(), // right
                drawable.getIntrinsicHeight() // bottom
        );

        button.setBackground(CustomBorderDrawable.customButton(80, 80, 80, 80, Nokri_Config.APP_COLOR, "#00000000",  Nokri_Config.APP_COLOR, 2));
        button.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        button.setCompoundDrawables(null,null,drawable,null);
    }
    public static void generateKeyhash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    public static Drawable getXmlDrawable( Drawable drawable){

        drawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC));
       return drawable;
    }
    public static void setXmlDrawableSingleLayer(Context context,View view,int drawableId){
        LayerDrawable layerDrawable = (LayerDrawable) context.getResources().getDrawable(drawableId);
        layerDrawable.setDrawableByLayerId(layerDrawable.getId(0),getXmlDrawable(context.getResources().getDrawable(drawableId)));
        view.setBackground(layerDrawable);
    }
    public static void changeSystemBarColor(Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
    }
    public static Spanned stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
    public static void setTextViewHtml(TextView textView,String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        }
        else
            textView.setText(Html.fromHtml(text));

    }
    public static void opeInBrowser(Context context, String uri) {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(browserIntent);
        } catch (Exception e) {
            Nokri_ToastManager.showLongToast(context, "Invalid Url");
        }

    }
    public static void setRoundButtonColor(Context context ,View button){

        Drawable mDrawable = context.getResources().getDrawable(R.drawable.rounded_button);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC));
        button.setBackground(mDrawable);
    }
    public static void setRoundButtonColorRed(Context context ,View button){

        Drawable mDrawable = context.getResources().getDrawable(R.drawable.rounded_button);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor("#e90006"), PorterDuff.Mode.SRC));
        button.setBackground(mDrawable);
    }

    public static void setRoundButtonColorCustom(Context context ,View button,String color){

        Drawable mDrawable = context.getResources().getDrawable(R.drawable.rounded_button);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC));
        button.setBackground(mDrawable);
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        int status = 0;
        if (conn == TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public static void enableInternetReceiver(Context context) {
        ComponentName component = new ComponentName(context, Nokri_NetwordStateManager.class);

        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableInternetReceiver(Context context) {
        ComponentName component = new ComponentName(context, Nokri_NetwordStateManager.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public static boolean isInternetReceiverEnabled(Context context) {
        ComponentName component = new ComponentName(context, Nokri_NetwordStateManager.class);
        int status = context.getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            return true;
        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            return false;
        }
        return false;

    }

public static boolean manageEmptyEditText(Context context,ArrayList<Nokri_EditTextModel>modelList){

        for(int i=0;i<modelList.size();i++){
            if(modelList.get(i).isRequired() && modelList.get(i).getEditText().getText().toString().isEmpty()) {
                modelList.get(i).getEditText().setError("!");
                Nokri_ToastManager.showLongToast(context, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                return false;
            }
            }
        return true;
    }
public static void checkEditTextForError(EditText editText){
    if(editText.getText().toString().trim().isEmpty()){
        editText.setError("!");
       // Nokri_ToastManager.showLongToast(context, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
    }
}
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void setMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public static String getBoldBlockQuote(String text){
        try {
            String textInsideBlockQuote = text.substring(0, text.indexOf("<blockquote") + 12) + "<b>" + text.substring(text.indexOf("<blockquote") + 12, text.indexOf("</blockquote")) + "</b>" + text.substring(text.indexOf("</blockquote"));
            return textInsideBlockQuote;
        }
        catch (StringIndexOutOfBoundsException e){
            return text;
        }

    }
    public static void turnSystemFontsOff(Activity activity){
        Configuration configuration = activity.getResources().getConfiguration();
        configuration.fontScale=(float) 1; //0.85 small size, 1 normal size, 1,15 big etc

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }
    public static boolean isAppInstalled(Context context,String packageName){

        try{
            context.getPackageManager().getPackageInfo(packageName,0);
            return true;
        }
        catch (PackageManager.NameNotFoundException ex){
            return false;
        }
    }
    public static boolean isAppinBackground(final Context context) {   boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
