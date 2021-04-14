package com.panyz.pos_pickview.calendar;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import com.panyz.pos_pickview.R;
import com.panyz.pos_pickview.listener.OnDismissListener;
import com.panyz.pos_pickview.utils.PickerViewAnimateUtil;
import com.panyz.pos_pickview.view.BasePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickView implements DatePickerController, View.OnClickListener {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_RANGE = 1;

    private Context context;
    private ViewGroup decorView;
    private ViewGroup rootView;
    protected ViewGroup contentContainer;
    private Animation outAnim;
    private Animation inAnim;
    private OnDateSelectedListener onDateSelectedListener;

    private boolean isShowing;
    private boolean dismissing;
    private boolean isAnim = true;

    private int mode;//0-单选日期，1-选时间段

    private SimpleMonthAdapter.CalendarDay mStartDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mSelectDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mRange;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    private DayPickerView dayPickView;
    private TextView tvYearMonth;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Calendar mCalendar;

    private String titleText;
    private String cancelText;
    private String submitText;

    public DatePickView(Builder builder) {
        this.context = builder.context;
        this.mode = builder.mode;
        this.titleText = builder.titleText;
        this.cancelText = builder.cancelText;
        this.submitText = builder.submitText;
        this.onDateSelectedListener = builder.onDateSelectedListener;
        if (builder.dateRange != null) {
            mRange = builder.dateRange;
        }
        initViews();
        initAnim();
        initLayout();
    }

    private void initViews() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (decorView == null) {
            decorView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        }
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_date_pick_view, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_55272C32));
        contentContainer = rootView.findViewById(R.id.content_container);
        contentContainer.setLayoutParams(params);
        setKeyBackCancelable(true);
        setOutSideCancelable(false);
    }

    private void initAnim() {
        inAnim = getAnimation(true);
        outAnim = getAnimation(false);
    }

    private void initLayout() {
        LayoutInflater.from(context).inflate(R.layout.layout_date_pick_view_content, contentContainer);

        TextView tvTitle = contentContainer.findViewById(R.id.tv_title);
        tvStartDate = contentContainer.findViewById(R.id.tv_start_date);
        tvEndDate = contentContainer.findViewById(R.id.tv_end_date);
        tvYearMonth = contentContainer.findViewById(R.id.tv_year_month);

        Button btnCancel = contentContainer.findViewById(R.id.btn_cancel);
        Button btnSubmit = contentContainer.findViewById(R.id.btn_submit);

        contentContainer.findViewById(R.id.iv_pre_month).setOnClickListener(this);
        contentContainer.findViewById(R.id.iv_next_month).setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        if (mode == MODE_SINGLE) {
            tvTitle.setText(R.string.pickerview_date);
            ConstraintLayout clDateRange = contentContainer.findViewById(R.id.cl_date_range);
            clDateRange.setVisibility(View.GONE);
        } else if (mode == MODE_RANGE) {
            tvTitle.setText(R.string.pickerview_date_range);
        }

        dayPickView = contentContainer.findViewById(R.id.day_pick_view);
        dayPickView.setController(this, mStartDay, mode, this.mRange);


        mCalendar = Calendar.getInstance();
        if (mRange != null) {
            setSelectDays(mRange.getFirst(), mRange.getLast());
            if (mRange.getFirst() != null) {
                mCalendar.set(Calendar.MONTH, mRange.getFirst().month);
            }
        }

        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        tvYearMonth.setText(stringBuilder.toString());

        if (titleText != null) {
            tvTitle.setText(titleText);
        }
        if (cancelText != null) {
            btnCancel.setText(cancelText);
        }
        if (submitText != null) {
            btnSubmit.setText(submitText);
        }
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(context, millis, millis, flags);
    }


    private Animation getAnimation(boolean isInAnimation) {
        int res = PickerViewAnimateUtil.getAnimationResource(Gravity.BOTTOM, isInAnimation);
        return AnimationUtils.loadAnimation(context, res);
    }

    private void setKeyBackCancelable(boolean isCancelable) {
        ViewGroup view = rootView;
        view.setFocusable(isCancelable);
        view.setFocusableInTouchMode(isCancelable);
        if (isCancelable) {
            view.setOnKeyListener(onKeyListener);
        } else {
            view.setOnKeyListener(null);
        }
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_DOWN && isShowing()) {
                dismiss();
                return true;
            }
            return false;
        }
    };

    public boolean isShowing() {
        return rootView.getParent() != null || isShowing;
    }

    public void dismiss() {
        if (dismissing) {
            return;
        }
        if (isAnim) {
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismissImmediately();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            contentContainer.startAnimation(outAnim);
        } else {
            dismissImmediately();
        }
        dismissing = true;
    }

    private void dismissImmediately() {
        decorView.post(new Runnable() {
            @Override
            public void run() {
                decorView.removeView(rootView);
                isShowing = false;
                dismissing = false;
            }
        });
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        isShowing = true;
        onAttached(rootView);
        rootView.requestFocus();
    }

    private void onAttached(View view) {
        decorView.addView(view);
        if (isAnim) {
            contentContainer.startAnimation(inAnim);
        }
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        SimpleMonthAdapter.CalendarDay start = new SimpleMonthAdapter.CalendarDay();
        SimpleMonthAdapter.CalendarDay end = new SimpleMonthAdapter.CalendarDay();
        start.setDay(year, month, day);
        end.setDay(year, month, day);
        setSelectDays(start, end);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        setSelectDays(selectedDays.getFirst(), selectedDays.getLast());
    }

    private void setSelectDays(SimpleMonthAdapter.CalendarDay start, SimpleMonthAdapter.CalendarDay end) {
        if (mSelectDay == null) {
            mSelectDay = new SimpleMonthAdapter.SelectedDays<>();
        }

        if (mode == MODE_RANGE) {
            if (start != null && end != null) {
                String date1 = sdf.format(start.getDate());
                String date2 = sdf.format(end.getDate());

                Calendar calendar1 = strToCalander(date1);
                Calendar calendar2 = strToCalander(date2);

                if (calendar1.before(calendar2)) {
                    mSelectDay.setFirst(start);
                    mSelectDay.setLast(end);
                    tvStartDate.setText(date1);
                    tvEndDate.setText(date2);
                } else {
                    mSelectDay.setFirst(end);
                    mSelectDay.setLast(start);
                    tvStartDate.setText(date2);
                    tvEndDate.setText(date1);
                }
            }
        } else {
            mSelectDay.setFirst(start);
            mSelectDay.setLast(start);
        }



    }

    public Calendar strToCalander(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public void setOutSideCancelable(boolean isCancelable) {

        if (rootView != null) {
            View view = rootView.findViewById(R.id.outmost_container);

            if (isCancelable) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            dismiss();
                        }
                        return false;
                    }
                });
            } else {
                view.setOnTouchListener(null);
            }
        }

    }

    /**
     * 切换到上一个月
     */
    public void onPreMonth() {
        if (dayPickView != null) {
            if (dayPickView.smoothToIndexMonth(false)) {
                mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1);
                StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
                stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
                tvYearMonth.setText(stringBuilder.toString());
            }

        }
    }


    /**
     * 切换到下一个月
     */
    public void onNextMonth() {
        if (dayPickView != null) {
            if (dayPickView.smoothToIndexMonth(true)) {
                mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) + 1);
                StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
                stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
                tvYearMonth.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_next_month) {
            onNextMonth();
        } else if (v.getId() == R.id.iv_pre_month) {
            onPreMonth();
        } else if (v.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (v.getId() == R.id.btn_submit) {
            if (onDateSelectedListener != null) {
                onDateSelectedListener.onDateSelected(mSelectDay);
            }
            dismiss();
        }
    }

    public interface OnDateSelectedListener {
        void onDateSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mSelectData);
    }

    public static class Builder {
        private Context context;
        private int mode = 0;
        private String titleText;
        private String cancelText;
        private String submitText;
        private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> dateRange;
        private OnDateSelectedListener onDateSelectedListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder mode(int mode) {
            this.mode = mode;
            return this;
        }

        public Builder dateRange(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> dateRange) {
            this.dateRange = dateRange;
            return this;
        }

        public Builder titleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder submitText(String submitText) {
            this.submitText = submitText;
            return this;
        }

        public Builder onDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
            this.onDateSelectedListener = onDateSelectedListener;
            return this;
        }


        public DatePickView show() {
            DatePickView datePickView = new DatePickView(this);
            datePickView.show();
            return datePickView;
        }

    }


}
