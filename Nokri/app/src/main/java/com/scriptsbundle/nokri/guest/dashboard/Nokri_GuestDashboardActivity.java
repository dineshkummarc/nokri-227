package com.scriptsbundle.nokri.guest.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.activities.Nokri_SigninActivity;
import com.scriptsbundle.nokri.activities.Nokri_SignupActivity;
import com.scriptsbundle.nokri.guest.blog.fragments.Nokri_BlogGridFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_FeaturedJobsFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_Home2ScreenFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_HomeScreenFragment;


import com.scriptsbundle.nokri.guest.home.fragments.Nokri_RecentJobsFragment;
import com.scriptsbundle.nokri.guest.settings.fragments.Nokri_SettingsFragment;
import com.scriptsbundle.nokri.manager.Nokri_AdManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.manager.notification.FireBaseNotificationModel;
import com.scriptsbundle.nokri.manager.notification.Nokri_NotificationPopup;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.search.fragments.Nokri_JobSearchFragment;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import com.squareup.picasso.Picasso;

public class Nokri_GuestDashboardActivity extends AppCompatActivity implements Nokri_PopupManager.ConfirmInterface {
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private Nokri_FontManager fontManager;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private LinearLayout toolbarTitleContainer;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    public CircularImageView profileImage;
    private Nokri_GuestDashboardModel guestDashboardModel;
    private Nokri_PopupManager popupManager;
    private LinearLayout bottomAdContainer,topAdContainer;

    boolean doubleBackToExitPressedOnce = false;
    private Fragment fragment;
    private Class fragmentClass;
    String ad_title, requestFrom, ad_id;


    @Override
    protected void onResume() {
        super.onResume();

       try {
           Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
       }
       catch (Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Nokri_Config.APP_COLOR = Nokri_SharedPrefManager.getAppColor(this);
        setContentView(R.layout.activity_nokri_guest_dashboard);
        Nokri_GoogleAnalyticsManager.initialize(this);
        Nokri_FeaturedJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_RecentJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);
        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        guestDashboardModel = Nokri_SharedPrefManager.getGuestSettings(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTextView = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleContainer = findViewById(R.id.toolbar_title_container);

        toolbar.findViewById(R.id.collapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) findViewById(R.id.collapse);
                ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
                imageViewRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();

                        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
                    }
                });

                imageView.setVisibility(View.VISIBLE);


                Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();

                androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
//                Nokri_ToastManager.showLongToast(Nokri_GuestDashboardActivity.this,"sddfas");
            }
        });

        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);



        FireBaseNotificationModel fireBaseNotificationModel = Nokri_SharedPrefManager.getFirebaseNotification(this);
        if(fireBaseNotificationModel!=null)
        {
            if(!fireBaseNotificationModel.getTitle().trim().isEmpty()&& Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION){

                Nokri_NotificationPopup.showNotificationDialog(this,fireBaseNotificationModel.getTitle(),fireBaseNotificationModel.getMessage(),fireBaseNotificationModel.getImage());
                Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = false;
            }
        }

    //    Nokri_SharedPrefManager.saveAccountType("public",this);

        bottomAdContainer = findViewById(R.id.bottom_ad_container);
        topAdContainer = findViewById(R.id.top_ad_container);




    if(Nokri_Globals.SHOW_AD) {


        if(Nokri_Globals.IS_BANNER_EBABLED){
            if (Nokri_Globals.SHOW_AD_TOP) {

                Nokri_AdManager.nokri_displaybanners(this, topAdContainer);
            }

            if (!Nokri_Globals.SHOW_AD_TOP) {

                Nokri_AdManager.nokri_displaybanners(this, bottomAdContainer);
            }

        }


        if(Nokri_Globals.IS_INTERTIAL_ENABLED)
        Nokri_AdManager.loadInterstitial(this);

    }



        View headerView = navigationView.getHeaderView(0); // 0-index header

        TextView navigationHeaderTexrView = headerView.findViewById(R.id.txt_nav_header);
        profileImage = headerView.findViewById(R.id.img_profile);

        navigationHeaderTexrView.setText(guestDashboardModel.getGuestName());
        if(!TextUtils.isEmpty(guestDashboardModel.getCandidateDp()))
        Picasso.get().load(guestDashboardModel.getCandidateDp()).fit().centerCrop().into(profileImage);
        fontManager = new Nokri_FontManager();

        fontManager.nokri_setMonesrratSemiBioldFont(toolbarTitleTextView,getAssets());
        nokri_setNavigationFont(navigationView.getMenu());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")){
            Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
            //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
            fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();

        }
        else
        {
            Fragment homeScreen2Fragment=new Nokri_Home2ScreenFragment();
            fragmentTransaction.add(R.id.fragment_placeholder,homeScreen2Fragment).commit();


        }
        toolbarTitleTextView.setText("Home");

        setUpNavigationView();
        nokri_setDrawerMenuText(navigationView.getMenu());

        popupManager = new Nokri_PopupManager(this,this);





        Drawable mDrawable = getResources().getDrawable(R.drawable.drawer_highlight);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN));
        navigationView.setItemBackground(mDrawable);

        Nokri_Utils.changeSystemBarColor(this);
        headerView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
      //  profileImage.setShadowColor(Color.parseColor(Nokri_Config.APP_COLOR));
        profileImage.setBorderColor(Color.parseColor(Nokri_Config.APP_COLOR));
    }
    private void nokri_setDrawerMenuText(Menu menu){

        MenuItem home = menu.findItem(R.id.nav_home);
        MenuItem explore = menu.findItem(R.id.nav_explore);
        MenuItem signin = menu.findItem(R.id.nav_singin);
        MenuItem signup = menu.findItem(R.id.nav_singup);
        MenuItem exit = menu.findItem(R.id.nav_exit);
        MenuItem blog = menu.findItem(R.id.nav_blog);
        MenuItem settings = menu.findItem(R.id.nav_settings);
       // MenuItem candidateSearch = menu.findItem(R.id.nav_candidate_search);
        home.setTitle(guestDashboardModel.getHome());
        explore.setTitle(guestDashboardModel.getExplore());
        signin.setTitle(guestDashboardModel.getSignin());
        signup.setTitle(guestDashboardModel.getSignup());
        exit.setTitle(guestDashboardModel.getExit());
        blog.setTitle(guestDashboardModel.getBlog());
        settings.setTitle(guestDashboardModel.getSettings());

      //  candidateSearch.setTitle(guestDashboardModel.getCandidateSearch());
       /* Nokri_NotificationPopup.showNotificationDialog(this,Nokri_SharedPrefManager.getFirebaseNotificationTitme(this),
                Nokri_SharedPrefManager.getFirebaseNotificationMessage(this),
                Nokri_SharedPrefManager.getFirebaseNotificationImage(this));*/

    }
    private void nokri_setNavigationFont(Menu m){

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
      /*      SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
*/
            //the method we have create in activity
            fontManager.nokri_applyFontToMenuItem(mi,getAssets());
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar
    }

    private void doSearch() {

        Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();

        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
    }
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Check to see which item was being clicked and perform appropriate action
                int i = menuItem.getItemId();//Replacing the main content with ContentFragment Which is our Inbox View;
                if (i == R.id.nav_home) {
                    if(Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")){
                    fragmentClass = Nokri_HomeScreenFragment.class;
                    toolbarTitleTextView.setText(guestDashboardModel.getHome());}
                    else if(Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("2")){
                        fragmentClass=Nokri_Home2ScreenFragment.class;
                    }
                } else if (i == R.id.nav_explore) {
                    fragmentClass = Nokri_JobSearchFragment.class;
                    toolbarTitleTextView.setText(guestDashboardModel.getExplore());

                        /*     case R.id.nav_candidate_search:
                        fragmentClass = Nokri_CandidateSearchFragment.class;
                        toolbarTitleTextView.setText(guestDashboardModel.getCandidateSearch());

                        break;*/
                } else if (i == R.id.nav_blog) {
                    fragmentClass = Nokri_BlogGridFragment.class;
                    toolbarTitleTextView.setText(guestDashboardModel.getBlog());
                } else if (i == R.id.nav_singin) {
                    fragmentClass = null;
                    startActivity(new Intent(Nokri_GuestDashboardActivity.this, Nokri_SigninActivity.class));
                    finish();
                } else if (i == R.id.nav_singup) {
                    fragmentClass = null;
                    startActivity(new Intent(Nokri_GuestDashboardActivity.this, Nokri_SignupActivity.class));
                    finish();
                } else if (i == R.id.nav_settings) {
                    fragmentClass = Nokri_SettingsFragment.class;
                } else if (i == R.id.nav_exit) {
                    fragmentClass = null;
                    popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
                }


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                drawer.closeDrawers();
                return true;

            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                    if(fragmentClass!=null){
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(fragment!=null){
                            androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();}
                   fragmentClass = null; }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        navigationView.getMenu().getItem(0).setChecked(true);
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        else{
            if(getSupportFragmentManager().getBackStackEntryCount() == 0)
            {      if(findViewById(R.id.filter_reset_container).getVisibility() == View.VISIBLE)
            {findViewById(R.id.filter_reset_container).setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                return;}

                if (doubleBackToExitPressedOnce) {
                if(popupManager!=null)
                popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
                return;
            }

                this.doubleBackToExitPressedOnce = true;
                Nokri_ToastManager.showShortToast(this, Nokri_Globals.ON_BACK_EXIT_TEXT);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
                //drawer.openDrawer(GravityCompat.START);
            }

            else
                getSupportFragmentManager().popBackStack();
        }
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        //  super.onBackPressed();
    }



    @Override
    public void onConfirmClick(Dialog dialog) {
        dialog.dismiss();
        finish();
    }


}
