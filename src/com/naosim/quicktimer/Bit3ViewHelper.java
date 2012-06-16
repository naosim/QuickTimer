package com.naosim.quicktimer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Bit3ViewHelper {
	public ViewGroup baseView;
	public static final int MAX_BIT = 3;
	public int bitCount = MAX_BIT;
	public Bit3ViewHelper(ViewGroup baseView) {
		this.baseView = baseView;
	}
	
	public Bit3ViewHelper setBitCount(int bitCount) {
		if(bitCount > MAX_BIT) {
			bitCount = MAX_BIT;
		}
		
		this.bitCount = bitCount;
		
		// 不要な桁を消す
		for(int i = 0; i < MAX_BIT; i++) {
			int visibility = View.GONE;
			if(i < bitCount) {
				visibility = View.VISIBLE;
			}
			baseView.getChildAt(i).setVisibility(visibility);
		}
		
		return this;
	}
	
	private void setText(String text) {
		for(int i = 0; i < MAX_BIT && i < text.length(); i++) {
			((TextView)baseView.getChildAt(i)).setText("" + text.charAt(i));
		}
	}
	
	public void setInt(int num) {
		String text = "";
		if(bitCount == 3) {
			text = Utils.format3(num);
		} else {
			text = Utils.format2(num);
		}
		
		setText(text);
	}

}
