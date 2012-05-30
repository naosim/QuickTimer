package com.naosim.quicktimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.naosim.quicktimer.CountDownTimer.CountDownTimerListener;
import com.naosim.quicktimer.CountDownTimer.TimeSet;

public class QuickTimerActivity extends Activity implements CountDownTimerListener {
	
	/** 設定できる時間[分]の配列 */
    private static final int[] MINUTES = {1, 2, 3, 5, 10, 15, 30, 45, 60, 90};
    
	public Handler handler = new Handler();
	public CountDownTimer timer;
	/** 通知音を再生を管理する */
	public RingtonePlayer ringtonePlayer = new RingtonePlayer(this);
	
	public TextView min;
	public TextView sec;
	public TextView msec;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // タイトルバー削除
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        min = (TextView)findViewById(R.id.min);
        sec = (TextView)findViewById(R.id.sec);
        msec = (TextView)findViewById(R.id.msec);
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	timer = new CountDownTimer(this).setInterval(60 * 1000).start();
    	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	timer.destroy();
    }
	
//	/**
//	 * 表示の更新
//	 */
//	public void update() {
//		// 残り時間の取得
//		int[] a = CountDownTimer.formatTime(timer.getRestTime());
//		
//		// viewへセット
//		msec.setText(Utils.format3(a[0]));
//		sec.setText(Utils.format2(a[1]));
//		min.setText(Utils.format2(a[2]));
//	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューアイテムを追加します
		for(int i = 0; i < MINUTES.length; i++) {
			int num = i + 1;
			menu.add(Menu.NONE, Menu.FIRST + num, Menu.NONE, "" + MINUTES[i] + "分");
		}
        return super.onCreateOptionsMenu(menu);
    }

    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// 選択した時間[ms]の取得
    	int index = item.getItemId() - (Menu.FIRST + 1);
    	long interval = MINUTES[index] * 60 * 1000;
    	
    	// タイマーをセット
    	setTimer(interval);
    	
        return true;
    }
    
    /**
     * タイマーをセットする
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
		ringtonePlayer.play();
	}
	
	
}