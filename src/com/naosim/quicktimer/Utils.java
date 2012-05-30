package com.naosim.quicktimer;

public class Utils {
	/**
	 * 2桁の数字のフォーマッター
	 * @param n
	 * @return
	 */
	public static String format2(int n) {
		return n < 10 ? "0" + n : "" + n; 
	}
	
	/**
	 * 3桁の数字のフォーマッター
	 * @param n
	 * @return
	 */
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
