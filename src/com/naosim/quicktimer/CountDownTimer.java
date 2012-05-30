package com.naosim.quicktimer;

import java.util.Date;

import android.os.Handler;

public class CountDownTimer implements Runnable {
	public static final String TAG = "CountDownTimer";
	public long interval;
	public long startDate;
	public Handler handler;
	
	CountDownTimerListener countDownTimerListener;
	
	public CountDownTimer(CountDownTimerListener countDownTimerListener) {
		setCountDownTimerListener(countDownTimerListener);
	}
	
	public CountDownTimer setCountDownTimerListener(CountDownTimerListener countDownTimerListener) {
		this.countDownTimerListener = countDownTimerListener;
		return this;
	}
	
	public CountDownTimer setInterval(long interval) {
		this.interval = interval;
		return this;
	}
	
	/**
	 *  開始
	 *  既に再生中の場合は、とくに何もしない
	 * @return
	 */
	public CountDownTimer start() {
		if(isDoing()) {
			return this;
		}
		
		startDate = new Date().getTime();
		
		if(handler != null) {
			handler.removeCallbacks(this);
		}
		handler = new Handler();
		handler.postDelayed(this, 10);
		
		return this;
	}
	
	/**
	 * タイマーが起動中かどうかを判定する
	 * @return
	 */
	public boolean isDoing() {
		// handlerが存在し、かつ、残り時間が正の値
		return handler != null && getRestTime() >= 0;
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
	
	@Override
	public void run() {
		// リスナーへ通知
		if(countDownTimerListener != null) {
			countDownTimerListener.onDoing(new TimeSet(this.getRestTime()));
		}
		if(this.getRestTime() > 0) {
			// 残り時間が正の場合、ループをまわす
			handler.postDelayed(this, 10);
		}
		else {
			// 残り時間が負の場合、終了
			// ハンドラ削除
			handler.removeCallbacks(this);
			handler  = null;
			
			// 終了通知
			countDownTimerListener.onFinish(this);
		}
	}
	
	// すべてリセット
	public void destroy() {
		countDownTimerListener = null;
		if(handler != null) {
			handler.removeCallbacks(this);
			handler = null;
		}
		
		interval = 0;
		startDate = 0;
	}
	
	public static interface CountDownTimerListener {
		/**
		 * タイマー起動中のイベント
		 * @param msec
		 * @param sec
		 * @param min
		 */
		public void onDoing(TimeSet timeSet);
		
		/**
		 * タイマー終了
		 */
		public void onFinish(CountDownTimer timer);
	}
	
	public static class TimeSet {
		public int msec = 0;
		public int sec = 0;
		public int minute = 0;
		public int hour = 0;
		
		public TimeSet(long time) {
			if(time < 0) {
				return;
			}
			
			hour = (int)time / (1000 * 60 * 60);
			time -= hour * 1000 * 60 * 60;
			
			minute = (int)time / (1000 * 60);
			time -= minute * 1000 * 60;
			
			sec = (int)time / (1000);
			time -= sec * 1000;
			
			msec = (int)time;
		}
	}
}
