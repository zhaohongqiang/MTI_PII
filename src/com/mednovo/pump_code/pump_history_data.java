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


public class pump_history_data extends Activity implements View.OnClickListener{
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
    private TextView history_five;
    private TextView history_four;
    private TextView history_three;
    private TextView history_two;
    private TextView history_one;
    private String Menu_name;
    private int Flag_list = 0;
    private String Tmp_name;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        history_five = (TextView) findViewById(R.id.five);
        history_four = (TextView) findViewById(R.id.four);
        history_three = (TextView) findViewById(R.id.three);
        history_two = (TextView) findViewById(R.id.two);
        history_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);


        Intent data = getIntent();
        Flag_list = data.getExtras().getInt("Flag_list");
        Menu_name = data.getExtras().getString("Menu_name");
        Tmp_name = data.getExtras().getString("Tmp_name");
        history_one.setText("记录数");
        history_two.setText("1/1");
        history_three.setText("日期");
        history_four.setText("时间");

        switch(Flag_list){
            case 1:
                history_five.setText("15 U");
                break;
            case 2:
                history_five.setText("2.00 U/h");
                break;
            case 3:
                history_five.setText("1小时 30.0 U");
                break;
            case 4:
                history_five.setText("60.0 U");
                break;
            case 5:
                history_four.setText("请医生指导设定！");
                history_five.setText("U-100");
                break;
            case 6:
                history_five.setText("常显示");
                break;
            case 7:
                history_five.setText("鸣笛");
                break;
            case 8:
                history_five.setText("15年");
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
        if(v == Button_Menu){
            onBackPressed();
        }
    }

}
