package com.naosim.quicktimer;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.naosim.quicktimer.CountDownTimer.CountDownTimerListener;
import com.naosim.quicktimer.CountDownTimer.TimeSet;

public class QuickTimerActivity extends Activity implements
		CountDownTimerListener, OnClickListener {

	/** 設定できる時間[分]の配列 */
	public static final int[] MINUTES = { 1, 2, 3, 5, 10, 15, 30, 45, 60, 90 };

	public static final long DEFAULT_TIME = 60 * 1000;

	public CountDownTimer timer = new CountDownTimer(this);
	/** 通知音を再生を管理する */
	public SoundEffectPlayer sePlayer;

	public TextView min;
	public TextView sec;
	public TextView msec;
	/** タイマー中に戻るキーを押した場合に表示するダイアログ */
	public Dialog backDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		

		// タイトルバー削除
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		min = (TextView) findViewById(R.id.min);
		sec = (TextView) findViewById(R.id.sec);
		msec = (TextView) findViewById(R.id.msec);

		min.setTypeface(Typeface.createFromAsset(getAssets(), "square.ttf"));

		findViewById(R.id.baseView).setOnClickListener(this);
		findViewById(R.id.stopText).setOnClickListener(this);

		backDialog = createBackDialog();

		timer.setInterval(DEFAULT_TIME).start();

		sePlayer = new SoundEffectPlayer(this);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "square.ttf");
		setupFont((ViewGroup)findViewById(R.id.baseView), typeface);
	}
	
	public static void setupFont(ViewGroup baseView, Typeface typeface) {
		for(int i = 0; i < baseView.getChildCount(); i++) {
			View v = baseView.getChildAt(i);
			if(v instanceof ViewGroup) {
				setupFont((ViewGroup)v, typeface);
			} else if(v instanceof TextView) {
				((TextView)v).setTypeface(typeface);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// スリープに入らないように設定
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// スリープに入らない設定を解除
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// タイマー解放
		timer.destroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューアイテムを追加します
		for (int i = 0; i < MINUTES.length; i++) {
			int num = i + 1;
			menu.add(Menu.NONE, Menu.FIRST + num, Menu.NONE, "" + MINUTES[i]
					+ "分");
		}
		menu.add(Menu.NONE, Menu.FIRST + MINUTES.length + 1, Menu.NONE, "時刻指定");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		sePlayer.playPi();
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
		// sePlayer.playPi();
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		sePlayer.playSelect();

		// 選択した時間[ms]の取得
		int index = item.getItemId() - (Menu.FIRST + 1);
		if (index < MINUTES.length) {
			long interval = MINUTES[index] * 60 * 1000;

			// タイマーをセット
			setTimer(interval);
		} else {
			showTimePickerDialog();
		}

		return true;
	}

	/**
	 * タイマーをセットする
	 * 
	 * @param interval
	 */
	public void setTimer(long interval) {
		timer.setInterval(interval).start();
	}

	@Override
	public void onDoing(TimeSet timeSet) {

		// viewへセット
		msec.setText(Utils.format3(timeSet.msec));
		sec.setText(Utils.format2(timeSet.sec));
		min.setText(Utils.format2(timeSet.hour * 60 + timeSet.minute));
	}

	@Override
	public void onFinish(CountDownTimer timer) {
		// ringtonePlayer.play();
		sePlayer.playAlerm();
	}

	public Dialog createBackDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("確認");
		alertDialogBuilder.setMessage("アプリを終了するとタイマーも終了しますが、よろしいですか？");
		alertDialogBuilder.setPositiveButton(android.R.string.ok,
				onClickListener);
		alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
		alertDialogBuilder.setCancelable(true);
		return alertDialogBuilder.create();
	}

	public DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};

	/**
	 * キーを押されたときのイベント タイマー起動中に戻るボタンが押された場合、アプリを終了して良いかの確認ダイアログを表示する
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// キーコード表示
		// 戻るボタンが押されたとき
		if (e.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& e.getAction() == KeyEvent.ACTION_UP) {
			if (!timer.isDoing()) {
				return super.dispatchKeyEvent(e);
			}

			// アプリ終了の確認ダイアログ
			backDialog.show();

			return true;
		}
		return super.dispatchKeyEvent(e);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.baseView) {
			sePlayer.stopAlerm();
			// 画面を押したらメニューが表示される
			openOptionsMenu();
		} else if (v.getId() == R.id.stopText) {
			if (timer.stop()) {
				sePlayer.playBump();
			}
		}

	}

	public TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Log.e("TAG", "" + hourOfDay + ", " + minute);
			timer.setTime(hourOfDay, minute).start();
		}
	};

	public void showTimePickerDialog() {
		Date now = new Date();
		final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				onTimeSetListener, now.getHours(), now.getMinutes(), true);
		timePickerDialog.show();
	}

}