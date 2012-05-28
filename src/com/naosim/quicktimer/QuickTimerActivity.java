package com.naosim.quicktimer;

import java.text.NumberFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class QuickTimerActivity extends Activity implements Runnable {
	
	Handler handler = new Handler();
	CountDownTimer timer;
	
	TextView min;
	TextView sec;
	TextView msec;
	
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
		
		handler.postDelayed(this, 10);
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
	
	
}