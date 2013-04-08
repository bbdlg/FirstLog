package com.example.firstlog;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;

public class LogTextActivity extends Activity {
    private ImageView lbs;
    private boolean isLbs = false;
    private ImageView video;
    private boolean isVideo = false;
    private ImageView photo;
    private boolean isPhoto = false;
    private ImageView mark;
    private boolean isMark = false;

	private String saveDir = FirstLogHelper.localRootPath;
	private File filePhoto;
	private File fileVideo;
	private String markStr = "";
    
	private Location location = null;	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean isNetworkEnabled = false;
	private boolean isGpsEnabled = false;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.log_text);
        
        //Location Based Service，LBS
        lbs = (ImageView)findViewById(R.id.imageView_lbs);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
			
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
				setLocation(location);
				changeLbsIcon();
			}
		};
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
        lbs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        
				// 判断Use GPS satellites.是否勾选
		        isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
		        // 判断Use wireless networks 是否勾选。因该函数一直返回true，故改用其他方式
		        //boolean isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);    
		        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        if (cwjManager.getActiveNetworkInfo() != null) {
		        	isNetworkEnabled = cwjManager.getActiveNetworkInfo().isAvailable();
		        }
		        
		        Log.i("haha", "GPS:"+isGpsEnabled+", Network:"+isNetworkEnabled);
		        
		        if(!isGpsEnabled && !isNetworkEnabled) {
		        	Log.i("haha", "did't set GPS or network!");
		        	((FirstLogHelper)getApplication()).dialogTips(LogTextActivity.this, "没有网络没有GPS让我咋知道您现在在哪猫着呢？=_=");
					return;
		        }
		        
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        
		        //try to get last gps location
		        if(null == location) {
		            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        }
		        //if didn't get gps location, then try to get last network location
		        if(null == location) {
		            //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        }
		        //still can't get any location infomation, give waitting tips ...
		        if(null == location) {
		        	Toast.makeText(LogTextActivity.this, "稍安勿躁，一会儿就亮啦~", Toast.LENGTH_LONG).show();
		        }
		        else {
		        	changeLbsIcon();
		        }
			}
		});
        
        //video
        video = (ImageView)findViewById(R.id.imageView_video);
        video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					fileVideo = new File(saveDir, "temp.mp4");
					fileVideo.delete();
					if (!fileVideo.exists()) {
						try {
							fileVideo.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(LogTextActivity.this, "视频文件创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.VIDEO_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileVideo));
					startActivityForResult(intent, UserData.ENUM_VIDEO);
				} else {
					Toast.makeText(LogTextActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
				}
			
			}
		});
 
        //photo
        photo = (ImageView)findViewById(R.id.imageView_photo);
        photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					filePhoto = new File(saveDir, "temp.jpg");
					filePhoto.delete();
					if (!filePhoto.exists()) {
						try {
							filePhoto.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(LogTextActivity.this, "照片文件创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePhoto));
					startActivityForResult(intent, UserData.ENUM_PHOTO);
				} else {
					Toast.makeText(LogTextActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
				}
			
			}
		});

        //mark
        mark = (ImageView)findViewById(R.id.imageView_mark);
        mark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LogTextActivity.this, AddMarkActivity.class);
				startActivityForResult(intent, UserData.ENUM_MARK);	
			}
		});
        
        //save
        ImageButton saveButton = (ImageButton)findViewById(R.id.imageButton_save);
        saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        EditText contentEditText = (EditText)findViewById(R.id.editText_content);

		        if((contentEditText.getText().toString()).isEmpty()) {
		        	((FirstLogHelper)getApplication()).dialogTips(LogTextActivity.this, "还没有写任何内容呢~~");
		        	return;
		        }
		        
		        //store in db
/*				SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
		        String email = statusPreferences.getString("username", "noSuchEmailUser");
				UserData data = new UserData();
				data.setEmail(email);
				data.setTimesec(""+((FirstLogHelper)getApplication()).getTime());
				data.setLongitude(""+((FirstLogHelper)getApplication()).getLocation().getLongitude());
				data.setLatitude(""+((FirstLogHelper)getApplication()).getLocation().getLatitude());
				data.setSort(UserData.TEXT);
				data.setContent(contentEditText.getText().toString());
				data.setDeleted("");
				
				UserDataHelper userDataHelper = new UserDataHelper(LogTextActivity.this);
				userDataHelper.saveUserData(data);
*/
				// TODO Auto-generated method stub
				finish();
			}
		});

        //reset
        ImageButton resetButton = (ImageButton)findViewById(R.id.imageButton_delete);
        resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText contentEditText = (EditText)findViewById(R.id.editText_content);
				contentEditText.setText("");
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
		super.onActivityResult(requestCode, resultCode, data);
		
		if(RESULT_OK != resultCode) {
			Log.e("GuideActivity", "Other activity return error code:"+resultCode);
			return;
		}

		switch(requestCode) {
			case UserData.ENUM_AUDIO:
				break;
	
			case UserData.ENUM_PHOTO:
				if(null != filePhoto) {
					changePhotoIcon();	
				}
				break;
				
			case UserData.ENUM_VIDEO:
				if(null != fileVideo) {
					changeVideoIcon();
				}
				break;
			case UserData.ENUM_MARK:
				Bundle bundle = data.getExtras();
		        if((null != bundle) && (false == bundle.isEmpty())) {
		        	markStr = bundle.getString("markStr");
		        	changeMarkIcon();
		        }
				break;
		}
		
		return;
	}
	
	
	private void changeLbsIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.lbs_yes);
    	lbs.setImageDrawable(drawable);
    	isLbs = true;
	}
	
	private void changeVideoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.video_yes);
    	video.setImageDrawable(drawable);
    	isVideo = true;
	}

	private void changePhotoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.photo_yes);
    	photo.setImageDrawable(drawable);
    	isPhoto = true;
	}
	
	private void changeMarkIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.mark_yes);
    	mark.setImageDrawable(drawable);
    	isMark = true;
	}
}
