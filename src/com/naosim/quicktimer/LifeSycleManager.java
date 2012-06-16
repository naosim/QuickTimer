package com.naosim.quicktimer;

import java.util.ArrayList;

public class LifeSycleManager implements LifeSycle {
	public ArrayList<LifeSycle> lifeSycleItems = new ArrayList<LifeSycle>(); 
	
	public void add(LifeSycle item) {
		lifeSycleItems.add(item);
	}
	
	public void remove(LifeSycle item) {
		lifeSycleItems.remove(item);
	}

	@Override
	public void onStart() {
		for(LifeSycle item : lifeSycleItems) {
			item.onStart();
		}
	}

	@Override
	public void onResume() {
		for(LifeSycle item : lifeSycleItems) {
			item.onResume();
		}
	}

	@Override
	public void onPause() {
		for(LifeSycle item : lifeSycleItems) {
			item.onPause();
		}
	}

	@Override
	public void onStop() {
		for(LifeSycle item : lifeSycleItems) {
			item.onStop();
		}
	}

	@Override
	public void onDestroy() {
		for(LifeSycle item : lifeSycleItems) {
			item.onDestroy();
		}
		
	}
}
