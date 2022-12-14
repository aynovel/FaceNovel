package com.bytedance.club.adapter.person.personcenter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.internet.NetRequest;
import com.bytedance.club.adapter.person.landing.LoginActivity;
import com.bytedance.club.adapter.person.personcenter.adapter.ProblemAdapter;
import com.bytedance.club.adapter.person.personcenter.bean.ProblemBean;
import com.bytedance.club.publics.BaseActivity;
import com.bytedance.club.publics.net.OkHttpResult;
import com.bytedance.club.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHelpActivity extends BaseActivity {

    @BindView(R.id.feed_back)
    TextView mFeedBack;
    @BindView(R.id.rcv_content)
    RecyclerView mRecyclerView;
    @BindView(R.id.noneView)
    View mNoneView;

    private Intent intent = new Intent();

    private ProblemAdapter mProblemAdapter;
    List<ProblemBean.ResultData.Data> mProblemBeanList = new ArrayList<>();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setMiddleText(getString(R.string.mine_help));
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_user_help);
        ButterKnife.bind(this);
        mFeedBack.setOnClickListener(onFeedbackClick);
    }

    @Override
    protected void initializeData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // ??????????????????
        mProblemAdapter = new ProblemAdapter(this, mProblemBeanList);
        mRecyclerView.setAdapter(mProblemAdapter);
        // ???????????????
        problemlist();
    }

    private final View.OnClickListener onFeedbackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                intent = new Intent();
                intent.setClass(context, FeedbackActivity.class);
            } else {
                intent.setClass(context, LoginActivity.class);
            }
            startActivity(intent);
        }
    };

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /**
     * ???????????????????????????0?????????????????????
     */
    private void switchPageBySize() {
        if (mProblemBeanList == null || mProblemBeanList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    /**
     * ??????Q&A????????????
     */
    private void problemlist() {
        NetRequest.problemlist(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(data));
                            String resultString = jsonObject.getString("ResultData");
                            JSONObject json = new JSONObject(resultString);
                            String strResult = json.getString("data");
                            JSONArray arr = new JSONArray(strResult);
                            for (int i = 0; i < arr.length(); i++) {
                                mProblemAdapter.add("position:" + i);
                            }
                            Gson gson = new Gson();
                            mProblemBeanList = gson.fromJson(strResult, new TypeToken<List<ProblemBean.ResultData.Data>>() {
                            }.getType());
                            mProblemAdapter.data(mProblemBeanList);
                            switchPageBySize();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        switchPageBySize();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later???");
            }
        });
    }

}
