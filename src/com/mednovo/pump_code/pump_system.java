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
import java.text.SimpleDateFormat;
import java.util.Date;


public class pump_system extends Activity implements View.OnClickListener{
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
    private TextView system_five;
    private TextView system_four;
    private TextView system_three;
    private TextView system_one;
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
        system_five = (TextView) findViewById(R.id.five);
        system_four = (TextView) findViewById(R.id.four);
        system_three = (TextView) findViewById(R.id.three);
        system_one = (TextView) findViewById(R.id.one);

        Button_Menu.setOnClickListener(this);
        Button_OK.setOnClickListener(this);
        Button_UP.setOnClickListener(this);
        Button_DOWN.setOnClickListener(this);

        Intent data = getIntent();
        Flag_list = data.getExtras().getInt("Flag_list");
        Menu_name = data.getExtras().getString("Menu_name");
        Tmp_name = data.getExtras().getString("Tmp_name");
        system_one.setText(Menu_name);
        system_three.setText(Tmp_name);
        system_five.setVisibility(View.INVISIBLE);

        switch(Flag_list){
            case 1:
                system_four.setText("15.0 U");
                break;
            case 2:
                system_four.setText("2.00 U/h");
                break;
            case 3:
                system_four.setText("1小时 30.0 U");
                SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                system_four.setText(builder);
                break;
            case 4:
                system_four.setText("60.0 U");
                break;
            case 5:
                system_four.setText("请医生指导设定！");
                system_four.setText("U-100");
                system_four.setTextColor(0xffff0000);
                break;
            case 6:
                system_four.setText("常显示");
                system_four.setTextColor(0xffff0000);
                break;
            case 7:
                system_four.setText("鸣笛");
                system_four.setTextColor(0xffff0000);
                break;
            case 8:
                system_five.setVisibility(View.VISIBLE);
                SimpleDateFormat formatter = new SimpleDateFormat("yy年MM月dd日");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String time = formatter.format(curDate);
                system_four.setText(time);
                SimpleDateFormat formatter1 = new SimpleDateFormat("kk时mm分ss秒");
                Date curDate1 = new Date(System.currentTimeMillis());//获取当前时间
                String time1 = formatter1.format(curDate1);
                system_five.setText(time1);

                SpannableStringBuilder builder1 = new SpannableStringBuilder(system_four.getText().toString());
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                builder1.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                builder1.setSpan(new ForegroundColorSpan(Color.RED), 1, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                system_four.setText(builder1);
                break;
            case 9:
                system_four.setText("恢复");
                system_four.setTextColor(0xffff0000);
                break;
            case 10:
                system_four.setText("English");
                system_four.setText("中文");
                system_four.setTextColor(0xffff0000);
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
        if(v == Button_DOWN){
            Flag_click = true;
            String [] tmp = {};
            int hour = 1;
            double value = 0.0;
            BigDecimal data_tmp;
            BigDecimal data;
            switch(Flag_list){
                case 1:
                    tmp = system_four.getText().toString().split("U");;
                    value = (Float.parseFloat(tmp[0])*100 - 100)/100;
                    data_tmp = new BigDecimal(10.0);
                    data = new BigDecimal(value);
                    if(0 > data.compareTo(data_tmp)){
                        value = (Double.parseDouble(tmp[0])*1000 - 100)/1000;
                    }
                    data_tmp = new BigDecimal(0.0);
                    data = new BigDecimal(value);
                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(Double.toString(value) + " U");
                    }
                    break;
                case 2:
                    tmp = system_four.getText().toString().split("U");
                    value = (Double.parseDouble(tmp[0])*1000 - 100)/1000;
                    data_tmp = new BigDecimal(0.0);
                    data = new BigDecimal(value);

                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(String.format("%.2f",value) + " U/h");
                    }
                    break;
                case 3:
                    if(Next_click){
                        tmp = system_four.getText().toString().split(" ");;
                        value = (Float.parseFloat(tmp[1])*100 - 100)/100;
                        data_tmp = new BigDecimal(10.0);
                        data = new BigDecimal(value);
                        if(0 > data.compareTo(data_tmp)){
                            value = (Double.parseDouble(tmp[1])*1000 - 100)/1000;
                        }
                        data_tmp = new BigDecimal(0.0);
                        data = new BigDecimal(value);
                        if(0 == data.compareTo(data_tmp)){

                        }else{
                            system_four.setText(tmp[0] + " "+ Double.toString(value) + " U");
                            SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            system_four.setText(builder);
                        }
                    }else {//hour
                        tmp = system_four.getText().toString().split("小时");
                        if (Integer.parseInt(tmp[0]) == 1) {
                            hour = 1;
                        } else {
                            hour = Integer.parseInt(tmp[0]) - 1;
                        }
                        system_four.setText(Integer.toString(hour) + "小时" + tmp[1]);
                        SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        system_four.setText(builder);
                    }
                    break;
                case 4:
                    tmp = system_four.getText().toString().split("U");;
                    value = (Float.parseFloat(tmp[0])*100 - 100)/100;
                    data_tmp = new BigDecimal(2);
                    data = new BigDecimal(value);
                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(Double.toString(value) + " U");
                    }
                    break;
                case 5:
                    if(system_four.getText().toString().equals("U-100")){
                        system_four.setText("U-40");
                    }else{
                        system_four.setText("U-100");
                    }
                    break;
                case 6:
                    if(system_four.getText().toString().equals("常显示")){
                        system_four.setText("不常显示");
                    }else {
                        system_four.setText("常显示");
                    }
                    break;
                case 7:
                    if(system_four.getText().toString().equals("鸣笛")){
                        system_four.setText("不鸣笛");
                    }else{
                        system_four.setText("鸣笛");
                    }
                    break;
                case 8:
                    system_four.setText("15年");
                    break;
                case 9:
                    if(system_four.getText().toString().equals("恢复")){
                        system_four.setText("不恢复");
                    }else{
                        system_four.setText("恢复");
                    }
                    break;
                case 10:
                    if(0xffff0000 == system_four.getCurrentTextColor()){
                        //system_four.setText("English");
                        system_four.setTextColor(0xffff0000);
                        system_four.setTextColor(0xff000000);
                    }else{
                        //system_four.setText("中文");
                        system_four.setTextColor(0xffff0000);
                        system_four.setTextColor(0xff000000);
                    }
                    break;
            }
        }
        if(v == Button_UP){
            Flag_click = true;
            String [] tmp = {};
            int hour = 4;
            double value = 0.00;
            BigDecimal data_tmp;
            BigDecimal data;
            switch(Flag_list){
                case 1:
                    tmp = system_four.getText().toString().split("U");;
                    value = (Float.parseFloat(tmp[0])*100 + 100)/100;
                    data_tmp = new BigDecimal(11.0);
                    data = new BigDecimal(value);
                    if(0 > data.compareTo(data_tmp)){
                        value = (Double.parseDouble(tmp[0])*1000 + 100)/1000;
                    }
                    data_tmp = new BigDecimal(26.0);
                    data = new BigDecimal(value);
                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(Double.toString(value) + " U");
                    }
                    break;
                case 2:
                    tmp = system_four.getText().toString().split("U");
                    value = (Double.parseDouble(tmp[0])*1000 + 100)/1000;
                    data_tmp = new BigDecimal(4.1);
                    data = new BigDecimal(value);
                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(String.format("%.2f", value) + " U/h");
                    }
                    break;
                case 3:
                    if(Next_click){
                        tmp = system_four.getText().toString().split(" ");;
                        value = (Float.parseFloat(tmp[1])*100 + 100)/100;
                        data_tmp = new BigDecimal(11.0);
                        data = new BigDecimal(value);
                        if(0 > data.compareTo(data_tmp)){
                            value = (Double.parseDouble(tmp[1])*1000 + 100)/1000;
                        }
                        data_tmp = new BigDecimal(31.0);
                        data = new BigDecimal(value);
                        if(0 == data.compareTo(data_tmp)){

                        }else{
                            system_four.setText(tmp[0] + " "+ Double.toString(value) + " U");
                            SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            system_four.setText(builder);
                        }
                    }else{//hour
                        tmp = system_four.getText().toString().split("小时");
                        if(Integer.parseInt(tmp[0]) == 4){
                            hour = 4;
                        }else{
                            hour = Integer.parseInt(tmp[0]) + 1;
                        }
                        system_four.setText(Integer.toString(hour) + "小时" + tmp[1]);
                        SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        system_four.setText(builder);
                    }
                    break;
                case 4:
                    tmp = system_four.getText().toString().split("U");
                    value = (Float.parseFloat(tmp[0])*100 + 100)/100;
                    data_tmp = new BigDecimal(121);
                    data = new BigDecimal(value);
                    if(0 == data.compareTo(data_tmp)){

                    }else{
                        system_four.setText(Double.toString(value) + " U");
                    }
                    break;
                case 5:
                    if(system_four.getText().toString().equals("U-100")){
                        system_four.setText("U-40");
                    }else{
                        system_four.setText("U-100");
                    }
                    break;
                case 6:
                    if(system_four.getText().toString().equals("常显示")){
                        system_four.setText("不常显示");
                    }else {
                        system_four.setText("常显示");
                    }
                    break;
                case 7:
                    if(system_four.getText().toString().equals("鸣笛")){
                        system_four.setText("不鸣笛");
                    }else{
                        system_four.setText("鸣笛");
                    }
                    break;
                case 8:
                    system_four.setText("15年");
                    break;
                case 9:
                    if(system_four.getText().toString().equals("恢复")){
                        system_four.setText("不恢复");
                    }else{
                        system_four.setText("恢复");
                    }
                    break;
                case 10:
                    if(0xffff0000 == system_four.getCurrentTextColor()){
                        //system_four.setText("English");
                        system_four.setTextColor(0xffff0000);
                        system_four.setTextColor(0xff000000);
                    }else{
                        //system_four.setText("中文");
                        system_four.setTextColor(0xffff0000);
                        system_four.setTextColor(0xff000000);
                    }
                    break;
            }
        }

        if(v == Button_OK){
            String [] tmp = {};
            String [] tmp2 = {};
            double value = 0.00;
            BigDecimal data_tmp;
            BigDecimal data;

            if(3 == Flag_list){
                if(!Next_click) {//第一次进入
                    Next_click = true;
                    SpannableStringBuilder builder = new SpannableStringBuilder(system_four.getText().toString());
                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    builder.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 6, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    system_four.setText(builder);
                }else{
                    tmp = system_four.getText().toString().split("小时");

                    tmp2 = system_four.getText().toString().split(" ");
                    value = Float.parseFloat(tmp2[1]);
                    data_tmp = new BigDecimal(30.0);
                    data = new BigDecimal(value);

                    if(1 == Integer.parseInt(tmp[0]) && (0 == data.compareTo(data_tmp))){//没有改变
                        MenuActivity();//回主界面
                    }else{
                        ConfirmActivity();//确认界面
                    }
                }
            }else {
                if (Flag_click){
                        ConfirmActivity();//确认界面
                } else {
                    MenuActivity();//回主界面
                }
            }
        }
    }

}
