package com.scriptsbundle.nokri.FilePicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scriptsbundle.nokri.FilePicker.adapter.PathAdapter;
import com.scriptsbundle.nokri.FilePicker.filter.LFileFilter;
import com.scriptsbundle.nokri.FilePicker.model.ParamEntity;
import com.scriptsbundle.nokri.FilePicker.utils.FileUtils;
import com.scriptsbundle.nokri.FilePicker.utils.StringUtils;
import com.scriptsbundle.nokri.FilePicker.widget.EmptyRecyclerView;
import com.scriptsbundle.nokri.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LFilePickerActivity extends AppCompatActivity {

    private final String TAG = "FilePickerLeon";
    private EmptyRecyclerView mRecylerView;
    private View mEmptyView;
    private TextView mTvPath;
    private TextView mTvBack;
    private Button mBtnAddBook;
    private String mPath;
    private List<File> mListFiles;
    private ArrayList<String> mListNumbers = new ArrayList<String>();
    private PathAdapter mPathAdapter;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private boolean mIsAllSelected = false;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mParamEntity = (ParamEntity) getIntent().getExtras().getSerializable("param");
        setTheme(mParamEntity.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);
        initView();
        initToolbar();
        updateAddButton();
        if (!checkSDState()) {
            Toast.makeText(this, "No SD Card Available", Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = mParamEntity.getPath();
        if (StringUtils.isEmpty(mPath)) {
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mTvPath.setText(mPath);
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter = new PathAdapter(mListFiles, this, mFilter, mParamEntity.isMutilyMode(), mParamEntity.isGreater(), mParamEntity.getFileSize());
        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPathAdapter.setmIconStyle(mParamEntity.getIconStyle());
        mRecylerView.setAdapter(mPathAdapter);
        mRecylerView.setmEmptyView(mEmptyView);
        initListener();
    }

    private void initToolbar() {
//        if (mParamEntity.getTitle() != null) {
//            mToolbar.setTitle(mParamEntity.getTitle());
//        }
//        if (mParamEntity.getTitleStyle() != 0) {
//            mToolbar.setTitleTextAppearance(this, mParamEntity.getTitleStyle());
//        }
//        if (mParamEntity.getTitleColor() != null) {
//            mToolbar.setTitleTextColor(Color.parseColor(mParamEntity.getTitleColor()));
//        }
//        if (mParamEntity.getBackgroundColor() != null) {
//            mToolbar.setBackgroundColor(Color.parseColor(mParamEntity.getBackgroundColor()));
//        }
//        switch (mParamEntity.getBackIcon()) {
//            case Constant.BACKICON_STYLEONE:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back1);
//                break;
//            case Constant.BACKICON_STYLETWO:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back2);
//                break;
//            case Constant.BACKICON_STYLETHREE:
//                //默认风格
//                break;
//        }
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    private void updateAddButton() {
        if (!mParamEntity.isMutilyMode()) {
            mBtnAddBook.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseMode()) {
            mBtnAddBook.setVisibility(View.VISIBLE);
            mBtnAddBook.setText(getString(R.string.lfile_OK));
            mParamEntity.setMutilyMode(false);
        }
    }

    /**
     * 添加点击事件处理
     */
    private void initListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null) {
                    return;
                }
                if (!tempPath.equals("/storage/emulated")){
                    mPath = tempPath;
                    mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
                    mPathAdapter.setmListData(mListFiles);
                    mPathAdapter.updateAllSelelcted(false);
                    mIsAllSelected = false;
                    mBtnAddBook.setText(getString(R.string.lfile_Selected));
                    mRecylerView.scrollToPosition(0);
                    setShowPath(mPath);
                    mListNumbers.clear();
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText());
                    } else {
                        mBtnAddBook.setText(R.string.lfile_Selected);
                    }
                }

            }
        });
        mPathAdapter.setOnItemClickListener(new PathAdapter.OnItemClickListener() {
            @Override
            public void click(int position) {
                if (mParamEntity.isMutilyMode()) {
                    if (mListFiles.get(position).isDirectory()) {
                        //如果当前是目录，则进入继续查看目录
                        chekInDirectory(position);
                        mPathAdapter.updateAllSelelcted(false);
                        mIsAllSelected = false;
                        mBtnAddBook.setText(getString(R.string.lfile_Selected));
                    } else {
                        //如果已经选择则取消，否则添加进来
                        if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
                            mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
                        } else {
                            mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        }
                        if (mParamEntity.getAddText() != null) {
                            mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                        } else {
                            mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                        }
                        if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                            Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    //单选模式直接返回
                    if (mListFiles.get(position).isDirectory()) {
                        chekInDirectory(position);
                        return;
                    }
                    if (mParamEntity.isChooseMode()) {
                        //选择文件模式,需要添加文件路径，否则为文件夹模式，直接返回当前路径
                        mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        chooseDone();
                    } else {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_ChooseTip, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mBtnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParamEntity.isChooseMode() && mListNumbers.size() < 1) {
                    String info = mParamEntity.getNotFoundFiles();
                    if (TextUtils.isEmpty(info)) {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_NotFoundBooks, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LFilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //返回
                    chooseDone();
                }
            }
        });
    }


    /**
     * 点击进入目录
     *
     * @param position
     */
    private void chekInDirectory(int position) {
        mPath = mListFiles.get(position).getAbsolutePath();
        setShowPath(mPath);
        //更新数据源
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.notifyDataSetChanged();
        mRecylerView.scrollToPosition(0);
    }

    /**
     * 完成提交
     */
    private void chooseDone() {
        //判断是否数量符合要求
        if (mParamEntity.isChooseMode()) {
            if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("paths", mListNumbers);
        intent.putExtra("path", mTvPath.getText().toString().trim());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecylerView = (EmptyRecyclerView) findViewById(R.id.recylerview);
        mTvPath = (TextView) findViewById(R.id.tv_path);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mBtnAddBook = (Button) findViewById(R.id.btn_addbook);
        mEmptyView = findViewById(R.id.empty_view);
        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        }
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 显示顶部地址
     *
     * @param path
     */
    private void setShowPath(String path) {
        mTvPath.setText(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        this.mMenu = menu;
        updateOptionsMenu(menu);
        return true;
    }

    /**
     * 更新选项菜单展示，如果是单选模式，不显示全选操作
     *
     * @param menu
     */
    private void updateOptionsMenu(Menu menu) {
        mMenu.findItem(R.id.action_selecteall_cancel).setVisible(mParamEntity.isMutilyMode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            mPathAdapter.updateAllSelelcted(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
            if (mIsAllSelected) {
                for (File mListFile : mListFiles) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mListNumbers.contains(mListFile.getAbsolutePath())) {
                        mListNumbers.add(mListFile.getAbsolutePath());
                    }
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                    }
                }
            } else {
                mListNumbers.clear();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
            }
        }
        return true;
    }

    /**
     * 更新选项菜单文字
     */


}
