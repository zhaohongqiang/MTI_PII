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


public class pump_Temporary_rate extends Activity implements View.OnClickListener{
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
    private TextView Temporary_five;
    private TextView Temporary_four;
    private TextView Temporary_three;
    private TextView Temporary_one;
    private String Menu_name;
    private boolean Next_click = false;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        Temporary_five = (TextView) findViewById(R.id.five);
        Temporary_four = (TextView) findViewById(R.id.four);
        Temporary_three = (TextView) findViewById(R.id.three);
        Temporary_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        Temporary_one.setText(Menu_name);
        Temporary_three.setVisibility(View.INVISIBLE);
        Temporary_four.setText("1小时 100 %");
        SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Temporary_four.setText(builder);
        Temporary_five.setVisibility(View.INVISIBLE);
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

        if(v == Button_DOWN){
            String [] tmp = {};
            int hour = 1;
            int value = 0;
            
            if(Next_click){
                tmp = Temporary_four.getText().toString().split(" ");;
                value = Integer.parseInt(tmp[1]) - 25;
                if(0 == value){

                }else{
                    Temporary_four.setText(tmp[0] + " "+ Integer.toString(value) + " %");
                    SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    Temporary_four.setText(builder);
                }
            }else {//hour
                tmp = Temporary_four.getText().toString().split("小时");
                if (Integer.parseInt(tmp[0]) == 1) {
                    hour = 1;
                } else {
                    hour = Integer.parseInt(tmp[0]) - 1;
                }
                Temporary_four.setText(Integer.toString(hour) + "小时" + tmp[1]);
                SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Temporary_four.setText(builder);
            }  
        }

        if(v == Button_UP){
            String [] tmp = {};
            int hour = 4;
            int value = 0;
            
            if(Next_click){
                tmp = Temporary_four.getText().toString().split(" ");;
                value = Integer.parseInt(tmp[1]) + 25;
                if(225 == value){

                }else{
                    Temporary_four.setText(tmp[0] + " "+ Integer.toString(value) + " %");
                    SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    Temporary_four.setText(builder);
                }
            }else{//hour
                tmp = Temporary_four.getText().toString().split("小时");
                if(Integer.parseInt(tmp[0]) == 4){
                    hour = 4;
                }else{
                    hour = Integer.parseInt(tmp[0]) + 1;
                }
                Temporary_four.setText(Integer.toString(hour) + "小时" + tmp[1]);
                SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Temporary_four.setText(builder);
            }
        }

        if(v == Button_OK){
            String [] tmp = {};
            String [] tmp2 = {};
      
            if(!Next_click) {//第一次进入
                Next_click = true;
                SpannableStringBuilder builder = new SpannableStringBuilder(Temporary_four.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Temporary_four.setText(builder);
            }else{
                tmp = Temporary_four.getText().toString().split("小时");
                tmp2 = Temporary_four.getText().toString().split(" ");

                if(1 == Integer.parseInt(tmp[0]) && (100 == Integer.parseInt(tmp2[1]))){//没有改变
                    MenuActivity();//回主界面
                }else{
                    ConfirmActivity();//确认界面
                }
            }
        }       
    }

}
