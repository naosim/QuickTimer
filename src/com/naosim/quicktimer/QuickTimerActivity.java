package com.naosim.quicktimer;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class QuickTimerActivity extends Activity implements Runnable {
	
    private static final int[] minutes = {1, 2, 3, 5, 10, 15, 30, 45, 60, 90};
    
	public Handler handler = new Handler();
	public CountDownTimer timer;
	
	public TextView min;
	public TextView sec;
	public TextView msec;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        min = (TextView)findViewById(R.id.min);
        sec = (TextView)findViewById(R.id.sec);
        msec = (TextView)findViewById(R.id.msec);
        
        timer = new CountDownTimer();
        timer.setInterval(60 * 1000);
        timer.start();
        
        update();
        
        handler.postDelayed(this, 10);
    }

	@Override
	public void run() {
		update();
		
		if(timer.getRestTime() > 0) {
			handler.postDelayed(this, 10);
		}
		else {
			playRingtone();
		}
	}
	
	Ringtone ringtone;
	
	public void playRingtone() {
		if(ringtone != null && ringtone.isPlaying()) {
			return;
		}
		
		//通常の着信音を選択する
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
        
        new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				ringtone.stop();
				
			}}, 5000);
	}
	
	public void update() {
		int[] a = CountDownTimer.formatTime(timer.getRestTime());
		msec.setText(format3(a[0]));
		sec.setText(format2(a[1]));
		min.setText(format2(a[2]));
	}
	
	public static String format2(int n) {
		return n < 10 ? "0" + n : "" + n; 
	}
	
	public static String format3(int n) {
		String p = "";
		if(n < 10) {
			p = "00";
		} else if(n < 100) {
			p = "0";
		}
		
		return p + n; 
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューアイテムを追加します
		for(int i = 0; i < minutes.length; i++) {
			int num = i + 1;
			menu.add(Menu.NONE, Menu.FIRST + num, Menu.NONE, "" + minutes[i] + "分");
		}
        return super.onCreateOptionsMenu(menu);
    }

    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	if(!timer.isDoing()) {
    		timer.start();
    	}
    	int index = item.getItemId() - (Menu.FIRST + 1);
    	timer.interval = minutes[index] * 60 * 1000;
    	
    	handler.postDelayed(this, 10);
    	
        return true;
    }
	
	
}