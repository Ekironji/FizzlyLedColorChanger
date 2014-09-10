package com.fizzly.flcc.effectsthread;

import android.graphics.Color;
import android.util.Log;

import com.fizzly.flcc.device.FizzlyDevice;

public class Blink extends Thread{
	
	private int millis = 100;
	private FizzlyDevice mFizzlyDevice = null;
	private int color = 0;
	private int number = 1;
	
	public Blink (FizzlyDevice mFizzlyDevice, int millis, int color, int number){
		this.millis = millis;
		this.mFizzlyDevice = mFizzlyDevice;
		this.color = color;
		this.number = number;
		
		Log.i("Blink",Color.red(color) + " " + Color.green(color) + " " + Color.blue(color) );
		
	}

	@Override
	public void run() {
		super.run();
    	mFizzlyDevice.setRgbBlinkColor(Color.red(color), Color.green(color), Color.blue(color), millis, number);
	}
	
}
