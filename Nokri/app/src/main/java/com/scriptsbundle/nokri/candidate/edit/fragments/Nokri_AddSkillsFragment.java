package com.scriptsbundle.nokri.candidate.edit.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_SkillsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mabbas007.tagsedittext.TagsEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AddSkillsFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {
    private TextView addSkillsTextView, selectSkillsTextView, selectPercentTextView;

    private Button saveSkillButton;
    private Nokri_FontManager fontManager;
    private TagsEditText mTagsEditText, percentageTagsEditText;

    private ArrayList<Nokri_SkillsModel> skillsModelList, percentageModelList;
    private ArrayList<String> selecSkills, selecPercentage;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_AddSkillsFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_add_skills, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
        nokri_initialize();
        nokri_setFonts();

        dialogManager = new Nokri_DialogManager();

        try {
            nokri_getCandidateSkills();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void nokri_postSkills() {
        ArrayList<String> ids = getIdsFromTags();
        ArrayList<String> percentageIds = getSkillIdsFromTags();

        if (ids == null || ids.size() <= 0) {
            Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.SELECT_VALID_SKILL);
            return;
        }
        if (percentageIds == null || percentageIds.size() <= 0) {
            Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.SELECT_VALID_SKILL);
            return;
        }
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        for (int i = 0; i < ids.size(); i++) {

            array.add(ids.get(i));

        }


        JsonArray array2 = new JsonArray();
        for (int i = 0; i < percentageIds.size(); i++) {

            array2.add(percentageIds.get(i));

        }


        object.add("cand_skills", array);
        object.add("cand_skills_values", array2);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidateSkills(object, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postCandidateSkills(object, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateSkills(object, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
//                            nokri_getCandidateSkills();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                        }

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
                    dialogManager.showCustom(responseObject.code() + "");
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


    private void nokri_getCandidateSkills() throws JSONException {
        skillsModelList = new ArrayList<>();
        percentageModelList = new ArrayList<>();
        JSONObject response = Nokri_CandidateEditProfileFragment.skills;
        if (response.getBoolean("success")) {
            Log.d("something", response.toString());
            JSONObject extras = response.getJSONObject("extras");
            JSONObject pageTitle = extras.getJSONObject("page_title");
            addSkillsTextView.setText(pageTitle.getString("key"));
            JSONObject buttonName = extras.getJSONObject("btn_name");
            saveSkillButton.setText(buttonName.getString("key"));
            JSONObject dataObject = response.getJSONObject("data");

            JSONArray selectedSkillsArray = dataObject.getJSONArray("skills_selected");
            JSONObject skillsField = dataObject.getJSONObject("skills_field");
            JSONObject percentageField = dataObject.getJSONObject("skills_field_values");

            selectSkillsTextView.setText(skillsField.getString("key"));
            ArrayList<String> addedSkills = new ArrayList<>();
            ArrayList<String> addedPercentage = new ArrayList<>();
            for (int i = 0; i < selectedSkillsArray.length(); i++) {
                JSONObject skill = selectedSkillsArray.getJSONObject(i);
                addedSkills.add(skill.getString("name"));
                addedPercentage.add(skill.getString("Percent value"));

            }

            String[] skillsArray = new String[addedSkills.size()];
            skillsArray = addedSkills.toArray(skillsArray);
            mTagsEditText.setTags(skillsArray);

            String[] percentageArray = new String[addedPercentage.size()];
            percentageArray = addedPercentage.toArray(percentageArray);
            percentageTagsEditText.setTags(percentageArray);

            selectPercentTextView.setText(percentageField.getString("key"));
            JSONArray allPercentageArray = percentageField.getJSONArray("value");
            JSONArray allSkillsArray = skillsField.getJSONArray("value");

            selecPercentage = new ArrayList<>();
            selecSkills = new ArrayList<>();

            for (int i = 0; i < allSkillsArray.length(); i++) {
                Nokri_SkillsModel model = new Nokri_SkillsModel();
                JSONObject skill = allSkillsArray.getJSONObject(i);
                model.setId(skill.getInt("key"));
                model.setName(skill.getString("value"));
                selecSkills.add(skill.getString("value"));
                skillsModelList.add(model);


            }

            for (int i = 0; i < allPercentageArray.length(); i++) {
                Nokri_SkillsModel model = new Nokri_SkillsModel();
                JSONObject skill = allPercentageArray.getJSONObject(i);
                model.setId(skill.getInt("value"));
                model.setName(skill.getString("value"));
                selecPercentage.add(skill.getString("value"));
                percentageModelList.add(model);


            }


            mTagsEditText.setAdapter(new ArrayAdapter<>(getActivity().getBaseContext(),
                    android.R.layout.simple_dropdown_item_1line, selecSkills));
            percentageTagsEditText.setAdapter(new ArrayAdapter<>(getActivity().getBaseContext(),
                    android.R.layout.simple_dropdown_item_1line, selecPercentage));

            dialogManager.hideAlertDialog();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }

    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(addSkillsTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(selectSkillsTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(selectPercentTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(saveSkillButton, getActivity().getAssets());
    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();
        addSkillsTextView = getView().findViewById(R.id.txt_add_skills);
        selectSkillsTextView = getView().findViewById(R.id.txt_select_skill);
        selectPercentTextView = getView().findViewById(R.id.txt_select_percent);
        saveSkillButton = getView().findViewById(R.id.btn_saveskills);
        Nokri_Utils.setEditBorderButton(getContext(), saveSkillButton);

        mTagsEditText = getView().findViewById(R.id.tagsEditText);
        percentageTagsEditText = getView().findViewById(R.id.percentageTagsEditText);
        //mTagsEditText.setOnFocusChangeListener(this);
        //percentageTagsEditText.setOnFocusChangeListener(this);
        mTagsEditText.setTagsWithSpacesEnabled(true);
        percentageTagsEditText.setTagsWithSpacesEnabled(true);
        mTagsEditText.setThreshold(1);
        percentageTagsEditText.setThreshold(1);
        saveSkillButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        nokri_postSkills();
    }

    private ArrayList<String> getIdsFromTags() {
        ArrayList<String> ids = new ArrayList<>();
        List<String> tags = mTagsEditText.getTags();

        if (!tags.isEmpty() && tags != null) {
            for (int i = 0; i < skillsModelList.size(); i++) {
                String tag = skillsModelList.get(i).getName();
                String id = skillsModelList.get(i).getId() + "";
                for (int j = 0; j < tags.size(); j++) {
                    if (tags.get(j).equals(tag))
                        ids.add(id);
                }


            }


        }

        return ids;
    }

    private ArrayList<String> getSkillIdsFromTags() {
        ArrayList<String> ids = new ArrayList<>();
        List<String> tags = percentageTagsEditText.getTags();

        if (!tags.isEmpty() && tags != null) {
            for (int i = 0; i < percentageModelList.size(); i++) {
                String tag = percentageModelList.get(i).getName();
                String id = percentageModelList.get(i).getId() + "";

                for (int j = 0; j < tags.size(); j++) {
                    if (tags.get(j).equals(tag))
                        ids.add(id);
                }


            }


        }

        return ids;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {

                case R.id.tagsEditText:

                    mTagsEditText.setActivated(true);
                    break;
                case R.id.percentageTagsEditText:

                    percentageTagsEditText.setActivated(true);
                    break;
            }
        }

    }
}
