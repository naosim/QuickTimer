package com.naosim.quicktimer;

import java.util.Date;
import java.util.TimeZone;

import android.os.Handler;
import android.util.Log;

/**
 * タイマーの管理クラス 利用後は必ず、onDestoryを呼ぶこと。(アプリ終了時とか)
 * 
 * @author naosim
 * 
 */
public class CountDownTimer implements Runnable, LifeSycle {
	public static final String TAG = "CountDownTimer";
	/** タイマーの時間[ms] */
	public long interval;
	public int hour;
	public int minute;

	/** 1日をミリ秒にした値 */
	public static final long MS_DAY = 24 * 60 * 60 * 1000;
	/** 1時間をミリ秒にした値 */
	public static final long MS_HOUR = 60 * 60 * 1000;
	/** 1分をミリ秒にした値 */
	public static final long MS_MINUTE = 60 * 1000;
	/** 1秒をミリ秒にした値 */
	public static final long MS_SECOND = 1000;

	/** 開始時刻 */
	public long startDate;
	/** 定期的にループをまわすためのHandler */
	public Handler handler;
	/** イベントのリスナー */
	CountDownTimerListener countDownTimerListener;

	public CountDownTimer(CountDownTimerListener countDownTimerListener) {
		setCountDownTimerListener(countDownTimerListener);
	}

	public CountDownTimer setCountDownTimerListener(
			CountDownTimerListener countDownTimerListener) {
		this.countDownTimerListener = countDownTimerListener;
		return this;
	}

	/**
	 * タイマー時間の設定
	 * 
	 * @param interval
	 * @return
	 */
	public CountDownTimer setInterval(long interval) {
		this.interval = interval;
		this.hour = -1;
		this.minute = -1;
		return this;
	}

	/**
	 * 時刻指定の設定
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public CountDownTimer setTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		this.interval = -1;
		return this;
	}

	/**
	 * 開始 既に再生中の場合は、とくに何もしない
	 * 
	 * @return
	 */
	public CountDownTimer start() {
		Log.e("CountDownTimer", "start");
		if (isDoing()) {
			return this;
		}

		Log.e("CountDownTimer", "go");

		startDate = new Date().getTime();

		if (interval < 0) {
			interval = createInterval(hour, minute, startDate);
		}

		if (handler != null) {
			handler.removeCallbacks(this);
		}
		handler = new Handler();
		handler.postDelayed(this, 10);

		return this;
	}

	public static long createInterval(int hour, int minute, long startDate) {
		long interval = -1;

		startDate = startDate + TimeZone.getDefault().getOffset(0);

		// 本日の00:00を取得
		long now = (startDate / MS_DAY) * MS_DAY;
		long endDate = hour * MS_HOUR + minute * MS_MINUTE + now;
		interval = endDate - startDate;
		if (interval < 0) {
			// 過去の時刻だったら1日加える
			interval += MS_DAY;
		}

		return interval;
	}

	public boolean stop() {
		if (handler != null) {
			handler.removeCallbacks(this);
			handler = null;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * タイマーが起動中かどうかを判定する
	 * 
	 * @return
	 */
	public boolean isDoing() {
		// handlerが存在し、かつ、残り時間が正の値
		Log.e("CountDownTimer", "time" + getRestTime());
		return handler != null && getRestTime() >= 0;
	}

	/**
	 * 残り時間の取得
	 * 
	 * @return 残り時間[ms]
	 */
	public long getRestTime() {
		return startDate + interval - new Date().getTime();
	}

	@Override
	public void run() {
		if (this.getRestTime() > 0) {
			// 残り時間が正の場合、ループをまわす
			handler.postDelayed(this, 10);
		} else {
			// 残り時間が負の場合、終了
			// ハンドラ削除
			handler.removeCallbacks(this);
			handler = null;

			// 終了通知
			if (countDownTimerListener != null) {
				countDownTimerListener.onFinish(this);
			}
			
		}

		// リスナーへ通知
		if (countDownTimerListener != null) {
			countDownTimerListener.onDoing(new TimeSet(this.getRestTime()));
		}

	}

	// すべてリセット
	@Override
	public void onDestroy() {
		// ハンドラー解放
		if (handler != null) {
			handler.removeCallbacks(this);
			handler = null;
		}

		// メンバー解放
		 countDownTimerListener = null;
		// interval = 0;
		// startDate = 0;
	}

	/**
	 * タイマーのイベントリスナー
	 * 
	 * @author naosim
	 * 
	 */
	public static interface CountDownTimerListener {
		/**
		 * タイマー起動中のイベント
		 * 
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
	 * 
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
		 * コンストラクタ203G
		 * 
		 * @param time
		 */
		public TimeSet(long time) {
			if (time < 0) {
				return;
			}

			hour = (int) (time / MS_HOUR);
			time -= hour * MS_HOUR;

			minute = (int) (time / MS_MINUTE);
			time -= minute * MS_MINUTE;

			sec = (int) (time / (MS_SECOND));
			time -= sec * MS_SECOND;

			msec = (int) time;
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

}
