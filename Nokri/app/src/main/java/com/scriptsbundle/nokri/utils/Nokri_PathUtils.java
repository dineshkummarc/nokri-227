package com.scriptsbundle.nokri.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.scriptsbundle.nokri.utils.models.permissionsModel;

/**
 * Created by Glixen Technologies on 11/01/2018.
 */

public class Nokri_PathUtils {
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

        public static String getPath(final Context context, final Uri uri) {

            // DocumentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

                if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    String storageDefinition;


                    if("primary".equalsIgnoreCase(type)){

                        return Environment.getExternalStorageDirectory() + "/" + split[1];

                    } else {

                        if(Environment.isExternalStorageRemovable()){
                            storageDefinition = "EXTERNAL_STORAGE";

                        } else{
                            storageDefinition = "SECONDARY_STORAGE";
                        }

                        return System.getenv(storageDefinition) + "/" + split[1];
                    }

                } else if (isDownloadsDocument(uri)) {// DownloadsProvider

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);

                } else if (isMediaDocument(uri)) {// MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }

            } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);

            } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
                return uri.getPath();
            }

            return null;
        }

        public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

    public static void reload(Context context, String tag) {
        Fragment frg;
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

        frg = manager.findFragmentByTag(tag);
        final FragmentTransaction ft = manager.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
ft.remove(frg);
        ft.commit();
    }
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }


        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    public static permissionsModel getPermissionsModel() {


        Gson gson = new Gson();
        String permissionsModel = pref.getString("permissionsModel", null);
        permissionsModel model = gson.fromJson(permissionsModel, permissionsModel.class);
        return model;
    }

    public static void setPermissionsModel(permissionsModel permissionsModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(permissionsModel);
        editor.putString("permissionsModel", toJson);
        editor.apply();

    }

    }
