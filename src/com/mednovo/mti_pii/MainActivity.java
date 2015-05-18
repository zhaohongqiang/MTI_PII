package com.mednovo.mti_pii;

import java.util.ArrayList;
import java.util.List;

import com.mednovo.chart_myself.HorizontalBarChartActivity;
import com.mednovo.mtblesdk.MTBeacon;
import com.mednovo.tools.Tools;
import com.mednovo.mti_pii.BLEService.LocalBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final static int REQUEST_ENABLE_BT = 2001;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			Tools.mBLEService = binder.getService();
			if (Tools.mBLEService.initBle()) {
				// scanBle(); // ��ʼɨ���豸
				if (!Tools.mBLEService.mBluetoothAdapter.isEnabled()) {
					final Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				} else {
					scanBle(); // ��ʼɨ���豸
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				scanBle(); // ��ʼɨ���豸
			} else {
				// finish();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		bindService(new Intent(this, BLEService.class), connection,
				Context.BIND_AUTO_CREATE);

	}

	// ��ʼ���ؼ�
	private Button button;
	private LayoutInflater mInflater;
	private ListView ble_listview;
	private List<MTBeacon> scan_devices = new ArrayList<MTBeacon>();
	private List<MTBeacon> scan_devices_dis = new ArrayList<MTBeacon>();
	private BaseAdapter list_adapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.devicefmt, null);
			}

			TextView device_name_txt = (TextView) convertView.findViewById(R.id.device_name_txt);
			TextView device_rssi_txt = (TextView) convertView.findViewById(R.id.device_rssi_txt);
			TextView device_mac_txt = (TextView) convertView.findViewById(R.id.device_mac_txt);

			device_name_txt.setText(getItem(position).GetDevice().getName());
			device_mac_txt.setText("Mac: " + getItem(position).GetDevice().getAddress());
			device_rssi_txt.setText("Rssi: " + getItem(position).GetAveragerssi());

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public MTBeacon getItem(int position) {
			return scan_devices_dis.get(position);
		}

		@Override
		public int getCount() {
			return scan_devices_dis.size();
		}
	};

	private void initView() {
		mInflater = LayoutInflater.from(this);
		ble_listview = (ListView) findViewById(R.id.ble_listview);

		ble_listview.setAdapter(list_adapter);
		ble_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// scan_flag = false;
				// Tools.mBLEService.stopscanBle(mLeScanCallback);
				Intent intent = new Intent(getApplicationContext(), ServiceActivity.class);
				intent.putExtra("device", scan_devices_dis.get(position).GetDevice());
				startActivity(intent);
			}
		});

		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.button:
						/*Intent intent = new Intent(getApplicationContext(), HorizontalBarChartActivity.class);
						startActivity(intent);*/

						Intent intent = new Intent();
						intent.setClass(MainActivity.this, HorizontalBarChartActivity.class);
						startActivity(intent);
						break;
					default:
						break;
				}

			}
		});
	}

	// ��ʼɨ��
	private int scan_timer_select = 0;
	private boolean scan_flag = true;
	private Handler search_timer = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			search_timer.sendEmptyMessageDelayed(0, 500);

			if (!scan_flag) {
				return;
			}
			
			if (!Tools.mBLEService.mBluetoothAdapter.isEnabled()) {
				return;
			}

			// ɨ��ʱ�����
			switch (scan_timer_select) {
			case 1: // ��ʼɨ��
				Tools.mBLEService.scanBle(mLeScanCallback);
				break;
			case 3: // ֹͣɨ��(����)
				Tools.mBLEService.stopscanBle(mLeScanCallback); // ֹͣɨ��

				for (int i = 0; i < scan_devices.size();) { // ����
					if (scan_devices.get(i).CheckSearchcount() > 2) {
						scan_devices.remove(i);
					} else {
						i++;
					}
				}

				scan_devices_dis.clear(); // ��ʾ����
				for (MTBeacon device : scan_devices) {
					scan_devices_dis.add(device);
				}
				list_adapter.notifyDataSetChanged();

				break;

			default:
				break;
			}
			scan_timer_select = (scan_timer_select + 1) % 4;
		}

	};
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			int i = 0;
			// ����Ƿ������������豸�����Ҹ���
			for (i = 0; i < scan_devices.size(); i++) {
				if (0 == device.getAddress().compareTo(
						scan_devices.get(i).GetDevice().getAddress())) {
					scan_devices.get(i).ReflashInf(device, rssi, scanRecord); // ������Ϣ
					return;
				}
			}

			// �������豸
			scan_devices.add(new MTBeacon(device, rssi, scanRecord));
		}
	};

	private void scanBle() {
		search_timer.sendEmptyMessageDelayed(0, 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scan_flag = false;
		Tools.mBLEService.stopscanBle(mLeScanCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		scan_flag = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
        Tools.mBLEService.mBluetoothAdapter.disable();//�ر�����
	}
}
