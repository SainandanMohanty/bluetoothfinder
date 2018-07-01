package com.example.sain.bluetoothfinder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1000;
    ArrayAdapter arrayAdapter;
    private ArrayList<String> deviceIds = new ArrayList<>();
    private ArrayList<String> deviceAddresses = new ArrayList<>();
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null && action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                setStatus(true);
            } else if (action != null && action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setStatus(false);
            } else if (action != null && action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String name = device.getName();
                String address = device.getAddress();
                String rssi = String.valueOf(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));

                if (!deviceAddresses.contains(address)) {
                    String id;
                    if (name == null) {
                        id = address;
                    } else {
                        id = address + " | " + name;
                    }

                    deviceIds.add(id);
                    deviceAddresses.add(address);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, deviceIds);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            setStatus(true);
            initiate(bluetoothAdapter);
        }
    }

    public void search(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            setStatus(true);
            initiate(bluetoothAdapter);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void initiate(BluetoothAdapter bluetoothAdapter) {
        deviceIds.clear();
        deviceAddresses.clear();
        bluetoothAdapter.startDiscovery();
    }

    private void setStatus(boolean statusBool) {
        Button search = findViewById(R.id.search);

        if (statusBool) {
            search.setText("Searching...");
            search.setEnabled(false);
        } else {
            search.setText("Search");
            search.setEnabled(true);
        }
    }
}