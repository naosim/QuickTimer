package com.naosim.quicktimer;

import java.util.Date;
import java.util.TimeZone;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * タイマーの管理クラス 利用後は必ず、onDestoryを呼ぶこと。(アプリ終了時とか)
 * 
 * @author naosim
 * 
 */
public class CountDownTimer implements Runnable, LifeSycle {
	public static final String TAG = "CountDownTimer";

	public static final String KEY_INTERVAL = "interval";
	public static final String KEY_IS_DOING = "isDoing";
	public static final String KEY_START_DATE = "START_DATE";
	public static final String KEY_REST_TIME = "restTime";
	/** タイマーの時間[ms] */
	public long interval;
	public int hour;
	public int minute;

	/** 開始時刻 */
	public long startDate;
	public long restTime;
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

	private CountDownTimer start(long startDate) {
		Log.e("CountDownTimer", "start");
		if (isDoing()) {
			return this;
		}

		this.startDate = startDate;

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

	/**
	 * 開始 既に再生中の場合は、とくに何もしない
	 * 
	 * @return
	 */
	public CountDownTimer start() {
		return start(new Date().getTime());
	}

	public CountDownTimer restart() {
		return start(startDate);
	}

	public static long createInterval(int hour, int minute, long startDate) {
		long interval = -1;

		startDate = startDate + TimeZone.getDefault().getOffset(0);

		// 本日の00:00を取得
		long now = (startDate / DateUtils.DAY_IN_MILLIS)
				* DateUtils.DAY_IN_MILLIS;
		long endDate = hour * DateUtils.HOUR_IN_MILLIS + minute
				* DateUtils.MINUTE_IN_MILLIS + now;
		interval = endDate - startDate;
		if (interval < 0) {
			// 過去の時刻だったら1日加える
			interval += DateUtils.DAY_IN_MILLIS;
		}

		return interval;
	}

	public boolean stop() {
		if (countDownTimerListener != null) {
			countDownTimerListener.onDoing(new TimeSet(restTime));
		}

		interval = restTime;
		
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
		return handler != null && getRestTime() >= 0;
	}

	/**
	 * 残り時間の取得
	 * 
	 * @return 残り時間[ms]
	 */
	public long getRestTime() {
		Long result = startDate + interval - new Date().getTime();
		restTime = result;

		return result;
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

			hour = (int) (time / DateUtils.HOUR_IN_MILLIS);
			time -= hour * DateUtils.HOUR_IN_MILLIS;

			minute = (int) (time / DateUtils.MINUTE_IN_MILLIS);
			time -= minute * DateUtils.MINUTE_IN_MILLIS;

			sec = (int) (time / (DateUtils.SECOND_IN_MILLIS));
			time -= sec * DateUtils.SECOND_IN_MILLIS;

			msec = (int) time;
		}
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onStop() {

	}

	public void save(Bundle outState) {
		// 設定した時間を保存する
		outState.putLong(KEY_INTERVAL, restTime);
		outState.putLong(KEY_START_DATE, startDate);
		outState.putLong(KEY_REST_TIME, restTime);
		outState.putBoolean(KEY_IS_DOING, isDoing());
	}

	public void load(Bundle savedInstanceState) {
		long interval = 0;
		boolean isDoing = true;
		long startDate = new Date().getTime();
		long restTime = 0;

		interval = savedInstanceState.getLong(KEY_INTERVAL, interval);
		isDoing = savedInstanceState.getBoolean(KEY_IS_DOING, isDoing);
		startDate = savedInstanceState.getLong(KEY_START_DATE, startDate);
		restTime = savedInstanceState.getLong(KEY_REST_TIME, restTime);

		setInterval(interval);
		if (isDoing) {
			start();
		} else {
			this.startDate = startDate;
			this.restTime = restTime;
			stop();
		}

	}

}
