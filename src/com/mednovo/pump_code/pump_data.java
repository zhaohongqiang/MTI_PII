package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mednovo.mti_pii.R;


public class pump_data extends Activity implements View.OnClickListener{
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

    /**
     *
     */
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
        Menu_name = data.getExtras().getString("Menu_name");
        data_one.setText(Menu_name);
        data_three.setText("1 上传数据");
        data_three.setTextColor(0xffff0000);
        data_four.setText("2 软件升级");
        data_five.setVisibility(View.INVISIBLE);
    }

    private void MenuActivity() {
        Intent intent_pump_second = new Intent(this, pump_second.class);
        startActivity(intent_pump_second);
    }

    private void OneActivity() {
        Intent intent_pump_bolus = new Intent();
        intent_pump_bolus.setClass(getApplicationContext(), pump_data_in.class);
        intent_pump_bolus.putExtra("Flag_list",Flag_list);
        intent_pump_bolus.putExtra("Menu_name",Menu_name);
        startActivityForResult(intent_pump_bolus, 0);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_DOWN || v == Button_UP){
            if(0xffff0000 == data_three.getCurrentTextColor()){
                Flag_list = 1;
                data_four.setTextColor(0xffff0000);
                data_three.setTextColor(0xff000000);
            }else {
                Flag_list = 0;
                data_three.setTextColor(0xffff0000);
                data_four.setTextColor(0xff000000);
            }
        }
        if(v == Button_Menu){
            //SecondActivity();
            onBackPressed();
        }
        if(v == Button_OK){
            OneActivity();
        }

    }

}