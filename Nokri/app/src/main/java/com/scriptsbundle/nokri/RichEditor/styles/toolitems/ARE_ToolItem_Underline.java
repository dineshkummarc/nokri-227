package com.scriptsbundle.nokri.RichEditor.styles.toolitems;

import android.content.Context;
import android.text.Editable;
import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.RichEditor.AREditText;
import com.scriptsbundle.nokri.RichEditor.Constants;
import com.scriptsbundle.nokri.RichEditor.Util;
import com.scriptsbundle.nokri.RichEditor.spans.AreUnderlineSpan;
import com.scriptsbundle.nokri.RichEditor.styles.IARE_Style;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.styles.ARE_Style_Underline;

/**
 * Created by wliu on 13/08/2018.
 */

public class ARE_ToolItem_Underline extends ARE_ToolItem_Abstract {

    @Override
    public IARE_ToolItem_Updater getToolItemUpdater() {
        if (mToolItemUpdater == null) {
            mToolItemUpdater = new ARE_ToolItem_UpdaterDefault(this, Constants.CHECKED_COLOR, Constants.UNCHECKED_COLOR);
            setToolItemUpdater(mToolItemUpdater);
        }
        return mToolItemUpdater;
    }

    @Override
    public IARE_Style getStyle() {
        if (mStyle == null) {
            AREditText editText = this.getEditText();
            IARE_ToolItem_Updater toolItemUpdater = getToolItemUpdater();
            mStyle = new ARE_Style_Underline(editText, (ImageView) mToolItemView, toolItemUpdater);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.underline);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        boolean underlinedExists = false;

        AREditText editText = this.getEditText();
        Editable editable = editText.getEditableText();
        if (selStart > 0 && selStart == selEnd) {
            CharacterStyle[] styleSpans = editable.getSpans(selStart - 1, selStart, CharacterStyle.class);

            for (int i = 0; i < styleSpans.length; i++) {
                if (styleSpans[i] instanceof AreUnderlineSpan) {
					underlinedExists = true;
				}
            }
        } else {
			//
			// Selection is a range
			CharacterStyle[] styleSpans = editable.getSpans(selStart, selEnd, CharacterStyle.class);

			for (int i = 0; i < styleSpans.length; i++) {

				if (styleSpans[i] instanceof AreUnderlineSpan) {
					if (editable.getSpanStart(styleSpans[i]) <= selStart
							&& editable.getSpanEnd(styleSpans[i]) >= selEnd) {
						underlinedExists = true;
					}
				}
			}
		}

        mToolItemUpdater.onCheckStatusUpdate(underlinedExists);
    }
}
