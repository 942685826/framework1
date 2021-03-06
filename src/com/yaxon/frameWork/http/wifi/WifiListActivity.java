package com.yaxon.frameWork.http.wifi;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.yaxon.frameWork.R;
import com.yaxon.frameWork.adapter.CommonAdapter;
import com.yaxon.frameWork.adapter.CommonViewHold;
import com.yaxon.frameWork.debug.ToastUtils;

public class WifiListActivity extends Activity implements OnClickListener {
    public static final String TAG = "MainActivity";
    private Button check_wifi, open_wifi, close_wifi, scan_wifi, jump_next;
    private ListView mlistView;
    protected WifiUtil mWifiAdmin;
    private List<ScanResult> mWifiList;
    public int level;
    protected String ssid;
    private CommonAdapter commonAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        mWifiAdmin = new WifiUtil(WifiListActivity.this);
        initViews();
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //="android.net.wifi.STATE_CHANGE"
        registerReceiver(mReceiver, filter);
        mlistView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(WifiListActivity.this);
                ssid = mWifiList.get(position).SSID;
                alert.setTitle(ssid);
                alert.setMessage("输入密码");
                final EditText et_password = new EditText(WifiListActivity.this);
                final SharedPreferences preferences = getSharedPreferences("wifi_password", Context.MODE_PRIVATE);
                et_password.setText(preferences.getString(ssid, ""));
                alert.setView(et_password);
                //alert.setView(view1);
                alert.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = et_password.getText().toString();
                        if (null == pw || pw.length() < 8) {
                            Toast.makeText(WifiListActivity.this, "密码至少8位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Editor editor = preferences.edit();
                        editor.putString(ssid, pw);
                        editor.commit();
                        mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(ssid, et_password.getText().toString(), 3), WifiListActivity.this);
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        //mWifiAdmin.removeWifi(mWifiAdmin.getNetworkId());
                    }
                });
                alert.create();
                alert.show();

            }
        });
    }

    /*
     * 控件初始化
     * */
    private void initViews() {
        check_wifi = (Button) findViewById(R.id.check_wifi);
        open_wifi = (Button) findViewById(R.id.open_wifi);
        close_wifi = (Button) findViewById(R.id.close_wifi);
        scan_wifi = (Button) findViewById(R.id.scan_wifi);
        jump_next = (Button) findViewById(R.id.jump_next);
        mlistView = (ListView) findViewById(R.id.wifi_list);
        check_wifi.setOnClickListener(WifiListActivity.this);
        open_wifi.setOnClickListener(WifiListActivity.this);
        close_wifi.setOnClickListener(WifiListActivity.this);
        scan_wifi.setOnClickListener(WifiListActivity.this);
        jump_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_wifi:
                mWifiAdmin.checkWifiState(WifiListActivity.this);
                break;
            case R.id.open_wifi:
                mWifiAdmin.openWifi(WifiListActivity.this);
                break;
            case R.id.close_wifi:
                mWifiAdmin.closeWifi(WifiListActivity.this);
                break;
            case R.id.scan_wifi:
                mWifiAdmin.startScan(WifiListActivity.this);
                mWifiList = mWifiAdmin.getWifiList();
                if (mWifiList != null) {
                    if (commonAdapter == null) {
                        commonAdapter = new CommonAdapter<ScanResult>(mWifiList, this, R.layout.wifi_listitem) {

                            @Override
                            public void convert(CommonViewHold commonViewHold, ScanResult scanResult) {
                                ImageView wifi_level = commonViewHold.getView(R.id.wifi_level);
                                commonViewHold.setText(R.id.ssid, scanResult.SSID);
                                level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                                if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("PSK") ||
                                        scanResult.capabilities.contains("EAP")) {
                                    wifi_level.setImageResource(R.drawable.wifi_signal_lock);
                                } else {
                                    wifi_level.setImageResource(R.drawable.wifi_signal_open);
                                }
                                wifi_level.setImageLevel(level);
                            }
                        };
                        mlistView.setAdapter(commonAdapter);
                    } else {
                        commonAdapter.setListData(mWifiList);
                    }
                    new Utility().setListViewHeightBasedOnChildren(mlistView);
                }
                break;
            case R.id.jump_next:
//			Intent it =new Intent(WifiListActivity.this,WifiInfoActivity.class);
//        	startActivity(it);
                break;
            default:
                break;
        }
    }

    /*设置listview的高度*/
    public class Utility {
        public void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

    //监听wifi状态变化
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo()
                        .getSSID();
                ToastUtils.showShort(context, wifiSSID + "连接成功");

            }
        }

    };
}
