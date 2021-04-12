package com.panyz.pos_pickview.confugure;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.panyz.pos_pickview.R;
import com.panyz.pos_pickview.listener.CustomListener;
import com.panyz.pos_pickview.listener.OnOptionsSelectChangeListener;
import com.panyz.pos_pickview.listener.OnOptionsSelectListener;
import com.panyz.pos_pickview.listener.OnTimeSelectChangeListener;
import com.panyz.pos_pickview.listener.OnTimeSelectListener;
import com.panyz.pos_pickview.view.WheelView;

import java.util.Calendar;

public class PickerOptions {
    public static final int TYPE_PICKER_OPTIONS = 1;
    public static final int TYPE_PICKER_TIME = 2;

    public OnOptionsSelectListener optionsSelectListener;
    public OnTimeSelectListener timeSelectListener;
    public View.OnClickListener cancelListener;

    public OnTimeSelectChangeListener timeSelectChangeListener;
    public OnOptionsSelectChangeListener optionsSelectChangeListener;
    public CustomListener customListener;

    //options picker
    public String label1, label2, label3;//单位字符
    public int option1, option2, option3;//默认选中项
    public int x_offset_one, x_offset_two, x_offset_three;//x轴偏移量

    public boolean cyclic1 = false;//是否循环，默认否
    public boolean cyclic2 = false;
    public boolean cyclic3 = false;

    public boolean isRestoreItem = false; //切换时，还原第一项


    //time picker
    public boolean[] type = new boolean[]{true, true, true, false, false, false};//显示类型，默认显示： 年月日

    public Calendar date;//当前选中时间
    public Calendar startDate;//开始时间
    public Calendar endDate;//终止时间
    public int startYear;//开始年份
    public int endYear;//结尾年份

    public boolean cyclic = false;//是否循环
    public boolean isLunarCalendar = false;//是否显示农历

    public String label_year, label_month, label_day, label_hours, label_minutes, label_seconds;//单位
    public int x_offset_year, x_offset_month, x_offset_day, x_offset_hours, x_offset_minutes, x_offset_seconds;//单位


    public PickerOptions(Context context, int buildType) {
        this.context = context;
        initRes(context);
        if (buildType == TYPE_PICKER_OPTIONS) {
            layoutRes = R.layout.pickerview_options;
        } else {
            layoutRes = R.layout.pickerview_time;
        }
    }

    private void initRes(Context context) {
        textColorConfirm = ContextCompat.getColor(context, R.color.color_ffffff);
        textColorCancel = ContextCompat.getColor(context, R.color.color_0078FF);
        textColorTitle = ContextCompat.getColor(context, R.color.color_272C32);
        bgColorTitle = ContextCompat.getColor(context, R.color.color_F4F4F6);
        bgColorWheel = ContextCompat.getColor(context, R.color.color_ffffff);

    }

    //******* general field ******//
    public int layoutRes;
    public ViewGroup decorView;
    public int textGravity = Gravity.CENTER;
    public Context context;

    public String textContentConfirm;//确定按钮文字
    public String textContentCancel;//取消按钮文字
    public String textContentTitle;//标题文字

    public int textColorConfirm;//确定按钮颜色
    public int textColorCancel;//取消按钮颜色
    public int textColorTitle;//标题颜色
    public int bgColorWheel;//滚轮背景颜色
    public int bgColorTitle;//标题背景颜色

    public int textSizeSubmitCancel = 16;//确定取消按钮大小
    public int textSizeTitle = 16;//标题文字大小
    public int textSizeContent = 16;//内容文字大小

    public int textColorOut = 0xFFa8a8a8; //分割线以外的文字颜色
    public int textColorCenter = 0xFF2a2a2a; //分割线之间的文字颜色
    public int dividerColor = 0xFF0078FF; //分割线的颜色
    public int outSideColor = -1; //显示时的外部背景色颜色,默认是灰色

    public float lineSpacingMultiplier = 2.5f; // 条目间距倍数 默认1.6
    public boolean isDialog;//是否是对话框模式

    public boolean cancelable = true;//是否能取消
    public boolean isCenterLabel = false;//是否只显示中间的label,默认每个item都显示
    public Typeface font = Typeface.MONOSPACE;//字体样式
    public WheelView.DividerType dividerType = WheelView.DividerType.FILL;//分隔线类型
    public int itemsVisibleCount = 5; //最大可见条目数
    public boolean isAlphaGradient = false; //透明度渐变
}
