package com.mednovo.mti_pii;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
//import java.util.Map;
import java.util.UUID;

//import com.mednovo.tools.CommandsEnum;
//import com.mednovo.tools.CommandsNewEnum;
import com.mednovo.tools.CRC16;
import com.mednovo.tools.CommandsFound;
import com.mednovo.tools.DataTransmission;
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
		mBluetoothGattCharacteristic = Tools.mBLEService.mBluetoothGatt
				.getServices().get(intent.getIntExtra("one", 0))
				.getCharacteristics().get(intent.getIntExtra("two", 0));

		setBroadcastReceiver(); // 设置广播监听

		initView(); // 初始化控件
	}

	// 设置广播监听
	private BroadcastReceiver bluetoothReceiver;

	private void setBroadcastReceiver() {
		// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
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
				// 数据改变通知
				if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
					//dis_recive_msg(intent.getByteArrayExtra("value"));
					CRC16_recive_msg(intent.getByteArrayExtra("value"));//校验接收的CRC码值
					//Analysis_recive_msg(intent.getByteArrayExtra("value"));
					if(((byte) 0xA2) == receive_bytes[CRC16_len-1]){//读取结束
						Analysis_all_recive_msg(receive_bytes);
					}
					return;
				}
				// 读取数据
				if (BLEService.ACTION_READ_OVER.equals(action)) {
					//dis_recive_msg(intent.getByteArrayExtra("value"));
					Analysis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}

				// 连接状态改变
				if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
					talking_conect_flag_txt.setText("已连接");
				}
				if (BLEService.ACTION_STATE_DISCONNECTED.equals(action)) {
					Tools.mBLEService.disConectBle();
					talking_conect_flag_txt.setText("已断开");
					Toast.makeText(getApplicationContext(), "已断开连接",
							Toast.LENGTH_LONG).show();
				}
			}

		};
		// 注册广播接收器
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private void dis_recive_msg(byte[] tmp_byte) {
		if (talking_stopdis_btn.isChecked())
			return; // 停止显示

		String tmp = "";
		if (0 == tmp_byte.length) {
			return;
		}

		switch (read_fmt_int) {
		case 0: // 字符串显示
			try {
				//tmp = new String(tmp_byte, "GB2312");
                tmp = new String(tmp_byte, "utf-8");//修改读取格式为utf-8型
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 1: // 16进制显示
			for (int i = 0; i < tmp_byte.length; i++) {
				String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				tmp += ' ';
				tmp = tmp + hex;
			}
			break;
		case 2: // 10进制显示
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
        ChatMsgFmt entity2 = new ChatMsgFmt("蓝牙端", tmp, MESSAGE_FROM.OTHERS);
		chat_list.add(entity2);
		chat_list_adapter.notifyDataSetChanged();
	}

	/**
	 * @param tmp_byte
	 */
	private void CRC16_recive_msg(byte[] tmp_byte) {//解析数据
		if (talking_stopdis_btn.isChecked()) {
			return; // 停止显示
		}
		if (0 == tmp_byte.length) {
			return;
		}
		switch (send_fmt_int){
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
					Arrays.fill(receive_bytes, (byte) 0);//清空
					System.arraycopy(tmp_byte,0,receive_bytes,0,tmp_byte.length);
					receive_len_total = byteToHexStringToDec(tmp_byte[1]);
					CRC16_len = tmp_byte.length;
					cycle_index = CRC16_len/20;
					if(0 == cycle_index && (((byte) 0xA2) == tmp_byte[tmp_byte.length - 1])){//长度不满20字节
						System.arraycopy(tmp_byte,tmp_byte.length - 3, CRC16_bytes,0,2);
						if(CRC16.checkBuf(receive_bytes,CRC16_len)){
							System.out.print("zhq_log CRC16校验正确！");
						}
					}
				}else{//数据大于20个字节
					CRC16_len = CRC16_len + tmp_byte.length;
					cycle_index = (CRC16_len - 1)/20;
					System.arraycopy(tmp_byte, 0, receive_bytes, 20*cycle_index, tmp_byte.length);
					System.arraycopy(tmp_byte,tmp_byte.length - 3, CRC16_bytes,0,2);//获取CRC校验值
					if(((byte) 0xA2) == tmp_byte[tmp_byte.length - 1]){//数据接收完毕
						if(CRC16.checkBuf(receive_bytes,CRC16_len)){//验证CRC
							System.out.print("zhq_log CRC16校验正确！");
						}
					}
				}
				break;
			default:
				break;
		}
	}

	private void Analysis_recive_msg(byte[] tmp_byte) {//解析数据
		if (talking_stopdis_btn.isChecked())
			return; // 停止显示

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

					if(((byte) 0x00) == tmp_byte[16]){//蓝牙允许
						//Open_Bluetooth.setChecked(true);
						Open_Bluetooth_txt = "允许开启蓝牙";
						Open_Bluetooth_Read.setText("是");
					}
					else{
						//Open_Bluetooth.setChecked(false);
						Open_Bluetooth_txt = "禁止开启蓝牙";
						Open_Bluetooth_Read.setText("否");
					}
					receive = "产品序列号  "+ Serial_Num_txt +"\r\n"
							+ "补偿步数  " + CompensationStepNum_txt + "\r\n"
							+ "驱动方案  " + Driving_scheme_txt + "\r\n"
							+ Open_Bluetooth_txt;

				}else{
					if(((byte) 0x00) == tmp_byte[0]){//更改浓度
						//Open_Bluetooth.setChecked(true);
						Concentration_change_txt = "允许更改浓度";
						Concentration_change_read.setText("允许");
					}
					else{
						//Open_Bluetooth.setChecked(false);
						Concentration_change_txt = "禁止更改浓度";
						Concentration_change_read.setText("禁止");
					}
					System.arraycopy(tmp_byte, 1, tmp_version_software, 0, 6);
					try {
						version_software_txt = new String(tmp_version_software,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					version_software.setText(version_software_txt.toCharArray(),0,version_software_txt.length());
					receive = Concentration_change_txt + "\r\n" + "软件版本  " + version_software_txt;
				}
				break;
			case CommandsFound.READ_SYSSETDATA:
				break;
			default:
				break;
		}


		switch (read_fmt_int) {
			case 0: // 字符串显示
				try {
					//tmp = new String(tmp_byte, "GB2312");
					tmp = new String(tmp_byte, "utf-8");//修改读取格式为utf-8型
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1: // 16进制显示
				for (int i = 0; i < tmp_byte.length; i++) {
					String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
					if (hex.length() == 1) {
						hex = '0' + hex;
					}
					tmp += ' ';
					tmp = tmp + hex;
				}
				break;
			case 2: // 10进制显示
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
		//ChatMsgFmt entity2 = new ChatMsgFmt("蓝牙端", tmp, MESSAGE_FROM.OTHERS);
		ChatMsgFmt entity2 = new ChatMsgFmt("蓝牙端", receive, MESSAGE_FROM.OTHERS);
		chat_list.add(entity2);
		chat_list_adapter.notifyDataSetChanged();

	}



	private void Analysis_all_recive_msg(byte[] tmp_byte) {//解析数据
		if (talking_stopdis_btn.isChecked()) {
			return; // 停止显示
		}

		String tmp = "";
		String receive = "";

		if (0 == tmp_byte.length) {
			return;
		}

		/**
		 * @param 出厂数据定义
		 */
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

		/**
		 * @param 即时大剂量记录定义
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
		 * @param 定时大剂量记录定义
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
		 * @param 暂停泵记录定义
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
		 * @param 基础率记录定义
		 */
		String Basalrecord_total = "";
		String Basalrecord_num = "";
		String Basalrecord_year = "";
		String Basalrecord_month = "";
		String Basalrecord_day = "";

		String[] Basalrecord_tmp_value = new String[100];
		final   String BasalHour_tmp[] = {"时段00—01: ","时段01—02: ","时段02—03: ","时段03—04: ",
											"时段04—05: ","时段05—06: ","时段06—07: ","时段07—08: ",
											"时段08—09: ","时段09—10: ","时段10—11: ","时段11—12: ",
											"时段12—13: ","时段13—14: ","时段14—15: ","时段15—16: ",
											"时段16—17: ","时段17—18: ","时段18—19: ","时段19—20: ",
											"时段20—21: ","时段21—22: ","时段22—23: ","时段23—24: "};

		final byte[] tmp_Basalrecord_total = new byte[1];
		final byte[] tmp_Basalrecord_num = new byte[1];
		final byte[] tmp_Basalrecord_time = new byte[3];
		final byte[] tmp_Basalrecord_value = new byte[48];

		/**
		 * @param 暂时率记录定义
		 */
		String Temporaryrecord_total = "";
		String Temporaryrecord_num = "";
		String Temporaryrecord_year = "";
		String Temporaryrecord_month = "";
		String Temporaryrecord_day = "";
		String Temporaryrecord_hour = "";
		String Temporaryrecord_min = "";
		String Temporaryrecord_value = "";

		final byte[] tmp_Temporaryrecord_total = new byte[1];
		final byte[] tmp_Temporaryrecord_num = new byte[1];
		final byte[] tmp_Temporaryrecord_time = new byte[5];
		final byte[] tmp_Temporaryrecord_value = new byte[2];

		/**
		 * @param 日总量记录定义
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
		 * @param 事件记录定义
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
		 * @param 排气记录定义
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

					if(((byte) 0x00) == tmp_byte[16]){//蓝牙允许
						//Open_Bluetooth.setChecked(true);
						Open_Bluetooth_txt = "允许开启蓝牙";
						Open_Bluetooth_Read.setText("是");
					}else{
						//Open_Bluetooth.setChecked(false);
						Open_Bluetooth_txt = "禁止开启蓝牙";
						Open_Bluetooth_Read.setText("否");
					}
					receive = "产品序列号  "+ Serial_Num_txt +"\r\n"
							+ "补偿步数  " + CompensationStepNum_txt + "\r\n"
							+ "驱动方案  " + Driving_scheme_txt + "\r\n"
							+ Open_Bluetooth_txt + "\r\n";

					if(((byte) 0x00) == tmp_byte[20]){//更改浓度
						//Open_Bluetooth.setChecked(true);
						Concentration_change_txt = "允许更改浓度";
						Concentration_change_read.setText("允许");
					}else{
						//Open_Bluetooth.setChecked(false);
						Concentration_change_txt = "禁止更改浓度";
						Concentration_change_read.setText("禁止");
					}

					System.arraycopy(tmp_byte, 21, tmp_version_software, 0, 6);
					try {
						version_software_txt = new String(tmp_version_software,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					version_software.setText(version_software_txt.toCharArray(),0,version_software_txt.length());

					receive = receive + Concentration_change_txt + "\r\n" + "软件版本  " + version_software_txt;
				}
				break;
			case CommandsFound.READ_SYSSETDATA:
				break;
			case CommandsFound.READ_BASALDATA:
				break;
			case CommandsFound.READ_RUNNINGDATA:
				break;
			case CommandsFound.READ_MEALBOLUSRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Mealbolusrecord_year = tmp_add_zero(Mealbolusrecord_year);//个位数加零
						Mealbolusrecord_month = tmp_add_zero(Mealbolusrecord_month);//个位数加零
						Mealbolusrecord_day = tmp_add_zero(Mealbolusrecord_day);
						Mealbolusrecord_hour = tmp_add_zero(Mealbolusrecord_hour);
						Mealbolusrecord_min = tmp_add_zero(Mealbolusrecord_min);

						//DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
						//Mealbolusrecord_value = df.format(((double)(bytesToHexStringToInt(tmp_Mealbolusrecord_value)))/100);
						//Mealbolusrecord_value = Double.toString(((double)(bytesToHexStringToInt(tmp_Mealbolusrecord_value)))/100);
						Mealbolusrecord_value = tmp_two_decimal_places(tmp_Mealbolusrecord_value);

						receive = receive + "\r\n"
								+ "记录数  " + Mealbolusrecord_num + "/" + Mealbolusrecord_total + "\r\n"
								+ "日期  " + Mealbolusrecord_year + "-"
								                  +	Mealbolusrecord_month + "-"
												    + Mealbolusrecord_day + "\r\n"
								+ "时间  " + Mealbolusrecord_hour + ":" + Mealbolusrecord_min + "\r\n"
								+ "剂量值  " + Mealbolusrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_TIMEBOLUSRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Timebolusrecord_year = tmp_add_zero(Timebolusrecord_year);//个位数加零
						Timebolusrecord_month = tmp_add_zero(Timebolusrecord_month);//个位数加零
						Timebolusrecord_day = tmp_add_zero(Timebolusrecord_day);
						Timebolusrecord_hour_start = tmp_add_zero(Timebolusrecord_hour_start);
						Timebolusrecord_min_start = tmp_add_zero(Timebolusrecord_min_start);
						Timebolusrecord_hour_end = tmp_add_zero(Timebolusrecord_hour_end);
						Timebolusrecord_min_end = tmp_add_zero(Timebolusrecord_min_end);

						Timebolusrecord_value = tmp_two_decimal_places(tmp_Timebolusrecord_value);

						receive = receive + "\r\n"
								+ "记录数  " + Timebolusrecord_num + "/" + Timebolusrecord_total + "\r\n"
								+ "日期  " + Timebolusrecord_year + "-"
											+ Timebolusrecord_month + "-"
												+ Timebolusrecord_day + "\r\n"
								+ "时间  " + Timebolusrecord_hour_start + ":" + Timebolusrecord_min_start
								+ "-"      + Timebolusrecord_hour_end + ":" + Timebolusrecord_min_end + "\r\n"
								+ "剂量值  " + Timebolusrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_SUSPENDEDPUMPRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[11]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Suspendpumprecord_year = tmp_add_zero(Suspendpumprecord_year);//个位数加零
						Suspendpumprecord_month = tmp_add_zero(Suspendpumprecord_month);//个位数加零
						Suspendpumprecord_day = tmp_add_zero(Suspendpumprecord_day);
						Suspendpumprecord_hour = tmp_add_zero(Suspendpumprecord_hour);
						Suspendpumprecord_min = tmp_add_zero(Suspendpumprecord_min);

						receive = receive + "\r\n"
								+ "记录数  " + Suspendpumprecord_num + "/" + Suspendpumprecord_total + "\r\n"
								+ "日期  " + Suspendpumprecord_year + "-"
								+	Suspendpumprecord_month + "-"
								+ Suspendpumprecord_day + "\r\n"
								+ "时间  " + Suspendpumprecord_hour + ":" + Suspendpumprecord_min + "\r\n";

						if(((byte) 0x00) == tmp_byte[10]){//读取
							receive = receive + "打开泵";
						}else{
							receive = receive + "暂停泵";
						}
					}
				}
				break;
			case CommandsFound.READ_BASALRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[56]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Basalrecord_year = tmp_add_zero(Basalrecord_year);//个位数加零
						Basalrecord_month = tmp_add_zero(Basalrecord_month);//个位数加零
						Basalrecord_day = tmp_add_zero(Basalrecord_day);

						receive = receive + "\r\n"
								+ "记录数  " + Basalrecord_num + "/" + Basalrecord_total + "\r\n"
								+ "日期  " + Basalrecord_year + "-"
								+	Basalrecord_month + "-"
								+ Basalrecord_day + "\r\n"
								+ "剂量值" + "\r\n";
						for(int i=0;i<48;i+=2){
							Basalrecord_tmp_value[i/2] = twobytesTodecimal_places(tmp_Basalrecord_value[i],tmp_Basalrecord_value[i+1]);
							receive = receive + BasalHour_tmp[i/2] + Basalrecord_tmp_value[i/2] + " U/h" + "\r\n";
						}
						Log.v("zhq_log  receive ","" + receive);

					}
				}
				break;
			case CommandsFound.READ_TEMPORARYRECORD://协议有错误，未发送暂时率结束时间
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
						}

						System.arraycopy(tmp_byte, 3, tmp_Temporaryrecord_total, 0, 1);
						System.arraycopy(tmp_byte, 4, tmp_Temporaryrecord_num, 0, 1);
						System.arraycopy(tmp_byte, 5, tmp_Temporaryrecord_time, 0, 5);
						System.arraycopy(tmp_byte, 10, tmp_Temporaryrecord_value, 0, 2);

						Temporaryrecord_total = bytesToHexStringToDecString(tmp_Temporaryrecord_total);
						Temporaryrecord_num = bytesToHexStringToDecString(tmp_Temporaryrecord_num);

						Temporaryrecord_year = byteToHexStringToDecString(tmp_Temporaryrecord_time[0]);
						Temporaryrecord_month = byteToHexStringToDecString(tmp_Temporaryrecord_time[1]);
						Temporaryrecord_day = byteToHexStringToDecString(tmp_Temporaryrecord_time[2]);
						Temporaryrecord_hour = byteToHexStringToDecString(tmp_Temporaryrecord_time[3]);
						Temporaryrecord_min = byteToHexStringToDecString(tmp_Temporaryrecord_time[4]);

						Temporaryrecord_year = tmp_add_zero(Temporaryrecord_year);//个位数加零
						Temporaryrecord_month = tmp_add_zero(Temporaryrecord_month);//个位数加零
						Temporaryrecord_day = tmp_add_zero(Temporaryrecord_day);
						Temporaryrecord_hour = tmp_add_zero(Temporaryrecord_hour);
						Temporaryrecord_min = tmp_add_zero(Temporaryrecord_min);

						Temporaryrecord_value = bytesToHexStringToDecString(tmp_Temporaryrecord_value);

						receive = receive + "\r\n"
								+ "记录数  " + Temporaryrecord_num + "/" + Temporaryrecord_total + "\r\n"
								+ "日期  " + Temporaryrecord_year + "-"
								+	Temporaryrecord_month + "-"
								+ Temporaryrecord_day + "\r\n"
								+ "时间  " + Temporaryrecord_hour + ":" + Temporaryrecord_min + "\r\n"
								+ "暂时率  " + Temporaryrecord_value + "%";
					}
				}
				break;
			case CommandsFound.READ_DAILYTOTALRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[10]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Dailytotalrecord_year = tmp_add_zero(Dailytotalrecord_year);//个位数加零
						Dailytotalrecord_month = tmp_add_zero(Dailytotalrecord_month);//个位数加零
						Dailytotalrecord_day = tmp_add_zero(Dailytotalrecord_day);

						Dailytotalrecord_value = tmp_two_decimal_places(tmp_Dailytotalrecord_value);

						receive = receive + "\r\n"
								+ "记录数  " + Dailytotalrecord_num + "/" + Dailytotalrecord_total + "\r\n"
								+ "日期  " + Dailytotalrecord_year + "-"
								+	Dailytotalrecord_month + "-"
								+ Dailytotalrecord_day + "\r\n"
								+ "日总量  " + Dailytotalrecord_value + "U";
					}
				}
				break;
			case CommandsFound.READ_EVENTRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[11]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Eventrecord_year = tmp_add_zero(Eventrecord_year);//个位数加零
						Eventrecord_month = tmp_add_zero(Eventrecord_month);//个位数加零
						Eventrecord_day = tmp_add_zero(Eventrecord_day);
						Eventrecord_hour = tmp_add_zero(Eventrecord_hour);
						Eventrecord_min = tmp_add_zero(Eventrecord_min);

						receive = receive + "\r\n"
								+ "记录数  " + Eventrecord_num + "/" + Eventrecord_total + "\r\n"
								+ "日期  " + Eventrecord_year + "-"
								+	Eventrecord_month + "-"
								+ Eventrecord_day + "\r\n"
								+ "时间  " + Eventrecord_hour + ":" + Eventrecord_min + "\r\n";

						if(((byte) 0x01) == tmp_byte[10]){//读取
							receive = receive + "针筒空";
						}else if(((byte) 0x02) == tmp_byte[10]){
							receive = receive + "针筒堵塞";
						}else{
							receive = receive + "电池电量低";
						}
					}
				}
				break;
			case CommandsFound.READ_EXHAUSTRECORD:
				if((((byte) 0xA8) == tmp_byte[0]) && (((byte) 0xA2) == tmp_byte[CRC16_len - 1]) ){//数据接收完整
					if(CRC16_len == 8) {
						//无记录或者错误
						Toast.makeText(getApplicationContext(), "无记录", Toast.LENGTH_LONG)
								.show();
					}else{
						if(((byte) 0x00) == tmp_byte[12]){//读取
							receive = "读取成功";
						}else{
							receive = "读取失败";
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

						Exhaustrecord_year = tmp_add_zero(Exhaustrecord_year);//个位数加零
						Exhaustrecord_month = tmp_add_zero(Exhaustrecord_month);//个位数加零
						Exhaustrecord_day = tmp_add_zero(Exhaustrecord_day);
						Exhaustrecord_hour = tmp_add_zero(Exhaustrecord_hour);
						Exhaustrecord_min = tmp_add_zero(Exhaustrecord_min);

						Exhaustrecord_value = tmp_two_decimal_places(tmp_Exhaustrecord_value);

						receive = receive + "\r\n"
								+ "记录数  " + Exhaustrecord_num + "/" + Exhaustrecord_total + "\r\n"
								+ "日期  " + Exhaustrecord_year + "-"
								+	Exhaustrecord_month + "-"
								+ Exhaustrecord_day + "\r\n"
								+ "时间  " + Exhaustrecord_hour + ":" + Exhaustrecord_min + "\r\n"
								+ "排起量  " + Exhaustrecord_value + "U";
					}
				}
				break;
			default:
				break;
		}


		switch (read_fmt_int) {
			case 0: // 字符串显示
				try {
					//tmp = new String(tmp_byte, "GB2312");
					tmp = new String(tmp_byte, "utf-8");//修改读取格式为utf-8型
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1: // 16进制显示
				for (int i = 0; i < tmp_byte.length; i++) {
					String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
					if (hex.length() == 1) {
						hex = '0' + hex;
					}
					tmp += ' ';
					tmp = tmp + hex;
				}
				break;
			case 2: // 10进制显示
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
		//ChatMsgFmt entity2 = new ChatMsgFmt("蓝牙端", tmp, MESSAGE_FROM.OTHERS);
		ChatMsgFmt entity2 = new ChatMsgFmt("蓝牙端", receive, MESSAGE_FROM.OTHERS);
		chat_list.add(entity2);
		chat_list_adapter.notifyDataSetChanged();

	}

	private String tmp_two_decimal_places(byte[] tmp) {//保留小数点后两位
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		result = df.format(((double)(bytesToHexStringToInt(tmp)))/100);
		return result;
	}

	private String tmp_two_decimal_places_byte(byte tmp) {//保留小数点后两位
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		result = df.format(((double)(byteToHexStringToDec(tmp)))/100);
		return result;
	}

	private String twobytesTodecimal_places(byte tmpfirst, byte tmpend) {//整数转换为浮点类型 保留小数点后两位
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		result = df.format(((double)(twobyteToHexStringToDec(tmpfirst,tmpend)))/100);
		return result;
	}

	private static int twobyteToHexStringToDec(byte tmpfirst, byte tmpend) {//2个byte型转换为十进制
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

	private static String bytesToHexStringToDecString(byte[] contror_code) {//byte型转换为十六进制
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

	private static int bytesToHexStringToInt(byte[] contror_code) {//byte型转换为十六进制
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

	private static int byteToHexStringToDec(byte contror_code) {//byte型转换为十六进制
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

	private static String byteToHexStringToDecString(byte contror_code) {//byte型转换为十六进制
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


	// 初始化控件
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


    private LinearLayout generalsend_command;
	private GridLayout factory_data;
	private EditText Serial_Num;
	private EditText CompensationStepNum;
	private EditText Driving_scheme;
	private EditText version_software;
	private Switch Open_Bluetooth;
	private EditText Open_Bluetooth_Read;
	private	EditText Concentration_change_read;

	private GridLayout system_set;
	private GridLayout basal_rate_set_one;
	private GridLayout basal_rate_set_two;
	private GridLayout basal_rate_set_three;
    private EditText control_code;
    private Button general_sendbut;

	private List<ChatMsgFmt> chat_list = new ArrayList<ChatMsgFmt>();
	private ChatAdapater chat_list_adapter;
	private ArrayAdapter<String> fmt_adapter;
	private static final String FMT_SELCET[] = { "Str", "Hex", "Dec" };
    private ArrayAdapter<String> send_adapter;
    private static final String SEND_SELCET[] = { "设置出厂数据", "读取出厂数据", "设置系统设置数据", "读取系统设置数据",
                                                     "设置基础率数据", "读取基础率数据", "读取运行状态数据", "读取即时大剂量记录",
                                                        "读取定时大剂量记录", "读取暂停泵记录", "读取基础率记录", "读取暂时率记录",
                                                           "读取日总量记录", "读取事件记录", "读取排气记录"};

    /*  private static final String SEND_SELCET[] = { "A8080100001678A2", "A80811000017BDA2", "A808130000B67DA2",
                                                  "A80814000007BCA2", "A80820000187B2A2", "A808210001D672A2",
                                                  "A8082200012672A2", "A80823000177B2A2", "A808240001C673A2",
                                                  "A80825000197B3A2", "A80826000167B3A2", "A8082700013673A2"};*/
	private int write_fmt_int; // 发送数据格式 整形
    private int send_fmt_int = 0;
	private int read_fmt_int = 0; // 接收数据格式 整形
	private int proper = 0; // 通道权限
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

        generalsend_command = (LinearLayout) findViewById(R.id.generalsend_command);

		factory_data = (GridLayout) findViewById(R.id.factorydata);
		Serial_Num = (EditText) findViewById(R.id.serial_num);
		CompensationStepNum = (EditText) findViewById(R.id.CompensationStepNum);
		Driving_scheme = (EditText) findViewById(R.id.Driving_scheme);
		version_software = (EditText) findViewById(R.id.version_software);
		Open_Bluetooth = (Switch) findViewById(R.id.open_bluetooth);
		Open_Bluetooth_Read = (EditText) findViewById(R.id.open_bluetooth_read);
		Concentration_change_read = (EditText) findViewById(R.id.Concentration_change_read);

		system_set = (GridLayout) findViewById(R.id.system_set);
		basal_rate_set_one =(GridLayout) findViewById(R.id.basal_rate_set_one);
		basal_rate_set_two =(GridLayout) findViewById(R.id.basal_rate_set_two);
		basal_rate_set_three =(GridLayout) findViewById(R.id.basal_rate_set_three);
        control_code = (EditText) findViewById(R.id.Control_code);//控制码
        general_sendbut = (Button) findViewById(R.id.General_sendbutton);

		// 初始化控件参数
		talking_conect_flag_txt.setText("已连接");
		fmt_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FMT_SELCET);
		fmt_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		read_fmt_select.setAdapter(fmt_adapter); // 发送和读取数据格式
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
						edit_string_id.setVisibility(View.VISIBLE); // 显示
						edit_hex_id.setVisibility(View.GONE); // 隐藏
						edit_shi_id.setVisibility(View.GONE); // 隐藏
						edit_string_id.setFocusable(true);
						edit_string_id.setFocusableInTouchMode(true);
						edit_string_id.requestFocus();
						break; // 字符串
					case 1:
						edit_string_id.setVisibility(View.GONE); // 显示
						edit_hex_id.setVisibility(View.VISIBLE); // 隐藏
						edit_shi_id.setVisibility(View.GONE); // 隐藏
						edit_hex_id.setFocusable(true);
						edit_hex_id.setFocusableInTouchMode(true);
						edit_hex_id.requestFocus();
						break; // 16进制
					case 2:
						edit_string_id.setVisibility(View.GONE); // 显示
						edit_hex_id.setVisibility(View.GONE); // 隐藏
						edit_shi_id.setVisibility(View.VISIBLE); // 隐藏
						edit_shi_id.setFocusable(true);
						edit_shi_id.setFocusableInTouchMode(true);
						edit_shi_id.requestFocus();
						break; // 10进制
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

				if(false) {
					if (send_fmt_int < 7) {//记录
						generalsend_command.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
					} else {
						generalsend_command.setVisibility(View.VISIBLE);//可见
					}

					if (send_fmt_int < 2) {//出厂数据
						factory_data.setVisibility(View.VISIBLE);//可见
					} else {
						factory_data.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
					}

					if ((send_fmt_int == 2) || (send_fmt_int == 3)) {//系统设置
						system_set.setVisibility(View.VISIBLE);//可见
					} else {
						system_set.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
					}

					if ((send_fmt_int == 4) || (send_fmt_int == 5)) {//基础率
						basal_rate_set_one.setVisibility(View.VISIBLE);//可见
					} else {
						basal_rate_set_one.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
					}
				}

				factory_data.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
				system_set.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
				basal_rate_set_one.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
				generalsend_command.setVisibility(View.GONE);//隐藏不参与布局（不占地方）


				switch (send_fmt_int) {
					case CommandsFound.SET_FACTORYDATA:
						Log.v("zhq_log CommandsFound SET_FACTORYDATA ", "" + CommandsFound.SET_FACTORYDATA);
						factory_data.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_FACTORYDATA:
						Log.v("zhq_log CommandsFound READ_FACTORYDATA ", "" + CommandsFound.READ_FACTORYDATA);
						break;
					case CommandsFound.SET_SYSSETDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.SET_SYSSETDATA);
						system_set.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_SYSSETDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_SYSSETDATA);
						break;
					case CommandsFound.SET_BASALDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.SET_BASALDATA);
						basal_rate_set_one.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_BASALDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_BASALDATA);

						break;
					case CommandsFound.READ_RUNNINGDATA:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_RUNNINGDATA);

						break;
					case CommandsFound.READ_MEALBOLUSRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_MEALBOLUSRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_TIMEBOLUSRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_TIMEBOLUSRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_SUSPENDEDPUMPRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_SUSPENDEDPUMPRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_BASALRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_BASALRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_TEMPORARYRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_TEMPORARYRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_DAILYTOTALRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_DAILYTOTALRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_EVENTRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_EVENTRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					case CommandsFound.READ_EXHAUSTRECORD:
						Log.v("zhq_log CommandsFound", "" + CommandsFound.READ_EVENTRECORD);
						generalsend_command.setVisibility(View.VISIBLE);//可见
						break;
					default:
						break;
				}

				if (true) {
					//=============================================================================================
                    /*byte[] sendmsg = getNewMsgEdit(true); // 发送数据
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
						Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_LONG)
								.show();
						return;
					}

					String newsendmsg = null;
					try {
						newsendmsg = new String(sendmsg, "utf-8");//GB2312
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//ChatMsgFmt entity = new ChatMsgFmt("手机端", SEND_SELCET[send_fmt_int], MESSAGE_FROM.ME);
					ChatMsgFmt entity = new ChatMsgFmt("手机端", newsendmsg, MESSAGE_FROM.ME);
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
		Open_Bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					//选中时 do some thing
					Open_Bluetooth.setText("开");
				} else {
					//非选中时 do some thing
					Open_Bluetooth.setText("关");
				}
			}
		});

		// 查看是有什么权限
		proper = mBluetoothGattCharacteristic.getProperties();
		if (0 != (proper & 0x02)) { // 可读
			talking_read_btn.setVisibility(View.VISIBLE);
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))) { // 可写
			writeable_Layout.setVisibility(View.VISIBLE);
            alwaysuse_Layout.setVisibility(View.INVISIBLE);//不可见
            if(0 != (proper & 0x02)) {//可读
                alwaysuse_Layout.setVisibility(View.VISIBLE);//可见 打开常用命令
            }
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // 通知
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, true);
			BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
					.getDescriptor(UUID
							.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Tools.mBLEService.mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	// 消息适配器
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

	// 消息是自己发送还是接收
	private enum MESSAGE_FROM {
		ME, OTHERS
	}

	// 聊天内容
	private class ChatMsgFmt {
		private String name; // 名字
		private String msg; // 信息
		private MESSAGE_FROM from; // 接受还是发送

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

	// 定时发送数据
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

			byte[] sendmsg = getMsgEdit(true); // 发送数据 sendmsg = getMsgEdit(false); // 发送数据
			if (sendmsg == null) {
				return;
			}
            Log.v("zhq_log sendmsg", "" + sendmsg);
			mBluetoothGattCharacteristic.setValue(sendmsg);
			Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
		}
	};

	// 按钮监听
	@Override
	public void onClick(View v) {

		if (v == talking_clear_btn) { // 清空会话
			chat_list.clear();
			chat_list_adapter.notifyDataSetChanged();
			return;
		}
		if (!Tools.mBLEService.isConnected()) {
			Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_LONG)
					.show();
			return;
		}
        if (v == general_sendbut) { // 发送按钮
            byte[] sendmsg = getNewMsgEdit(true);
            if (sendmsg == null) {
                return;
            }
            Log.v("zhq_log 常用 sendmsg", "" + sendmsg);
            mBluetoothGattCharacteristic.setValue(sendmsg);
            Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
            return;
        }
		if (v == sendbuttonid) { // 发送按钮
			byte[] sendmsg = getMsgEdit(true);
			if (sendmsg == null) {
				return;
			}
            Log.v("zhq_log sendmsg", "" + sendmsg);
			mBluetoothGattCharacteristic.setValue(sendmsg);
			Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
			return;
		}
		if (v == talking_read_btn) { // 读取按钮
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
			return;
		}
		if (v == send_onTime_checkbox) { // 定时发送数据
			if (send_onTime_checkbox.isChecked()) {
				sendontime_handl.sendEmptyMessage(0);
			}
		}
	}

	// 获取输入内容
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
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);//小写字母，否则改为‘A’
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
			for (byte_size = 0; data_int != 0; byte_size++) { // 计算占用字节数
				data_int /= 256;
			}
			write_msg_byte = new byte[byte_size];

			data_int = Integer.parseInt(tmp_str);
			for (int i = 0; i < byte_size; i++) { // 转换
				write_msg_byte[i] = (byte) (0xFF & (data_int % 256));
				data_int /= 256;
			}

			break;
		}

		if (0 == tmp_str.length())
			return null;
		// 显示
		if (dis_flag) {
			//ChatMsgFmt entity = new ChatMsgFmt("Me", tmp_str, MESSAGE_FROM.ME);
            ChatMsgFmt entity = new ChatMsgFmt("手机端", tmp_str, MESSAGE_FROM.ME);
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
     * 演示枚举类型的遍历
     */

    /*private void testCommandEnum(CommandsEnum commandsEnum) {

        CommandsEnum[] allCommand = CommandsEnum.values();

        for (CommandsEnum aCommand : allCommand) {

            System.out.println("当前命令name：" + aCommand.name());

            System.out.println("当前命令ordinal：" + aCommand.ordinal());

            System.out.println("当前命令：" + aCommand);

            if(aCommand.ordinal() == send_fmt_int){
                return ;
            }

        }

    }*/


    //获取常用内容
    private byte[] getNewMsgEdit(boolean dis_flag) {
        String tmp_str = "";//发送数据
        byte[] tmp_byte = null;
        byte[] write_msg_byte = null;
        String tmp_contror_code = "00";//控制码

         //CommandsEnum commandsEnum = CommandsEnum.SET_FACTORYDATA; //枚举类型
        Log.v("zhq_log CommandsFound  send_fmt_int ",""+ send_fmt_int);

		if(send_fmt_int < 7){
			tmp_str = DataTransmission.Data_Transmission(
					SEND_SELCET[send_fmt_int],send_fmt_int,tmp_contror_code);
		}else{//控制码显示
			tmp_contror_code = control_code.getText().toString();
			if(tmp_contror_code != null && tmp_contror_code.length() != 0){
				tmp_str = DataTransmission.Data_Transmission(
						SEND_SELCET[send_fmt_int],send_fmt_int,tmp_contror_code);
			}else{
				Toast.makeText(getApplicationContext(), "请输入需要查询第几条记录", Toast.LENGTH_LONG)
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
            Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_LONG)
                    .show();
            return null;
        }

        // 显示
        if (dis_flag) {
            //ChatMsgFmt entity = new ChatMsgFmt("Me", tmp_str, MESSAGE_FROM.ME);
            ChatMsgFmt entity = new ChatMsgFmt("手机端", SEND_SELCET[send_fmt_int], MESSAGE_FROM.ME);
            chat_list.add(entity);
            chat_list_adapter.notifyDataSetChanged();
        }
        return write_msg_byte;
    }


	@Override
	protected void onResume() {
		super.onResume();
		if (Tools.mBLEService.isConnected()) {
			talking_conect_flag_txt.setText("已连接");
		} else {
			talking_conect_flag_txt.setText("已断开");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bluetoothReceiver);

		if (!Tools.mBLEService.isConnected()) {
			return;
		}

		if (0 != (proper & 0x10)) { // 去掉可通知
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, false);
		}

	}
}
