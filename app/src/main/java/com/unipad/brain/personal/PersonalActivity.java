package com.unipad.brain.personal;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.personal.bean.UploadFileBean;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.brain.personal.view.ChatFunctionView;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.DateUtil;
import com.unipad.utils.FileUtil;
import com.unipad.utils.PicUtil;
import com.unipad.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Wbj on 2016/4/26.
 */
public class PersonalActivity extends BasicActivity implements IDataObserver {

    private TextView mTextSelected, mTextRight;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private PersonalInfoFragment mInfoFragment = new PersonalInfoFragment();
    private PersonalAuthenticationFragment mAuthenticationFragment;
    private PersonalRecordFragment mRecordFragment;
    private PersonalFavoriteFragment mFavoriteFragment;
    private PersonalMsgFragment mMsgFragment;
    private PersonalWalletFragment mWalletFragment;
    private PersonalSettingFragment mSettingFragment;
    private PersonalCommonFragment mCurrentFragment;
    private TextView txtName;
    private ImageView user_photo;
    private final int CROP_FLAG = 100;
    private PersonCenterService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        service.registerObserver(HttpConstant.UOLOAD_PHOTO_FILE, this);
        if (null != savedInstanceState) {
            filePath = savedInstanceState.getString("path");
            ToastUtil.createWaitingDlg(this, null, Constant.LOGIN_WAIT_DLG).show(15);
            if(!TextUtils.isEmpty(filePath))
                service.uploadAuthFile(filePath,1);
        }

        setContentView(R.layout.personal_aty);
    }

    @Override
    public void initData() {
        findViewById(R.id.title_bar_left_text).setOnClickListener(this);
        txtName = (TextView) findViewById(R.id.user_name);
        ((TextView) findViewById(R.id.txt_user_group_person)).setText("" + DateUtil.getMatchGroud(this));
        mTextRight = (TextView) findViewById(R.id.title_bar_right_text);
        mTextRight.setOnClickListener(this);
        user_photo = (ImageView) findViewById(R.id.user_photo);
        mFragmentManager = getFragmentManager();
        this.setTextViewSelected((TextView) findViewById(R.id.personal_info));
        TextView txtLevel = (TextView)findViewById(R.id.user_age_ads);
        txtLevel.setText(getString(R.string.person_level) + AppContext.instance().loginUser.getLevel());
        setTxtName();

        ImageOptions imageOptions =new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                .setRadius(DensityUtil.dip2px(360))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        // .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                        // .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                .build();
        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto()))
            x.image().bind(user_photo, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(), imageOptions);
        // 上传文件
    }

    public TextView getmTextRight(){
        return mTextRight;
    }

    public void setTxtName() {
        if (txtName != null)
            txtName.setText(AppContext.instance().loginUser.getUserName());
    }

    private View getView() {
        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(R.layout.history_answer, null);
        table.addView(createTableRow());

        return table;
    }

    private TableRow createTableRow() {
        TableRow table = (TableRow) LayoutInflater.from(this).inflate(R.layout.history_item, null);

        return table;
    }

    public void personalItemClick(View view) {
        TextView textCurrent = (TextView) view;
        if (mTextSelected == textCurrent) {
            return;
        }

        if (mTextSelected != null) {
            mTextSelected.setTextColor(getResources().getColor(R.color.black));
            mTextSelected.setSelected(false);
        }

        this.setTextViewSelected(textCurrent);
    }

    private void setTextViewSelected(TextView textCurrent) {
        if (textCurrent == null) {
            return;
        }

        textCurrent.setSelected(true);
        textCurrent.setTextColor(getResources().getColor(R.color.personal_item_selected));
        mTextSelected = textCurrent;

        this.switchFragment(textCurrent.getId());
    }

    private void switchFragment(int textViewId) {
        PersonalCommonFragment fragment = null;
        switch (textViewId) {
            case R.id.personal_info:
                if (mInfoFragment == null) {
                    mInfoFragment = new PersonalInfoFragment();
                }
                fragment = mInfoFragment;
                break;
            case R.id.personal_authentication:
                if (mAuthenticationFragment == null) {
                    mAuthenticationFragment = new PersonalAuthenticationFragment();
                }
                fragment = mAuthenticationFragment;
                break;
            case R.id.personal_record:
                if (mRecordFragment == null) {
                    mRecordFragment = new PersonalRecordFragment();
                }
                fragment = mRecordFragment;
                break;
            case R.id.personal_favorite:
                if (mFavoriteFragment == null) {
                    mFavoriteFragment = new PersonalFavoriteFragment();

                }
                fragment = mFavoriteFragment;
                break;
//            case R.id.personal_wallet:
//                if (mWalletFragment == null) {
//                    mWalletFragment = new PersonalWalletFragment();
//                }
//                fragment = mWalletFragment;
//                break;
            case R.id.personal_msg:
                if (mMsgFragment == null) {
                    mMsgFragment = new PersonalMsgFragment();
                }
                fragment = mMsgFragment;
                break;
            case R.id.personal_setting:
                if (mSettingFragment == null) {
                    mSettingFragment = new PersonalSettingFragment();
                }
                fragment = mSettingFragment;
                break;
            default:
                break;
        }

        if (fragment == null) {
            return;
        }

        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (mCurrentFragment != null) {
            mFragmentTransaction.hide(mCurrentFragment);
        }
        if (!fragment.isAdded()) {
            mFragmentTransaction.add(R.id.personal_frg_container, fragment);
        } else {
            mFragmentTransaction.show(fragment);
        }
        mFragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();

        this.setRightText(fragment.getTitleBarRightText());
        mCurrentFragment = fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_left_text:
                finish();
                break;
            case R.id.title_bar_right_text:
                if (mCurrentFragment != null) {
                    mCurrentFragment.clickTitleBarRightText();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置TitleBar的右侧按钮的显示文字
     *
     * @param text 要显示的文字，传空值表示没有
     */
    public void setRightText(String text) {
        mTextRight.setText(TextUtils.isEmpty(text) ? "" : text);
    }

    private ChatFunctionView chatFunctionView;

    private PopupWindow chatFunctionWindow;

    public void showScPic() {
        initChatFunctionView();
        chatFunctionWindow.showAtLocation(new Button(this), Gravity.CENTER, 0, 0);
    }

    /**
     * 初始化选择拍照view
     */
    private void initColleagueWindow() {
        if (null == chatFunctionView) {
            chatFunctionView = new ChatFunctionView(this);
            chatFunctionView.setPhotoPath(user_photo.getId()+"");
        }
    }

    /**
     * 初始化选择图片的view
     */
    private void initChatFunctionView() {
        if (null == chatFunctionWindow) {
            chatFunctionWindow = new PopupWindow(this);
            initColleagueWindow();
            chatFunctionWindow.setContentView(chatFunctionView);
            chatFunctionWindow.setFocusable(true);
//            chatFunctionWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            chatFunctionWindow.setWidth(400);
            chatFunctionWindow.setHeight(300);
            chatFunctionWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            chatFunctionWindow.setAnimationStyle(R.style.colleague_custom_animation);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 保存图片文件的本地路径
        switch (requestCode) {
            case ChatFunctionView.Camera_flag:
                if (resultCode != this.RESULT_OK) {
                    break;
                }
                if (FileUtil.hasSDCard()) {
                    filePath = chatFunctionView.getFileName();
                    if (null == filePath || "".equals(filePath)) {
                        ToastUtil.showToast(getString(R.string.util_getpicfile_failed));
                        return;
                    }
                    //File file = new File(path);
                    Bitmap picMap =  PicUtil.getImageThumbnail(filePath,86,86);
                    if(picMap != null){
                        mCurrentFragment.setImageBitmap(PicUtil.getRoundedCornerBitmap(picMap,360));

                        setHeadImgView();
                    }
//                    if (file.length() > 0 && (file.getParent() != null && !"".equals(file.getParent()))) {
//                        chatFunctionView.setFileName();
//                        startCrop(file);
//                    }
                } else {
                    ToastUtil.showToast(getString(R.string.util_getpicfile_failed));
                    // MyTools.showToast(this, getResources().getString());
                }
                break;
            case ChatFunctionView.Picture_flag:
                if (resultCode != RESULT_OK) {
                    break;
                }
                String imgFile = chatFunctionView.parseImageFile(data, this);
                if (TextUtils.isEmpty(imgFile)) {
                    return;
                }
                startCrop(new File(imgFile));//chatFunctionView.getFileByFileName(imgFile,getApplicationContext(), this, null);
                break;
            case CROP_FLAG:
                if (resultCode == this.RESULT_OK) {
                    //MyTools.showToast(GroupFileActivity.this, "裁剪成功");
                    if (null != data) {
                        Bitmap bmap = data.getParcelableExtra("data");
                        if (null != bmap) {
                            // imgHeadView.setImageBitmap(bmap);
                            File mainFile = chatFunctionView.getMainPhotoFile();
                            if (null == mainFile)
                                return;

                            File saveFile = new File(mainFile, System.currentTimeMillis() + ".jpg");

                            FileOutputStream output = null;
                            try {
                                output = new FileOutputStream(saveFile);
                                bmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                                if (saveFile != null && saveFile.exists()) {
                                    filePath = saveFile.getPath();
                                    setHeadImgView();
                                } else {
                                    ToastUtil.showToast("文件不存在");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                if (null != output)
                                    try {
                                        output.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                            }
                        }
                    }
                } else {
                    ToastUtil.showToast(getString(R.string.tailor_fail));
                }
                break;
            default:
                break;
        }
    }


    private String filePath;   // 开始裁剪之前 保存图片的路径以防数据的丢失

    /**
     * 开始裁剪
     *
     * @param file
     */
    private void startCrop(File file) {
        filePath = file.getAbsolutePath();
        Intent cropIntent = new Intent();
        Uri uri = Uri.fromFile(file);
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.setAction("com.android.camera.action.CROP");
        cropIntent.putExtra("crop", true);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 150);
        cropIntent.putExtra("outputY", 150);
        cropIntent.putExtra("return-data", true);
        this.startActivityForResult(cropIntent, CROP_FLAG);
    }

    /**
     * 设置头像
     *
     * @param
     */
    public void setHeadImgView() {
        if (user_photo != null) {
            if (null != chatFunctionWindow && chatFunctionWindow.isShowing()) {
                chatFunctionWindow.dismiss();
            }
           // ToastUtil.createWaitingDlg(this, null, Constant.LOGIN_WAIT_DLG).show(15);
            service.uploadAuthFile(filePath,1);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("path", filePath);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            filePath = savedInstanceState.getString("path");
            setHeadImgView();
        }
        // Log.e(this.getClass().getSimpleName(),"onRestoreInstanceState" + filePath);
    }

    @Override
    public void update(int key, Object o) {
        // 上传头像
        switch (key) {
            case HttpConstant.UOLOAD_PHOTO_FILE:
                //HIDDialog.dismissAll();
                UploadFileBean uploadFileBean = (UploadFileBean) o;
                if (uploadFileBean.getRet_code() != 0)
                    return;
                AppContext.instance().loginUser.setPhoto(uploadFileBean.getPath());
                ImageOptions imageOptions =new ImageOptions.Builder()
                        //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                        .setRadius(DensityUtil.dip2px(360))//ImageView圆角半径
                        .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                       // .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                       // .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                        .build();
                x.image().bind(user_photo, HttpConstant.PATH_FILE_URL + uploadFileBean.getPath(), imageOptions,new Callback.CommonCallback<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap map = PicUtil.drawableToBitmap(drawable);
                      //  user_photo.setImageBitmap(PicUtil.getRoundedCornerBitmap(map, 360));
                        mCurrentFragment.setImageBitmap(map);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {

                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
                break;
        }
    }

}
