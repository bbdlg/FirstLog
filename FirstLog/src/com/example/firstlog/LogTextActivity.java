package com.example.firstlog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LogTextActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.log_text);
        
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
}
