package com.mednovo.pump_code;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mednovo.mti_pii.R;


public class Menu_Down extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //�����ޱ���
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //����ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.menu_updown);
    }
}