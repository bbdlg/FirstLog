package com.example.firstlog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LogTextActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_text);
        
        //save
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        EditText contentEditText = (EditText)findViewById(R.id.content);

		        if((contentEditText.getText().toString()).isEmpty()) {
		        	((FirstLogHelper)getApplication()).dialogTips(LogTextActivity.this, "还没有写任何内容呢~~");
		        	return;
		        }
		        
		        //store in db
				SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
		        String email = statusPreferences.getString("username", "noSuchEmailUser");
				UserData data = new UserData();
				data.setEmail(email);
				data.setTimesec(""+((FirstLogHelper)getApplication()).getTime());
				data.setLongitude(""+((FirstLogHelper)getApplication()).getLocation().getLongitude());
				data.setLatitude(""+((FirstLogHelper)getApplication()).getLocation().getLatitude());
				data.setSort(UserData.TEXT);
				data.setContent(contentEditText.getText().toString());
				data.setComment("");
				
				UserDataHelper userDataHelper = new UserDataHelper(LogTextActivity.this);
				userDataHelper.SaveUserData(data);

				// TODO Auto-generated method stub
				finish();
			}
		});

        
        //reset
        Button resetButton = (Button)findViewById(R.id.reset);
        resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText contentEditText = (EditText)findViewById(R.id.content);
				contentEditText.setText("");
			}
		});
	}
}
