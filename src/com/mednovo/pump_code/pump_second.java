package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.mednovo.mti_pii.R;



public class pump_second extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pump_second);
        initView();

    }

    private Button Button_Menu;
    private Button Button_OK;
    private Button Button_UP;
    private Button Button_DOWN;
    private Button Button_Select[] = new Button[8];
    private int[] R_id = new int[]{ R.id.One,R.id.Two,R.id.Three,R.id.Four,
            R.id.Five,R.id.Six,R.id.Seven,R.id.Eight};

    private static final String Menu_name[] = { "大剂量", "暂停泵", "基础率", "暂时率",
            "系统设定", "历史记录", "数据通信", "出厂设置"};
    private TextView menu_name;
    private int Flag_menu = 0;

    private void initView() {
        Button_OK = (Button) findViewById(R.id.OK);
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        menu_name = (TextView) findViewById(R.id.menu_name);

        for(int i=0;i<8;i++){
            Button_Select[i] = (Button) findViewById(R_id[i]);
            Button_Select[i].setOnClickListener(this);
        }

        Button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Button_OK) {
                    OkActivity();
                }
            }
        });

        Button_DOWN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Button_DOWN) {
                    DownActivity();
                }
            }
        });

        Button_UP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Button_UP) {
                    UpActivity();
                }
            }
        });

        Button_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Button_Menu) {
                    onBackPressed();
                }
            }
        });


    }

    private void OkActivity() {

        for(int i=0;i<8;i++){
            if(0xffff0000 == Button_Select[i].getCurrentTextColor()){//进入哪个界面
                Flag_menu = i;
                break;
            }
        }
        switch (Flag_menu){
            case 0:
                /*Intent intent_pump_meal_bolus = new Intent(this, pump_meal_bolus.class);//大剂量
                startActivity(intent_pump_meal_bolus);*/
                Intent intent_pump_meal_bolus = new Intent();
                intent_pump_meal_bolus.setClass(getApplicationContext(), pump_meal_bolus.class);
                intent_pump_meal_bolus.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_meal_bolus, 0);
                break;
            case 1:
                /*Intent intent_pump_suspend = new Intent(this, pump_suspend.class);//大剂量
                startActivity(intent_pump_suspend);*/
                Intent intent_pump_suspend = new Intent();
                intent_pump_suspend.setClass(getApplicationContext(), pump_suspend.class);
                intent_pump_suspend.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_suspend, 0);
                break;
            case 2:
                Intent intent_pump_basal = new Intent();
                intent_pump_basal.setClass(getApplicationContext(), pump_basal.class);
                intent_pump_basal.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_basal, 0);
                break;
            case 3:
                Intent intent_pump_Temporary_rate = new Intent();
                intent_pump_Temporary_rate.setClass(getApplicationContext(), pump_Temporary_rate.class);
                intent_pump_Temporary_rate.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_Temporary_rate, 0);
                break;
            case 4:
                Intent intent_pump_System_set = new Intent();
                intent_pump_System_set.setClass(getApplicationContext(), pump_System_set.class);
                intent_pump_System_set.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_System_set, 0);
                break;
            case 5:
                Intent intent_pump_history = new Intent();
                intent_pump_history.setClass(getApplicationContext(), pump_history.class);
                intent_pump_history.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_history, 0);
                break;
            case 6:
                Intent intent_pump_data = new Intent();
                intent_pump_data.setClass(getApplicationContext(), pump_data.class);
                intent_pump_data.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_data, 0);
                break;
            case 7:
                Intent intent_pump_factory_set = new Intent();
                intent_pump_factory_set.setClass(getApplicationContext(), pump_factory_set.class);
                intent_pump_factory_set.putExtra("Menu_name",Menu_name[Flag_menu]);
                startActivityForResult(intent_pump_factory_set, 0);
                break;
            default:
                break;
        }
    }

    private void DownActivity() {
        for(int i=0;i<8;i++){
            if(0xffff0000 == Button_Select[i].getCurrentTextColor()){
                Button_Select[i].setTextColor(Color.parseColor("#ff000000"));
                if(i==7){
                    Button_Select[0].setTextColor(Color.parseColor("#ffff0000"));
                    menu_name.setText(Menu_name[0]);
                    menu_name.setTextColor(0xffff0000);
                }else{
                    Button_Select[i+1].setTextColor(Color.parseColor("#ffff0000"));
                    menu_name.setText(Menu_name[i + 1]);
                    menu_name.setTextColor(0xffff0000);
                }
                break;
            }
        }
    }

    private void UpActivity() {
        for(int i=0;i<8;i++){
            if(0xffff0000 == Button_Select[i].getCurrentTextColor()){
                Button_Select[i].setTextColor(Color.parseColor("#ff000000"));
                if(i==0){
                    Button_Select[7].setTextColor(Color.parseColor("#ffff0000"));
                    menu_name.setText(Menu_name[7]);
                    menu_name.setTextColor(0xffff0000);
                }else{
                    Button_Select[i-1].setTextColor(Color.parseColor("#ffff0000"));
                    menu_name.setText(Menu_name[i-1]);
                    menu_name.setTextColor(0xffff0000);
                }
                break;
            }
        }
    }

    // 按钮监听
    @Override
    public void onClick(View v) {

    }
}
