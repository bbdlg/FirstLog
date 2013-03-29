package com.example.firstlog;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class FirstLogHelper extends Application {
	public static final String mbAppId = "621606";
	public static final String mbApiKey = "5tyi3bBpNWPXZUZjpu7QacxP";//请替换申请客户端应用时获取的Api Key串
	public static final String remoteRootPath =  "/apps/FirstLog";
	public static final String localRootPath = Environment.getExternalStorageDirectory().getPath() + "/FirstLog";
	public static String token = null; 
	
	private Location location;	
	
	//UI thread
	public static Handler uiThreadHandler = new Handler();
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location curLoc) {
		location = curLoc;
	}
	
	/*
	 * @返回yyy-MM-dd HH:mm:ss
	 */
	public String getTime() {
		//Date currentTime = new Date();
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		//String dateString = formatter.format(currentTime);
		//return dateString;
		return String.valueOf(System.currentTimeMillis());
	}
	
	/*
	 * @返回yyy-MM
	 */
	public String getYearAndMonth() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String dateString = formatter.format(currentTime);
		return dateString;
		//return System.currentTimeMillis();
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

	public String getCurUsername() {
		SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
		String email = statusPreferences.getString("username", "noSuchEmailUser");
		return email;
	}
	
	/**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */  
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        // 获取这个图片的宽和高，注意此处的bitmap为null   
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // 设为 false   
        // 计算缩放比   
        int h = options.outHeight;  
        int w = options.outWidth;  
        int beWidth = w / width;  
        int beHeight = h / height;  
        int be = 1;  
        if (beWidth < beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 0) {  
            be = 1;  
        }  
        options.inSampleSize = be;  
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false   
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象   
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
}
