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
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public SoundEffectPlayer(Context context) {
		this.mContext = context;
		alerm = MediaPlayer.create(context, R.raw.buz_1);
	    alerm.setLooping(false);
	    pi = MediaPlayer.create(context, R.raw.pi);
	    pi.setLooping(false);
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
