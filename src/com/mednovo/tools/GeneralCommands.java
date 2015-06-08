package com.mednovo.tools;

//import android.content.Context;

//import com.mednovo.mti_pii.R;
import java.util.HashMap;



public class GeneralCommands {//常用命令
    private static HashMap<String, String> attributes = new HashMap();

    private static HashMap<String, String> Framelength = new HashMap();

    //public static String SET_FACTORYDATA = "A8 19 00 30 31 30 31 31 30 33 33 32 30 30 30 31 00 00 20 06 00 00 8A A8 A2";
    //public static String READ_FACTORYDATA = "A8 08 01 00 00 16 78 A2";

    /*===============常用的查询设置命令=================*/
    public static String SET_FACTORYDATA = "设置出厂数据";
    public static String READ_FACTORYDATA = "读取出厂数据";
    public static String SET_SYSSETDATA = "设置系统设置数据";
    public static String READ_SYSSETDATA = "读取系统设置数据";
    public static String SET_BASALDATA = "设置基础率数据";
    public static String READ_BASALDATA = "读取基础率数据";
    public static String READ_RUNNINGDATA = "读取运行状态数据";
    public static String READ_MEALBOLUSRECORD = "读取即时大剂量记录";
    public static String READ_TIMEBOLUSRECORD = "读取定时大剂量记录";
    public static String READ_SUSPENDEDPUMPRECORD = "读取暂停泵记录";
    public static String READ_BASALRECORD = "读取基础率记录";
    public static String READ_TEMPORARYRECORD = "读取暂时率记录";
    public static String READ_DAILYTOTALRECORD = "读取日总量记录";
    public static String READ_EVENTRECORD = "读取事件记录";
    public static String READ_EXHAUSTRECORD = "读取排气记录";

    /*==================发送数据各个字节段定义
    * Start_bit         起始位 0xA8
    * Frame_length      帧长度
    * Function_code     功能码
    * Data_field        数据域
    * Control_code      控制码
    * CRC_check         CRC校验
    * End_bit           结束位 0xA2
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

    static {      //组串时 枚举未实现，将名称替换为功能码  Function_code
        //user
        //attributes.put(getResources().getString(R.string.Set_factorydata), "MedNovo MTI-PII");//设置出厂数据
        //attributes.put(Context.getString(R.string.Set_factorydata), "MedNovo MTI-PII");//设置出厂数据   Set factory data
        //attributes.put("设置出厂数据", "SET_FACTORYDATA");

        attributes.put(SET_FACTORYDATA, Function_tmpcode[CommandsFound.SET_FACTORYDATA]);//设置出厂数据 功能码 十六进制
        attributes.put(READ_FACTORYDATA, Function_tmpcode[CommandsFound.READ_FACTORYDATA]);//读取出厂数据 功能码

        attributes.put(SET_SYSSETDATA, Function_tmpcode[CommandsFound.SET_SYSSETDATA]);//设置系统设置数据 功能码
        attributes.put(READ_SYSSETDATA, Function_tmpcode[CommandsFound.READ_SYSSETDATA]);//读取系统设置数据 功能码

        attributes.put(SET_BASALDATA, Function_tmpcode[CommandsFound.SET_BASALDATA]);//设置基础率数据 功能码
        attributes.put(READ_BASALDATA, Function_tmpcode[CommandsFound.READ_BASALDATA]);//读取基础率数据 功能码

        attributes.put(READ_RUNNINGDATA, Function_tmpcode[CommandsFound.READ_RUNNINGDATA]);//读取运行状态数据 功能码

        attributes.put(READ_MEALBOLUSRECORD, Function_tmpcode[CommandsFound.READ_MEALBOLUSRECORD]);//读取即时大剂量记录 功能码
        attributes.put(READ_TIMEBOLUSRECORD, Function_tmpcode[CommandsFound.READ_TIMEBOLUSRECORD]);//读取定时大剂量记录 功能码

        attributes.put(READ_SUSPENDEDPUMPRECORD, Function_tmpcode[CommandsFound.READ_SUSPENDEDPUMPRECORD]);//读取暂停泵记录 功能码
        attributes.put(READ_BASALRECORD, Function_tmpcode[CommandsFound.READ_BASALRECORD]);//读取基础率记录 功能码
        attributes.put(READ_TEMPORARYRECORD, Function_tmpcode[CommandsFound.READ_TEMPORARYRECORD]);//读取暂时率记录 功能码

        attributes.put(READ_DAILYTOTALRECORD, Function_tmpcode[CommandsFound.READ_DAILYTOTALRECORD]);//读取日总量记录 功能码
        attributes.put(READ_EVENTRECORD, Function_tmpcode[CommandsFound.READ_EVENTRECORD]);//读取事件记录 功能码
        attributes.put(READ_EXHAUSTRECORD, Function_tmpcode[CommandsFound.READ_EXHAUSTRECORD]);//读取排气记录 功能码

    }

    static {      //组串时 枚举未实现，将名称替换为帧长度  Frame_length

        Framelength.put(SET_FACTORYDATA, Frame_tmplength[CommandsFound.SET_FACTORYDATA]);//设置出厂数据 帧长度 十六进制
        Framelength.put(READ_FACTORYDATA, Frame_tmplength[CommandsFound.READ_FACTORYDATA]);//读取出厂数据 帧长度

        Framelength.put(SET_SYSSETDATA, Frame_tmplength[CommandsFound.SET_SYSSETDATA]);//设置系统设置数据 帧长度
        Framelength.put(READ_SYSSETDATA, Frame_tmplength[CommandsFound.READ_SYSSETDATA]);//读取系统设置数据 帧长度

        Framelength.put(SET_BASALDATA, Frame_tmplength[CommandsFound.SET_BASALDATA]);//设置基础率数据 帧长度
        Framelength.put(READ_BASALDATA, Frame_tmplength[CommandsFound.READ_BASALDATA]);//读取基础率数据 帧长度

        Framelength.put(READ_RUNNINGDATA, Frame_tmplength[CommandsFound.READ_RUNNINGDATA]);//读取运行状态数据 帧长度

        Framelength.put(READ_MEALBOLUSRECORD, Frame_tmplength[CommandsFound.READ_MEALBOLUSRECORD]);//读取即时大剂量记录 帧长度
        Framelength.put(READ_TIMEBOLUSRECORD, Frame_tmplength[CommandsFound.READ_TIMEBOLUSRECORD]);//读取定时大剂量记录 帧长度

        Framelength.put(READ_SUSPENDEDPUMPRECORD, Frame_tmplength[CommandsFound.READ_SUSPENDEDPUMPRECORD]);//读取暂停泵记录 帧长度
        Framelength.put(READ_BASALRECORD, Frame_tmplength[CommandsFound.READ_BASALRECORD]);//读取基础率记录 帧长度
        Framelength.put(READ_TEMPORARYRECORD, Frame_tmplength[CommandsFound.READ_TEMPORARYRECORD]);//读取暂时率记录 帧长度

        Framelength.put(READ_DAILYTOTALRECORD, Frame_tmplength[CommandsFound.READ_DAILYTOTALRECORD]);//读取日总量记录 帧长度
        Framelength.put(READ_EVENTRECORD, Frame_tmplength[CommandsFound.READ_EVENTRECORD]);//读取事件记录 帧长度
        Framelength.put(READ_EXHAUSTRECORD, Frame_tmplength[CommandsFound.READ_EXHAUSTRECORD]);//读取排气记录 帧长度

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