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

        //======׼���鴮====================================//
        //======��ʼλ=======Start_bit ="A8"===============
        //======֡����=======Frame_length==================
        //======������=======Function_code=================
        //======������=======Data_field ="00"==============
        //======������=======Control_code ="00"============
        //======CRCУ����====CRC_check=====================
        //======����λ=======End_bit ="A2"=================
        switch (send_fmt_int){
            case CommandsFound.SET_FACTORYDATA:
            case CommandsFound.SET_SYSSETDATA:
            case CommandsFound.SET_BASALDATA:
                Log.v("zhq_log CommandsFound SET_FACTORYDATA  SET_SYSSETDATA SET_BASALDATA",
                        String.format("%d  %d  %d", CommandsFound.SET_FACTORYDATA, CommandsFound.SET_SYSSETDATA, CommandsFound.SET_BASALDATA));

                command.put("Frame_length",//֡����
                        GeneralCommands.lookup_framelength(commandname, "Frame_length"));
                //command.put("Commandname", commandname);//����
                GeneralCommands.Frame_length = command.get("Frame_length");
                Log.v("zhq_log ֡���� frame_length", "" + GeneralCommands.Frame_length);

                command.put("Function_code",//������
                        GeneralCommands.lookup(commandname, "Function_code"));
                //command.put("Commandname", commandname);//����
                GeneralCommands.Function_code = command.get("Function_code");
                Log.v("zhq_log ������ function_code",""+ GeneralCommands.Function_code);

                //GeneralCommands.Data_field = ;
                Log.v("zhq_log ������ Data_field",""+ GeneralCommands.Data_field);
                Log.v("zhq_log ������ Control_code",""+ GeneralCommands.Control_code);


                Data_TmpTransmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code;
                Log.v("zhq_log ���ݷ���Data_TmpTransmission = ","" + Data_TmpTransmission);

                GeneralCommands.CRC_check = CRC16.getCrc(Data_TmpTransmission);//CRCУ����
                Log.v("zhq_log CRC_check","" + GeneralCommands.CRC_check);

                Data_Transmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code + GeneralCommands.CRC_check
                        + GeneralCommands.End_bit;
                Log.v("zhq_log ���ݷ���Data_Transmission = ","" + Data_Transmission);
                break;
            default://��������
                command.put("Frame_length",//֡����
                        GeneralCommands.lookup_framelength(commandname, "Frame_length"));
                //command.put("Commandname", commandname);//����
                GeneralCommands.Frame_length = command.get("Frame_length");
                Log.v("zhq_log ֡���� frame_length",""+ GeneralCommands.Frame_length);

                command.put("Function_code",//������
                        GeneralCommands.lookup(commandname, "Function_code"));
                //command.put("Commandname", commandname);//����
                GeneralCommands.Function_code = command.get("Function_code");
                Log.v("zhq_log ������ function_code",""+ GeneralCommands.Function_code);

                GeneralCommands.Data_field = "00";
                Log.v("zhq_log ������ Data_field",""+ GeneralCommands.Data_field);

                if(send_fmt_int < 7) {
                    GeneralCommands.Control_code = contror_code;//������
                }else{
                    bytecontror_code = DectoBytes(contror_code);
                    Log.v("zhq_log ������ bytecontror_code", "" + bytecontror_code);
                    GeneralCommands.Control_code = bytesToHexString(bytecontror_code);//������
                }

                    Log.v("zhq_log ������ Control_code",""+ GeneralCommands.Control_code);

                Data_TmpTransmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                                            + GeneralCommands.Control_code;
                Log.v("zhq_log ���ݷ���Data_TmpTransmission = ","" + Data_TmpTransmission);

                GeneralCommands.CRC_check = CRC16.getCrc(Data_TmpTransmission);//CRCУ����
                Log.v("zhq_log CRC_check","" + GeneralCommands.CRC_check);

                Data_Transmission = GeneralCommands.Start_bit + GeneralCommands.Frame_length
                        + GeneralCommands.Function_code + GeneralCommands.Data_field
                        + GeneralCommands.Control_code + GeneralCommands.CRC_check
                        + GeneralCommands.End_bit;
                Log.v("zhq_log ���ݷ���Data_Transmission = ","" + Data_Transmission);
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
                    //=====================׼���鴮====================================//

                    command.put("Function_code",
                            GeneralCommands.lookup(commandname, "Function_code"));//������
                    //command.put("Commandname", commandname);//����
                    String function_code = command.get("Function_code");
                    Log.v("zhq_log function_code", "" + function_code);

                    command.put("Frame_length",
                            GeneralCommands.lookup_framelength(commandname, "Frame_length"));//֡����
                    //command.put("Commandname", commandname);//����
                    String frame_length = command.get("Frame_length");
                    Log.v("zhq_log frame_length", "" + frame_length);
                    //commands.add(command);

                    GeneralCommands.CRC_check = CRC16.getCrc("A808010000");
                    Log.v("zhq_log CRC_check", "" + GeneralCommands.CRC_check);

                    sendB = CRC16.getSendBuf("A808010000");//��ȡ��������У�� 16 78
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
                    sendB = CRC16.getSendBuf("A808110000");//��ȡϵͳ�������� 17 BD
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
                    sendB = CRC16.getSendBuf("A808130000");//��ȡ���������� B6 7D
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_RUNNINGDATA:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_RUNNINGDATA);
                if(false) {
                    sendB = CRC16.getSendBuf("A808140000");//��ȡ����״̬���� 07 BC
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_MEALBOLUSRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_MEALBOLUSRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808200001");//��ȡ��ʱ�������¼ 87 B2
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_TIMEBOLUSRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_TIMEBOLUSRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808210001");//��ȡ��ʱ�������¼ D6 72
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_SUSPENDEDPUMPRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_SUSPENDEDPUMPRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808220001");//��ȡ��ͣ�ü�¼ 26 72
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_BASALRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_BASALRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808230001");//��ȡ�����ʼ�¼ 77 B2
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_TEMPORARYRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_TEMPORARYRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808240001");//��ȡ��ʱ�ʼ�¼ C6 73
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_DAILYTOTALRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_DAILYTOTALRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808250001");//��ȡ��������¼ 97 B3
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_EVENTRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_EVENTRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808260001");//��ȡ�¼���¼ 67 B3
                    System.out.println(CRC16.getBufHexStr(sendB));
                    newsendB = CRC16.getBufHexStr(sendB);
                    newsendB += "a2";
                    Log.v("zhq_log newsendB", "" + newsendB);
                }
                break;
            case CommandsFound.READ_EXHAUSTRECORD:
                Log.v("zhq_log CommandsFound",""+ CommandsFound.READ_EVENTRECORD);
                if(false) {
                    sendB = CRC16.getSendBuf("A808270001");//��ȡ������¼ 36 73
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

    public static byte[] DectoBytes(String contror_code) {//����ġ��ڼ�����¼�������������룩ת��Ϊbyte����
        byte[] bytecontror_code = null;

        if (0 == contror_code.length())
            return null;

        int data_int = Integer.parseInt(contror_code);
        int byte_size = 0;

        if(data_int == 0){//�������Ϊ0
            byte_size = 1;
            bytecontror_code = new byte[byte_size];
            bytecontror_code[0] = (byte) (0xFF & (data_int % 256));
            return bytecontror_code;
        }//��Ϊ0ʱ  ����֤
        for (byte_size = 0; data_int != 0; byte_size++) { // ����ռ���ֽ���
            data_int /= 256;
        }

        bytecontror_code = new byte[byte_size];

        data_int = Integer.parseInt(contror_code);
        /*for (int i = 0; i < byte_size; i++) { // ת��
            bytecontror_code[i] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }//ת��������λ�ں�*/
        for (int i = byte_size; i > 0; i--) { // ת��
            bytecontror_code[i - 1] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }
        return bytecontror_code;
    }

    public static byte[] Expand_DectoBytes(String contror_code) {//����100��
        byte[] bytecontror_code = null;

        if (0 == contror_code.length())
            return null;

        int data_int = (int)((Float.parseFloat(contror_code))*100);
        int byte_size = 0;

        if(data_int == 0){
            byte_size = 1;
        }//��Ϊ0ʱ  ����֤
        for (byte_size = 0; data_int != 0; byte_size++) { // ����ռ���ֽ���
            data_int /= 256;
        }

        bytecontror_code = new byte[byte_size];

        data_int = (int)((Float.parseFloat(contror_code))*100);
        /*for (int i = 0; i < byte_size; i++) { // ת��
            bytecontror_code[i] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }//ת��������λ�ں�*/
        for (int i = byte_size; i > 0; i--) { // ת��
            bytecontror_code[i - 1] = (byte) (0xFF & (data_int % 256));
            data_int /= 256;
        }
        return bytecontror_code;
    }

    public static String bytesToHexString(byte[] contror_code) {//byte�͡��ڼ�����¼��ת��Ϊʮ������
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




