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
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_CertificationModel;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_EducationalDetailModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
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
public class Nokri_CertificationsDetailsFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener {
    private Nokri_FontManager fontManager;
    private TextView certificationDetailTextView, certificationTitleTextView, projectStartTextView, projectEndTextView, durationTextView, instituteTextView, descriptionTextView;
    private EditText certificationTitleEditText, startDateEditText, endDateEditText, durationEditText, instituteEditText;
    //   private ImageView boldImageView,italicImageView,underlineImageView,numberBulletsImageView,listBulletsImageView;
    private ImageButton plustImageButton, minusImageButton;
    private Calendar calendar;
    private Nokri_DialogManager dialogManager;


    private List<Nokri_CertificationModel> certificationList, postCertificationList;
    private Nokri_CertificationModel addMoreModel = new Nokri_CertificationModel();
    private int counter;
    private int flag;
    private LinearLayout container;
    private boolean areAllFieldsFilled;
    private Button saveCertificationButton;
    private String name, start, end, duration, institute, desc, title;
    private ArrayList<EditText> listofStartDates = new ArrayList<>();
    private ArrayList<EditText> listofEndDates = new ArrayList<>();
    private int currentIndex;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_CertificationsDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        container = getView().findViewById(R.id.container);
        dialogManager = new Nokri_DialogManager();
        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
        saveCertificationButton = getView().findViewById(R.id.btn_savecertification);
        Nokri_Utils.setEditBorderButton(getContext(), saveCertificationButton);
        saveCertificationButton.setOnClickListener(this);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(saveCertificationButton, getActivity().getAssets());

        try {
            nokri_getAddMore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nokri_initialize(View view, Nokri_CertificationModel model) {
        fontManager = new Nokri_FontManager();

        certificationDetailTextView = view.findViewById(R.id.txt_certification_detail);
        certificationTitleTextView = view.findViewById(R.id.txt_certification_title);
        durationTextView = view.findViewById(R.id.txt_duration);
        projectStartTextView = view.findViewById(R.id.txt_project_start);
        projectEndTextView = view.findViewById(R.id.txt_project_end);
        instituteTextView = view.findViewById(R.id.txt_institute);
        descriptionTextView = view.findViewById(R.id.txt_description);

        certificationDetailTextView.setText(title);
        certificationTitleTextView.setText(name);
        durationTextView.setText(duration);
        projectStartTextView.setText(start);
        projectEndTextView.setText(end);
        instituteTextView.setText(institute);
        descriptionTextView.setText(desc);

//        fontManager.nokri_setMonesrratSemiBioldFont(certificationDetailTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(certificationTitleTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(durationTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(projectStartTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(projectEndTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(instituteTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(descriptionTextView,getActivity().getAssets());


        certificationTitleEditText = view.findViewById(R.id.edittxt_certification_title);
        durationEditText = view.findViewById(R.id.edittxt_duraion);
        startDateEditText = view.findViewById(R.id.edittxt_start_date);
        endDateEditText = view.findViewById(R.id.edittxt_end_date);
        instituteEditText = view.findViewById(R.id.edittxt_institute);

        final RichEditor descriptionEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
        LinearLayout textarea = view.findViewById(R.id.textarea);

//        fontManager.nokri_setOpenSenseFontEditText(certificationTitleEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(durationEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(startDateEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(endDateEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(instituteEditText,getActivity().getAssets());


        if (model != null) {
            certificationTitleEditText.setText(model.getTitle());
            durationEditText.setText(model.getDuration());
            startDateEditText.setText(model.getStartData());
            endDateEditText.setText(model.getEndDate());
            instituteEditText.setText(model.getInstitute());

            descriptionEditText.setHtml(model.getDescription());
        } else if (model == null) {
            certificationTitleEditText.setHint(addMoreModel.getTitle());
            durationEditText.setHint(addMoreModel.getDuration());
            startDateEditText.setHint(addMoreModel.getStartData());
            endDateEditText.setHint(addMoreModel.getEndDate());

            instituteEditText.setHint(addMoreModel.getInstitute());
            descriptionEditText.setHtml(addMoreModel.getDescription());

        }


        certificationTitleEditText.setOnFocusChangeListener(this);
        durationEditText.setOnFocusChangeListener(this);
        startDateEditText.setOnFocusChangeListener(this);
        endDateEditText.setOnFocusChangeListener(this);
        instituteEditText.setOnFocusChangeListener(this);
        descriptionEditText.setOnFocusChangeListener(this);


        certificationTitleEditText.setHint(name);
        durationEditText.setHint(duration);
        startDateEditText.setHint(start);
        endDateEditText.setHint(end);
        instituteEditText.setHint(institute);
        descriptionEditText.setPlaceholder(desc);


        calendar = Calendar.getInstance();

        descriptionEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));
        descriptionEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        //    descriptionEditText.setBackground(getActivity().getResources().getDrawable(R.drawable.circle));


        ImageView boldImageView = view.findViewById(R.id.img_bold);
        ImageView italicImageView = view.findViewById(R.id.img_italic);
        ImageView underlineImageView = view.findViewById(R.id.img_underline);
        ImageView numberBulletsImageView = view.findViewById(R.id.img_num_bullets);
        ImageView listBulletsImageView = view.findViewById(R.id.img_list_bullets);


        boldImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setBold();
            }
        });
        italicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setItalic();
            }
        });
        underlineImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setUnderline();
            }
        });
        numberBulletsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setNumbers();
            }
        });
        listBulletsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setBullets();
            }
        });
        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.focusEditor();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });
       /* italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);
*/

        plustImageButton = view.findViewById(R.id.img_btn_plus);
        //ImageButton minusImageButton = view.findViewById(R.id.img_btn_minus);
     /*   if(container.getChildCount()==0)
            minusImageButton.setTag(0);
        else

            minusImageButton.setTag(container.getChildCount()-1);
        // minusImageButton.setTag(counter);*/
        plustImageButton.setOnClickListener(this);
    /*    minusImageButton.setOnClickListener(this);
        if(certificationList.size()==1 &&counter == 0){
            minusImageButton.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_certification_title:
                if (selected) {
                    certificationTitleEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    durationEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteEditText.setHintTextColor(getResources().getColor(R.color.grey));


                }
                break;
            case R.id.edittxt_duraion:
                if (selected) {
                    certificationTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    durationEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteEditText.setHintTextColor(getResources().getColor(R.color.grey));


                }
                break;
            case R.id.edittxt_start_date:
                if (selected) {
                    certificationTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    durationEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteEditText.setHintTextColor(getResources().getColor(R.color.grey));

/*
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 0;*/
                }
                break;
            case R.id.edittxt_end_date:
                if (selected) {
                    certificationTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    durationEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    instituteEditText.setHintTextColor(getResources().getColor(R.color.grey));


                   /* new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 1;*/
                }
                break;
            case R.id.edittxt_institute:
                if (selected) {
                    certificationTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    durationEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    instituteEditText.setHintTextColor(getResources().getColor(R.color.grey_500));

                }
                break;


        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {

            case R.id.btn_savecertification:

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


    private void nokri_postCertification() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();
        for (int i = 0; i < postCertificationList.size(); i++) {


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("certification_name", postCertificationList.get(i).getTitle());
            jsonObject.addProperty("certification_start", postCertificationList.get(i).getStartData());
            jsonObject.addProperty("certification_end", postCertificationList.get(i).getEndDate());
            jsonObject.addProperty("certification_duration", postCertificationList.get(i).getDuration());
            jsonObject.addProperty("certification_institute", postCertificationList.get(i).getInstitute());
            jsonObject.addProperty("certification_desc", postCertificationList.get(i).getDescription());

            params.add(jsonObject);
        }


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidateCertification(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postCandidateCertification(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateCertification(params, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());

                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
//                            container.removeAllViews();
//                            nokri_getProfession();
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
        JSONObject response = Nokri_CandidateEditProfileFragment.certificationsGetMore;
        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray educationArray = dataObject.getJSONArray("certification");
            for (int i = 0; i < educationArray.length(); i++) {
                JSONArray dataArray = educationArray.getJSONArray(i);
                Nokri_EducationalDetailModel model = new Nokri_EducationalDetailModel();
                for (int j = 0; j < dataArray.length(); j++) {
                    JSONObject data = dataArray.getJSONObject(j);
                    if (data.getString("field_type_name").equals("certification_name"))
                        addMoreModel.setTitle(data.getString("value"));
                    else if (data.getString("field_type_name").equals("certification_start"))
                        addMoreModel.setStartData(data.getString("value"));
                    else if (data.getString("field_type_name").equals("certification_end"))
                        addMoreModel.setEndDate(data.getString("value"));
                    else if (data.getString("field_type_name").equals("certification_duration"))
                        addMoreModel.setDuration(data.getString("value"));
                    else if (data.getString("field_type_name").equals("certification_institute"))
                        addMoreModel.setInstitute(data.getString("value"));
                    else if (data.getString("field_type_name").equals("certification_desc"))
                        addMoreModel.setDescription(data.getString("value"));
                }
            }
            dialogManager.hideAlertDialog();
            nokri_getProfession();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }
    }

    private void nokri_getProfession() throws JSONException {
        certificationList = new ArrayList<>();
        JSONObject response = Nokri_CandidateEditProfileFragment.certificationsGet;

        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray extrasArray = dataObject.getJSONArray("extras");
                            /*certificationDetailTextView
                                    title missing*/
            for (int i = 0; i < extrasArray.length(); i++) {
                JSONObject extras = extrasArray.getJSONObject(i);
                if (extras.getString("field_type_name").equals("btn_name")) {
                    saveCertificationButton.setText(extras.getString("value"));
                } else if (extras.getString("field_type_name").equals("section_name")) {
                    title = extras.getString("value") + ":";
                }

            }
            JSONArray certificationsArray = dataObject.getJSONArray("certification");
            //  JSONArray educationArray = dataObject.getJSONArray("education");
            for (int i = 0; i < certificationsArray.length(); i++) {
                JSONArray dataArray = certificationsArray.getJSONArray(i);
                Nokri_CertificationModel model = new Nokri_CertificationModel();
                for (int j = 0; j < dataArray.length(); j++) {

                    JSONObject data = dataArray.getJSONObject(j);

                    if (data.getString("field_type_name").equals("certification_name")) {
                        model.setTitle(data.getString("value"));
                        name = data.getString("key");

                    } else if (data.getString("field_type_name").equals("certification_start")) {
                        model.setStartData(data.getString("value"));
                        start = data.getString("key");

                    } else if (data.getString("field_type_name").equals("certification_end")) {
                        model.setEndDate(data.getString("value"));
                        end = data.getString("key");
                    } else if (data.getString("field_type_name").equals("certification_duration")) {
                        model.setDuration(data.getString("value"));
                        duration = data.getString("key");

                    } else if (data.getString("field_type_name").equals("certification_institute")) {
                        model.setInstitute(data.getString("value"));
                        institute = data.getString("key");

                    } else if (data.getString("field_type_name").equals("certification_desc")) {
                        model.setDescription(data.getString("value"));
                        desc = data.getString("key");

                    }


                    if (j + 1 == dataArray.length())
                        certificationList.add(model);
                }
            }

            dialogManager.hideAlertDialog();
            nokri_setDynamicForms();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }

    }

    private void nokri_setDynamicForms() {

        for (int i = 0; i < certificationList.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.fragment_nokri_cerfifications_details_holder, container, false);

            container.addView(view);
            saveCertificationButton.setVisibility(View.VISIBLE);
            nokri_initialize(view, certificationList.get(i));
        }
        nokri_resetTags(container);

    }

    private void nokri_iniflateDynamicViews() {
        View view = getLayoutInflater().inflate(R.layout.fragment_nokri_cerfifications_details_holder, container, false);
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
        postCertificationList = new ArrayList<>();
        ArrayList<Boolean> fieldsList = new ArrayList<>();
        ArrayList<Boolean> editorList = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            boolean isRichEditorEmpty;
            boolean areAllFieldsFilled;
            View view = container.getChildAt(i);
            EditText certificationTitleEditText = view.findViewById(R.id.edittxt_certification_title);
            EditText durationEditText = view.findViewById(R.id.edittxt_duraion);
            EditText startDateEditText = view.findViewById(R.id.edittxt_start_date);
            EditText endDateEditText = view.findViewById(R.id.edittxt_end_date);
            EditText instuteEditText = view.findViewById(R.id.edittxt_institute);

            RichEditor descriptonEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);

            View line = view.findViewById(R.id.line);
            Nokri_Utils.checkEditTextForError(certificationTitleEditText);
            Nokri_Utils.checkEditTextForError(durationEditText);
            Nokri_Utils.checkEditTextForError(startDateEditText);
            Nokri_Utils.checkEditTextForError(endDateEditText);
            Nokri_Utils.checkEditTextForError(instuteEditText);

            Nokri_CertificationModel model = new Nokri_CertificationModel();

            String certificationTitle = certificationTitleEditText.getText().toString();
            String duration = durationEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String endData = endDateEditText.getText().toString();
            String institute = instuteEditText.getText().toString();


            String description = descriptonEditText.getHtml();

            if (description != null && !description.isEmpty()) {
                isRichEditorEmpty = false;
                line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                if (!certificationTitle.isEmpty() && !duration.isEmpty() && !startDate.isEmpty() && !endData.isEmpty() && !institute.isEmpty() && !description.isEmpty()) {
                    model.setTitle(certificationTitle);
                    model.setDuration(duration);
                    model.setStartData(startDate);
                    model.setEndDate(endData);
                    model.setInstitute(institute);

                    model.setDescription(description);
                    postCertificationList.add(model);
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
            nokri_postCertification();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_certifications_details, container, false);
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
            if (certificationList.size() == 1 && counter == 0) {
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
