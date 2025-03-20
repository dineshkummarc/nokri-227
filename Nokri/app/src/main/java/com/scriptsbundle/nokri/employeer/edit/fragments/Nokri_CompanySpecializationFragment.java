package com.scriptsbundle.nokri.employeer.edit.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_SkillsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import mabbas007.tagsedittext.TagsEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CompanySpecializationFragment extends Fragment implements TagsEditText.TagsEditListener, View.OnFocusChangeListener, View.OnClickListener, View.OnTouchListener, DatePickerDialog.OnDateSetListener {
    private Nokri_FontManager fontManager;
    private TagsEditText mTagsEditText;
    private TextView categoriesTextView, noOfEmployeesTextView, establishedTextView;
    private EditText noOfEmployeesEditText, establishedEditText;
    private Button saveSpecializationButton;
    private ArrayList<Nokri_SkillsModel> skillsModelList;
    private ArrayList<String> selecSkills;
    private Calendar calendar = Calendar.getInstance();
    private Nokri_DialogManager dialogManager;

    public Nokri_CompanySpecializationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTagsEditText = (TagsEditText) getView().findViewById(R.id.tagsEditText);
        //mTagsEditText.setHint("Enter names of fruits");
        mTagsEditText.setTagsListener(this);
        mTagsEditText.setTagsWithSpacesEnabled(true);
        mTagsEditText.setAdapter(new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.skills)));
        mTagsEditText.setThreshold(1);
        nokri_initialize();
        nokri_setFonts();
        try {
            nokri_getEmployeerSkills();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void nokri_setFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(categoriesTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(noOfEmployeesTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(noOfEmployeesEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(saveSpecializationButton, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(establishedEditText, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(establishedTextView, getActivity().getAssets());

    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();
        categoriesTextView = getView().findViewById(R.id.txt_categories);
        noOfEmployeesTextView = getView().findViewById(R.id.txt_num);
        noOfEmployeesEditText = getView().findViewById(R.id.edittxt_num);
        establishedEditText = getView().findViewById(R.id.edittxt_established);
        saveSpecializationButton = getView().findViewById(R.id.btn_savespecialization);
        establishedTextView = getView().findViewById(R.id.txt_established);
        noOfEmployeesEditText.setOnFocusChangeListener(this);
        saveSpecializationButton.setOnClickListener(this);
        establishedEditText.setOnFocusChangeListener(this);

        establishedEditText.setOnTouchListener(this);

        Nokri_Utils.setEditBorderButton(getContext(), saveSpecializationButton);


        fontManager = new Nokri_FontManager();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_company_specialization, container, false);
    }

    @Override
    public void onTagsChanged(Collection<String> collection) {

    }

    @Override
    public void onEditingFinished() {

    }


    private void nokri_getEmployeerSkills() throws JSONException {
        dialogManager = new Nokri_DialogManager();
        skillsModelList = new ArrayList<>();
        JSONObject response = Nokri_CompanyEditProfileFragment.companySpecialization;
        if (response.getBoolean("success")) {

            JSONArray extras = response.getJSONArray("extras");
            Log.d("something", extras.toString());
            for (int i = 0; i < extras.length(); i++) {
                JSONObject extra = extras.getJSONObject(i);
                if (extra.getString("field_type_name").equals("skill_txt")) {
                    categoriesTextView.setText(extra.getString("value"));
                    mTagsEditText.setHint(extra.getString("key"));
                } else if (extra.getString("field_type_name").equals("section_name")) {
                    noOfEmployeesEditText.setHint(extra.getString("key"));


                } else if (extra.getString("field_type_name").equals("btn_name")) {

                    saveSpecializationButton.setText(extra.getString("value"));
                } else if (extra.getString("field_type_name").equals("est_txt")) {

                    establishedEditText.setHint(extra.getString("key"));
                }
            }

            //      addSkillsTextView.setText(pageTitle.getString("key") + ":");

            //    saveSkillButton.setText(buttonName.getString("key"));
            JSONObject dataObject = response.getJSONObject("data");
            JSONObject numOfEmployeesObject = dataObject.getJSONObject("employes_field");


            noOfEmployeesEditText.setText(numOfEmployeesObject.getString("value"));
            noOfEmployeesTextView.setText(numOfEmployeesObject.getString("key"));

            establishedEditText.setText(dataObject.getJSONObject("establish").getString("value"));
            establishedTextView.setText(dataObject.getJSONObject("establish").getString("key"));
            establishedEditText.setHint(dataObject.getJSONObject("establish").getString("key"));


            JSONArray selectedSkillsArray = dataObject.getJSONArray("skills_selected");
            JSONObject skillsField = dataObject.getJSONObject("skills_field");
            //  selectSkillsTextView.setText(skillsField.getString("key"));
            ArrayList<String> addedSkills = new ArrayList<>();
            for (int i = 0; i < selectedSkillsArray.length(); i++) {
                JSONObject skill = selectedSkillsArray.getJSONObject(i);
                addedSkills.add(skill.getString("value"));

            }
            String[] skillsArray = new String[addedSkills.size()];
            skillsArray = addedSkills.toArray(skillsArray);
            mTagsEditText.setTags(skillsArray);

/*
       else  if(jsonObject.getString("field_type_name").equals("emp_est")){
                                establishedEditText.setText(jsonObject.getString("value"));
                                establishedTextView.setText(jsonObject.getString("key"));
                                establishedEditText.setHint(jsonObject.getString("key"));
                            }*/
/*
                                else  if(jsonObject.getString("field_type_name").equals("emp_est")){
                                establishedEditText.setText(jsonObject.getString("value"));
                                establishedTextView.setText(jsonObject.getString("key"));
                                establishedEditText.setHint(jsonObject.getString("key"));
                            }
*/


            JSONArray allSkillsArray = skillsField.getJSONArray("value");

            selecSkills = new ArrayList<>();

            for (int i = 0; i < allSkillsArray.length(); i++) {
                Nokri_SkillsModel model = new Nokri_SkillsModel();
                JSONObject skill = allSkillsArray.getJSONObject(i);
                model.setId(skill.getInt("key"));
                model.setName(skill.getString("value"));
                selecSkills.add(skill.getString("value"));
                skillsModelList.add(model);

            }
            mTagsEditText.setAdapter(new ArrayAdapter<>(getActivity().getBaseContext(),
                    android.R.layout.simple_dropdown_item_1line, selecSkills));

            dialogManager.hideAlertDialog();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }
    }

    private void nokri_postSkills() {
        ArrayList<String> ids = getIdsFromTags();
        if (ids == null || ids.isEmpty() || noOfEmployeesEditText.getText().toString().trim().isEmpty()) {
            mTagsEditText.setError("!");
            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            return;
        }
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        for (int i = 0; i < ids.size(); i++) {

            array.add(ids.get(i));

        }

        object.add("emp_skills", array);
        if (noOfEmployeesEditText.getText().toString() != null || !noOfEmployeesEditText.getText().toString().trim().isEmpty() || establishedEditText.getText().toString() != null || !establishedEditText.getText().toString().trim().isEmpty()) {
            object.addProperty("emp_nos", noOfEmployeesEditText.getText().toString());
            object.addProperty("emp_establish", establishedEditText.getText().toString());
        } else {
            Nokri_Utils.checkEditTextForError(noOfEmployeesEditText);
            Nokri_Utils.checkEditTextForError(establishedEditText);
            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            return;
        }


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmployeerSkills(object, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postEmployeerSkills(object, Nokri_RequestHeaderManager.addHeaders());
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
                            nokri_getEmployeerSkills();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));

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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
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

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_num:
                if (selected) {
                    noOfEmployeesEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    establishedEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;

            case R.id.edittxt_established:
                if (selected) {

                    establishedEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                    noOfEmployeesEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        nokri_postSkills();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.edittxt_established:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;

                }


        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        establishedEditText.setText(sdf.format(calendar.getTime()));
        establishedEditText.setError(null);
    }
}
