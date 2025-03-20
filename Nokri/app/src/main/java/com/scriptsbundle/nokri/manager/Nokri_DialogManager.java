package com.scriptsbundle.nokri.manager;

import android.app.Activity;
import android.util.Log;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Config;

/**
 * Created by Glixen Technologies on 11/01/2018.
 */

public class Nokri_DialogManager {
    private static final int DEFAULT_DISMISS_TIME = 0;
    private Nokri_FullScreenAlertDialog fullScreenDialog;

    public void showAlertDialog(final Activity context){

        fullScreenDialog = new Nokri_FullScreenAlertDialog(context, R.style.PopupTheme);
         if(!fullScreenDialog.isShowing()) {
            if(!context.isFinishing()) {
                try {

                    fullScreenDialog.setCustomMessage(Nokri_Globals.PLEASE_WAIT_TEXT);
                    if(!context.isFinishing()||!fullScreenDialog.isShowing()) {
                        fullScreenDialog.setCancelable(false);
                        fullScreenDialog.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(fullScreenDialog!=null){

                                    try{
                                        fullScreenDialog.dismiss();

                                    }
                                    catch (Exception e){}
                                }
                            }
                        }, Nokri_Config.ALERT_DIALOG_TIMEOUT);

                    }
                    }
                catch (IllegalStateException e){
                    Log.e("IllegalStateException", "Exception", e);

                }
                 catch (NullPointerException ex){  Log.e("IllegalStateException", "Exception", ex);}
                catch (Exception exc){Log.e("IllegalStateException", "Exception", exc);}
            }
        }
        else
        {
            return;
        }
    }



    public  void hideAlertDialog(){

            try {
                if(fullScreenDialog!=null)
                fullScreenDialog.dismiss();
            }
            catch (IllegalStateException e){}
            catch (NullPointerException ex){}
    }
    public  void showError(){
        if(fullScreenDialog!=null)
           try {
               fullScreenDialog.showError();
           }
           catch (IllegalStateException e){}
           catch (NullPointerException ex){}
    }
    public  void showCustom(String custom){
        if(fullScreenDialog!=null)
        Nokri_ToastManager.showLongToast(fullScreenDialog.getContext(),custom);
 /*       if(fullScreenDialog!=null)
        {
            if(!fullScreenDialog.isShowing())
                return;
          try {
              fullScreenDialog.setCustomMessage(custom);
              fullScreenDialog.showCustomMessage();
          }
          catch (IllegalStateException e)
          {}
          catch (NullPointerException ex){}
        }*/

    }
    public  void hideAfterDelay(){
        if(fullScreenDialog!=null)
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {if(fullScreenDialog!=null)
                        fullScreenDialog.dismiss();
                    }
                    catch (IllegalStateException e){}
                    catch (NullPointerException e){}
                }
            },DEFAULT_DISMISS_TIME);
        }

    }

    public  void hideAfterDelay(int timeInMiliseconds){
        if(fullScreenDialog!=null)
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                           try {if(fullScreenDialog!=null)
                               fullScreenDialog.dismiss();

                           }
                    catch (IllegalStateException e){}
                    catch (NullPointerException e){}
                }
            },timeInMiliseconds);
        }

    }



}
