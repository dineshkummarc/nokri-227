package com.scriptsbundle.nokri.candidate.profile.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ContactModel;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.utils.AddRatingBottomSheet;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.RatingsAdapter;
import com.scriptsbundle.nokri.utils.RatingsBottomSheet;
import com.scriptsbundle.nokri.utils.models.RatingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CompanyDetailFragment extends Fragment implements OnMapReadyCallback, View.OnFocusChangeListener, View.OnClickListener {

    private RecyclerView profileDetailRecyclerView;
    private ArrayList<Nokri_DescriptionModel> descriptionModelList;
    private TextView companyInfoTextView, companyInfoDataTextView, profileDetailTextView;
    private Nokri_FontManager fontManager;
    private Nokri_DescriptionRecyclerViewAdapter adapter;
    Nokri_CompanyPublicProfileFragment companyPublicProfileFragment;
    private MapView map;
    private static double LATITUDE, LONGITUDE;
    private GoogleMap googleMap;
    private EditText nameEditText, emailEditText, subjectEditText, messageEditText;
    private Button messageButton;
    private TextView contactTextView;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private String aboutMeText, aboutMeDataText, profileDetailText;
    private Nokri_DialogManager dialogManager;
    private Nokri_ContactModel contactModel;
    public boolean mapSwitch;


    JSONObject ratingSection;
    TextView addRating;
    TextView ratingText;
    RecyclerView ratingList;
    TextView viewAllReviews;
    String USER_ID;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Override this if you want to get savedInstanceState.
        mViewCreated = true;
        tryViewCreatedFirstSight();
    }

    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }

    public void setRatingObject(JSONObject jsonObject, String USER_ID) {
        ratingSection = jsonObject;
        this.USER_ID = USER_ID;
    }

    protected void onViewCreatedFirstSight(View view) {

    }

    protected void onUserFirstSight() {

    }

    protected void onUserVisibleChanged(boolean visible) {
        if (getContext() != null) {
            if (visible) {
                if (companyPublicProfileFragment == null) {
                } //  Nokri_ToastManager.showShortToast(getContext(),"visible detail");
            }
        }
    }

    public Nokri_CompanyDetailFragment() {
        // Required empty public constructor
    }

    protected void nokri_setContactModel(Nokri_ContactModel contactModel) {
        this.contactModel = contactModel;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapSwitch) {
            map.onResume();
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontManager = new Nokri_FontManager();


        viewAllReviews = getView().findViewById(R.id.viewAllReviews);
        ratingList = getView().findViewById(R.id.ratingList);
        addRating = getView().findViewById(R.id.add_rating);
        Nokri_Utils.setRoundButtonColor(getActivity(), addRating);
        ratingText = getView().findViewById(R.id.ratingText);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingSection != null) {
                    AddRatingBottomSheet bottomSheet = new AddRatingBottomSheet(ratingSection, true);
                    bottomSheet.show(getChildFragmentManager(),
                            "RatingBottomSheet");
                }
            }
        });

        JSONArray reviewArray = null;
        try {
            reviewArray = ratingSection.getJSONArray("reviews_data");
            ratingText.setText(ratingSection.getJSONObject("extra").getString("reviews_title"));

            ArrayList<RatingModel> ratings = new ArrayList<>();
            for (int i = 0; i < reviewArray.length(); i++) {
                RatingModel model = new RatingModel();
                JSONObject jsonObject1 = reviewArray.getJSONObject(i);
                model.setDate(jsonObject1.getString("_rating_date"));
                model.setRaterName(jsonObject1.getString("_rating_poster"));
                model.setRating(jsonObject1.getDouble("_rating_avg"));
                model.setContainsReply(jsonObject1.getBoolean("has_reply"));
                model.setAuthorReply(jsonObject1.getString("reply_text"));
                model.setAuthorName(jsonObject1.getString("reply_heading"));
                model.setRaterTitle(jsonObject1.getString("_rating_title"));
                model.setRaterComments(jsonObject1.getString("_rating_description"));
                model.setCanReply(jsonObject1.getBoolean("can_reply"));
                model.setUrl(jsonObject1.getString("cand_image"));
                model.setCommentID(jsonObject1.getString("cid"));
                ratings.add(model);
            }

            if (ratings.size() == 0) {
                viewAllReviews.setVisibility(View.GONE);
            }
            viewAllReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RatingsBottomSheet bottomSheet = new RatingsBottomSheet(USER_ID, true);
                    bottomSheet.show(getChildFragmentManager(),
                            "AllRatingsBottomSheet");
                }
            });

            RatingsAdapter adapter = new RatingsAdapter(getActivity(), ratings, false);
            ratingList.setLayoutManager(new LinearLayoutManager(getActivity()));
            ratingList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        companyPublicProfileFragment = (Nokri_CompanyPublicProfileFragment) getParentFragment();


        profileDetailRecyclerView = getView().findViewById(R.id.profile_detail_recyclerview);
        profileDetailRecyclerView.setNestedScrollingEnabled(false);
        companyInfoTextView = getView().findViewById(R.id.txt_company_info);
        companyInfoDataTextView = getView().findViewById(R.id.txt_company_info_data);
        profileDetailTextView = getView().findViewById(R.id.txt_profile_detail);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        emailEditText = getView().findViewById(R.id.edittxt_email);

        subjectEditText = getView().findViewById(R.id.edittxt_subject);
        messageEditText = getView().findViewById(R.id.edittxt_message);
        contactTextView = getView().findViewById(R.id.txt_contact);

        messageButton = getView().findViewById(R.id.btn_message);

        Nokri_Utils.setRoundButtonColor(getContext(), messageButton);
        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        subjectEditText.setOnFocusChangeListener(this);
        messageEditText.setOnFocusChangeListener(this);
        messageButton.setOnClickListener(this);
        if (contactModel != null) {

            nameEditText.setHint(contactModel.getNameHint());
            emailEditText.setHint(contactModel.getEmailHint());
            subjectEditText.setHint(contactModel.getSubjectHint());
            messageEditText.setHint(contactModel.getMessageHint());
            messageButton.setText(contactModel.getButtonText());
            contactTextView.setText(contactModel.getContactTitleText());
        }

        fontManager.nokri_setMonesrratSemiBioldFont(contactTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(companyInfoTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(profileDetailTextView, getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontTextView(companyInfoDataTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(subjectEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(messageEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontButton(messageButton, getActivity().getAssets());

        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);

        subjectEditText.setOnFocusChangeListener(this);
        messageEditText.setOnFocusChangeListener(this);

        setViews();
    }

    protected void setData(String latitide, String longitude, boolean mapSwitch, String profileDetailText, String aboutMeText, String aboutMeDataText, ArrayList<Nokri_DescriptionModel> descriptionModelList) {


        this.mapSwitch = mapSwitch;

        try {
            LATITUDE = Double.parseDouble(latitide);
            Log.d("loccccccc", LATITUDE + "");
        } catch (NumberFormatException e) {
            LATITUDE = 0;
        }
        try {
            LONGITUDE = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            LONGITUDE = 0;
        }

        this.descriptionModelList = descriptionModelList;
        this.aboutMeText = aboutMeText;
        this.profileDetailText = profileDetailText;
        this.aboutMeDataText = aboutMeDataText;
    }

    protected void setViews() {


        setMapLocation();

        profileDetailTextView.setText(profileDetailText);
        companyInfoTextView.setText(aboutMeText);
        companyInfoDataTextView.setText(aboutMeDataText);


        adapter = new Nokri_DescriptionRecyclerViewAdapter(descriptionModelList, getContext(), 1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        profileDetailRecyclerView.setLayoutManager(layoutManager);

        profileDetailRecyclerView.setItemAnimator(new DefaultItemAnimator());
        profileDetailRecyclerView.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_company_detail, container, false);

        map = view.findViewById(R.id.map_fragment);
        if (mapSwitch) {
            map.setVisibility(View.GONE);
            map.onCreate(savedInstanceState);

            map.onResume();
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.getMapAsync(this);
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        } else {
            map.setVisibility(View.GONE);

        }
        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
       /* LatLng location = new LatLng(LATITUDE, LONGITUDE);
        map.addMarker(new MarkerOptions().position(location));
        map.setMinZoomPreference(Nokri_Config.MAP_CAM_MIN_ZOOM);
        map.setMaxZoomPreference(Nokri_Config.MAP_CAM_MAX_ZOOM);
        map.moveCamera(CameraUpdateFactory.newLatLng(location));*/
        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(location));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    private void setMapLocation() {
        if (googleMap != null) {


            LatLng location = new LatLng(LATITUDE, LONGITUDE);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(location));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mapSwitch) {
            map.onPause();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapSwitch) {
            map.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapSwitch) {
            map.onLowMemory();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {
            case R.id.edittxt_name:
                if (hasFocus) {
                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_email:

                if (hasFocus) {
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            case R.id.edittxt_subject:

                if (hasFocus) {
                    subjectEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            case R.id.edittxt_message:

                if (hasFocus) {
                    messageEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            default:
                break;


        }

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_message:
                nokri_sendMessage();
                break;
        }

    }

    private void nokri_sendMessage() {

        if (!Nokri_Utils.isValidEmail(emailEditText.getText().toString())) {
            emailEditText.setError("!");

            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.INVALID_EMAIL);

            return;

        }

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sender_name", nameEditText.getText().toString());
        jsonObject.addProperty("sender_email", emailEditText.getText().toString());
        jsonObject.addProperty("sender_subject", subjectEditText.getText().toString());
        jsonObject.addProperty("sender_message", messageEditText.getText().toString());
        jsonObject.addProperty("receiver_id", contactModel.getReceiverId());
        jsonObject.addProperty("receiver_name", contactModel.getReceiverName());
        jsonObject.addProperty("receiver_email", contactModel.getReceiverEmail());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            //    nokri_getLocationAndAddress();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });


    }


}



