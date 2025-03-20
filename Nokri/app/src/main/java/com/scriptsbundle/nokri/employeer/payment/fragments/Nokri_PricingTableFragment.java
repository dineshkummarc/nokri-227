package com.scriptsbundle.nokri.employeer.payment.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.sasidhar.smaps.payumoney.MakePaymentActivity;
import com.sasidhar.smaps.payumoney.PayUMoney_Constants;
import com.sasidhar.smaps.payumoney.Utils;
import com.scriptsbundle.nokri.employeer.payment.activities.Nokri_InAppPurchaseActivity;
import com.scriptsbundle.nokri.employeer.payment.activities.Nokri_StripePaymentActivity;
import com.scriptsbundle.nokri.employeer.payment.activities.Nokri_ThankYouActivity;
import com.scriptsbundle.nokri.employeer.payment.models.Nokri_InAppPurchaseModel;
import com.scriptsbundle.nokri.employeer.payment.models.Nokri_PricingModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.custom.CustomViewPager;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_ViewPagerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_PricingTableFragment extends Fragment implements TabLayout.OnTabSelectedListener, Nokri_PackagesFragment.OnSelectPlanClickListener, PaytmPaymentTransactionCallback {
    private CustomViewPager viewPager;
    private TextView toolbarTitleTextView;
    private TabLayout tabLayout;

    private Nokri_FontManager fontManager;
    private ArrayList<Nokri_PricingModel> modelList;
    private ArrayList<Nokri_SpinnerModel> listOfPaymentTypes;
    private JSONObject paypalJson;
    private String selectedItemName;
    private String selecdProductId;
    private Nokri_DialogManager dialogManager;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    // These will hold all the payment parameters
//    private PaymentParams mPaymentParams;

    // This sets the configuration
    //  private PayuConfig payuConfig;

    private Spinner environmentSpinner;

    // Used when generating hash from SDK
    // private PayUChecksum checksum;
    private String merchantKey;
    private String userCredentials;
    public static boolean calledFromCandidate;
    //MerchantId 7252328
    //MercantKey op9jzF
    //MerchantSalt FKncAE8q

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }


    public Nokri_PricingTableFragment() {
        // Required empty public constructor

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fontManager = new Nokri_FontManager();

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        //   Payu.setInstance(getContext());
        modelList = new ArrayList<>();
        listOfPaymentTypes = new ArrayList<>();

        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Nokri_Config.APP_COLOR));
        if (Nokri_Globals.IS_RTL_ENABLED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

        }


        nokri_getPackages();
        emptyTextView = getView().findViewById(R.id.txt_empty);
//        emptyTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        //nokri_payu();
        //nokri_paytm("","","paytm","539");
    }

    private void nokri_setupFonts() {

        if (Nokri_Globals.IS_RTL_ENABLED) {


            for (int i = tabLayout.getTabCount() - 1; i >= 0; i--) {
                //noinspection ConstantConditions
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.custom_tab2, tabLayout, false);
                TextView tv = relativeLayout.findViewById(R.id.tab_title);

                tv.setText(modelList.get(i).getProductTitle());
                if (i == 0)
                    tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));


                fontManager.nokri_setOpenSenseFontTextView(tv, getActivity().getAssets());
                tabLayout.getTabAt(i).setCustomView(relativeLayout);


            }


        } else {

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                //noinspection ConstantConditions
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.custom_tab2, tabLayout, false);
                TextView tv = relativeLayout.findViewById(R.id.tab_title);

                tv.setText(modelList.get(i).getProductTitle());
                if (i == 0)
                    tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));


                fontManager.nokri_setOpenSenseFontTextView(tv, getActivity().getAssets());
                tabLayout.getTabAt(i).setCustomView(relativeLayout);


            }
        }
    }

    private void nokri_setupViewPager() {
        Nokri_ViewPagerAdapter pagerAdapter = new Nokri_ViewPagerAdapter(getChildFragmentManager());
   /*     if(Nokri_Globals.IS_RTL_ENABLED){
            for(int i=modelList.size()-1;i>=0;i--){
                Nokri_PackagesFragment packagesFragment = new Nokri_PackagesFragment();
                packagesFragment.nokri_setPricingModel(modelList.get(i));

                packagesFragment.nokri_setOnSetPlanClickListener(this);
                packagesFragment.nokri_setSpinnerModel(listOfPaymentTypes);
                pagerAdapter.addFragment(packagesFragment,modelList.get(i).getProductTitle());

            }
        }
        else{*/
        for (int i = 0; i < modelList.size(); i++) {
            Nokri_PackagesFragment packagesFragment = new Nokri_PackagesFragment();
            packagesFragment.nokri_setPricingModel(modelList.get(i));
            packagesFragment.nokri_setOnSetPlanClickListener(this);
            packagesFragment.nokri_setSpinnerModel(listOfPaymentTypes);
            pagerAdapter.addFragment(packagesFragment, modelList.get(i).getProductTitle());

        }//}
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOffscreenPageLimit(modelList.size());

        dialogManager.hideAlertDialog();

        //        viewPager.setOffscreenPageLimit(3);
    }

    private void nokri_getPackages() {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.getCandidatePackages(Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.getCandidatePackages(Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.getPackages(Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.getPackages(Nokri_RequestHeaderManager.addHeaders());
            }
        }
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
                        JSONObject data = response.getJSONObject("data");

//                         if(data.getBoolean("is_paypal_key"))
//                         {
//                             paypalJson = data.getJSONObject("paypal");
//                         }
//
//
//
//                         JSONArray paymentTypes = data.getJSONArray("payment_types");
//
//                         for(int i=0;i<paymentTypes.length();i++)
//                         {  JSONObject paymentType = paymentTypes.getJSONObject(i);
//
//                             Nokri_SpinnerModel model = new Nokri_SpinnerModel();
//                             model.setId(paymentType.getString("key"));
//                             model.setName(paymentType.getString("value"));
//                            listOfPaymentTypes.add(model);
//                         }


                        toolbarTitleTextView.setText(response.getJSONObject("extra").getString("page_title"));
//                         boolean isInAppOn = response.getJSONObject("extra").getJSONObject("android").getBoolean("in_app_on");
//                         String secretCode = response.getJSONObject("extra").getJSONObject("android").getString("secret_code");
//                         String billingError = response.getJSONObject("extra").getString("billing_error");
//                         String noMarketMessage = response.getJSONObject("extra").getJSONObject("android").getJSONObject("message").getString("no_market");
//                         String titleText =    response.getJSONObject("extra").getJSONObject("android").getString("title_text");
//                         String oneTimeMessage = response.getJSONObject("extra").getJSONObject("android").getJSONObject("message").getString("one_time");

                        JSONArray produnctsArray = data.getJSONArray("products");
                        if (produnctsArray.length() != 0) {

//
                            if (data.getBoolean("is_paypal_key")) {
                                paypalJson = data.getJSONObject("paypal");
                                Log.d("paypaljson", paypalJson.toString());

                            }
                            JSONArray paymentTypes = data.getJSONArray("payment_types");

                            for (int i = 0; i < paymentTypes.length(); i++) {
                                JSONObject paymentType = paymentTypes.getJSONObject(i);

                                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                                model.setId(paymentType.getString("key"));
                                model.setName(paymentType.getString("value"));
                                listOfPaymentTypes.add(model);
                            }
//                            toolbarTitleTextView.setTitle(response.getJSONObject("extra").getString("page_title"));
                            //  toolbarTitleTextView.setText(response.getJSONObject("extra").getString("page_title"));
                            boolean isInAppOn = response.getJSONObject("extra").getJSONObject("android").getBoolean("in_app_on");
                            String secretCode = response.getJSONObject("extra").getJSONObject("android").getString("secret_code");
                            String billingError = response.getJSONObject("extra").getString("billing_error");
                            String noMarketMessage = response.getJSONObject("extra").getJSONObject("android").getJSONObject("message").getString("no_market");
                            String titleText = response.getJSONObject("extra").getJSONObject("android").getString("title_text");
                            String oneTimeMessage = response.getJSONObject("extra").getJSONObject("android").getJSONObject("message").getString("one_time");

                            for (int i = 0; i < produnctsArray.length(); i++) {
                                ArrayList<Nokri_PricingModel.Nokri_PremiumJobs> premiumJobsList = new ArrayList<>();
                                Nokri_PricingModel model = new Nokri_PricingModel();
                                JSONObject object = produnctsArray.getJSONObject(i);


                                Nokri_PricingModel.Nokri_PremiumJobs preniumJobsModel = model.new Nokri_PremiumJobs();
                                preniumJobsModel.setName(object.getString("days_text"));
                                preniumJobsModel.setNumberOfJobs(object.getString("days_value"));
                                premiumJobsList.add(preniumJobsModel);

                                model.setAdsText(object.getString("free_ads_text"));
                                model.setAdsValue(object.getInt("free_ads_value"));
                                model.setProducId(object.getString("product_id"));
                                model.setProductTitle(object.getString("product_title"));
                                model.setCurrency(object.getString("product_price_sign"));
                                if (object.getString("is_free").equals("0")) {
                                    model.setButtonVisible(false);
                                } else if (object.getString("is_free").equals("1")) {
                                    model.setButtonVisible(true);
                                }

                                model.setButtonText(object.getString("product_btn"));
                                model.setProductPrice(object.getString("product_price_only"));
                                model.setProductLink(object.getString("product_link"));
                                model.setProductQuantity(object.getInt("product_qty"));
                                model.setProductButtonText(object.getString("product_btn"));

                                Nokri_InAppPurchaseModel inAppPurchaseModel = new Nokri_InAppPurchaseModel();
                                inAppPurchaseModel.setProductId(object.getString("product_id"));
                                inAppPurchaseModel.setProductTitle(object.getString("product_title"));
                                inAppPurchaseModel.setProductAndroidInApp(object.getString("product_android_inapp"));
                                inAppPurchaseModel.setProductPrice(object.getString("product_price"));
                                inAppPurchaseModel.setProductPricsOnly(object.getString("product_price_only"));
                                inAppPurchaseModel.setProductPriceSign(object.getString("product_price_sign"));
                                inAppPurchaseModel.setProductLink(object.getString("product_link"));
                                inAppPurchaseModel.setProductQuantity(object.getInt("product_qty"));
                                inAppPurchaseModel.setAndroidPurchaseCode(object.getJSONObject("product_appCode").getString("android"));
                                inAppPurchaseModel.setMessage(object.getJSONObject("product_appCode").getString("message"));
                                inAppPurchaseModel.setInAppOn(isInAppOn);
                                inAppPurchaseModel.setNoMarketMessage(noMarketMessage);
                                inAppPurchaseModel.setOneTimeMessage(oneTimeMessage);
                                inAppPurchaseModel.setTitleText(titleText);
                                inAppPurchaseModel.setSecretCode(secretCode);
                                inAppPurchaseModel.setBillingError(billingError);
                                model.setInAppPurchaseModel(inAppPurchaseModel);

                                if (calledFromCandidate) {

                                    Nokri_PricingModel.Nokri_PremiumJobs jobsLeftModel = model.new Nokri_PremiumJobs();
                                    jobsLeftModel.setNumberOfJobs(object.getString("jobs_left_text"));
                                    jobsLeftModel.setName(object.getString("jobs_left_value"));
                                    premiumJobsList.add(jobsLeftModel);
                                    Nokri_PricingModel.Nokri_PremiumJobs featuredProfileModel = model.new Nokri_PremiumJobs();
                                    featuredProfileModel.setNumberOfJobs(object.getString("feature_days_text"));
                                    featuredProfileModel.setName(object.getString("feature_days_value"));
                                    premiumJobsList.add(featuredProfileModel);
                                    model.setPremiumJobs(premiumJobsList);
                                    modelList.add(model);
                                } else {

                                    if (object.getBoolean("is_cand_search")) {
                                        String candidateSearch = object.getString("cand_search");
                                        String candidateSearchValue = object.getString("cand_search_value");
                                        Nokri_PricingModel.Nokri_PremiumJobs preniumJobs = model.new Nokri_PremiumJobs();
                                        preniumJobs.setName(candidateSearch);
                                        preniumJobs.setNumberOfJobs(candidateSearchValue);
                                        premiumJobsList.add(preniumJobs);
                                    }
                                    JSONArray preniumJobsArray = object.getJSONArray("premium_jobs");

                                    for (int j = 0; j < preniumJobsArray.length(); j++) {

                                        JSONObject preniumJobObject = preniumJobsArray.getJSONObject(j);
                                        Nokri_PricingModel.Nokri_PremiumJobs preniumJobs = model.new Nokri_PremiumJobs();
                                        preniumJobs.setName(preniumJobObject.getString("name"));
                                        preniumJobs.setNumberOfJobs(preniumJobObject.getString("no_of_jobs"));

                                        premiumJobsList.add(preniumJobs);

                                        if (j + 1 == preniumJobsArray.length()) {
                                            model.setPremiumJobs(premiumJobsList);
                                            modelList.add(model);

                                        }
                                    }
                                }

                            }
                        } else {
                            if (produnctsArray.length() == 0) {
                                messageContainer.setVisibility(View.VISIBLE);
                                emptyTextView.setText(response.getJSONObject("extra").getString("no_packge"));
//                            Toast.makeText(getApplicationContext(), "No products were added", Toast.LENGTH_SHORT).show();
                            }
                        }

                        nokri_setupViewPager();
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setOnTabSelectedListener(Nokri_PricingTableFragment.this);
                        nokri_setupFonts();


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_pricing_table, container, false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onButtonClick(Nokri_PricingModel model) {
        nokri_checkout("free", model.getProducId());
    }

    @Override
    public void onItemClick(String id, String name, int position, Nokri_PricingModel model) {
        selectedItemName = name;
        selecdProductId = model.getProducId();

        switch (id) {
            case "stripe":
                Intent intent = new Intent(getActivity(), Nokri_StripePaymentActivity.class);
                intent.putExtra("packageType", id);
                intent.putExtra("calledFromCandidate",calledFromCandidate);
                intent.putExtra("id", model.getProducId());
                getActivity().startActivity(intent);
                break;
            case "bank_transfer":
                nokri_checkout(id, model.getProducId());
                break;
            case "cash_on_delivery":
                nokri_checkout(id, model.getProducId());
                break;
            case "cheque":
                nokri_checkout(id, model.getProducId());
                break;
            case "paypal":
                if (paypalJson != null)
                    nokri_PayPal(paypalJson, model);
                else
                    Nokri_ToastManager.showLongToast(getContext(), "PayPal parameters are not properly set on server");
                break;

            case "paytm":
                //nokri_paytm(id,model.getProductPrice(),"paytm",model.getProducId());
                break;

            case "app_inapp":
                Nokri_InAppPurchaseModel inAppPurchaseModel = model.getInAppPurchaseModel();
                if (!inAppPurchaseModel.getAndroidPurchaseCode().trim().isEmpty()) {
                    if (inAppPurchaseModel.isInAppOn()) {
                        Intent inApp = new Intent(getActivity(), Nokri_InAppPurchaseActivity.class);
                        inApp.putExtra("id", model.getProducId());
                        inApp.putExtra("packageType", "app_inapp");
                        inApp.putExtra("calledFromCandidate",calledFromCandidate);
                        inApp.putExtra("porductId", inAppPurchaseModel.getProductAndroidInApp());
                        inApp.putExtra("activityName", inAppPurchaseModel.getTitleText());
                        inApp.putExtra("billing_error", inAppPurchaseModel.getBillingError());
                        inApp.putExtra("no_market", inAppPurchaseModel.getNoMarketMessage());
                        inApp.putExtra("one_time", inAppPurchaseModel.getOneTimeMessage());
                        inApp.putExtra("LICENSE_KEY", inAppPurchaseModel.getSecretCode());
                        getActivity().startActivity(inApp);
                    } else {
                        Nokri_ToastManager.showLongToast(getContext(), inAppPurchaseModel.getMessage());
                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), inAppPurchaseModel.getMessage());
                }

                break;
            default:

                break;

        }
    }


    private void nokri_PayPal(JSONObject jsonObject, Nokri_PricingModel model) {
        PayPalConfiguration
                config = null;
        String currency = null;
        try {
            config = new PayPalConfiguration();
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            //      .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                /*    .clientId("AWi4uRx8t958K8ZjdZa9qU--YfHtzJJVR3Pm6u6wxi9hQFxyw-bFyJk0MgauUh9bQXzSAUonQE8JAswI")
                    .merchantName("Johnny sins")
                    .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                    .merchantUserAgreementUri(Uri.parse("https://www.example.com/privacy"));*/

            try {
                if (jsonObject.getString("mode").equals("sandbox")) {
                    config.environment(PayPalConfiguration.ENVIRONMENT_SANDBOX);
                } else
                    config.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION);
                config.clientId(jsonObject.getString("api_key")).
                        merchantName(jsonObject.getString("merchant_name")).
                        merchantPrivacyPolicyUri(Uri.parse(jsonObject.getString("privecy_url")))
                        .merchantUserAgreementUri(Uri.parse(jsonObject.getString("agreement_url")));
                currency = jsonObject.getString("currency");
            } catch (JSONException e) {
                Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                e.printStackTrace();
            }
            Intent intent = new Intent(getActivity(), PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            getActivity().startService(intent);

            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(model.getProductPrice()), currency, model.getProductTitle(),
                    PayPalPayment.PAYMENT_INTENT_SALE);

            //Creating Paypal Payment activity intent
            Intent intent1 = new Intent(getActivity(), PaymentActivity.class);

            //putting the paypal configuration to the intent
            intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            //Puting paypal payment to the intent
            intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult
            startActivityForResult(intent1, Nokri_Config.PAYPAL_REQUEST_CODE);
        } /*catch (JSONException e) {
            e.printStackTrace();
        } */ catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == PayUMoney_Constants.PAYMENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    Nokri_ToastManager.showLongToast(getContext(), "Successful");
                    break;
                case RESULT_CANCELED:
                    Nokri_ToastManager.showLongToast(getContext(), "Payment Cancelled | Failed.");
                    break;
            }

        }


        if (requestCode == PayUMoney_Constants.PAYMENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(getActivity(), "Payment Success.", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getActivity(), "Payment Cancelled | Failed.", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        if (requestCode == Nokri_Config.PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e("info ", "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        JsonObject params = new JsonObject();
                        params.addProperty("package_id", selecdProductId);
                        params.addProperty("source_token", paymentId);
                        params.addProperty("payment_from", selectedItemName);
                        params.addProperty("payment_client", payment_client);

                        nokri_checkout(params);

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {

                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Nokri_ToastManager.showLongToast(getContext(), "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");

            }
        }

    }

    private void nokri_checkout(final String packgeType, String productId) {
        JsonObject params = new JsonObject();
        params.addProperty("package_id", productId);

        params.addProperty("payment_from", packgeType);


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
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
                        Nokri_ToastManager.showShortToast(getContext(), response.getString("message"));
                        dialogManager.hideAfterDelay();
                        if (!packgeType.equals("free")) {
                            Intent intent = new Intent(getActivity(), Nokri_ThankYouActivity.class);
                            getActivity().startActivity(intent);
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
                    dialogManager.showCustom(responseObject.message());
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


    private void nokri_checkout(JsonObject params) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
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
                        Nokri_ToastManager.showShortToast(getContext(), response.getString("message"));
                        dialogManager.hideAfterDelay();
                        Intent intent = new Intent(getActivity(), Nokri_ThankYouActivity.class);
                        getActivity().startActivity(intent);

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(getContext(), "exception in jhsib");
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(getContext(), "exception in IO");
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });
    }


    private void nokri_paytm(String orderId, String price, String orderBy, String productId) {


        JsonObject params = new JsonObject();

        params.addProperty("orderid", "CUST001");
        params.addProperty("email", "username@emailprovider.com");
        params.addProperty("edtmobile", "7777777777");
        params.addProperty("edtamount", "100.12");
        params.addProperty("payment_from", orderBy);
        params.addProperty("package_id", productId);


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (calledFromCandidate){
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.postCandidatePackages(params, Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
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


                        JSONObject responseObj = new JSONObject(responseObject.body().string());
                        JSONObject response = responseObj.getJSONObject("data");


                        HashMap<String, String> paramMap = new HashMap<String, String>();


                        paramMap.put("MID", response.getString("MID"));
                        paramMap.put("ORDER_ID", response.getString("ORDER_ID"));
                        paramMap.put("CUST_ID", response.getString("CUST_ID"));
                        paramMap.put("INDUSTRY_TYPE_ID", response.getString("INDUSTRY_TYPE_ID"));
                        paramMap.put("CHANNEL_ID", response.getString("CHANNEL_ID"));
                        paramMap.put("TXN_AMOUNT", response.getString("TXN_AMOUNT"));
                        paramMap.put("WEBSITE", response.getString("WEBSITE"));
                        paramMap.put("EMAIL", response.getString("EMAIL"));
                        paramMap.put("MOBILE_NO", response.getString("MOBILE_NO"));
                        paramMap.put("CALLBACK_URL", response.getString("CALLBACK_URL"));
                        paramMap.put("CHECKSUMHASH", response.getString("CHECKSUMHASH"));
                        Log.d("data------->", response.getString("CALLBACK_URL"));


                        PaytmPGService Service = PaytmPGService.getStagingService();// for sating enviornment

                        PaytmOrder Order = new PaytmOrder(paramMap);
                        Service.initialize(Order, null);


                        Service.startPaymentTransaction(getContext(), true, true, Nokri_PricingTableFragment.this);
                        dialogManager.hideAlertDialog();


                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(getContext(), "exception in jhsib");
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(getContext(), "exception in IO");
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });



















            /*     params.addProperty("orderid",orderId);
                params.addProperty("email",Nokri_SharedPrefManager.getEmail(getContext()));
                params.addProperty("mobile",Nokri_SharedPrefManager.getPhone(getContext()));
                params.addProperty("amount",price);*/
        // if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
        //   ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);


//                PaytmPGService Service = PaytmPGService.getStagingService();// for sating enviornment
        //PaytmPGService Service = PaytmPGService.getProductionService();//For Production enviornment
/*
            HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put( "MID" , "rxazcv89315285244163");
                // Key in your staging and production MID available in your dashboard
                paramMap.put( "ORDER_ID" , "order1");
                paramMap.put( "CUST_ID" , "cust123");
                paramMap.put( "MOBILE_NO" , "7777777777");
                paramMap.put( "EMAIL" , "username@emailprovider.com");
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , "100.12");
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                // This is the staging value. Production value is available in your dashboard
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                // This is the staging value. Production value is available in your dashboard
                paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
                paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");
                PaytmOrder Order = new PaytmOrder(paramMap);
                Service.initialize(Order, null);


                Service.startPaymentTransaction(getContext(),true,true,this);*/


        //}
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Nokri_ToastManager.showLongToast(getContext(), "Payment Transaction response " + bundle.toString());
    }

    @Override
    public void networkNotAvailable() {
        Nokri_ToastManager.showLongToast(getContext(), "Network connection error: Check your internet connectivity");
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Nokri_ToastManager.showLongToast(getContext(), "Authentication failed: Server error" + s);
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Nokri_ToastManager.showLongToast(getContext(), "UI Error " + s);

    }

    @Override
    public void onErrorLoadingWebPage(int i, String inErrorMessage, String inFailingUrl) {
        Nokri_ToastManager.showLongToast(getContext(), "Unable to load webpage " + inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {

        Nokri_ToastManager.showLongToast(getContext(), "Transaction cancelled");
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {


        Nokri_ToastManager.showLongToast(getContext(), "Transaction cancelled " + bundle.toString());
    }

    private void nokri_payu() {


        String salt = "qauKbEAJ";
        HashMap params = new HashMap<>();
        params.put(PayUMoney_Constants.KEY, "LLKwG0"); // Get merchant key from PayU Money Account
        params.put(PayUMoney_Constants.TXN_ID, "393463");
        params.put(PayUMoney_Constants.AMOUNT, "100");
        params.put(PayUMoney_Constants.PRODUCT_INFO, "Product");
        params.put(PayUMoney_Constants.FIRST_NAME, "First name");
        params.put(PayUMoney_Constants.EMAIL, "approve@test.com");
        params.put(PayUMoney_Constants.PHONE, "27827777777");
        params.put(PayUMoney_Constants.SURL, "https://www.payumoney.com/mobileapp/payumoney/success.php");
        params.put(PayUMoney_Constants.FURL, "https://www.payumoney.com/mobileapp/payumoney/failure.php");


// User defined fields are optional (pass empty string)
//        params.put(PayUMoney_Constants.UDF1, "");
//        params.put(PayUMoney_Constants.UDF2, "");
//        params.put(PayUMoney_Constants.UDF3, "");
//        params.put(PayUMoney_Constants.UDF4, "");
//        params.put(PayUMoney_Constants.UDF5, "");


// generate hash by passing params and salt
        String hash = Utils.generateHash(params, salt); // Get Salt from PayU Money Account
        params.put(PayUMoney_Constants.HASH, hash);


// SERVICE PROVIDER VALUE IS ALWAYS "payu_paisa".
        params.put(PayUMoney_Constants.SERVICE_PROVIDER, "payu_paisa");

        Intent intent = new Intent(getActivity(), MakePaymentActivity.class);
        intent.putExtra(PayUMoney_Constants.ENVIRONMENT, PayUMoney_Constants.ENV_DEV);
        intent.putExtra(PayUMoney_Constants.PARAMS, params);
        startActivityForResult(intent, PayUMoney_Constants.PAYMENT_REQUEST);

    }

}

