package com.scriptsbundle.nokri.candidate.edit.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import com.iceteck.silicompressorr.SiliCompressor;
import com.scriptsbundle.nokri.FilePicker.LFilePicker;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.adapters.Nokri_FilePreviewAdapter;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_FileModel;
import com.scriptsbundle.nokri.custom.ProgressRequestBody;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_UploadProgressDialolque;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import droidninja.filepicker.FilePickerConst;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AddResumeFragment extends Fragment implements View.OnClickListener, Nokri_FilePreviewAdapter.OnItemClickListener, Nokri_PopupManager.ConfirmInterface, ProgressRequestBody.UploadCallbacks {
    private Nokri_FontManager fontManager;
    private TextView addResumeTextView, resumeFormatAllowedTextView, dropFilesTextView;
    private Button saveSkillsButton;
    private RelativeLayout fileUpload;
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Nokri_FileModel> modelList;
    private Nokri_PopupManager popupManager;
    private Nokri_FilePreviewAdapter adapter;
    private RecyclerView recyclerView;
    private String id;
    private Nokri_UploadProgressDialolque progressDialolque;
    private Nokri_DialogManager dialogManager;
    String noVideoAlert;
    EditText videoEditText;
    Button saveVideoButton;
    Nokri_AddResumeFragment mFragment;
    String videoLimit, videoLimitText;
    public static final String LOG_TAG = Nokri_AddPortfolioFragment.class.getSimpleName();
    String mCurrentPhotoPath;
    Uri capturedUri = null;
    boolean youtubeVideo = true;
    Uri compressUri = null;
    TextView picDescription;
    private Button buttonSelectVideo;
    LinearLayout compressionMsg;
    LinearLayout youtubeVideoContainer, normalVideoContainer;
    RestService restService;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_AddResumeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_add_resume, container, false);
        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
        this.mFragment = this;
        restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        dialogManager = new Nokri_DialogManager();

        buttonSelectVideo = view.findViewById(R.id.btn_select_video);
        picDescription = view.findViewById(R.id.pic_description);
        compressionMsg = view.findViewById(R.id.compressionMsg);

        buttonSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(TYPE_VIDEO);
            }
        });
        nokri_initialize(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_setFonts();
        try {

            nokri_getCandidateResumeList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(addResumeTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(resumeFormatAllowedTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(dropFilesTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(saveSkillsButton, getActivity().getAssets());
    }

    private void nokri_initialize(View view) {
        fontManager = new Nokri_FontManager();
        fileUpload = view.findViewById(R.id.file_upload);
        addResumeTextView = view.findViewById(R.id.txt_add_resume);
        resumeFormatAllowedTextView = view.findViewById(R.id.txt_resume_format_allowed);
        dropFilesTextView = view.findViewById(R.id.txt_drop_files);
        videoEditText = view.findViewById(R.id.edittxt_youtube);
        saveVideoButton = view.findViewById(R.id.btn_saveyoutube);
        saveVideoButton.setOnClickListener(this);
        normalVideoContainer = view.findViewById(R.id.normalVideoContainer);
        youtubeVideoContainer = view.findViewById(R.id.youtubeVideoContainer);

        saveSkillsButton = view.findViewById(R.id.btn_saveskills);
        Nokri_Utils.setEditBorderButton(getContext(), saveSkillsButton);
        recyclerView = view.findViewById(R.id.recyclerview);
        popupManager = new Nokri_PopupManager(getContext(), this);
        fileUpload.setOnClickListener(this);
    }

    private void nokri_setupRecyclerview() {


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_FilePreviewAdapter(modelList, getContext(), this, 0);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAfterDelay(6000);
    }

    private void nokri_deleteResume() {

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
        // Call<ResponseBody> myCall = restService.postDeleteResume(params,Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            updateResumeList();
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


    private void updateResumeList() {
        dialogManager.showAlertDialog(getActivity());
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                String buttonText = null;
                modelList = new ArrayList<>();

                JSONObject response = null;
                try {
                    response = new JSONObject(responseObject.body().string());


                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray extrasArray = data.getJSONArray("extras");

                        for (int i = 0; i < extrasArray.length(); i++) {

                            JSONObject extra = extrasArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("section_name")) {
                                addResumeTextView.setText(extra.getString("value") + ":");
                            } else if (extra.getString("field_type_name").equals("section_text")) {
                                resumeFormatAllowedTextView.setText(extra.getString("value"));
                            } else if (extra.getString("field_type_name").equals("click_text")) {
                                if (data.getJSONArray("resumes").length() == 0) {
                                    dropFilesTextView.setText(extra.getString("value"));
                                    dropFilesTextView.setVisibility(View.VISIBLE);
                                } else dropFilesTextView.setVisibility(View.GONE);
                            } else if (extra.getString("field_type_name").equals("btn_name")) {
                                saveSkillsButton.setText(extra.getString("value"));
                            } else if (extra.getString("field_type_name").equals("del_resume")) {
                                buttonText = extra.getString("value");
                            }

                        }


                        JSONArray dataArray = data.getJSONArray("resumes");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONArray objectArray = dataArray.getJSONArray(i);
                            Nokri_FileModel model = new Nokri_FileModel();

                            for (int j = 0; j < objectArray.length(); j++) {
                                JSONObject dataObject = objectArray.getJSONObject(j);

                                if (dataObject.getString("field_type_name").equals("resume_name")) {
                                    model.setName(dataObject.getString("value"));

                                } else if (dataObject.getString("field_type_name").equals("resume_url")) {
                                    model.setUrl(dataObject.getString("value"));
                                } else if (dataObject.getString("field_type_name").equals("resume_id"))
                                    model.setId(dataObject.getString("value"));
                                model.setButtonText(buttonText);
                                if (j + 1 == objectArray.length())
                                    modelList.add(model);
                            }
                        }

                        JSONArray videoArray = data.getJSONArray("extra");
                        //Resume Video Section
                        for (int i = 0; i < videoArray.length(); i++) {
                            JSONObject jsonObject = videoArray.getJSONObject(i);
                            if (jsonObject.getString("field_type_name").equals("video_url")) {
                                String videoUrlHint = jsonObject.getString("key");
                                String videoUrl = jsonObject.getString("value");
                                videoEditText.setHint(videoUrlHint);
                                videoEditText.setText(videoUrl);
                            } else if (jsonObject.getString("field_type_name").equals("no_video_url")) {
                                noVideoAlert = jsonObject.getString("value");
                            } else if (jsonObject.getString("field_type_name").equals("is_video_upload")) {
                                buttonSelectVideo.setText(jsonObject.getString("key"));
                                if (jsonObject.getString("value").equals("0")) {
                                    youtubeVideoContainer.setVisibility(View.VISIBLE);
                                    normalVideoContainer.setVisibility(View.GONE);
                                } else {
                                    normalVideoContainer.setVisibility(View.VISIBLE);
                                    youtubeVideoContainer.setVisibility(View.GONE);
                                }
                            } else if (jsonObject.getString("field_type_name").equals("video_limit")) {
                                videoLimit = jsonObject.getString("value");
                                videoLimitText = jsonObject.getString("key");
                            } else if (jsonObject.getString("field_type_name").equals("video_save_btn")) {
                                String buttonTxt = jsonObject.getString("value");
                                saveVideoButton.setText(buttonTxt);
                            }
                        }
                        noVideoAlert = data.getJSONArray("extra").getJSONObject(1).getString("value");

                        Nokri_Utils.setBordederButton(getActivity(), saveVideoButton);
                        saveVideoButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                        nokri_setupRecyclerview();
                    } else {
                        dialogManager.showCustom(response.getString("message"));
                        dialogManager.hideAfterDelay();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void nokri_getCandidateResumeList() throws JSONException {
        String buttonText = null;
        modelList = new ArrayList<>();

        JSONObject response = Nokri_CandidateEditProfileFragment.resumeList;

        if (response.getBoolean("success")) {
            JSONObject data = response.getJSONObject("data");
            JSONArray extrasArray = data.getJSONArray("extras");

            for (int i = 0; i < extrasArray.length(); i++) {

                JSONObject extra = extrasArray.getJSONObject(i);
                if (extra.getString("field_type_name").equals("section_name")) {
                    addResumeTextView.setText(extra.getString("value") + ":");
                } else if (extra.getString("field_type_name").equals("section_text")) {
                    resumeFormatAllowedTextView.setText(extra.getString("value"));
                } else if (extra.getString("field_type_name").equals("click_text")) {
                    if (data.getJSONArray("resumes").length() == 0) {
                        dropFilesTextView.setText(extra.getString("value"));
                        dropFilesTextView.setVisibility(View.VISIBLE);
                    } else dropFilesTextView.setVisibility(View.GONE);
                } else if (extra.getString("field_type_name").equals("btn_name")) {
                    saveSkillsButton.setText(extra.getString("value"));
                } else if (extra.getString("field_type_name").equals("del_resume")) {
                    buttonText = extra.getString("value");
                }

            }


            JSONArray dataArray = data.getJSONArray("resumes");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONArray objectArray = dataArray.getJSONArray(i);
                Nokri_FileModel model = new Nokri_FileModel();

                for (int j = 0; j < objectArray.length(); j++) {
                    JSONObject dataObject = objectArray.getJSONObject(j);

                    if (dataObject.getString("field_type_name").equals("resume_name")) {
                        model.setName(dataObject.getString("value"));

                    } else if (dataObject.getString("field_type_name").equals("resume_url")) {
                        model.setUrl(dataObject.getString("value"));
                    } else if (dataObject.getString("field_type_name").equals("resume_id"))
                        model.setId(dataObject.getString("value"));
                    model.setButtonText(buttonText);
                    if (j + 1 == objectArray.length())
                        modelList.add(model);
                }
            }

            JSONArray videoArray = data.getJSONArray("extra");
            //Resume Video Section
            for (int i = 0; i < videoArray.length(); i++) {
                JSONObject jsonObject = videoArray.getJSONObject(i);
                if (jsonObject.getString("field_type_name").equals("video_url")) {
                    String videoUrlHint = jsonObject.getString("key");
                    String videoUrl = jsonObject.getString("value");
                    videoEditText.setHint(videoUrlHint);
                    videoEditText.setText(videoUrl);
                } else if (jsonObject.getString("field_type_name").equals("no_video_url")) {
                    noVideoAlert = jsonObject.getString("value");
                } else if (jsonObject.getString("field_type_name").equals("is_video_upload")) {
                    buttonSelectVideo.setText(jsonObject.getString("key"));
                    if (jsonObject.getString("value").equals("0")) {
                        youtubeVideoContainer.setVisibility(View.VISIBLE);
                        normalVideoContainer.setVisibility(View.GONE);
                    } else {
                        normalVideoContainer.setVisibility(View.VISIBLE);
                        youtubeVideoContainer.setVisibility(View.GONE);
                    }
                } else if (jsonObject.getString("field_type_name").equals("video_limit")) {
                    videoLimit = jsonObject.getString("value");
                    videoLimitText = jsonObject.getString("key");
                } else if (jsonObject.getString("field_type_name").equals("video_save_btn")) {
                    String buttonTxt = jsonObject.getString("value");
                    saveVideoButton.setText(buttonTxt);
                }
            }
            noVideoAlert = data.getJSONArray("extra").getJSONObject(1).getString("value");

            Nokri_Utils.setBordederButton(getActivity(), saveVideoButton);
            saveVideoButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            nokri_setupRecyclerview();
        } else {
            dialogManager.showCustom(response.getString("message"));
            dialogManager.hideAfterDelay();
        }
    }


    @Override
    public void onClick(View view) {
//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(paths)
//                .setActivityTheme(R.style.AppTheme)
//                .pickFile(this);

        if (view.getId() == R.id.file_upload) {

            new LFilePicker()
                    .withSupportFragment(mFragment)
                    .withRequestCode(FilePickerConst.REQUEST_CODE_DOC)
                    .withStartPath("/storage/emulated/0")
                    .withFileFilter(new String[]{".pdf", ".xlsx", ".xls", ".doc", ".docx", ".ppt", ".pptx", ".txt"})
                    .withMaxNum(1)
                    .start();
        } else if (view.getId() == saveVideoButton.getId()) {
            saveVideo();
        }
    }

    public void saveVideo() {

        dialogManager.showAlertDialog(getActivity());

        JsonObject params = new JsonObject();
        if (videoEditText.getText().toString().equals("")) {
            dialogManager.hideAlertDialog();
            Toast.makeText(getActivity(), noVideoAlert, Toast.LENGTH_SHORT).show();
            return;
        }
        params.addProperty("cand_video", videoEditText.getText().toString());
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.saveResumeVideo(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.saveResumeVideo(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(response.body().string());

                        dialogManager.hideAlertDialog();


                    } catch (IOException | JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(FilePickerConst.REQUEST_CODE_DOC == requestCode){
//
//            if(resultCode== Activity.RESULT_OK && data!=null)
//
//            {
//
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//
//                nokri_uploadResumeRequest(docPaths.get(0));
//            }
//
//        }
        if (FilePickerConst.REQUEST_CODE_DOC == requestCode) {
            if (resultCode == RESULT_OK && data != null) {
//        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);

                ArrayList<String> docPaths = data.getStringArrayListExtra("paths");

                Log.d("SHittttttttttttttttt", docPaths.get(0));
                nokri_uploadResumeRequest(docPaths.get(0));
            }

        }
        if (requestCode == REQUEST_TAKE_VIDEO) {

            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    //create destination directory
                    File f = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
                    File file = new File(String.valueOf(data.getData()));
                    long length = f.length();
                    Uri returnUri = data.getData();
                    Cursor returnCursor =
                            getActivity().getContentResolver().query(returnUri, null, null, null, null);
                    /*
                     * Get the column indexes of the data in the Cursor,
                     * move to the first row in the Cursor, get the data,
                     * and display it.
                     */
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    long fileSize = bytesToMeg(Long.parseLong(returnCursor.getString(sizeIndex)));

                    if (fileSize > Long.parseLong(videoLimit)) {
                        Toast.makeText(getActivity(), videoLimitText, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (f.mkdirs() || f.isDirectory()) {
                        //compress and output new video specs
                        //new VideoCompressAsyncTask(this).execute("true", mCurrentPhotoPath, f.getPath());
//                        Toast.makeText(getActivity(), String.valueOf(length), Toast.LENGTH_SHORT).show();
                        new VideoCompressAsyncTask(getActivity()).execute("false", data.getData().toString(), f.getPath());
                    }

                }
            }
        }
    }

    private static final long MEGABYTE = 1000L * 1000L;

    public static long bytesToMeg(long bytes) {
        return bytes / MEGABYTE;
    }


    public static final String FILE_PROVIDER_AUTHORITY = ".provider";
    private static final int REQUEST_TAKE_CAMERA_PHOTO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID = 2;
    private static final int REQUEST_TAKE_VIDEO = 200;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;


    private void requestPermissions(int mediaType) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (mediaType == TYPE_IMAGE) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID);
            }

        } else {
            if (mediaType == TYPE_VIDEO) {
                // Want to compress a video
                dispatchTakeVideoIntent();
            }

        }
    }

    private File createMediaFile(int type) throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = (type == TYPE_IMAGE) ? "JPEG_" + timeStamp + "_" : "VID_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(
                type == TYPE_IMAGE ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        File file = File.createTempFile(
                fileName,  /* prefix */
                type == TYPE_IMAGE ? ".jpg" : ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Get the path of the file created
        mCurrentPhotoPath = file.getAbsolutePath();
        Log.d(LOG_TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath);
        return file;
    }


    private void dispatchTakeVideoIntent() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {
            capturedUri = FileProvider.getUriForFile(getActivity(),
                    getActivity().getPackageName() + FILE_PROVIDER_AUTHORITY,
                    createMediaFile(TYPE_VIDEO));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_VIDEO);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        takeVideoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        takeVideoIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//
//                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                capturedUri = FileProvider.getUriForFile(this,
//                        getPackageName() + FILE_PROVIDER_AUTHORITY,
//                        createMediaFile(TYPE_VIDEO));
//
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
//                Log.d(LOG_TAG, "VideoUri: " + capturedUri.toString());
//                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }


    }


    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonSelectVideo.setEnabled(false);
            compressionMsg.setVisibility(View.VISIBLE);
            picDescription.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                //This bellow is just a temporary solution to test that method call works
                boolean b = Boolean.parseBoolean(paths[0]);
                if (b) {
                    filePath = SiliCompressor.with(mContext).compressVideo(paths[1], paths[2]);
                } else {
                    Uri videoContentUri = Uri.parse(paths[1]);
                    // Example using the bitrate and video size parameters
                    filePath = SiliCompressor.with(getActivity()).compressVideo(
                            videoContentUri,
                            paths[2],
                            1080,
                            720,
                            2500000);
//                    filePath = SiliCompressor.with(mContext).compressVideo(
//                            videoContentUri,
//                            paths[2]);
                }


            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);

            File imageFile = new File(compressedFilePath);
            compressUri = Uri.fromFile(imageFile);
//            videoView.setVideoURI(Uri.parse(compressedFilePath));
//            videoView.start();
//            videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Intent i = new Intent(getActivity(), VideoActivity.class);
////                    i.putExtra("video", compressedFilePath);
////                    startActivity(i);
//                }
//            });
            String name = imageFile.getName();
            float length = imageFile.length() / 1024f; // Size in KB

            buttonSelectVideo.setEnabled(true);
            String value = length + " KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", "Video Compressed", name, value);
            compressionMsg.setVisibility(View.GONE);
            picDescription.setVisibility(View.VISIBLE);
            picDescription.setText(text);
//            nokri_uploadVideoResumeRequest(compressedFilePath);
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }

    private void nokri_uploadVideoResumeRequest(String absolutePath) {

        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("resume_video", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.v("Resume Upload", jsonObject.toString());
                        if (jsonObject.getBoolean("success")) {


                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));


//                            nokri_getCandidateResumeList();
                            progressDialolque.handleSuccessScenerion();
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
//                            nokri_getCandidateResumeList();
                            progressDialolque.handleFailedScenerio();
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                progressDialolque.handleFailedScenerio();
                t.printStackTrace();
            }
        });
    }


    private void nokri_uploadResumeRequest(String absolutePath) {

        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.v("Resume Upload", jsonObject.toString());
                        if (jsonObject.getBoolean("success")) {


                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));


                            updateResumeList();
                            progressDialolque.handleSuccessScenerion();
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
//                            updateResumeList();
                            progressDialolque.handleFailedScenerio();
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                progressDialolque.handleFailedScenerio();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(Nokri_FileModel item, int position) {
        id = item.getId();
        popupManager.nokri_showDeletePopup();

    }

    @Override
    public void onConfirmClick(Dialog dialog) {
        dialog.dismiss();
        nokri_deleteResume();

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
}
