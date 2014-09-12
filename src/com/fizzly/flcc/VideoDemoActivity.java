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
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fizzly.flcc.device.FizzlyDevice;
import com.fizzly.flcc.effectsthread.Blink;
import com.fizzly.flcc.effectsthread.CatchFizzly;
import com.fizzly.flcc.effectsthread.FadeBlink;
import com.fizzly.flcc.effectsthread.Rainbow;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class VideoDemoActivity extends Activity {
    private final static String TAG = VideoDemoActivity.class.getSimpleName();

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
	
	// EditTexts
	private EditText mRainbowEditText   = null;
	private EditText mBlinkEditText     = null;
	private EditText mFadeBlinkEditText = null;
	private EditText mMultipleBlinkEditText = null;
	private EditText mCatchEditText   = null;
	

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
        setContentView(R.layout.test_activity_3);

        final Intent intent = getIntent();
        mDeviceName    = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState  = (TextView) findViewById(R.id.connection_state);
        mDataField        = (TextView) findViewById(R.id.data_value);

        mRainbowEditText = (EditText)findViewById(R.id.rainbowEditText);
        mBlinkEditText   = (EditText)findViewById(R.id.blinkEditText);
        mFadeBlinkEditText = (EditText)findViewById(R.id.fadeBlinkEditText);
        mMultipleBlinkEditText = (EditText)findViewById(R.id.multipleBlinkEditText);
        mCatchEditText = (EditText)findViewById(R.id.catchEditText);
        
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
    
    
    
    
    public void onCheck(View v){    	
    	if(mBluetoothLeService.getBtGatt().getServices().size() == 11){
    		mFizzlyDevice = new FizzlyDevice(mBluetoothLeService);

        	mDataField.setText("Fizzly Ok");
    	} else
    		mDataField.setText("Fizzly NOT READY");
    }
    
    
    public void onRainbowOff(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 0, 0);
    }
    
    public void onRainbowPlay(View v){
    	int millis = Integer.parseInt(mRainbowEditText.getText().toString());
    	new Rainbow(mFizzlyDevice, millis).start();  	
    }
    
    
    public void onRedBlink(View v){
    	new Blink(mFizzlyDevice, Integer.parseInt(mBlinkEditText.getText().toString()), Color.RED, 1).start();
    }
    
    public void onGreenBlink(View v){
    	new Blink(mFizzlyDevice, Integer.parseInt(mBlinkEditText.getText().toString()), Color.GREEN, 1).start();
    }
    
    public void onBlueBlink(View v){
    	new Blink(mFizzlyDevice, Integer.parseInt(mBlinkEditText.getText().toString()), Color.BLUE, 1).start();    	
    }
    
    public void onOtherColorBlink(View v){
    	
    }
    
    
    
    public void onRedFadeBlink(View v){
    	new FadeBlink(mFizzlyDevice, Integer.parseInt(mFadeBlinkEditText.getText().toString()), Color.RED, 1).start();
    }
    
    public void onGreenFadeBlink(View v){
    	new FadeBlink(mFizzlyDevice, Integer.parseInt(mFadeBlinkEditText.getText().toString()), Color.GREEN, 1).start();
    }
    
    public void onBlueFadeBlink(View v){
    	new FadeBlink(mFizzlyDevice, Integer.parseInt(mFadeBlinkEditText.getText().toString()), Color.BLUE, 1).start();
    }
    
    public void onOtherColorFadeBlink(View v){
    }
    
    
    
    public void onRedMultipleBlink(View v){

    	new Blink(mFizzlyDevice, Integer.parseInt(mMultipleBlinkEditText.getText().toString()), Color.RED, 5).start();
    }
    
    public void onGreenMultipleBlink(View v){
    	new Blink(mFizzlyDevice, Integer.parseInt(mMultipleBlinkEditText.getText().toString()), Color.GREEN, 5).start();
    }
    
    public void onBlueMultipleBlink(View v){
    	new Blink(mFizzlyDevice, Integer.parseInt(mMultipleBlinkEditText.getText().toString()), Color.YELLOW, 5).start();
    }
    
    public void onOtherColorMultipleBlink(View v){
    	
    }
    
    
    public void onCatchOff(View v){
    	mFizzlyDevice.setRgbColor(0, 0, 0, 0);
    }
    
    public void onCatchPlay(View v){
    	int millis = Integer.parseInt(mCatchEditText.getText().toString());
    	new CatchFizzly(mFizzlyDevice, millis).start();  	
    }


    

    

    
    
    
    
    
    
}
