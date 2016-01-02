/*
 * Copyright 2015 Jameson Williams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nosemaj.pixphony;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

import jp.kshoji.blemidi.device.MidiInputDevice;

import org.nosemaj.pixphony.ble.BleConnectionListener;
import org.nosemaj.pixphony.ble.PixmobConnectionManager;
import org.nosemaj.pixphony.ble.PixmobDeviceListener;

import java.util.ArrayList;

public class BleListActivity extends Activity {
    private static final String TAG = BleListActivity.class.getName();

    private PixmobConnectionManager mConnectionManager = null;

    private boolean mBleInitialized = false;

    private ArrayList<MidiInputDevice> mDeviceList = new ArrayList<MidiInputDevice>();
    private ArrayAdapter<MidiInputDevice> mDeviceAdapter;
    private ListView mDeviceView = null; 

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_list);

        setupAdapter();
        setupConnectionManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConnectedDevices();
        startBleScan();
    }

    private void loadConnectedDevices() {
        for (MidiInputDevice d : mConnectionManager.getConnectedDevices()) {
            updateAdapter(d, true);
        }
    }

    @Override
    public void onPause() {
        stopBleScan();
        super.onPause();
    }

    private void startBleScan() {
        mConnectionManager.startScan();
    }

    private void stopBleScan() {
        mConnectionManager.stopScan();
    }

/*
    private static final int BLE_RESCAN_INTERVAL = 3000;
    private Handler mBleScanHandler = new Handler();
  
    Runnable mBleScanTask = new Runnable() {
        @Override 
        public void run() {
            mConnectionManager.stopScan();
            mConnectionManager.startScan(); 
            mBleScanHandler.postDelayed(mBleScanTask, BLE_RESCAN_INTERVAL);
        }
    };
  
    void startBleScan() {
        mBleScanTask.run(); 
    }
  
    void stopBleScan() {
        mBleScanHandler.removeCallbacks(mBleScanTask);
    }
*/

    private void setupAdapter() {
        mDeviceView = (ListView) findViewById(R.id.ble_device_view);
        mDeviceAdapter = 
            new ArrayAdapter<MidiInputDevice>(this, android.R.layout.simple_list_item_1, mDeviceList);
        mDeviceView.setOnItemClickListener(mDeviceListClickListener);
        mDeviceView.setAdapter(mDeviceAdapter);
    }
    
    private void updateAdapter(final MidiInputDevice bleDevice, final boolean isPresent) {
        runOnUiThread(new Runnable() {
             @Override
             public void run() {
                mDeviceAdapter.remove(bleDevice);

                if (isPresent) {
                    mDeviceAdapter.add(bleDevice);
                }
            }
        });
    }

    private void setupConnectionManager() {
        mConnectionManager = ((PixphonyApplication)getApplicationContext()).getConnectionManager();
        mConnectionManager.init();
        mConnectionManager.setBleConnectionListener(mConnectionListener);
    }

    BleConnectionListener mConnectionListener = new BleConnectionListener() {
        @Override
        public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
            super.onMidiInputDeviceAttached(midiInputDevice);
            updateAdapter(midiInputDevice, true);
        }
    
        @Override
        public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
            super.onMidiInputDeviceDetached(midiInputDevice);
            updateAdapter(midiInputDevice, false);
        }
    };

    AdapterView.OnItemClickListener mDeviceListClickListener = 
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
        
            final MidiInputDevice item = (MidiInputDevice) parent.getItemAtPosition(position);

            if (mConnectionManager != null &&
                    mConnectionManager.isInitialized()) {
                mConnectionManager.disconnectDevice(item);
            }
        }
    };
}

