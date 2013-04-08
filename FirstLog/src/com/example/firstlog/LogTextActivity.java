package com.example.firstlog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;

public class LogTextActivity extends Activity {
    private ImageView lbs;
    private boolean is_lbs = false;
    
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
        
        //Location Based Service，LBS
        ImageView lbs = (ImageView)findViewById(R.id.imageView_lbs);
        lbs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
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
	
	private void changeLbsIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.lbs_yes);
    	lbs.setImageDrawable(drawable);
    	is_lbs = true;
	}
}
