package com.naosim.quicktimer;

import java.util.Date;

public class CountDownTimer {
	public long interval;
	
	public long startDate;
	
	public CountDownTimer setInterval(long interval) {
		this.interval = interval;
		return this;
	}
	
	public void start() {
		startDate = new Date().getTime();
	}
	
	public long getRestTime() {
		return startDate + interval - new Date().getTime();
	}
	
	public static int[] formatTime(long restTime) {
		int[] result = new int[4];
		
		if(restTime < 0) {
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
			result[3] = 0;
			return result;
		}
		
		long hour = restTime / (1000 * 60 * 60);
		restTime -= hour * 1000 * 60 * 60;
		
		long minute = restTime / (1000 * 60);
		restTime -= minute * 1000 * 60;
		
		long sec = restTime / (1000);
		restTime -= sec * 1000;
	
		result[0] = (int)restTime;
		result[1] = (int)sec;
		result[2] = (int)minute;
		result[3] = (int)hour;
		
		return result;
	}
}
