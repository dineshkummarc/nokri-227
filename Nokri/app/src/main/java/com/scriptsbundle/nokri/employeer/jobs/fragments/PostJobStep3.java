package com.scriptsbundle.nokri.employeer.jobs.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.dashboard.Nokri_EmployerDashboardActivity;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_PostJobPackagesAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_PostJobsPackagesModel;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostJobStep3 extends AppCompatActivity implements CheckBoxInterface,
        GoogleMap.OnMyLocationChangeListener, OnMapReadyCallback, View.OnClickListener, TextWatcher {

    private PlacesClient placesClient;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView;
    private GoogleMap googleMap;
    EditText latitudeEditText, longitudeEditText;
    Double LATITUDE, LONGITUDE;
    Spinner countrySpinner, stateSpinner, citySpinner, townSpinner;
    JSONObject response;
    RichEditor jobDetailsEditor;
    Button publishJobButton, publishJobButton2;
    TextView locationOnMapTextView, setLocationTextView, latitudeTextView, longitudeTextView, selectTextView;
    Nokri_SpinnerModel countrySpinnerModel, stateSpinnerModel, citySpinnerModel, townSpinnerModel;
    ProgressDialog progressBar;
    LinearLayout countrySpinnerLayout;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    TextView countryTitle, countryValue;
    private MapView map;
    Toolbar toolbar;
    ArrayList<Nokri_PostJobsPackagesModel> modelList = new ArrayList<>();
    RecyclerView recyclerView;
    public static int locationCount = 0;
    public static String countryId;
    public static String cityId;
    public static String stateId;
    public static String townId;
    public static String class_type_value;
    JsonObject mainJsonObject;
    boolean forUpdate;
    Nokri_FontManager fontManager = new Nokri_FontManager();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job_step3);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Nokri_Globals.POST_JOB_STRING);



        mainJsonObject = new Gson().fromJson(getIntent().getStringExtra("main_json_object"), JsonObject.class);
            
        

        try {
            response = new JSONObject(getIntent().getStringExtra("page3"));
            forUpdate = response.getBoolean("is_update");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        countrySpinnerLayout = findViewById(R.id.country_spinner_layout);
        map = findViewById(R.id.map_fragment);
        map.onCreate(savedInstanceState);
        map.onResume();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        map.getMapAsync(this);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.addTextChangedListener(this);
        placesClient = Places.createClient(this);
        latitudeEditText = findViewById(R.id.edittxt_latitude);
        longitudeEditText = findViewById(R.id.edittxt_longitude);
        publishJobButton = findViewById(R.id.btn_publish_job);
        publishJobButton2 = findViewById(R.id.btn_publish_job2);
        countrySpinner = findViewById(R.id.spinner_country);
        stateSpinner = findViewById(R.id.spinner_state);
        citySpinner = findViewById(R.id.spinner_city);
        townSpinner = findViewById(R.id.spinner_town);
        selectTextView = findViewById(R.id.txt_select_country);

        locationOnMapTextView = findViewById(R.id.txt_location_on_map);
        setLocationTextView = findViewById(R.id.txt_set_location);
        latitudeEditText = findViewById(R.id.edittxt_latitude);
        longitudeEditText = findViewById(R.id.edittxt_longitude);
        latitudeTextView = findViewById(R.id.txt_latitude);
        longitudeTextView = findViewById(R.id.txt_longitude);
        countryTitle = findViewById(R.id.category_title);
        countryValue = findViewById(R.id.category_value);
        recyclerView = findViewById(R.id.recyclerview);

        publishJobButton.setOnClickListener(this);
        publishJobButton2.setOnClickListener(this);


        fontManager.nokri_setOpenSenseFontTextView(locationOnMapTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(autoCompleteTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(selectTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(setLocationTextView,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(latitudeEditText,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(longitudeEditText,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(latitudeTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(longitudeTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(countryTitle,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(countryValue,getAssets());
        fontManager.nokri_setOpenSenseFontButton(publishJobButton,getAssets());
        fontManager.nokri_setOpenSenseFontButton(publishJobButton2,getAssets());

        Nokri_Utils.setRoundButtonColor(this, publishJobButton);
        Nokri_Utils.setRoundButtonColor(this, publishJobButton2);

        try {
            if (forUpdate){
                initViewsForUpdate();
            }else{
                initViews();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void initViewsForUpdate() throws Exception {

        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
        setLocationTextView.setText(response.getJSONObject("job_loc").getString("key"));
        selectTextView.setText(response.getJSONObject("job_location").getString("key"));

        JSONArray preniumJobsArray = response.getJSONArray("premium_jobs");

        if (preniumJobsArray.length() > 0) {

            for (int i = 0; i < preniumJobsArray.length(); i++) {
                JSONObject preniumJob = preniumJobsArray.getJSONObject(i);
                Nokri_PostJobsPackagesModel model = new Nokri_PostJobsPackagesModel();
                model.setId(preniumJob.getString("field_type_name"));
                model.setQuantity(preniumJob.getString("value"));
                model.setTitle(preniumJob.getString("key"));
                model.setRemainign(preniumJob.getString("fieldname"));
                model.setChecked(preniumJob.getBoolean("is_required"));

                modelList.add(model);
            }
            nokri_setupRecyclerView();
        } else {
            findViewById(R.id.prenium_jobs_container).setVisibility(View.GONE);
            findViewById(R.id.btn_publish_job2).setVisibility(View.VISIBLE);
        }

        JSONArray countriesList  = response.getJSONObject("job_country").getJSONArray("value");
        JSONArray stateList = response.getJSONObject("job_state").getJSONArray("value");
        JSONArray cityList = response.getJSONObject("job_city").getJSONArray("value");
        JSONArray townList = response.getJSONObject("job_town").getJSONArray("value");


        setLocations(countriesList,stateList,cityList,townList);

        countrySpinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorySpinnerList.clear();
                if (countriesList != null && countriesList.length() != 0) {


                    makeSpinnerList(countriesList);

                    String jsonList;
                    jsonList = new Gson().toJson(categorySpinnerList);

                    if (jsonList != null) {
                        overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                        locationCount = 0;
                        countryId = "";
                        cityId = "";
                        stateId = "";
                        townId = "";
                        Intent intent = new Intent(PostJobStep3.this, PostJobSpinnerListActivity.class);
                        intent.putExtra("list", jsonList);
                        intent.putExtra("locationCount", locationCount);
                        intent.putExtra("calledFrom", "country");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, 321);
                    }
                }
            }
        });

        try {
            LATITUDE = Double.parseDouble(response.getJSONObject("job_lat").getString("value"));

        } catch (NumberFormatException e) {
            LATITUDE = Double.valueOf(0);
        }
        try {
            LONGITUDE = Double.parseDouble(response.getJSONObject("job_long").getString("value"));
        } catch (NumberFormatException e) {
            LONGITUDE = Double.valueOf(0);
        }

        String location = response.getJSONObject("job_loc").getString("value");
        if (location.equals(""))
            autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));
        else {
            autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));
            autoCompleteTextView.setText(location);
        }
        latitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
        latitudeEditText.setHint(response.getJSONObject("job_lat").getString("key"));

        longitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
        longitudeEditText.setHint(response.getJSONObject("job_long").getString("key"));

        if (LATITUDE!=0){
            latitudeEditText.setText(String.valueOf(LATITUDE));
        }
        if (LONGITUDE!=0){
            longitudeEditText.setText(String.valueOf(LONGITUDE));
        }


        publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));

    }

    @SuppressLint("SetTextI18n")
    private void setLocations(JSONArray countriesList, JSONArray stateList, JSONArray cityList, JSONArray townList) {
        if (countriesList!=null && countriesList.length()!=0){
            if (stateList!=null && stateList.length()!=0){
                if (cityList!=null && cityList.length()!=0){
                    if (townList!=null && townList.length()!=0){
                        countryTitle.setText("Location");
                        countryValue.setText(setLocationId(townList,"town") + ", "  + setLocationId(cityList,"city") + ", "
                                +setLocationId(stateList,"state") + ", " + setLocationId(countriesList,"country"));
                    }else{
                        countryTitle.setText("Location");
                        countryValue.setText(setLocationId(cityList,"city") + ", "  +setLocationId(stateList,"state") + ", " + setLocationId(countriesList,"country"));
                    }
                }else{
                    countryTitle.setText("Location");
                    countryValue.setText(setLocationId(stateList,"state") + ", " + setLocationId(countriesList,"country"));
                }
            }else{
                countryTitle.setText("Location");
                countryValue.setText(setLocationId(countriesList,"country"));            }
        }
    }


    public String setLocationId(JSONArray jsonArray,String calledFrom){
        for (int i = 0;i<jsonArray.length();i++){
            try {
                boolean selected = jsonArray.getJSONObject(i).getBoolean("selected");
                if (selected){
                    countryId = jsonArray.getJSONObject(i).getString("key");
                    if (calledFrom.equals("country")){
                        countryId = jsonArray.getJSONObject(i).getString("key");
                    }else if (calledFrom.equals("state")){
                        stateId = jsonArray.getJSONObject(i).getString("key");
                    }else if (calledFrom.equals("city")){
                        cityId = jsonArray.getJSONObject(i).getString("key");
                    }else if (calledFrom.equals("town")){
                        townId = jsonArray.getJSONObject(i).getString("key");
                    }
                    return jsonArray.getJSONObject(i).getString("value");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }

    public void initViews() throws Exception {

        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
        setLocationTextView.setText(response.getJSONObject("job_loc").getString("key"));
        selectTextView.setText(response.getJSONObject("job_location").getString("key"));

        JSONArray preniumJobsArray = response.getJSONArray("premium_jobs");

        if (preniumJobsArray.length() > 0) {

            for (int i = 0; i < preniumJobsArray.length(); i++) {
                JSONObject preniumJob = preniumJobsArray.getJSONObject(i);
                Nokri_PostJobsPackagesModel model = new Nokri_PostJobsPackagesModel();
                model.setId(preniumJob.getString("field_type_name"));
                model.setQuantity(preniumJob.getString("value"));
                model.setTitle(preniumJob.getString("key"));
                model.setRemainign(preniumJob.getString("fieldname"));
                model.setChecked(preniumJob.getBoolean("is_required"));

                modelList.add(model);
            }
            nokri_setupRecyclerView();
        } else {
            findViewById(R.id.prenium_jobs_container).setVisibility(View.GONE);
            findViewById(R.id.btn_publish_job2).setVisibility(View.VISIBLE);
        }
        autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));
        latitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
        latitudeEditText.setHint(response.getJSONObject("job_lat").getString("key"));
        longitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));


        JSONArray countriesList  = response.getJSONObject("job_country").getJSONArray("value");


        countryId = countriesList.getJSONObject(0).getString("key");
        countryTitle.setText(response.getJSONObject("job_country").getString("key"));
        countryValue.setText(countriesList.getJSONObject(0).getString("value"));


        countrySpinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorySpinnerList.clear();
                if (countriesList != null && countriesList.length() != 0) {


                    makeSpinnerList(countriesList);

                    String jsonList;
                    jsonList = new Gson().toJson(categorySpinnerList);

                    if (jsonList != null) {
                        overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                        locationCount = 0;
                        countryId = "";
                        cityId = "";
                        stateId = "";
                        townId = "";
                        Intent intent = new Intent(PostJobStep3.this, PostJobSpinnerListActivity.class);
                        intent.putExtra("list", jsonList);
                        intent.putExtra("locationCount", locationCount);
                        intent.putExtra("calledFrom", "country");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, 321);
                    }
                }
            }
        });

        try {
            LATITUDE = Double.parseDouble(response.getJSONObject("job_lat").getString("key"));

        } catch (NumberFormatException e) {
            LATITUDE = Double.valueOf(0);
        }
        try {
            LONGITUDE = Double.parseDouble(response.getJSONObject("job_long").getString("key"));
        } catch (NumberFormatException e) {
            LONGITUDE = Double.valueOf(0);
        }

        longitudeEditText.setHint(response.getJSONObject("job_long").getString("key"));
        publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));

    }

    private void nokri_setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Nokri_PostJobPackagesAdapter adapter = new Nokri_PostJobPackagesAdapter(true,modelList, getApplicationContext());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void makeSpinnerList(JSONArray jsonArray) {
        int index = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("value"));
                model.setId(jsonObject.getString("key"));
                model.setHasChild(jsonObject.getBoolean("has_child"));
                if (i == 0) {
                    countryId = model.getId();

                }
                categorySpinnerList.add(model);
                {
                    if (jsonObject.getBoolean("selected")) {
                        index = i;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void nokri_manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]);  // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_dropdown_item_1line, data);
            autoCompleteTextView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnMyLocationChangeListener(this);
        if (ActivityCompat.checkSelfPermission(PostJobStep3.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PostJobStep3.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Nokri_ToastManager.showLongToast(PostJobStep3.this, "This Feature Requires Permission");
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                latitudeEditText.setText(LATITUDE + "");
                longitudeEditText.setText(LONGITUDE + "");
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(PostJobStep3.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    autoCompleteTextView.setText(address);
                    setMapLocation();

                } catch (IOException e) {
                    Nokri_ToastManager.showLongToast(PostJobStep3.this, "Something Went Wrong");
                } catch (Exception ex) {
                    Nokri_ToastManager.showLongToast(PostJobStep3.this, "Something Went Wrong");
                }


                return false;
            }
        });

    }

    @Override
    public void onMyLocationChange(Location location) {

        if (location != null) {
            LATITUDE = location.getLatitude();
            LONGITUDE = location.getLongitude();

        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        nokri_manageAutoComplete(s.toString());
    }

    private void nokri_setSpinnerSelection(Spinner spinner, int index) {


        spinner.setSelection(index);
        Log.d("itemzzz", "called" + index + " " + spinner.getAdapter().getItem(index).toString());


    }

    private void setMapLocation() {
        if (googleMap != null) {
            googleMap.clear();
            LatLng location = new LatLng(LATITUDE, LONGITUDE);
            googleMap.addMarker(new MarkerOptions().position(location));
            googleMap.setMinZoomPreference(Nokri_Config.MAP_CAM_MIN_ZOOM);
            googleMap.setMaxZoomPreference(Nokri_Config.MAP_CAM_MAX_ZOOM);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bold:
                jobDetailsEditor.setBold();
                break;
            case R.id.img_italic:
                jobDetailsEditor.setItalic();
                break;
            case R.id.img_underline:
                jobDetailsEditor.setUnderline();
                break;
            case R.id.img_num_bullets:
                jobDetailsEditor.setNumbers();
                break;
            case R.id.img_list_bullets:
                jobDetailsEditor.setBullets();
                break;

            case R.id.btn_publish_job:
                try {
                    makeJsonObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeJsonObject() throws JSONException {
        progressBar.show();
        mainJsonObject.addProperty("job_country", countryId);
        mainJsonObject.addProperty("job_cities", cityId);
        mainJsonObject.addProperty("job_states", stateId);
        mainJsonObject.addProperty("job_town", townId);
        mainJsonObject.addProperty("job_address", autoCompleteTextView.getText().toString());
        mainJsonObject.addProperty("job_lat", latitudeEditText.getText().toString());
        mainJsonObject.addProperty("job_long", longitudeEditText.getText().toString());
        String premiumJobsString = "";
        for (int i = 0;i<premiumJobsList.size();i++){
            if (i == 0){
                premiumJobsString = premiumJobsString + premiumJobsList.get(i).getId();
            }else{
                premiumJobsString = premiumJobsString + ","+ premiumJobsList.get(i).getId();
            }
        }
        mainJsonObject.addProperty("class_type_value", premiumJobsString);

        if (forUpdate){
            mainJsonObject.addProperty("is_update",response.getString("updateId"));
            mainJsonObject.addProperty("job_id",response.getString("updateId"));
        }

        Log.d("data_info",mainJsonObject.toString());

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this),this);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getApplicationContext())) {
            myCall = restService.postJob(mainJsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postJob(mainJsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.dismiss();
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success")){
                            Intent i = new Intent(PostJobStep3.this, Nokri_EmployerDashboardActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra("calledFromCustom",true);
                            String jobId = jsonObject.getJSONObject("extra").getString("is_update");
                            i.putExtra("job_id",jobId);
                            startActivity(i);
                            finish();
                            Toast.makeText(PostJobStep3.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }





                    } catch (Exception e) {
                        progressBar.dismiss();
                        e.printStackTrace();
                        Nokri_ToastManager.showShortToast(PostJobStep3.this,e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.dismiss();
                Nokri_ToastManager.showShortToast(PostJobStep3.this,t.getMessage());
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                if (data.getStringExtra("country_id")!=null)
                    countryId = data.getStringExtra("country_id");
                if (data.getStringExtra("state_id")!=null)
                    stateId = data.getStringExtra("state_id");
                if (data.getStringExtra("city_id")!=null)
                    cityId = data.getStringExtra("city_id");
                if (data.getStringExtra("town_id")!=null)
                    townId = data.getStringExtra("town_id");
                countryValue.setText(model.getName());
            }
        }
    }


    public ArrayList<Nokri_PostJobsPackagesModel> premiumJobsList = new ArrayList<>();
    @Override
    public void onCheckClick(int position) {
        boolean isFound = false;
        for (int i = 0;i<premiumJobsList.size();i++){
            if (premiumJobsList.get(i).getId().equals(modelList.get(position).getId())){
                isFound = true;
                break;
            }
        }
        if (!isFound)
            premiumJobsList.add(modelList.get(position));
    }

    @Override
    public void onCheckUnClick(int position) {
        for (int i = 0;i<premiumJobsList.size();i++){
            if (premiumJobsList.get(i).getId().equals(modelList.get(position).getId())){
                premiumJobsList.remove(i);
            }
        }
    }
}
