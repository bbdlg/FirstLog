package com.example.firstlog;

import java.io.File;
import java.util.List;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class GuideActivity extends Activity {
	private String mbOauth = null;
	private final static String remoteRootPath =  FirstLogHelper.remoteRootPath;
	private final static String localRootPath = FirstLogHelper.localRootPath;
	// the handler
    private Handler mbUiThreadHandler = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        
        mbUiThreadHandler = new Handler();
        
        //get location
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				//dialogTips("status changed");
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				//dialogTips(provider+" enable");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				//dialogTips(provider+" disable");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Log.i("gps", "call onLocationChanged()");
				((FirstLogHelper)getApplication()).setLocation(location);
			}
		};
		// 判断Use GPS satellites.是否勾选
        boolean isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        // 判断Use wireless networks 是否勾选。因该函数一直返回true，故改用其他方式
        //boolean isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        boolean isNetworkEnabled = false;
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null) {
        	isNetworkEnabled = cwjManager.getActiveNetworkInfo().isAvailable();
        }
        
        Log.i("haha", "GPS:"+isGpsEnabled+", Network:"+isNetworkEnabled);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        if(!isGpsEnabled && !isNetworkEnabled) {
        	//tips
        	//showDialog("");
        	Log.i("haha", "did't set GPS or network!");
        	((FirstLogHelper)getApplication()).dialogTips(GuideActivity.this, "没有网络没有GPS让我咋知道您现在在哪猫着呢？=_=");
			Log.w("diag", "call diaglogConfirm in guideActivity() ... ");
        }

        //get last known location
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null == location) {
        	Log.i("Location", "can't get location by gps, now via network");
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(null == location) {
        	Log.i("Location", "can't get location by network, now set a default value");
        	location = new Location(LocationManager.NETWORK_PROVIDER);
        }
        ((FirstLogHelper)getApplication()).setLocation(location);
        
        //start text
        Button textButton = (Button)findViewById(R.id.starttext);
        textButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, LogTextActivity.class);
				startActivity(intent);
			}
		});      
        
        //start photo
        Button photoButton = (Button)findViewById(R.id.startphoto);
        photoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, LogPhotoActivity.class);
				startActivity(intent);
			}
		});   
        
        //show log
        Button showlogButton = (Button)findViewById(R.id.showlog);
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
			((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "您一定要抛弃我么:(");
			return true;
		}
		return false;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		/*
		*
		* add()方法的四个参数，依次是：
		*
		* 1、组别，如果不分组的话就写Menu.NONE,
		*
		* 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
		*
		* 3、顺序，那个菜单现在在前面由这个参数的大小决定
		*
		* 4、文本，菜单的显示文本
		*/
	   menu.add(Menu.NONE, Menu.FIRST + 1, 1, "换个账号Log First").setIcon(android.R.drawable.ic_menu_delete);
	   menu.add(Menu.NONE, Menu.FIRST + 2, 2, "同步FirstLog到云端").setIcon(android.R.drawable.ic_menu_delete);

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
            
        case Menu.FIRST + 2:	//sync data
            SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
            String curuser = statusPreferences.getString("username", "noSuchEmailUser");
            String token = statusPreferences.getString("token_of_"+curuser, "no_token");
			if(token.equals("no_token")) {
				Log.i("sync files", "token has not found");
				//try to get new token
				
				//restore in SharedPreferences
				
			}
			else {
				Log.i("sync files", "token has found");
				mbOauth = token;
				syncFiles();
			}
			
            
        	break;

        }
        
        return false;
    }
    
    public void syncFiles() {
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);

		    		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(remoteRootPath, "name", "asc");
		    		
		    		//upload
					FileSyncHelper fileSyncHelper = new FileSyncHelper();
		    		File localDir = new File(localRootPath);		
		    		List<String> localFileList = null;
					try {
						localFileList = fileSyncHelper.showAllFiles(localDir);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
					Log.i("sync files", "localfilelist size = "+localFileList.size());
		    		for(int i=0; i<localFileList.size(); i++) {
		    			boolean noDirRemote = true;
		    			Log.i("sync files", "now process "+localFileList.get(i));
		    			String dirName = fileSyncHelper.getLastDirOfPath(localFileList.get(i));
		    			Log.i("sync files", "isDir = <"+dirName+">");
		    			if(0 != dirName.length()) {
		    				Log.i("sync files", "get local dir "+dirName);
		    				int j=0;
		    				for(j=0; (null != ret.list) && j<ret.list.size() && ret.list.get(j).isDir; j++) {
		    					String dirNameRemote = fileSyncHelper.getFileNameOfPath(ret.list.get(j).path);
		    					if(ret.list.get(j).isDir && dirNameRemote.equals(dirName)) {
			    					noDirRemote = false;
			    					break;		    						
		    					}
		    				}
		    				if(noDirRemote) {
		    					//upload local dir ...
		    					Log.i("sync files", "start mkdir remote DIR<"+dirName+">");
		    					
		    					//mkdir remote
		    					BaiduPCSClient api_mkdir = new BaiduPCSClient();
		    					api_mkdir.setAccessToken(mbOauth);
		    					final BaiduPCSActionInfo.PCSFileInfoResponse ret1 = api_mkdir.makeDir(remoteRootPath+"/"+dirName);
		    					Log.i("sync files", "mkdir in remote <"+remoteRootPath+"/"+dirName+">");
		    				}
		    				
		    			}
		    			else {
		    				String fileNameLocal = fileSyncHelper.getFileNameOfPath(localFileList.get(i));
		    				Log.i("sync files", "get local file "+fileNameLocal);
		    			}
		    		}
		    		
		    		for(int i=0; i<FirstLogHelper.uploadFileList.size(); i++) {
		    			Log.i("sync files", "filelist<"+i+"> = "+FirstLogHelper.uploadFileList.get(i));
		    		}
		    		
					fileSyncHelper.upload(GuideActivity.this);
		    		
		    		
		    		//todo filelist ...
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        //Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Toast.makeText(this,
        //        "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单",
        //        Toast.LENGTH_LONG).show();

        // 如果返回false，此方法就把用户点击menu的动作给消费了，onCreateOptionsMenu方法将不会被调用

        return true;
    }
}
