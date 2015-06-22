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


public class pump_history extends Activity implements View.OnClickListener{
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
    private TextView history_one;
    private String Menu_name;
    private String Tmp_name;
    private static final String List_name[] = { "1 即时大剂量", "2 定时大剂量", "3 暂停泵记录", "4 基础率记录",
            "5 暂时率记录", "6 日总量记录", "7 事件记录", "8 排气记录"};
    private int Flag_list = 0;

    /**
     *
     */
    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        history_five = (TextView) findViewById(R.id.five);
        history_four = (TextView) findViewById(R.id.four);
        history_three = (TextView) findViewById(R.id.three);
        history_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        history_one.setText(Menu_name);
        history_three.setText("1 即时大剂量");
        history_three.setTextColor(0xffff0000);
        history_four.setText("2 定时大剂量");
        history_five.setText("3 暂停泵记录");
    }

    private void MenuActivity() {
        Intent intent_pump_second = new Intent(this, pump_second.class);
        startActivity(intent_pump_second);
    }

    private void OneActivity() {
        Intent intent_pump_history_data = new Intent();
        intent_pump_history_data.setClass(getApplicationContext(), pump_history_data.class);
        intent_pump_history_data.putExtra("Flag_list", Flag_list);//
        intent_pump_history_data.putExtra("Menu_name",Menu_name);
        intent_pump_history_data.putExtra("Tmp_name",Tmp_name);//
        startActivityForResult(intent_pump_history_data, 0);
    }

    // 按钮监听
    @Override
    public void onClick(View v) {
        if(v == Button_DOWN){
             if (0xffff0000 == history_three.getCurrentTextColor()) {//1
                    history_three.setTextColor(0xff000000);
                    history_four.setTextColor(0xffff0000);
             } else if (0xffff0000 == history_four.getCurrentTextColor()) {//2
                    history_four.setTextColor(0xff000000);
                    history_five.setTextColor(0xffff0000);
             } else if (0xffff0000 == history_five.getCurrentTextColor()) {//3
                    String [] tmp = history_five.getText().toString().split(" ");
                    int postion = Integer.parseInt(tmp[0]);
                    if(8 == postion){
                        history_five.setText(List_name[2]);
                        history_four.setText(List_name[1]);
                        history_three.setText(List_name[0]);
                        history_three.setTextColor(0xffff0000);
                        history_five.setTextColor(0xff000000);
                    }else if(1 == postion){
                        history_five.setText(List_name[1]);
                        history_four.setText(List_name[0]);
                        history_three.setText(List_name[7]);
                    }else if(2 == postion){
                        history_five.setText(List_name[2]);
                        history_four.setText(List_name[1]);
                        history_three.setText(List_name[0]);
                    }else{
                        history_five.setText(List_name[postion]);
                        history_four.setText(List_name[postion - 1]);
                        history_three.setText(List_name[postion - 2]);
                }
            }
        }
        if(v == Button_UP){
            if (0xffff0000 == history_five.getCurrentTextColor()) {//1
                history_five.setTextColor(0xff000000);
                history_four.setTextColor(0xffff0000);
            } else if (0xffff0000 == history_four.getCurrentTextColor()) {//2
                history_four.setTextColor(0xff000000);
                history_three.setTextColor(0xffff0000);
            } else if (0xffff0000 == history_three.getCurrentTextColor()) {//3
                String [] tmp = history_three.getText().toString().split(" ");
                int postion = Integer.parseInt(tmp[0]);
                if(1 == postion){
                    history_five.setText(List_name[7]);
                    history_four.setText(List_name[6]);
                    history_three.setText(List_name[5]);
                    history_five.setTextColor(0xffff0000);
                    history_three.setTextColor(0xff000000);
                }else{
                    history_three.setText(List_name[postion - 2]);
                    history_four.setText(List_name[postion - 1]);
                    history_five.setText(List_name[postion]);
                }
            }
        }
        if(v == Button_Menu){
            //SecondActivity();
            onBackPressed();
        }
        if(v == Button_OK){
            if (0xffff0000 == history_three.getCurrentTextColor()) {//1
                String[] tmp = history_three.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == history_four.getCurrentTextColor()) {//2
                String[] tmp = history_four.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            } else if (0xffff0000 == history_five.getCurrentTextColor()) {//3
                String[] tmp = history_five.getText().toString().split(" ");
                Flag_list = Integer.parseInt(tmp[0]);
                Tmp_name = tmp[1];
            }
            OneActivity();//进入相应界面
        }

    }

}