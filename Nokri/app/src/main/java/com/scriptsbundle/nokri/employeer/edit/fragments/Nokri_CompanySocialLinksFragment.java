package com.scriptsbundle.nokri.employeer.edit.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CompanySocialLinksFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {
    private TextView facebookTextView, twitterTextView, linkedinTextView, googlePlusTextView;
    private EditText facebookEditText, twitterEditText, linkedinEditText, googlePlusEditText;
    private Button saveProfileButton;
    private Nokri_FontManager fontManager;
    private Nokri_DialogManager dialogManager;

    public Nokri_CompanySocialLinksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();
        try {
            nokri_getEmployeerSocialLinks();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void nokri_postSocialLinks() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("emp_fb", facebookEditText.getText().toString());
        jsonObject.addProperty("emp_twiter", twitterEditText.getText().toString());
        jsonObject.addProperty("emp_linked", linkedinEditText.getText().toString());
        jsonObject.addProperty("emp_google", googlePlusEditText.getText().toString());


        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmployeerSocialLinks(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postEmployeerSocialLinks(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateSocialLinks(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", response.toString());
                        if (response.getBoolean("success")) {

                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                            nokri_getEmployeerSocialLinks();
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }

    private void nokri_setFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(facebookTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(twitterTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(linkedinTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(googlePlusTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(facebookEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(twitterEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(linkedinEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(googlePlusEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontButton(saveProfileButton, getActivity().getAssets());
    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        facebookTextView = getView().findViewById(R.id.txt_facebook);
        twitterTextView = getView().findViewById(R.id.txt_twitter);
        linkedinTextView = getView().findViewById(R.id.txt_linkedin);
        googlePlusTextView = getView().findViewById(R.id.txt_google_plus);

        facebookEditText = getView().findViewById(R.id.edittxt_facebook);
        twitterEditText = getView().findViewById(R.id.edittxt_twitter);
        linkedinEditText = getView().findViewById(R.id.edittxt_linkedin);
        googlePlusEditText = getView().findViewById(R.id.edittxt_google_plus);

        saveProfileButton = getView().findViewById(R.id.btn_saveprofile);

        facebookEditText.setOnFocusChangeListener(this);
        twitterEditText.setOnFocusChangeListener(this);
        linkedinEditText.setOnFocusChangeListener(this);
        googlePlusEditText.setOnFocusChangeListener(this);
        saveProfileButton.setOnClickListener(this);

        Nokri_Utils.setEditBorderButton(getContext(), saveProfileButton);
    }


    private void nokri_getEmployeerSocialLinks() throws JSONException {
        dialogManager = new Nokri_DialogManager();
        JSONObject response = Nokri_CompanyEditProfileFragment.socialLink;
        if (response.getBoolean("success")) {
            JSONArray extrasArray = response.getJSONArray("extras");
            for (int i = 0; i < extrasArray.length(); i++) {
                JSONObject extra = extrasArray.getJSONObject(i);

                if (extra.getString("field_type_name").equals("btn_txt"))
                    saveProfileButton.setText(extra.getString("value"));

                if (extra.getString("field_type_name").equals("fb_txt"))
                    facebookEditText.setHint(extra.getString("value"));
                if (extra.getString("field_type_name").equals("tw_txt"))
                    twitterEditText.setHint(extra.getString("value"));
                if (extra.getString("field_type_name").equals("lk_txt"))
                    linkedinEditText.setHint(extra.getString("value"));
                if (extra.getString("field_type_name").equals("g+_txt"))
                    googlePlusEditText.setHint(extra.getString("value"));


            }

            JSONArray dataJsonArray = response.getJSONArray("data");
            for (int i = 0; i < dataJsonArray.length(); i++) {
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                if (jsonObject.getString("field_type_name").equals("emp_fb")) {
                    facebookTextView.setText(jsonObject.getString("key"));
                    facebookEditText.setText(jsonObject.getString("value"));
                    //facebookEditText.setHint(jsonObject.getString("key"));
                } else if (jsonObject.getString("field_type_name").equals("emp_twiter")) {
                    twitterTextView.setText(jsonObject.getString("key"));
                    twitterEditText.setText(jsonObject.getString("value"));
                    //twitterEditText.setHint(jsonObject.getString("key"));
                } else if (jsonObject.getString("field_type_name").equals("emp_linked")) {
                    linkedinTextView.setText(jsonObject.getString("key"));
                    linkedinEditText.setText(jsonObject.getString("value"));
                    // linkedinEditText.setHint(jsonObject.getString("key"));
                } else if (jsonObject.getString("field_type_name").equals("emp_google")) {
                    googlePlusTextView.setText(jsonObject.getString("key"));
                    googlePlusEditText.setText(jsonObject.getString("value"));
                    //  googlePlusEditText.setHint(jsonObject.getString("key"));
                }

            }
            dialogManager.hideAlertDialog();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_social_links, container, false);
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_facebook:
                if (selected) {
                    facebookEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    twitterEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    linkedinEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    googlePlusEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_twitter:
                if (selected) {
                    facebookEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    twitterEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    linkedinEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    googlePlusEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_linkedin:
                if (selected) {
                    facebookEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    twitterEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    linkedinEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    googlePlusEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_google_plus:
                if (selected) {
                    facebookEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    twitterEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    linkedinEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    googlePlusEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {

        nokri_postSocialLinks();


    }

    private boolean nokri_areAnyFieldsEmpty() {
        Nokri_Utils.checkEditTextForError(facebookEditText);
        Nokri_Utils.checkEditTextForError(twitterEditText);
        Nokri_Utils.checkEditTextForError(linkedinEditText);
        Nokri_Utils.checkEditTextForError(googlePlusEditText);
        if (!facebookEditText.getText().toString().trim().isEmpty() && !twitterEditText.getText().toString().trim().isEmpty() && !linkedinEditText.getText().toString().trim().isEmpty() && !googlePlusEditText.getText().toString().trim().isEmpty())
            return false;
        else
            return true;
    }
}
