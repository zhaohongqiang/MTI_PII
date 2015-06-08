package com.mednovo.tools;

//import android.content.Context;

//import com.mednovo.mti_pii.R;
import java.util.HashMap;



public class GeneralCommands {//��������
    private static HashMap<String, String> attributes = new HashMap();

    private static HashMap<String, String> Framelength = new HashMap();

    //public static String SET_FACTORYDATA = "A8 19 00 30 31 30 31 31 30 33 33 32 30 30 30 31 00 00 20 06 00 00 8A A8 A2";
    //public static String READ_FACTORYDATA = "A8 08 01 00 00 16 78 A2";

    /*===============���õĲ�ѯ��������=================*/
    public static String SET_FACTORYDATA = "���ó�������";
    public static String READ_FACTORYDATA = "��ȡ��������";
    public static String SET_SYSSETDATA = "����ϵͳ��������";
    public static String READ_SYSSETDATA = "��ȡϵͳ��������";
    public static String SET_BASALDATA = "���û���������";
    public static String READ_BASALDATA = "��ȡ����������";
    public static String READ_RUNNINGDATA = "��ȡ����״̬����";
    public static String READ_MEALBOLUSRECORD = "��ȡ��ʱ�������¼";
    public static String READ_TIMEBOLUSRECORD = "��ȡ��ʱ�������¼";
    public static String READ_SUSPENDEDPUMPRECORD = "��ȡ��ͣ�ü�¼";
    public static String READ_BASALRECORD = "��ȡ�����ʼ�¼";
    public static String READ_TEMPORARYRECORD = "��ȡ��ʱ�ʼ�¼";
    public static String READ_DAILYTOTALRECORD = "��ȡ��������¼";
    public static String READ_EVENTRECORD = "��ȡ�¼���¼";
    public static String READ_EXHAUSTRECORD = "��ȡ������¼";

    /*==================�������ݸ����ֽڶζ���
    * Start_bit         ��ʼλ 0xA8
    * Frame_length      ֡����
    * Function_code     ������
    * Data_field        ������
    * Control_code      ������
    * CRC_check         CRCУ��
    * End_bit           ����λ 0xA2
    * */

    public static String   Bluetooth_Allow = "00";
    public static String   Concentration_change = "00";
    public static String   lcd_setup ="1";
    public static String   lcd_OnOff ="1";
    public static String   ring_setup ="1";
    public static String   language_set ="1";
    public static String   LPM_Status ="0";
    public static String   LcdBk_OnOff ="0";
    public static String   KeyLock ="0";
    public static String   Extra ="0";

    public static String   Start_bit ="A8";
    public static String   Frame_length ="08";
    public static String   Function_code ="01";
    public static String   Data_field ="00";
    public static String   Control_code ="00";
    public static String   CRC_check ="";
    public static String   End_bit ="A2";

    public static String[] Function_tmpcode = {"00","01",
                                            "10","11","12","13","14",
                                            "20","21","22","23","24","25","26","27"
                                            };
    public static String[] Frame_tmplength = {"19","08",
                                            "18","08","37","08","08",
                                            "08","08","08","08","08","08","08","08"
                                           };

    public static String[] BasalData_value = {"0.0","0.0","0.0","0.0","0.0","0.0",
                                                "0.0","0.0","0.0","0.0","0.0","0.0",
                                                "0.0","0.0","0.0","0.0","0.0","0.0",
                                                "0.0","0.0","0.0","0.0","0.0","0.0"
                                            };

    static {      //�鴮ʱ ö��δʵ�֣��������滻Ϊ������  Function_code
        //user
        //attributes.put(getResources().getString(R.string.Set_factorydata), "MedNovo MTI-PII");//���ó�������
        //attributes.put(Context.getString(R.string.Set_factorydata), "MedNovo MTI-PII");//���ó�������   Set factory data
        //attributes.put("���ó�������", "SET_FACTORYDATA");

        attributes.put(SET_FACTORYDATA, Function_tmpcode[CommandsFound.SET_FACTORYDATA]);//���ó������� ������ ʮ������
        attributes.put(READ_FACTORYDATA, Function_tmpcode[CommandsFound.READ_FACTORYDATA]);//��ȡ�������� ������

        attributes.put(SET_SYSSETDATA, Function_tmpcode[CommandsFound.SET_SYSSETDATA]);//����ϵͳ�������� ������
        attributes.put(READ_SYSSETDATA, Function_tmpcode[CommandsFound.READ_SYSSETDATA]);//��ȡϵͳ�������� ������

        attributes.put(SET_BASALDATA, Function_tmpcode[CommandsFound.SET_BASALDATA]);//���û��������� ������
        attributes.put(READ_BASALDATA, Function_tmpcode[CommandsFound.READ_BASALDATA]);//��ȡ���������� ������

        attributes.put(READ_RUNNINGDATA, Function_tmpcode[CommandsFound.READ_RUNNINGDATA]);//��ȡ����״̬���� ������

        attributes.put(READ_MEALBOLUSRECORD, Function_tmpcode[CommandsFound.READ_MEALBOLUSRECORD]);//��ȡ��ʱ�������¼ ������
        attributes.put(READ_TIMEBOLUSRECORD, Function_tmpcode[CommandsFound.READ_TIMEBOLUSRECORD]);//��ȡ��ʱ�������¼ ������

        attributes.put(READ_SUSPENDEDPUMPRECORD, Function_tmpcode[CommandsFound.READ_SUSPENDEDPUMPRECORD]);//��ȡ��ͣ�ü�¼ ������
        attributes.put(READ_BASALRECORD, Function_tmpcode[CommandsFound.READ_BASALRECORD]);//��ȡ�����ʼ�¼ ������
        attributes.put(READ_TEMPORARYRECORD, Function_tmpcode[CommandsFound.READ_TEMPORARYRECORD]);//��ȡ��ʱ�ʼ�¼ ������

        attributes.put(READ_DAILYTOTALRECORD, Function_tmpcode[CommandsFound.READ_DAILYTOTALRECORD]);//��ȡ��������¼ ������
        attributes.put(READ_EVENTRECORD, Function_tmpcode[CommandsFound.READ_EVENTRECORD]);//��ȡ�¼���¼ ������
        attributes.put(READ_EXHAUSTRECORD, Function_tmpcode[CommandsFound.READ_EXHAUSTRECORD]);//��ȡ������¼ ������

    }

    static {      //�鴮ʱ ö��δʵ�֣��������滻Ϊ֡����  Frame_length

        Framelength.put(SET_FACTORYDATA, Frame_tmplength[CommandsFound.SET_FACTORYDATA]);//���ó������� ֡���� ʮ������
        Framelength.put(READ_FACTORYDATA, Frame_tmplength[CommandsFound.READ_FACTORYDATA]);//��ȡ�������� ֡����

        Framelength.put(SET_SYSSETDATA, Frame_tmplength[CommandsFound.SET_SYSSETDATA]);//����ϵͳ�������� ֡����
        Framelength.put(READ_SYSSETDATA, Frame_tmplength[CommandsFound.READ_SYSSETDATA]);//��ȡϵͳ�������� ֡����

        Framelength.put(SET_BASALDATA, Frame_tmplength[CommandsFound.SET_BASALDATA]);//���û��������� ֡����
        Framelength.put(READ_BASALDATA, Frame_tmplength[CommandsFound.READ_BASALDATA]);//��ȡ���������� ֡����

        Framelength.put(READ_RUNNINGDATA, Frame_tmplength[CommandsFound.READ_RUNNINGDATA]);//��ȡ����״̬���� ֡����

        Framelength.put(READ_MEALBOLUSRECORD, Frame_tmplength[CommandsFound.READ_MEALBOLUSRECORD]);//��ȡ��ʱ�������¼ ֡����
        Framelength.put(READ_TIMEBOLUSRECORD, Frame_tmplength[CommandsFound.READ_TIMEBOLUSRECORD]);//��ȡ��ʱ�������¼ ֡����

        Framelength.put(READ_SUSPENDEDPUMPRECORD, Frame_tmplength[CommandsFound.READ_SUSPENDEDPUMPRECORD]);//��ȡ��ͣ�ü�¼ ֡����
        Framelength.put(READ_BASALRECORD, Frame_tmplength[CommandsFound.READ_BASALRECORD]);//��ȡ�����ʼ�¼ ֡����
        Framelength.put(READ_TEMPORARYRECORD, Frame_tmplength[CommandsFound.READ_TEMPORARYRECORD]);//��ȡ��ʱ�ʼ�¼ ֡����

        Framelength.put(READ_DAILYTOTALRECORD, Frame_tmplength[CommandsFound.READ_DAILYTOTALRECORD]);//��ȡ��������¼ ֡����
        Framelength.put(READ_EVENTRECORD, Frame_tmplength[CommandsFound.READ_EVENTRECORD]);//��ȡ�¼���¼ ֡����
        Framelength.put(READ_EXHAUSTRECORD, Frame_tmplength[CommandsFound.READ_EXHAUSTRECORD]);//��ȡ������¼ ֡����

    }

    public static String lookup(String commandname, String defaultName) {
        String name = attributes.get(commandname);
        return name == null ? defaultName : name;
    }

    public static String lookup_framelength(String commandname, String defaultName) {
        String name = Framelength.get(commandname);
        return name == null ? defaultName : name;
    }
}