package com.mednovo.tools;

//import java.util.EnumMap;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTransmission{

    public static String Data_Transmission(String commandname, int send_fmt_int,String contror_code) {

        List<Map<String, String>> commands;
        String Data_Transmission = "";
        String Data_TmpTransmission = "";
        byte[] bytecontror_code = null;
        String newsendB = "";
        byte[] sendB = null;
        //commands = new ArrayList<Map<String, String>>();
        Map<String, String> command = new HashMap<String, String>();

        //======准备组串====================================//
        //======起始位=======Start_bit ="A8"===============
        //======帧长度=======Frame_length==================
        //======功能码=======Function_code=================
        //======数据域=======Data_field ="00"==============
        //======控制码=======Control_code ="00"============
        //======CRC校验码====CRC_check=====================
        //======结束位=======End_bit ="A2"=================
        switch (send_fmt_int){
            case CommandsFound.SET_FACTORYDATA:
            case CommandsFound.SET_SYSSETDATA:
            case CommandsFound.SET_BASALDATA:
                Log.v("zhq_log CommandsFound SET_FACTORYDATA  SET_SYSSETDATA SET_BASALDATA",
                        String.format("%d  %d  %d", CommandsFound.SET_FACTORYDATA, CommandsFound.SET_SYSSETDATA, CommandsFound.SET_BASALDATA));

                command.put("Frame_length",//帧长度
                        GeneralCommands.lookup_framelength(commandname, "Frame_length"));
                //command.put("Commandname", commandname);//命令
                GeneralCommands.Frame_length = command.get("Frame_length");
                Log.v("zhq_log 帧长度 frame_length", "" + GeneralCommands.Frame_length);

                command.put("Function_code",//功能码
                        GeneralCommands.lookup(commandname, "Function_code"));
                //command.put("Commandname", commandname);//命令
                GeneralCommands.Function_code = command.get("Function_code");
                Log.v("zhq_log 功能码 function_code",""+ GeneralCommands.Function_code);

                //GeneralCommands.Data_field = ;
                Log.v("zhq_log 数据域 Data_field",""+ GeneralCommands.Data_field);
                Log.v("zhq_log 控制码 Control_code",""+ GeneralCommands.Control_code);


                Data_TmpTransmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code;
                Log.v("zhq_log 数据发送Data_TmpTransmission = ","" + Data_TmpTransmission);

                GeneralCommands.CRC_check = CRC16.getCrc(Data_TmpTransmission);//CRC校验码
                Log.v("zhq_log CRC_check","" + GeneralCommands.CRC_check);

                Data_Transmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code + GeneralCommands.CRC_check
                        + GeneralCommands.End_bit;
                Log.v("zhq_log 数据发送Data_Transmission = ","" + Data_Transmission);
                break;
            default://发送数据
                command.put("Frame_length",//帧长度
                        GeneralCommands.lookup_framelength(commandname, "Frame_length"));
                //command.put("Commandname", commandname);//命令
                GeneralCommands.Frame_length = command.get("Frame_length");
                Log.v("zhq_log 帧长度 frame_length",""+ GeneralCommands.Frame_length);

                command.put("Function_code",//功能码
                        GeneralCommands.lookup(commandname, "Function_code"));
                //command.put("Commandname", commandname);//命令
                GeneralCommands.Function_code = command.get("Function_code");
                Log.v("zhq_log 功能码 function_code",""+ GeneralCommands.Function_code);

                GeneralCommands.Data_field = "00";
                Log.v("zhq_log 数据域 Data_field",""+ GeneralCommands.Data_field);

                if(send_fmt_int < 7) {
                    GeneralCommands.Control_code = contror_code;//控制码
                }else{
                    bytecontror_code = DectoBytes(contror_code);
                    Log.v("zhq_log 控制码 bytecontror_code", "" + bytecontror_code);
                    GeneralCommands.Control_code = bytesToHexString(bytecontror_code);//控制码
                }

                    Log.v("zhq_log 控制码 Control_code",""+ GeneralCommands.Control_code);

                Data_TmpTransmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                                            + GeneralCommands.Control_code;
                Log.v("zhq_log 数据发送Data_TmpTransmission = ","" + Data_TmpTransmission);

                GeneralCommands.CRC_check = CRC16.getCrc(Data_TmpTransmission);//CRC校验码
                Log.v("zhq_log CRC_check","" + GeneralCommands.CRC_check);

                Data_Transmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code + GeneralCommands.CRC_check
                        + GeneralCommands.End_bit;
                Log.v("zhq_log 数据发送Data_Transmission = ","" + Data_Transmission);
                break;
        }
        //commands.add(command);

        switch (send_fmt_int){
            case CommandsFound.SET_FACTORYDATA:
                Log.v("zhq_log CommandsFound SET_FACTORYDATA ",""+ CommandsFound.SET_FACTORYDATA);
                break;
            case CommandsFound.READ_FACTORYDATA:
                Log.v("zhq_log CommandsFound READ_FACTORYDATA ",""+ CommandsFound.READ_FACTORYDATA);
                if(false) {
                    //=====================准备组串====================================//

                    command.put("Function_code",
                            GeneralCommands.lookup(commandname, "Function_code"));//功能码
                    //command.put("Commandname", commandname);//命令
                    String function_code = command.get("Function_code");
                    Log.v("zhq_log function_code", "" + function_code);

                    command.put("Frame_length",
                            GeneralCommands.lookup_framelength(commandname, "Frame_length"));//帧长度
                    //command.put("Commandname", commandname);//命令
                    String frame_length = command.get("Frame_length");
                    Log.v("zhq_log frame_length", "" + frame_length);
                    //commands.add(command);

                    GeneralCommands.CRC_check = CRC16.getCrc("A808010000");
                    Log.v("zhq_log CRC_check", "" + GeneralCommands.CRC_check);

                    sendB = CRC16.getSendBuf("A808010000");//读取出厂数据校验 16 78
                    Log.v("zhq_log sendB", "" + sendB);
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.SET_SYSSETDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.SET_SYSSETDATA);
                break;
            case CommandsFound.READ_SYSSETDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_SYSSETDATA);
                if(false) {
                    sendB = CRC16.getSendBuf("A808110000");//读取系统设置数据 17 BD
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.SET_BASALDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.SET_BASALDATA);
                break;
            case CommandsFound.READ_BASALDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_BASALDATA);
                if(false) {
                    sendB = CRC16.getSendBuf("A808130000");//读取基础率数据 B6 7D
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_RUNNINGDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_RUNNINGDATA);
                if(false) {
                    sendB = CRC16.getSendBuf("A808140000");//读取运行状态数据 07 BC
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_MEALBOLUSRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_MEALBOLUSRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808200001");//读取即时大剂量记录 87 B2
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_TIMEBOLUSRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_TIMEBOLUSRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808210001");//读取定时大剂量记录 D6 72
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_SUSPENDEDPUMPRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_SUSPENDEDPUMPRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808220001");//读取暂停泵记录 26 72
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_BASALRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_BASALRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808230001");//读取基础率记录 77 B2
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_TEMPORARYRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_TEMPORARYRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808240001");//读取暂时率记录 C6 73
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_DAILYTOTALRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_DAILYTOTALRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808250001");//读取日总量记录 97 B3
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_EVENTRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_EVENTRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808260001");//读取事件记录 67 B3
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_EXHAUSTRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_EVENTRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808270001");//读取排气记录 36 73
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            default:
                break;
        }
        return Data_Transmission;//newsendB;
    }

    public static byte[] DectoBytes(String contror_code) {//输入的“第几条记录”（整数控制码）转换为byte类型
        byte[] bytecontror_code = null;

        if (0 == contror_code.length())
            return null;

        int data_int = Integer.parseInt(contror_code);
        int byte_size = 0;

        if(data_int == 0){//传入的数为0
            byte_size = 1;
            bytecontror_code = new byte[byte_size];
            bytecontror_code[0] = (byte) (0xFF & (data_int % 256));
            return bytecontror_code;
        }//当为0时  待验证
        for (byte_size = 0; data_int != 0; byte_size++) { // 计算占用字节数
            data_int /= 256;
        }

        bytecontror_code = new byte[byte_size];

        data_int = Integer.parseInt(contror_code);
        /*for (int i = 0; i < byte_size; i++) { // 转换
            bytecontror_code[i] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }//转换出来高位在后*/
        for (int i = byte_size; i > 0; i--) { // 转换
            bytecontror_code[i - 1] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }
        return bytecontror_code;
    }

    public static byte[] Expand_DectoBytes(String contror_code) {//扩大100倍
        byte[] bytecontror_code = null;

        if (0 == contror_code.length())
            return null;

        int data_int = (int)((Float.parseFloat(contror_code))*100);
        int byte_size = 0;

        if(data_int == 0){
            byte_size = 1;
        }//当为0时  待验证
        for (byte_size = 0; data_int != 0; byte_size++) { // 计算占用字节数
            data_int /= 256;
        }

        bytecontror_code = new byte[byte_size];

        data_int = (int)((Float.parseFloat(contror_code))*100);
        /*for (int i = 0; i < byte_size; i++) { // 转换
            bytecontror_code[i] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }//转换出来高位在后*/
        for (int i = byte_size; i > 0; i--) { // 转换
            bytecontror_code[i - 1] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }
        return bytecontror_code;
    }

    public static String bytesToHexString(byte[] contror_code) {//byte型“第几条记录”转换为十六进制
        String result = "";
        for (int i = 0; i < contror_code.length; i++) {
            String hexString = Integer.toHexString(contror_code[i] & 0xFF);

            if (hexString.length() == 1) {
                hexString = '0' + hexString;
                //hexString = hexString - '0';
            }
            result += hexString.toUpperCase();
        }
        return result;
    }
}




