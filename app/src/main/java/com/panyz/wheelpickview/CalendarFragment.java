package com.panyz.wheelpickview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.panyz.pos_pickview.calendar.DatePickerController;
import com.panyz.pos_pickview.calendar.DayPickerView;
import com.panyz.pos_pickview.calendar.SimpleMonthAdapter;

import java.util.Calendar;

public class CalendarFragment extends DialogFragment implements DatePickerController {
    DayPickerView mDayPickerView;

    private SimpleMonthAdapter.CalendarDay mStartDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mSelectDay;
    private SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> mRange;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDayPickerView = (DayPickerView) view.findViewById(R.id.pickerView);
        mDayPickerView.setController(this,mStartDay, 0,this.mRange);
        int monthCount = mDayPickerView.getAdapter().getItemCount();
        mDayPickerView.smoothScrollToPosition(monthCount);
        return view;
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
}

