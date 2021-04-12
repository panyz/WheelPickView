package com.panyz.wheelpickview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.panyz.pos_pickview.builder.OptionsPickerBuilder;
import com.panyz.pos_pickview.builder.TimePickerBuilder;
import com.panyz.pos_pickview.confugure.PickerOptions;
import com.panyz.pos_pickview.listener.OnOptionsSelectListener;
import com.panyz.pos_pickview.listener.OnTimeSelectListener;
import com.panyz.pos_pickview.view.OptionsPickerView;
import com.panyz.pos_pickview.view.TimePickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.panyz.pos_pickview.confugure.PickerOptions.TYPE_PICKER_OPTIONS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickView();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickView();
            }
        });
    }

    private void showPickView() {
        List<String> list = new ArrayList<>();
        list.add("TEXT1");
        list.add("TEXT2");
        list.add("TEXT3");
        list.add("TEXT4");
        list.add("TEXT5");

        List<List<String>> list2 = new ArrayList<>();
        list2.add(list);
        list2.add(list);
        list2.add(list);
        list2.add(list);
        list2.add(list);

//        List<List<String>> list3 = new ArrayList<>();
//        list3.add(list);
//        list3.add(list);
//        list3.add(list);
//        list3.add(list);
//        list3.add(list);

        OptionsPickerView<String> optionsPickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

            }
        }).setTitleText("测试").build();
        optionsPickerView.setPicker(list);
        optionsPickerView.show();
    }


    private void showTimePickView() {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -100);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 100);
        TimePickerBuilder timePickerViewBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .setTitleText("测试")//标题
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDate(Calendar.getInstance())
                .setOutSideCancelable(true);//点击外部dismiss default true

        timePickerViewBuilder.setRangDate(startDate, endDate);
        timePickerViewBuilder.build().show();
    }

}