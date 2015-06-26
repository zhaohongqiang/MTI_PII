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


public class pump_Confirm extends Activity implements View.OnClickListener{
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
    private TextView Confirm_five;
    private TextView Confirm_four;
    private TextView Confirm_three;
    private TextView Confirm_one;
    private String Menu_name;

    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);
        Button_OK = (Button) findViewById(R.id.OK);
        Button_UP = (Button) findViewById(R.id.UP);
        Button_DOWN = (Button) findViewById(R.id.DOWN);
        Confirm_five = (TextView) findViewById(R.id.five);
        Confirm_four = (TextView) findViewById(R.id.four);
        Confirm_three = (TextView) findViewById(R.id.three);
        Confirm_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);


        Intent data = getIntent();
        Menu_name = data.getExtras().getString("Menu_name");
        Confirm_one.setText(Menu_name);
        Confirm_three.setText("是否确认？   是");
        Confirm_four.setText("            否");
        Confirm_four.setTextColor(0xffff0000);
        Confirm_five.setVisibility(View.INVISIBLE);
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
        if(v == Button_OK){
            MenuActivity();
        }
        if(v == Button_DOWN || v == Button_UP){
            if(0xffff0000 == Confirm_four.getCurrentTextColor()){
                Confirm_four.setTextColor(0xff000000);
                SpannableStringBuilder builder = new SpannableStringBuilder(Confirm_three.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.RED), 8, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Confirm_three.setText(builder);
            }else{
                Confirm_four.setTextColor(0xffff0000);
                SpannableStringBuilder builder = new SpannableStringBuilder(Confirm_three.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.BLACK), 8, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Confirm_three.setText(builder);
            }

        }

    }

}
