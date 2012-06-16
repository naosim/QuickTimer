package com.naosim.quicktimer;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ControlButtonHelper implements OnClickListener {
	public static final int STOP = 0;
	public static final int START = 1;
	public TextView baseView;
	public int status;
	ControlButtonListener l;
	public ControlButtonHelper(TextView baseView) {
		this.baseView = baseView;
		this.baseView.setOnClickListener(this);
	}
	
	public void setStatus(int status) {
		this.status = status;
		if(status == STOP) {
			baseView.setText(R.string.stop);
		}else {
			baseView.setText("start");
		}
	}
	
	public void setControlButtonListener(ControlButtonListener l) {
		this.l = l;
	}
	
	public interface ControlButtonListener {
		public void start();
		public void stop();
	}

	@Override
	public void onClick(View v) {
		if(l != null) {
			if(status == STOP) {
				l.stop();
			}else{
				l.start();
			}
		}
		
		status = status == STOP ? START : STOP;
		setStatus(status);
		
		
		
	}

}
