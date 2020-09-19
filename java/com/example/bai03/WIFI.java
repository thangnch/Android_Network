package com.example.bai03;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WIFI extends AppCompatActivity {

    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_i_f_i);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        checkPermission();


        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mDevicesListView = (ListView) findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view

        Button btnOn = findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turn_on();
                //
            }
        });

        Button btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turn_off();
                //
            }
        });

        Button btnShowAvail = findViewById(R.id.btnShowAvail);
        btnShowAvail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAvail();
                //
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectFITHOU("BeeNet","0904376574");
                //
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    void checkPermission() {
        Log.d("PERM", "RUN");
        // Send SMS to 5556

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int p1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int p2 = checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE);
            int p3 = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
            int p4 = checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE);
            int p5 = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

            List<String> permissions = new ArrayList<String>();

            if (p1 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (p2 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            }
            if (p3 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (p5 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (p4 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CHANGE_NETWORK_STATE);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 9999);
            } else {
                //sendSMS();
            }
        } else {
            //sendSMS();
        }
    }

    void turn_on() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        Toast.makeText(getApplicationContext(), "WIFI is  ON!", Toast.LENGTH_SHORT).show();
    }

    void turn_off() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        Toast.makeText(getApplicationContext(), "WIFI is  OFF!", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                // scan failure handling
                scanFailure();
            }
        }
    };

    void showAvail() {
        Log.d("OK", "RUN");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            Log.d("OK", "RUN FAIL");
            // scan failure handling
            scanFailure();
        }

    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        mBTArrayAdapter.clear();

        if (results.size() > 0) {
            String wifis[] = new String[results.size()];

            // Lấy thông tin từng mạng
            for (int i = 0; i < results.size(); i++) {
                wifis[i] = results.get(i).toString();
                mBTArrayAdapter.add(wifis[i]);

            }
            mBTArrayAdapter.notifyDataSetChanged();
        }
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        mBTArrayAdapter.clear();
    }

    public void connectFITHOU(String SSID, String PASSWORD) {
        try {
            WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wc.SSID = "\"" + SSID + "\"";
            wc.preSharedKey = "\"" + PASSWORD + "\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            wifiManager.setWifiEnabled(true);
            int netId = wifiManager.addNetwork(wc);
            if (netId == -1) {
                netId = getExistingNetworkId(wc.SSID);
            }
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

}