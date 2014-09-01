package com.fizzly.flcc.device;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.fizzly.flcc.BluetoothLeService;
import com.fizzly.flcc.SampleGattAttributes;

public class FizzlyDevice {
	
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGatt mBtGatt;
    
    // BLE services

    BluetoothGattService rgbFizzlyService;
    BluetoothGattService accFizzlyService;
    
    final byte CHANGE_COLOR = 0x00;
    final byte BLINK 		= 0x01;
    
    UUID UIDD_RGB_SERVICE = UUID.fromString(SampleGattAttributes.RGB_SERVICE_UUID); 
    UUID UIDD_RGB_COMMAND  = UUID.fromString(SampleGattAttributes.RGB_COMMAND_UUID);
    
    UUID UIDD_ACC_SERVICE = UUID.fromString(SampleGattAttributes.ACC_SERVICE_UUID);  
    UUID UIDD_ACC_ENABLER = UUID.fromString(SampleGattAttributes.ACC_ENABLER_UUID);  
    UUID UIDD_ACC_DATA = UUID.fromString(SampleGattAttributes.ACC_DATA_UUID);  
	
    
	public FizzlyDevice(BluetoothLeService mBluetoothLeService){
	
		this.mBluetoothLeService = mBluetoothLeService;
		
		mBtGatt = mBluetoothLeService.getBtGatt();		
		
		rgbFizzlyService = mBtGatt.getService(UIDD_RGB_SERVICE);
		accFizzlyService = mBtGatt.getService(UIDD_ACC_SERVICE);
	}
	
	
	public void setRgbColor(int red, int green, int blue, int millisecTime){
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
	
	
	
	public void setRgbBlinkColor(){
		
	}
	
	
	
	public void enableAccelerometer(){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_ENABLER);
		byte[] msg = {0x01};
		mBluetoothLeService.writeCharacteristic(accCharacteristic, msg);
	}
	
	// lettura dati accellerometro
	public void getAccelerationValues(){
		BluetoothGattCharacteristic accCharacteristic = accFizzlyService.getCharacteristic(UIDD_ACC_DATA);		
		mBluetoothLeService.readCharacteristic(accCharacteristic);
	}
	
	
	
	

    
}
