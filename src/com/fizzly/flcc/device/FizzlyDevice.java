package com.fizzly.flcc.device;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.fizzly.flcc.FizzlyBleService;
import com.fizzly.flcc.FizzlyGattAttributes;

public class FizzlyDevice {
	
	public static final int BEEPER_ON_OFF_MODE = 0x00;
	public static final int BEEPER_BLINK_MODE  = 0x01;	
	public static final int BEEPER_TONE_LOW    = 0x00;
	public static final int BEEPER_TONE_HIGH   = 0x01;		
	public static final int BEEPER_OFF         = 0x00;
	public static final int BEEPER_ON          = 0xff;	
	
    final byte CHANGE_COLOR = 0x00;
    final byte BLINK 		= 0x01;
	
    private FizzlyBleService mBluetoothLeService;
    private BluetoothGatt mBtGatt;
    
    // BLE services
    BluetoothGattService rgbFizzlyService;
    BluetoothGattService accFizzlyService;
    BluetoothGattService beeperFizzlyService;
    
  
    UUID UIDD_RGB_SERVICE     = UUID.fromString(FizzlyGattAttributes.RGB_SERVICE_UUID); 
    UUID UIDD_RGB_COMMAND     = UUID.fromString(FizzlyGattAttributes.RGB_COMMAND_UUID);
       
    UUID UIDD_BEEPER_SERVICE  = UUID.fromString(FizzlyGattAttributes.BEEPER_SERVICE_UUID); 
    UUID UIDD_BEEPER_ENABLER  = UUID.fromString(FizzlyGattAttributes.BEEPER_ENABLER_UUID);  
    UUID UIDD_BEEPER_COMMAND  = UUID.fromString(FizzlyGattAttributes.BEEPER_COMMAND_UUID);
    
    UUID UIDD_ACC_SERVICE     = UUID.fromString(FizzlyGattAttributes.ACC_SERVICE_UUID);  
    UUID UIDD_ACC_ENABLER     = UUID.fromString(FizzlyGattAttributes.ACC_ENABLER_UUID);  
    UUID UIDD_ACC_DATA        = UUID.fromString(FizzlyGattAttributes.ACC_DATA_UUID);   
    UUID UIDD_ACC_PERIOD      = UUID.fromString(FizzlyGattAttributes.ACC_PERIOD_UUID);  
	
    
	public FizzlyDevice(FizzlyBleService mBluetoothLeService){
		this.mBluetoothLeService = mBluetoothLeService;
		
		mBtGatt = mBluetoothLeService.getBtGatt();	
		
		for(BluetoothGattService srv : mBtGatt.getServices()){
			Log.w("", "services> " + srv.getUuid().toString());
		}
		
		rgbFizzlyService    = mBtGatt.getService(UIDD_RGB_SERVICE);
		beeperFizzlyService = mBtGatt.getService(UIDD_BEEPER_SERVICE);
		accFizzlyService    = mBtGatt.getService(UIDD_ACC_SERVICE);
	}
	
	/**
	 * RGB Led
	 * 
	 * 
	 */	
	public void setRgbColor(int red, int green, int blue, int millisecTime){
		
		Log.w("FizzlyDevice setRgbColor", "(FizzlyBleService rgbFizzlyService is null: " + (rgbFizzlyService == null));
		
        BluetoothGattCharacteristic rgbCharacteristic = rgbFizzlyService.getCharacteristic(UIDD_RGB_COMMAND);
        
        byte[] msg = new byte[6];
        msg[0] = (byte)CHANGE_COLOR;
        msg[1] = (byte)red;
    	msg[2] = (byte)green;
    	msg[3] = (byte)blue;
    	msg[4] = (byte)(millisecTime/10);
    	msg[5] = (byte)0x00;
        
        mBluetoothLeService.writeCharacteristic(rgbCharacteristic, msg);
	}
	
	
	
	public void setRgbBlinkColor(int red, int green, int blue, int millisecPeriod, int blinksNumber){
        BluetoothGattCharacteristic rgbCharacteristic = rgbFizzlyService.getCharacteristic(UIDD_RGB_COMMAND);
		
		byte[] msg = new byte[6];
        msg[0] = (byte)BLINK;
        msg[1] = (byte)red;
    	msg[2] = (byte)green;
    	msg[3] = (byte)blue;
    	msg[4] = (byte)(millisecPeriod/10);
    	msg[5] = (byte)blinksNumber;
        
        mBluetoothLeService.writeCharacteristic(rgbCharacteristic, msg);
	}
	
	
	/**
	 * Beeper
	 * 
	 */	
	public void enableBeeper(){
		// abilito il servizio
		BluetoothGattCharacteristic beeperCharacteristic = beeperFizzlyService.getCharacteristic(UIDD_BEEPER_ENABLER);		
		mBluetoothLeService.writeCharacteristic(beeperCharacteristic, true);
	}
	
	
	public void turnOnBeeper(int tone){
		BluetoothGattCharacteristic beeperCharacteristic = beeperFizzlyService.getCharacteristic(UIDD_BEEPER_COMMAND);		
		
		if(tone != BEEPER_TONE_LOW && tone != BEEPER_TONE_HIGH){		
			Log.e("FizzlyDevice","Wrong tone value. Must be 0x00 or 0x01");		
			return;
		}
		
		byte[] msg = {(byte)BEEPER_ON_OFF_MODE, (byte)tone, (byte)BEEPER_ON, (byte)0x00 };	

        mBluetoothLeService.writeCharacteristic(beeperCharacteristic, msg);
	}
	
	public void turnOffBeeper(int tone){
		BluetoothGattCharacteristic beeperCharacteristic = beeperFizzlyService.getCharacteristic(UIDD_BEEPER_COMMAND);		
		
		if(tone != BEEPER_TONE_LOW && tone != BEEPER_TONE_HIGH){		
			Log.e("FizzlyDevice","Wrong tone value. Must be 0x00 or 0x01");		
			return;
		}			
		
		byte[] msg = {(byte)BEEPER_ON_OFF_MODE, (byte)tone, (byte)BEEPER_OFF, (byte)0x00 };	

        mBluetoothLeService.writeCharacteristic(beeperCharacteristic, msg);
	}
	
	
	
	public void playBeepSequence(int tone, int millisPeriod, int beepNumber){
		// abilito il servizio
		BluetoothGattCharacteristic beeperCharacteristic = beeperFizzlyService.getCharacteristic(UIDD_BEEPER_COMMAND);		
		
		if(tone != BEEPER_TONE_LOW && tone != BEEPER_TONE_HIGH){		
			Log.e("FizzlyDevice","Wrong tone value. Must be 0x00 or 0x01");		
			return;
		}		
		
		if(millisPeriod < 1){		
			Log.e("FizzlyDevice","Wrong millis period value. Must be greater than zero");		
			return;
		}	
		
		if(beepNumber < 1){		
			Log.e("FizzlyDevice","Wrong beep numbers value. Must be greater than zero");		
			return;
		}	
		
		byte[] msg = {(byte)BEEPER_BLINK_MODE, (byte)tone, (byte)(millisPeriod/10), (byte)(beepNumber) };	
		
		Log.i("", "is charat null: " + (beeperCharacteristic == null));
		Log.i("", "is msg null: " + (msg == null));

        mBluetoothLeService.writeCharacteristic(beeperCharacteristic, msg);
	}
	
	
	/**
	 * Accelorometer
	 * 
	 */
	public void enableAccelerometer(){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_ENABLER);
		mBluetoothLeService.writeCharacteristic(accCharacteristic, true);
	}
	
	public void setAccelerometerPeriod(int millis){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_PERIOD);
		byte[] msg = {(byte ) millis};
		mBluetoothLeService.writeCharacteristic(accCharacteristic, msg);
	}
	
	public void enableAccelerationNotification(){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_DATA);		
		mBluetoothLeService.setCharacteristicNotification(accCharacteristic, true);
	}
	
	public void getAcceleration(){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_DATA);		
		mBluetoothLeService.readCharacteristic(accCharacteristic);
	}
	
	
	
	

    
}
