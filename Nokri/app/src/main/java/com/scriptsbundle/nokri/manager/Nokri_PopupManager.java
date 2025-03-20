package com.scriptsbundle.nokri.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.manager.models.Nokri_PopupModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Config;

/**
 * Created by Glixen Technologies on 18/01/2018.
 */

public class Nokri_PopupManager {

    private Context context;
    private Nokri_PopupModel popupModel;
    private Nokri_FontManager fontManager;

    public interface ConfirmInterface {
        void onConfirmClick(Dialog dialog);
    }

    public interface NoInternetInterface {
        void onButtonClick(DialogInterface dialog);

        void onNoClick(DialogInterface dialog);
    }

    private ConfirmInterface confirmInterface;

    public Nokri_PopupManager(Context context, ConfirmInterface confirmInterface) {
        this.context = context;
        this.confirmInterface = confirmInterface;
        fontManager = new Nokri_FontManager();
    }

    // delete title
    private void showDeleteDialog() {
        popupModel = Nokri_SharedPrefManager.getPopupSettings(context);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_delete_jobs);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        final Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        final Button closeButton = dialog.findViewById(R.id.btn_close);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        confirmButton.setText(popupModel.getConfirmButton());
        closeButton.setText(popupModel.getCancelButton());
        deleteJobTextView.setText(popupModel.getConfirmText());
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, context.getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmInterface != null)
                    confirmInterface.onConfirmClick(dialog);
                // dialog.dismiss();
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    //requires only exit text
    private void showCustomTitleDialog(String title) {
        popupModel = Nokri_SharedPrefManager.getPopupSettings(context);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_delete_jobs);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        deleteJobTextView.setText(title);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        final Button closeButton = dialog.findViewById(R.id.btn_close);

        confirmButton.setText(popupModel.getConfirmButton());
        deleteJobTextView.setText(popupModel.getConfirmText());
        closeButton.setText(popupModel.getCancelButton());
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, context.getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmInterface != null)
                    confirmInterface.onConfirmClick(dialog);
                //dialog.dismiss();
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    //Not Being Used
    private void showCustomDialog(String title, String buttonOneText, String buttonTwoText) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.popup_delete_jobs);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        deleteJobTextView.setText(title);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        confirmButton.setText(buttonOneText);

        final Button closeButton = dialog.findViewById(R.id.btn_close);
        closeButton.setText(buttonTwoText);
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, context.getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmInterface.onConfirmClick(dialog);
                //dialog.dismiss();
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    //Set By Server Response needs successfully text
    private void showSuccessDialog(String message) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_saved_jobs_success);
        TextView successTextView = dialog.findViewById(R.id.txt_success);
        popupModel = Nokri_SharedPrefManager.getPopupSettings(context);
        successTextView.setText(popupModel.getSuccessText());
        TextView successTextViewData = dialog.findViewById(R.id.txt_success_data);
        ImageView closeImageView = dialog.findViewById(R.id.img_close);
        successTextViewData.setText(message);
        fontManager.nokri_setMonesrratSemiBioldFont(successTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(successTextViewData, context.getAssets());

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    public void nokri_showSuccessPopup(String message) {
        showSuccessDialog(message);
    }

    public void nokri_showDeletePopup() {
        showDeleteDialog();
    }

    public void nokri_showPopupWithCustomMessage(String message) {
        showCustomTitleDialog(message);
    }

    public void nokri_showPopupCustom(String message, String leftButtonText, String rightButtonText) {
        showCustomDialog(message, leftButtonText, rightButtonText);
    }

    public static void nokri_showNoInternetAlert(Context context, final NoInternetInterface noInternetInterface) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Connection Lost!")
                .setMessage("Close App?").setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        noInternetInterface.onButtonClick(dialog);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noInternetInterface.onNoClick(dialog);
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


}

