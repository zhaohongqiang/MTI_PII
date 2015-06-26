package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

import com.mednovo.mti_pii.R;



public class pump_main extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pump_main);
        initView();

    }

    private  Button Button_Menu;
    private  Button Button_OK;
    private  Button Button_UP;
    private  Button Button_DOWN;
    private  Boolean Click_flag = false;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);
    }

    private void MenuActivity() {
        Intent intent_pump_second = new Intent(this, pump_second.class);
        startActivity(intent_pump_second);
    }

    private void UpActivity() {
        Intent intent_Menu_Up = new Intent(this, Menu_Up.class);
        startActivity(intent_Menu_Up);
    }

    private void DownActivity() {
        Intent intent_Menu_Down = new Intent(this, Menu_Down.class);
        startActivity(intent_Menu_Down);
    }

    private void ExhaustActivity(String Menu_name) {

        Intent intent_pump_Confirm = new Intent();
        intent_pump_Confirm.setClass(getApplicationContext(), pump_Confirm.class);
        intent_pump_Confirm.putExtra("Menu_name",Menu_name);
        startActivityForResult(intent_pump_Confirm, 0);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_Menu) {
            MenuActivity();
        }

        if(v == Button_DOWN) {
            if(Click_flag){
                Click_flag = false;
                ExhaustActivity("排气");
            }else {
                DownActivity();
            }
        }

        if(v == Button_UP) {
            UpActivity();
        }

        if(v == Button_OK){
            Click_flag = true;
        }
    }

}
