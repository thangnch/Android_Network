package com.example.bai03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mDevicesListView = (ListView)findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view

        // Đăng ký với HĐH , khi tìm thấy device mới thì báo
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        getApplicationContext().registerReceiver(blReceiver, filter);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

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

        Button btnOnD = findViewById(R.id.btnOnD);
        btnOnD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turn_on_discovery();

                //
            }
        });

        Button btnShowPaired = findViewById(R.id.btnShowPaired);
        btnShowPaired.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPaired();

                //
            }
        });

        Button btnDiscover = findViewById(R.id.btnDiscover);
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                discoverNew();

                //
            }
        });
    }

    void turn_off()
    {

        if (!BTAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(),"Bluetooth is already OFF!",Toast.LENGTH_LONG).show();

        }
        else
        {
            BTAdapter.disable();
        }

    }
    void turn_on_discovery()
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 5000);
        startActivity(discoverableIntent);

    }

    void turn_on()
    {

        // Kiểm tra xem BT đã bật hay chưa
        if (!BTAdapter.isEnabled()) {
            // Báo lỗi
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 9999);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Bluetooth is already ON!",Toast.LENGTH_SHORT).show();
        }



    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("OK","Blueooth");
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    void discoverNew()
    {

        // Check if the device is already discovering
        if(BTAdapter.isDiscovering()){
            BTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(BTAdapter.isEnabled()) {

                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();

                mBTArrayAdapter.clear(); // clear items
                BTAdapter.startDiscovery();
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }



    }

    void showPaired()
    {
        mBTArrayAdapter.clear();
        mPairedDevices = BTAdapter.getBondedDevices();
        if(BTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();

    }
}