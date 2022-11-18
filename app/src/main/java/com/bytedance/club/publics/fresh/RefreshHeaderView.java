package com.bytedance.club.publics.fresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.club.R;
import com.bytedance.club.publics.fresh.weight.BaseHeaderView;
import com.bytedance.club.publics.tool.GlideUtil;

public class RefreshHeaderView extends BaseHeaderView {

    ImageView loadingView;
    TextView textView;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_refresh_header, this, true);
        loadingView = findViewById(R.id.loading);
        textView = findViewById(R.id.textView);
        GlideUtil.load(context, R.drawable.bload, 0, loadingView);
    }

    @Override
    public float getSpanHeight() {
        return getHeight();
    }

    @Override
    protected void onStateChange(int state) {
        switch (state) {
            case NONE:
                break;
            case PULLING:
                textView.setText(R.string.pull_to_refresh);
                break;
            case LOOSENT_O_REFRESH:
                textView.setText(R.string.loosen_to_refresh);
                break;
            case REFRESHING:
                textView.setText(R.string.loading_refresh);
                break;
            case REFRESH_CLONE:
                textView.setText(R.string.refresh_complete);
                break;
        }
    }
}
