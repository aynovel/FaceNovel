package com.bytedance.club.tab.buildins.commonnavigator.titles;

import android.content.Context;

import com.bytedance.club.tab.buildins.ArgbEvaluatorHolder;


public class ColorTransitionPagerTitleView extends SimplePagerTitleView {

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        setTextColor(color);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }
}
