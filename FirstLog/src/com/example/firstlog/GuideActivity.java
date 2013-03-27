package com.example.firstlog;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class GuideActivity extends Activity {
	
	private File file;
	private String saveDir = FirstLogHelper.localRootPath;
	public static boolean startRecordAudio = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        
        new Handler();
        
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
 
        //start audio
        Button audioButton = (Button)findViewById(R.id.startaudio);
        
        audioButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(false == startRecordAudio) {
					startRecordAudio = true;
					Button button = (Button)findViewById(R.id.startaudio);
					button.setText("说完啦");
				}
				else {
					startRecordAudio = false;
					Button button = (Button)findViewById(R.id.startaudio);
					button.setText(R.string.guide_audio);
				}
				/*
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					file = new File(saveDir, "temp.mp3");
					file.delete();
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(GuideActivity.this, "音频文件创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					//Intent intent = new Intent(
					//		"android.media.action.AUDIO_CAPTURE");
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("audio/amr");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		            //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					startActivityForResult(intent, UserData.ENUM_AUDIO);
				} else {
					Toast.makeText(GuideActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
					return;
				}
				 */
			}
		}); 
        
        //start photo
        Button photoButton = (Button)findViewById(R.id.startphoto);
        photoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					file = new File(saveDir, "temp.jpg");
					file.delete();
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(GuideActivity.this, "照片文件创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, UserData.ENUM_PHOTO);
				} else {
					Toast.makeText(GuideActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
				}
			
			}
		});   
 
        //start video
        Button videoButton = (Button)findViewById(R.id.startvideo);
        videoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					file = new File(saveDir, "temp.mp4");
					file.delete();
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(GuideActivity.this, "录像文件创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.VIDEO_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, UserData.ENUM_VIDEO);
				} else {
					Toast.makeText(GuideActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
				}
			
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
				Log.e("sync", "token has not found");
				Toast.makeText(this, "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
			}
			else {				
				FirstLogHelper.token = token;
				
				Log.w("sync", "start sync ...");
				Toast.makeText(this, "开始同步文件和数据库...", Toast.LENGTH_LONG).show();
				
				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
				fileSyncHelper.syncFiles();
				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
				tableSyncHelper.syncTables();
				
				Log.w("sync", "finish sync!");
				Toast.makeText(this, "恭喜恭喜！文件和数据库都同步完成啦~", Toast.LENGTH_LONG).show();
			}
			
        	break;

        }
        
        return false;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(RESULT_OK != resultCode) {
			Log.e("GuideActivity", "Other activity return error code:"+resultCode);
			return;
		}

		String time = ((FirstLogHelper)getApplication()).getTime();
		String name = ""+time;
		String sort = "";
				
		switch(requestCode) {
			case UserData.ENUM_AUDIO:
				name += ".mp3";
				sort = UserData.AUDIO;
				break;
	
			case UserData.ENUM_PHOTO:
				name += ".jpg";
				sort = UserData.PHOTO;
				break;
				
			case UserData.ENUM_VIDEO:
				name += ".mp4";
				sort = UserData.VIDEO;
				break;
		}
		
		if (file != null && file.exists()) {
			//store in sdcard
			String yearAndMonth = ((FirstLogHelper)getApplication()).getYearAndMonth();
			File newfile = new File(saveDir+"/"+yearAndMonth, name);
			File savePath = new File(saveDir+"/"+yearAndMonth);
			if (!savePath.exists()) {
				savePath.mkdirs();
			}
			file.renameTo(newfile);
			file.delete();
			
	        //store in db
			SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
	        String email = statusPreferences.getString("username", "noSuchEmailUser");
			UserData userdata = new UserData();
			userdata.setEmail(email);
			userdata.setTimesec(""+time);
			userdata.setLongitude(""+((FirstLogHelper)getApplication()).getLocation().getLongitude());
			userdata.setLatitude(""+((FirstLogHelper)getApplication()).getLocation().getLatitude());
			userdata.setSort(sort);
			userdata.setContent(yearAndMonth+"/"+name);
			
			UserDataHelper userDataHelper = new UserDataHelper(GuideActivity.this);
			userDataHelper.saveUserData(userdata);
		}
	}
	
}
