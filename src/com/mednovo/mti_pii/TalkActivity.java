package com.mednovo.mti_pii;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
//import java.util.Map;
import java.util.UUID;

//import com.mednovo.tools.CommandsEnum;
//import com.mednovo.tools.CommandsNewEnum;
import com.mednovo.chart_myself.HorizontalBarChartActivity;
import com.mednovo.tools.CRC16;
import com.mednovo.tools.CommandsFound;
import com.mednovo.tools.DataTransmission;
import com.mednovo.tools.GeneralCommands;
import com.mednovo.tools.Tools;
import com.mednovo.mti_pii.R;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

public class TalkActivity extends Activity implements OnClickListener {

	private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.talk_activity);

		Intent intent = getIntent();
		if(Tools.mBLEService == null){
			Toast.makeText(getApplicationContext(), "�ѶϿ�����",
					Toast.LENGTH_LONG).show();
			Intent intent_return = new Intent(this, MainActivity.class);
			startActivity(intent_return);
			return;
		}
		mBluetoothGattCharacteristic = Tools.mBLEService.mBluetoothGatt
				.getServices().get(intent.getIntExtra("one", 0))
				.getCharacteristics().get(intent.getIntExtra("two", 0));

		setBroadcastReceiver(); // ���ù㲥����

		initView(); // ��ʼ���ؼ�
	}

	// ���ù㲥����
	private BroadcastReceiver bluetoothReceiver;

	private void setBroadcastReceiver() {
		// ����һ��IntentFilter���󣬽���actionָ��ΪBluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(BLEService.ACTION_DATA_CHANGE);
		intentFilter.addAction(BLEService.ACTION_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_RSSI_READ);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		intentFilter.addAction(BLEService.ACTION_STATE_DISCONNECTED);
		intentFilter.addAction(BLEService.ACTION_WRITE_OVER);
		bluetoothReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// ���ݸı�֪ͨ
				if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
					//dis_recive_msg(intent.getByteArrayExtra("value"));
					CRC16_recive_msg(intent.getByteArrayExtra("value"));//У����յ�CRC��ֵ
					//Analysis_recive_msg(intent.getByteArrayExtra("value"));
					if(((byte) 0xA2) == receive_bytes[CRC16_len-1]){//��ȡ����
						Analysis_all_recive_msg(receive_bytes);
					}
					return;
				}
				// ��ȡ����
				if (BLEService.ACTION_READ_OVER.equals(action)) {
					//if(" " == receive_bytes[0])
						dis_recive_msg(intent.getByteArrayExtra("value"));
					Analysis_all_recive_msg(receive_bytes);
					//Analysis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}

				// ����״̬�ı�
				if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
					talking_conect_flag_txt.setText("������");
				}
				if (BLEService.ACTION_STATE_DISCONNECTED.equals(action)) {
					Tools.mBLEService.disConectBle();
					talking_conect_flag_txt.setText("�ѶϿ�");
					Toast.makeText(getApplicationContext(), "�ѶϿ�����",
							Toast.LENGTH_LONG).show();
				}
			}

		};
		// ע��㲥������
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private void dis_recive_msg(byte[] tmp_byte) {
		if (talking_stopdis_btn.isChecked())
			return; // ֹͣ��ʾ

		String tmp = "";
		if (0 == tmp_byte.length) {
			return;
		}

		switch (read_fmt_int) {
		case 0: // �ַ�����ʾ
			try {
				//tmp = new String(tmp_byte, "GB2312");
                tmp = new String(tmp_byte, "utf-8");//�޸Ķ�ȡ��ʽΪutf-8��
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 1: // 16������ʾ
			for (int i = 0; i < tmp_byte.length; i++) {
				String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				tmp += ' ';
				tmp = tmp + hex;
			}
			break;
		case 2: // 10������ʾ
			int count = 0;
			for (int i = 0; i < tmp_byte.length; i++) {
				count *= 256;
				count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
			}
			tmp = Integer.toString(count);
			break;
		default:
			break;
		}

		//ChatMsgFmt entity2 = new ChatMsgFmt("Device", tmp, MESSAGE_FROM.OTHERS);
        ChatMsgFmt entity2 = new ChatMsgFmt("������", tmp, MESSAGE_FROM.OTHERS);
		chat_list.add(entity2);
		chat_list_adapter.notifyDataSetChanged();
	}

	/**
	 * @param tmp_byte
	 */
	private void CRC16_recive_msg(byte[] tmp_byte) {//��������
		if (talking_stopdis_btn.isChecked()) {
			return; // ֹͣ��ʾ
		}
		String tmp = "";

		if (0 == tmp_byte.length) {
			return;
		}

		if(false){//������ȡ����
			switch (read_fmt_int) {
				case 0: // �ַ�����ʾ
					try {
						//tmp = new String(tmp_byte, "GB2312");
						tmp = new String(tmp_byte, "utf-8");//�޸Ķ�ȡ��ʽΪutf-8��
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case 1: // 16������ʾ
					for (int i = 0; i < tmp_byte.length; i++) {
						String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
						if (hex.length() == 1) {
							hex = '0' + hex;
						}
						tmp += ' ';
						tmp = tmp + hex;
					}
					break;
				case 2: // 10������ʾ
					int count = 0;
					for (int i = 0; i < tmp_byte.length; i++) {
						count *= 256;
						count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
					}
					tmp = Integer.toString(count);
					break;
				default:
					break;
			}

			//ChatMsgFmt entity2 = new ChatMsgFmt("Device", tmp, MESSAGE_FROM.OTHERS);
			ChatMsgFmt entity2 = new ChatMsgFmt("������", tmp, MESSAGE_FROM.OTHERS);
			chat_list.add(entity2);
			chat_list_adapter.notifyDataSetChanged();
		}

		switch (send_fmt_int){
			case CommandsFound.SET_FACTORYDATA:
			case CommandsFound.SET_SYSSETDATA:
			case CommandsFound.SET_BASALDATA:

			case CommandsFound.READ_FACTORYDATA:
			case CommandsFound.READ_SYSSETDATA:
			case CommandsFound.READ_BASALDATA:
			case CommandsFound.READ_RUNNINGDATA:
			case CommandsFound.READ_MEALBOLUSRECORD:
			case CommandsFound.READ_TIMEBOLUSRECORD:
			case CommandsFound.READ_SUSPENDEDPUMPRECORD:
			case CommandsFound.READ_BASALRECORD:
			case CommandsFound.READ_TEMPORARYRECORD:
			case CommandsFound.READ_DAILYTOTALRECORD:
			case CommandsFound.READ_EVENTRECORD:
			case CommandsFound.READ_EXHAUSTRECORD:

				if(((byte) 0xA8) == tmp_byte[0]){
					Arrays.fill(receive_bytes, (byte) 0);//���
					System.arraycopy(tmp_byte,0,receive_bytes,0,tmp_byte.length);
					receive_len_total = byteToHexStringToDec(tmp_byte[1]);
					CRC16_len = tmp_byte.length;
					cycle_index = CRC16_len/20;
					if(0 == cycle_index && (((byte) 0xA2) == tmp_byte[tmp_byte.length - 1])){//���Ȳ���20�ֽ�
						System.arraycopy(tmp_byte,tmp_byte.length - 3, CRC16_bytes,0,2);
						if(CRC16.checkBuf(receive_bytes,CRC16_len)){
							System.out.print("zhq_log CRC16У����ȷ��");
						}
					}
				}else{//���ݴ���20���ֽ�
					CRC16_len = CRC16_len + tmp_byte.length;
					cycle_index = (CRC16_len - 1)/20;
					System.arraycopy(tmp_byte, 0, receive_bytes, 20*cycle_index, tmp_byte.length);
					System.arraycopy(tmp_byte,tmp_byte.length - 3, CRC16_bytes,0,2);//��ȡCRCУ��ֵ
					if(((byte) 0xA2) == tmp_byte[tmp_byte.length - 1]){//���ݽ������
						if(CRC16.checkBuf(receive_bytes,CRC16_len)){//��֤CRC
							System.out.print("zhq_log CRC16У����ȷ��");
						}
					}
				}
				break;
			default:
				break;
		}
	}

	private void Analysis_recive_msg(byte[] tmp_byte) {//��������
		if (talking_stopdis_btn.isChecked())
			return; // ֹͣ��ʾ

		String tmp = "";
		String receive = "";

		if (0 == tmp_byte.length) {
			return;
		}


		String Serial_Num_txt = "";
		String CompensationStepNum_txt = "";
		String Driving_scheme_txt = "";
		String Open_Bluetooth_txt = "";
		String version_software_txt = "";
		String Concentration_change_txt = "";

		final byte[] tmp_Serial_Num = new byte[13];
		final byte[] tmp_CompensationStepNum = new byte[2];
		final byte[] tmp_Driving_scheme = new byte[1];
		final byte[] tmp_version_software = new byte[6];

		switch (send_fmt_int){
			case CommandsFound.READ_FACTORYDATA:
				if(((byte) 0xA8) == tmp_byte[0]){
					System.arraycopy(tmp_byte,3,tmp_Serial_Num,0,13);
					System.arraycopy(tmp_byte,17,tmp_CompensationStepNum,0,2);
					System.arraycopy(tmp_byte, 19, tmp_Driving_scheme, 0, 1);
					try {
						Serial_Num_txt = new String(tmp_Serial_Num,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					CompensationStepNum_txt = bytesToHexStringToDecString(tmp_CompensationStepNum);
					Driving_scheme_txt = bytesToHexStringToDecString(tmp_Driving_scheme);

					Serial_Num.setText(Serial_Num_txt.toCharArray(), 0, Serial_Num_txt.length());
					CompensationStepNum.setText(CompensationStepNum_txt.toCharArray(), 0, CompensationStepNum_txt.length());
					Driving_scheme.setText(Driving_scheme_txt.toCharArray(),0,Driving_scheme_txt.length());

					if(((byte) 0x00) == tmp_byte[16]){//��������
						//Open_Bluetooth.setChecked(true);
						Open_Bluetooth_txt = "����������";
						Open_Bluetooth_Read.setText("��");
					}
					else{
						//Open_Bluetooth.setChecked(false);
						Open_Bluetooth_txt = "��ֹ��������";
						Open_Bluetooth_Read.setText("��");
					}
					receive = "��Ʒ���к�  "+ Serial_Num_txt +"\r\n"
							+ "��������  " + CompensationStepNum_txt + "\r\n"
							+ "��������  " + Driving_scheme_txt + "\r\n"
							+ Open_Bluetooth_txt;

				}else{
					if(((byte) 0x00) == tmp_byte[0]){//����Ũ��
						//Open_Bluetooth.setChecked(true);
						Concentration_change_txt = "�������Ũ��";
						Concentration_change_read.setText("����");
					}
					else{
						//Open_Bluetooth.setChecked(false);
						Concentration_change_txt = "��ֹ����Ũ��";
						Concentration_change_read.setText("��ֹ");
					}
					System.arraycopy(tmp_byte, 1, tmp_version_software, 0, 6);
					try {
						version_software_txt = new String(tmp_version_software,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					version_software.setText(version_software_txt.toCharArray(),0,version_software_txt.length());
					receive = Concentration_change_txt + "\r\n" + "����汾  " + version_software_txt;
				}
				break;
			case CommandsFound.READ_SYSSETDATA:
				break;
			default:
				break;
		}


		switch (read_fmt_int) {
			case 0: // �ַ�����ʾ
				try {
					//tmp = new String(tmp_byte, "GB2312");
					tmp = new String(tmp_byte, "utf-8");//�޸Ķ�ȡ��ʽΪutf-8��
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1: // 16������ʾ
				for (int i = 0; i < tmp_byte.length; i++) {
					String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
					if (hex.length() == 1) {
						hex = '0' + hex;
					}
					tmp += ' ';
					tmp = tmp + hex;
				}
				break;
			case 2: // 10������ʾ
				int count = 0;
				for (int i = 0; i < tmp_byte.length; i++) {
					count *= 256;
					count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
				}
				tmp = Integer.toString(count);
				break;
			default:
				break;
		}

		//ChatMsgFmt entity2 = new ChatMsgFmt("Device", tmp, MESSAGE_FROM.OTHERS);
		ChatMsgFmt entity2 = new ChatMsgFmt("������", tmp, MESSAGE_FROM.OTHERS);
		//ChatMsgFmt entity2 = new ChatMsgFmt("������", receive, MESSAGE_FROM.OTHERS);
		chat_list.add(entity2);
		chat_list_adapter.notifyDataSetChanged();

	}



	private void Analysis_all_recive_msg(byte[] tmp_byte) {//��������
		if (talking_stopdis_btn.isChecked()) {
			return; // ֹͣ��ʾ
		}

		String tmp = "";
		String receive = "";

		if (0 == tmp_byte.length) {
			return;
		}

		/**
		 * @param �������ݶ���
		 */
		String Serial_Num_txt = "";
		String CompensationStepNum_txt = "";
		String Driving_scheme_txt = "";
		String Open_Bluetooth_txt = "";
		String version_software_txt = "";
		String Concentration_change_txt = "";

		byte[] tmp_Serial_Num = new byte[13];
		byte[] tmp_CompensationStepNum = new byte[2];
		byte[] tmp_Driving_scheme = new byte[1];
		byte[] tmp_version_software = new byte[6];

		/**
		 * @param ϵͳ�������ݶ���
		 */
		String Max_meal_bolus_txt = "";
		String Max_basal_rate_txt = "";
		String Max_hours_bolus_hours_txt = "";
		String Max_hours_bolus_dosevalue_txt = "";
		String Max_daily_total_txt = "";
		String System_flag = "";
		String System_year_txt = "";
		String System_month_txt = "";
		String System_day_txt = "";
		String System_hour_txt = "";
		String System_min_txt = "";

		final byte[] tmp_Max_meal_bolus = new byte[2];
		final byte[] tmp_Max_basal_rate = new byte[2];
		final byte[] tmp_Max_hours_bolus_hours = new byte[2];
		final byte[] tmp_Max_hours_bolus_dosevalue = new byte[2];
		final byte[] tmp_Max_daily_total = new byte[2];
		final byte[] tmp_Concentration_set = new byte[1];
		//final byte[] tmp_System_flag = new byte[1];
		final byte[] tmp_System_time = new byte[5];

		/**
		 * @param ���û��������ݶ���
		 */
		final int[] R_id = new int[]{
				R.id.hour_value1,R.id.hour_value2,R.id.hour_value3,R.id.hour_value4,R.id.hour_value5,R.id.hour_value6,
				R.id.hour_value7,R.id.hour_value8,R.id.hour_value9,R.id.hour_value10,R.id.hour_value11,R.id.hour_value12,
				R.id.hour_value13,R.id.hour_value14,R.id.hour_value15,R.id.hour_value16,R.id.hour_value17,R.id.hour_value18,
				R.id.hour_value19,R.id.hour_value20,R.id.hour_value21,R.id.hour_value22,R.id.hour_value23,R.id.hour_value24
		};

		/**
		 * @param ����״̬���ݶ���
		 */
		String Basal_Infusion_value = "";
		String Temporary_Infusion_value = "";
		String Temporary_Infusion_hour_start = "";
		String Temporary_Infusion_min_start = "";
		String Temporary_Infusion_hour_end = "";
		String Temporary_Infusion_min_end = "";
		String Temporary_set = "";
		String Meal_bolus_Infusion_value = "";
		String Time_bolus_Infusion_value = "";
		String Time_bolus_Infusion_hour_start = "";
		String Time_bolus_Infusion_hour_end = "";
		String Time_bolus_Infusion_min_start = "";
		String Time_bolus_Infusion_min_end = "";
		String Time_bolus_Infusion_set_value = "";
		String Cumulative_input = "";
		String Power = "";

		final byte[] tmp_Basal_Infusion_value = new byte[2];
		final byte[] tmp_Temporary_Infusion_value = new byte[2];
		final byte[] tmp_Temporary_Infusion_time = new byte[4];
		final byte[] tmp_Temporary_set = new byte[1];

		final byte[] tmp_Meal_bolus_Infusion_value = new byte[2];
		final byte[] tmp_Time_bolus_Infusion_value = new byte[2];
		final byte[] tmp_Time_bolus_Infusion_time = new byte[4];
		final byte[] tmp_Time_bolus_Infusion_set_value = new byte[2];
		final byte[] tmp_Cumulative_input = new byte[2];
		final byte[] tmp_Power = new byte[1];


		/**
		 * @param ��ʱ�������¼����
		 */
		String Mealbolusrecord_total = "";
		String Mealbolusrecord_num = "";
		String Mealbolusrecord_year = "";
		String Mealbolusrecord_month = "";
		String Mealbolusrecord_day = "";
		String Mealbolusrecord_hour = "";
		String Mealbolusrecord_min = "";
		String Mealbolusrecord_value = "";

		final byte[] tmp_Mealbolusrecord_total = new byte[1];
		final byte[] tmp_Mealbolusrecord_num = new byte[1];
		final byte[] tmp_Mealbolusrecord_time = new byte[5];
		final byte[] tmp_Mealbolusrecord_value = new byte[2];

		/**
		 * @param ��ʱ�������¼����
		 */
		String Timebolusrecord_total = "";
		String Timebolusrecord_num = "";
		String Timebolusrecord_year = "";
		String Timebolusrecord_month = "";
		String Timebolusrecord_day = "";
		String Timebolusrecord_hour_start = "";
		String Timebolusrecord_min_start = "";
		String Timebolusrecord_hour_end = "";
		String Timebolusrecord_min_end = "";
		String Timebolusrecord_value = "";

		final byte[] tmp_Timebolusrecord_total = new byte[1];
		final byte[] tmp_Timebolusrecord_num = new byte[1];
		final byte[] tmp_Timebolusrecord_time = new byte[7];
		final byte[] tmp_Timebolusrecord_value = new byte[2];

		/**
		 * @param ��ͣ�ü�¼����
		 */
		String Suspendpumprecord_total = "";
		String Suspendpumprecord_num = "";
		String Suspendpumprecord_year = "";
		String Suspendpumprecord_month = "";
		String Suspendpumprecord_day = "";
		String Suspendpumprecord_hour = "";
		String Suspendpumprecord_min = "";

		final byte[] tmp_Suspendpumprecord_total = new byte[1];
		final byte[] tmp_Suspendpumprecord_num = new byte[1];
		final byte[] tmp_Suspendpumprecord_time = new byte[5];

		/**
		 * @param �����ʼ�¼����
		 */
		String Basalrecord_total = "";
		String Basalrecord_num = "";
		String Basalrecord_year = "";
		String Basalrecord_month = "";
		String Basalrecord_day = "";

		String[] Basalrecord_tmp_value = new String[100];
		final   String BasalHour_tmp[] = {"ʱ��00��01: ","ʱ��01��02: ","ʱ��02��03: ","ʱ��03��04: ",
											"ʱ��04��05: ","ʱ��05��06: ","ʱ��06��07: ","ʱ��07��08: ",
											"ʱ��08��09: ","ʱ��09��10: ","ʱ��10��11: ","ʱ��11��12: ",
											"ʱ��12��13: ","ʱ��13��14: ","ʱ��14��15: ","ʱ��15��16: ",
											"ʱ��16��17: ","ʱ��17��18: ","ʱ��18��19: ","ʱ��19��20: ",
											"ʱ��20��21: ","ʱ��21��22: ","ʱ��22��23: ","ʱ��23��24: "};

		final byte[] tmp_Basalrecord_total = new byte[1];
		final byte[] tmp_Basalrecord_num = new byte[1];
		final byte[] tmp_Basalrecord_time = new byte[3];
		final byte[] tmp_Basalrecord_value = new byte[48];

		/**
		 * @param ��ȡ���������ݶ���
		 */

		final byte[] tmp_BasalData_total = new byte[1];
		final byte[] tmp_BasalData_value = new byte[48];
		String[] BasalData_tmp_value = new String[100];


		/**
		 * @param ��ʱ�ʼ�¼����
		 */
		String Temporaryrecord_total = "";
		String Temporaryrecord_num = "";
		String Temporaryrecord_year = "";
		String Temporaryrecord_month = "";
		String Temporaryrecord_day = "";
		String Temporaryrecord_hour_start = "";
		String Temporaryrecord_min_start = "";
		String Temporaryrecord_hour_end = "";
		String Temporaryrecord_min_end = "";
		String Temporaryrecord_value = "";

		final byte[] tmp_Temporaryrecord_total = new byte[1];
		final byte[] tmp_Temporaryrecord_num = new byte[1];
		final byte[] tmp_Temporaryrecord_time = new byte[7];
		final byte[] tmp_Temporaryrecord_value = new byte[1];

		/**
		 * @param ��������¼����
		 */
		String Dailytotalrecord_total = "";
		String Dailytotalrecord_num = "";
		String Dailytotalrecord_year = "";
		String Dailytotalrecord_month = "";
		String Dailytotalrecord_day = "";
		String Dailytotalrecord_value = "";

		final byte[] tmp_Dailytotalrecord_total = new byte[1];
		final byte[] tmp_Dailytotalrecord_num = new byte[1];
		final byte[] tmp_Dailytotalrecord_time = new byte[3];
		final byte[] tmp_Dailytotalrecord_value = new byte[2];

		/**
		 * @param �¼���¼����
		 */
		String Eventrecord_total = "";
		String Eventrecord_num = "";
		String Eventrecord_year = "";
		String Eventrecord_month = "";
		String Eventrecord_day = "";
		String Eventrecord_hour = "";
		String Eventrecord_min = "";

		final byte[] tmp_Eventrecord_total = new byte[1];
		final byte[] tmp_Eventrecord_num = new byte[1];
		final byte[] tmp_Eventrecord_time = new byte[5];

		/**
		 * @param ������¼����
		 */
		String Exhaustrecord_total = "";
		String Exhaustrecord_num = "";
		String Exhaustrecord_year = "";
		String Exhaustrecord_month = "";
		String Exhaustrecord_day = "";
		String Exhaustrecord_hour = "";
		String Exhaustrecord_min = "";
		String Exhaustrecord_value = "";

		final byte[] tmp_Exhaustrecord_total = new byte[1];
		final byte[] tmp_Exhaustrecord_num = new byte[1];
		final byte[] tmp_Exhaustrecord_time = new byte[5];
		final byte[] tmp_Exhaustrecord_value = new byte[2];


		switch (send_fmt_int){
			case CommandsFound.SET_FACTORYDATA:
				if(CRC16_len == 0x08){//��Ӧ���ó�������
					if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ) {
						if(((byte) 0x00) == tmp_byte[3]){//���ؽ��
							receive = "���óɹ�";
						}else{
							receive = "����ʧ��";
						}
					}
				}else {//��ѯ������������
					if ((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1])) {
						System.arraycopy(tmp_byte, 3, tmp_Serial_Num, 0, 13);
						System.arraycopy(tmp_byte, 17, tmp_CompensationStepNum, 0, 2);
						System.arraycopy(tmp_byte, 19, tmp_Driving_scheme, 0, 1);
						try {
							Serial_Num_txt = new String(tmp_Serial_Num, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						CompensationStepNum_txt = bytesToHexStringToDecString(tmp_CompensationStepNum);
						Driving_scheme_txt = bytesToHexStringToDecString(tmp_Driving_scheme);

						Serial_Num.setText(Serial_Num_txt.toCharArray(), 0, Serial_Num_txt.length());
						CompensationStepNum.setText(CompensationStepNum_txt.toCharArray(), 0, CompensationStepNum_txt.length());
						Driving_scheme.setText(Driving_scheme_txt.toCharArray(), 0, Driving_scheme_txt.length());

						if (((byte) 0x00) == tmp_byte[16]) {//��������
							Open_Bluetooth.setChecked(true);
						} else {
							Open_Bluetooth.setChecked(false);
						}

						if (((byte) 0x00) == tmp_byte[20]) {//����Ũ��
							Concentration_change.setChecked(true);
						} else {
							Concentration_change.setChecked(false);
						}

						System.arraycopy(tmp_byte, 21, tmp_version_software, 0, 6);
						try {
							version_software_txt = new String(tmp_version_software, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						version_software.setText(version_software_txt.toCharArray(), 0, version_software_txt.length());
						receive = "��ȡĬ��ֵ�ɹ���";
					}
				}
				break;
			case CommandsFound.SET_SYSSETDATA:
				if(CRC16_len == 0x08){//��Ӧ����ϵͳ����
					if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ) {
						if(((byte) 0x00) == tmp_byte[3]){//���ؽ��
							receive = "���óɹ�";
						}else{
							receive = "����ʧ��";
						}
					}
				}else {//��ѯϵͳ��������
					if ((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1])) {
						System.arraycopy(tmp_byte,3,tmp_Max_meal_bolus,0,2);
						System.arraycopy(tmp_byte,5,tmp_Max_basal_rate,0,2);
						System.arraycopy(tmp_byte,7,tmp_Max_hours_bolus_hours,0,2);
						System.arraycopy(tmp_byte,9,tmp_Max_hours_bolus_dosevalue,0,2);
						System.arraycopy(tmp_byte,11,tmp_Max_daily_total,0,2);
						System.arraycopy(tmp_byte,13,tmp_Concentration_set,0,1);
						//System.arraycopy(tmp_byte,14,tmp_System_flag,0,1);
						System.arraycopy(tmp_byte, 15, tmp_System_time, 0, 5);

						Max_meal_bolus_txt = tmp_two_decimal_places(tmp_Max_meal_bolus);
						Max_basal_rate_txt = tmp_two_decimal_places(tmp_Max_basal_rate);
						Max_hours_bolus_hours_txt = bytesToHexStringToDecString(tmp_Max_hours_bolus_hours);
						Max_hours_bolus_dosevalue_txt = tmp_two_decimal_places(tmp_Max_hours_bolus_dosevalue);
						Max_daily_total_txt = tmp_two_decimal_places(tmp_Max_daily_total);

						max_meal_bolus.setText(Max_meal_bolus_txt);
						//max_meal_bolus.setText(Max_meal_bolus_txt.toCharArray(), 0, Max_meal_bolus_txt.length());
						max_basal_rate.setText(Max_basal_rate_txt);
						max_hours_bolus_Hours.setText(Max_hours_bolus_hours_txt);
						max_hours_bolus_Dose_value.setText(Max_hours_bolus_dosevalue_txt);
						max_daily_total.setText(Max_daily_total_txt);


						//System_flag = byteToBit(tmp_byte[14]);
						System_flag = system_set_status(tmp_byte[14]);

						System_year_txt = byteToHexStringToDecString(tmp_System_time[0]);
						System_month_txt = byteToHexStringToDecString(tmp_System_time[1]);
						System_day_txt = byteToHexStringToDecString(tmp_System_time[2]);
						System_hour_txt = byteToHexStringToDecString(tmp_System_time[3]);
						System_min_txt = byteToHexStringToDecString(tmp_System_time[4]);

						System_year_txt = tmp_add_zero(System_year_txt);//��λ������
						System_month_txt = tmp_add_zero(System_month_txt);//��λ������
						System_day_txt = tmp_add_zero(System_day_txt);
						System_hour_txt = tmp_add_zero(System_hour_txt);
						System_min_txt = tmp_add_zero(System_min_txt);

						time_set.setText(String.format("%s-%s-%s %s:%s",
								System_year_txt, System_month_txt,System_day_txt,
								System_hour_txt,System_min_txt));

						/*receive = "�������  "+ Max_meal_bolus_txt + " U" + "\r\n"
								+ "��������  " + Max_basal_rate_txt + " U/h" + "\r\n"
								+ "���ʱ����  "
								+ Max_hours_bolus_hours_txt + " Сʱ " + Max_hours_bolus_dosevalue_txt + "  U" + "\r\n"
								+ "���������  " + Max_daily_total_txt + " U" + "\r\n";*/

						if(((byte) 0x00) == tmp_byte[13]){//Ũ���趨
							Concentration_set.setText("U40");
							receive = receive + "Ũ���趨�� U40" + "\r\n";
						}else{
							Concentration_set.setText("U100");
							receive = receive + "Ũ���趨�� U100" + "\r\n";
						}

						/*receive = receive + System_flag
								+ "����  " + System_year_txt + "-"
								+	System_month_txt + "-"
								+ System_day_txt + "\r\n"
								+ "ʱ��  " + System_hour_txt + ":" + System_min_txt;*/
						receive = "��ȡĬ��ֵ�ɹ���";
					}
				}
				break;
			case CommandsFound.SET_BASALDATA:
				if(CRC16_len == 0x08){//��Ӧ���û���������
					if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ) {
						if(((byte) 0x00) == tmp_byte[3]){//���ؽ��
							receive = "���óɹ�";
						}else{
							receive = "����ʧ��";
						}
					}
				}else {//��ѯ��������������
					if ((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1])) {//���ݽ�������
						System.arraycopy(tmp_byte, 3, tmp_BasalData_value, 0, 48);

						//receive = "����ֵ" + "\r\n";
						for(int i=0;i<48;i+=2){
							BasalData_tmp_value[i/2] = twobytesTodecimal_places(tmp_BasalData_value[i],tmp_BasalData_value[i+1]);
							//receive = receive + BasalHour_tmp[i/2] + BasalData_tmp_value[i/2] + " U/h" + "\r\n";
							GeneralCommands.BasalData_value[i/2] = twobytesTodecimal_places(tmp_BasalData_value[i],tmp_BasalData_value[i+1]);
							hour_value[i/2].setText(BasalData_tmp_value[i/2]);
						}
						/*hour_value1.setText(BasalData_tmp_value[0]);
						hour_value2.setText(BasalData_tmp_value[1]);
						hour_value3.setText(BasalData_tmp_value[2]);
						hour_value4.setText(BasalData_tmp_value[3]);
						hour_value5.setText(BasalData_tmp_value[4]);
						hour_value6.setText(BasalData_tmp_value[5]);
						hour_value7.setText(BasalData_tmp_value[6]);
						hour_value8.setText(BasalData_tmp_value[7]);
						hour_value9.setText(BasalData_tmp_value[8]);
						hour_value10.setText(BasalData_tmp_value[9]);
						hour_value11.setText(BasalData_tmp_value[10]);
						hour_value12.setText(BasalData_tmp_value[11]);
						hour_value13.setText(BasalData_tmp_value[12]);
						hour_value14.setText(BasalData_tmp_value[13]);
						hour_value15.setText(BasalData_tmp_value[14]);
						hour_value16.setText(BasalData_tmp_value[15]);
						hour_value17.setText(BasalData_tmp_value[16]);
						hour_value18.setText(BasalData_tmp_value[17]);
						hour_value19.setText(BasalData_tmp_value[18]);
						hour_value20.setText(BasalData_tmp_value[19]);
						hour_value21.setText(BasalData_tmp_value[20]);
						hour_value22.setText(BasalData_tmp_value[21]);
						hour_value23.setText(BasalData_tmp_value[22]);
						hour_value24.setText(BasalData_tmp_value[23]);
*/
						receive = "��ȡĬ��ֵ�ɹ���";
						Log.v("zhq_log  receive ","" + receive);
					}
				}
				break;
			case CommandsFound.READ_FACTORYDATA:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){
					System.arraycopy(tmp_byte,3,tmp_Serial_Num,0,13);
					System.arraycopy(tmp_byte,17,tmp_CompensationStepNum,0,2);
					System.arraycopy(tmp_byte,19,tmp_Driving_scheme,0,1);
					try {
						Serial_Num_txt = new String(tmp_Serial_Num,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					CompensationStepNum_txt = bytesToHexStringToDecString(tmp_CompensationStepNum);
					Driving_scheme_txt = bytesToHexStringToDecString(tmp_Driving_scheme);

					Serial_Num.setText(Serial_Num_txt.toCharArray(), 0, Serial_Num_txt.length());
					CompensationStepNum.setText(CompensationStepNum_txt.toCharArray(), 0, CompensationStepNum_txt.length());
					Driving_scheme.setText(Driving_scheme_txt.toCharArray(),0,Driving_scheme_txt.length());

					if(((byte) 0x00) == tmp_byte[16]){//��������
						//Open_Bluetooth.setChecked(true);
						Open_Bluetooth_txt = "����������";
						Open_Bluetooth_Read.setText("��");
					}else{
						//Open_Bluetooth.setChecked(false);
						Open_Bluetooth_txt = "��ֹ��������";
						Open_Bluetooth_Read.setText("��");
					}
					receive = "��Ʒ���к�  "+ Serial_Num_txt +"\r\n"
							+ "��������  " + CompensationStepNum_txt + "\r\n"
							+ "��������  " + Driving_scheme_txt + "\r\n"
							+ Open_Bluetooth_txt + "\r\n";

					if(((byte) 0x00) == tmp_byte[20]){//����Ũ��
						//Open_Bluetooth.setChecked(true);
						Concentration_change_txt = "�������Ũ��";
						Concentration_change_read.setText("����");
					}else{
						//Open_Bluetooth.setChecked(false);
						Concentration_change_txt = "��ֹ����Ũ��";
						Concentration_change_read.setText("��ֹ");
					}

					System.arraycopy(tmp_byte, 21, tmp_version_software, 0, 6);
					try {
						version_software_txt = new String(tmp_version_software,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					version_software.setText(version_software_txt.toCharArray(),0,version_software_txt.length());

					receive = receive + Concentration_change_txt + "\r\n" + "����汾  " + version_software_txt;
				}
				break;
			case CommandsFound.READ_SYSSETDATA:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){
					System.arraycopy(tmp_byte,3,tmp_Max_meal_bolus,0,2);
					System.arraycopy(tmp_byte,5,tmp_Max_basal_rate,0,2);
					System.arraycopy(tmp_byte,7,tmp_Max_hours_bolus_hours,0,2);
					System.arraycopy(tmp_byte,9,tmp_Max_hours_bolus_dosevalue,0,2);
					System.arraycopy(tmp_byte,11,tmp_Max_daily_total,0,2);
					System.arraycopy(tmp_byte,13,tmp_Concentration_set,0,1);
					//System.arraycopy(tmp_byte,14,tmp_System_flag,0,1);
					System.arraycopy(tmp_byte, 15, tmp_System_time, 0, 5);

					Max_meal_bolus_txt = tmp_two_decimal_places(tmp_Max_meal_bolus);
					Max_basal_rate_txt = tmp_two_decimal_places(tmp_Max_basal_rate);
					Max_hours_bolus_hours_txt = bytesToHexStringToDecString(tmp_Max_hours_bolus_hours);
					Max_hours_bolus_dosevalue_txt = tmp_two_decimal_places(tmp_Max_hours_bolus_dosevalue);
					Max_daily_total_txt = tmp_two_decimal_places(tmp_Max_daily_total);

					//System_flag = byteToBit(tmp_byte[14]);
					System_flag = system_set_status(tmp_byte[14]);

					System_year_txt = byteToHexStringToDecString(tmp_System_time[0]);
					System_month_txt = byteToHexStringToDecString(tmp_System_time[1]);
					System_day_txt = byteToHexStringToDecString(tmp_System_time[2]);
					System_hour_txt = byteToHexStringToDecString(tmp_System_time[3]);
					System_min_txt = byteToHexStringToDecString(tmp_System_time[4]);

					System_year_txt = tmp_add_zero(System_year_txt);//��λ������
					System_month_txt = tmp_add_zero(System_month_txt);//��λ������
					System_day_txt = tmp_add_zero(System_day_txt);
					System_hour_txt = tmp_add_zero(System_hour_txt);
					System_min_txt = tmp_add_zero(System_min_txt);

					receive = "�������  "+ Max_meal_bolus_txt + " U" + "\r\n"
							+ "��������  " + Max_basal_rate_txt + " U/h" + "\r\n"
							+ "���ʱ����  "
							+ Max_hours_bolus_hours_txt + " Сʱ " + Max_hours_bolus_dosevalue_txt + "  U" + "\r\n"
							+ "���������  " + Max_daily_total_txt + " U" + "\r\n";

					if(((byte) 0x00) == tmp_byte[13]){//Ũ���趨
						receive = receive + "Ũ���趨�� U40" + "\r\n";
					}else{
						receive = receive + "Ũ���趨�� U100" + "\r\n";
					}

					receive = receive + System_flag
							+ "����  " + System_year_txt + "-"
							+	System_month_txt + "-"
							+ System_day_txt + "\r\n"
							+ "ʱ��  " + System_hour_txt + ":" + System_min_txt;
				}
				break;
			case CommandsFound.READ_BASALDATA:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					{
						System.arraycopy(tmp_byte, 3, tmp_BasalData_value, 0, 48);

						receive = "����ֵ" + "\r\n";
						for(int i=0;i<48;i+=2){
							BasalData_tmp_value[i/2] = twobytesTodecimal_places(tmp_BasalData_value[i],tmp_BasalData_value[i+1]);
							GeneralCommands.BasalData_value[i/2] = twobytesTodecimal_places(tmp_BasalData_value[i],tmp_BasalData_value[i+1]);
							receive = receive + BasalHour_tmp[i/2] + BasalData_tmp_value[i/2] + " U/h" + "\r\n";
						}
						Log.v("zhq_log  receive ","" + receive);

					}
				}
				break;
			case CommandsFound.READ_RUNNINGDATA:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){
					System.arraycopy(tmp_byte,3,tmp_Basal_Infusion_value,0,2);
					System.arraycopy(tmp_byte,5,tmp_Temporary_Infusion_value,0,2);
					System.arraycopy(tmp_byte,7,tmp_Temporary_Infusion_time,0,4);
					System.arraycopy(tmp_byte,11,tmp_Temporary_set,0,1);
					System.arraycopy(tmp_byte,12,tmp_Meal_bolus_Infusion_value,0,2);
					System.arraycopy(tmp_byte,14,tmp_Time_bolus_Infusion_value,0,2);
					System.arraycopy(tmp_byte,16,tmp_Time_bolus_Infusion_time,0,4);
					System.arraycopy(tmp_byte,20,tmp_Time_bolus_Infusion_set_value,0,2);
					System.arraycopy(tmp_byte,22,tmp_Cumulative_input,0,2);
					System.arraycopy(tmp_byte,24,tmp_Power,0,1);
					//System.arraycopy(tmp_byte,25,tmp_System_flag,0,1);
					System.arraycopy(tmp_byte,26,tmp_System_time,0,5);

					Basal_Infusion_value = tmp_two_decimal_places(tmp_Basal_Infusion_value);
					Temporary_Infusion_value = tmp_two_decimal_places(tmp_Temporary_Infusion_value);
					Temporary_Infusion_hour_start = byteToHexStringToDecString(tmp_Temporary_Infusion_time[0]);
					Temporary_Infusion_min_start = byteToHexStringToDecString(tmp_Temporary_Infusion_time[1]);
					Temporary_Infusion_hour_end = byteToHexStringToDecString(tmp_Temporary_Infusion_time[2]);
					Temporary_Infusion_min_end = byteToHexStringToDecString(tmp_Temporary_Infusion_time[3]);

					Temporary_Infusion_hour_start = tmp_add_zero(Temporary_Infusion_hour_start);//��λ������
					Temporary_Infusion_min_start = tmp_add_zero(Temporary_Infusion_min_start);//��λ������
					Temporary_Infusion_hour_end = tmp_add_zero(Temporary_Infusion_hour_end);
					Temporary_Infusion_min_end = tmp_add_zero(Temporary_Infusion_min_end);

					Temporary_set = bytesToHexStringToDecString(tmp_Temporary_set);

					Meal_bolus_Infusion_value = tmp_two_decimal_places(tmp_Meal_bolus_Infusion_value);
					Time_bolus_Infusion_value = tmp_two_decimal_places(tmp_Time_bolus_Infusion_value);
					Time_bolus_Infusion_hour_start = byteToHexStringToDecString(tmp_Time_bolus_Infusion_time[0]);
					Time_bolus_Infusion_min_start = byteToHexStringToDecString(tmp_Time_bolus_Infusion_time[1]);
					Time_bolus_Infusion_hour_end = byteToHexStringToDecString(tmp_Time_bolus_Infusion_time[2]);
					Time_bolus_Infusion_min_end = byteToHexStringToDecString(tmp_Time_bolus_Infusion_time[3]);

					Time_bolus_Infusion_hour_start = tmp_add_zero(Time_bolus_Infusion_hour_start);//��λ������
					Time_bolus_Infusion_min_start = tmp_add_zero(Time_bolus_Infusion_min_start);//��λ������
					Time_bolus_Infusion_hour_end = tmp_add_zero(Time_bolus_Infusion_hour_end);
					Time_bolus_Infusion_min_end = tmp_add_zero(Time_bolus_Infusion_min_end);

					Time_bolus_Infusion_set_value = tmp_two_decimal_places(tmp_Time_bolus_Infusion_set_value);

					Cumulative_input = tmp_two_decimal_places(tmp_Cumulative_input);
					Power = bytesToHexStringToDecString(tmp_Power);
					System_flag = system_set_status(tmp_byte[25]);

					System_year_txt = byteToHexStringToDecString(tmp_System_time[0]);
					System_month_txt = byteToHexStringToDecString(tmp_System_time[1]);
					System_day_txt = byteToHexStringToDecString(tmp_System_time[2]);
					System_hour_txt = byteToHexStringToDecString(tmp_System_time[3]);
					System_min_txt = byteToHexStringToDecString(tmp_System_time[4]);

					System_year_txt = tmp_add_zero(System_year_txt);//��λ������
					System_month_txt = tmp_add_zero(System_month_txt);//��λ������
					System_day_txt = tmp_add_zero(System_day_txt);
					System_hour_txt = tmp_add_zero(System_hour_txt);
					System_min_txt = tmp_add_zero(System_min_txt);

					receive = "��������עֵ  "+ Basal_Infusion_value + " U/h" + "\r\n"
							+ "��ʱ����עֵ  " + Temporary_Infusion_value + " U/h" + "\r\n"
							+ "ʱ��  "
							+ Temporary_Infusion_hour_start + ":" + Temporary_Infusion_min_start + "-"
							+ Temporary_Infusion_hour_end + ":" + Temporary_Infusion_min_end + "\r\n"
							+ "��ֵ  " + Temporary_set + "%" + "\r\n"
							+ "��ʱ�������עֵ  " + Meal_bolus_Infusion_value + " U\r\n"
							+ "��ʱ�������עֵ  " + Time_bolus_Infusion_value + " U\r\n"
							+ "ʱ��  "
							+ Time_bolus_Infusion_hour_start + ":" + Time_bolus_Infusion_min_start + "-"
							+ Time_bolus_Infusion_hour_end + ":" + Time_bolus_Infusion_min_end + "\r\n"
							+ "��ʱ���������ֵ  " + Time_bolus_Infusion_set_value + " U\r\n"
							+ "�ۼ���ע��  " + Cumulative_input + " U\r\n"
							+ "ϵͳ����  " + Power + "%\r\n"
							+ System_flag
							+ "����  " + System_year_txt + "-"
							+	System_month_txt + "-"
							+ System_day_txt + "\r\n"
							+ "ʱ��  " + System_hour_txt + ":" + System_min_txt;

				}
				break;
			case CommandsFound.READ_MEALBOLUSRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Mealbolusrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Mealbolusrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Mealbolusrecord_time, 0, 5);
						System.arraycopy(tmp_byte, 10, tmp_Mealbolusrecord_value, 0, 2);

						Mealbolusrecord_total = bytesToHexStringToDecString(tmp_Mealbolusrecord_total);
						Mealbolusrecord_num = bytesToHexStringToDecString(tmp_Mealbolusrecord_num);

						Mealbolusrecord_year = byteToHexStringToDecString(tmp_Mealbolusrecord_time[0]);
						Mealbolusrecord_month = byteToHexStringToDecString(tmp_Mealbolusrecord_time[1]);
						Mealbolusrecord_day = byteToHexStringToDecString(tmp_Mealbolusrecord_time[2]);
						Mealbolusrecord_hour = byteToHexStringToDecString(tmp_Mealbolusrecord_time[3]);
						Mealbolusrecord_min = byteToHexStringToDecString(tmp_Mealbolusrecord_time[4]);

						Mealbolusrecord_year = tmp_add_zero(Mealbolusrecord_year);//��λ������
						Mealbolusrecord_month = tmp_add_zero(Mealbolusrecord_month);//��λ������
						Mealbolusrecord_day = tmp_add_zero(Mealbolusrecord_day);
						Mealbolusrecord_hour = tmp_add_zero(Mealbolusrecord_hour);
						Mealbolusrecord_min = tmp_add_zero(Mealbolusrecord_min);

						//DecimalFormat df = new DecimalFormat("0.00");//��ʽ��С��������Ĳ�0
						//Mealbolusrecord_value = df.format(((double)(bytesToHexStringToInt(tmp_Mealbolusrecord_value)))/100);
						//Mealbolusrecord_value = Double.toString(((double)(bytesToHexStringToInt(tmp_Mealbolusrecord_value)))/100);
						Mealbolusrecord_value = tmp_two_decimal_places(tmp_Mealbolusrecord_value);

						receive = receive + "\r\n"
								+ "��¼��  " + Mealbolusrecord_num + "/" + Mealbolusrecord_total + "\r\n"
								+ "����  " + Mealbolusrecord_year + "-"
								                  +	Mealbolusrecord_month + "-"
												    + Mealbolusrecord_day + "\r\n"
								+ "ʱ��  " + Mealbolusrecord_hour + ":" + Mealbolusrecord_min + "\r\n"
								+ "����ֵ  " + Mealbolusrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_TIMEBOLUSRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Timebolusrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Timebolusrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Timebolusrecord_time, 0, 7);
						System.arraycopy(tmp_byte, 12, tmp_Timebolusrecord_value, 0, 2);

						Timebolusrecord_total = bytesToHexStringToDecString(tmp_Timebolusrecord_total);
						Timebolusrecord_num = bytesToHexStringToDecString(tmp_Timebolusrecord_num);

						Timebolusrecord_year = byteToHexStringToDecString(tmp_Timebolusrecord_time[0]);
						Timebolusrecord_month = byteToHexStringToDecString(tmp_Timebolusrecord_time[1]);
						Timebolusrecord_day = byteToHexStringToDecString(tmp_Timebolusrecord_time[2]);
						Timebolusrecord_hour_start = byteToHexStringToDecString(tmp_Timebolusrecord_time[3]);
						Timebolusrecord_min_start = byteToHexStringToDecString(tmp_Timebolusrecord_time[4]);
						Timebolusrecord_hour_end = byteToHexStringToDecString(tmp_Timebolusrecord_time[5]);
						Timebolusrecord_min_end = byteToHexStringToDecString(tmp_Timebolusrecord_time[6]);

						Timebolusrecord_year = tmp_add_zero(Timebolusrecord_year);//��λ������
						Timebolusrecord_month = tmp_add_zero(Timebolusrecord_month);//��λ������
						Timebolusrecord_day = tmp_add_zero(Timebolusrecord_day);
						Timebolusrecord_hour_start = tmp_add_zero(Timebolusrecord_hour_start);
						Timebolusrecord_min_start = tmp_add_zero(Timebolusrecord_min_start);
						Timebolusrecord_hour_end = tmp_add_zero(Timebolusrecord_hour_end);
						Timebolusrecord_min_end = tmp_add_zero(Timebolusrecord_min_end);

						Timebolusrecord_value = tmp_two_decimal_places(tmp_Timebolusrecord_value);

						receive = receive + "\r\n"
								+ "��¼��  " + Timebolusrecord_num + "/" + Timebolusrecord_total + "\r\n"
								+ "����  " + Timebolusrecord_year + "-"
											+ Timebolusrecord_month + "-"
												+ Timebolusrecord_day + "\r\n"
								+ "ʱ��  " + Timebolusrecord_hour_start + ":" + Timebolusrecord_min_start
								+ "-"      + Timebolusrecord_hour_end + ":" + Timebolusrecord_min_end + "\r\n"
								+ "����ֵ  " + Timebolusrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_SUSPENDEDPUMPRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[11]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Suspendpumprecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Suspendpumprecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Suspendpumprecord_time, 0, 5);

						Suspendpumprecord_total = bytesToHexStringToDecString(tmp_Suspendpumprecord_total);
						Suspendpumprecord_num = bytesToHexStringToDecString(tmp_Suspendpumprecord_num);

						Suspendpumprecord_year = byteToHexStringToDecString(tmp_Suspendpumprecord_time[0]);
						Suspendpumprecord_month = byteToHexStringToDecString(tmp_Suspendpumprecord_time[1]);
						Suspendpumprecord_day = byteToHexStringToDecString(tmp_Suspendpumprecord_time[2]);
						Suspendpumprecord_hour = byteToHexStringToDecString(tmp_Suspendpumprecord_time[3]);
						Suspendpumprecord_min = byteToHexStringToDecString(tmp_Suspendpumprecord_time[4]);

						Suspendpumprecord_year = tmp_add_zero(Suspendpumprecord_year);//��λ������
						Suspendpumprecord_month = tmp_add_zero(Suspendpumprecord_month);//��λ������
						Suspendpumprecord_day = tmp_add_zero(Suspendpumprecord_day);
						Suspendpumprecord_hour = tmp_add_zero(Suspendpumprecord_hour);
						Suspendpumprecord_min = tmp_add_zero(Suspendpumprecord_min);

						receive = receive + "\r\n"
								+ "��¼��  " + Suspendpumprecord_num + "/" + Suspendpumprecord_total + "\r\n"
								+ "����  " + Suspendpumprecord_year + "-"
								+	Suspendpumprecord_month + "-"
								+ Suspendpumprecord_day + "\r\n"
								+ "ʱ��  " + Suspendpumprecord_hour + ":" + Suspendpumprecord_min + "\r\n";

						if(((byte) 0x00) == tmp_byte[10]){//��ȡ
							receive = receive + "�򿪱�";
						}else{
							receive = receive + "��ͣ��";
						}
					}
				}
				break;
			case CommandsFound.READ_BASALRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[56]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Basalrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Basalrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Basalrecord_time, 0, 3);
						System.arraycopy(tmp_byte, 8, tmp_Basalrecord_value, 0, 48);

						Basalrecord_total = bytesToHexStringToDecString(tmp_Basalrecord_total);
						Basalrecord_num = bytesToHexStringToDecString(tmp_Basalrecord_num);

						Basalrecord_year = byteToHexStringToDecString(tmp_Basalrecord_time[0]);
						Basalrecord_month = byteToHexStringToDecString(tmp_Basalrecord_time[1]);
						Basalrecord_day = byteToHexStringToDecString(tmp_Basalrecord_time[2]);

						Basalrecord_year = tmp_add_zero(Basalrecord_year);//��λ������
						Basalrecord_month = tmp_add_zero(Basalrecord_month);//��λ������
						Basalrecord_day = tmp_add_zero(Basalrecord_day);

						receive = receive + "\r\n"
								+ "��¼��  " + Basalrecord_num + "/" + Basalrecord_total + "\r\n"
								+ "����  " + Basalrecord_year + "-"
								+	Basalrecord_month + "-"
								+ Basalrecord_day + "\r\n"
								+ "����ֵ" + "\r\n";
						for(int i=0;i<48;i+=2){
							Basalrecord_tmp_value[i/2] = twobytesTodecimal_places(tmp_Basalrecord_value[i],tmp_Basalrecord_value[i+1]);
							GeneralCommands.BasalData_value[i/2] = twobytesTodecimal_places(tmp_Basalrecord_value[i],tmp_Basalrecord_value[i+1]);
							receive = receive + BasalHour_tmp[i/2] + Basalrecord_tmp_value[i/2] + " U/h" + "\r\n";
						}
						Log.v("zhq_log  receive ","" + receive);

					}
				}
				break;
			case CommandsFound.READ_TEMPORARYRECORD://Э���д���δ������ʱ�ʽ���ʱ��
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[13]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Temporaryrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Temporaryrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Temporaryrecord_time, 0, 7);
						System.arraycopy(tmp_byte, 12, tmp_Temporaryrecord_value, 0, 1);

						Temporaryrecord_total = bytesToHexStringToDecString(tmp_Temporaryrecord_total);
						Temporaryrecord_num = bytesToHexStringToDecString(tmp_Temporaryrecord_num);

						Temporaryrecord_year = byteToHexStringToDecString(tmp_Temporaryrecord_time[0]);
						Temporaryrecord_month = byteToHexStringToDecString(tmp_Temporaryrecord_time[1]);
						Temporaryrecord_day = byteToHexStringToDecString(tmp_Temporaryrecord_time[2]);
						Temporaryrecord_hour_start = byteToHexStringToDecString(tmp_Temporaryrecord_time[3]);
						Temporaryrecord_min_start = byteToHexStringToDecString(tmp_Temporaryrecord_time[4]);
						Temporaryrecord_hour_end = byteToHexStringToDecString(tmp_Temporaryrecord_time[5]);
						Temporaryrecord_min_end = byteToHexStringToDecString(tmp_Temporaryrecord_time[6]);

						Temporaryrecord_year = tmp_add_zero(Temporaryrecord_year);//��λ������
						Temporaryrecord_month = tmp_add_zero(Temporaryrecord_month);//��λ������
						Temporaryrecord_day = tmp_add_zero(Temporaryrecord_day);
						Temporaryrecord_hour_start = tmp_add_zero(Temporaryrecord_hour_start);
						Temporaryrecord_min_start = tmp_add_zero(Temporaryrecord_min_start);
						Temporaryrecord_hour_end = tmp_add_zero(Temporaryrecord_hour_end);
						Temporaryrecord_min_end = tmp_add_zero(Temporaryrecord_min_end);

						Temporaryrecord_value = bytesToHexStringToDecString(tmp_Temporaryrecord_value);

						receive = receive + "\r\n"
								+ "��¼��  " + Temporaryrecord_num + "/" + Temporaryrecord_total + "\r\n"
								+ "����  "   + Temporaryrecord_year + "-"
												+ Temporaryrecord_month + "-"
													+ Temporaryrecord_day + "\r\n"
								+ "ʱ��  "
								+ Temporaryrecord_hour_start + ":" + Temporaryrecord_min_start + "-"
								+ Temporaryrecord_hour_end + ":" + Temporaryrecord_min_end +"\r\n"
								+ "��ʱ��  " + Temporaryrecord_value + "%";
					}
				}
				break;
			case CommandsFound.READ_DAILYTOTALRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[10]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Dailytotalrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Dailytotalrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Dailytotalrecord_time, 0, 3);
						System.arraycopy(tmp_byte, 8, tmp_Dailytotalrecord_value, 0, 2);

						Dailytotalrecord_total = bytesToHexStringToDecString(tmp_Dailytotalrecord_total);
						Dailytotalrecord_num = bytesToHexStringToDecString(tmp_Dailytotalrecord_num);

						Dailytotalrecord_year = byteToHexStringToDecString(tmp_Dailytotalrecord_time[0]);
						Dailytotalrecord_month = byteToHexStringToDecString(tmp_Dailytotalrecord_time[1]);
						Dailytotalrecord_day = byteToHexStringToDecString(tmp_Dailytotalrecord_time[2]);

						Dailytotalrecord_year = tmp_add_zero(Dailytotalrecord_year);//��λ������
						Dailytotalrecord_month = tmp_add_zero(Dailytotalrecord_month);//��λ������
						Dailytotalrecord_day = tmp_add_zero(Dailytotalrecord_day);

						Dailytotalrecord_value = tmp_two_decimal_places(tmp_Dailytotalrecord_value);

						receive = receive + "\r\n"
								+ "��¼��  " + Dailytotalrecord_num + "/" + Dailytotalrecord_total + "\r\n"
								+ "����  " + Dailytotalrecord_year + "-"
								+	Dailytotalrecord_month + "-"
								+ Dailytotalrecord_day + "\r\n"
								+ "������  " + Dailytotalrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_EVENTRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[11]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Eventrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Eventrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Eventrecord_time, 0, 5);

						Eventrecord_total = bytesToHexStringToDecString(tmp_Eventrecord_total);
						Eventrecord_num = bytesToHexStringToDecString(tmp_Eventrecord_num);

						Eventrecord_year = byteToHexStringToDecString(tmp_Eventrecord_time[0]);
						Eventrecord_month = byteToHexStringToDecString(tmp_Eventrecord_time[1]);
						Eventrecord_day = byteToHexStringToDecString(tmp_Eventrecord_time[2]);
						Eventrecord_hour = byteToHexStringToDecString(tmp_Eventrecord_time[3]);
						Eventrecord_min = byteToHexStringToDecString(tmp_Eventrecord_time[4]);

						Eventrecord_year = tmp_add_zero(Eventrecord_year);//��λ������
						Eventrecord_month = tmp_add_zero(Eventrecord_month);//��λ������
						Eventrecord_day = tmp_add_zero(Eventrecord_day);
						Eventrecord_hour = tmp_add_zero(Eventrecord_hour);
						Eventrecord_min = tmp_add_zero(Eventrecord_min);

						receive = receive + "\r\n"
								+ "��¼��  " + Eventrecord_num + "/" + Eventrecord_total + "\r\n"
								+ "����  " + Eventrecord_year + "-"
								+	Eventrecord_month + "-"
								+ Eventrecord_day + "\r\n"
								+ "ʱ��  " + Eventrecord_hour + ":" + Eventrecord_min + "\r\n";

						if(((byte) 0x01) == tmp_byte[10]){//��ȡ
							receive = receive + "��Ͳ��";
						}else if(((byte) 0x02) == tmp_byte[10]){
							receive = receive + "��Ͳ����";
						}else{
							receive = receive + "��ص�����";
						}
					}
				}
				break;
			case CommandsFound.READ_EXHAUSTRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//���ݽ�������
					if(CRC16_len == 8) {
						//�޼�¼���ߴ���
						Toast.makeText(getApplicationContext(), "�޼�¼", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//��ȡ
							receive = "��ȡ�ɹ�";
						}else{
							receive = "��ȡʧ��";
						}

						System.arraycopy(tmp_byte, 3, tmp_Exhaustrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Exhaustrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Exhaustrecord_time, 0, 5);
						System.arraycopy(tmp_byte, 10, tmp_Exhaustrecord_value, 0, 2);

						Exhaustrecord_total = bytesToHexStringToDecString(tmp_Exhaustrecord_total);
						Exhaustrecord_num = bytesToHexStringToDecString(tmp_Exhaustrecord_num);

						Exhaustrecord_year = byteToHexStringToDecString(tmp_Exhaustrecord_time[0]);
						Exhaustrecord_month = byteToHexStringToDecString(tmp_Exhaustrecord_time[1]);
						Exhaustrecord_day = byteToHexStringToDecString(tmp_Exhaustrecord_time[2]);
						Exhaustrecord_hour = byteToHexStringToDecString(tmp_Exhaustrecord_time[3]);
						Exhaustrecord_min = byteToHexStringToDecString(tmp_Exhaustrecord_time[4]);

						Exhaustrecord_year = tmp_add_zero(Exhaustrecord_year);//��λ������
						Exhaustrecord_month = tmp_add_zero(Exhaustrecord_month);//��λ������
						Exhaustrecord_day = tmp_add_zero(Exhaustrecord_day);
						Exhaustrecord_hour = tmp_add_zero(Exhaustrecord_hour);
						Exhaustrecord_min = tmp_add_zero(Exhaustrecord_min);

						Exhaustrecord_value = tmp_two_decimal_places(tmp_Exhaustrecord_value);

						receive = receive + "\r\n"
								+ "��¼��  " + Exhaustrecord_num + "/" + Exhaustrecord_total + "\r\n"
								+ "����  " + Exhaustrecord_year + "-"
								+	Exhaustrecord_month + "-"
								+ Exhaustrecord_day + "\r\n"
								+ "ʱ��  " + Exhaustrecord_hour + ":" + Exhaustrecord_min + "\r\n"
								+ "������  " + Exhaustrecord_value + "U";
					}
				}
				break;
			default:
				break;
		}


		switch (read_fmt_int) {
			case 0: // �ַ�����ʾ
				try {
					//tmp = new String(tmp_byte, "GB2312");
					tmp = new String(tmp_byte, "utf-8");//�޸Ķ�ȡ��ʽΪutf-8��
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1: // 16������ʾ
				for (int i = 0; i < tmp_byte.length; i++) {
					String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
					if (hex.length() == 1) {
						hex = '0' + hex;
					}
					tmp += ' ';
					tmp = tmp + hex;
				}
				break;
			case 2: // 10������ʾ
				int count = 0;
				for (int i = 0; i < tmp_byte.length; i++) {
					count *= 256;
					count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
				}
				tmp = Integer.toString(count);
				break;
			default:
				break;
		}

		if(receive == null || receive.length() <= 0) {
			System.out.println("zhq_log �յ�");
		}else{
			//ChatMsgFmt entity2 = new ChatMsgFmt("Device", tmp, MESSAGE_FROM.OTHERS);
			//ChatMsgFmt entity2 = new ChatMsgFmt("������", tmp, MESSAGE_FROM.OTHERS);
			ChatMsgFmt entity2 = new ChatMsgFmt("������", receive, MESSAGE_FROM.OTHERS);
			chat_list.add(entity2);
			chat_list_adapter.notifyDataSetChanged();
		}

		if(send_fmt_int == CommandsFound.READ_BASALDATA || send_fmt_int == CommandsFound.SET_BASALDATA
				|| send_fmt_int == CommandsFound.READ_BASALRECORD){
			myselfchartActivity(send_fmt_int);
		}

	}

	private void myselfchartActivity(int send_fmt_int) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), HorizontalBarChartActivity.class);
		intent.putExtra("set_Or_read",send_fmt_int);
		startActivityForResult(intent, 0);
		//startActivity(intent);
	}

	private String system_set_status(byte b) {
		String result = "";
		if((byte) 0 == (byte) ((b >> 0) & 0x1)){
			lcd_setup.setChecked(false);
			result = "Һ������״̬�� ������ʾ\n";
		}else {
			lcd_setup.setChecked(true);
			result = "Һ������״̬�� ����ʾ\n";
		}
		if((byte) 0 == (byte) ((b >> 1) & 0x1)){
			result += "Һ������״̬�� �ر�\n";
		}else{
			result += "Һ������״̬�� ��\n";
		}
		if((byte) 0 == (byte) ((b >> 2) & 0x1)){
			ring_setup.setChecked(false);
			result += "��������״̬�� ������\n";
		}else{
			ring_setup.setChecked(true);
			result += "��������״̬�� ����\n";
		}
		if((byte) 0 == (byte) ((b >> 3) & 0x1)){
			language_set.setChecked(false);
			result += "��������״̬�� English\n";
		}else{
			language_set.setChecked(true);
			result += "��������״̬�� ����\n";
		}
		if((byte) 0 == (byte) ((b >> 4) & 0x1)){
			result += "�͹���״̬�� �ǵ͹���\n";
		}else{
			result += "�͹���״̬�� �͹���\n";
		}
		if((byte) 0 == (byte) ((b >> 5) & 0x1)){
			result += "Һ������״̬�� �ر�\n";
		}else{
			result += "Һ������״̬�� ��\n";
		}
		if((byte) 0 == (byte) ((b >> 6) & 0x1)){
			result += "��������״̬�� δ����\n";
		}else{
			result += "��������״̬�� ����\n";
		}
		return result;
	}

	/**
	 * ��byteת��Ϊһ������Ϊ8��byte���飬����ÿ��ֵ����bit
	 */
	private byte[] getBooleanArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = (byte)(b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}
	/**
	 * ��byteתΪ�ַ�����bit
	 */
	private String byteToBit(byte b) {
		return ""
				/*+ (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
				+ (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
				+ (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
				+ (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);*/
				+ (byte) ((b >> 0) & 0x1) + (byte) ((b >> 1) & 0x1)
				+ (byte) ((b >> 2) & 0x1) + (byte) ((b >> 3) & 0x1)
				+ (byte) ((b >> 4) & 0x1) + (byte) ((b >> 5) & 0x1)
				+ (byte) ((b >> 6) & 0x1) + (byte) ((b >> 7) & 0x1);
	}

	/**
	 * BitתByte
	 */
	private byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit����
			if (byteStr.charAt(0) == '0') {// ����
				re = Integer.parseInt(byteStr, 2);
			} else {// ����
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {//4 bit����
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	private String Add_zero(String tmp) {//ǰ׺����0
		String result = "";

		if(3 == tmp.length()){
			result = "0" + tmp;
		}else if(2 == tmp.length()){
			result = "00" + tmp;
		}else if(1 == tmp.length()){
			result = "000" + tmp;
		}else if(0 == tmp.length()){
			result = "0000" + tmp;
		}else{
			result = tmp;
		}
		return result;
	}

	private String Expand_multiple_flag(byte[] tmp) {//����100��ת��Ϊʮ������
		String result = "";
		DecimalFormat df = new DecimalFormat("0000");
		result = df.format((bytesToHexStringToInt(tmp))*100);
		result = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(result));
		return result;
	}

	private String Expand_multiple(byte[] tmp) {//����100��ת��Ϊʮ������
		String result = "";
		DecimalFormat df = new DecimalFormat("0000");
		result = df.format((bytesToHexStringToInt(tmp))*100);
		result = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(result));
		return result;
	}

	private String tmp_two_decimal_places(byte[] tmp) {//����С�������λ
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//��ʽ��С��������Ĳ�0
		result = df.format(((double)(bytesToHexStringToInt(tmp)))/100);
		return result;
	}

	private String tmp_two_decimal_places_byte(byte tmp) {//����С�������λ
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//��ʽ��С��������Ĳ�0
		result = df.format(((double)(byteToHexStringToDec(tmp)))/100);
		return result;
	}

	private String twobytesTodecimal_places(byte tmpfirst, byte tmpend) {//����ת��Ϊ�������� ����С�������λ
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//��ʽ��С��������Ĳ�0
		result = df.format(((double)(twobyteToHexStringToDec(tmpfirst, tmpend)))/100);
		return result;
	}

	private static int twobyteToHexStringToDec(byte tmpfirst, byte tmpend) {//2��byte��ת��Ϊʮ����
		String result = "";
		int tmp = 0;
		{
			String hexStringfirst = Integer.toHexString(tmpfirst & 0xFF);
			if (hexStringfirst.length() == 1) {
				hexStringfirst = '0' + hexStringfirst;
			}
			String hexStringend = Integer.toHexString(tmpend & 0xFF);
			if (hexStringend.length() == 1) {
				hexStringend = '0' + hexStringend;
			}

			result = hexStringfirst.toUpperCase() + hexStringend.toUpperCase();
		}
		tmp = Integer.parseInt(result, 16);
		return tmp;
	}

	private String tmp_add_zero(String tmp) {
		if(tmp.length()<2)
		{
			tmp = "0" + tmp;
		}
		return tmp;
	}

	private static byte[] StringToHexBytes(String result) {//String��ת��Ϊbyteʮ������
		//String result = "";
		byte[] tmp_byte = null;
		byte[] return_byte = null;

		/*for (int i = 0; i < byte_data.length; i++) {
			String hexString = Integer.toHexString(byte_data[i] & 0xFF);

			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}*/

		tmp_byte = result.getBytes();
		return_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
		for (int i = 0; i < tmp_byte.length; i++) {
			if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
				if (0 == i % 2)
					return_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
				else
					return_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
			} else {
				if (0 == i % 2)
					return_byte[i / 2] = (byte) (((tmp_byte[i] - 'A' + 10) * 16) & 0xFF);
				else
					return_byte[i / 2] |= (byte) ((tmp_byte[i] - 'A' + 10) & 0xFF);
			}
		}
		return return_byte;
	}

	private static String bytesToHexStringToDecString(byte[] contror_code) {//byte[]��ת��Ϊʮ������
		String result = "";
		int tmp = 0;
		for (int i = 0; i < contror_code.length; i++) {
			String hexString = Integer.toHexString(contror_code[i] & 0xFF);

			if (hexString.length() == 1) {
				hexString = '0' + hexString;
				//hexString = hexString - '0';
			}
			result += hexString.toUpperCase();
		}
		tmp = Integer.parseInt(result,16);
		return Integer.toString(tmp);
	}

	private static int bytesToHexStringToInt(byte[] contror_code) {//byte[]��ת��Ϊʮ������
		String result = "";
		int tmp = 0;
		for (int i = 0; i < contror_code.length; i++) {
			String hexString = Integer.toHexString(contror_code[i] & 0xFF);

			if (hexString.length() == 1) {
				hexString = '0' + hexString;
				//hexString = hexString - '0';
			}
			result += hexString.toUpperCase();
		}
		tmp = Integer.parseInt(result,16);
		return tmp;
	}

	private static int byteToHexStringToDec(byte contror_code) {//byte��ת��Ϊʮ����
		String result = "";
		int tmp = 0;
		{
			String hexString = Integer.toHexString(contror_code & 0xFF);

			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		tmp = Integer.parseInt(result,16);
		return tmp;
	}

	private static String byteToHexStringToDecString(byte contror_code) {//byte��ת��Ϊʮ������
		String result = "";
		int tmp = 0;
		{
			String hexString = Integer.toHexString(contror_code & 0xFF);

			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		tmp = Integer.parseInt(result,16);
		return Integer.toString(tmp);
	}


	// ��ʼ���ؼ�
	private TextView talking_conect_flag_txt;
	private Button talking_read_btn;
	private Button talking_clear_btn;
	private Spinner read_fmt_select;
	private ToggleButton talking_stopdis_btn;
	private ListView chatist;
	private Spinner write_fmt_select;
    private Spinner send_fmt_select;
	private EditText edit_string_id;
	private EditText edit_hex_id;
	private EditText edit_shi_id;
	private Button sendbuttonid;
	private CheckBox send_onTime_checkbox;
	private EditText send_time_edit;
	private LinearLayout writeable_Layout;
    private LinearLayout alwaysuse_Layout;
	private LinearLayout command_layout;

    private LinearLayout generalsend_command;
	private GridLayout factory_data;
	private EditText Serial_Num;
	private EditText CompensationStepNum;
	private EditText Driving_scheme;
	private EditText version_software;
	private ToggleButton Open_Bluetooth;
	private ToggleButton Concentration_change;
	private EditText Open_Bluetooth_Read;
	private	EditText Concentration_change_read;

	private GridLayout system_set;
	private EditText max_meal_bolus;
	private EditText max_basal_rate;
	private EditText max_hours_bolus_Hours;
	private EditText max_hours_bolus_Dose_value;
	private EditText max_daily_total;
	private EditText Concentration_set;
	private EditText time_set;
	private Switch lcd_setup;
	private ToggleButton ring_setup;
	private Switch language_set;

	private GridLayout basal_rate_set_one;
	//private GridLayout basal_rate_set_two;
	//private GridLayout basal_rate_set_three;
	private EditText hour_value1;
	private EditText hour_value2;
	private EditText hour_value3;
	private EditText hour_value4;
	private EditText hour_value5;
	private EditText hour_value6;
	private EditText hour_value7;
	private EditText hour_value8;
	private EditText hour_value9;
	private EditText hour_value10;
	private EditText hour_value11;
	private EditText hour_value12;
	private EditText hour_value13;
	private EditText hour_value14;
	private EditText hour_value15;
	private EditText hour_value16;
	private EditText hour_value17;
	private EditText hour_value18;
	private EditText hour_value19;
	private EditText hour_value20;
	private EditText hour_value21;
	private EditText hour_value22;
	private EditText hour_value23;
	private EditText hour_value24;
	private EditText hour_value[] = new EditText[24];

	private int[] R_id = new int[]{
			R.id.hour_value1,R.id.hour_value2,R.id.hour_value3,R.id.hour_value4,R.id.hour_value5,R.id.hour_value6,
			R.id.hour_value7,R.id.hour_value8,R.id.hour_value9,R.id.hour_value10,R.id.hour_value11,R.id.hour_value12,
			R.id.hour_value13,R.id.hour_value14,R.id.hour_value15,R.id.hour_value16,R.id.hour_value17,R.id.hour_value18,
			R.id.hour_value19,R.id.hour_value20,R.id.hour_value21,R.id.hour_value22,R.id.hour_value23,R.id.hour_value24
	};


    private EditText control_code;
    private Button general_sendbut;
	private Button button_success;

	private List<ChatMsgFmt> chat_list = new ArrayList<ChatMsgFmt>();
	private ChatAdapater chat_list_adapter;
	private ArrayAdapter<String> fmt_adapter;
	private static final String FMT_SELCET[] = { "Str", "Hex", "Dec" };
    private ArrayAdapter<String> send_adapter;
    private static final String SEND_SELCET[] = { "���ó�������", "��ȡ��������", "����ϵͳ��������", "��ȡϵͳ��������",
                                                     "���û���������", "��ȡ����������", "��ȡ����״̬����", "��ȡ��ʱ�������¼",
                                                        "��ȡ��ʱ�������¼", "��ȡ��ͣ�ü�¼", "��ȡ�����ʼ�¼", "��ȡ��ʱ�ʼ�¼",
                                                           "��ȡ��������¼", "��ȡ�¼���¼", "��ȡ������¼"};

    /*  private static final String SEND_SELCET[] = { "A8080100001678A2", "A80811000017BDA2", "A808130000B67DA2",
                                                  "A80814000007BCA2", "A80820000187B2A2", "A808210001D672A2",
                                                  "A8082200012672A2", "A80823000177B2A2", "A808240001C673A2",
                                                  "A80825000197B3A2", "A80826000167B3A2", "A8082700013673A2"};*/
	private int write_fmt_int; // �������ݸ�ʽ ����
    private int send_fmt_int = 0;
	private int read_fmt_int = 0; // �������ݸ�ʽ ����
	private int proper = 0; // ͨ��Ȩ��
	private byte[] receive_bytes = new byte[100];
	private byte[] CRC16_bytes = new byte[2];
    private int CRC16_len = 0;
	private int receive_len_total = 0;
	private int cycle_index = 0;

	private void initView() {
		talking_conect_flag_txt = (TextView) findViewById(R.id.talking_conect_flag_txt);
		talking_read_btn = (Button) findViewById(R.id.talking_read_btn);
		talking_clear_btn = (Button) findViewById(R.id.talking_clear_btn);
		read_fmt_select = (Spinner) findViewById(R.id.read_fmt_select);
		talking_stopdis_btn  = (ToggleButton) findViewById(R.id.talking_stopdis_btn);
		chatist = (ListView) findViewById(R.id.chatist);
		write_fmt_select = (Spinner) findViewById(R.id.write_fmt_select);
        send_fmt_select = (Spinner) findViewById(R.id.send_fmt_select);
		edit_string_id = (EditText) findViewById(R.id.edit_string_id);
		edit_hex_id = (EditText) findViewById(R.id.edit_hex_id);
		edit_shi_id = (EditText) findViewById(R.id.edit_shi_id);
		sendbuttonid = (Button) findViewById(R.id.sendbuttonid);
		send_onTime_checkbox = (CheckBox) findViewById(R.id.send_onTime_checkbox);
		send_time_edit = (EditText) findViewById(R.id.send_time_edit);
		writeable_Layout = (LinearLayout) findViewById(R.id.writeable_Layout);
        alwaysuse_Layout = (LinearLayout) findViewById(R.id.alwaysuse_Layout);
		command_layout = (LinearLayout) findViewById(R.id.command_layout);

        generalsend_command = (LinearLayout) findViewById(R.id.generalsend_command);

		factory_data = (GridLayout) findViewById(R.id.factorydata);
		Serial_Num = (EditText) findViewById(R.id.serial_num);
		CompensationStepNum = (EditText) findViewById(R.id.CompensationStepNum);
		Driving_scheme = (EditText) findViewById(R.id.Driving_scheme);
		version_software = (EditText) findViewById(R.id.version_software);
		Open_Bluetooth = (ToggleButton) findViewById(R.id.open_bluetooth);
		Open_Bluetooth_Read = (EditText) findViewById(R.id.open_bluetooth_read);
		Concentration_change = (ToggleButton) findViewById(R.id.Concentration_change);
		Concentration_change_read = (EditText) findViewById(R.id.Concentration_change_read);

		system_set = (GridLayout) findViewById(R.id.system_set);
		max_meal_bolus = (EditText) findViewById(R.id.max_meal_bolus);
		max_basal_rate = (EditText) findViewById(R.id.max_basal_rate);
		max_hours_bolus_Hours = (EditText) findViewById(R.id.max_hours_bolus1);
		max_hours_bolus_Dose_value = (EditText) findViewById(R.id.max_hours_bolus2);
		max_daily_total = (EditText) findViewById(R.id.max_daily_total);
		Concentration_set = (EditText) findViewById(R.id.Concentration_set);
		time_set = (EditText) findViewById(R.id.time_set);
		lcd_setup = (Switch) findViewById(R.id.lcd_setup);
		ring_setup = (ToggleButton) findViewById(R.id.ring_setup);
		language_set = (Switch) findViewById(R.id.language_set);

		basal_rate_set_one = (GridLayout) findViewById(R.id.basal_rate_set_one);
		//basal_rate_set_two = (GridLayout) findViewById(R.id.basal_rate_set_two);
		//basal_rate_set_three = (GridLayout) findViewById(R.id.basal_rate_set_three);
		hour_value1 = (EditText) findViewById(R.id.hour_value1);
		hour_value2 = (EditText) findViewById(R.id.hour_value2);
		hour_value3 = (EditText) findViewById(R.id.hour_value3);
		hour_value4 = (EditText) findViewById(R.id.hour_value4);
		hour_value5 = (EditText) findViewById(R.id.hour_value5);
		hour_value6 = (EditText) findViewById(R.id.hour_value6);
		hour_value7 = (EditText) findViewById(R.id.hour_value7);
		hour_value8 = (EditText) findViewById(R.id.hour_value8);
		hour_value9 = (EditText) findViewById(R.id.hour_value9);
		hour_value10 = (EditText) findViewById(R.id.hour_value10);
		hour_value11 = (EditText) findViewById(R.id.hour_value11);
		hour_value12 = (EditText) findViewById(R.id.hour_value12);
		hour_value13 = (EditText) findViewById(R.id.hour_value13);
		hour_value14 = (EditText) findViewById(R.id.hour_value14);
		hour_value15 = (EditText) findViewById(R.id.hour_value15);
		hour_value16 = (EditText) findViewById(R.id.hour_value16);
		hour_value17 = (EditText) findViewById(R.id.hour_value17);
		hour_value18 = (EditText) findViewById(R.id.hour_value18);
		hour_value19 = (EditText) findViewById(R.id.hour_value19);
		hour_value20 = (EditText) findViewById(R.id.hour_value20);
		hour_value21 = (EditText) findViewById(R.id.hour_value21);
		hour_value22 = (EditText) findViewById(R.id.hour_value22);
		hour_value23 = (EditText) findViewById(R.id.hour_value23);
		hour_value24 = (EditText) findViewById(R.id.hour_value24);

		for(int i=0;i<24;i++){
			hour_value[i] = (EditText) findViewById(R_id[i]);
		}

        control_code = (EditText) findViewById(R.id.Control_code);//������
        general_sendbut = (Button) findViewById(R.id.General_sendbutton);
		button_success = (Button) findViewById(R.id.button_success);

		// ��ʼ���ؼ�����
		talking_conect_flag_txt.setText("������");
		fmt_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FMT_SELCET);
		fmt_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		read_fmt_select.setAdapter(fmt_adapter); // ���ͺͶ�ȡ���ݸ�ʽ
		read_fmt_select.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				read_fmt_int = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		write_fmt_select.setAdapter(fmt_adapter);
		write_fmt_select.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				write_fmt_int = arg2;
				switch (write_fmt_int) {
					case 0:
						edit_string_id.setVisibility(View.VISIBLE); // ��ʾ
						edit_hex_id.setVisibility(View.GONE); // ����
						edit_shi_id.setVisibility(View.GONE); // ����
						edit_string_id.setFocusable(true);
						edit_string_id.setFocusableInTouchMode(true);
						edit_string_id.requestFocus();
						break; // �ַ���
					case 1:
						edit_string_id.setVisibility(View.GONE); // ��ʾ
						edit_hex_id.setVisibility(View.VISIBLE); // ����
						edit_shi_id.setVisibility(View.GONE); // ����
						edit_hex_id.setFocusable(true);
						edit_hex_id.setFocusableInTouchMode(true);
						edit_hex_id.requestFocus();
						break; // 16����
					case 2:
						edit_string_id.setVisibility(View.GONE); // ��ʾ
						edit_hex_id.setVisibility(View.GONE); // ����
						edit_shi_id.setVisibility(View.VISIBLE); // ����
						edit_shi_id.setFocusable(true);
						edit_shi_id.setFocusableInTouchMode(true);
						edit_shi_id.requestFocus();
						break; // 10����
					default:
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

        send_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SEND_SELCET);
        send_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        send_fmt_select.setAdapter(send_adapter);
        send_fmt_select.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				send_fmt_int = arg2;

				if (false) {
					if (send_fmt_int < 7) {//��¼
						generalsend_command.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
					} else {
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
					}

					if (send_fmt_int < 2) {//��������
						factory_data.setVisibility(View.VISIBLE);//�ɼ�
					} else {
						factory_data.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
					}

					if ((send_fmt_int == 2) || (send_fmt_int == 3)) {//ϵͳ����
						system_set.setVisibility(View.VISIBLE);//�ɼ�
					} else {
						system_set.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
					}

					if ((send_fmt_int == 4) || (send_fmt_int == 5)) {//������
						basal_rate_set_one.setVisibility(View.VISIBLE);//�ɼ�
					} else {
						basal_rate_set_one.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
					}
				}

				factory_data.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
				system_set.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
				basal_rate_set_one.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
				generalsend_command.setVisibility(View.GONE);//���ز����벼�֣���ռ�ط���
				button_success.setVisibility(View.GONE);
				send_onTime_checkbox.setClickable(true);

				switch (send_fmt_int) {
					case CommandsFound.SET_FACTORYDATA:
						Log.v("zhq_log CommandsFound SET_FACTORYDATA ", "" + CommandsFound.SET_FACTORYDATA);
						factory_data.setVisibility(View.VISIBLE);//�ɼ�
						button_success.setVisibility(View.VISIBLE);
						Read_SettingData(send_fmt_int + 1);//��ѯ����
						break;
					case CommandsFound.READ_FACTORYDATA:
						Log.v("zhq_log CommandsFound READ_FACTORYDATA ", "" + CommandsFound.READ_FACTORYDATA);
						break;
					case CommandsFound.SET_SYSSETDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.SET_SYSSETDATA);
						system_set.setVisibility(View.VISIBLE);//�ɼ�
						button_success.setVisibility(View.VISIBLE);
						Read_SettingData(send_fmt_int + 1);//��ѯ����
						break;
					case CommandsFound.READ_SYSSETDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_SYSSETDATA);
						break;
					case CommandsFound.SET_BASALDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.SET_BASALDATA);
						basal_rate_set_one.setVisibility(View.VISIBLE);//�ɼ�
						button_success.setVisibility(View.VISIBLE);
						Read_SettingData(send_fmt_int + 1);//��ѯ����
						send_onTime_checkbox.setClickable(false);
						break;
					case CommandsFound.READ_BASALDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_BASALDATA);
						send_onTime_checkbox.setClickable(false);
						break;
					case CommandsFound.READ_RUNNINGDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_RUNNINGDATA);

						break;
					case CommandsFound.READ_MEALBOLUSRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_MEALBOLUSRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_TIMEBOLUSRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_TIMEBOLUSRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_SUSPENDEDPUMPRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_SUSPENDEDPUMPRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_BASALRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_BASALRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						send_onTime_checkbox.setClickable(false);
						break;
					case CommandsFound.READ_TEMPORARYRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_TEMPORARYRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_DAILYTOTALRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_DAILYTOTALRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_EVENTRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_EVENTRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					case CommandsFound.READ_EXHAUSTRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_EVENTRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//�ɼ�
						break;
					default:
						break;
				}

				if (true) {
					//=============================================================================================
                    /*byte[] sendmsg = getNewMsgEdit(true); // ��������
                    if (sendmsg == null) {
                        return;
                    }
                    Log.v("zhq_log sendmsg", "" + sendmsg);
                    mBluetoothGattCharacteristic.setValue(sendmsg);
                    Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);*/
					//=============================================================================================
				} else {
					byte[] sendmsg = new byte[40];
					try {
						sendmsg = SEND_SELCET[send_fmt_int].getBytes("utf-8");//GB2312
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (sendmsg == null) {
						return;
					}
					if (!Tools.mBLEService.isConnected()) {
						Toast.makeText(getApplicationContext(), "�ѶϿ�����", Toast.LENGTH_LONG)
								.show();
						return;
					}

					String newsendmsg = null;
					try {
						newsendmsg = new String(sendmsg, "utf-8");//GB2312
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//ChatMsgFmt entity = new ChatMsgFmt("�ֻ���", SEND_SELCET[send_fmt_int], MESSAGE_FROM.ME);
					ChatMsgFmt entity = new ChatMsgFmt("�ֻ���", newsendmsg, MESSAGE_FROM.ME);
					chat_list.add(entity);
					chat_list_adapter.notifyDataSetChanged();

					mBluetoothGattCharacteristic.setValue(sendmsg);
					Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
				}
				//send_fmt_int = 0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		chat_list_adapter = new ChatAdapater(getApplicationContext());
		chatist.setAdapter(chat_list_adapter);
		talking_read_btn.setOnClickListener(this);
		talking_clear_btn.setOnClickListener(this);
		talking_stopdis_btn.setOnClickListener(this);
		sendbuttonid.setOnClickListener(this);
		send_onTime_checkbox.setOnClickListener(this);

        general_sendbut.setOnClickListener(this);
		button_success.setOnClickListener(this);
		Open_Bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//ѡ��ʱ do some thing
					//Open_Bluetooth.setText("��");
					GeneralCommands.Bluetooth_Allow = "00";
				} else {
					//��ѡ��ʱ do some thing
					//Open_Bluetooth.setText("��");
					GeneralCommands.Bluetooth_Allow = "FF";
				}
			}
		});

		Concentration_change.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					//ѡ��ʱ do some thing
					GeneralCommands.Concentration_change = "00";//����
				} else {
					//��ѡ��ʱ do some thing
					GeneralCommands.Concentration_change = "FF";//��ֹ
				}
			}
		});

		lcd_setup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					//ѡ��ʱ do some thing
					GeneralCommands.lcd_setup = "1";
				} else {
					//��ѡ��ʱ do some thing
					GeneralCommands.lcd_setup = "0";
				}
			}
		});

		ring_setup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					//ѡ��ʱ do some thing
					GeneralCommands.ring_setup = "1";
				} else {
					//��ѡ��ʱ do some thing
					GeneralCommands.ring_setup = "0";
				}
			}
		});

		language_set.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					//ѡ��ʱ do some thing
					GeneralCommands.language_set = "1";
				} else {
					//��ѡ��ʱ do some thing
					GeneralCommands.language_set = "0";
				}
			}
		});


		// �鿴����ʲôȨ��
		proper = mBluetoothGattCharacteristic.getProperties();
		if (0 != (proper & 0x02)) { // �ɶ�
			talking_read_btn.setVisibility(View.VISIBLE);
			command_layout.setVisibility(View.GONE);
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))) { // ��д
			writeable_Layout.setVisibility(View.VISIBLE);
            //alwaysuse_Layout.setVisibility(View.INVISIBLE);//���ɼ�
			command_layout.setVisibility(View.GONE);

			if(0 != (proper & 0x02)) {//�ɶ�
                //alwaysuse_Layout.setVisibility(View.VISIBLE);//�ɼ� �򿪳�������
				if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
						|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // ֪ͨ
					writeable_Layout.setVisibility(View.GONE);
					command_layout.setVisibility(View.VISIBLE);
				}
            }
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // ֪ͨ
			if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE))
					|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))) { // ��д
			}else{
				command_layout.setVisibility(View.INVISIBLE);
			}
			//command_layout.setVisibility(View.INVISIBLE);
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, true);
			BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
					.getDescriptor(UUID
							.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Tools.mBLEService.mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	private void Read_SettingData(int send_fmt_int) {
		byte[] sendmsg = Read_SetData(send_fmt_int);
		if (sendmsg == null) {
			return;
		}
		Log.v("zhq_log ���� sendmsg", "" + sendmsg);

		if(sendmsg.length == sendmsg[1]){

		}else{
			Toast.makeText(getApplicationContext(), "���ݳ��ȴ���", Toast.LENGTH_LONG)
					.show();
			return;
		}

		int tmp = (sendmsg.length - 1)/20 + 1;
		//byte[] newsendmsg new byte[20];
		if(sendmsg.length <= 20){
			mBluetoothGattCharacteristic.setValue(sendmsg);
			Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
		}
		else {//�������ݳ���20���ֽ�ʱ
			for (int i = 0;i < tmp;i++) {
				int j = (i == tmp - 1) ? (sendmsg.length - i*20) : 20;
				byte[] newsendmsg  = new byte[j];

				System.arraycopy(sendmsg, 0 + i*20, newsendmsg, 0, j);
				mBluetoothGattCharacteristic.setValue(newsendmsg);
				boolean status = Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
				Log.v("zhq_log status", "" + status);
				System.out.println(Arrays.toString(newsendmsg));
			}
		}
		return;

	}

	private byte[] Read_SetData(int send_fmt_int) {
		String tmp_str = "";//��������
		byte[] tmp_byte = null;
		byte[] write_msg_byte = null;
		String tmp_contror_code = "00";//������

		Log.v("zhq_log CommandsFound  send_fmt_int ",""+ send_fmt_int);

		tmp_str = DataTransmission.Data_Transmission(
				SEND_SELCET[send_fmt_int],send_fmt_int,tmp_contror_code);

		if (0 == tmp_str.length())
			return null;

		tmp_byte = tmp_str.getBytes();
		write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
		for (int i = 0; i < tmp_byte.length; i++) {
			if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
				if (0 == i % 2)
					write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
				else
					write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
			} else {
				if (0 == i % 2)
					write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'A' + 10) * 16) & 0xFF);
				else
					write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'A' + 10) & 0xFF);
			}
		}

		if (!Tools.mBLEService.isConnected()) {
			Toast.makeText(getApplicationContext(), "�ѶϿ�����", Toast.LENGTH_LONG)
					.show();
			return null;
		}

		// ��ʾ
		if (false) {
			ChatMsgFmt entity = new ChatMsgFmt("�ֻ���", SEND_SELCET[send_fmt_int], MESSAGE_FROM.ME);
			chat_list.add(entity);
			chat_list_adapter.notifyDataSetChanged();
		}
		return write_msg_byte;
	}

	// ��Ϣ������
	private class ChatAdapater extends BaseAdapter {

		private LayoutInflater mInflater;

		@SuppressWarnings("unused")
		public ChatAdapater(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return chat_list.size();
		}

		@Override
		public ChatMsgFmt getItem(int position) {
			return chat_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (getItem(position).from == MESSAGE_FROM.ME) {
				convertView = mInflater.inflate(R.layout.msg_to_fmt, null);
			} else {
				convertView = mInflater.inflate(R.layout.msg_from_fmt, null);
			}

			TextView msg_nameid = (TextView) convertView.findViewById(R.id.msg_nameid);
			TextView msg_id = (TextView) convertView.findViewById(R.id.msg_id);

			msg_nameid.setText(getItem(position).getName());
			msg_id.setText(getItem(position).getMsg());

			return convertView;
		}

	}

	// ��Ϣ���Լ����ͻ��ǽ���
	private enum MESSAGE_FROM {
		ME, OTHERS
	}

	// ��������
	private class ChatMsgFmt {
		private String name; // ����
		private String msg; // ��Ϣ
		private MESSAGE_FROM from; // ���ܻ��Ƿ���

		public String getName() {
			return name;
		}

		public String getMsg() {
			return msg;
		}

		public MESSAGE_FROM getFrom() {
			return from;
		}

		public ChatMsgFmt(String name, String msg, MESSAGE_FROM from) {
			this.name = name;
			this.msg = msg;
			this.from = from;
		}
	}

	// ��ʱ��������
	private Handler sendontime_handl = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (!Tools.mBLEService.isConnected()) {
				send_onTime_checkbox.setChecked(false);
			}

			if (!send_onTime_checkbox.isChecked()) {
				return;
			}
			String delay_time_str = send_time_edit.getText().toString();
			int delay_time_int = 0;
			if (delay_time_str.length() == 0) {
				send_time_edit.setText("10");
				delay_time_int = 10;
			} else {
				delay_time_int = Integer.parseInt(send_time_edit.getText().toString());
			}
			sendontime_handl.sendEmptyMessageDelayed(0, delay_time_int);

			byte[] sendmsg = getNewMsgEdit(true); // �������� sendmsg = getMsgEdit(false); // ��������
			if (sendmsg == null) {
				return;
			}
            Log.v("zhq_log sendmsg", "" + sendmsg);
			mBluetoothGattCharacteristic.setValue(sendmsg);
			Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
		}
	};

	/**
	 * @param ����¼�    /
	 */
	// ��ť����
	@Override
	public void onClick(View v) {

		if (v == talking_clear_btn) { // ��ջỰ
			chat_list.clear();
			chat_list_adapter.notifyDataSetChanged();
			return;
		}
		if (!Tools.mBLEService.isConnected()) {
			Toast.makeText(getApplicationContext(), "�ѶϿ�����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if(v == button_success){
			String Data_field_txt = "";

			switch(send_fmt_int) {
				case CommandsFound.SET_FACTORYDATA:
					String Serial_Num_txt = Serial_Num.getText().toString();
					byte[] tmp_Serial_Num = new byte[13];
					try {//getbytes���ܻ᲻���������ַ����������쳣����
						tmp_Serial_Num = Serial_Num_txt.getBytes("utf-8");//string ת byte[]
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				/*byte[] tmp_Serial_Num = DataTransmission.DectoBytes(Serial_Num_txt);*/
					Serial_Num_txt = DataTransmission.bytesToHexString(tmp_Serial_Num);
					Log.v("zhq_log Serial_Num_txt", "" + Serial_Num_txt);

					String CompensationStepNum_txt = CompensationStepNum.getText().toString();
					byte[] tmp_CompensationStepNum_txt = DataTransmission.DectoBytes(CompensationStepNum_txt);
					CompensationStepNum_txt = DataTransmission.bytesToHexString(tmp_CompensationStepNum_txt);
					CompensationStepNum_txt = Add_zero(CompensationStepNum_txt);
					Log.v("zhq_log CompensationStepNum_txt", "" + CompensationStepNum_txt);

					String Driving_scheme_txt = Driving_scheme.getText().toString();
					byte[] tmp_Driving_scheme_txt = DataTransmission.DectoBytes(Driving_scheme_txt);
					Driving_scheme_txt = DataTransmission.bytesToHexString(tmp_Driving_scheme_txt);
					Log.v("zhq_log Driving_scheme_txt", "" + Driving_scheme_txt);

					Data_field_txt = Serial_Num_txt + GeneralCommands.Bluetooth_Allow
							+ CompensationStepNum_txt + Driving_scheme_txt
							+ GeneralCommands.Concentration_change;

					break;
				case CommandsFound.SET_SYSSETDATA:
					String max_meal_bolus_txt = max_meal_bolus.getText().toString();
					max_meal_bolus_txt =  Add_zero(
											DataTransmission.bytesToHexString(
													DataTransmission.Expand_DectoBytes(max_meal_bolus_txt)));
					Log.v("zhq_log max_meal_bolus_txt", "" + max_meal_bolus_txt);

					String max_basal_rate_txt = max_basal_rate.getText().toString();
					max_basal_rate_txt = Add_zero(
											DataTransmission.bytesToHexString(
													DataTransmission.Expand_DectoBytes(max_basal_rate_txt)));
					Log.v("zhq_log max_basal_rate_txt", "" + max_basal_rate_txt);

					String max_hours_bolus_Hours_txt = max_hours_bolus_Hours.getText().toString();
					max_hours_bolus_Hours_txt = Add_zero(
													DataTransmission.bytesToHexString(
															DataTransmission.DectoBytes(max_hours_bolus_Hours_txt)));
					Log.v("zhq_log max_hours_bolus_Hours_txt", "" + max_hours_bolus_Hours_txt);

					String max_hours_bolus_Dose_value_txt = max_hours_bolus_Dose_value.getText().toString();
					max_hours_bolus_Dose_value_txt = Add_zero(
														DataTransmission.bytesToHexString(
																DataTransmission.Expand_DectoBytes(max_hours_bolus_Dose_value_txt)));
					Log.v("zhq_log max_hours_bolus_Dose_value_txt", "" + max_hours_bolus_Dose_value_txt);

					String max_daily_total_txt = max_daily_total.getText().toString();
					max_daily_total_txt = Add_zero(
											DataTransmission.bytesToHexString(
													DataTransmission.Expand_DectoBytes(max_daily_total_txt)));
					Log.v("zhq_log max_daily_total_txt", "" + max_daily_total_txt);

					String Concentration_set_txt = Concentration_set.getText().toString();
					if("U40".equals(Concentration_set_txt)){
						Concentration_set_txt = "00";
					}else{
						Concentration_set_txt = "01";
					}

					/*String tmp_system_flag = GeneralCommands.lcd_setup + GeneralCommands.lcd_OnOff
							+ GeneralCommands.ring_setup + GeneralCommands.language_set
							+ GeneralCommands.LPM_Status + GeneralCommands.LcdBk_OnOff
							+ GeneralCommands.KeyLock + GeneralCommands.Extra;*/
					String tmp_system_flag = GeneralCommands.Extra + GeneralCommands.KeyLock
							+ GeneralCommands.LcdBk_OnOff + GeneralCommands.LPM_Status
							+ GeneralCommands.language_set + GeneralCommands.ring_setup
							+ GeneralCommands.lcd_OnOff + GeneralCommands.lcd_setup;
					byte System_flag_byte = BitToByte(tmp_system_flag);
					byte[] System_flag_bytes = new byte[1];
					System_flag_bytes[0] = System_flag_byte;

					String System_flag = DataTransmission.bytesToHexString(System_flag_bytes);

					SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
					Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
					String time = formatter.format(curDate);
					//time_set.setText(time.toCharArray(),0,time.length());//��ǰʱ��

					String time_set_txt = time_set.getText().toString();
					String [] tmp = time_set_txt.split(" ");
					String [] year = tmp[0].split("-");
					String [] hour = tmp[1].split(":");

					String year_set = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(year[0]));
					String month_set = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(year[1]));
					String day_set = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(year[2]));

					String hour_set = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(hour[0]));
					String min_set = DataTransmission.bytesToHexString(DataTransmission.DectoBytes(hour[1]));


					Data_field_txt = max_meal_bolus_txt + max_basal_rate_txt + max_hours_bolus_Hours_txt
							+ max_hours_bolus_Dose_value_txt + max_daily_total_txt + Concentration_set_txt
							+ System_flag + year_set + month_set + day_set + hour_set +min_set;

					break;
				case CommandsFound.SET_BASALDATA:
					if(false) {//ע�͵�����Ĵ���
						String hour_value1_txt = hour_value1.getText().toString();
						hour_value1_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value1_txt)));
						Log.v("zhq_log hour_value1_txt", "" + hour_value1_txt);

						String hour_value2_txt = hour_value2.getText().toString();
						hour_value2_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value2_txt)));
						Log.v("zhq_log hour_value2_txt", "" + hour_value2_txt);

						String hour_value3_txt = hour_value3.getText().toString();
						hour_value3_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value3_txt)));
						Log.v("zhq_log hour_value3_txt", "" + hour_value3_txt);

						String hour_value4_txt = hour_value4.getText().toString();
						hour_value4_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value4_txt)));
						Log.v("zhq_log hour_value4_txt", "" + hour_value4_txt);

						String hour_value5_txt = hour_value5.getText().toString();
						hour_value5_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value5_txt)));
						Log.v("zhq_log hour_value5_txt", "" + hour_value5_txt);

						String hour_value6_txt = hour_value6.getText().toString();
						hour_value6_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value6_txt)));
						Log.v("zhq_log hour_value6_txt", "" + hour_value6_txt);

						String hour_value7_txt = hour_value7.getText().toString();
						hour_value7_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value7_txt)));
						Log.v("zhq_log hour_value7_txt", "" + hour_value7_txt);

						String hour_value8_txt = hour_value8.getText().toString();
						hour_value8_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value8_txt)));
						Log.v("zhq_log hour_value8_txt", "" + hour_value8_txt);

						String hour_value9_txt = hour_value9.getText().toString();
						hour_value9_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value9_txt)));
						Log.v("zhq_log hour_value9_txt", "" + hour_value9_txt);

						String hour_value10_txt = hour_value10.getText().toString();
						hour_value10_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value10_txt)));
						Log.v("zhq_log hour_value10_txt", "" + hour_value10_txt);

						String hour_value11_txt = hour_value11.getText().toString();
						hour_value11_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value11_txt)));
						Log.v("zhq_log hour_value11_txt", "" + hour_value11_txt);

						String hour_value12_txt = hour_value12.getText().toString();
						hour_value12_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value12_txt)));
						Log.v("zhq_log hour_value12_txt", "" + hour_value12_txt);

						String hour_value13_txt = hour_value13.getText().toString();
						hour_value13_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value13_txt)));
						Log.v("zhq_log hour_value13_txt", "" + hour_value13_txt);

						String hour_value14_txt = hour_value14.getText().toString();
						hour_value14_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value14_txt)));
						Log.v("zhq_log hour_value14_txt", "" + hour_value14_txt);

						String hour_value15_txt = hour_value15.getText().toString();
						hour_value15_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value15_txt)));
						Log.v("zhq_log hour_value15_txt", "" + hour_value15_txt);

						String hour_value16_txt = hour_value16.getText().toString();
						hour_value16_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value16_txt)));
						Log.v("zhq_log hour_value16_txt", "" + hour_value16_txt);

						String hour_value17_txt = hour_value7.getText().toString();
						hour_value17_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value17_txt)));
						Log.v("zhq_log hour_value17_txt", "" + hour_value17_txt);

						String hour_value18_txt = hour_value18.getText().toString();
						hour_value18_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value18_txt)));
						Log.v("zhq_log hour_value18_txt", "" + hour_value18_txt);

						String hour_value19_txt = hour_value19.getText().toString();
						hour_value19_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value19_txt)));
						Log.v("zhq_log hour_value19_txt", "" + hour_value19_txt);

						String hour_value20_txt = hour_value20.getText().toString();
						hour_value20_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value20_txt)));
						Log.v("zhq_log hour_value20_txt", "" + hour_value20_txt);

						String hour_value21_txt = hour_value21.getText().toString();
						hour_value21_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value21_txt)));
						Log.v("zhq_log hour_value21_txt", "" + hour_value21_txt);

						String hour_value22_txt = hour_value22.getText().toString();
						hour_value22_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value22_txt)));
						Log.v("zhq_log hour_value22_txt", "" + hour_value22_txt);

						String hour_value23_txt = hour_value23.getText().toString();
						hour_value23_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value23_txt)));
						Log.v("zhq_log hour_value23_txt", "" + hour_value23_txt);

						String hour_value24_txt = hour_value24.getText().toString();
						hour_value24_txt = Add_zero(
								DataTransmission.bytesToHexString(
										DataTransmission.Expand_DectoBytes(hour_value24_txt)));
						Log.v("zhq_log hour_value24_txt", "" + hour_value24_txt);
						Data_field_txt = hour_value1_txt + hour_value2_txt + hour_value3_txt + hour_value4_txt
								+ hour_value5_txt + hour_value6_txt + hour_value7_txt + hour_value8_txt
								+ hour_value9_txt + hour_value10_txt + hour_value11_txt + hour_value12_txt
								+ hour_value13_txt + hour_value14_txt + hour_value15_txt + hour_value16_txt
								+ hour_value17_txt + hour_value18_txt + hour_value19_txt + hour_value20_txt
								+ hour_value21_txt + hour_value22_txt + hour_value23_txt + hour_value24_txt;
					}
					String hour_value_txt[] = new String[24];
					for(int i=0;i<24;i++){
						hour_value_txt[i] =  Add_zero(
												DataTransmission.bytesToHexString(
													DataTransmission.Expand_DectoBytes(hour_value[i].getText().toString())));
						Data_field_txt += hour_value_txt[i];
					}
					break;
				default:
					break;
			}
			Log.v("zhq_log ������","" + Data_field_txt);
			GeneralCommands.Data_field = Data_field_txt;
		}
        if (v == general_sendbut) { // ���Ͱ�ť
            byte[] sendmsg = getNewMsgEdit(true);
            if (sendmsg == null) {
                return;
            }
            Log.v("zhq_log ���� sendmsg", "" + sendmsg);

			if(sendmsg.length == sendmsg[1]){

			}else{
				/*Toast.makeText(getApplicationContext(), "���ݳ��ȴ���", Toast.LENGTH_LONG)
						.show();*/
				Toast.makeText(getApplicationContext(), "����������ɰ�ť��Ȼ��������!", Toast.LENGTH_LONG)
						.show();
				return;
			}


			int tmp = (sendmsg.length - 1)/20 + 1;
			//byte[] newsendmsg new byte[20];
			if(sendmsg.length <= 20){
				mBluetoothGattCharacteristic.setValue(sendmsg);
				Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
			}
			else {//�������ݳ���20���ֽ�ʱ
				for (int i = 0;i < tmp;i++) {
					int j = (i == tmp - 1) ? (sendmsg.length - i*20) : 20;
					byte[] newsendmsg  = new byte[j];

					System.arraycopy(sendmsg, 0 + i*20, newsendmsg, 0, j);
					mBluetoothGattCharacteristic.setValue(newsendmsg);
					boolean status = Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
					Log.v("zhq_log status", "" + status);
					System.out.println(Arrays.toString(newsendmsg));
					/*try {
						Thread.currentThread();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				}
			}
            //mBluetoothGattCharacteristic.setValue(sendmsg);
            //boolean status = Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);

            return;
        }
		if (v == sendbuttonid) { // ���Ͱ�ť
			byte[] sendmsg = getMsgEdit(true);
			if (sendmsg == null) {
				return;
			}
            Log.v("zhq_log sendmsg", "" + sendmsg);
			mBluetoothGattCharacteristic.setValue(sendmsg);
			boolean back = Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
			Log.v("zhq_log back", "" + back);
			return;
		}
		if (v == talking_read_btn) { // ��ȡ��ť
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
			return;
		}
		if (v == send_onTime_checkbox) { // ��ʱ��������
			if (send_onTime_checkbox.isChecked()) {
				sendontime_handl.sendEmptyMessage(0);
			}
		}
	}

	// ��ȡ��������
	private byte[] getMsgEdit(boolean dis_flag) {
		String tmp_str = "";
		byte[] tmp_byte = null;
		byte[] write_msg_byte = null;

		switch (write_fmt_int) {
		case 0:
			tmp_str = edit_string_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			write_msg_byte = tmp_str.getBytes();
			break;

		case 1:
			tmp_str = edit_hex_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			tmp_byte = tmp_str.getBytes();
			write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
			for (int i = 0; i < tmp_byte.length; i++) {
				if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
				} else {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);//Сд��ĸ�������Ϊ��A��
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'a' + 10) & 0xFF);
				}
			}
			break;

		case 2:
			tmp_str = edit_shi_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			int data_int = Integer.parseInt(tmp_str);
			int byte_size = 0;
			for (byte_size = 0; data_int != 0; byte_size++) { // ����ռ���ֽ���
				data_int /= 256;
			}
			write_msg_byte = new byte[byte_size];

			data_int = Integer.parseInt(tmp_str);
			for (int i = 0; i < byte_size; i++) { // ת��
				write_msg_byte[i] = (byte) (0xFF & (data_int % 256));
				data_int /= 256;
			}

			break;
		}

		if (0 == tmp_str.length())
			return null;
		// ��ʾ
		if (dis_flag) {
			//ChatMsgFmt entity = new ChatMsgFmt("Me", tmp_str, MESSAGE_FROM.ME);
            ChatMsgFmt entity = new ChatMsgFmt("�ֻ���", tmp_str, MESSAGE_FROM.ME);
			chat_list.add(entity);
			chat_list_adapter.notifyDataSetChanged();
		}

		return write_msg_byte;
	}

    private int getsendint(int send_fmt_int ){
        return   send_fmt_int;
    }

    /**
     *
     * ��ʾö�����͵ı���
     */

    /*private void testCommandEnum(CommandsEnum commandsEnum) {

        CommandsEnum[] allCommand = CommandsEnum.values();

        for (CommandsEnum aCommand : allCommand) {

            System.out.println("��ǰ����name��" + aCommand.name());

            System.out.println("��ǰ����ordinal��" + aCommand.ordinal());

            System.out.println("��ǰ���" + aCommand);

            if(aCommand.ordinal() == send_fmt_int){
                return ;
            }

        }

    }*/


    //��ȡ��������
    private byte[] getNewMsgEdit(boolean dis_flag) {
        String tmp_str = "";//��������
        byte[] tmp_byte = null;
        byte[] write_msg_byte = null;
        String tmp_contror_code = "00";//������

         //CommandsEnum commandsEnum = CommandsEnum.SET_FACTORYDATA; //ö������
        Log.v("zhq_log CommandsFound  send_fmt_int ",""+ send_fmt_int);

		if(send_fmt_int < 7){
			tmp_str = DataTransmission.Data_Transmission(
					SEND_SELCET[send_fmt_int],send_fmt_int,tmp_contror_code);
		}else{//��������ʾ
			tmp_contror_code = control_code.getText().toString();
			if(tmp_contror_code != null && tmp_contror_code.length() != 0){
				tmp_str = DataTransmission.Data_Transmission(
						SEND_SELCET[send_fmt_int],send_fmt_int,tmp_contror_code);
			}else{
				Toast.makeText(getApplicationContext(), "��������Ҫ��ѯ�ڼ�����¼", Toast.LENGTH_LONG)
						.show();
				return null;
			}
		}

		if (0 == tmp_str.length())
			return null;

		tmp_byte = tmp_str.getBytes();
		write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
		for (int i = 0; i < tmp_byte.length; i++) {
			if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
				if (0 == i % 2)
					write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
				else
					write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
			} else {
				if (0 == i % 2)
					write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'A' + 10) * 16) & 0xFF);
				else
					write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'A' + 10) & 0xFF);
			}
		}

        if (!Tools.mBLEService.isConnected()) {
            Toast.makeText(getApplicationContext(), "�ѶϿ�����", Toast.LENGTH_LONG)
                    .show();
            return null;
        }

        // ��ʾ
        if (dis_flag) {
            //ChatMsgFmt entity = new ChatMsgFmt("Me", tmp_str, MESSAGE_FROM.ME);
            ChatMsgFmt entity = new ChatMsgFmt("�ֻ���", SEND_SELCET[send_fmt_int], MESSAGE_FROM.ME);
            chat_list.add(entity);
            chat_list_adapter.notifyDataSetChanged();
        }
        return write_msg_byte;
    }


	@Override
	protected void onResume() {
		super.onResume();
		if(Tools.mBLEService != null) {
			if (Tools.mBLEService.isConnected()) {
				talking_conect_flag_txt.setText("������");
			} else {
				talking_conect_flag_txt.setText("�ѶϿ�");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bluetoothReceiver);

		if (!Tools.mBLEService.isConnected()) {
			return;
		}

		if (0 != (proper & 0x10)) { // ȥ����֪ͨ
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, false);
		}

	}
}
