/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.fizzly.flcc;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fizzly.flcc.device.FizzlyDevice;
import com.fizzly.flcc.effectsthread.Rainbow;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class CleanDeviceControlActivity extends Activity {
    private final static String TAG = CleanDeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME    = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private FizzlyBleService mBluetoothLeService;
    private BluetoothGatt mBtGatt;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;    
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    
    //-----------------------------
    final byte CHANGE_COLOR = 0x00;
    final byte BLINK 		= 0x01;
    

	private FizzlyDevice mFizzlyDevice;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((FizzlyBleService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (FizzlyBleService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);

                mDataField.setText("mBluetoothLeService is null: " + (mBluetoothLeService == null));

                mFizzlyDevice = new FizzlyDevice(mBluetoothLeService);                            
                invalidateOptionsMenu();
                
            } else if (FizzlyBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                
            } else if (FizzlyBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            	
            } else if (FizzlyBleService.ACTION_DATA_AVAILABLE.equals(action)) {
            	// da qui arrivano i dati dal BLEService quando arrivano 
            	
            	Log.i("CleanDeviceControllAcivity"," BroadcastReciever ACTION_DATA_AVAILABLE called");
            	
                displayData(intent.getStringExtra(FizzlyBleService.EXTRA_DATA));
            }
        }
    };
    
    // metodo richiamato dal Broadcast reciever quando riceve una notifica
    // 
	private void onCharacteristicChanged(String uuidStr, byte[] value) {
		
	}

	

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_2);

        final Intent intent = getIntent();
        mDeviceName    = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState  = (TextView) findViewById(R.id.connection_state);
        mDataField        = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, FizzlyBleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        mDataField.setText("tippete");
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.w(TAG, "Connect request result=" + result);
            mBtGatt = mBluetoothLeService.getBtGatt();        
            
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FizzlyBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(FizzlyBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(FizzlyBleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(FizzlyBleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    
    public void onOffClick(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 0, 1000);
    }
    
    public void onRedClick(View v){
    	mFizzlyDevice.setRgbColor(100, 0, 0, 1000);
    }
    
    public void onGreenClick(View v){
    	mFizzlyDevice.setRgbColor(0, 100, 0, 1000);
    }
    
    public void onBlueClick(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 100, 1000);
    }
    

    
    public void on5Click(View v){	
    	mFizzlyDevice.enableBeeper();
    }
    
    public void on6Click(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_LOW, 100, 5); 
    }
    
    public void on7Click(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_HIGH, 100, 5);
    }
    
    public void on8Click(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_LOW, 100, 5);
    }
    
    
    
    
    public void onRgb1(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 0, 200);
    }
    
    public void onRgb2(View v){
    	mFizzlyDevice.setRgbColor(111, 0, 0, 200);
    	
    }
    
    public void onRgb3(View v){
    	mFizzlyDevice.setRgbColor(0, 111, 0, 200);
    	
    }
    
    public void onRgb4(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 111, 200);
    	
    }
    
    public void onRgb5(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_LOW, 100, 5);
    	mFizzlyDevice.setRgbBlinkColor(111, 0, 0, 100, 5);
    }
    
    

    
    public void onBeeper1(View v){
    	mFizzlyDevice.enableBeeper();
    }
    
    public void onBeeper2(View v){
    	new Rainbow(mFizzlyDevice, 3000).start();
    }
    
    public void onBeeper3(View v){
    	
    }
    
    public void onBeeper4(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_LOW, 100, 5); 
    }
    
    public void onBeeper5(View v){
    	mFizzlyDevice.playBeepSequence(FizzlyDevice.BEEPER_TONE_HIGH, 100, 5);
    }
    

    
    public void onAcc1(View v){
    	mFizzlyDevice.enableAccelerometer();
    }
    
    public void onAcc2(View v){
    	mFizzlyDevice.setAccelerometerPeriod(100);  	
    }
    
    public void onAcc3(View v){
    	mFizzlyDevice.getAcceleration();
    }
    
    public void onAcc4(View v){
    	mFizzlyDevice.enableAccelerationNotification();
    }
    
    public void onAcc5(View v){    	
    	if(mBluetoothLeService.getBtGatt().getServices().size() == 11){
    		mFizzlyDevice = new FizzlyDevice(mBluetoothLeService);

        	mDataField.setText("Fizzly Ok");
    	} else
    		mDataField.setText("Fizzly NOT READY");
    }
    

    
    
    
    
    
    
}
