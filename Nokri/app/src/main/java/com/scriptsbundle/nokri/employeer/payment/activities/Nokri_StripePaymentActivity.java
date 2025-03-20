package com.scriptsbundle.nokri.employeer.payment.activities;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;


import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_StripePaymentActivity extends AppCompatActivity {



    EditText cardNumber, cvc;
    Spinner monthSpinner, yearSpinner;
    Button chkout;
    String cvcNo, cardNo;
    int month, year;
    TextView textViewCardNo, textViewExpTit, textViewMonth, textViewYear, textViewCVC;

    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;

    private String PUBLISHABLE_KEY;  //pk_live_tkSrJzIUzdR9sDx7rLINyGHI //pk_test_07HcOQstgKo91LWCA2rd1a13
    private String id = "";
     String packageType;
    private Nokri_DialogManager dialogManager;
    boolean calledFromCandidate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
     Bundle bundle =   getIntent().getExtras();

     if(bundle!=null){
         id = bundle.getString("id");
         packageType = bundle.getString("packageType");
         calledFromCandidate = getIntent().getBooleanExtra("calledFromCandidate",false);

     }


        this.cardNumber = (EditText) findViewById(R.id.editText9);
        this.cvc = (EditText) findViewById(R.id.cvc);
        this.monthSpinner = (Spinner) findViewById(R.id.spinner);
        this.yearSpinner = (Spinner) findViewById(R.id.spinner2);
        this.chkout = (Button) findViewById(R.id.button4);
        Nokri_Utils.setRoundButtonColor(this,this.chkout);
        this.textViewCardNo = findViewById(R.id.textView23);
        this.textViewCVC =  findViewById(R.id.textView24);
        this.textViewExpTit =  findViewById(R.id.textView20);
        this.textViewMonth =  findViewById(R.id.textView21);
        this.textViewYear = findViewById(R.id.textView22);


        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cvcNo = cvc.getText().toString();
                cardNo = cardNumber.getText().toString();
                month = getInteger(monthSpinner);
                year = getInteger(yearSpinner);

                Card card = new Card(cardNo, month, year, cvcNo);
                boolean validation = card.validateCard();
                if (validation) {

                    try {
                        dialogManager = new Nokri_DialogManager();
                        dialogManager.showAlertDialog(Nokri_StripePaymentActivity.this);

                        Stripe stripe = new Stripe(Nokri_StripePaymentActivity.this, Nokri_Config.STRIPE_KEY);
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        Log.e("token success", token.toString());
                                        Log.e("token success", token.getId());
                                        adforest_Checkout(id, token);
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this, error.getLocalizedMessage());
                                        Log.e("token fail", error.getLocalizedMessage());
                                        Nokri_ToastManager.showLongToast(Nokri_StripePaymentActivity.this, error.getLocalizedMessage());

                                        dialogManager.hideAfterDelay();
                                    }
                                }
                        );
                    }
                    catch (IllegalArgumentException ex){
                        Nokri_ToastManager.showLongToast(Nokri_StripePaymentActivity.this,ex.getMessage());
                        dialogManager.hideAlertDialog();
                    }
                } else if (!card.validateNumber()) {
                    Nokri_Utils.checkEditTextForError(cardNumber);
                    Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this,"Card No Not Valid");
                    dialogManager.hideAlertDialog();

                } else if (!card.validateExpiryDate()) {
                    Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this,"Card Expiry Not Valid");
                    dialogManager.hideAlertDialog();


                } else if (!card.validateCVC()) {
                    Nokri_Utils.checkEditTextForError(cvc);
                    Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this,"Card CVC Not Valid");
                    dialogManager.hideAlertDialog();

                } else {
                    Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this,"Card Not Valid");
                    dialogManager.hideAlertDialog();


                }
            }
        });

        // get view from server
        adforest_getViews();

    }





    //calling APi for geting views from server
    private void adforest_getViews() {



        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);

            Log.d("info packageId",id);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(Nokri_StripePaymentActivity.this), Nokri_SharedPrefManager.getPassword(Nokri_StripePaymentActivity.this),Nokri_StripePaymentActivity.this);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(Nokri_StripePaymentActivity.this)) {
            myCall = restService.getStripe(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getStripe( Nokri_RequestHeaderManager.addHeaders());
        }
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Stripe Responce", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {

                                Log.d("Info Stripe Data", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectThis = response.getJSONObject("data").getJSONObject("form");


                                stringCardError = response.getJSONObject("data").getJSONObject("error").getString("card_number");
                                stringExpiryError = response.getJSONObject("data").getJSONObject("error").getString("expiration_date");
                                stringCVCError = response.getJSONObject("data").getJSONObject("error").getString("invalid_cvc");
                                stringInvalidCard = response.getJSONObject("data").getJSONObject("error").getString("card_details");

                                cardNumber.setHint(jsonObjectThis.getString("card_input_text"));
                                cvc.setHint(jsonObjectThis.getString("cvc_input_text"));
                                chkout.setText(jsonObjectThis.getString("btn_text"));

                                textViewCardNo.setText(jsonObjectThis.getString("card_input_text"));
                                textViewExpTit.setText(jsonObjectThis.getString("select_title"));
                                textViewMonth.setText(jsonObjectThis.getString("select_month"));
                                textViewYear.setText(jsonObjectThis.getString("select_year"));
                                textViewCVC.setText(jsonObjectThis.getString("cvc_input_text"));

                                JSONArray jsonArray = jsonObjectThis.getJSONArray("select_option_year");

                                ArrayList<String> arrayList = new ArrayList<>();
                                //Iterate the jsonArray and print the info of JSONObjects
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    arrayList.add(jsonArray.getString(i));
                                }
                                Nokri_SpinnerAdapter adapter = new Nokri_SpinnerAdapter(Nokri_StripePaymentActivity.this,R.layout.spinner_item_popup,arrayList);

                                Nokri_SpinnerAdapter adapter2 = new Nokri_SpinnerAdapter(Nokri_StripePaymentActivity.this, R.layout.spinner_item_popup, Arrays.asList(getResources().getStringArray(R.array.month_array)) );
                                yearSpinner.setAdapter(adapter);
                                monthSpinner.setAdapter(adapter2);

                            } else {
                                Nokri_ToastManager.showShortToast(Nokri_StripePaymentActivity.this,response.get("message").toString());
                            }
                        }
                        dialogManager.hideAlertDialog();

                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialogManager.hideAlertDialog();
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

    }

    private void adforest_Checkout(String id, Token token) {


            JsonObject params = new JsonObject();
            params.addProperty("package_id", id);
            params.addProperty("source_token", token.getId());
            params.addProperty("payment_from",packageType);
            Log.d("info Send Checkout", params.toString());

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(Nokri_StripePaymentActivity.this), Nokri_SharedPrefManager.getPassword(Nokri_StripePaymentActivity.this),Nokri_StripePaymentActivity.this);

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(this)) {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(this)) {
                myCall = restService.postStripe(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postStripe(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Checkout Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                           // settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                            Intent intent = new Intent(Nokri_StripePaymentActivity.this, Nokri_ThankYouActivity.class);
                           startActivity(intent);
                           finish();
                            dialogManager.hideAlertDialog();
                        }else {dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(Nokri_StripePaymentActivity.this,responseObj.message());}
                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        //Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        dialogManager.hideAlertDialog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        //Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        dialogManager.hideAlertDialog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                        dialogManager.hideAlertDialog();
                    } else {
                        dialogManager.hideAlertDialog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });

    }

    private Integer getInteger(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }




    @Override
    public void onResume() {
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        super.onResume();
    }


}

