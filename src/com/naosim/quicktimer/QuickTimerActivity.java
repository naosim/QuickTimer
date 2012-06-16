package com.naosim.quicktimer;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.naosim.quicktimer.ControlButtonHelper.ControlButtonListener;
import com.naosim.quicktimer.CountDownTimer.CountDownTimerListener;
import com.naosim.quicktimer.CountDownTimer.TimeSet;
import com.naosim.quicktimer.OptionHelper.OptionListener;
import com.naosim.quicktimer.OptionHelper.StartActivityForResultListener;

public class QuickTimerActivity extends Activity implements
		CountDownTimerListener, OnClickListener, ControlButtonListener, StartActivityForResultListener {
	public static final String TAG = "QuickTimerActivity";

	/** 設定できる時間[分]の配列 */
	public static final int[] MINUTES = { 1, 2, 3, 5, 10, 15, 30, 45, 60, 90 };

	public static final long DEFAULT_TIME = 60 * DateUtils.SECOND_IN_MILLIS;

	public CountDownTimer timer = new CountDownTimer(this);
	/** 通知音を再生を管理する */
	public SoundEffectPlayer sePlayer;

	public LifeSycleManager lifeSycleManager = new LifeSycleManager();

	public Bit3ViewHelper minHelper;
	public Bit3ViewHelper secHelper;
	public Bit3ViewHelper msecHelper;

	public ControlButtonHelper controlButtonHelper;
	public OptionHelper optionHelper;

	/** タイマー中に戻るキーを押した場合に表示するダイアログ */
	public Dialog backDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// タイトルバー削除
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		sePlayer = new SoundEffectPlayer(this);
		optionHelper = new OptionHelper(getMenuInflater(), sePlayer, this, getPreferences(MODE_PRIVATE));
		sePlayer.setSeEnable(optionHelper.getSeEnable());

		minHelper = new Bit3ViewHelper((ViewGroup) findViewById(R.id.minBase))
				.setBitCount(2);
		secHelper = new Bit3ViewHelper((ViewGroup) findViewById(R.id.secBase))
				.setBitCount(2);
		msecHelper = new Bit3ViewHelper((ViewGroup) findViewById(R.id.msecBase));

		findViewById(R.id.baseView).setOnClickListener(this);

		View optionText = findViewById(R.id.optionText);
		optionText.setOnClickListener(this);
		registerForContextMenu(optionText);

		findViewById(R.id.optionText).setOnClickListener(this);

		backDialog = createBackDialog();

		if (savedInstanceState == null) {
			timer.setInterval(DEFAULT_TIME).start();
		} else {
			timer.load(savedInstanceState);
		}

		// Start/Stopのボタン
		controlButtonHelper = new ControlButtonHelper(
				(TextView) findViewById(R.id.stopText));
		controlButtonHelper.setControlButtonListener(this);
		controlButtonHelper
				.setStatus(timer.isDoing() ? ControlButtonHelper.STOP
						: ControlButtonHelper.START);

		lifeSycleManager.add(timer);

		
		lifeSycleManager.add(sePlayer);

		// TextViewのフォントを変更する
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				getString(R.string.fontFileName));
		setupFont((ViewGroup) findViewById(R.id.baseView), typeface);
	}

	/**
	 * 縦横切り替え時のイベント
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 設定した時間を保存する
		timer.save(outState);

	}

	public static void setupFont(ViewGroup baseView, Typeface typeface) {
		for (int i = 0; i < baseView.getChildCount(); i++) {
			View v = baseView.getChildAt(i);
			if (v instanceof ViewGroup) {
				setupFont((ViewGroup) v, typeface);
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(typeface);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		lifeSycleManager.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// スリープに入らないように設定
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		lifeSycleManager.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// スリープに入らない設定を解除
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		lifeSycleManager.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();
		lifeSycleManager.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		lifeSycleManager.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューアイテムを追加します
		for (int i = 0; i < MINUTES.length; i++) {
			int num = i + 1;
			menu.add(Menu.NONE, Menu.FIRST + num, Menu.NONE, "" + MINUTES[i]
					+ getString(R.string.menuMinuteText));
		}
		menu.add(Menu.NONE, Menu.FIRST + MINUTES.length + 1, Menu.NONE,
				getString(R.string.menuTime));
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
			timer.setInterval(interval).start();
			controlButtonHelper.setStatus(ControlButtonHelper.STOP);
		} else {
			showTimePickerDialog();
		}

		return true;
	}

	@Override
	public void onDoing(TimeSet timeSet) {

		// viewへセット
		msecHelper.setInt(timeSet.msec);
		secHelper.setInt(timeSet.sec);
		minHelper.setInt(timeSet.hour * 60 + timeSet.minute);
	}

	@Override
	public void onFinish(CountDownTimer timer) {
		// ringtonePlayer.play();
		sePlayer.playAlerm();
	}

	public Dialog createBackDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle(getString(android.R.string.dialog_alert_title));
		alertDialogBuilder.setMessage(getString(R.string.backAlertMessage));
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
		} else if (v.getId() == R.id.optionText) {
			// 設定画面表示
			openContextMenu(v);
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

	@Override
	public void start() {
		timer.start();
		sePlayer.playSelect();
	}

	@Override
	public void stop() {
		if (timer.stop()) {
			sePlayer.playPi();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		sePlayer.playChrip();
		optionHelper.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		sePlayer.playChrip();
		
		optionHelper.onContextItemSelected(item);

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 戻り値が大丈夫
		if (requestCode == 2 && resultCode == RESULT_OK) {
			// RingtoneのURI取得
			Uri uri = data
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (sePlayer.setUserAlerm(uri)) {
				// 指定成功
				optionHelper.setUserAlerm(uri);
			} else {
				// 指定失敗
				Toast.makeText(this, "再生の設定に失敗しました。", Toast.LENGTH_SHORT);
			}
		}
	}
}