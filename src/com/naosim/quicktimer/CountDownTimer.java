package com.naosim.quicktimer;

import java.util.Date;

import android.os.Handler;

/**
 * タイマーの管理クラス
 * 利用後は必ず、onDestoryを呼ぶこと。(アプリ終了時とか)
 * @author naosim
 *
 */
public class CountDownTimer implements Runnable {
	public static final String TAG = "CountDownTimer";
	/** タイマーの時間[ms] */
	public long interval;
	/** 開始時刻 */
	public long startDate;
	/** 定期的にループをまわすためのHandler */
	public Handler handler;
	/** イベントのリスナー */
	CountDownTimerListener countDownTimerListener;
	
	public CountDownTimer(CountDownTimerListener countDownTimerListener) {
		setCountDownTimerListener(countDownTimerListener);
	}
	
	public CountDownTimer setCountDownTimerListener(CountDownTimerListener countDownTimerListener) {
		this.countDownTimerListener = countDownTimerListener;
		return this;
	}
	
	/**
	 * タイマー時間の設定
	 * @param interval
	 * @return
	 */
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
	
	/**
	 * 残り時間の取得
	 * @return 残り時間[ms]
	 */
	public long getRestTime() {
		return startDate + interval - new Date().getTime();
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
		// ハンドラー解放
		if(handler != null) {
			handler.removeCallbacks(this);
			handler = null;
		}
		
		// メンバー解放
//		countDownTimerListener = null;
//		interval = 0;
//		startDate = 0;
	}
	
	/**
	 * タイマーのイベントリスナー
	 * @author naosim
	 *
	 */
	public static interface CountDownTimerListener {
		/**
		 * タイマー起動中のイベント
		 * @param msec
		 * @param sec
		 * @param min
		 */
		public void onDoing(TimeSet timeSet);
		
		/**
		 * タイマー終了のイベント
		 */
		public void onFinish(CountDownTimer timer);
	}
	
	/**
	 * 時間情報
	 * @author naosim
	 *
	 */
	public static class TimeSet {
		/** ミリ秒 */
		public int msec = 0;
		/** 秒 */
		public int sec = 0;
		/** 分 */
		public int minute = 0;
		/** 時間 */
		public int hour = 0;
		
		/**
		 * コンストラクタ
		 * @param time
		 */
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
