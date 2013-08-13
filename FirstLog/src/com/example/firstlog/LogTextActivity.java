package com.example.firstlog;

import java.io.File;
import java.io.IOException;

import com.baidu.location.BDLocation;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;

public class LogTextActivity extends Activity {
	private BDLocation location = null;
	
	private void setLocation() {
		this.location = ((FirstLogHelper)getApplication()).getLocation();
	}

	private BDLocation getLocation() {
		return location;
	}
	//private LocationClient mLocClient;

	private EditText text;
    private ImageView lbs;
    private ImageView video;
    private ImageView photo;
    private ImageView mark;
    private String saveDir = FirstLogHelper.localRootPath;
	private File filePhoto = null;
	private File fileVideo = null;
	private String markStr = "";


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.log_text);

        text 	= (EditText)findViewById(R.id.editText_content);
        lbs 	= (ImageView)findViewById(R.id.imageView_lbs);
        video 	= (ImageView)findViewById(R.id.imageView_video);
        photo 	= (ImageView)findViewById(R.id.imageView_photo);
        mark 	= (ImageView)findViewById(R.id.imageView_mark);
        
        //Location Based Service，LBS
        lbs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setLocation();
				if(null != getLocation()) {
					changeLbsIcon();
				}
			}
		});
        
        
        //video
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

	        	lbs.performClick();
		        if((text.getText().toString()).isEmpty()
		        		&& (null == fileVideo)
		        		&& (null == filePhoto)) {
		        	//((FirstLogHelper)getApplication()).dialogTips(LogTextActivity.this, "啥都不写点么？那我默认了哈");
		        	Toast.makeText(LogTextActivity.this, "啥都不写点么？那我默认了哈~ 再次按下保存按钮即可保存",
							Toast.LENGTH_LONG).show();
		        	text.setText("简单拍个爪~");
		        	return;
		        }

		        //check location (lbs)
		        if(null == getLocation()) {
		        	((FirstLogHelper)getApplication()).dialogTips(LogTextActivity.this, "稍等一下下，还木有获得您的位置信息");
		        	return;
		        }
		        
		        //store in db
		        String curtime = ((FirstLogHelper)getApplication()).getTime();
				String yearAndMonth = ((FirstLogHelper)getApplication()).getYearAndMonth();
				File newfile = null;
				UserData data = new UserData();
				UserDataHelper userDataHelper = new UserDataHelper(LogTextActivity.this);
		        SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
		        String email = statusPreferences.getString("username", "noSuchEmailUser");
		        //proc username
		        data.setEmail(email);
		        //proc time
		        data.setTimesec(curtime);
				//generate save directory
				File savePath = new File(saveDir+"/"+yearAndMonth);
				if (!savePath.exists()) {
					savePath.mkdirs();
				}
		        
				//proc lbs
		        data.setLongitude(""+getLocation().getLongitude());
		        data.setLatitude(""+getLocation().getLatitude());
				
		        //proc text
		        data.setText(text.getText().toString());
				
		        //proc video
				if(null != fileVideo) {
					String newVideoName = "/"+yearAndMonth + "/" + curtime+".mp4";
					String fullVideoName = saveDir + newVideoName;
					newfile= new File(fullVideoName);
					fileVideo.renameTo(newfile);
					fileVideo.delete();
			        data.setVideo(newVideoName);
				}
				
				//proc photo
				if(null != filePhoto) {
					String newFileName = "/"+yearAndMonth+"/"+curtime+".jpg";
					String fullFileName = saveDir + newFileName;
					newfile= new File(fullFileName);
					filePhoto.renameTo(newfile);
					filePhoto.delete();
			        data.setPhoto(newFileName);
				}
				
				//proc mark
				data.setMark(markStr);
				
				//proc address
				data.setAddress(getLocation().getAddrStr());
				
				//save data
				userDataHelper.saveUserData(data);

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
				resetLbsIcon();
				resetMarkIcon();
				resetPhotoIcon();
				resetVideoIcon();
				filePhoto = null;
				fileVideo = null;
				markStr = "";
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
		        	if(!markStr.equals("")) {
		        		changeMarkIcon();
		        	}
		        }
				break;
		}
		
		return;
	}
	
	private void changeLbsIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.lbs_yes);
    	lbs.setImageDrawable(drawable);
	}
	
	private void changeVideoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.video_yes);
    	video.setImageDrawable(drawable);
	}

	private void changePhotoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.photo_yes);
    	photo.setImageDrawable(drawable);
	}
	
	private void changeMarkIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.mark_yes);
    	mark.setImageDrawable(drawable);
	}
	
	private void resetLbsIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.lbs_no);
    	lbs.setImageDrawable(drawable);
	}
	
	private void resetVideoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.video_no);
    	video.setImageDrawable(drawable);
	}

	private void resetPhotoIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.photo_no);
    	photo.setImageDrawable(drawable);
	}
	
	private void resetMarkIcon() {
    	Drawable drawable = getResources().getDrawable(R.drawable.mark_no);
    	mark.setImageDrawable(drawable);
	}
}
