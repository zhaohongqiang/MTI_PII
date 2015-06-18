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



public class pump_second extends Activity {
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

    private  Button Button_OK;
    private void initView() {
        Button_OK = (Button) findViewById(R.id.OK);

        Button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Button_OK) {
                    OkActivity();
                }
            }
        });
    }

    private void OkActivity() {
        Intent intent_pump_meal_bolus = new Intent(this, pump_meal_bolus.class);//大剂量
        startActivity(intent_pump_meal_bolus);
    }

}
