package com.scriptsbundle.nokri.RichEditor.styles.toolitems.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.scriptsbundle.nokri.RichEditor.AREditText;
import com.scriptsbundle.nokri.RichEditor.spans.AreSubscriptSpan;
import com.scriptsbundle.nokri.RichEditor.styles.ARE_ABS_Style;
import com.scriptsbundle.nokri.RichEditor.styles.ARE_Helper;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.IARE_ToolItem_Updater;


public class ARE_Style_Subscript extends ARE_ABS_Style<AreSubscriptSpan> {

    private ImageView mSubscriptImage;

    private boolean mSubscriptChecked;

    private AREditText mEditText;

    private IARE_ToolItem_Updater mCheckUpdater;

    /**
     * @param imageView image view
     */
    public ARE_Style_Subscript(AREditText editText, ImageView imageView, IARE_ToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mSubscriptImage = imageView;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mSubscriptImage);
    }

    /**
     * @param editText edit text
     */
    public void setEditText(AREditText editText) {
        this.mEditText = editText;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubscriptChecked = !mSubscriptChecked;
                ARE_Helper.updateCheckStatus(ARE_Style_Subscript.this, mSubscriptChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(),
                            mEditText.getSelectionStart(),
                            mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return mSubscriptImage;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mSubscriptChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mSubscriptChecked;
    }

    @Override
    public AreSubscriptSpan newSpan() {
        return new AreSubscriptSpan();
    }
}
