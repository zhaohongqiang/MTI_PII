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


public class pump_factory_set extends Activity implements View.OnClickListener{
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
    private TextView factory_five;
    private TextView factory_four;
    private TextView factory_three;
    private TextView factory_one;
    private String Menu_name;
    private static final String List_name[] = { "1 最大大剂量", "2 最大基础率", "3 最大时段量", "4 最大日总量",
            "5 浓度设定", "6 显示设定", "7 鸣笛设定", "8 出厂编号"};
    private int Flag_list = 0;
    private String Tmp_name;

    /**
     *
     */
    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        factory_five = (TextView) findViewById(R.id.five);
        factory_four = (TextView) findViewById(R.id.four);
        factory_three = (TextView) findViewById(R.id.three);
        factory_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        factory_one.setText(Menu_name);
        factory_three.setText("1 最大大剂量");
        factory_three.setTextColor(0xffff0000);
        factory_four.setText("2 最大基础率");
        factory_five.setText("3 最大时段量");
    }

    private void MenuActivity() {
        Intent intent_pump_second = new Intent(this, pump_second.class);
        startActivity(intent_pump_second);
    }

    private void OneActivity() {
        Intent intent_pump_facroty_data = new Intent();
        intent_pump_facroty_data.setClass(getApplicationContext(), pump_factory_data.class);
        intent_pump_facroty_data.putExtra("Flag_list", Flag_list);//
        intent_pump_facroty_data.putExtra("Menu_name",Menu_name);
        intent_pump_facroty_data.putExtra("Tmp_name",Tmp_name);//
        startActivityForResult(intent_pump_facroty_data, 0);
    }
    
    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_DOWN){
             if (0xffff0000 == factory_three.getCurrentTextColor()) {//1
                    factory_three.setTextColor(0xff000000);
                    factory_four.setTextColor(0xffff0000);
             } else if (0xffff0000 == factory_four.getCurrentTextColor()) {//2
                    factory_four.setTextColor(0xff000000);
                    factory_five.setTextColor(0xffff0000);
             } else if (0xffff0000 == factory_five.getCurrentTextColor()) {//3
                    String [] tmp = factory_five.getText().toString().split(" ");
                    int postion = Integer.parseInt(tmp[0]);
                    if(8 == postion){
                        factory_five.setText(List_name[2]);
                        factory_four.setText(List_name[1]);
                        factory_three.setText(List_name[0]);
                        factory_three.setTextColor(0xffff0000);
                        factory_five.setTextColor(0xff000000);
                    }else if(1 == postion){
                        factory_five.setText(List_name[1]);
                        factory_four.setText(List_name[0]);
                        factory_three.setText(List_name[7]);
                    }else if(2 == postion){
                        factory_five.setText(List_name[2]);
                        factory_four.setText(List_name[1]);
                        factory_three.setText(List_name[0]);
                    }else{
                        factory_five.setText(List_name[postion]);
                        factory_four.setText(List_name[postion - 1]);
                        factory_three.setText(List_name[postion - 2]);
                }
            }
        }
        if(v == Button_UP){
            if (0xffff0000 == factory_five.getCurrentTextColor()) {//1
                factory_five.setTextColor(0xff000000);
                factory_four.setTextColor(0xffff0000);
            } else if (0xffff0000 == factory_four.getCurrentTextColor()) {//2
                factory_four.setTextColor(0xff000000);
                factory_three.setTextColor(0xffff0000);
            } else if (0xffff0000 == factory_three.getCurrentTextColor()) {//3
                String [] tmp = factory_three.getText().toString().split(" ");
                int postion = Integer.parseInt(tmp[0]);
                if(1 == postion){
                    factory_five.setText(List_name[7]);
                    factory_four.setText(List_name[6]);
                    factory_three.setText(List_name[5]);
                    factory_five.setTextColor(0xffff0000);
                    factory_three.setTextColor(0xff000000);
                }else{
                    factory_three.setText(List_name[postion - 2]);
                    factory_four.setText(List_name[postion - 1]);
                    factory_five.setText(List_name[postion]);
                }
            }
        }
        if(v == Button_Menu){            
            onBackPressed();
        }
        if(v == Button_OK){
            if (0xffff0000 == factory_three.getCurrentTextColor()) {//1
                String[] tmp = factory_three.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == factory_four.getCurrentTextColor()) {//2
                String[] tmp = factory_four.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == factory_five.getCurrentTextColor()) {//3
                String[] tmp = factory_five.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            }
            OneActivity();//进入相应界面
        }

    }

}