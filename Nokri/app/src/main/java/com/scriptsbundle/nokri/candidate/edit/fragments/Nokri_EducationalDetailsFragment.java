package com.scriptsbundle.nokri.candidate.edit.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_EducationalDetailModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_EducationalDetailsFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener {

    private LinearLayout container;
    private Nokri_FontManager fontManager;
    private TextView educationalDetailTextView, degreeTitleTextView, instituteNameTextView, degreeStartTextView, degreeEndTextView, percentageTextView, gradesTextView, descriptonTextView;
    private EditText degreeTitleEditText, instituteNameEditText, startDateEditText, endDateEditText, percentageEditText, gradesEditText;
    //private ImageView boldImageView,italicImageView,underlineImageView,numberBulletsImageView,listBulletsImageView;
    private ImageButton plustImageButton, minusImageButton;

    private Button saveEducatioButton;
    private Calendar calendar;
    private int flag;
    private List<Nokri_EducationalDetailModel> educationList, postEducationList;
    private int counter;
    private String degreeTitle, instituteName, startDate, endDate, percentage, grades, detail, buttonText, title;
    private Nokri_EducationalDetailModel addMoreModel = new Nokri_EducationalDetailModel();
    private boolean areAllFieldsFilled;
    private boolean isRichEditorEmpty;

    private ArrayList<EditText> listofStartDates = new ArrayList<>();
    private ArrayList<EditText> listofEndDates = new ArrayList<>();
    private int currentIndex;

    private String degreeTitleHint, degreeStartHint, degreeEndHint, degreeInstituteHint, degreePercentHint, degreeGradeHint, degreeDetailHint;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_EducationalDetailsFragment() {

    }

    private void nokri_initialize(View view, Nokri_EducationalDetailModel model) {
        fontManager = new Nokri_FontManager();

        educationalDetailTextView = view.findViewById(R.id.txt_educational_detail);
        degreeTitleTextView = view.findViewById(R.id.txt_degree_title);
        instituteNameTextView = view.findViewById(R.id.txt_institute_name);
        degreeStartTextView = view.findViewById(R.id.txt_degree_start);
        degreeEndTextView = view.findViewById(R.id.txt_degree_end);
        percentageTextView = view.findViewById(R.id.txt_percentage);
        gradesTextView = view.findViewById(R.id.txt_grades);
        descriptonTextView = view.findViewById(R.id.txt_description);

        educationalDetailTextView.setText(title);
        degreeTitleTextView.setText(degreeTitle);
        instituteNameTextView.setText(instituteName);
        degreeStartTextView.setText(startDate);
        degreeEndTextView.setText(endDate);
        percentageTextView.setText(percentage);
        gradesTextView.setText(grades);
        descriptonTextView.setText(detail);

//        fontManager.nokri_setMonesrratSemiBioldFont(educationalDetailTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(degreeTitleTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(instituteNameTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(degreeStartTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(degreeEndTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(percentageTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(gradesTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(descriptonTextView, getActivity().getAssets());


        degreeTitleEditText = view.findViewById(R.id.edittxt_degree_title);
        instituteNameEditText = view.findViewById(R.id.edittxt_institute_name);
        startDateEditText = view.findViewById(R.id.edittxt_start_date);


        endDateEditText = view.findViewById(R.id.edittxt_end_date);
        percentageEditText = view.findViewById(R.id.edittxt_percentage);
        gradesEditText = view.findViewById(R.id.edittxt_grades);
        final RichEditor descriptonEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
        LinearLayout textarea = view.findViewById(R.id.textarea);
        descriptonEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        descriptonEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));


//        fontManager.nokri_setOpenSenseFontEditText(degreeTitleEditText, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(instituteNameEditText, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(startDateEditText, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(endDateEditText, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(percentageEditText, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(gradesEditText, getActivity().getAssets());


        if (model != null) {
            degreeTitleEditText.setText(model.getDegreeTitle());
            instituteNameEditText.setText(model.getInstituteName());
            startDateEditText.setText(model.getStartData());
            endDateEditText.setText(model.getEndDate());

            percentageEditText.setText(model.getPercentage());
            gradesEditText.setText(model.getGrades());
            descriptonEditText.setHtml(model.getDescription());
        }


        degreeTitleEditText.setOnFocusChangeListener(this);
        instituteNameEditText.setOnFocusChangeListener(this);
        startDateEditText.setOnFocusChangeListener(this);
        endDateEditText.setOnFocusChangeListener(this);
        percentageEditText.setOnFocusChangeListener(this);
        gradesEditText.setOnFocusChangeListener(this);
        degreeTitleEditText.setOnFocusChangeListener(this);


        calendar = Calendar.getInstance();

        descriptonEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));

        descriptonEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));


        ImageView boldImageView = view.findViewById(R.id.img_bold);
        ImageView italicImageView = view.findViewById(R.id.img_italic);
        ImageView underlineImageView = view.findViewById(R.id.img_underline);
        ImageView numberBulletsImageView = view.findViewById(R.id.img_num_bullets);
        ImageView listBulletsImageView = view.findViewById(R.id.img_list_bullets);


        boldImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.setBold();
            }
        });
        italicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.setItalic();
            }
        });
        underlineImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.setUnderline();
            }
        });
        numberBulletsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.setNumbers();
            }
        });
        listBulletsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.setBullets();
            }
        });
        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptonEditText.focusEditor();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });

        plustImageButton = view.findViewById(R.id.img_btn_plus);


        plustImageButton.setOnClickListener(this);

        nokri_setHints(view);


    }

    private void nokri_setHints(View view) {

        EditText degreeTitleEditText = view.findViewById(R.id.edittxt_degree_title);
        EditText instituteNameEditText = view.findViewById(R.id.edittxt_institute_name);
        EditText startDateEditText = view.findViewById(R.id.edittxt_start_date);
        EditText endDateEditText = view.findViewById(R.id.edittxt_end_date);
        EditText percentageEditText = view.findViewById(R.id.edittxt_percentage);
        EditText gradesEditText = view.findViewById(R.id.edittxt_grades);
        RichEditor descriptonEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
        degreeTitleEditText.setHint(degreeTitleHint);
        instituteNameEditText.setHint(degreeInstituteHint);
        startDateEditText.setHint(degreeStartHint);
        endDateEditText.setHint(degreeEndHint);
        percentageEditText.setHint(degreePercentHint);
        gradesEditText.setHint(degreeGradeHint);
        descriptonEditText.setPlaceholder(degreeDetailHint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nokri_educational_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
        container = getView().findViewById(R.id.container);
        saveEducatioButton = getView().findViewById(R.id.btn_saveeducation);
        Nokri_Utils.setEditBorderButton(getContext(), saveEducatioButton);
        saveEducatioButton.setOnClickListener(this);
        dialogManager = new Nokri_DialogManager();
        try {
            nokri_getAddMore();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void nokri_getEducation() throws JSONException {
        educationList = new ArrayList<>();
        JSONObject response = Nokri_CandidateEditProfileFragment.educationGet;

        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray educationArray = dataObject.getJSONArray("education");
            JSONArray extrasArray = dataObject.getJSONArray("extras");
            for (int i = 0; i < extrasArray.length(); i++) {
                JSONObject jsonObject = extrasArray.getJSONObject(i);
                if (jsonObject.getString("field_type_name").equals("section_name")) {
                    //   ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(jsonObject.getString("value"));
                    //  Nokri_Globals.CANDIDATE_EDUCATIONAL_DETAILS_TITLE = jsonObject.getString("value");
                    title = jsonObject.getString("value") + ":";
                } else if (jsonObject.getString("field_type_name").equals("btn_name")) {
                    buttonText = jsonObject.getString("value");
                    saveEducatioButton.setText(buttonText);
                    new Nokri_FontManager().nokri_setOpenSenseFontButton(saveEducatioButton, getActivity().getAssets());
                }
            }


            for (int i = 0; i < educationArray.length(); i++) {
                JSONArray dataArray = educationArray.getJSONArray(i);
                Nokri_EducationalDetailModel model = new Nokri_EducationalDetailModel();
                for (int j = 0; j < dataArray.length(); j++) {

                    JSONObject data = dataArray.getJSONObject(j);

                    if (data.getString("field_type_name").equals("degree_name")) {
                        model.setDegreeTitle(data.getString("value"));
                        degreeTitle = data.getString("key");
                        degreeTitleHint = data.getString("is_show");

                    } else if (data.getString("field_type_name").equals("degree_start")) {
                        model.setStartData(data.getString("value"));
                        startDate = data.getString("key");
                        degreeStartHint = data.getString("is_show");
                    } else if (data.getString("field_type_name").equals("degree_end")) {
                        model.setEndDate(data.getString("value"));
                        endDate = data.getString("key");
                        degreeEndHint = data.getString("is_show");
                    } else if (data.getString("field_type_name").equals("degree_institute")) {
                        model.setInstituteName(data.getString("value"));
                        instituteName = data.getString("key");
                        degreeInstituteHint = data.getString("is_show");
                    } else if (data.getString("field_type_name").equals("degree_detail")) {
                        detail = data.getString("key");

                        model.setDescription(data.getString("value"));
                        degreeDetailHint = data.getString("is_show");
                    } else if (data.getString("field_type_name").equals("degree_grade")) {
                        model.setGrades(data.getString("value"));
                        grades = data.getString("key");
                        degreeGradeHint = data.getString("is_show");
                    } else if (data.getString("field_type_name").equals("degree_percent")) {
                        model.setPercentage(data.getString("value"));
                        percentage = data.getString("key");
                        degreePercentHint = data.getString("is_show");
                    }

                    if (j + 1 == dataArray.length())
                        educationList.add(model);
                }
            }

            dialogManager.hideAlertDialog();
            nokri_setDynamicForms();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }

    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_degree_title:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_institute_name:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_start_date:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));


                }
                break;
            case R.id.edittxt_end_date:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));


                }
                break;
            case R.id.edittxt_percentage:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_grades:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey_500));

                }
                break;
            case R.id.edittxt_descripton:
                if (selected) {
                    degreeTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteNameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    percentageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    gradesEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
        }
    }

    private void nokri_setDynamicForms() {

        for (int i = 0; i < educationList.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.fragment_nokri_educational_details_holder, container, false);

            container.addView(view);
            saveEducatioButton.setVisibility(View.VISIBLE);
            nokri_initialize(view, educationList.get(i));


        }
        nokri_resetTags(container);


    }

    private void nokri_iniflateDynamicViews() {
        View view = getLayoutInflater().inflate(R.layout.fragment_nokri_educational_details_holder, container, false);
        ImageButton minusImageButton = view.findViewById(R.id.img_btn_minus);

        minusImageButton.setOnClickListener(this);
        container.addView(view);


        ++counter;


        nokri_initialize(view, null);
        nokri_resetTags(container);
    }

    private boolean nokri_areFieldsEmpty(ArrayList<Boolean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i))
                return list.get(i);
        }
        return true;
    }

    private boolean nokri_areRichEditorsEmpty(ArrayList<Boolean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i))
                return list.get(i);
        }
        return true;
    }


    private void nokri_getDataFromAllChilds() {
        ArrayList<Boolean> fieldsList = new ArrayList<>();
        ArrayList<Boolean> editorList = new ArrayList<>();
        postEducationList = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            boolean isRichEditorEmpty;
            boolean areAllFieldsFilled;
            View view = container.getChildAt(i);
            EditText degreeTitleEditText = view.findViewById(R.id.edittxt_degree_title);
            EditText instituteNameEditText = view.findViewById(R.id.edittxt_institute_name);
            EditText startDateEditText = view.findViewById(R.id.edittxt_start_date);
            EditText endDateEditText = view.findViewById(R.id.edittxt_end_date);
            EditText percentageEditText = view.findViewById(R.id.edittxt_percentage);
            EditText gradesEditText = view.findViewById(R.id.edittxt_grades);
            RichEditor descriptonEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
            View line = view.findViewById(R.id.line);
            Nokri_Utils.checkEditTextForError(degreeTitleEditText);
            Nokri_Utils.checkEditTextForError(instituteNameEditText);
            Nokri_Utils.checkEditTextForError(startDateEditText);
            Nokri_Utils.checkEditTextForError(endDateEditText);
            Nokri_Utils.checkEditTextForError(percentageEditText);
            Nokri_Utils.checkEditTextForError(gradesEditText);
            Nokri_EducationalDetailModel model = new Nokri_EducationalDetailModel();

            String degreeTitle = degreeTitleEditText.getText().toString();
            String instituteName = instituteNameEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String endData = endDateEditText.getText().toString();
            String percentage = percentageEditText.getText().toString();
            String grades = gradesEditText.getText().toString();
            String description = descriptonEditText.getHtml();

            if (description != null && !description.trim().isEmpty()) {
                isRichEditorEmpty = false;
                line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                if (!degreeTitle.trim().isEmpty() && !instituteName.trim().isEmpty() && !startDate.trim().isEmpty() && !endData.trim().isEmpty() && !percentage.trim().isEmpty() && !grades.trim().isEmpty() && !description.trim().isEmpty()) {
                    model.setDegreeTitle(degreeTitle);
                    model.setInstituteName(instituteName);
                    model.setStartData(startDate);
                    model.setEndDate(endData);
                    model.setPercentage(percentage);
                    model.setGrades(grades);
                    model.setDescription(description);
                    postEducationList.add(model);
                    areAllFieldsFilled = true;
                } else {
                    line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    areAllFieldsFilled = false;
                    isRichEditorEmpty = false;
                }
            } else {
                line.setBackgroundColor(Color.RED);
                Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                areAllFieldsFilled = false;
                isRichEditorEmpty = true;
            }

            fieldsList.add(areAllFieldsFilled);
            editorList.add(isRichEditorEmpty);

        }
        if (nokri_areFieldsEmpty(fieldsList) && !nokri_areRichEditorsEmpty(editorList))
            nokri_postEducation();


    }

    private void nokri_postEducation() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();
        for (int i = 0; i < postEducationList.size(); i++) {


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("degree_name", postEducationList.get(i).getDegreeTitle());
            jsonObject.addProperty("degree_institute", postEducationList.get(i).getInstituteName());
            jsonObject.addProperty("degree_start", postEducationList.get(i).getStartData());
            jsonObject.addProperty("degree_end", postEducationList.get(i).getEndDate());
            jsonObject.addProperty("degree_percent", postEducationList.get(i).getPercentage());
            jsonObject.addProperty("degree_grade", postEducationList.get(i).getGrades());
            jsonObject.addProperty("degree_detail", postEducationList.get(i).getDescription());
            params.add(jsonObject);


        }


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidateEducation(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postCandidateEducation(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //   Call<ResponseBody> myCall = restService.postCandidateEducation(params, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
//                            container.removeAllViews();
//                            nokri_getEducation();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
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

    private void nokri_getAddMore() throws JSONException {
        JSONObject response = Nokri_CandidateEditProfileFragment.educationGetMore;
        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray educationArray = dataObject.getJSONArray("education");
            for (int i = 0; i < educationArray.length(); i++) {
                JSONArray dataArray = educationArray.getJSONArray(i);

                for (int j = 0; j < dataArray.length(); j++) {

                    JSONObject data = dataArray.getJSONObject(j);

                    if (data.getString("field_type_name").equals("degree_name"))
                        addMoreModel.setDegreeTitle(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_start"))
                        addMoreModel.setStartData(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_end"))
                        addMoreModel.setEndDate(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_institute"))
                        addMoreModel.setInstituteName(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_detail"))
                        addMoreModel.setDescription(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_grade"))
                        addMoreModel.setGrades(data.getString("value"));
                    else if (data.getString("field_type_name").equals("degree_percent"))
                        addMoreModel.setPercentage(data.getString("value"));


                }
            }
            dialogManager.hideAlertDialog();
            nokri_getEducation();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }
    }


    @Override
    public void onClick(final View view) {
        switch (view.getId()) {

            case R.id.btn_saveeducation:

                nokri_getDataFromAllChilds();
                break;
            case R.id.img_btn_plus:
                nokri_iniflateDynamicViews();
                break;
            case R.id.img_btn_minus:

                Nokri_PopupManager popupManager = new Nokri_PopupManager(getContext(), new Nokri_PopupManager.ConfirmInterface() {
                    @Override
                    public void onConfirmClick(Dialog dialog) {
                        View v = container.getChildAt(Integer.parseInt(view.getTag().toString()));

                        if (v != null) {

                            listofStartDates.remove(Integer.parseInt(view.getTag().toString()));
                            listofEndDates.remove(Integer.parseInt(view.getTag().toString()));
                            container.removeView(v);

                            if (container.getChildCount() == 0) {
                                nokri_iniflateDynamicViews();
                            }
                            nokri_resetTags(container);
                        }

                        if (v == null) {
                            View view1 = container.getChildAt(container.getChildCount() - 1);
                            listofStartDates.remove(container.getChildCount() - 1);
                            listofEndDates.remove(container.getChildCount() - 1);
                            container.removeView(view1);

                            if (container.getChildCount() == 0) {
                                nokri_iniflateDynamicViews();
                            }
                            nokri_resetTags(container);
                        }

                        dialog.dismiss();
                    }
                });
                popupManager.nokri_showDeletePopup();


                break;
            default:
                break;
        }
    }

    private void nokri_resetTags(LinearLayout container) {
        listofStartDates.clear();
        listofEndDates.clear();
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            v.setTag(i);
                   /* listofStartDates.get(i).setTag(i);
                    listofEndDates.get(i).setTag(i);*/
            ImageButton minusImageButton = v.findViewById(R.id.img_btn_minus);
            minusImageButton.setTag(i);
            EditText startDate = v.findViewById(R.id.edittxt_start_date);
            startDate.setOnTouchListener(this);
            startDate.setTag(i);

            EditText endDate = v.findViewById(R.id.edittxt_end_date);
            endDate.setTag(i);
            endDate.setOnTouchListener(this);

            listofStartDates.add(startDate);
            listofEndDates.add(endDate);
            minusImageButton.setOnClickListener(this);
            if (educationList.size() == 1 && counter == 0) {
                minusImageButton.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);

        String myFormat = "MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (flag == 0) {


            listofStartDates.get(currentIndex).setText(sdf.format(calendar.getTime()));
            listofStartDates.get(currentIndex).setError(null);
        } else if (flag == 1) {

            listofEndDates.get(currentIndex).setText(sdf.format(calendar.getTime()));
            listofEndDates.get(currentIndex).setError(null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.edittxt_start_date:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    currentIndex = Integer.parseInt(v.getTag().toString());
                    Log.v("taggggggggggggggggggggc", currentIndex + " " + listofStartDates.size());
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 0;
                }
                break;
            case R.id.edittxt_end_date:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    currentIndex = Integer.parseInt(v.getTag().toString());
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 1;
                }
                break;
        }
        return false;
    }

}

