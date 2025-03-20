package com.scriptsbundle.nokri.employeer.EmployerSearch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.MaterialProgressBar;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobSpinnerListActivity;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobStep3;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerSearch extends Fragment implements View.OnClickListener {

    private TextView specializationTextView;
    private Spinner specializationSpinner, stateSpinner, citySpinner, townSpinner;
    private String specialization = "", name = "", location = "";
    private Nokri_SpinnerModel specializationSpinnerModel;
    private TextView toolbarTitleTextView;

    private MaterialProgressBar progressBar;
    private Nokri_DialogManager dialogManager;
    private TextView searchByTitleTextView, footerTextView;
    private EditText searchEditText, edittext_headline;
    private String[] values = new String[4];
    private ImageButton searchImageButton;
    private LinearLayout searchNow;
    private String spinnerTitleText;

    public static int locationCount = 0;
    public static String countryId = "";
    public static String cityId = "";
    public static String stateId = "";
    public static String townId = "";
    LinearLayout countrySpinnerLayout;
    TextView categoryTitle, categoryValue;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public EmployerSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employer_search, container, false);


        nokri_initialize(view);
        nokri_setupFonts();
        nokri_getEmployerSeachFilters();

        return view;
    }

    private void nokri_initialize(View view) {
        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);
        specializationSpinner = view.findViewById(R.id.spinner_specialization);
        specializationTextView = view.findViewById(R.id.txt_specialization);
        searchByTitleTextView = view.findViewById(R.id.txt_search_by_title);

        countrySpinnerLayout = view.findViewById(R.id.country_spinner_layout);
        categoryTitle = view.findViewById(R.id.txt_category);
        categoryValue = view.findViewById(R.id.txt_category_value);

        footerTextView = view.findViewById(R.id.footer_text);
        toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        searchEditText = view.findViewById(R.id.edittxt_search);
        searchImageButton = view.findViewById(R.id.img_btn_search);


        searchImageButton = view.findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        searchNow = view.findViewById(R.id.search_now);
        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        progressBar = view.findViewById(R.id.progress);
        searchImageButton.setOnClickListener(this);

        searchNow.setOnClickListener(this);
    }

    private void nokri_setupFonts() {

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontTextView(searchByTitleTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(specializationTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(footerTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryValue, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryTitle, getActivity().getAssets());

    }


    private void nokri_getEmployerSeachFilters() {
        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());

        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        //  toolbarTitleTextView.setText("Advanced Search");
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployerSearchFilters(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getEmployerSearchFilters(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONArray extras = response.getJSONArray("extra");

                        for (int i = 0; i < extras.length(); i++) {

                            JSONObject extra = extras.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("cand_search_title")) {
                                toolbarTitleTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("cand_search_now")) {
                                searchByTitleTextView.setText(extra.getString("key"));
                                footerTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("cand_search_name")) {

                                searchEditText.setHint(extra.getString("key"));
                            }

                        }

                        JSONArray searchFields = response.getJSONObject("data").getJSONArray("search_fields");
                        spinnerTitleText = searchFields.getJSONObject(1).getString("is_show");
                        for (int i = 0; i < searchFields.length(); i++) {

                            JSONObject filterObject = searchFields.getJSONObject(i);

                            if (filterObject.getString("field_type_name").equals("emp_title")) {
                                String hint = filterObject.getString("value");
                                searchEditText.setHint(hint);
                            } else if (filterObject.getString("field_type_name").equals("cand_skills")) {
                                specializationSpinnerModel = nokri_populateSpinner(specializationSpinner, filterObject.getJSONArray("value"));
                                specializationSpinner.setTag("cand_skills");
                                specializationTextView.setText(filterObject.getString("key"));

                            }

                        }

                        categoryTitle.setText(searchFields.getJSONObject(2).getString("key"));
                        categoryValue.setText(spinnerTitleText);
                        JSONArray countriesList = searchFields.getJSONObject(2).getJSONArray("value");


                        countrySpinnerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (countriesList != null && countriesList.length() != 0) {

                                    categorySpinnerList.clear();
                                    makeSpinnerList(countriesList,spinnerTitleText);

                                    String jsonList;
                                    jsonList = new Gson().toJson(categorySpinnerList);

                                    if (jsonList != null) {
                                        getActivity().overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                                        locationCount = 0;
                                        countryId = "";
                                        cityId = "";
                                        stateId = "";
                                        townId = "";
                                        PostJobStep3.countryId = "";
                                        PostJobStep3.cityId = "";
                                        PostJobStep3.stateId = "";
                                        PostJobStep3.townId = "";
                                        Intent intent = new Intent(getActivity(), PostJobSpinnerListActivity.class);
                                        intent.putExtra("list", jsonList);
                                        intent.putExtra("calledFrom", "country");
                                        intent.putExtra("locationCount", locationCount);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivityForResult(intent, 321);
                                    }
                                }
                            }
                        });

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

                } else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
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

    private void makeSpinnerList(JSONArray jsonArray,String title) {
        int index = 0;

        Nokri_SpinnerModel defaultSelection = new Nokri_SpinnerModel();
        defaultSelection.setName(title);
        defaultSelection.setId("");
        defaultSelection.setHasChild(false);
        categorySpinnerList.add(defaultSelection);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("value"));
                model.setId(jsonObject.getString("key"));
                model.setHasChild(jsonObject.getBoolean("has_child"));
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



    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
        model.getNames().add(spinnerTitleText);
        model.getIds().add(spinnerTitleText);
        model.getHasChild().add(false);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getContext() != null && model != null && spinner != null && model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames(), true));
            nokri_setSpinnerSelection(spinner, index);
//            spinner.setOnItemSelectedListener(this);
        }
        return model;
    }

    private void nokri_setSpinnerSelection(Spinner spinner, int index) {


        spinner.setSelection(index);
        Log.d("itemzzz", "called" + index + " " + spinner.getAdapter().getItem(index).toString());


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_search:
                nokri_PostSearchCandidate(true);
                break;
            case R.id.search_now:
                nokri_PostSearchCandidate(false);
                break;
        }

    }


    private void nokri_PostSearchCandidate(boolean searchViaTitle) {

        JsonObject params = new JsonObject();
        EmployerSearchModel employerSearchModel = new EmployerSearchModel();

        if (searchViaTitle){
            if (!searchEditText.getText().toString().equals(""))
                employerSearchModel.setTitle(searchEditText.getText().toString());
            else
                employerSearchModel.setTitle("");
            employerSearchModel.setSkill("");
            employerSearchModel.setLocation("");
        }else{

            if (specializationSpinner.getSelectedItemPosition()==0){
                specialization = "";
            }else{
                specialization = specializationSpinnerModel.getIds().get(specializationSpinner.getSelectedItemPosition());
            }
            employerSearchModel.setSkill(specialization);

            String location;
            location = countryId;
            if (!stateId.isEmpty())
                location = stateId;

            if (!cityId.isEmpty())
                location = cityId;

            if (!townId.isEmpty())
                location = townId;

            employerSearchModel.setLocation(location);

            if (!searchEditText.getText().toString().equals(""))
                employerSearchModel.setTitle(searchEditText.getText().toString());
            else
                employerSearchModel.setTitle("");
        }





        Nokri_SharedPrefManager.saveEmployerSearchModel(employerSearchModel, getContext());
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment showFilteredEmployersFragment = new ShowFilteredEmployers();
        fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), showFilteredEmployersFragment).addToBackStack(null).commit();


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
                categoryValue.setText(model.getName());
            }
        }
    }

}
