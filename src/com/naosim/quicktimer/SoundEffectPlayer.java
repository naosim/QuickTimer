package com.naosim.quicktimer;

import com.naosim.quicktimer.OptionHelper.OptionListener;

import android.content.Context;
import android.content.Intent;
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
public class SoundEffectPlayer implements LifeSycle, OptionListener {
	/** 再生時間[msec] */
	public static final int TIME_PLAYING = 5000;
	
	protected Ringtone mRingtone;
	protected Context mContext;
	
	public MediaPlayer alerm;
	public MediaPlayer pi;
	public MediaPlayer select;
	public MediaPlayer bump;
	public MediaPlayer chirp;
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public SoundEffectPlayer(Context context) {
		this.mContext = context;
		setDefaultAlerm();
	    pi = MediaPlayer.create(context, R.raw.chirp);
	    pi.setLooping(false);
	    select = MediaPlayer.create(context, R.raw.chirp);
	    select.setLooping(false);
	    bump = MediaPlayer.create(context, R.raw.chirp);
	    bump.setLooping(false); 
	    chirp = MediaPlayer.create(context, R.raw.chirp);
	    chirp.setLooping(false); 
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
		if(!isSeEnable) {
			return;
		}
		pi.start();
	}
	
	public void playChrip() {
		if(!isSeEnable) {
			return;
		}
		chirp.start();
	}
	
	public void playBump() {
		if(!isSeEnable) {
			return;
		}
		bump.start();
	}
	
	public void playSelect() {
		if(!isSeEnable) {
			return;
		}
		
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
	
	@Override
	public void onDestroy() {
		mContext = null;
		alerm.release();
		pi.release();
		select.release();
		bump.release();
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onResume() {
		
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void onStop() {
		
	}

	public boolean isSeEnable = true;
	
	/**
	 * 効果音(アラーム音以外の音)の生成有無の設定
	 */
	public void setSeEnable(boolean isSeEnable) {
		this.isSeEnable = isSeEnable;
	}
	
	public boolean setUserAlerm(Uri uri) {
		try{
			alerm = MediaPlayer.create(mContext, uri);
			alerm.setLooping(false);
		}catch(Exception e) {
			return false;
		}catch (Error e) {
			return false;
		}
	    
	    return true;
	}
	
	public void setDefaultAlerm() {
		alerm = MediaPlayer.create(mContext, R.raw.eb_win);
	    alerm.setLooping(false);
	}

}
