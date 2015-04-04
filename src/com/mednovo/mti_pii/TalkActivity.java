package com.mednovo.mti_pii;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.UUID;

//import com.mednovo.tools.CommandsEnum;
//import com.mednovo.tools.CommandsNewEnum;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
					dis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}
				// 读取数据
				if (BLEService.ACTION_READ_OVER.equals(action)) {
					dis_recive_msg(intent.getByteArrayExtra("value"));
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
	private GridLayout system_set;
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
		system_set = (GridLayout) findViewById(R.id.system_set);
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

                if(send_fmt_int < 7){//记录
                    generalsend_command.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
                }else{
                    generalsend_command.setVisibility(View.VISIBLE);//可见
                }

				if(send_fmt_int < 2){//出厂数据
					factory_data.setVisibility(View.VISIBLE);//可见
				}else{
					factory_data.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
				}

				if((send_fmt_int == 2) || (send_fmt_int == 3)){//出厂数据
					system_set.setVisibility(View.VISIBLE);//可见
				}else{
					system_set.setVisibility(View.GONE);//隐藏不参与布局（不占地方）
				}

                if(true) {
                    //=============================================================================================
                    /*byte[] sendmsg = getNewMsgEdit(true); // 发送数据
                    if (sendmsg == null) {
                        return;
                    }
                    Log.v("zhq_log sendmsg", "" + sendmsg);
                    mBluetoothGattCharacteristic.setValue(sendmsg);
                    Tools.mBLEService.mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);*/
                    //=============================================================================================
                }else {
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
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);
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

        if(true) {
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

            //tmp_str = SEND_SELCET[send_fmt_int];
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
                        write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);
                    else
                        write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'a' + 10) & 0xFF);
                }
            }

            if (0 == tmp_str.length())
                return null;
        }else {
            tmp_str = SEND_SELCET[send_fmt_int];
            if (0 == tmp_str.length())
                return null;

            tmp_byte = SEND_SELCET[send_fmt_int].getBytes();
            write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
            for (int i = 0; i < tmp_byte.length; i++) {
                if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
                    if (0 == i % 2)
                        write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
                    else
                        write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
                } else {
                    if (0 == i % 2)
                        write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);
                    else
                        write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'a' + 10) & 0xFF);
                }
            }

            if (0 == tmp_str.length())
                return null;
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
