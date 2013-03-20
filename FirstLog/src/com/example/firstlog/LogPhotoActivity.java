package com.example.firstlog;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LogPhotoActivity extends Activity {

	private ImageView iv_image;

	private Button bt_camera;

	private Bitmap photo;

	private File file;

	public static final String SAVEDIR = Environment.getExternalStorageDirectory()
			.getPath() + "/FirstLog";
	private String saveDir = SAVEDIR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_photo);

		iv_image = (ImageView) findViewById(R.id.iv_image);
		bt_camera = (Button) findViewById(R.id.bt_camera);

		bt_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				destoryImage();
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					file = new File(saveDir, "temp.jpg");
					file.delete();
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(LogPhotoActivity.this, "照片创建失败了，是什么原因呢？",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, 1);
				} else {
					Toast.makeText(LogPhotoActivity.this, "您老的sdcard坏了或者没插~",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		File savePath = new File(saveDir);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
			if (file != null && file.exists()) {
				//store in sdcard
				String yearAndMonth = ((FirstLogHelper)getApplication()).getYearAndMonth();
				String time = ((FirstLogHelper)getApplication()).getTime();
				File newfile = new File(saveDir+"/"+yearAndMonth, ""+time+".jpg");
				File savePath = new File(saveDir+"/"+yearAndMonth);
				if (!savePath.exists()) {
					savePath.mkdirs();
				}
				file.renameTo(newfile);
				
		        //store in db
				SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
		        String email = statusPreferences.getString("username", "noSuchEmailUser");
				UserData userdata = new UserData();
				userdata.setEmail(email);
				userdata.setTimesec(""+time);
				userdata.setLongitude(""+((FirstLogHelper)getApplication()).getLocation().getLongitude());
				userdata.setLatitude(""+((FirstLogHelper)getApplication()).getLocation().getLatitude());
				userdata.setSort(UserData.PHOTO);
				userdata.setContent(yearAndMonth+"/"+time+".jpg");
				
				UserDataHelper userDataHelper = new UserDataHelper(LogPhotoActivity.this);
				userDataHelper.SaveUserData(userdata);
				
				//show in current activity
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = 2;
				photo = BitmapFactory.decodeFile(newfile.getPath(), option);
				iv_image.setImageBitmap(photo);
			}
		}
	}

	@Override
	protected void onDestroy() {
		destoryImage();
		super.onDestroy();
	}

	private void destoryImage() {
		if (photo != null) {
			photo.recycle();
			photo = null;
		}
	}
}
