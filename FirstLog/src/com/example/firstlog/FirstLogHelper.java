package com.example.firstlog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class FirstLogHelper extends Application {
	private Location location;
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location curLoc) {
		location = curLoc;
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}
	
    //获取屏幕方向
    public static int ScreenOrient(Activity activity)
        {
            int orient = activity.getRequestedOrientation(); 
            if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                //宽>高为横屏,反正为竖屏  
                 WindowManager windowManager = activity.getWindowManager();  
                 Display display = windowManager.getDefaultDisplay();  
                 int screenWidth  = display.getWidth();  
                 int screenHeight = display.getHeight();  
                 orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
            return orient;
        }
    public static void AutoBackground(Activity activity,View view,int Background_v, int Background_h)
    {
        int orient=ScreenOrient(activity);
        if (orient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //纵向 
            view.setBackgroundResource(Background_v);
        }else{ //横向
            view.setBackgroundResource(Background_h);
        }  
    }
    
    public void dialogConfirm(Context context, String msg) {
    	Log.w("diag", "call diaglogConfirm ... ");
		AlertDialog.Builder builder = new Builder(context);
		Log.w("diag", "call Builder ... ");
		builder.setMessage(msg);
		//builder.setTitle("提示");
		builder.setPositiveButton("你说对了",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//AccoutList.this.finish();
				//System.exit(1);
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		});
		builder.setNegativeButton("我按错了",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.create().show();
	}
	
	public void dialogTips(Context context, String msg) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		//builder.setTitle("提示");
		builder.setPositiveButton("了解", null);
		builder.show();
	}
	

	/**
	 * 毫秒转日期字符串
	 *
	 * @param str
	 * @return
	 */
	public String getDateTimeByMillisecond(String str) {
	
	  Date date = new Date(Long.valueOf(str));
	
	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	  String time = format.format(date);
	
	  return time;
}
}
