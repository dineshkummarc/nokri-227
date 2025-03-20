package com.scriptsbundle.nokri.utils.models;

import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GlixenTech on 3/22/2018.
 */

public class Nokri_EditTextModel {
    private EditText editText;
    private boolean isRequired;

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public void setRequired(JSONObject required) {
        try {
            isRequired = required.getBoolean("is_required");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

