package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mednovo.mti_pii.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


public class pump_data_in extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pump_meal_bolus);

        initView();

    }

    private  Button Button_Menu;
    private  Button Button_OK;
    private  Button Button_DOWN;
    private  Button Button_UP;
    private TextView data_five;
    private TextView data_four;
    private TextView data_three;
    private TextView data_one;
    private String Menu_name;
    private int Flag_list = 0;
    private String Tmp_name;
    private boolean Flag_click = false;
    private boolean Next_click = false;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        data_five = (TextView) findViewById(R.id.five);
        data_four = (TextView) findViewById(R.id.four);
        data_three = (TextView) findViewById(R.id.three);
        data_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Flag_list = data.getExtras().getInt("Flag_list");
        Menu_name = data.getExtras().getString("Menu_name");        
        data_one.setText(Menu_name);
        data_three.setVisibility(View.INVISIBLE);
        data_five.setVisibility(View.INVISIBLE);

        switch(Flag_list){
            case 0:
                data_four.setText("打开蓝牙......");
                break;
            case 1:
                data_four.setText("该功能厂家使用");
                break;
        }

    }

    private void MenuActivity() {
        Intent intent_pump_main = new Intent(this, pump_main.class);
        startActivity(intent_pump_main);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_Menu || v == Button_OK){
            MenuActivity();
        }

    }

}
