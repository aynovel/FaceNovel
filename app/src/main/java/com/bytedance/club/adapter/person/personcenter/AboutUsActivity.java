package com.bytedance.club.adapter.person.personcenter;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.entry.AppUser;
import com.bytedance.club.bookcase.ShelfUtil;
import com.bytedance.club.adapter.person.landing.PrivatePolicyActivity;
import com.bytedance.club.adapter.person.landing.ServiceTermsActivity;
import com.bytedance.club.publics.BaseActivity;
import com.bytedance.club.publics.tool.AndroidManifestUtil;
import com.bytedance.club.publics.tool.LOG;
import com.bytedance.club.publics.tool.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.version)
    TextView mVersion;
    @BindView(R.id.debug)
    ImageView debug;
    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MINE_STRING_SETTING_ABOUT_US);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        mVersion.setText(String.format(Locale.getDefault(), getString(R.string.version), AndroidManifestUtil.getVersionName()));
        debug.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (PlotRead.isTest) {
                    PlotRead.isTest = FALSE;
                    Toast.makeText(AboutUsActivity.this, "release", Toast.LENGTH_SHORT).show();
                    LOG.i(getClass().getSimpleName(), "debug???????????????");
                } else {
                    PlotRead.isTest = TRUE;
                    Toast.makeText(AboutUsActivity.this, "debug", Toast.LENGTH_SHORT).show();

                    LOG.i(getClass().getSimpleName(), "debug???????????????");
                }
                onLogOutClick();
                return true;
            }
        });
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };


    @OnClick(R.id.TermsServiceItem)
    void onServiceTermsClick() {
        Intent intent = new Intent(getBaseContext(), ServiceTermsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.PrivacyPolicyItem)
    void onPrivatePolicyClick() {
        Intent intent = new Intent(getBaseContext(), PrivatePolicyActivity.class);
        startActivity(intent);
    }


    /**
     * ????????????
     */
    void onLogOutClick() {
        showLoading(getString(R.string.loading_off));
        // ????????????
        ShelfUtil.shelfUpload(AboutUsActivity.this);
        AppUser user = PlotRead.getAppUser();
        user.nickName = getString(R.string.tourists) + user.uid;
        user.head = BLANK;
        user.sex = ZERO;
        user.level = ZERO;
        user.vip = ZERO;
//        mFirebaseAnalytics.setUserProperty("favorite_food", "0");
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
        SharedPreferencesUtil.putInt(USER + uid, KEY_TOKEN_TIME, SharedPreferencesUtil.getInt(USER + uid,KEY_TOKEN_TIME)-1);
        // ??????????????????
        SharedPreferencesUtil.putInt(APP, com.bytedance.club.publics.Constant.LAST_LOGIN_WAY, ZERO);
        // ??????????????????token
        PlotRead.getAppUser().notifyWhenLogin();

        dismissLoading();
    }
}
