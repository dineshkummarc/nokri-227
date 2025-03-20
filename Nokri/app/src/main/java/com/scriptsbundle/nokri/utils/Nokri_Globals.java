package com.scriptsbundle.nokri.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Glixen Technologies on 24/01/2018.
 */

public class Nokri_Globals {

    public  static String EDIT_MESSAGE;
    public static String EMPTY_FIELDS_PLACEHOLDER = "All Fields Are Mandatory";
    public static  String NO_URL_FOUND = "No Url Found";
    public static  String COMMENT_REQUIRED_TEXT = "Please write a comment first";
    public static  String REQUIRED_REPY_TEXT = "Please write a reply first";
    public static  String REQUIRED_Employee_Login_TEXT = "Please login as employer";

    public static  String APP_NOT_FOUNT = "No App Found To Open This File";
    public static  String EXIT_TEXT = "Exit?";
    public static String INVALID_EMAIL = "Email Format Not Valid";
    public static String TERMS_AND_SERVICES = "You Must Agree With Our Term And Services";
    public static String ON_BACK_EXIT_TEXT = "Please click BACK again to exit";
    public static String SELECT_VALID_SKILL = "Please Select A Valid Skill From The Droprown";
    public static  String SELECT_SKILL = "Please Select At Least One Skill";
    public static String LOGIN_FIRST = "";
    public static String NEXT_STEP = "";
    public static String APP_NOT_INSTALLED = "This app is not installed";
    public static String INVALID_URL = "invalid url";
    public static boolean IS_RTL_ENABLED = false;
    public static  String AD_ID = "";
    public static  String INTERTIAL_ID = "";
    public static boolean  IS_INTERTIAL_ENABLED = false;
    public static boolean IS_BANNER_EBABLED = false;
    public static  long AD_INITIAL_TIME = 10;
    public static  long AD_DISPLAY_TIME  = 60;
    public static boolean SHOW_AD_TOP = false;
    public static boolean SHOW_AD = false;
    public static boolean SHOULD_HOW_FIREBASE_NOTIFICATION = false;
    public static String PLEASE_WAIT_TEXT = "";
    public static String JOB_SEARCH_PLACEHOLER = "";
    public static String tagline = "Over 1 millions jobs resume pools";
    public static String JOB_ALERTS_TAGLINE = "Over 1 millions jobs resume pools";
    public static String BACK_STRING = "Back";
    public static String NEXT_STRING = "Next";
    public static String POST_JOB_STRING = "Post Job";
    public static int DESIGN_TYPE = 2;
    public static String LinkedInValidUrl = "Please enter a valid linked in url";
    public static String LinkedInHint = "Enter your LinkedIn public profile url here";
    public static String LinkedInUrl = "";
    public static boolean showEmployerMap = false;
    public static boolean showCandidateMap = false;
    public static String SOMETHING_WENT_WRONG = "Something Went Wrong!";


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }
}





