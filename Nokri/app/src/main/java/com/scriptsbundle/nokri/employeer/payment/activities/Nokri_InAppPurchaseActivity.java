package com.scriptsbundle.nokri.employeer.payment.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

public class Nokri_InAppPurchaseActivity extends AppCompatActivity {
    private static String LICENSE_KEY = "";
    String productId, packageId, packageType, activityName, billing_error, no_market = "", one_time = "";
    RestService restService;
    Nokri_SharedPrefManager settingsMain;
    // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators

    BillingClient billingClient;
    private BillingProcessor bp;
    boolean calledFromCandidate;
    private Nokri_DialogManager dialogManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_in_app_purchase);
        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            productId = getIntent().getStringExtra("porductId");
            activityName = getIntent().getStringExtra("activityName");
            billing_error = getIntent().getStringExtra("billing_error");
            no_market = getIntent().getStringExtra("no_market");
            one_time = getIntent().getStringExtra("one_time");
            LICENSE_KEY = getIntent().getStringExtra("LICENSE_KEY");

        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    List<String> skuList = new ArrayList<>();
                    skuList.add(productId);
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    int responseCode = billingClient.launchBillingFlow(Nokri_InAppPurchaseActivity.this, billingFlowParams).getResponseCode();
                                }
                            });
                    // The BillingClient is ready. You can query purchases here.
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });




        restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this),this);

        setTitle(activityName);

    }
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    ConsumeParams consumeParams =
                            ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.consumeAsync(consumeParams,listener);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                showToast(billing_error);
                finish();
            } else {
                showToast(billing_error);
                finish();
            }

        }
    };
    ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            JsonObject params = new JsonObject();
            params.addProperty("package_id", packageId);
            params.addProperty("payment_from", packageType);
            nokri_checkout(params);
        }
    };


    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
    private void nokri_checkout(JsonObject params) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(Nokri_InAppPurchaseActivity.this), Nokri_SharedPrefManager.getPassword(Nokri_InAppPurchaseActivity.this), Nokri_InAppPurchaseActivity.this);

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(Nokri_InAppPurchaseActivity.this)) {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(this)) {
                myCall = restService.postPackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postPackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showShortToast(Nokri_InAppPurchaseActivity.this, response.getString("message"));
                        dialogManager.hideAfterDelay();
                        Intent intent = new Intent(Nokri_InAppPurchaseActivity.this, Nokri_ThankYouActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(Nokri_InAppPurchaseActivity.this, "exception in jhsib");
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(Nokri_InAppPurchaseActivity.this, "exception in IO");
                        e.printStackTrace();
                    }

                } else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_InAppPurchaseActivity.this, t.getMessage());
            }
        });
    }

//    @Override
//    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//
//        Log.d("info purchase", "onProductPurchased: " + productId + details.toString());
//        bp.consumePurchase(porductId);
//        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(this);
//        adforest_Checkout();
//    }

//    @Override
//    public void onPurchaseHistoryRestored() {
//
//    }

//    @Override
//    public void onBillingError(int errorCode, @Nullable Throwable error) {
//        showToast(billing_error);
//        finish();
//    }
//
//    @Override
//    public void onBillingInitialized() {
//        Log.d("info purchase", "onBillingInitialized");
//
//    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("info string ", resultCode + "" + requestCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}

