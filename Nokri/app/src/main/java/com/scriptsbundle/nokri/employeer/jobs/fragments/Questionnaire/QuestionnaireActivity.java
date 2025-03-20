package com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.LruCache;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_PostJobFragment;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.models.Nokri_ProgressModel;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

public class QuestionnaireActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnClickListener {
    Toolbar toolbar;
    TextView done;
    TextView add;
    RecyclerView recyclerView;
    MyListAdapter adapter;
    LinearLayout mainQuestionnaireLayout;
    Nokri_FontManager fontManager = new Nokri_FontManager();
    Nokri_SettingsModel settings;
    Nokri_ProgressModel progressModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        settings = Nokri_SharedPrefManager.getSettings(this);
        progressModel = Nokri_SharedPrefManager.getProgressSettings(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString(Nokri_PostJobFragment.questionnaireModel.heading);
        s.setSpan(new TypefaceSpan(this,"OpenSans.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        mainQuestionnaireLayout = findViewById(R.id.main_questionnaire_layout);

        done = findViewById(R.id.done);
        done.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        Nokri_Utils.setRoundButtonColor(this,done);

        done.setOnClickListener(this);
        fontManager.nokri_setOpenSenseFontTextView(done,getAssets());

        add = findViewById(R.id.add);
        fontManager.nokri_setOpenSenseFontTextView(add,getAssets());
        Nokri_Utils.setRoundButtonColorCustom(this,add,"#39db6a");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showCustomDialog(Nokri_PostJobFragment.questionnaireModel.questionsLabel,
                       Nokri_PostJobFragment.questionnaireModel.buttonYes,
                       Nokri_PostJobFragment.questionnaireModel.buttonNo
                       ,Nokri_PostJobFragment.questionnaireModel.questionsPlaceholder);
            }
        });


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyListAdapter(this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


    }


    private void showCustomDialog(String title,String buttonOneText,String buttonTwoText,String hint){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.questionnaire_dialog);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        EditText editText = dialog.findViewById(R.id.edittext);

        editText.setHint(hint);
        editText.setPadding(10,0,0,0);
        deleteJobTextView.setText(title);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        confirmButton.setText(buttonOneText);
        fontManager.nokri_setOpenSenseFontButton(confirmButton,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(editText,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(deleteJobTextView,getAssets());

        final Button closeButton = dialog.findViewById(R.id.btn_close);
        fontManager.nokri_setOpenSenseFontTextView(closeButton,getAssets());
        closeButton.setText(buttonTwoText);
//        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView,context.getAssets());
//        fontManager.nokri_setOpenSenseFontButton(closeButton,context.getAssets());
//        fontManager.nokri_setOpenSenseFontButton(confirmButton,context.getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        confirmButton.setOnClickListener(new CustomListener(dialog));

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MyListAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = Nokri_PostJobFragment.questions.get(position);

            // backup of removed item for undo purpose
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mainQuestionnaireLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done){
            finish();
        }
    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            EditText editText = dialog.findViewById(R.id.edittext);
            if (!editText.getText().toString().equals("")){
                Nokri_PostJobFragment.questions.add(editText.getText().toString());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }else{
                editText.setError("");
                editText.requestFocus();
            }
            // Do whatever you want here

            // If you want to close the dialog, uncomment the line below
            //dialog.dismiss();
        }
    }

    static class TypefaceSpan extends MetricAffectingSpan {
        /** An <code>LruCache</code> for previously loaded typefaces. */
        private static LruCache<String, Typeface> sTypefaceCache =
                new LruCache<String, Typeface>(12);

        private Typeface mTypeface;

        public TypefaceSpan(Context context, String typefaceName) {
            mTypeface = sTypefaceCache.get(typefaceName);

            if (mTypeface == null) {
                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                        .getAssets(), typefaceName);

                // Cache the loaded Typeface
                sTypefaceCache.put(typefaceName, mTypeface);
            }
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }
}
