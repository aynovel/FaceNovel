package com.bytedance.club.adapter.person.readingmsg;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.club.sql.NoneViewHolder;
import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.entry.BeanParser;
import com.bytedance.club.entry.Comment;
import com.bytedance.club.internet.NetRequest;
import com.bytedance.club.activtiy.WorkCommentDetailActivity;
import com.bytedance.club.publics.BaseRecyclerViewFragment;
import com.bytedance.club.publics.net.OkHttpResult;
import com.bytedance.club.publics.tool.ComYou;
import com.bytedance.club.publics.tool.GlideUtil;
import com.bytedance.club.publics.tool.JSONUtil;
import com.bytedance.club.publics.weight.viewtext.TextViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageReplyMeFragment extends BaseRecyclerViewFragment {

    private int pageIndex = ONE;
    private int totalPage = ZERO;
    private final List<Comment> messages = new ArrayList<>();
    private ReplyMeAdapter replyMeAdapter;

    @Override
    protected void bindView() {
        super.bindView();
        mTitleBar.setVisibility(View.GONE);
        mRefreshLayout.setHasHeader(FALSE);
    }

    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        replyMeAdapter = new ReplyMeAdapter();
        mRecyclerView.setAdapter(replyMeAdapter);
        fetch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            messages.clear();
            replyMeAdapter.notifyDataSetChanged();
            reload();
        }
    }

    private class ReplyMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(getContext(), parent);
            }
            return new ReplyMeViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_message_reply_me, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText("还没有人回复您呢~");
                return;
            }
            ReplyMeViewHolder viewHolder = (ReplyMeViewHolder) holder;
            final Comment comment = messages.get(position);
            GlideUtil.load(context, comment.head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(comment.nickname);
            viewHolder.date.setText(ComYou.formatTime(comment.addtime));

            // 评论内容
            CharSequence result = TextViewUtil.replaceSpan(comment.content);
            viewHolder.content.setText(result);

            String from = String.format(Locale.getDefault(), "我的%s：%s", comment.fromType == ONE ? "评论" : "回复", comment.from);
            viewHolder.from.setText(from);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), WorkCommentDetailActivity.class);
                    intent.putExtra("wid", comment.wid);
                    intent.putExtra("id", comment.pid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (messages.size() == ZERO) {
                return ONE;
            }
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (messages.size() == ZERO) {
                return ZERO;
            } else {
                return ONE;
            }
        }
    }

    class ReplyMeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head)
        ImageView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.from)
        TextView from;

        ReplyMeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void fetch() {
        NetRequest.replyMeList(pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        int count = JSONUtil.getInt(result, "count");
                        if (pageIndex == ONE && totalPage == ZERO) {
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + ONE;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            messages.add(BeanParser.getComment(JSONUtil.getJSONObject(lists, i)));
                        }
                        replyMeAdapter.notifyDataSetChanged();
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void reload() {
        pageIndex = ONE;
        totalPage = ZERO;
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        fetch();
    }
}
