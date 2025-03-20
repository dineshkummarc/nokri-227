package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire.QuestionnaireModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_ReceivedResumeAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_ResumeReceivedModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.download.manager.service.Nokri_DownloadService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_ResumeReceivedFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private RecyclerView recyclerView;
    private Nokri_ReceivedResumeAdapter adapter;
    private List<Nokri_ResumeReceivedModel> modelList;

    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    public static String ID;
    private ImageButton searchImageButton;
    private EditText searchEditText;
    private Spinner spinner;
    private ArrayList<String> spinnerNmaes;
    private ArrayList<String> spinnerIds;
    private int counter;
    private RichEditor aboutEditText;
    private ImageView boldImageView, italicImageView, underlineImageView, numberBulletsImageView, listBulletsImageView;
    private int popupCounter = 0;
    private String candiateStatus;
    private DownloadResult serviceResult;
    private Snackbar snackbar;
    private Nokri_DialogManager dialogManager;

    private ProgressBar progressBar;
    private int nextPage = 1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    private boolean isCallFromFilters = false;
    private boolean isCallFromSearch = false;
    private String spinnerId;
    String jobId;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public class DownloadResult extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */

        public DownloadResult(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch (resultCode) {
                case 1000:
                    if (resultData.getBoolean("state"))
                        setSnackBar(getContext(), getView().findViewById(R.id.container), "Download Complete", resultData.getString("path"));
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }

    private void setSnackBar(final Context cotext, View coordinatorLayout, String snackTitle, final String path) {
        snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Open", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  cotext.startActivity(new Intent(Environment.DIRECTORY_DOWNLOADS));
                snackbar.dismiss();
                Intent install = new Intent(Intent.ACTION_VIEW);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Uri apkURI = FileProvider.getUriForFile(
                            getContext(),
                            getContext().getApplicationContext()
                                    .getPackageName() + ".provider", new File(path));
                    install.setDataAndType(apkURI, Nokri_Utils.getMimeType(path));
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    install.setDataAndType(Uri.fromFile(new File(path)),
                            Nokri_Utils.getMimeType(path));
                }


                try {
                    cotext.startActivity(install);
                } catch (ActivityNotFoundException e) {
                    Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.APP_NOT_FOUNT);
                }

            }
        });


        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);


    }

    public Nokri_ResumeReceivedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_resume_received, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nextPage = 1;
        counter = 0;
        isCallFromFilters = false;
        isCallFromSearch = false;
        hasNextPage = true;

        nokri_getReceivedResumes(true);

    }

    private void nokri_initialize() {

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        recyclerView = getView().findViewById(R.id.recyclerview);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        //  setupAdapter();
        spinner = getView().findViewById(R.id.spinner);
        searchImageButton = getView().findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        searchEditText = getView().findViewById(R.id.edittxt_search);

        serviceResult = new DownloadResult(new Handler(Looper.getMainLooper()));


        modelList = new ArrayList<>();
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(), loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);

        loadMoreButton.setOnClickListener(this);
        searchImageButton.setOnClickListener(this);

        searchEditText.setOnFocusChangeListener(this);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner parentSpinner = (Spinner) adapterView;

                if (parentSpinner.getId() == spinner.getId()) {

                    if (counter != 0) {
                        nextPage = 1;
                        nokri_filterReceivedResumes(spinnerIds.get(i), true);
                    }
                    counter++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupAdapter(String source) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_ReceivedResumeAdapter(modelList, getContext(), new Nokri_ReceivedResumeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_ResumeReceivedModel item) {

            }

            @Override
            public void onTakeActionClick(Nokri_ResumeReceivedModel model) {
                nokri_getTemplatesPopup(model);

            }

            @Override
            public void onDownloadClick(Nokri_ResumeReceivedModel model) {
                Intent intent = new Intent(getActivity(), Nokri_DownloadService.class);

                intent.putExtra("url", model.getDownlaodUrl());
                intent.putExtra("filename", model.getFileName());
                intent.putExtra("result", serviceResult);
                getActivity().startService(intent);
            }


            @Override
            public void menuItemSelected(Nokri_ResumeReceivedModel model, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_take_action:
                        nokri_getTemplatesPopup(model);
                        break;
                    case R.id.menu_download:
                        if (model.isJobLinkedin()) {
                            Nokri_Utils.opeInBrowser(getContext(), model.getDownlaodUrl());
                        } else {
                            Intent intent = new Intent(getActivity(), Nokri_DownloadService.class);

                            intent.putExtra("url", model.getDownlaodUrl());
                            intent.putExtra("filename", model.getFileName());
                            intent.putExtra("result", serviceResult);
                            getActivity().startService(intent);
                        }

                        break;
                    case R.id.menu_view_profile:

                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment publicProfileFragment = new NokriPublicProfileFragment();
                        NokriPublicProfileFragment.USER_ID = model.getId();

                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), publicProfileFragment).addToBackStack(null).commit();
                        break;
                    case R.id.menu_view_cover_letter:

                        nokri_showCoverLetterDialog(model);
                        break;
                    case R.id.menu_delete_profile:
                        deleteReceivedResume(jobId,model.getId());
                        break;
                    default:
                        break;

                }
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d("isssueeseasdf", modelList.toString() + "\n" + source);

    }

    public void deleteReceivedResume(String jobId, String candId) {
        JsonObject params = new JsonObject();
        isCallFromFilters = false;
        isCallFromSearch = false;
        params.addProperty("job_id", jobId);
        params.addProperty("cand_id", candId);

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext()))
            myCall = restService.deleteCandidateResume(params, Nokri_RequestHeaderManager.addSocialHeaders());
        else
            myCall = restService.deleteCandidateResume(params, Nokri_RequestHeaderManager.addHeaders());


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        for (int i = 0;i<modelList.size();i++){
                            if (modelList.get(i).getId().equals(candId)){
                                modelList.remove(i);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();

                        dialogManager.hideAlertDialog();
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (searchEditText != null)
            searchEditText.setText("");
    }

    private void nokri_getReceivedResumes(final boolean showAlert) {
        JsonObject params = new JsonObject();
        isCallFromFilters = false;
        isCallFromSearch = false;
        params.addProperty("page_number", nextPage);
        params.addProperty("job_id", ID);
        if (showAlert) {

            dialogManager = new Nokri_DialogManager();

            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
            modelList = new ArrayList<>();
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getReceivedResumes(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getReceivedResumes(params, Nokri_RequestHeaderManager.addHeaders());
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
                        spinnerNmaes = new ArrayList<>();
                        spinnerIds = new ArrayList<>();

                        emptyTextView.setText("");


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");




                        hasNextPage = pagination.getBoolean("has_next_page");
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONObject data = response.getJSONObject("data");
                        Log.d("isssueeseasdf", "data from get resume received " + data.toString());
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        JSONArray filterArray = data.getJSONArray("cand_filter");


                        for (int i = 0; i < filterArray.length(); i++) {
                            JSONObject valueObject = filterArray.getJSONObject(i);
                            spinnerNmaes.add(valueObject.getString("key"));
                            spinnerIds.add(valueObject.getString("value"));
                        }
                        if (spinner.getAdapter() == null || spinner.getAdapter().isEmpty())
                            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, spinnerNmaes));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {

                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            setupAdapter("empty array");

                            return;
                        } else {
                            messageContainer.setVisibility(View.GONE);

                        }
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_ResumeReceivedModel model = new Nokri_ResumeReceivedModel();
                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);
                                if (object.getString("field_type_name").equals("cand_id"))
                                    model.setId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setImageUrl(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name")) {
                                    model.setName(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("cand_stat"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dwnlod")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(false);

                                } else if (object.getString("field_type_name").equals("cand_linked")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(true);

                                } else if (object.getString("field_type_name").equals("cand_adress"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_date"))
                                    model.setDate(object.getString("value"));
                                else if (object.getString("field_type_name").equals("resume_name"))
                                    model.setFileName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_action"))
                                    model.setActionButtonText(object.getString("key"));
                                else if (object.getString("field_type_name").equals("cand_cover")) {
                                    model.setCoverLetterAvailable(object.getBoolean("is_required"));
                                    if (object.getBoolean("is_required")) {
                                        model.setCoverLetterTitle(object.getString("key"));
                                        model.setCoverLetterText(object.getString("value"));
                                    }
                                } else if (object.getString("field_type_name").equals("cand_question")) {
                                    ArrayList<QuestionnaireModel> questions = new ArrayList<>();
                                    JSONArray questionsArray = object.getJSONArray("value");
                                    for (int k = 0; k < questionsArray.length(); k++) {
                                        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
                                        questionnaireModel.question = questionsArray.getJSONObject(k).getString("quest");
                                        questionnaireModel.answer = questionsArray.getJSONObject(k).getString("answer");
                                        questions.add(questionnaireModel);
                                    }
                                    model.setQuestions(questions);
                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);


                            }
                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter("after data population");
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (showAlert)
                            dialogManager.hideAlertDialog();
                        if (companiesArray.length()!=0)
                            jobId = response.getString("job_id");
                        else
                            Nokri_ToastManager.showShortToast(getActivity(),response.getString("message"));
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                            e.printStackTrace();
                        }
                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
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


    private void nokri_filterEmailTemplate(String id, final ExtendedEditText subjectEditText, final RichEditor templateEditText) {
        JsonObject params = new JsonObject();
        params.addProperty("temp_val", id);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.filterEmailTemplates(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.filterEmailTemplates(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        modelList = new ArrayList<>();

                        emptyTextView.setText("");


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONArray dataArray = response.getJSONArray("data");

                        JSONObject subjectObject = dataArray.getJSONObject(0);
                        JSONObject templateObject = dataArray.getJSONObject(1);
                        JSONObject candidateStatusObject = dataArray.getJSONObject(2);
                        subjectEditText.setText(subjectObject.getString("value"));
                        templateEditText.setHtml(String.valueOf(Html.fromHtml(templateObject.getString("value"))));
                        candiateStatus = candidateStatusObject.getString("value");
                        Log.v("myRespone", dataArray.toString());
                        dialogManager.hideAfterDelay();
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

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.img_btn_search:
                String name = searchEditText.getText().toString();
                if (name != null && !name.isEmpty()) {
                    nextPage = 1;
                    nokri_getResumeFromName(name, true);

                } else
                    Nokri_ToastManager.showLongToast(getContext(), "Name is required");
                break;

            case R.id.img_bold:
                aboutEditText.setBold();
                break;
            case R.id.img_italic:
                aboutEditText.setItalic();
                break;
            case R.id.img_underline:
                aboutEditText.setUnderline();
                break;
            case R.id.img_num_bullets:
                aboutEditText.setNumbers();
                break;
            case R.id.img_list_bullets:
                aboutEditText.setBullets();
                break;
            case R.id.btn_load_more:

                loadMoreButton.setVisibility(View.GONE);
                if (!isCallFromFilters && !isCallFromSearch) {
                    if (hasNextPage) {
                        nokri_getReceivedResumes(false);
                    }
                } else if (isCallFromFilters && !isCallFromSearch) {
                    if (hasNextPage) {
                        nokri_filterReceivedResumes(spinnerIds.get(spinner.getSelectedItemPosition()), false);
                    }
                } else if (isCallFromSearch) {
                    if (hasNextPage) {
                        nokri_getResumeFromName(searchEditText.getText().toString(), false);
                    }
                }
                break;
            default:
                break;
        }

    }


    private void nokri_filterReceivedResumes(String id, final boolean showAlert) {
        isCallFromFilters = true;
        isCallFromSearch = false;
        JsonObject params = new JsonObject();
        params.addProperty("c_status", id);
        params.addProperty("job_id", ID);
        params.addProperty("page_number", nextPage);


        if (showAlert) {

            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.filterReceivedResume(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.filterReceivedResume(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        if (showAlert)
                            modelList = new ArrayList<>();


                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONObject data = response.getJSONObject("data");
                        Log.d("isssueeseasdf", "data from filter " + data.toString());
//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            setupAdapter("Empty Array Filter Received Resume");
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_ResumeReceivedModel model = new Nokri_ResumeReceivedModel();
                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);
                                if (object.getString("field_type_name").equals("cand_id"))
                                    model.setId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setImageUrl(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name")) {
                                    model.setName(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("cand_dwnlod")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(false);

                                } else if (object.getString("field_type_name").equals("cand_linked")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(true);

                                } else if (object.getString("field_type_name").equals("cand_stat"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dwnlod"))
                                    model.setDownlaodUrl(object.getString("value"));

                                else if (object.getString("field_type_name").equals("cand_adress"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_date"))
                                    model.setDate(object.getString("value"));
                                else if (object.getString("field_type_name").equals("resume_name"))
                                    model.setFileName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_action"))
                                    model.setActionButtonText(object.getString("key"));
                                else if (object.getString("field_type_name").equals("cand_cover")) {
                                    model.setCoverLetterAvailable(object.getBoolean("is_required"));
                                    if (object.getBoolean("is_required")) {
                                        model.setCoverLetterTitle(object.getString("key"));
                                        model.setCoverLetterText(object.getString("value"));
                                    }
                                } else if (object.getString("field_type_name").equals("cand_question")) {
                                    ArrayList<QuestionnaireModel> questions = new ArrayList<>();
                                    JSONArray questionsArray = object.getJSONArray("value");
                                    for (int k = 0; k < questionsArray.length(); k++) {
                                        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
                                        questionnaireModel.question = questionsArray.getJSONObject(k).getString("quest");
                                        questionnaireModel.answer = questionsArray.getJSONObject(k).getString("answer");
                                        questions.add(questionnaireModel);
                                    }
                                    model.setQuestions(questions);
                                }

                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        //   Log.d("Pointz",modelList.toString());

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (showAlert)
                            dialogManager.hideAlertDialog();
                        setupAdapter("After looping throght filter");


                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                if (showAlert)
                    dialogManager.hideAfterDelay();
            }
        });

    }


    @Override
    public void onFocusChange(View view, boolean selected) {
        if (selected)
            searchEditText.setHintTextColor(getActivity().getResources().getColor(R.color.app_blue));
        else
            searchEditText.setHintTextColor(getActivity().getResources().getColor(R.color.grey));
    }

    private void nokri_getResumeFromName(String name, final boolean showAlert) {
        isCallFromFilters = false;
        isCallFromSearch = true;
        JsonObject params = new JsonObject();

        params.addProperty("job_id", ID);
        params.addProperty("c_name", name);
        params.addProperty("page_number", nextPage);
        if (showAlert) {

            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
            modelList = new ArrayList<>();
            nextPage = 1;
        }

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.filterResumeWithName(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.filterResumeWithName(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {


                        emptyTextView.setText("");


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");
                        if (!showAlert)
                            nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONObject data = response.getJSONObject("data");
/*

                        JSONObject jobFilterObject = data.getJSONObject("job_filter");
                        JSONArray valueArray = jobFilterObject.getJSONArray("value");

                        for (int i = 0;i<valueArray.length();i++){
                            JSONObject valueObject =  valueArray.getJSONObject(i);
                            spinnerNmaes.add(valueObject.getString("value"));
                            spinnerIds.add(valueObject.getString("key"));
                        }
                        if(spinner.getAdapter() == null)
                            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,spinnerNmaes));
*/


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            setupAdapter("empty array get resume for name");
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_ResumeReceivedModel model = new Nokri_ResumeReceivedModel();
                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);
                                if (object.getString("field_type_name").equals("cand_id"))
                                    model.setId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setImageUrl(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name")) {
                                    model.setName(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("cand_stat"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dwnlod"))
                                    model.setDownlaodUrl(object.getString("value"));

                                else if (object.getString("field_type_name").equals("cand_dwnlod")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(false);

                                } else if (object.getString("field_type_name").equals("cand_linked")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(true);

                                } else if (object.getString("field_type_name").equals("cand_adress"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_date"))
                                    model.setDate(object.getString("value"));
                                else if (object.getString("field_type_name").equals("resume_name"))
                                    model.setFileName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_action"))
                                    model.setActionButtonText(object.getString("key"));
                                else if (object.getString("field_type_name").equals("cand_cover")) {
                                    model.setCoverLetterAvailable(object.getBoolean("is_required"));
                                    if (object.getBoolean("is_required")) {
                                        model.setCoverLetterTitle(object.getString("key"));
                                        model.setCoverLetterText(object.getString("value"));
                                    }
                                } else if (object.getString("field_type_name").equals("cand_question")) {
                                    ArrayList<QuestionnaireModel> questions = new ArrayList<>();
                                    JSONArray questionsArray = object.getJSONArray("value");
                                    for (int k = 0; k < questionsArray.length(); k++) {
                                        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
                                        questionnaireModel.question = questionsArray.getJSONObject(k).getString("quest");
                                        questionnaireModel.answer = questionsArray.getJSONObject(k).getString("answer");
                                        questions.add(questionnaireModel);
                                    }
                                    model.setQuestions(questions);
                                }


                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        //   Log.d("Pointz",modelList.toString());
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (showAlert)
                            dialogManager.hideAlertDialog();
                        setupAdapter("get resume for name loop end");


                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                if (showAlert)
                    dialogManager.hideAfterDelay();
            }
        });

    }


    private void nokri_getTemplatesPopup(final Nokri_ResumeReceivedModel model) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getTemplatesPopup(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getTemplatesPopup(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        ArrayList<String> keysList = new ArrayList<>();
                        ArrayList<String> valuesList = new ArrayList<>();

                        ArrayList<String> statusKeyList = new ArrayList<>();
                        ArrayList<String> statusValueList = new ArrayList<>();


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        JSONArray filterArray = data.getJSONArray("email_data");
                        JSONArray statusArray = data.getJSONArray("status_data");


                        for (int i = 0; i < filterArray.length(); i++) {
                            JSONObject valueObject = filterArray.getJSONObject(i);
                            keysList.add(valueObject.getString("key"));
                            valuesList.add(valueObject.getString("value"));
                        }
                        for (int i = 0; i < statusArray.length(); i++) {

                            JSONObject valueObject = statusArray.getJSONObject(i);
                            statusKeyList.add(valueObject.getString("key"));
                            statusValueList.add(valueObject.getString("value"));

                        }


                        JSONArray extrasArray = data.getJSONArray("extra");
                        String headingText = null, yesOption = null, noOption = null;
                        String templateLabel = null, subjectLabel = null, bodyLabel = null;
                        String buttonText = null;
                        String statusLabel = null;
                        for (int i = 0; i < extrasArray.length(); i++) {
                            JSONObject extraObject = extrasArray.getJSONObject(i);
                            if (extraObject.getString("field_type_name").equals("email_send")) {
                                headingText = extraObject.getString("key");

                            } else if (extraObject.getString("field_type_name").equals("email_yes")) {
                                yesOption = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("email_no")) {
                                noOption = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("email_temp")) {
                                templateLabel = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("email_sub")) {
                                subjectLabel = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("email_body")) {
                                bodyLabel = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("email_btn")) {
                                buttonText = extraObject.getString("key");
                            } else if (extraObject.getString("field_type_name").equals("select_status")) {
                                statusLabel = extraObject.getString("key");
                            }


                        }
                        nokri_showDialog(model, headingText, yesOption, noOption, templateLabel, subjectLabel, bodyLabel, buttonText, valuesList, keysList, statusValueList, statusKeyList, statusLabel);
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter("empty array get template popup");
                        dialogManager.hideAfterDelay();
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


    private void nokri_showCoverLetterDialog(Nokri_ResumeReceivedModel model) {
        Nokri_FontManager fontManager = new Nokri_FontManager();

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cover_letter_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.heading).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        TextView titleTextView = dialog.findViewById(R.id.txt_title);
        TextView coverLetterTextView = dialog.findViewById(R.id.txt_cover);
        LinearLayout questionsLayout = dialog.findViewById(R.id.question_layout);
        if (model.getQuestions() != null) {
            if (model.getQuestions().size() != 0) {
                questionsLayout.removeAllViews();
                for (int i = 0; i < model.getQuestions().size(); i++) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.questions_display_layout, null);
                    TextView questionText = view.findViewById(R.id.questionText);
                    TextView answerText = view.findViewById(R.id.answerText);
                    TextView questionLabel = view.findViewById(R.id.questionLabel);
                    TextView answerLabel = view.findViewById(R.id.answerLabel);
                    questionText.setText(model.getQuestions().get(i).question);
                    answerText.setText(model.getQuestions().get(i).answer);
                    questionsLayout.addView(view);
                }
            }
        }


        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView, getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(coverLetterTextView, getContext().getAssets());

        titleTextView.setText(model.getCoverLetterTitle());
        coverLetterTextView.setText(model.getCoverLetterText());
        ImageView imageClose = dialog.findViewById(R.id.img_close);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void nokri_showDialog(final Nokri_ResumeReceivedModel model, String headingText, String yesOption, String noOption, String templateLabel, String subjectLabel, String bodyLabel, String buttonText, ArrayList<String> names, final ArrayList<String> ids, final ArrayList<String> statusValueList, final ArrayList<String> statusKeyList, String statusLabel) {
        Nokri_FontManager fontManager = new Nokri_FontManager();

        final Dialog dialog = new Dialog(getContext());
        final ToggleSwitch toggleSwitch;

        final ArrayList<String> toggleItems = new ArrayList<>();
        toggleItems.add(noOption);
        toggleItems.add(yesOption);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.resume_action_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        //Initialozations
        final Spinner actionSpinner = dialog.findViewById(R.id.spinner_action);
        final Spinner statusSpinner = dialog.findViewById(R.id.spinner_status);
        final TextFieldBoxes textFieldBoxes = dialog.findViewById(R.id.text_field_boxes);
        final RelativeLayout container = dialog.findViewById(R.id.container1);
        dialog.findViewById(R.id.heading).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        aboutEditText = dialog.findViewById(R.id.edittxt_about);
        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.rich_text_size));


        boldImageView = dialog.findViewById(R.id.img_bold);
        italicImageView = dialog.findViewById(R.id.img_italic);
        underlineImageView = dialog.findViewById(R.id.img_underline);
        numberBulletsImageView = dialog.findViewById(R.id.img_num_bullets);
        listBulletsImageView = dialog.findViewById(R.id.img_list_bullets);
        dialog.findViewById(R.id.toogle_switch);


        Button sendButton = dialog.findViewById(R.id.btn_send);
        TextView headingTextView = dialog.findViewById(R.id.txt_heading);
        TextView titleTextView = dialog.findViewById(R.id.txt_title);
        final ExtendedEditText emailEditText = dialog.findViewById(R.id.edittxt_email);
        final TextView actionTextView = dialog.findViewById(R.id.txt_action);
        final TextView emailTextView = dialog.findViewById(R.id.txt_email);
        final TextView bodyTextView = dialog.findViewById(R.id.txt_body);
        final TextView statusTextView = dialog.findViewById(R.id.txt_status);
        toggleSwitch = dialog.findViewById(R.id.toogle_switch);
        sendButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        toggleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));

        final LinearLayout textFormattingRow = dialog.findViewById(R.id.text_formatting_row);

        boldImageView.setOnClickListener(this);
        italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);


        toggleSwitch.setLabels(toggleItems);
        sendButton.setText(buttonText);
        headingTextView.setText(headingText);
        actionTextView.setText(templateLabel);
        emailTextView.setText(subjectLabel);
        emailEditText.setHint(subjectLabel);
        titleTextView.setText(model.getName());
        bodyTextView.setText(bodyLabel);
        statusTextView.setText(statusLabel);

// Initializations End


        final int togglePosition = toggleSwitch.getCheckedTogglePosition();

        if (togglePosition == 0) {

            textFieldBoxes.setVisibility(View.GONE);
            emailEditText.setVisibility(View.GONE);
            actionSpinner.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            bodyTextView.setVisibility(View.GONE);
            aboutEditText.setVisibility(View.GONE);
            textFormattingRow.setVisibility(View.GONE);
            actionTextView.setVisibility(View.GONE);
            dialog.findViewById(R.id.line).setVisibility(View.GONE);

            statusTextView.setVisibility(View.VISIBLE);
            statusSpinner.setVisibility(View.VISIBLE);
        } else {
            textFieldBoxes.setVisibility(View.VISIBLE);
            emailEditText.setVisibility(View.VISIBLE);
            actionSpinner.setVisibility(View.VISIBLE);
            emailTextView.setVisibility(View.VISIBLE);
            bodyTextView.setVisibility(View.VISIBLE);
            aboutEditText.setVisibility(View.VISIBLE);
            textFormattingRow.setVisibility(View.VISIBLE);
            actionTextView.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.line).setVisibility(View.VISIBLE);

            statusTextView.setVisibility(View.GONE);
            statusSpinner.setVisibility(View.GONE);

        }

        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {

                if (position == 0) {

                    textFieldBoxes.setVisibility(View.GONE);
                    emailEditText.setVisibility(View.GONE);
                    actionSpinner.setVisibility(View.GONE);
                    emailTextView.setVisibility(View.GONE);
                    bodyTextView.setVisibility(View.GONE);
                    aboutEditText.setVisibility(View.GONE);
                    textFormattingRow.setVisibility(View.GONE);
                    actionTextView.setVisibility(View.GONE);
                    dialog.findViewById(R.id.line).setVisibility(View.GONE);
                    statusTextView.setVisibility(View.VISIBLE);
                    statusSpinner.setVisibility(View.VISIBLE);
                } else {
                    textFieldBoxes.setVisibility(View.VISIBLE);
                    emailEditText.setVisibility(View.VISIBLE);
                    actionSpinner.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.VISIBLE);
                    bodyTextView.setVisibility(View.VISIBLE);
                    aboutEditText.setVisibility(View.VISIBLE);
                    textFormattingRow.setVisibility(View.VISIBLE);
                    actionTextView.setVisibility(View.VISIBLE);
                    dialog.findViewById(R.id.line).setVisibility(View.VISIBLE);
                    statusTextView.setVisibility(View.GONE);
                    statusSpinner.setVisibility(View.GONE);

                }
            }
        });

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        params.width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() - 100;
        container.setLayoutParams(params);
        textFieldBoxes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    textFieldBoxes.setPrimaryColor(getResources().getColor(R.color.app_blue));
                else {
                    textFieldBoxes.setPrimaryColor(getResources().getColor(R.color.grey));
                }
            }
        });


        statusSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, statusValueList));

        actionSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, names));

        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterView;

                if (spinner.getId() == actionSpinner.getId()) {

                    nokri_filterEmailTemplate(ids.get(i), emailEditText, aboutEditText);


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = toggleSwitch.getCheckedTogglePosition();
                boolean isEmailSend;
                if (position == 0)
                    isEmailSend = false;
                else
                    isEmailSend = true;

                if (isEmailSend) {

                    if (!emailEditText.getText().toString().isEmpty() && emailEditText != null && aboutEditText.getHtml() != null && !aboutEditText.getHtml().isEmpty()) {
                        nokri_postSendEmail(model.getId(), candiateStatus, isEmailSend, emailEditText.getText().toString(), aboutEditText.getHtml());
                    } else {
                        Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    }
                } else if (!isEmailSend) {

                    nokri_postSendEmail(model.getId(), statusKeyList.get(statusSpinner.getSelectedItemPosition()), isEmailSend, emailEditText.getText().toString(), aboutEditText.getHtml());


                }
                Log.d("tagzzz", ID + " " + model.getId() + " " + candiateStatus + " " + isEmailSend + " " + emailEditText.getText().toString() + " " + aboutEditText.getHtml());
            }
        });

        fontManager.nokri_setOpenSenseFontButton(sendButton, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(headingTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(actionTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(statusTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(emailTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getActivity().getAssets());
        ImageView imageClose = dialog.findViewById(R.id.img_close);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void nokri_postSendEmail(String candidateId, String candiateStatus, boolean isEmailSend, String emailSubject, String emailBody) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("candidate_id", candidateId);
        jsonObject.addProperty("job_id", ID);
        jsonObject.addProperty("cand_status", candiateStatus);
        jsonObject.addProperty("is_send_mail", isEmailSend);
        if (isEmailSend) {
            jsonObject.addProperty("email_sub", emailSubject);
            jsonObject.addProperty("email_body", emailBody);
        }
        params.add(jsonObject);
        Log.v("responsezzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postSendEail(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postSendEail(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        //   Call<ResponseBody> myCall = restService.postCandidateEducation(params, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("responsezzzzz", response.toString());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                            if (counter == 1) {
                                nextPage = 1;
                                nokri_getReceivedResumes(true);

                            } else {
                                nextPage = 1;
                                nokri_filterReceivedResumes(spinnerIds.get(spinner.getSelectedItemPosition()), true);
                            }
                        } else {
                            dialogManager.showCustom(responseObject.message());
                            Log.v("responsezzzzz", responseObject.message());
                            dialogManager.hideAfterDelay();

                        }

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Log.v("responsezzzzz", e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        Log.v("responsezzzzz", e.getMessage());
                        e.printStackTrace();

                    }
                } else {
                    dialogManager.showCustom(responseObject.code() + "");
                    dialogManager.hideAfterDelay();
                    Log.v("responsezzzzz", responseObject.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }


}

