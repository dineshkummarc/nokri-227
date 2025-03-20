package com.scriptsbundle.nokri.candidate.edit.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_EducationalDetailModel;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_ProfessionalDetailModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.utils.Nokri_Config;
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
public class Nokri_ProfessionalsDetailsFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener {
    private TextView professionalDetailTextView, projectStartTextView, projectEndTextView, yourRoleTextView, organizationTextView, descriptionTextView;
    private EditText startDateEditText, endDateEditText, yourRoleEditText, organizationEditText;
    //private ImageView boldImageView,italicImageView,underlineImageView,numberBulletsImageView,listBulletsImageView;
    private ImageButton plustImageButton, minusImageButton;
    private Calendar calendar;
    //private RichEditor descriptionEditText;
    private Nokri_FontManager fontManager;
    private List<Nokri_ProfessionalDetailModel> professionList, postProfessionList;
    private Nokri_ProfessionalDetailModel addMoreModel = new Nokri_ProfessionalDetailModel();
    private int counter;
    private int flag;
    private LinearLayout container;
    private boolean areAllFieldsFilled;
    private Button saveProfessionButtin;
    private String name, start, end, organization, role, desc, title;

    private ArrayList<EditText> listofStartDates = new ArrayList<>();
    private ArrayList<EditText> listofEndDates = new ArrayList<>();
    private int currentIndex;
    private boolean isRichEditorEmpty;
    private LinearLayout textarea;
    private String organizationNameHint, projectRoleHint, projectStartHint, projectEndHint, projectDescriptionHint;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_ProfessionalsDetailsFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        container = getView().findViewById(R.id.container);
        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
        dialogManager = new Nokri_DialogManager();

        saveProfessionButtin = getView().findViewById(R.id.btn_saveprofession);
        Nokri_Utils.setEditBorderButton(getContext(), saveProfessionButtin);
        saveProfessionButtin.setOnClickListener(this);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(saveProfessionButtin, getActivity().getAssets());
        try {
            nokri_getAddMore();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint({"RestrictedApi", "NewApi"})
    private void nokri_initialize(final View view, Nokri_ProfessionalDetailModel model) {
        fontManager = new Nokri_FontManager();

        professionalDetailTextView = view.findViewById(R.id.txt_professional_detail);

        organizationTextView = view.findViewById(R.id.txt_organization);
        projectStartTextView = view.findViewById(R.id.txt_project_start);
        projectEndTextView = view.findViewById(R.id.txt_project_end);
        yourRoleTextView = view.findViewById(R.id.txt_your_role);
        descriptionTextView = view.findViewById(R.id.txt_description);
        TextView currentlyWorkingTextView = view.findViewById(R.id.txt_currently_working);


        professionalDetailTextView.setText(title);

        organizationTextView.setText(organization);
        projectStartTextView.setText(start);
        projectEndTextView.setText(end);
        yourRoleTextView.setText(role);
        descriptionTextView.setText(desc);
        currentlyWorkingTextView.setText(name);


//        fontManager.nokri_setMonesrratSemiBioldFont(professionalDetailTextView,getActivity().getAssets());
//
//        fontManager.nokri_setMonesrratSemiBioldFont(organizationTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(projectStartTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(projectEndTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(yourRoleTextView,getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(descriptionTextView,getActivity().getAssets());
//
//       fontManager.nokri_setOpenSenseFontTextView(currentlyWorkingTextView,getActivity().getAssets());

        AppCompatCheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    view.findViewById(R.id.edittxt_end_date).setVisibility(View.GONE);
                    view.findViewById(R.id.txt_project_end).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.edittxt_end_date).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.txt_project_end).setVisibility(View.VISIBLE);
                }
            }
        });

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        Color.RED //disabled
                        , Color.parseColor(Nokri_Config.APP_COLOR) //enabled

                }
        );

        checkBox.setSupportButtonTintList(colorStateList);


        yourRoleEditText = view.findViewById(R.id.edittxt_your_role);
        startDateEditText = view.findViewById(R.id.edittxt_start_date);
        endDateEditText = view.findViewById(R.id.edittxt_end_date);
        organizationEditText = view.findViewById(R.id.edittxt_oganization);

        final RichEditor descriptionEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
        final LinearLayout textArea = (LinearLayout) view.findViewById(R.id.textarea);

        textarea = view.findViewById(R.id.textarea);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 0);
        textArea.setLayoutParams(params);
        descriptionEditText.setPadding(0, 5, 0, 0);

        textArea.setBackground(getActivity().getDrawable(R.drawable.textarea));


//        fontManager.nokri_setOpenSenseFontEditText(yourRoleEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(startDateEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(endDateEditText,getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(organizationEditText,getActivity().getAssets());


        if (model != null) {

            yourRoleEditText.setText(model.getRole());
            startDateEditText.setText(model.getStartDate());
            endDateEditText.setText(model.getEndDate());
            organizationEditText.setText(model.getOrganization());
            descriptionEditText.setHtml(model.getDescription());
            if (model.getTitle().equals("1")) {
                endDateEditText.setVisibility(View.GONE);
                view.findViewById(R.id.edittxt_end_date).setVisibility(View.GONE);
                view.findViewById(R.id.txt_project_end).setVisibility(View.GONE);
                checkBox.setChecked(true);
            }
        }
     /*   else if(model == null){

            yourRoleEditText.setHint(addMoreModel.getRole());
            startDateEditText.setHint(addMoreModel.getStartDate());
            endDateEditText.setHint(addMoreModel.getEndDate());

            organizationEditText.setHint(addMoreModel.getOrganization());
            descriptionEditText.setHtml(addMoreModel.getDescription());

        }*/


        yourRoleEditText.setOnFocusChangeListener(this);
        startDateEditText.setOnFocusChangeListener(this);
        endDateEditText.setOnFocusChangeListener(this);
        organizationEditText.setOnFocusChangeListener(this);
        descriptionEditText.setOnFocusChangeListener(this);


        calendar = Calendar.getInstance();

        descriptionEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));
        descriptionEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        descriptionEditText.setBackgroundColor(getResources().getColor(android.R.color.transparent));


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
        plustImageButton.setOnClickListener(this);
        nokri_setHints(view);
    }

    private void nokri_setHints(View view) {
        EditText yourRoleEditText = view.findViewById(R.id.edittxt_your_role);
        EditText startDateEditText = view.findViewById(R.id.edittxt_start_date);
        EditText endDateEditText = view.findViewById(R.id.edittxt_end_date);
        EditText organizationEditText = view.findViewById(R.id.edittxt_oganization);

        RichEditor descriptionEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
        yourRoleEditText.setHint(projectRoleHint);
        startDateEditText.setHint(projectStartHint);
        endDateEditText.setHint(projectEndHint);
        organizationEditText.setHint(organizationNameHint);
        descriptionEditText.setPlaceholder(projectDescriptionHint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_nokri_professional_details, container, false);
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {

            case R.id.edittxt_your_role:
                if (selected) {

                    yourRoleEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    organizationEditText.setHintTextColor(getResources().getColor(R.color.grey));


                }
                break;
            case R.id.edittxt_start_date:
                if (selected) {

                    yourRoleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    organizationEditText.setHintTextColor(getResources().getColor(R.color.grey));


                   /* new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 0;*/
                }
                break;
            case R.id.edittxt_end_date:
                if (selected) {

                    yourRoleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey_500));
                    organizationEditText.setHintTextColor(getResources().getColor(R.color.grey));


                   /* new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    flag = 1;*/
                }
                break;
            case R.id.edittxt_oganization:
                if (selected) {

                    yourRoleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    startDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    endDateEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    organizationEditText.setHintTextColor(getResources().getColor(R.color.grey_500));

                }
                break;


        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
          /*  case R.id.img_bold:
                descriptionEditText.setBold();
                break;
            case R.id.img_italic:
                descriptionEditText.setItalic();
                break;
            case R.id.img_underline:
                descriptionEditText.setUnderline();
                break;
            case R.id.img_num_bullets:
                descriptionEditText.setNumbers();
                break;
            case R.id.img_list_bullets:
                descriptionEditText.setBullets();
                break;
        */
            case R.id.btn_saveprofession:
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

    private void nokri_postProfession() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();
        for (int i = 0; i < postProfessionList.size(); i++) {


            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("project_start", postProfessionList.get(i).getStartDate());
            if (postProfessionList.get(i).getIsChecked() == 0) {


                jsonObject.addProperty("project_end", postProfessionList.get(i).getEndDate());
            }
            jsonObject.addProperty("project_role", postProfessionList.get(i).getRole());
            jsonObject.addProperty("project_organization", postProfessionList.get(i).getOrganization());
            jsonObject.addProperty("project_desc", postProfessionList.get(i).getDescription());
            jsonObject.addProperty("project_name", postProfessionList.get(i).getIsChecked() + "");
            params.add(jsonObject);
            Log.d("tessst", params.toString());
        }


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidateProfession(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postCandidateProfession(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateProfession(params, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try { //Log.v("response",responseObject.body().string());
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
                        Log.v("response", e.toString());
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
        JSONObject response = Nokri_CandidateEditProfileFragment.professionGetMore;
        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray educationArray = dataObject.getJSONArray("profession");
            for (int i = 0; i < educationArray.length(); i++) {
                JSONArray dataArray = educationArray.getJSONArray(i);
                Nokri_EducationalDetailModel model = new Nokri_EducationalDetailModel();
                for (int j = 0; j < dataArray.length(); j++) {

                    JSONObject data = dataArray.getJSONObject(j);

                    if (data.getString("field_type_name").equals("project_name"))
                        addMoreModel.setTitle(data.getString("value"));
                    else if (data.getString("field_type_name").equals("project_start"))
                        addMoreModel.setStartDate(data.getString("value"));
                    else if (data.getString("field_type_name").equals("project_end"))
                        addMoreModel.setEndDate(data.getString("value"));
                    else if (data.getString("field_type_name").equals("project_organization"))
                        addMoreModel.setOrganization(data.getString("value"));
                    else if (data.getString("field_type_name").equals("project_role"))
                        addMoreModel.setRole(data.getString("value"));
                    else if (data.getString("field_type_name").equals("project_desc"))
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
        professionList = new ArrayList<>();
        JSONObject response = Nokri_CandidateEditProfileFragment.professionGet;

        if (response.getBoolean("success")) {
            JSONObject dataObject = response.getJSONObject("data");
            JSONArray professionArray = dataObject.getJSONArray("profession");
            JSONArray extrasArray = dataObject.getJSONArray("extras");
            for (int i = 0; i < extrasArray.length(); i++) {
                JSONObject extraObject = extrasArray.getJSONObject(i);
                if (extraObject.getString("field_type_name").equals("section_name")) {
                    title = extraObject.getString("value") + ":";
                } else if (extraObject.getString("field_type_name").equals("btn_name")) {
                    saveProfessionButtin.setText(extraObject.getString("value"));
                }
            }


            for (int i = 0; i < professionArray.length(); i++) {
                JSONArray dataArray = professionArray.getJSONArray(i);
                Nokri_ProfessionalDetailModel model = new Nokri_ProfessionalDetailModel();
                for (int j = 0; j < dataArray.length(); j++) {

                    JSONObject data = dataArray.getJSONObject(j);

                    if (data.getString("field_type_name").equals("project_name")) {

                        model.setTitle(data.getString("value"));

                        name = data.getString("key");


                    } else if (data.getString("field_type_name").equals("project_start")) {
                        model.setStartDate(data.getString("value"));
                        start = data.getString("key");
                        projectStartHint = data.getString("fieldname");
                    } else if (data.getString("field_type_name").equals("project_end")) {
                        model.setEndDate(data.getString("value"));
                        end = data.getString("key");
                        projectEndHint = data.getString("fieldname");
                    } else if (data.getString("field_type_name").equals("project_role")) {
                        model.setRole(data.getString("value"));
                        role = data.getString("key");
                        projectRoleHint = data.getString("fieldname");
                    } else if (data.getString("field_type_name").equals("project_organization")) {
                        model.setOrganization(data.getString("value"));
                        organization = data.getString("key");
                        organizationNameHint = data.getString("fieldname");

                    } else if (data.getString("field_type_name").equals("project_desc")) {
                        model.setDescription(data.getString("value"));
                        projectDescriptionHint = data.getString("fieldname");
                        desc = data.getString("key");


                    }


                    if (j + 1 == dataArray.length())
                        professionList.add(model);
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

        for (int i = 0; i < professionList.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.fragment_nokri_details_holder, container, false);

            container.addView(view);
            saveProfessionButtin.setVisibility(View.VISIBLE);
            nokri_initialize(view, professionList.get(i));
        }
        nokri_resetTags(container);

    }

    private void nokri_iniflateDynamicViews() {
        View view = getLayoutInflater().inflate(R.layout.fragment_nokri_details_holder, container, false);
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
        postProfessionList = new ArrayList<>();
        ArrayList<Boolean> fieldsList = new ArrayList<>();
        ArrayList<Boolean> editorList = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            boolean isRichEditorEmpty;
            boolean areAllFieldsFilled;
            View view = container.getChildAt(i);

            EditText organizatonEditText = view.findViewById(R.id.edittxt_oganization);
            EditText startDateEditText = view.findViewById(R.id.edittxt_start_date);
            EditText endDateEditText = view.findViewById(R.id.edittxt_end_date);
            EditText roleEditText = view.findViewById(R.id.edittxt_your_role);

            RichEditor descriptonEditText = (RichEditor) view.findViewById(R.id.edittxt_descripton);
            View line = view.findViewById(R.id.line);
            descriptonEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));

            Nokri_Utils.checkEditTextForError(organizatonEditText);
            Nokri_Utils.checkEditTextForError(startDateEditText);
            Nokri_Utils.checkEditTextForError(endDateEditText);
            Nokri_Utils.checkEditTextForError(roleEditText);

            Nokri_ProfessionalDetailModel model = new Nokri_ProfessionalDetailModel();


            String organization = organizatonEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String endData = endDateEditText.getText().toString();
            String role = roleEditText.getText().toString();

            String description = descriptonEditText.getHtml();

            if (description != null && !description.trim().isEmpty()) {
                isRichEditorEmpty = false;
                line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                if (endDateEditText.getVisibility() != View.VISIBLE)
                    endData = "Some Random Text";
                if (!organization.isEmpty() && !startDate.isEmpty() && !endData.isEmpty() && !role.isEmpty() && !description.isEmpty()) {

                    model.setOrganization(organization);
                    model.setStartDate(startDate);
                    model.setEndDate(endData);
                    model.setRole(role);
                    if (endDateEditText.getVisibility() == View.VISIBLE) {
                        model.setIsChecked(0);
                    } else {

                        model.setIsChecked(1);
                    }
                    model.setDescription(description);
                    postProfessionList.add(model);
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
            nokri_postProfession();

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
            if (professionList.size() == 1 && counter == 0) {
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
