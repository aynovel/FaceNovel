package com.bytedance.club.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.club.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DatePicker extends LinearLayout implements YearPicker.OnYearSelectedListener,
        MonthPicker.OnMonthSelectedListener, DayPicker.OnDaySelectedListener {

    private YearPicker mYearPicker;
    private MonthPicker mMonthPicker;
    private DayPicker mDayPicker;


    private OnDateSelectedListener mOnDateSelectedListener;

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     */
    public DatePicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_date, this);
        initChild();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker);
        int textSize = a.getDimensionPixelSize(R.styleable.DatePicker_itemTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        int textColor = a.getColor(R.styleable.DatePicker_itemTextColor, 0xFF5a5a5a);
        boolean isTextGradual = a.getBoolean(R.styleable.DatePicker_textGradual, true);
        boolean isCyclic = a.getBoolean(R.styleable.DatePicker_wheelCyclic, false);
        int halfVisibleItemCount = a.getInteger(R.styleable.DatePicker_halfVisibleItemCount, 2);
        int selectedItemTextColor = a.getColor(R.styleable.DatePicker_selectedTextColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_selectedTextColor));
        int selectedItemTextSize = a.getDimensionPixelSize(R.styleable.DatePicker_selectedTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelSelectedItemTextSize));
        int itemWidthSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemWidthSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemWidthSpace));
        int itemHeightSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemHeightSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemHeightSpace));
        boolean isZoomInSelectedItem = a.getBoolean(R.styleable.DatePicker_zoomInSelectedItem, true);
        boolean isShowCurtain = a.getBoolean(R.styleable.DatePicker_wheelCurtain, true);
        int curtainColor = a.getColor(R.styleable.DatePicker_wheelCurtainColor, Color.WHITE);
        boolean isShowCurtainBorder = a.getBoolean(R.styleable.DatePicker_wheelCurtainBorder, true);
        int curtainBorderColor = a.getColor(R.styleable.DatePicker_wheelCurtainBorderColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_divider));
        a.recycle();

        setTextSize(textSize);
        setTextColor(textColor);
        setTextGradual(isTextGradual);
        setCyclic(isCyclic);
        setHalfVisibleItemCount(halfVisibleItemCount);
        setSelectedItemTextColor(selectedItemTextColor);
        setSelectedItemTextSize(selectedItemTextSize);
        setItemWidthSpace(itemWidthSpace);
        setItemHeightSpace(itemHeightSpace);
        setZoomInSelectedItem(isZoomInSelectedItem);
        setShowCurtain(isShowCurtain);
        setCurtainColor(curtainColor);
        setShowCurtainBorder(isShowCurtainBorder);
        setCurtainBorderColor(curtainBorderColor);
    }

    private void initChild() {
        mYearPicker = findViewById(R.id.yearPicker_layout_date);
        mYearPicker.setOnYearSelectedListener(this);
        mMonthPicker = findViewById(R.id.monthPicker_layout_date);
        mMonthPicker.setOnMonthSelectedListener(this);
        mDayPicker = findViewById(R.id.dayPicker_layout_date);
        mDayPicker.setOnDaySelectedListener(this);
    }

    private void onDateSelected() {
        if (mOnDateSelectedListener != null) {
            mOnDateSelectedListener.onDateSelected(getYear(),
                    getMonth(), getDay());
        }
    }


    @Override
    public void onMonthSelected(int month) {
        mDayPicker.setMonth(getYear(), month);
        onDateSelected();
    }

    @Override
    public void onDaySelected(int day) {
        onDateSelected();
    }

    @Override
    public void onYearSelected(int year) {
        int month = getMonth();
        mDayPicker.setMonth(year, month);
        onDateSelected();
    }

    /**
     * Sets date.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public void setDate(int year, int month, int day) {
        setDate(year, month, day, true);
    }

    /**
     * Sets date.
     *
     * @param year         the year
     * @param month        the month
     * @param day          the day
     * @param smoothScroll the smooth scroll
     */
    public void setDate(int year, int month, int day, boolean smoothScroll) {
        mYearPicker.setSelectedYear(year, smoothScroll);
        mMonthPicker.setSelectedMonth(month, smoothScroll);
        mDayPicker.setSelectedDay(day, smoothScroll);
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return getDate(format);
    }

    /**
     * Gets date.
     *
     * @param dateFormat the date format
     * @return the date
     */
    public String getDate(@NonNull DateFormat dateFormat) {
        int year, month, day;
        year = getYear();
        month = getMonth();
        day = getDay();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return dateFormat.format(calendar.getTime());
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public int getYear() {
        return mYearPicker.getSelectedYear();
    }

    /**
     * Gets month.
     *
     * @return the month
     */
    public int getMonth() {
        return mMonthPicker.getSelectedMonth();
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public int getDay() {
        return mDayPicker.getSelectedDay();
    }

    /**
     * Gets year picker.
     *
     * @return the year picker
     */
    public YearPicker getYearPicker() {
        return mYearPicker;
    }

    /**
     * Gets month picker.
     *
     * @return the month picker
     */
    public MonthPicker getMonthPicker() {
        return mMonthPicker;
    }

    /**
     * Gets day picker.
     *
     * @return the day picker
     */
    public DayPicker getDayPicker() {
        return mDayPicker;
    }

    /**
     * ???????????????????????????
     *
     * @param textColor ????????????
     */
    public void setTextColor(@ColorInt int textColor) {
        mDayPicker.setTextColor(textColor);
        mMonthPicker.setTextColor(textColor);
        mYearPicker.setTextColor(textColor);
    }

    /**
     * ???????????????????????????
     *
     * @param textSize ????????????
     */
    public void setTextSize(int textSize) {
        mDayPicker.setTextSize(textSize);
        mMonthPicker.setTextSize(textSize);
        mYearPicker.setTextSize(textSize);
    }

    /**
     * ????????????????????????????????????
     *
     * @param selectedItemTextColor ????????????
     */
    public void setSelectedItemTextColor(@ColorInt int selectedItemTextColor) {
        mDayPicker.setSelectedItemTextColor(selectedItemTextColor);
        mMonthPicker.setSelectedItemTextColor(selectedItemTextColor);
        mYearPicker.setSelectedItemTextColor(selectedItemTextColor);
    }

    /**
     * ????????????????????????????????????
     *
     * @param selectedItemTextSize ????????????
     */
    public void setSelectedItemTextSize(int selectedItemTextSize) {
        mDayPicker.setSelectedItemTextSize(selectedItemTextSize);
        mMonthPicker.setSelectedItemTextSize(selectedItemTextSize);
        mYearPicker.setSelectedItemTextSize(selectedItemTextSize);
    }


    /**
     * ??????????????????????????????????????????
     * ?????????????????????????????????,????????????????????????itemCount = mHalfVisibleItemCount * 2 + 1
     *
     * @param halfVisibleItemCount ??????????????????
     */
    public void setHalfVisibleItemCount(int halfVisibleItemCount) {
        mDayPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mMonthPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mYearPicker.setHalfVisibleItemCount(halfVisibleItemCount);
    }

    /**
     * Sets item width space.
     *
     * @param itemWidthSpace the item width space
     */
    public void setItemWidthSpace(int itemWidthSpace) {
        mDayPicker.setItemWidthSpace(itemWidthSpace);
        mMonthPicker.setItemWidthSpace(itemWidthSpace);
        mYearPicker.setItemWidthSpace(itemWidthSpace);
    }

    /**
     * ????????????Item???????????????
     *
     * @param itemHeightSpace ?????????
     */
    public void setItemHeightSpace(int itemHeightSpace) {
        mDayPicker.setItemHeightSpace(itemHeightSpace);
        mMonthPicker.setItemHeightSpace(itemHeightSpace);
        mYearPicker.setItemHeightSpace(itemHeightSpace);
    }


    /**
     * Set zoom in center item.
     *
     * @param zoomInSelectedItem the zoom in center item
     */
    public void setZoomInSelectedItem(boolean zoomInSelectedItem) {
        mDayPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mMonthPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mYearPicker.setZoomInSelectedItem(zoomInSelectedItem);
    }

    /**
     * ???????????????????????????
     * set wheel cyclic
     *
     * @param cyclic ????????????????????????
     */
    public void setCyclic(boolean cyclic) {
        mDayPicker.setCyclic(cyclic);
        mMonthPicker.setCyclic(cyclic);
        mYearPicker.setCyclic(cyclic);
    }

    /**
     * ?????????????????????????????????????????????
     * Set the text color gradient
     *
     * @param textGradual ????????????
     */
    public void setTextGradual(boolean textGradual) {
        mDayPicker.setTextGradual(textGradual);
        mMonthPicker.setTextGradual(textGradual);
        mYearPicker.setTextGradual(textGradual);
    }


    /**
     * ????????????Item?????????????????????
     * set the center item curtain cover
     *
     * @param showCurtain ???????????????
     */
    public void setShowCurtain(boolean showCurtain) {
        mDayPicker.setShowCurtain(showCurtain);
        mMonthPicker.setShowCurtain(showCurtain);
        mYearPicker.setShowCurtain(showCurtain);
    }

    /**
     * ??????????????????
     * set curtain color
     *
     * @param curtainColor ????????????
     */
    public void setCurtainColor(@ColorInt int curtainColor) {
        mDayPicker.setCurtainColor(curtainColor);
        mMonthPicker.setCurtainColor(curtainColor);
        mYearPicker.setCurtainColor(curtainColor);
    }

    /**
     * ??????????????????????????????
     * set curtain border
     *
     * @param showCurtainBorder ?????????????????????
     */
    public void setShowCurtainBorder(boolean showCurtainBorder) {
        mDayPicker.setShowCurtainBorder(showCurtainBorder);
        mMonthPicker.setShowCurtainBorder(showCurtainBorder);
        mYearPicker.setShowCurtainBorder(showCurtainBorder);
    }

    /**
     * ?????????????????????
     * curtain border color
     *
     * @param curtainBorderColor ??????????????????
     */
    public void setCurtainBorderColor(@ColorInt int curtainBorderColor) {
        mDayPicker.setCurtainBorderColor(curtainBorderColor);
        mMonthPicker.setCurtainBorderColor(curtainBorderColor);
        mYearPicker.setCurtainBorderColor(curtainBorderColor);
    }

    /**
     * ?????????????????????????????????
     * set indicator text
     *
     * @param yearText  ??????????????????
     * @param monthText ??????????????????
     * @param dayText   ??????????????????
     */
    public void setIndicatorText(String yearText, String monthText, String dayText) {
        mYearPicker.setIndicatorText(yearText);
        mMonthPicker.setIndicatorText(monthText);
        mDayPicker.setIndicatorText(dayText);
    }

    /**
     * ??????????????????????????????
     * set indicator text color
     *
     * @param textColor ????????????
     */
    public void setIndicatorTextColor(@ColorInt int textColor) {
        mYearPicker.setIndicatorTextColor(textColor);
        mMonthPicker.setIndicatorTextColor(textColor);
        mDayPicker.setIndicatorTextColor(textColor);
    }

    /**
     * ??????????????????????????????
     * indicator text size
     *
     * @param textSize ????????????
     */
    public void setIndicatorTextSize(int textSize) {
        mYearPicker.setTextSize(textSize);
        mMonthPicker.setTextSize(textSize);
        mDayPicker.setTextSize(textSize);
    }

    /**
     * Sets on date selected listener.
     *
     * @param onDateSelectedListener the on date selected listener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    /**
     * The interface On date selected listener.
     */
    public interface OnDateSelectedListener {
        /**
         * On date selected.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        void onDateSelected(int year, int month, int day);
    }
}
