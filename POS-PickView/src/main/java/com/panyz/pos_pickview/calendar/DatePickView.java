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
import androidx.core.content.ContextCompat;

import com.panyz.pos_pickview.R;
import com.panyz.pos_pickview.listener.OnDismissListener;
import com.panyz.pos_pickview.utils.PickerViewAnimateUtil;

import java.util.Calendar;

public class DatePickView implements DatePickerController{
    public static final int MODE_SINGLE = 0;
    public static final int MODE_RANGE = 1;

    private Context context;
    private ViewGroup decorView;
    private ViewGroup rootView;
    protected ViewGroup contentContainer;
    private Animation outAnim;
    private Animation inAnim;
    private OnDismissListener onDismissListener;

    private boolean isShowing;
    private boolean dismissing;
    private boolean isAnim = true;

    private int mode = 0;//0-单选日期，1-选时间段

    private SimpleMonthAdapter.CalendarDay mStartDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mSelectDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mRange;

    private String mCurrentDate;
    private DayPickerView dayPickView;
    private TextView tvYearMonth;
    private Calendar mCalendar;

    public DatePickView(Context context,int mode,SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> range,String mCurrentDate) {
        this.context = context;
        this.mode = mode;
        if (range != null) {
            mRange = range;
            setSelectDays(mRange.getFirst(), mRange.getLast());
        }
        this.mCurrentDate = mCurrentDate;
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
        rootView.setBackgroundColor(ContextCompat.getColor(context,R.color.color_55272C32));
        contentContainer = rootView.findViewById(R.id.content_container);
        contentContainer.setLayoutParams(params);
        setKeyBackCancelable(true);
    }

    private void initAnim() {
        inAnim = getAnimation(true);
        outAnim = getAnimation(false);
    }

    private void initLayout() {
        LayoutInflater.from(context).inflate(R.layout.layout_date_pick_view_content, contentContainer);

        TextView tvTitle = contentContainer.findViewById(R.id.tv_title);
        TextView tvStartDate = contentContainer.findViewById(R.id.tv_start_date);
        TextView tvEndDate = contentContainer.findViewById(R.id.tv_end_date);
        tvYearMonth = contentContainer.findViewById(R.id.tv_year_month);

        Button btnCancel = contentContainer.findViewById(R.id.btn_cancel);
        Button btnSubmit = contentContainer.findViewById(R.id.btn_submit);

        contentContainer.findViewById(R.id.iv_pre_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreMonth();
            }
        });
        contentContainer.findViewById(R.id.iv_next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextMonth();
            }
        });

        ConstraintLayout clDateRange = contentContainer.findViewById(R.id.cl_date_range);
        clDateRange.setVisibility(View.GONE);

        if (mode == MODE_SINGLE) {
            tvTitle.setText("日期");
        } else if (mode == MODE_RANGE) {
            tvTitle.setText("时间段");
        }

        mCalendar = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        tvYearMonth.setText(stringBuilder.toString());

        dayPickView = contentContainer.findViewById(R.id.day_pick_view);
        dayPickView.setController(this,mStartDay, this.mRange);
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
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(DatePickView.this);
                }
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

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
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
        mSelectDay.setFirst(start);
        mSelectDay.setLast(end);
    }

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

}
