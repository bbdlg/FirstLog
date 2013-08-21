package com.bbdlg.firstlog;

import com.baidu.mobstat.StatService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String activityTag = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏可以使用如下代码： 
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        //设置无title bar可以使用如下代码： 
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        final View view = View.inflate(this, R.layout.main, null);

        setContentView(view);
        
        //check current apk is debug or release
        if(true == FirstLogHelper.isApkDebugable(MainActivity.this, getPackageName())) {
        	Toast.makeText(MainActivity.this, "当前程序是debug版本", Toast.LENGTH_LONG).show();
        }
        
		//渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
 
    }
    
    private void redirectTo() {
    	Log.i(activityTag, "main activity is showing ...");
    	//背景自动适应
        //LinearLayout layout=(LinearLayout)findViewById(R.id.layout);
        //AndroidHelper.AutoBackground(this, layout, R.drawable.bg_h, R.drawable.bg_v);
    	
		//start login activity
        Intent intent = new Intent();
        SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
        String statusString = statusPreferences.getString("haveLogined", "no");
        String email = statusPreferences.getString("username", "noSuchEmailUser");
        
        if(email.equals("noSuchEmailUser")) {
        	intent.setClass(MainActivity.this, RegActivity.class);
        }
        else if(statusString.equals("no")) {
        	intent.setClass(MainActivity.this, LoginActivity.class);
        }
        else {
        	intent.setClass(MainActivity.this, GuideActivity.class);
        }
        
        startActivity(intent);
        MainActivity.this.finish();       
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(MainActivity.this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(MainActivity.this);
	}

}
