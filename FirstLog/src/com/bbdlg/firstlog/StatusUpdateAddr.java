package com.bbdlg.firstlog;

public class StatusUpdateAddr {

	private static boolean isUpdating = false;
	private static long lastTimeMsStartUpdate = 0;
	private static String latitude = null;
	private static String longitude = null;
	private static String addr = null;
	
	public static final int OUTTIME = 3000; //ms
	
	public static boolean isValidUpdate() {
		if(isUpdating == false) {
			return true;
		}
		if(System.currentTimeMillis() - lastTimeMsStartUpdate > OUTTIME) {
			isUpdating = false;
			lastTimeMsStartUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public static void setPosition(String lati, String longi) {
		latitude = lati;
		longitude = longi;
	}
//	
//	public static void setAddr(String address) {
//		addr = address;
//	}
	
	public static void setStatusUpdate(boolean status) {
		isUpdating = status;
		if(status == true) {
			latitude = null;
			longitude = null;
			addr = null;
		}
	}
	
	public static String getLatitude() {
		return latitude;
	}
	
	public static String getLongitude() {
		return longitude;
	}
	
	public static String getAddr() {
		return addr;
	}
}
