package com.naosim.quicktimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * オプションボタンを押したときの動作を助ける
 * 
 * @author naosim
 * 
 */
public class OptionHelper {
	MenuInflater inflater;
	OptionListener l;
	StartActivityForResultListener l2;
	SharedPreferences pref;

	public OptionHelper(MenuInflater inflater, OptionListener l,StartActivityForResultListener l2,
			SharedPreferences pref) {
		this.inflater = inflater;
		this.l = l;
		this.l2 = l2;
		this.pref = pref;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		inflater.inflate(R.menu.context_menu, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.seOn) {
			l.setSeEnable(true);
			setSeEnable(true);
		} else if (id == R.id.seOff) {
			l.setSeEnable(false);
			setSeEnable(false);
		} else if (id == R.id.defaultAlerm) {
			l.setDefaultAlerm();
			setUserAlerm(null);
		} else if (id == R.id.userAlerm) {
			// ユーザ指定の着信音 音声選択開始
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
					RingtoneManager.TYPE_ALL);
			l2.startActivityForResult(intent, 2);
		}

		return true;
	}

	public interface OptionListener {
		public void setSeEnable(boolean isSeEnable);
		public void setDefaultAlerm();
		public boolean setUserAlerm(Uri uri);
	}
	
	public interface StartActivityForResultListener {
		public void startActivityForResult(Intent intent, int requestCode);
	}

	public static final String KEY_SE_ENABLE = "KEY_SE_ENABLE";
	public static final String KEY_ALERM_URI = "KEY_ALERM_URI";

	public boolean getSeEnable() {
		return pref.getBoolean(KEY_SE_ENABLE, true);
	}

	public void setSeEnable(boolean enable) {
		Editor e = pref.edit();
		e.putBoolean(KEY_SE_ENABLE, enable);
		e.commit();
	}

	public Uri getAlermUri() {
		String uri = pref.getString(KEY_ALERM_URI, null);
		if (TextUtils.isEmpty(uri)) {
			return null;
		}
		Uri result = Uri.parse(uri);
		if(result == null) {
			Log.e("getAlermUri", "uri is null");
		}

		Log.e("getAlermUri", uri);
		
		return Uri.parse(uri);
	}

	public void setUserAlerm(Uri uri) {
		Editor e = pref.edit();
		if (uri == null) {
			e.putString(KEY_ALERM_URI, null);
		} else {
			Log.e("setUserAlerm path", uri.getPath());
			Log.e("setUserAlerm uri", uri.toString());
			e.putString(KEY_ALERM_URI, uri.toString());
		}
		e.commit();
	}

}
