package com.bytedance.club.adapter.person.personcenter;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.internet.NetRequest;
import com.bytedance.club.adapter.person.personcenter.adapter.ReportPhotoAdapter;
import com.bytedance.club.publics.BaseActivity;
import com.bytedance.club.publics.net.OkHttpResult;
import com.bytedance.club.publics.tool.CustomToast;
import com.bytedance.club.publics.tool.DisplayUtil;
import com.bytedance.club.publics.tool.GenderDialog;
import com.bytedance.club.publics.tool.JSONUtil;
import com.bytedance.club.publics.tool.OssUtil;
import com.bytedance.club.publics.tool.ReportDialog;
import com.bytedance.club.publics.weight.SpaceItemDecoration;
import com.bytedance.club.adapter.AppendableAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReportActivity extends BaseActivity {


    @BindView(R.id.et_content)
    EditText mEditText;
    @BindView(R.id.id_font_count)
    TextView mWordCount;
    @BindView(R.id.feedback_send)
    TextView feedback_send;
    @BindView(R.id.photoRecyclerview)
    RecyclerView mPhotoRecyclerview;
    @BindView(R.id.tvPhotoNum)
    TextView mTvPhotoNum;
    //??????
    @BindView(R.id.tvType)
    TextView mTvType;


    int wid, cid;
    private ReportDialog mReportDialog;
    private int mSelectPosition = -1;
    //1 ???????????? 2 ????????????
    private int type = 1;

    private final int REQUEST_TAKE_PHOTO = ONE_THOUSAND;
    private final int REQUEST_GALLERY = TWO_THOUSAND;
    private final int REQUEST_CROP = THREE_THOUSAND;

    private Uri photoUri;
    private File zoom;
    private Uri zoomUri;
    private GenderDialog mGenderDialog;
    //????????????????????????
    private ArrayList<String> mALiUrlList;
    //??????
    private ArrayList<String> mTypeList;

    //????????????
    private ReportPhotoAdapter mPhotoAdapter;
    private final String TAG = "ReportActivity";

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setOnClickListener(onBackClick);
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setMiddleText(getResources().getString(R.string.chapter_feedback));
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_chapter_feedback);
        ButterKnife.bind(this);
        mEditText.addTextChangedListener(textWatcher);
        wid = getIntent().getIntExtra("wid", wid);
        cid = getIntent().getIntExtra("cid", cid);
        type = getIntent().getIntExtra("type", type);
        feedback_send.setClickable(false);
        mPhotoRecyclerview.setLayoutManager(new GridLayoutManager(context, 3));
        mPhotoRecyclerview.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dp2px(context, 10)));
        mPhotoAdapter = new ReportPhotoAdapter(context);
        mPhotoRecyclerview.setAdapter(mPhotoAdapter);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };


    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String mString = s.toString().trim();
            int count = mString.length();

            if (count > 9) {
                feedback_send.setTextColor(getResources().getColor(R.color.colorWhite));
                feedback_send.setBackgroundResource(R.drawable.shape_no_internet_reload);
                feedback_send.setClickable(true);
            } else {
                feedback_send.setTextColor(getResources().getColor(R.color.color_8C8D8E));
                feedback_send.setBackgroundResource(R.drawable.shape_no_internet_reload_gray_18);
                feedback_send.setClickable(false);
            }
            mWordCount.setText(String.format(Locale.getDefault(), "%d/1000", count));

        }
    };

    @Override
    protected void initializeData() {
        mTypeList = new ArrayList<>();
        mALiUrlList = new ArrayList<>();
        initEvent();
        getReportType();

    }
//    @OnClick(R.id.send_sms)
//     public void sendSms() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SMS_SEND);
//            } else {
//                setJurisdiction();
//            }
//        } else {
//            setJurisdiction();
//        }
//
//    }

//    /**
//     * ????????????
//     */
//    public void setJurisdiction() {
//        Intent intent = new Intent();                        //?????? Intent ??????
//        intent.setAction(Intent.ACTION_SENDTO);             //???????????????????????????
//        intent.setData(Uri.parse("smsto:" +"0396596313"));          //?????????????????????
//        FeedbackActivity.this.startActivity(intent);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_SMS_SEND) {
//            if (grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
//                setJurisdiction();
//            } else {
//                PlotRead.toast(PlotRead.NORMAL, getString(R.string.refused_sms));
//            }
//        }
//    }

    /**
     * ???????????????dialog??????
     */
    private void getReportType() {

        NetRequest.getReportType(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject resultObject = JSONUtil.getJSONObject(data, "ResultData");
                        int status = JSONUtil.getInt(resultObject, "status");
                        if (status == 1) {
                            String listString = JSONUtil.getString(resultObject, "list");
                            Type type = new TypeToken<List<String>>() {
                            }.getType();
                            ArrayList<String> typeList = new Gson().fromJson(listString, type);
                            mTypeList.clear();
                            mTypeList.addAll(typeList);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * ??????????????????
     */
    private void initEvent() {

        mPhotoAdapter.setOnItemClickLitener(new AppendableAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //????????????????????????
                switch (view.getId()) {
                    case R.id.imgAdd:
                        infodialog("photo");
                        break;

                    case R.id.imgDelete:
                        if (mPhotoAdapter.getDataItems().size() == 4) {
                            mPhotoAdapter.getDataItems().remove(position);
                            mPhotoAdapter.getDataItems().add("");
                        } else {
                            mPhotoAdapter.getDataItems().remove(position);
                        }
                        mALiUrlList.remove(position);
                        mPhotoAdapter.notifyDataSetChanged();
                        mTvPhotoNum.setText(mPhotoAdapter.getLimitText());
                        break;
                }
            }
        });
        mPhotoAdapter.getDataItems().add("");
        mPhotoAdapter.notifyDataSetChanged();


        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                   view.getParent().requestDisallowInterceptTouchEvent(true);
               }
               if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                   view.getParent().requestDisallowInterceptTouchEvent(true);
               }
               if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                   view.getParent().requestDisallowInterceptTouchEvent(false);
               }
                return false;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (null != grantResults && grantResults.length > 0 && grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                CustomToast.showToast(this, getString(R.string.refused_camera));
//                PlotRead.toast(PlotRead.INFO, getString(R.string.refused_camera));
            }
        }
    }

    /*
     * ????????????
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(getPictureCacheDir(), System.currentTimeMillis() + ".jpg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) { // 7.0????????????FileProvider
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photo);
        } else {
            photoUri = Uri.fromFile(photo);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //??????????????????????????????
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                getImg(photoUri);
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                photoUri = data.getData();
                getImg(photoUri);
            }
        }
    }

    /*
     * ??????
     */
    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }


    /*
     * ????????????
     */
    @SuppressLint("IntentReset")
    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }


    /*
     * ????????????????????????
     * @return
     */
    private File getPictureCacheDir() {
        File externalCacheDir = getExternalCacheDir();
        if (externalCacheDir == null) {
            externalCacheDir = getCacheDir();
        }
        return externalCacheDir;
    }

    /**
     * ??????????????????dialog
     *
     * @param type
     */
    private void infodialog(String type) {
        mGenderDialog = new GenderDialog(this, type, R.style.CustomDialog, view -> {
            switch (view.getId()) {
                case R.id.tv_male:
                    mGenderDialog.dismiss();
                    takePhoto();
                    break;
                case R.id.tv_female:
                    mGenderDialog.dismiss();
                    chooseFromGallery();
                    break;
                case R.id.tv_cancel:
                    mGenderDialog.dismiss();
                    break;
            }
        });

        mGenderDialog.show();
        Window dialogWindow = mGenderDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = mGenderDialog.getWindow().getAttributes();
            //??????dialog???????????????????????????,????????????????????????!!
            mGenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mGenderDialog.getWindow().setAttributes(params);
        }

    }

    /*
     * ????????????????????????????????????
     * @return
     */
    private String getHeadOssUrl() {
        int uid = PlotRead.getAppUser().uid;
        int mole = uid % TEN;
        Random ran = new Random(System.currentTimeMillis());
        int i = ran.nextInt(ONE_THOUSAND);
        String path = "upload/logo/";
        path += mole + "/logo_" + uid + "_" + i + ".jpg";
        return path;
    }

    /*
     * ???????????????
     */
    private final OssUtil.OssCallback ossCallback = new OssUtil.OssCallback() {

        @Override
        public void onSuccess(String url) {
            mALiUrlList.add(url);
            dismissLoading();
            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.upload_success));
            // ????????????

        }

        @Override
        public void onFailure() {
            dismissLoading();
            PlotRead.toast(PlotRead.FAIL, getString(R.string.upload_fail));
        }
    };


    /**
     * ??????????????????
     */
    private void uploadReportInfo() {
        int uid = PlotRead.getAppUser().uid;
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.please_enter_feedback));
            return;
        }
        //????????????
        String typeString = mTvType.getText().toString().trim();
        if (typeString.equals(getString(R.string.report_type_hint))) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.report_type_hint));
            return;
        }

        showLoading(getString(R.string.is_submitted));
        NetRequest.uploadReportInfo(uid, "", content, typeString, mALiUrlList,
                2, 2, wid, cid,
                new OkHttpResult() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        dismissLoading();
                        String serverNo = JSONUtil.getString(data, "ServerNo");
                        if (SN000.equals(serverNo)) {
                            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.feedback_success));
                            onBackPressed();
                        } else {
                            NetRequest.error(ReportActivity.this, serverNo);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        dismissLoading();
                        PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                    }
                });

    }

    @OnClick({R.id.llType, R.id.feedback_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llType:
                showTypeDialog();
                break;

            case R.id.feedback_send:
                uploadReportInfo();
                break;
        }
    }

    /**
     * ????????????
     */
    private void showTypeDialog() {
        if (mTypeList.size() > 0) {
            mReportDialog = new ReportDialog(context, mTypeList, mSelectPosition);
            mReportDialog.setOnSelectListener(new ReportDialog.OnSelectListener() {
                @Override
                public void onSelect(int position, String name) {
                    mSelectPosition = position;
                    mTvType.setText(name);
                    mTvType.setTextColor(getResources().getColor(R.color.color_000001));
                }
            });
            mReportDialog.show();
        }
    }

    /**
     * uir?????????bitmap
     *
     * @param uri
     */
    private void getImg(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            compressBitmap(bitmap);
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ???????????? ????????????
     *
     * @param bitmap
     */
    private void compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        Log.e(TAG, "compressBitmap??? ???????????? : " + baos.toByteArray().length + " byte");

        int options = 90;
        while (baos.toByteArray().length > 2 * 1024 * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options = options - 10;
        }

        Log.e(TAG, "compressBitmap??? ???????????? : " + baos.toByteArray().length + " byte");

        File file = new File(getPictureCacheDir(), System.currentTimeMillis() + ".jpg");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            baos.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

            showLoading(getString(R.string.photo_uploading));
            OssUtil.with(this).post(file.getAbsolutePath(), getHeadOssUrl(), ossCallback);
            if (mPhotoAdapter.getDataItems().size() == 4) {
                mPhotoAdapter.getDataItems().add(mPhotoAdapter.getDataItems().size() - 1, file.getAbsolutePath());
                mPhotoAdapter.getDataItems().remove("");
                mPhotoAdapter.notifyDataSetChanged();
                mTvPhotoNum.setText(mPhotoAdapter.getLimitText());
            } else {
                mPhotoAdapter.getDataItems().add(mPhotoAdapter.getDataItems().size() - 1, file.getAbsolutePath());
                mPhotoAdapter.notifyDataSetChanged();
                mTvPhotoNum.setText(mPhotoAdapter.getLimitText());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
//        Bitmap bitmap1 = BitmapFactory.decodeStream(byteArrayInputStream, null, null);
    }

}
