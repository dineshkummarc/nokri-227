package com.scriptsbundle.nokri.manager;

import com.scriptsbundle.nokri.activities.NokriSplashActivity;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Glixen Technologies on 11/01/2018.
 */

public class Nokri_RequestHeaderManager {

    public static Map<String,String> addHeaders(){
        Map<String,String> map = new HashMap<>();
        map.put("Nokri-Lang-Locale", NokriSplashActivity.language);
        map.put("Purchase-Code", Nokri_Config.PURCHASE_CODE);
        map.put("Custom-Security", Nokri_Config.CUSTOM_SECURITY);
        map.put("Content-Type", "application/json");
        map.put("Nokri-Request-From","android");
        return map;
    }



    public static Map<String,String> addSocialHeaders(){
        Map<String,String> map = new HashMap<>();
        map.put("NOKRi-LOGIN-TYPE", "social");
        map.put("Nokri-Lang-Locale", NokriSplashActivity.language== null?"":NokriSplashActivity.language);
        map.put("Purchase-Code", Nokri_Config.PURCHASE_CODE);
        map.put("Custom-Security", Nokri_Config.CUSTOM_SECURITY);
        map.put("Content-Type", "application/json");
        map.put("Nokri-Request-From","android");
        return map;
    }

    public static Map<String, String> UploadImageAddHeaders() {
        Map<String, String> map = new HashMap<>();
      /*  if (SettingsMain.isSocial(context)) {
            map.put("AdForest-Login-Type", "social");
        }*/
        map.put("Nokri-Request-From","android");
        map.put("Nokri-Lang-Locale", NokriSplashActivity.language);
        map.put("Purchase-Code", Nokri_Config.PURCHASE_CODE);
        map.put("custom-security", Nokri_Config.CUSTOM_SECURITY);
        map.put("Cache-Control", "max-age=640000");
        // map.put("Content-Type", "multipart/form-data");
        return map;
    }


    public static Map<String, String> UploadImageAddSocial() {
        Map<String, String> map = new HashMap<>();
      /*  if (SettingsMain.isSocial(context)) {

        }*/
        map.put("Nokri-Request-From","android");
        map.put("NOKRi-LOGIN-TYPE", "social");
        map.put("Nokri-Lang-Locale", NokriSplashActivity.language);
        map.put("Purchase-Code", Nokri_Config.PURCHASE_CODE);
        map.put("custom-security", Nokri_Config.CUSTOM_SECURITY);
        map.put("Cache-Control", "max-age=640000");
        // map.put("Content-Type", "multipart/form-data");
        return map;
    }
}
