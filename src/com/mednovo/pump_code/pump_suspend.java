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


public class pump_suspend extends Activity implements View.OnClickListener{
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
    private TextView Suspend_five;
    private TextView Suspend_four;
    private TextView Suspend_three;
    private TextView Suspend_one;
    private String Menu_name;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        Suspend_five = (TextView) findViewById(R.id.five);
        Suspend_four = (TextView) findViewById(R.id.four);
        Suspend_three = (TextView) findViewById(R.id.three);
        Suspend_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Suspend_three.setVisibility(View.INVISIBLE);

        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        Suspend_one.setText(Menu_name);
        Suspend_four.setText("06:11 暂停输注");
        Suspend_five.setVisibility(View.INVISIBLE);
    }

    private void MenuActivity() {
        Intent intent_pump_main = new Intent(this, pump_main.class);
        startActivity(intent_pump_main);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        MenuActivity();
    }

}
