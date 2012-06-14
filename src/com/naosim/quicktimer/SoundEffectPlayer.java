package com.naosim.quicktimer;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

/**
 * 通知音を再生を管理する
 * @author naosim
 *
 */
public class SoundEffectPlayer {
	/** 再生時間[msec] */
	public static final int TIME_PLAYING = 5000;
	
	protected Ringtone mRingtone;
	protected Context mContext;
	
	public MediaPlayer alerm;
	public MediaPlayer pi;
	public MediaPlayer select;
	public MediaPlayer bump;
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public SoundEffectPlayer(Context context) {
		this.mContext = context;
		alerm = MediaPlayer.create(context, R.raw.eb_win);
	    alerm.setLooping(false);
	    pi = MediaPlayer.create(context, R.raw.buy1);
	    pi.setLooping(false);
	    select = MediaPlayer.create(context, R.raw.get_2);
	    select.setLooping(false);
	    bump = MediaPlayer.create(context, R.raw.get_2);
	    bump.setLooping(false); 
	}
	
	/**
	 * 再生する
	 * 再生中に呼ばれた場合は、再生されない
	 */
	public void play() {
		if(mRingtone != null && mRingtone.isPlaying()) {
			return;
		}
		
		//通常の着信音を選択する
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mRingtone = RingtoneManager.getRingtone(mContext, uri);
        if(mRingtone != null) {
        	mRingtone.play();
        }
        
        new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				mRingtone.stop();
				
			}}, TIME_PLAYING);
	}
	
	public void playPi() {
		pi.start();
	}
	
	public void playBump() {
		bump.start();
	}
	
	public void playSelect() {
		if(select.isPlaying()) {
			select.pause();
			select.seekTo(0);
		}
		select.start();
	}
	
	public void playAlerm() {
		alerm.start();
	}
	
	public void stopAlerm() {
		if(alerm.isPlaying()) {
			alerm.pause();
			alerm.seekTo(0);
		}
	}

}
