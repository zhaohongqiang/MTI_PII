package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mednovo.mti_pii.R;


public class pump_System_set extends Activity implements View.OnClickListener{
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
    private TextView System_five;
    private TextView System_four;
    private TextView System_three;
    private TextView System_one;
    private String Menu_name;
    private String Tmp_name;
    private static final String List_name[] = { "1 最大大剂量", "2 最大基础率", "3 最大时段量", "4 最大日总量",
            "5 浓度设定", "6 显示设定", "7 鸣笛设定", "8 时间设定", "9 恢复出厂值", "10 设定语言"};
    private int Flag_list = 0;

    /**
     *
     */
    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        System_five = (TextView) findViewById(R.id.five);
        System_four = (TextView) findViewById(R.id.four);
        System_three = (TextView) findViewById(R.id.three);
        System_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        System_one.setText(Menu_name);
        System_three.setText("1 最大大剂量");
        System_three.setTextColor(0xffff0000);
        System_four.setText("2 最大基础率");
        System_five.setText("3 最大时段量");
    }

    private void OneActivity() {
        Intent intent_pump_system = new Intent();
        intent_pump_system.setClass(getApplicationContext(), pump_system.class);
        intent_pump_system.putExtra("Flag_list", Flag_list);//
        intent_pump_system.putExtra("Menu_name",Menu_name);
        intent_pump_system.putExtra("Tmp_name",Tmp_name);//
        startActivityForResult(intent_pump_system, 0);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_DOWN){
             if (0xffff0000 == System_three.getCurrentTextColor()) {//1
                    System_three.setTextColor(0xff000000);
                    System_four.setTextColor(0xffff0000);
             } else if (0xffff0000 == System_four.getCurrentTextColor()) {//2
                    System_four.setTextColor(0xff000000);
                    System_five.setTextColor(0xffff0000);
             } else if (0xffff0000 == System_five.getCurrentTextColor()) {//3
                    String [] tmp = System_five.getText().toString().split(" ");
                    int postion = Integer.parseInt(tmp[0]);
                    if(10 == postion){
                        /*System_five.setText(List_name[0]);
                        System_four.setText(List_name[postion - 1]);
                        System_three.setText(List_name[postion - 2]);*/
                        System_five.setText(List_name[2]);
                        System_four.setText(List_name[1]);
                        System_three.setText(List_name[0]);
                        System_three.setTextColor(0xffff0000);
                        System_five.setTextColor(0xff000000);
                    }else if(1 == postion){
                        System_five.setText(List_name[1]);
                        System_four.setText(List_name[0]);
                        System_three.setText(List_name[9]);
                    }else if(2 == postion){
                        System_five.setText(List_name[2]);
                        System_four.setText(List_name[1]);
                        System_three.setText(List_name[0]);
                    }else{
                        System_five.setText(List_name[postion]);
                        System_four.setText(List_name[postion - 1]);
                        System_three.setText(List_name[postion - 2]);
                }
            }
        }
        if(v == Button_UP){
            if (0xffff0000 == System_five.getCurrentTextColor()) {//1
                System_five.setTextColor(0xff000000);
                System_four.setTextColor(0xffff0000);
            } else if (0xffff0000 == System_four.getCurrentTextColor()) {//2
                System_four.setTextColor(0xff000000);
                System_three.setTextColor(0xffff0000);
            } else if (0xffff0000 == System_three.getCurrentTextColor()) {//3
                String [] tmp = System_three.getText().toString().split(" ");
                int postion = Integer.parseInt(tmp[0]);
                if(1 == postion){
                    System_five.setText(List_name[9]);
                    System_four.setText(List_name[8]);
                    System_three.setText(List_name[7]);
                    System_five.setTextColor(0xffff0000);
                    System_three.setTextColor(0xff000000);
                }else{
                    System_three.setText(List_name[postion - 2]);
                    System_four.setText(List_name[postion - 1]);
                    System_five.setText(List_name[postion]);
                }
            }
        }
        if(v == Button_Menu){
            onBackPressed();
        }
        if(v == Button_OK){
            if (0xffff0000 == System_three.getCurrentTextColor()) {//1
                String[] tmp = System_three.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == System_four.getCurrentTextColor()) {//2
                String[] tmp = System_four.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == System_five.getCurrentTextColor()) {//3
                String[] tmp = System_five.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            }
            OneActivity();//进入相应界面
        }

    }

}