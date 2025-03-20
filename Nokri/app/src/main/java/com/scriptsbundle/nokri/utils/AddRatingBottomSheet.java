package com.scriptsbundle.nokri.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRatingBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView ratingALabel, ratingBLabel, ratingCLabel;
    SimpleRatingBar ratingBarA, ratingBarB, ratingBarC;
    EditText title, description;
    TextView submit, reviewLabel;
    JSONObject jsonObject;
    float defaultRating = 3;
    float ratingA = 3;
    float ratingB = 3;
    float ratingC = 3;
    String candId;

    AddRatingBottomSheet object = this;
    boolean calledFromEmployer;

    Nokri_DialogManager manager = new Nokri_DialogManager();

    public AddRatingBottomSheet(JSONObject jsonObject, boolean calledFromEmployer) {
        this.jsonObject = jsonObject;
        this.calledFromEmployer = calledFromEmployer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_rating,
                container, false);

        ratingALabel = v.findViewById(R.id.rating_a_label);
        ratingBLabel = v.findViewById(R.id.rating_b_label);
        ratingCLabel = v.findViewById(R.id.rating_c_label);
        ratingBarA = v.findViewById(R.id.rating_a);
        ratingBarB = v.findViewById(R.id.rating_b);
        ratingBarC = v.findViewById(R.id.rating_c);
        ratingBarA.setRating(defaultRating);
        ratingBarB.setRating(defaultRating);
        ratingBarC.setRating(defaultRating);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        submit = v.findViewById(R.id.submit);
        reviewLabel = v.findViewById(R.id.reviewLabel);

        Nokri_Utils.setRoundButtonColor(getActivity(), submit);


        try {

            JSONObject extra = jsonObject.getJSONObject("extra");
            candId = extra.getString("cand_id");
            ratingALabel.setText(extra.getString("first_rating"));
            ratingBLabel.setText(extra.getString("second_rating"));
            ratingCLabel.setText(extra.getString("third_rating"));
            title.setHint(extra.getString("title_review"));
            description.setHint(extra.getString("your_review"));
            submit.setText(extra.getString("add_review"));
            reviewLabel.setText(extra.getString("add_review"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == submit.getId()) {
            try {
                if (validate()) {
                    postRating();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validate() throws JSONException {
        if (title.getText().toString().equals("")) {
            title.setError(jsonObject.getJSONObject("extra").getString("enter_title"));
            title.requestFocus();
            return false;
        }
        if (title.getText().toString().equals("")) {
            title.setError(jsonObject.getJSONObject("extra").getString("enter_message"));
            title.requestFocus();
            return false;
        }
        return true;
    }

    public void postRating() {
        ratingA = ratingBarA.getRating();
        ratingB = ratingBarB.getRating();
        ratingC = ratingBarC.getRating();
        String titleStr = title.getText().toString();
        String descriptionStr = title.getText().toString();

        JsonObject params = new JsonObject();
        params.addProperty("review_title", titleStr);
        params.addProperty("review_message", descriptionStr);
        params.addProperty("rating_service", ratingA);
        params.addProperty("rating_process", ratingB);
        params.addProperty("rating_selection", ratingC);
        if (calledFromEmployer)
            params.addProperty("emp_id", candId);
        else
            params.addProperty("cand_id", candId);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> call;

        manager.showAlertDialog(getActivity());
        if (calledFromEmployer) {

            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                call = restService.postEmployerRating(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                call = restService.postEmployerRating(params, Nokri_RequestHeaderManager.addHeaders());
            }

        } else {
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                call = restService.postRating(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                call = restService.postRating(params, Nokri_RequestHeaderManager.addHeaders());
            }

        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                manager.hideAlertDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                manager.hideAlertDialog();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
