package com.scriptsbundle.nokri.candidate.resume.fragments;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import com.scriptsbundle.nokri.FilePicker.LFilePicker;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.resume.adapter.Nokri_YourResumeAdapter;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;


import com.scriptsbundle.nokri.candidate.resume.model.Nokri_ResumeModel;
import com.scriptsbundle.nokri.custom.ProgressRequestBody;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_UploadProgressDialolque;

import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.RuntimePermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_YourResumeFragment extends Fragment implements View.OnClickListener, Nokri_PopupManager.ConfirmInterface, ProgressRequestBody.UploadCallbacks, RuntimePermissionHelper.permissionInterface {
    private TextView yourResumeTextView, srTextView, nameTextView, viewReusmeTextView, editResumeTextView;
    private Button addMoreButton;
    private Nokri_FontManager fontManager;
    private RecyclerView recyclerView;
    private List<Nokri_ResumeModel> modelList;
    RelativeLayout parentLayout;
    String candidateName, fileExtension;

    private ArrayList<String> paths = new ArrayList<>();
    private Nokri_PopupManager popupManager;
    private String id;
    private Nokri_YourResumeAdapter adapter;
    RuntimePermissionHelper runtimePermissionHelper;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private DownloadResult serviceResult;
    private Nokri_UploadProgressDialolque progressDialolque;
    private Snackbar snackbar;
    private Nokri_DialogManager dialogManager;
    Nokri_YourResumeFragment mFragment;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_YourResumeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mFragment = this;
        nokri_initialize();
        nokri_setFonts();

        addMoreButton.setOnClickListener(this);

        //  setupRecyclerview();
        nokri_getCandidateResumeList();
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getResume());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_your_resume, container, false);
    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(yourResumeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(srTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(viewReusmeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(editResumeTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
        //  fontManager.nokri_setOpenSenseFontButton(addMoreButton,getActivity().getAssets());
    }

    private void setupRecyclerview() {

        //populuateListWithDummyData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_YourResumeAdapter(modelList, getContext(), new Nokri_YourResumeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_ResumeModel item, int flag) {
                runtimePermissionHelper.requestStorageCameraPermission(1);

                if (flag == 0) {
//                    Intent intent = new Intent(getActivity(),Nokri_DownloadService.class);
//                    intent.putExtra("url",item.getLink());
//                    intent.putExtra("filename",item.getName());
//                    intent.putExtra("result",serviceResult);
//                    getActivity().startService(intent);
                    candidateName = item.getName();
                    new DownloadFileFromURL().execute(item.getLink());

                }
                if (flag == 1) {

                    popupManager.nokri_showDeletePopup();
                    id = item.getId();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAlertDialog();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressDialolque.updateProgress(percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccessPermission(int code) {

    }

    @Override
    public void onBackPressed() {

    }

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

    public boolean downloadedWithoutException = true;

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Nokri_ToastManager.showShortToast(getActivity(), "Download Started");

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
//                String fileExtension = f_url[0].substring(f_url[0].lastIndexOf("."));
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Download/" + candidateName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadedWithoutException = false;
                Nokri_ToastManager.showShortToast(getActivity(), e.getLocalizedMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if (downloadedWithoutException) {
                Snackbar.make(parentLayout, "Downloaded", Snackbar.LENGTH_LONG)
                        .show();
            }
            downloadedWithoutException = true;

        }

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

    private void nokri_deleteResume() {


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("resume_id", id);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            nokri_getCandidateResumeList();
                        } else {
                            dialogManager.showCustom(responseObject.message());

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


    private void nokri_initialize() {
        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        modelList = new ArrayList<>();
        parentLayout = getView().findViewById(R.id.container);
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);
        fontManager = new Nokri_FontManager();
        recyclerView = getView().findViewById(R.id.recyclerview);
        yourResumeTextView = getView().findViewById(R.id.txt_your_resume);
        srTextView = getView().findViewById(R.id.txt_sr);
        ;
        nameTextView = getView().findViewById(R.id.txt_name);
        viewReusmeTextView = getView().findViewById(R.id.txt_view);
        editResumeTextView = getView().findViewById(R.id.txt_edit);
        emptyTextView = getView().findViewById(R.id.txt_empty);

        addMoreButton = getView().findViewById(R.id.btn_add_more);

        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);

        serviceResult = new DownloadResult(new Handler(Looper.getMainLooper()));


        popupManager = new Nokri_PopupManager(getContext(), this);

        fontManager = new Nokri_FontManager();
    }


    private void nokri_getCandidateResumeList() {
        dialogManager = new Nokri_DialogManager();

        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
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
                        modelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.d("responceuplaodresumec", response.toString());
                        String buttonText = null;
                        String deleteButtonText = null;
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray extras = data.getJSONArray("extras");
                            for (int i = 0; i < extras.length(); i++) {
                                JSONObject object = extras.getJSONObject(i);
                                if (object.getString("field_type_name").equals("section_label")) {
                                    yourResumeTextView.setText(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("ad_more_btn")) {
                                    addMoreButton.setText(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("sr_txt")) {
                                    srTextView.setText(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("resume_name")) {
                                    nameTextView.setText(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("dwnld_resume")) {
                                    viewReusmeTextView.setText(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("del_resume")) {
                                    editResumeTextView.setText(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("dwnld")) {
                                    buttonText = object.getString("value");
                                } else if (object.getString("field_type_name").equals("section_name")) {
                                    TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                    toolbarTitleTextView.setText(object.getString("value"));
                                }
                                if (object.getString("field_type_name").equals("del_resume")) {
                                    deleteButtonText = object.getString("value");
                                }
                                if (object.getString("field_type_name").equals("not_added")) {
                                    emptyTextView.setText(object.getString("value"));
                                }
                            }


                            JSONArray dataArray = data.getJSONArray("resumes");
                            if (dataArray.length() <= 0) {
                                messageContainer.setVisibility(View.VISIBLE);
                                dialogManager.hideAlertDialog();

                            } else
                                messageContainer.setVisibility(View.GONE);


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONArray objectArray = dataArray.getJSONArray(i);
                                Nokri_ResumeModel model = new Nokri_ResumeModel();
                                model.setSrNum(i + 1 + "");
                                model.setButtonText(buttonText);
                                model.setDeleteButtonText(deleteButtonText);
                                for (int j = 0; j < objectArray.length(); j++) {
                                    JSONObject dataObject = objectArray.getJSONObject(j);

                                    if (dataObject.getString("field_type_name").equals("resume_name")) {
                                        model.setName(dataObject.getString("value"));
                                    } else if (dataObject.getString("field_type_name").equals("resume_url")) {
                                        model.setLink(dataObject.getString("value"));
                                    } else if (dataObject.getString("field_type_name").equals("resume_id"))
                                        model.setId(dataObject.getString("value"));

                                    if (j + 1 == objectArray.length())
                                        modelList.add(model);
                                }
                            }
                            setupRecyclerview();
                        } else {
                            dialogManager.showCustom(response.getString("message"));
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

//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(paths)
//                .setActivityTheme(R.style.AppTheme)
//                .enableDocSupport(true) //if you want to hide default tabs for document
//                .pickFile(this);
        if (R.id.txt_add_resume == R.id.txt_add_resume) {
            new LFilePicker()
                    .withSupportFragment(mFragment)
                    .withRequestCode(FilePickerConst.REQUEST_CODE_DOC)
                    .withStartPath("/storage/emulated/0")
                    .withFileFilter(new String[]{".pdf", ".xlsx", ".xls", ".doc", ".docx", ".ppt", ".pptx", ".txt"})
                    .withMaxNum(1)
                    .start();
//            Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
//            intent4.putExtra(Constant.MAX_NUMBER, 9);
//            intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf","txt"});
//            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(FilePickerConst.REQUEST_CODE_DOC == requestCode){
//            if(resultCode== Activity.RESULT_OK && data!=null)
//
//            {
//
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//
//                nokri_uploadResumeRequest(docPaths.get(0));
//            }
//        }

        /*if(requestCode == REQUEST_CODE && data!=null){
            nokri_uploadResumeRequest(Nokri_PathUtils.getPath(this,data.getData()));*/
        if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == RESULT_OK && data != null) {
//        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE));

                ArrayList<String> docPaths = data.getStringArrayListExtra("paths");
                Log.d("Document Paths", docPaths.get(0));
                nokri_uploadResumeRequest(docPaths.get(0));
            }
        }
    }


    private void nokri_uploadResumeRequest(String absolutePath) {

        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        // RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.d("responceuplaodresumeu", jsonObject.toString());
                        if (jsonObject.getBoolean("success")) {
                            // JSONArray dataArray = jsonObject.getJSONArray("data");

                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            // Log.v("Resume Upload",dataArray.toString());


                            nokri_getCandidateResumeList();
                            progressDialolque.handleSuccessScenerion();

                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            progressDialolque.handleFailedScenerio();
                            // nokri_getCandidateResumeList();
                        }
                    } catch (JSONException e) {


                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                    } catch (IOException e) {

                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialolque.handleFailedScenerio();
                t.printStackTrace();
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });
    }


    @Override
    public void onConfirmClick(Dialog dialog) {

        nokri_deleteResume();
        dialog.dismiss();
    }
}
