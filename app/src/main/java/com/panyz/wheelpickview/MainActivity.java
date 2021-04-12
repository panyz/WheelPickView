package com.panyz.wheelpickview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.panyz.pos_pickview.confugure.PickerOptions;
import com.panyz.pos_pickview.view.OptionsPickerView;

import java.util.ArrayList;
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


        OptionsPickerView optionsPickerView = new OptionsPickerView(new PickerOptions(this,TYPE_PICKER_OPTIONS));
        optionsPickerView.setTitleText("测试");
        optionsPickerView.setPicker(list,list2);
        optionsPickerView.show();
    }
}