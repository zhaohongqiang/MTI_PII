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


public class pump_bolus extends Activity implements View.OnClickListener{
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
    private TextView Bolus_five;
    private TextView Bolus_four;
    private TextView Bolus_three;
    private TextView Bolus_one;
    private String Menu_name;
    private int Flag_list = 0;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        Bolus_five = (TextView) findViewById(R.id.five);
        Bolus_four = (TextView) findViewById(R.id.four);
        Bolus_three = (TextView) findViewById(R.id.three);
        Bolus_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Bolus_three.setVisibility(View.INVISIBLE);

        Intent data = getIntent();
        Flag_list = data.getExtras().getInt("Flag_list");
        Menu_name = data.getExtras().getString("Menu_name");
        Bolus_one.setText(Menu_name);
        if(0 == Flag_list){
            Bolus_four.setText("本次 1.0 U");
            SpannableStringBuilder builder = new SpannableStringBuilder(Bolus_four.getText().toString());
            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            builder.setSpan(new ForegroundColorSpan(Color.RED), 3, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            Bolus_four.setText(builder);
            Bolus_five.setVisibility(View.INVISIBLE);
        }else{
            Bolus_four.setText("05:00-06:00");
            Bolus_five.setVisibility(View.VISIBLE);
            Bolus_five.setText("1.0 U");
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
            if(0 == Flag_list){//大剂量
                ConfirmActivity();//确认界面
            }
        }

        if(v == Button_DOWN){
            String [] tmp = {};
            double value = 0.0;
            BigDecimal data_tmp;
            BigDecimal data;

            if(0 == Flag_list) {
                tmp = Bolus_four.getText().toString().split(" ");
                value = (Float.parseFloat(tmp[1])*100 - 100)/100;
                data_tmp = new BigDecimal(10.0);
                data = new BigDecimal(value);
                if(0 > data.compareTo(data_tmp)){
                    value = (Double.parseDouble(tmp[1])*1000 - 100)/1000;
                }
                data_tmp = new BigDecimal(0.0);
                data = new BigDecimal(value);
                if (0 == data.compareTo(data_tmp)) {

                } else {
                    Bolus_four.setText(tmp[0] + " " + Double.toString(value) + " U");
                    SpannableStringBuilder builder = new SpannableStringBuilder(Bolus_four.getText().toString());
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 3, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    Bolus_four.setText(builder);

                }
            }
        }

        if(v == Button_UP){
            String [] tmp = {};
            double value = 0.0;
            BigDecimal data_tmp;
            BigDecimal data;

            if(0 == Flag_list) {
                tmp = Bolus_four.getText().toString().split(" ");
                value = (Float.parseFloat(tmp[1])*100 + 100)/100;
                data_tmp = new BigDecimal(11.0);
                data = new BigDecimal(value);
                if(0 > data.compareTo(data_tmp)){
                    value = (Double.parseDouble(tmp[1])*1000 + 100)/1000;
                }
                data_tmp = new BigDecimal(16.0);
                data = new BigDecimal(value);
                if (0 == data.compareTo(data_tmp)) {

                } else {
                    Bolus_four.setText(tmp[0] + " " + Double.toString(value) + " U");
                    SpannableStringBuilder builder = new SpannableStringBuilder(Bolus_four.getText().toString());
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 3, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    Bolus_four.setText(builder);
                }
            }
        }
    }

}
