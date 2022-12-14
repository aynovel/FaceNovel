package com.bytedance.club.adapter.person.personcenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.entry.AppUser;
import com.bytedance.club.internet.NetRequest;
import com.bytedance.club.bookcase.ShelfUtil;
import com.bytedance.club.publics.BaseActivity;
import com.bytedance.club.publics.Constant;
import com.bytedance.club.publics.net.OkHttpResult;
import com.bytedance.club.datautils.GlideCacheUtil;
import com.bytedance.club.publics.tool.JSONUtil;
import com.bytedance.club.publics.tool.SharedPreferencesUtil;
import com.bytedance.club.publics.weight.KOL;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingActivity extends BaseActivity {

    @BindView(R.id.logOut)
    TextView mLogOut;
    @BindView(R.id.clearMemoryItem)
    KOL mClearMemory;
    @BindView(R.id.v_reddot)
    View mVReddot;


    Intent intent = new Intent();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(aiye_STRING_SETTING);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        getVersionParam();
//        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
//            mLogOut.setVisibility(View.VISIBLE);
//        } else {
//            mLogOut.setVisibility(View.GONE);
//        }
        String size = getFormatSize(getCache());
        mClearMemory.setTip(size);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case BUS_LOG_IN:
                break;
            case BUS_LOG_OUT:
                PlotRead.toast(PlotRead.SUCCESS, "log out");
                break;
        }
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();

    //????????????
    @OnClick(R.id.pushManageItem)
    void onPushManageItemClick() {
        intent.setClass(context, ManagerActivity.class);
        startActivity(intent);
    }

    //????????????
    @OnClick(R.id.clearMemoryItem)
    void onClearMemoryItemClick() {
        if (getCache() == ZERO) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.cleaned_up));
        } else {
            new ClearMemoryTask().execute();
            GlideCacheUtil.getInstance().clearImageAllCache(SettingActivity.this);
        }
    }

    //??????
    @OnClick(R.id.rateUsItem)
    void onRateUsItemClick() {
        mVReddot.setVisibility(View.GONE);
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
    }

    @OnClick(R.id.logOut)
    void onLogOutClick() {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            showLoading(getString(R.string.loading_off));
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(2000);//??????3???
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // ????????????
                    ShelfUtil.shelfUpload(SettingActivity.this);
                    // ??????????????????
//        mLogOut.clearAnimation();
//        mLogOut.setVisibility(View.GONE);
                    AppUser user = PlotRead.getAppUser();
                    user.nickName = getString(R.string.tourists) + user.uid;
                    user.head = BLANK;
                    user.sex = ZERO;
                    user.level = ZERO;
                    user.vip = ZERO;
                    // ????????????
                    SharedPreferencesUtil.putBoolean(user.config, KEY_IS_VISITOR, TRUE);
                    // ????????????????????????
                    user.notifyWhenLogin();
                    // ??????????????????????????????
                    Message msg = Message.obtain();
                    msg.what = BUS_LOG_OUT;
                    EventBus.getDefault().post(msg);
                    int uid = 0;
                    SharedPreferencesUtil.putInt(APP, LAST_ID, uid);
                    SharedPreferencesUtil.putInt(USER + uid, KEY_TOKEN_TIME, SharedPreferencesUtil.getInt(USER + uid, KEY_TOKEN_TIME) - 1);
                    // ??????????????????
                    SharedPreferencesUtil.putInt(APP, Constant.LAST_LOGIN_WAY, ZERO);
                    // ??????????????????token
                    PlotRead.getAppUser().notifyWhenLogin();
                    dismissLoading();
                }
            }.start();
        }
    }

    private File getAppCacheDir() {
        File externalCacheDir = getExternalCacheDir();
        if (externalCacheDir == null) {
            externalCacheDir = getCacheDir();
        }
        return externalCacheDir;
    }

    private long getCache() {
        return getFolderCache(getAppCacheDir());
    }

    private long getFolderCache(File file) {
        File[] files = file.listFiles();
        long size = ZERO;
        for (File f : files) {
            if (f.isFile()) {
                size += f.length();
            } else {
                size += getFolderCache(f);
            }
        }
        return size;
    }

    private String getFormatSize(long size) {
        long KB = size / 1024;
        if (KB < ONE) {
            return size + " byte";
        }
        long MB = KB / 1024;
        if (MB < ONE) {
            return new BigDecimal(String.valueOf(KB)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " K";
        }
        long GB = MB / 1024;
        if (GB < ONE) {
            return new BigDecimal(String.valueOf(MB)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " M";
        }
        long TB = GB / 1024;
        if (TB < ONE) {
            return new BigDecimal(String.valueOf(GB)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " G";
        }
        return new BigDecimal(String.valueOf(TB)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " T";
    }

    @SuppressLint("StaticFieldLeak")
    private class ClearMemoryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLoading(getString(R.string.cleaning));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            clearMemory();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissLoading();
            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.clean_success));
            String size = getFormatSize(getCache());
            mClearMemory.setTip(size);
        }
    }

    private void clearMemory() {
        File file = getAppCacheDir();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            } else {
                deleteFolder(f);
            }
        }
    }

    private void deleteFolder(File file) {
        File[] files = file.listFiles();
        if (files.length == ZERO) {
            file.delete();
        } else {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                }
                f.delete();
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void getVersionParam() {
        NetRequest.versionParam(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        String version = JSONUtil.getString(result, "version_code");
                        String versioncode = version.replace(".", "");
                        int code = Integer.parseInt(versioncode);
                        if (Constant.getAppVersionCode(SettingActivity.this) < code) {
                            mVReddot.setVisibility(View.VISIBLE);
                        } else {
                            mVReddot.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
