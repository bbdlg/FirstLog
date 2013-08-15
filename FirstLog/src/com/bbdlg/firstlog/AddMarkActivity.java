package com.bbdlg.firstlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class AddMarkActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.add_mark);
        
        Button button = (Button)findViewById(R.id.button_add);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText editText = (EditText)findViewById(R.id.editText_mark);
				Intent intent = new Intent();
				intent.putExtra("markStr", editText.getText().toString());
				AddMarkActivity.this.setResult(RESULT_OK, intent);
				finish();
			}
		});
        
	}

}
