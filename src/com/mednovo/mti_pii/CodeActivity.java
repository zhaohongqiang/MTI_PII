package com.mednovo.mti_pii;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.UnsupportedEncodingException;


/**
 * Created by zhao on 2015/5/29.
 */

public class CodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height=display.getHeight();
        Log.v("width", "" + width);
        Log.v("height",""+ height);
        Button Clear = (Button) findViewById(R.id.button);
        Button Cal = (Button) findViewById(R.id.button2);

        Cal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText1 =(EditText)findViewById(R.id.editText);
                EditText editText2 =(EditText)findViewById(R.id.editText2);
                EditText editText3 =(EditText)findViewById(R.id.editText3);
                EditText editText4 =(EditText)findViewById(R.id.editText4);
                EditText editText5 =(EditText)findViewById(R.id.editText5);
                EditText editText6 =(EditText)findViewById(R.id.editText6);
                switch (v.getId()) {
                    case R.id.button2:
                        //��ȡ�ı���1���ı�
                        String Serial_TmpNo="";
                        String Serial_TxtNo="";
                        byte Serial_No[] = null;
                        if(editText1.getText().length() == 0){
                            editText2.getText().clear();
                            editText3.getText().clear();
                            editText4.getText().clear();
                            editText5.getText().clear();
                            editText6.getText().clear();
                            break;
                        }
                        else if(editText1.getText().length() < 13)
                        {
                            //dialog();
                            break;
                        }
                        else {
                            Serial_TxtNo = editText1.getText().toString();
                            try {//getbytes���ܻ᲻���������ַ����������쳣����
                                Serial_No = Serial_TxtNo.getBytes("UTF8");//string ת byte[]
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Serial_TxtNo = new String(Serial_No);//ʹ��Serial_No֮ǰҪ���ȳ�ʼ��
                            Serial_TmpNo = bytesToHexString(Serial_No);
                            editText2.setText(Serial_TmpNo.toCharArray(), 0, Serial_TmpNo.length());//���ı���1���ı������ı���2


                            byte[] Buf1 = new byte[9];//byte Buf1[] = null;
                            byte[] Buf2 = new byte[9];// byte Buf2[] = null;

                            byte CRC1; byte CRC2; byte NEWCRC1; byte NEWCRC2;
                            String CompensationStepNum = "";//������������
                            String EraseRecording = "";//������¼����
                            String ConcentrationChange = "";//Ũ�ȸ�������
                            String AntiBlockingScheme = "";//���·�������
                            int i;
                            //��������1 ��Ҫ�����к����ݣ�Ӳ���汾�š�����汾�š����кţ�
                            for (i = 0; i < 4; i++) {
                                Buf1[i] = Serial_No[i];
                                Log.v("Buf1", "" + Buf1[i]);
                            }
                            for (i = 4; i < 9; i++) {
                                Buf1[i] = Serial_No[i + 4];
                                Log.v("Buf1", "" + Buf1[i]);
                            }
                            //��������2 ��Ҫ�����к����ݣ�������š������ܺš����кţ�
                            for (i = 0; i < 9; i++) {
                                Buf2[i] = Serial_No[i + 4];
                                Log.v("Buf2", "" + Buf2[i]);
                            }

                            // String NewBuf1=new String(Buf1,"UTF-8");        //byte[]תstring
                            //char[] NewBuf1= Encoding.ASCII.GetChars(Buf1);      //byte[]ת��Ϊchar[]
                            //char[] NewBuf1=new char[13];
                            //����1����
                            CRC1 = CRCVerifyCount(Buf1, 0, 9);
                            //String NewBuf1=new String(CRC1,"UTF-8");        //byte[]תstring
                            Log.v("CRC1", "" + CRC1);
                            CRC2 = CRCVerifyCount(Buf2, 0, 9);
                            Log.v("CRC2", "" + CRC2);
                            CompensationStepNum = TwobytesToHexString(CRC1, CRC2);
                            Log.v("CompensationStepNum", "" + CompensationStepNum);
                            //editText3.setText(CompensationStepNum);
                            editText3.setText(CompensationStepNum.toCharArray(), 0, CompensationStepNum.length());

                            NEWCRC1 = EncryptNewdata(CRC1,(byte)0x92);
                            Log.v("NEWCRC1", "" + NEWCRC1);
                            NEWCRC2 = EncryptNewdata(CRC2,(byte)0x92);
                            Log.v("NEWCRC2", "" + NEWCRC2);
                            EraseRecording = TwobytesToHexString(NEWCRC1, NEWCRC2);
                            editText4.setText(EraseRecording.toCharArray(), 0, EraseRecording.length());

                            NEWCRC1 = EncryptNewdata(NEWCRC1, (byte) 0x49);
                            Log.v("NEWCRC1", "" + NEWCRC1);
                            NEWCRC2 = EncryptNewdata(NEWCRC2, (byte) 0x49);
                            Log.v("NEWCRC2", "" + NEWCRC2);
                            ConcentrationChange = TwobytesToHexString(NEWCRC1, NEWCRC2);
                            editText5.setText(ConcentrationChange.toCharArray(), 0, ConcentrationChange.length());

                            NEWCRC1 = EncryptNewdata(CRC1, (byte) 0x6b);
                            Log.v("NEWCRC1", "" + NEWCRC1);
                            NEWCRC2 = EncryptNewdata(CRC2, (byte) 0x6b);
                            Log.v("NEWCRC2", "" + NEWCRC2);
                            AntiBlockingScheme = TwobytesToHexString(NEWCRC1, NEWCRC2);
                            editText6.setText(AntiBlockingScheme.toCharArray(), 0, AntiBlockingScheme.length());

                            break;
                        }
                }
            }
        });
        Clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText1 =(EditText)findViewById(R.id.editText);
                EditText editText2 =(EditText)findViewById(R.id.editText2);
                switch (v.getId()) {
                    case R.id.button:
                        // ���
                        //editText1.setText("");
                        editText1.getText().clear();
                        break;
                }
            }
        });


  /*
        //��EditTextǰ������:import android.widget.EditText;
        //��ȡ�ı���1���ı�
        String str1="";
        EditText editText1 =(EditText)findViewById(R.id.editText);
        str1=editText1.getText().toString();

        //���ı���1���ı������ı���2
        EditText editText2 =(EditText)findViewById(R.id.editText2);
        editText2.setText(str1.toCharArray(), 0, str1.length());
   */
    }

    /*CRC8λ�㷨*/
    //indata:ҪУ������ݣ�preset:��ʼֵ
    //����У������
    public byte crc8fun( byte indata, byte preset)
    {
        int loop;
        byte outdata;
        byte crc_pol = (byte) 0xAB ;//����ֵ
        outdata = (byte) (indata^preset);
        //outdata = (byte) ((indata&0xff)^(preset&0xff));

        Log.v("outdata",""+outdata);
        for(loop=0;loop<8;loop++)
        {
            if((outdata&0x01)==0x01){
                outdata= (byte) (((outdata&0xff)>>>1)^(crc_pol&0xff));
                Log.v("outdata111111",""+outdata);
            }
            else{
                outdata= (byte) ((outdata&0xff)>>>1);
                Log.v("outdata22222",""+outdata);
            }
        }
        Log.v("outdata333333333",""+outdata);
        return outdata;
    }

    /*CRCУ��*/
    //u_Buf:ҪУ������飬u_start:������ʼλ��u_len:ҪУ������鳤��
    //����У������
    public byte CRCVerifyCount(byte[] u_Buf, int u_start, int u_len) {
        int i;
        byte data = (byte)0xFF;// 0xFF; -1//��ʼֵ
        for(i=u_start;i<u_len;i++) {
            Log.v("CRCVerifyCount",""+i);
            data = crc8fun(u_Buf[i], data);
        }
        return(data);
    }
    //8������2Ϊ����1�ļ������ݣ����㹫ʽΪ������1��ǰ��λ�ͺ���λ���ݷֱ��4λ���4λ������ȡ��������0x92�����
    /*��������*/
    public byte Encryptdata(byte u_data)
    {
        byte tmp;
        tmp=u_data;
        tmp=(byte)(((tmp&0xff)>>>4)+((u_data&0xff)<<4));
        tmp^=0xff;
        tmp^=0x92;
        return tmp;
    }
    public byte EncryptNewdata(byte u_data,byte u_code)
    {
        byte tmp;
        tmp=u_data;
        tmp=(byte)(((tmp&0xff)>>>4)+((u_data&0xff)<<4));
        tmp^=0xff;
        tmp^=u_code;
        return tmp;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static String bytesToHexString(byte[] bytes) {
        String result = "@1000\n";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
            result += " ";
        }
        result += '\n';
        result += 'q';
        return result;
    }
    public static String TwobytesToHexString(byte byteone ,byte bytetwo) {
        String result = "";
        {
            String hexStringOne = Integer.toHexString(byteone & 0xFF);
            if (hexStringOne.length() == 1) {
                hexStringOne = '0' + hexStringOne;
            }
            result += hexStringOne.toUpperCase();

            String hexStringTwo = Integer.toHexString(bytetwo & 0xFF);
            if (hexStringTwo.length() == 1) {
                hexStringTwo = '0' + hexStringTwo;
            }
            result += hexStringTwo.toUpperCase();
        }
        return result;
    }

}
