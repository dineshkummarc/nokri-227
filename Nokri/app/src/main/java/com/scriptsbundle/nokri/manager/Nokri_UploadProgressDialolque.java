package com.scriptsbundle.nokri.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.manager.models.Nokri_ProgressModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Globals;

/**
 * Created by GlixenTech on 3/22/2018.
 */

public class Nokri_UploadProgressDialolque implements Nokri_PopupManager.ConfirmInterface {

    private Context context;
    private Nokri_FontManager fontManager;
    private Dialog dialog;
    private ProgressBar progressBar;
    TextView titleTextView;
    TextView messageTextView;
    Button closeButton;
    TextView percentageTextView;
    TextView outofTextView;
    ImageButton closeImageButton;
    private Nokri_ProgressModel progressModel;
    private CloseClickListener closeClickListener;
    private Nokri_PopupManager popupManager;

    public Nokri_UploadProgressDialolque(Context context) {
        this.context = context;
        fontManager = new Nokri_FontManager();
        dialog = new Dialog(context);
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
  this.dialog.dismiss();
             dialog.dismiss();
                if(closeClickListener!=null)
                    closeClickListener.onCloseClick();
    }

    public interface CloseClickListener{
        void onCloseClick();
    }
    public void setCloseClickListener(CloseClickListener closeClickListener){
        this.closeClickListener = closeClickListener;
        if(closeImageButton!=null)
            closeImageButton.setVisibility(View.VISIBLE);
    }


    public void showUploadDialogue(){

        progressModel = Nokri_SharedPrefManager.getProgressSettings(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upload_file_popup);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

         titleTextView = dialog.findViewById(R.id.txt_title);
         messageTextView = dialog.findViewById(R.id.txt_message);
        progressBar = dialog.findViewById(R.id.progress);
        percentageTextView = dialog.findViewById(R.id.txt_progress);
        closeButton = dialog.findViewById(R.id.btn_close );
        outofTextView = dialog.findViewById(R.id.txt_outof);
        closeImageButton = dialog.findViewById(R.id.img_btn_close);
        closeButton.setText(progressModel.getButtonText());
        titleTextView.setText(progressModel.getTitle());

        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(messageTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontButton(closeButton,context.getAssets());
               dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) closeButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));

    closeImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(dialog!=null && dialog.isShowing())
            {

                popupManager = new Nokri_PopupManager(context,Nokri_UploadProgressDialolque.this);
                popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);

            }
        }
    });
    }

    public void updateProgress(int progess){
        progressBar.setProgress(progess);
        percentageTextView.setVisibility(View.VISIBLE);
        percentageTextView.setText(progess+"%");
    }
    public void updateProgress(int progess,int currentFile,int totalFiles){
        String outOfText = currentFile+"/"+totalFiles;
        outofTextView.setText(outOfText);
        outofTextView.setVisibility(View.VISIBLE);
        progressBar.setProgress(progess);
        percentageTextView.setVisibility(View.VISIBLE);
        percentageTextView.setText(progess+"%");

    }
    public void handleSuccessScenerion(){

        progressModel = Nokri_SharedPrefManager.getProgressSettings(context);
        closeImageButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
        outofTextView.setVisibility(View.INVISIBLE);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(progressModel.getSuccessTitle());
        messageTextView.setVisibility(View.VISIBLE);
       messageTextView.setText(progressModel.getSuccessMessage());
        percentageTextView.setText("100%");
       percentageTextView.setVisibility(View.VISIBLE);
        closeButton.setText(progressModel.getButtonText());
       closeButton.setVisibility(View.VISIBLE);
       closeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.dismiss();
           }
       });

    }
    public void handleFailedScenerio(){
        progressModel = Nokri_SharedPrefManager.getProgressSettings(context);
        closeImageButton.setVisibility(View.GONE);
        percentageTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
        outofTextView.setVisibility(View.INVISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(progressModel.getFailTitles());
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(progressModel.getFailMessage());
        closeButton.setText(progressModel.getButtonText());
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
    }

    public void handleFailedScenerio(String error){
        progressModel = Nokri_SharedPrefManager.getProgressSettings(context);
        percentageTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
        outofTextView.setVisibility(View.INVISIBLE);
        titleTextView.setVisibility(View.VISIBLE);
        closeImageButton.setVisibility(View.GONE);
        titleTextView.setText(error);
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(progressModel.getFailMessage());
        closeButton.setText(progressModel.getButtonText());
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
