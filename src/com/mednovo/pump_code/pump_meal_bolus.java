package com.mednovo.pump_code;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mednovo.mti_pii.R;


public class pump_meal_bolus extends Activity {
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
    private void initView() {
        Button_Menu = (Button) findViewById(R.id.MENU);

        Button_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == Button_Menu) {
                    myselfchartActivity();
                }
            }
        });
    }

    private void myselfchartActivity() {
        Intent intent_pump_second = new Intent(this, pump_second.class);
        startActivity(intent_pump_second);
    }

}
