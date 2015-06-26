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


public class pump_basal_in extends Activity implements View.OnClickListener{
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
    private TextView basal_five;
    private TextView basal_four;
    private TextView basal_three;
    private TextView basal_one;
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
        basal_five = (TextView) findViewById(R.id.five);
        basal_four = (TextView) findViewById(R.id.four);
        basal_three = (TextView) findViewById(R.id.three);
        basal_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Flag_list = data.getExtras().getInt("Flag_list");
        Menu_name = data.getExtras().getString("Menu_name");        
        basal_one.setText(Menu_name);
        basal_three.setVisibility(View.INVISIBLE);
        basal_five.setVisibility(View.INVISIBLE);

        switch(Flag_list){
            case 0:
                basal_four.setText("设定日总量 24.00 U");
                break;
            case 1:
                basal_four.setText("日总量 24.00 U");
                break;
        }

    }

    private void MenuActivity() {
        Intent intent_pump_main = new Intent(this, pump_main.class);
        startActivity(intent_pump_main);
    }

    private void ConfirmActivity() {

        Intent intent_pump_Confirm = new Intent();
        intent_pump_Confirm.setClass(getApplicationContext(), pump_Confirm.class);
        intent_pump_Confirm.putExtra("Menu_name",Menu_name);
        startActivityForResult(intent_pump_Confirm, 0);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_Menu){
            onBackPressed();
        }

        if(v == Button_OK){
            if(0 == Flag_list){
                ConfirmActivity();
            }else{

            }
        }
        if(v == Button_DOWN){

        }
        if(v == Button_UP){

        }

    }

}
