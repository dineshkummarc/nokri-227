package com.scriptsbundle.nokri.employeer.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.activities.Nokri_MainActivity;
import com.scriptsbundle.nokri.activities.PostJob;
import com.scriptsbundle.nokri.activities.WPML.WPMLModel;
import com.scriptsbundle.nokri.activities.WPML.WpmlActivity;
import com.scriptsbundle.nokri.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.scriptsbundle.nokri.employeer.EmployerSearch.EmployerSearch;
import com.scriptsbundle.nokri.employeer.dashboard.fragments.NokriEmployeerDashboardFragment;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.edit.fragments.Nokri_CompanyEditProfileFragment;
import com.scriptsbundle.nokri.employeer.email.fragments.Nokri_EditEmailTemplate;
import com.scriptsbundle.nokri.employeer.follow.fragments.Nokri_EmployeeFollowingFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes.MatchedResume;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobsFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_PostJobFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumesFragment;
import com.scriptsbundle.nokri.employeer.payment.fragments.Nokri_PackageDetailFragment;
import com.scriptsbundle.nokri.employeer.payment.fragments.Nokri_PricingTableFragment;
import com.scriptsbundle.nokri.guest.blog.fragments.Nokri_BlogGridFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_FeaturedJobsFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_Home2ScreenFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_HomeScreenFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_RecentJobsFragment;
import com.scriptsbundle.nokri.guest.search.fragments.Nokri_CandidateSearchFragment;
import com.scriptsbundle.nokri.guest.search.fragments.Nokri_JobSearchFragment;
import com.scriptsbundle.nokri.guest.settings.fragments.Nokri_SettingsFragment;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_AdManager;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
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
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.Res;

import com.squareup.picasso.Picasso;

/**
 * Created by Glixen Technologies on 27/01/2018.
 */

public class Nokri_EmployerDashboardActivity extends AppCompatActivity implements Nokri_PopupManager.ConfirmInterface {
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private Nokri_FontManager fontManager;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private LinearLayout toolbarTitleContainer;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private CircularImageView profileImage;
    private Nokri_EmployeerDashboardModel employeerDashboardModel;
    private LinearLayout bottomAdContainer, topAdContainer;

    boolean doubleBackToExitPressedOnce = false;
    private Nokri_PopupManager popupManager;
    private Fragment fragment;
    private Class fragmentClass;
    private Nokri_DialogManager dialogManager;
    boolean calledFromPostJobCustom = false;

    private Res res;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public Resources getResources() {
        if (res == null) {
            res = new Res(super.getResources());
        }
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nokri_Config.APP_COLOR = Nokri_SharedPrefManager.getAppColor(this);
        setContentView(R.layout.activity_employeer_dashboard);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        calledFromPostJobCustom = getIntent().getBooleanExtra("calledFromCustom", false);


        Nokri_FeaturedJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_RecentJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        FireBaseNotificationModel fireBaseNotificationModel = Nokri_SharedPrefManager.getFirebaseNotification(this);

        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);


        if (fireBaseNotificationModel != null) {
            if (!fireBaseNotificationModel.getTitle().trim().isEmpty() && Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION) {

                Nokri_NotificationPopup.showNotificationDialog(this, fireBaseNotificationModel.getTitle(), fireBaseNotificationModel.getMessage(), fireBaseNotificationModel.getImage());
                Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = false;
            }
        }

        popupManager = new Nokri_PopupManager(this, this);
        employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTextView = findViewById(R.id.toolbar_title);
        toolbarTitleContainer = findViewById(R.id.toolbar_title_container);
        toolbar.findViewById(R.id.collapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) findViewById(R.id.collapse);
//                ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
                imageView.setVisibility(View.VISIBLE);
                ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
                imageViewRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();

                        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
                    }
                });


//                imageViewRefresh.setOnClickListener(v -> Nokri_GuestDashboardActivity.this.recreate());
//                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setDisplayShowHomeEnabled(true);
//                }
                Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();
//        fragment.setFilterText(edtSeach.getText().toString().trim());
//        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "";
                androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
//                Nokri_ToastManager.showLongToast(Nokri_CandidateDashboardActivity.this,"sddfas");
            }
        });
        bottomAdContainer = findViewById(R.id.bottom_ad_container);
        topAdContainer = findViewById(R.id.top_ad_container);

        if (Nokri_Globals.SHOW_AD) {


            if (Nokri_Globals.IS_BANNER_EBABLED) {
                if (Nokri_Globals.SHOW_AD_TOP) {

                    Nokri_AdManager.nokri_displaybanners(this, topAdContainer);
                }

                if (!Nokri_Globals.SHOW_AD_TOP) {

                    Nokri_AdManager.nokri_displaybanners(this, bottomAdContainer);
                }

            }


            if (Nokri_Globals.IS_INTERTIAL_ENABLED)
                Nokri_AdManager.loadInterstitial(this);

        }


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // 0-index header
        TextView navHeaderTextView = headerView.findViewById(R.id.txt_nav_header);
        TextView navEmailTextView = headerView.findViewById(R.id.txt_nav_email);
        if (Nokri_SharedPrefManager.getName(this) != null)
            navHeaderTextView.setText(Nokri_SharedPrefManager.getName(this));
        profileImage = headerView.findViewById(R.id.img_profile);

        if (!TextUtils.isEmpty(Nokri_SharedPrefManager.getProfileImage(this)))
            Picasso.get().load(Nokri_SharedPrefManager.getProfileImage(this)).fit().centerCrop().into(profileImage);


        if (Nokri_SharedPrefManager.getEmail(this) != null)
            navEmailTextView.setText(Nokri_SharedPrefManager.getEmail(this));


        fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(toolbarTitleTextView, getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(navHeaderTextView, getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(navEmailTextView, getAssets());
        nokri_setNavigationFont(navigationView.getMenu());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
//        //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
//        fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (calledFromPostJobCustom) {
            String jobId = getIntent().getStringExtra("job_id");

            Fragment jobDetailFragment = new Nokri_JobDetailFragment();
            Nokri_JobDetailFragment.CALLING_SOURCE = "applied";
            Bundle bundle = new Bundle();
            bundle.putString("job_id",jobId);
            jobDetailFragment.setArguments(bundle);
            fragmentTransaction.replace(findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).commit();

        } else {
            if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
                Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
                //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
                fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
                toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
            } else {
                Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();

                fragmentTransaction.add(R.id.fragment_placeholder, homeScreen2Fragment).commit();
                toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());

            }
        }

        setUpNavigationView();


        nokri_setDrawerMenuText(navigationView.getMenu());

     /*   Nokri_NotificationPopup.showNotificationDialog(this,Nokri_SharedPrefManager.getFirebaseNotificationTitme(this),
                Nokri_SharedPrefManager.getFirebaseNotificationMessage(this),
                Nokri_SharedPrefManager.getFirebaseNotificationImage(this));*/


        Drawable mDrawable = getResources().getDrawable(R.drawable.drawer_highlight);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN));
        navigationView.setItemBackground(mDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        headerView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        profileImage.setShadowColor(Color.parseColor(Nokri_Config.APP_COLOR));
        profileImage.setBorderColor(Color.parseColor(Nokri_Config.APP_COLOR));


    }

    private void nokri_setDrawerMenuText(Menu menu) {
        MenuItem dashboard = menu.findItem(R.id.nav_dashboard);
        MenuItem edit = menu.findItem(R.id.nav_editprofile);
        MenuItem email = menu.findItem(R.id.nav_email);
        MenuItem jobs = menu.findItem(R.id.nav_jobs);
        MenuItem followers = menu.findItem(R.id.nav_followers);
        MenuItem packageDetails = menu.findItem(R.id.nav_package_details);
        MenuItem buyPackage = menu.findItem(R.id.nav_buy_package);
        MenuItem savedResume = menu.findItem(R.id.nav_saved_resumes);
        MenuItem postJob = menu.findItem(R.id.nav_post_job);
        MenuItem blog = menu.findItem(R.id.nav_blog);
        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem exit = menu.findItem(R.id.nav_exit);
        MenuItem home = menu.findItem(R.id.nav_home);
        MenuItem settings = menu.findItem(R.id.nav_settings);
        MenuItem candidateSearch = menu.findItem(R.id.nav_candidate_search);
        MenuItem matchedResumes = menu.findItem(R.id.nav_matched_resumes);
        MenuItem employerSearch = menu.findItem(R.id.nav_employer_search);
        MenuItem changeLanguage = menu.findItem(R.id.nav_wpml);

        WPMLModel model = Nokri_SharedPrefManager.getWPMLSettings(getApplicationContext());
        if (model.langArray.size()==0){
            changeLanguage.setVisible(false);
        }
        changeLanguage.setTitle(employeerDashboardModel.getWpmlMenuText());
        dashboard.setTitle(employeerDashboardModel.getDashboard());
        edit.setTitle(employeerDashboardModel.getProfile());
        email.setTitle(employeerDashboardModel.getTemplates());
        followers.setTitle(employeerDashboardModel.getFollower());
        jobs.setTitle(employeerDashboardModel.getJobs());
        //post.setTitle(employeerDashboardModel.getPostJob());
        savedResume.setTitle(employeerDashboardModel.getSavedResumes());
        logout.setTitle(employeerDashboardModel.getLogout());
        blog.setTitle(employeerDashboardModel.getBlog());
        packageDetails.setTitle(employeerDashboardModel.getPackageDetail());
        buyPackage.setTitle(employeerDashboardModel.getBuyPackage());
        postJob.setTitle(employeerDashboardModel.getPostJob());
        exit.setTitle(employeerDashboardModel.getExit());
        home.setTitle(employeerDashboardModel.getHome());
        settings.setTitle(employeerDashboardModel.getSettings());
        matchedResumes.setTitle(employeerDashboardModel.getMatchedResume());
        employerSearch.setTitle(employeerDashboardModel.getEmployerSearch());

        candidateSearch.setTitle(employeerDashboardModel.getCandidateSearch());
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
//            Fragment homeScreenFragment = new NokriEmployeerDashboardFragment();
//            //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
//            fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
//            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
//        } else {
//            Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();
//            fragmentTransaction.add(R.id.fragment_placeholder, homeScreen2Fragment).commit();
//            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
//
//        }
    }

    private void nokri_setNavigationFont(Menu m) {
        for (int i = 0; i < m.size(); i++) {
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
            fontManager.nokri_applyFontToMenuItem(mi, getAssets());
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_job_listing, menu);
//        return super.onCreateOptionsMenu(menu);
//
//
//
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        mSearchAction = menu.findItem(R.id.action_search);
//        return super.onPrepareOptionsMenu(menu);
//    }

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

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar
//
//        if(isSearchOpened){ //test if the search is open
//
//            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
//            //action.setDisplayShowTitleEnabled(true); //show the title in the action bar
//            toolbarTitleContainer.setVisibility(View.VISIBLE);
//            //hides the keyboard
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
//
//            //add the search icon in the action bar
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_medium));
//            isSearchOpened = false;
//        } else { //open the search entry
//
//            action.setDisplayShowCustomEnabled(true); //enable it to display a
//            // custom view in the action bar.
//            action.setCustomView(R.layout.search_bar);//add the custom view
//            //  action.setDisplayShowTitleEnabled(false); //hide the title
//            toolbarTitleContainer.setVisibility(View.GONE);
//            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edittext_search); //the text editor
//            edtSeach.setHint(Nokri_Globals.JOB_SEARCH_PLACEHOLER);
//            fontManager.nokri_setOpenSenseFontEditText(edtSeach,getAssets());
//            //this is a listener to do a search when the user clicks on search button
//            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                        doSearch();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//
//            edtSeach.requestFocus();
//
//            //open the keyboard focused in the edtSearch
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
//
//
//            //add the close icon
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));
//
//            isSearchOpened = true;
//        }
    }

    private void doSearch() {

        Nokri_AllJobsFragment fragment = new Nokri_AllJobsFragment();
        fragment.setFilterText(edtSeach.getText().toString().trim());
        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "";
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
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
//                        fragmentClass = Nokri_HomeScreenFragment.class;
                        if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
                            fragmentClass = Nokri_HomeScreenFragment.class;

                        } else if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("2")) {
                            fragmentClass = Nokri_Home2ScreenFragment.class;

                        }
                        break;
                    case R.id.nav_dashboard:
                        fragmentClass = NokriEmployeerDashboardFragment.class;

                        break;
                    case R.id.nav_editprofile:
                        fragmentClass = Nokri_CompanyEditProfileFragment.class;

                        break;
                    case R.id.nav_email:
                        fragmentClass = Nokri_EditEmailTemplate.class;

                        break;
                    case R.id.nav_jobs:
                        fragmentClass = Nokri_JobsFragment.class;

                        break;
                    case R.id.nav_package_details:
                        fragmentClass = Nokri_PackageDetailFragment.class;

                        break;
                    case R.id.nav_buy_package:
                        fragmentClass = Nokri_PricingTableFragment.class;
                        Nokri_PricingTableFragment.calledFromCandidate = false;

                        break;
                    case R.id.nav_followers:
                        fragmentClass = Nokri_EmployeeFollowingFragment.class;

                        break;

                    case R.id.nav_saved_resumes:
                        fragmentClass = SavedResumesFragment.class;

                        break;
                    case R.id.nav_post_job:
                        Nokri_SettingsModel settings = new Nokri_SettingsModel();
                        if (settings.isCatTempOn()) {
                            Intent i = new Intent(Nokri_EmployerDashboardActivity.this, PostJob.class);
                            startActivity(i);
                        } else {
                            Nokri_PostJobFragment.POST_JOB_CALLING_SOURCE = "";
                            fragmentClass = Nokri_PostJobFragment.class;
                        }
                        break;
                    case R.id.nav_matched_resumes:
                        fragmentClass = MatchedResume.class;
                        break;
                    case R.id.nav_employer_search:
                        EmployerSearch fragment = new EmployerSearch();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.nav_blog:
                        fragmentClass = Nokri_BlogGridFragment.class;
                        break;
                    case R.id.nav_candidate_search:
                        fragmentClass = Nokri_CandidateSearchFragment.class;
                        break;
                    case R.id.nav_logout:
                        if (Nokri_SharedPrefManager.getIsLoginFromLinkedIn(Nokri_EmployerDashboardActivity.this)){
                            showLinkedInLogoutDialog();
                        }else{
                            LoginManager.getInstance().logOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            Log.d("GoogleLogout", "Logged Out");

                                        }
                                    });
                            fragmentClass = null;
                            Nokri_DialogManager dialogManager = new Nokri_DialogManager();
                            dialogManager.showAlertDialog(Nokri_EmployerDashboardActivity.this);
                            Nokri_SharedPrefManager.invalidate(Nokri_EmployerDashboardActivity.this);
                            Intent intent = new Intent(Nokri_EmployerDashboardActivity.this, Nokri_MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            dialogManager.hideAlertDialog();
                            finish();
                        }

                    case R.id.nav_settings:
                        fragmentClass = Nokri_SettingsFragment.class;
                        break;
                    case R.id.nav_wpml:
                        Intent i = new Intent(getApplicationContext(), WpmlActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_exit:
                        popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
                        break;
                    default:
                        break;
                }

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

                if (!Nokri_Utils.isCallRunning) {

                    if (fragmentClass != null) {
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (fragment != null) {
                            androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
                        }
                        fragmentClass = null;
                    }
                }
                fragmentClass = null;
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



    AlertDialog logoutAlert;
    private void showLinkedInLogoutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Logging out LinkedIn").setMessage("Please Wait...");
        WebView wv = new WebView(this);

        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.loadUrl("https://linkedin.com/sales/logout");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("https://www.linkedin.com/sales/m")){
                    if (logoutAlert!=null){
                        Nokri_SharedPrefManager.isLoginFromLinkedin(false, Nokri_EmployerDashboardActivity.this);
                        LoginManager.getInstance().logOut();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        Log.d("GoogleLogout", "Logged Out");

                                    }
                                });
                        fragmentClass = null;
                        Nokri_DialogManager dialogManager = new Nokri_DialogManager();
                        dialogManager.showAlertDialog(Nokri_EmployerDashboardActivity.this);
                        Nokri_SharedPrefManager.invalidate(Nokri_EmployerDashboardActivity.this);
                        Intent intent = new Intent(Nokri_EmployerDashboardActivity.this, Nokri_MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        dialogManager.hideAlertDialog();
                        finish();
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        dialog.setView(wv);
        logoutAlert = dialog.create();

        logoutAlert.show();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        else{
            if (!Nokri_Utils.isCallRunning){

                if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if(findViewById(R.id.filter_reset_container).getVisibility() == View.VISIBLE)
                    {findViewById(R.id.filter_reset_container).setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        return;}
                    /* {
                    drawer.openDrawer(GravityCompat.START);
                    return;
                }*/
                    //   drawer.openDrawer(GravityCompat.START);
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

                }
                else
                    getSupportFragmentManager().popBackStackImmediate();
            }
        }
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
    }
//    @Override
//    public void onBackPressed() {
//
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawers();
//            return;
//        } else {
//            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                if (doubleBackToExitPressedOnce) {
//                    if (popupManager != null)
//                        popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
//                    return;
//                }
//
//                this.doubleBackToExitPressedOnce = true;
//                Nokri_ToastManager.showShortToast(this, Nokri_Globals.ON_BACK_EXIT_TEXT);
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
//            } else
//                getSupportFragmentManager().popBackStack();
//        }
//        if (isSearchOpened) {
//            handleMenuSearch();
//            return;
//        }
//        super.onBackPressed();
//    }


    @Override
    public void onConfirmClick(Dialog dialog) {

        dialog.dismiss();
        finish();
    }


}
