package com.scriptsbundle.nokri.guest.home.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.activities.Nokri_SigninActivity;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.scriptsbundle.nokri.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_MyProfileFragment;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.dashboard.fragments.NokriEmployeerDashboardFragment;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_PostJobFragment;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.search.fragments.Nokri_JobSearchFragment;
import com.scriptsbundle.nokri.guest.settings.fragments.Nokri_SettingsFragment;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.GPSTracker;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.RuntimePermissionHelper;
import com.squareup.picasso.Picasso;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Nokri_Home2ScreenFragment extends Fragment implements View.OnClickListener, RuntimePermissionHelper.permissionInterface, AdapterView.OnItemSelectedListener {
    private Button signInButton, signUpButton, SearchNow;
    EditText searchEditText;
    private TextView toolbarTitleTextView;
    private Nokri_GuestDashboardModel guestDashboardModel;
    SpaceNavigationView spaceNavigationView;
    TextView SubHeading1, SubHeading2, CardHeading, CardSubHeading, TextMiles, MilesNumber;
    private Nokri_FontManager fontManager;
    private ImageView logo, BackgroundImage;
    CardView NewCard;
    Spinner SpinnerCat;

    View Underline;
    Spinner CategorySpinner;
    BubbleSeekBar bubbleSeekBar;
    GPSTracker gps;
    double latitude, longitude;
    private int nextPage = 1;
    private int filterNextPage = 1;
    private boolean isFilterNetPage = false;
    private boolean hasNextPage = true;
    RuntimePermissionHelper runtimePermissionHelper;
    //    private Button loadMoreButton;
    private ImageView messageImage;
    //    private LinearLayout messageContainer;
    private Nokri_DialogManager dialogManager;
    //    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Nokri_JobsAdapter adapter;
    private List<Nokri_JobsModel> modelList;
    private String filterText = "";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<String> spinnerItems, spinnerID;
    Spinner spinnerCategories;
    String categoryId;
    private String mParam1;
    private String mParam2;
    public Fragment fragment;
    private Class fragmentClass;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseMessaging.getInstance().subscribeToTopic(Nokri_Config.TOPIC_GLOBAL);

        changeSystemBarColor(getActivity());
        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fragment != null) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), fragment).addToBackStack(null).commit();
            }
            fragmentClass = null;
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInButton = view.findViewById(R.id.btn_singin);
        SearchNow = view.findViewById(R.id.btn_singup);

        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);
        spinnerCategories = getView().findViewById(R.id.spinner_type);
        searchEditText = view.findViewById(R.id.edittxt_search);


        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);

        SubHeading1 = view.findViewById(R.id.textmillion);
        SubHeading2 = view.findViewById(R.id.textstories);
        NewCard = view.findViewById(R.id.cd_new_home);
        CardHeading = view.findViewById(R.id.txt_signin);
        Underline = view.findViewById(R.id.viw1);
        Underline.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        CardSubHeading = view.findViewById(R.id.cardsubhead);
        CategorySpinner = view.findViewById(R.id.spinner_type);
        TextMiles = view.findViewById(R.id.textmiles);
        MilesNumber = view.findViewById(R.id.numerictextview);

        bubbleSeekBar = view.findViewById(R.id.seakBar);

        spinnerCategories.setOnItemSelectedListener(this);
        bubbleSeekBar.getConfigBuilder()
                .thumbColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .trackColor(Color.GRAY)
                .bubbleColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .secondTrackColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .build();
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                MilesNumber.setText(String.valueOf(progress));
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });


        Nokri_Utils.changeSystemBarColor(getActivity());
        Nokri_Utils.setRoundButtonColor(getActivity(), SearchNow);
//
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey));
        logo = view.findViewById(R.id.img_logo);
        BackgroundImage = view.findViewById(R.id.img_bg);
//
        SearchNow.setOnClickListener(this);
        fontManager = new Nokri_FontManager();
        SpinnerCat = view.findViewById(R.id.spinner_type);
        guestDashboardModel = Nokri_SharedPrefManager.getGuestSettings(getApplicationContext());

        List<String> spinnerArray = new ArrayList<String>();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = view.findViewById(R.id.spinner_type);
        sItems.setAdapter(adapter);
//        sItems.setSelection(0);

        runtimePermissionHelper.requestLocationPermission(1);

        SpaceNavigationView spaceNavigationView = view.findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Home", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("Search", R.drawable.ic_search_nav_bottom));
        spaceNavigationView.addSpaceItem(new SpaceItem("Settings", R.drawable.ic_nav_profile));
        spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.ic_setting));
        spaceNavigationView.setSpaceBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setActiveCentreButtonBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        spaceNavigationView.setCentreButtonColor(Color.parseColor(Nokri_Config.APP_COLOR));
        spaceNavigationView.setActiveCentreButtonBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        spaceNavigationView.setActiveSpaceItemColor(Color.parseColor(Nokri_Config.APP_COLOR));


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {
                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment postJobFragment = new Nokri_PostJobFragment();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), postJobFragment).addToBackStack(null).commit();
                } else {

//                    Intent intent = new Intent(getContext(), Nokri_SigninActivity.class);
//                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "Please login as an Employee ", Toast.LENGTH_SHORT).show();

                    Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.REQUIRED_Employee_Login_TEXT);

                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
//                int i = itemName.indexOf(itemIndex);
                int i = itemIndex;
                if (i == 0) {
                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
                        Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
                        fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).addToBackStack(null).commit();

                    } else {
                        Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();
                        fragmentTransaction.add(R.id.fragment_placeholder, homeScreen2Fragment).addToBackStack(null).commit();


                    }
                } else if (i == 1) {

                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment editEmailTemplate = new Nokri_JobSearchFragment();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();
                } else if (i == 2) {
                    if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {
                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment nokri_myProfileFragment = new Nokri_MyProfileFragment();
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), nokri_myProfileFragment).addToBackStack(null).commit();

                    } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {
                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment nokri_myProfileFragment = new NokriEmployeerDashboardFragment();
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), nokri_myProfileFragment).addToBackStack(null).commit();
                    } else {
                        Intent intent = new Intent(getContext(), Nokri_SigninActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Please login at first ", Toast.LENGTH_SHORT).show();
                    }
                } else if (i == 3) {
                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment nokri_myProfileFragment = new Nokri_SettingsFragment();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), nokri_myProfileFragment).addToBackStack(null).commit();
                }


            }


            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
//                Toast.makeText(Nokri_MainActivity.this,"onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
//                Toast.makeText(getApplicationContext(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });


        String toolbarTitle = "";

        if (Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = model.getHome();
        } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = model.getHome();
        } else if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle = model.getHome();

        }
        ApplicationInfo ai;
        PackageManager pm = getContext().getPackageManager();
        try {


            ai = pm.getApplicationInfo(getContext().getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);


        toolbarTitleTextView.setText(applicationName);

        if (Nokri_AllJobsFragment.ALL_JOBS_SOURCE.equals("")) {
        }
        modelList = new ArrayList<>();

//        progressBar = getView().findViewById(R.id.progress_bar);
        nextPage = 1;
        isFilterNetPage = false;

        nokri_getMainActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri__home2_screen, container, false);


        return view;
    }

    public void onSuccessPermission(int code) {
        if (code == 1) {
            nokri_loctionSearch();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getContext(), Nokri_GuestDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public static void changeSystemBarColor(Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));

        }
    }

    private void nokri_loctionSearch() {

        gps = new GPSTracker(getActivity());

        List<Address> addresses1 = null;
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("infogps", "GpsTracker");


            try {
                addresses1 = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                addresses1 = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder result = new StringBuilder();
            if (addresses1 != null) {

                if (addresses1.size() > 0) {
                    Address address = addresses1.get(0);
                    int maxIndex = address.getMaxAddressLineIndex();
                    for (int x = 0; x <= maxIndex; x++) {
                        result.append(address.getAddressLine(x));
                        //result.append(",");
                    }
                }
                Log.d("info location", addresses1.toString());
                Log.d("info locaLatLong", latitude + " Long " + longitude);
            }

        } else
            gps.showSettingsAlert();
    }

    private void nokri_getMainActivity() {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;

        myCall = restService.getSecondHome(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());


                        JSONObject data = jsonObject.getJSONObject("data");
                        if (!TextUtils.isEmpty(data.getString("img")))
                            Picasso.get().load(data.getString("img")).into(BackgroundImage);

                        if (!TextUtils.isEmpty(data.getString("logo")))
                            Picasso.get().load(data.getString("logo")).into(logo);
                        SubHeading1.setText(data.getString("tagline"));
                        CardHeading.setText(data.getString("heading"));
                        CardSubHeading.setText(data.getString("key_wrd_headng"));
                        searchEditText.setHint(data.getString("key_wrd_plc"));
                        TextMiles.setText(data.getString("radius_text"));
                        MilesNumber.setText(data.getString("radius_value"));
                        SearchNow.setText(data.getString("btn_text"));
                        bubbleSeekBar.getConfigBuilder().max(Float.parseFloat(data.getString("radius_value"))).build();
                        bubbleSeekBar.setProgress(Float.parseFloat(data.getString("radius_value")));
                        JSONObject CategoryData = data.getJSONObject("categories");
                        JSONArray CategoryDataArray = CategoryData.getJSONArray("value");
                        spinnerItems = new ArrayList<>();
                        spinnerID = new ArrayList<>();
                        spinnerItems.add(data.getString("cat_plc"));
                        for (int i = 0; i < CategoryDataArray.length(); i++) {
                            spinnerItems.add(CategoryDataArray.getJSONObject(i).getString("value"));
                            spinnerID.add(CategoryDataArray.getJSONObject(i).getString("key"));
                        }
//                        if (spinnerCategories.getAdapter() == null)
                        spinnerCategories.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, spinnerItems));

                        dialogManager.hideAlertDialog();


                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Nokri_AllJobsFragment nokri_allJobsFragment = new Nokri_AllJobsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("job_title", searchEditText.getText().toString());
        bundle.putString("job_category", categoryId);
        bundle.putDouble("e_lat", latitude);
        bundle.putDouble("e_long", longitude);
        bundle.putString("e_distance", MilesNumber.getText().toString());
        bundle.putInt("page_number", nextPage);
        bundle.putString("requestFrom", "Home");
        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "external";

        nokri_allJobsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_placeholder, nokri_allJobsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i > 0) {
            categoryId = spinnerID.get(i - 1);
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
