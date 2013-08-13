package com.example.firstlog;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class GuideActivity extends Activity {
	
	private String saveDir = FirstLogHelper.localRootPath;
	public static boolean startRecordAudio = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.guide);
        
        new Handler();
        
        //check main directory
        File dirFirstFile=new File(saveDir);  
        if(!dirFirstFile.exists())	{  
             dirFirstFile.mkdir(); 
        }
        
        //start text
        Button textButton = (Button)findViewById(R.id.button_startlog);
        textButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, LogTextActivity.class);
				startActivity(intent);
			}
		});      
        
        //show log
        Button showlogButton = (Button)findViewById(R.id.button_showlog);
        showlogButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, ShowLogActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "您一定要抛弃我么:(");
			MessageBox msgBox =  new MessageBox(this);
	        int ret = msgBox.showDialog("确定要退出么？如果正在上传或下载数据，建议您稍候再退出 :)", "注意");
	    	if(1 == ret) {
	    		android.os.Process.killProcess(android.os.Process.myPid());
	    	}
			return true;
		}
		return false;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    	menu.add(Menu.NONE, Menu.FIRST + 1, 1, "切换账号");
    	menu.add(Menu.NONE, Menu.FIRST + 2, 2, "上传数据");
    	menu.add(Menu.NONE, Menu.FIRST + 3, 3, "下载数据");
    	menu.add(Menu.NONE, Menu.FIRST + 4, 4, "关于");

    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Menu.FIRST + 1:	//change user id
        	Editor status = getSharedPreferences("firstlog", 0).edit();
        	status.putString("haveLogined", "no");
        	status.commit();
        	
        	SharedPreferences sharedPreferences = getSharedPreferences("firstlog", 0);
        	String email = sharedPreferences.getString("username", "noSuchEmailUser");
        	
            Toast.makeText(this, "当前账号<"+email+">已注销", Toast.LENGTH_LONG).show();
            
            Intent intent = new Intent();
			intent.setClass(GuideActivity.this, LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("email", email);
			intent.putExtras(bundle);
			startActivity(intent);
			GuideActivity.this.finish();
            break;
            
        case Menu.FIRST + 2:	//upload files and sync data
        	MessageBox msgBox =  new MessageBox(this);
	        int ret = msgBox.showDialog("上传数据可能产生较大流量，建议在wifi网络下进行。", "温馨提示");
	    	if(0 == ret) {
	    		break;
	    	}
	    	
            SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
            String curuser = statusPreferences.getString("username", "noSuchEmailUser");
            String token = statusPreferences.getString("token_of_"+curuser, "no_token");
			if(token.equals("no_token")) {
				Log.e("sync", "token has not found");
				Toast.makeText(this, "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
			}
			else {				
				FirstLogHelper.token = token;
				
				Log.w("sync", "start sync ...");
				Toast.makeText(this, "开始上传本地文件到云端，并同步logs...", Toast.LENGTH_LONG).show();
				
				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
				fileSyncHelper.syncFiles("upload");
				
				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
				tableSyncHelper.syncTables();
				
				//Log.w("sync", "finish sync!");
				//Toast.makeText(this, "恭喜恭喜！本地文件上传完成，logs同步完成~", Toast.LENGTH_LONG).show();
			}

            break;
            
        case Menu.FIRST + 3:	//download files
        	//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "本操作将下载云端所有的照片、视频、语音等，可能占用大量空间，请慎重:)");
	        MessageBox msgBox1 =  new MessageBox(this);
	        int ret1 = msgBox1.showDialog("下载数据可能产生较大流量，建议在wifi网络下进行。同时如果您使用了较长时间的firstlog，本操作还可能会占用您本地SD卡的较大空间，请慎重", "温馨提示");
        	if(0 == ret1) {
        		break;
        	}
	        
            SharedPreferences statusPreferences1 = getSharedPreferences("firstlog", 0);
            String curuser1 = statusPreferences1.getString("username", "noSuchEmailUser");
            String token1 = statusPreferences1.getString("token_of_"+curuser1, "no_token");
			if(token1.equals("no_token")) {
				Log.e("sync", "token has not found");
				Toast.makeText(this, "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
			}
			else {				
				FirstLogHelper.token = token1;
				
				Log.w("sync", "start sync ...");
				Toast.makeText(this, "开始从云端下载文件...", Toast.LENGTH_LONG).show();
				
				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
				fileSyncHelper.syncFiles("download");
				
				//Log.w("sync", "finish sync!");
				//Toast.makeText(this, "恭喜恭喜！云端的所有文件均已下载到本地~", Toast.LENGTH_LONG).show();
			}
			
        	break;
        	
        case Menu.FIRST + 4:	//about
        	Intent aboutIntent = new Intent();
        	aboutIntent.setClass(GuideActivity.this, AboutActivity.class);
        	startActivity(aboutIntent);
        
        	break;
        }
        
        return false;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
	
	
}
