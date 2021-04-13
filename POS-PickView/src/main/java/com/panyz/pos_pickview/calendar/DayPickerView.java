package com.panyz.pos_pickview.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.panyz.pos_pickview.R;

import java.util.Calendar;
import java.util.Date;


public class DayPickerView extends RecyclerView {
    protected Context mContext;
    protected SimpleMonthAdapter mAdapter;
    private DatePickerController mController;
    protected int mCurrentScrollState = 0;
    protected long mPreviousScrollPosition;
    protected int mPreviousScrollState = 0;
    private TypedArray typedArray;
    private OnScrollListener onScrollListener;
    private int smoothToMonthIndex;


    public DayPickerView(Context context) {
        this(context, null);
    }

    public DayPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayPickerView);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            init(context);
        }
    }

    public void setController(DatePickerController mController, SimpleMonthAdapter.CalendarDay startDay, int mode,
                              SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> range) {
        this.mController = mController;
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(this);//这里放置的是的recycleview
        setUpAdapter(startDay, mode,range);
        setAdapter(mAdapter);
        smoothToCurrentMonth();
    }


    public void init(Context paramContext) {
        setLayoutManager(new ScrollLinearLayoutManager(paramContext, LinearLayoutManager.HORIZONTAL, false));
        mContext = paramContext;
        setUpListView();

        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final SimpleMonthView child = (SimpleMonthView) recyclerView.getChildAt(0);
                if (child == null) {
                    return;
                }

                mPreviousScrollPosition = dy;
                mPreviousScrollState = mCurrentScrollState;
            }
        };
    }

    private void smoothToCurrentMonth() {
        int smoothToMonthIndex;
        Calendar showCalendar;
        if (getSelectedDays() != null && getSelectedDays().getFirst() != null) {
            Date date = getSelectedDays().getFirst().getDate();
            showCalendar = Calendar.getInstance();
            showCalendar.setTime(date);
        } else {
            showCalendar = Calendar.getInstance();
        }

        smoothToMonthIndex = getShouldShowMonthIndex(showCalendar);

        if (smoothToMonthIndex > 0) {
            scrollToPosition(smoothToMonthIndex);
        }
    }

    private int getShouldShowMonthIndex(Calendar calendar) {
        int monthIndex;
        int diffYear = mController.getMaxYear() - calendar.get(Calendar.YEAR);
        if (diffYear > 0) {
            diffYear--;
            monthIndex = mAdapter.getItemCount() - (13 - calendar.getTime().getMonth() + mAdapter.getLastMonth())
                    - 12 * diffYear;
        } else {
            monthIndex = mAdapter.getItemCount() - (mAdapter.getLastMonth() - calendar.getTime().getMonth()) - 1;
        }
        smoothToMonthIndex = monthIndex;
        return monthIndex;
    }


    protected void setUpAdapter(SimpleMonthAdapter.CalendarDay startDay, int mode, SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> range) {
        if (mAdapter == null) {
            mAdapter = new SimpleMonthAdapter(getContext(), mController, typedArray);
            mAdapter.setMode(mode);
            if (startDay != null) {
                mAdapter.setSelectedDay(startDay);
            }
        }
        mAdapter.setSelectRange(range);
        mAdapter.notifyDataSetChanged();
    }

    protected void setUpListView() {
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(onScrollListener);
        setFadingEdgeLength(0);
    }

    public SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> getSelectedDays() {
        return mAdapter.getSelectedDays();
    }

    protected DatePickerController getController() {
        return mController;
    }

    protected TypedArray getTypedArray() {
        return typedArray;
    }

    public boolean smoothToIndexMonth(boolean isToNextMonth) {
        if (isToNextMonth) {
            if (smoothToMonthIndex < getAdapter().getItemCount() - 1) {
                smoothToMonthIndex++;
                scrollToPosition(smoothToMonthIndex);
                return true;
            }
        } else {
            if (smoothToMonthIndex > 0) {
                smoothToMonthIndex--;
                scrollToPosition(smoothToMonthIndex);
                return true;
            }
        }
        return false;
    }


    class ScrollLinearLayoutManager extends LinearLayoutManager {

        public ScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean canScrollHorizontally() {
            return false;
        }
    }


}