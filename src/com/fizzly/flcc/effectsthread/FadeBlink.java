package com.fizzly.flcc.effectsthread;

import android.graphics.Color;

import com.fizzly.flcc.device.FizzlyDevice;

public class FadeBlink extends Thread{
	
	
	private int millis = 100;
	private FizzlyDevice mFizzlyDevice = null;
	private int color = 0;
	private int number = 1;
	
	public FadeBlink (FizzlyDevice mFizzlyDevice, int millis, int color, int number){
		this.millis = millis;
		this.mFizzlyDevice = mFizzlyDevice;
		this.color = color;
		this.number = number;
	}

	@Override
	public void run() {
		super.run();

    	mFizzlyDevice.setRgbColor(Color.red(color), Color.green(color), Color.blue(color), millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(0, 0, 0, millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	   	
	}
	
}
