package com.scriptsbundle.nokri.manager;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import java.util.HashMap;
import java.util.Map;

public class Nokri_GoogleAnalyticsManager {






 public enum Target {
 APP,
 // Add more trackers here if you need, and update the code in #get(Target) below
 }

 private static Nokri_GoogleAnalyticsManager sInstance;
 public static synchronized void initialize(Context context) {
 if (sInstance == null) {
 sInstance = new Nokri_GoogleAnalyticsManager(context);

 }

 }

 public static synchronized Nokri_GoogleAnalyticsManager getInstance() {
 if (sInstance == null) {
 throw new IllegalStateException("Call initialize() before getInstance()");
 }

 return sInstance;
 }

 private final Map<Target, Tracker> mTrackers = new HashMap<Target, Tracker>();
 private final Context mContext;



 private Nokri_GoogleAnalyticsManager(Context context) {
 mContext = context.getApplicationContext();
 }

 public synchronized Tracker get(Target target,String analyticsId) {

 if (!mTrackers.containsKey(target)) {
 GoogleAnalytics analytics;
 Tracker tracker;
 switch (target) {
 case APP:
 analytics = GoogleAnalytics.getInstance(mContext);
 tracker = analytics.newTracker(""+analyticsId);
 tracker.enableExceptionReporting(true);
 tracker.enableAdvertisingIdCollection(true);
 tracker.setSessionTimeout(300);
 Log.d("tracket",analytics+analyticsId);
 break;
 default:
 throw new IllegalArgumentException("Unhandled analytics target " + target);
 }
 mTrackers.put(target, tracker);
 }

 return mTrackers.get(target);
 }

 public synchronized Tracker getGoogleAnalyticsTracker() {
     Nokri_GoogleAnalyticsManager analyticsTrackers = Nokri_GoogleAnalyticsManager.getInstance();
 return analyticsTrackers.get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);
 }
 public void trackScreenView(String screenName) {
 Tracker t = getGoogleAnalyticsTracker();

 // Set screen name.
 t.setScreenName(screenName);

 // Send a screen view.
 t.send(new HitBuilders.ScreenViewBuilder().build());

 GoogleAnalytics.getInstance(mContext).dispatchLocalHits();
 }
 }

