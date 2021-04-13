package com.panyz.pos_pickview.calendar;

public interface DatePickerController {
	int getMaxYear();

	void onDayOfMonthSelected(int year, int month, int day);

    void onDateRangeSelected(final SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays);

}