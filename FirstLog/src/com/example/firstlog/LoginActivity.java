package com.example.firstlog;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;

public class LoginActivity extends Activity {
	private static List<String> m = new ArrayList<String>();;
	private EditText view ;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.login);
        
        //从数据库获取用户列表
        DataHelper dataHelper = new DataHelper(LoginActivity.this);
        List<UserInfo> userlist = dataHelper.GetUserList(null);
        //m = new String[userlist.size()];
        for(int i=0; i<userlist.size(); i++) {
        	m.add(userlist.get(i).getEmail());
        }
        
		view = (EditText) findViewById(R.id.email_edittext);
		spinner = (Spinner) findViewById(R.id.userlist);
		//将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
		
		//设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//将adapter 添加到spinner中
		spinner.setAdapter(adapter);
		
		//添加事件Spinner事件监听  
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				view.setText(m.get(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//设置默认值
		spinner.setVisibility(View.VISIBLE);
        
        //get email info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if((null != bundle) && (false == bundle.isEmpty())) {
        	String email = bundle.getString("email");
            Log.i("haha", "get Email");
            EditText emailEditText = (EditText) findViewById(R.id.email_edittext);
            emailEditText.setText(email);
            //取得添加的值的位置
            int position = adapter.getPosition(email);
            if(position < 0) {
            	adapter.add(email);
            }
            else {
            	Log.i("spinner", ""+email+" has exist in spinner<"+position+">");
            }
            position = adapter.getPosition(email);
            //将spinner定位到添加值的位置
            spinner.setSelection(position);  
        }
                
        //register a new id
       	TextView reg = (TextView)findViewById(R.id.reg_tips);
       	reg.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
       	reg.getPaint().setFakeBoldText(true);

       	reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoRegActivity();
			}
		});
       	
       	//set login_button
       	Button login = (Button)findViewById(R.id.login_button);
       	login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//check password
				DataHelper dataHelper = new DataHelper(LoginActivity.this);
				String emailString  = ((EditText)findViewById(R.id.email_edittext)).getText().toString();
				String passwdString = ((EditText)findViewById(R.id.passwd_edittext)).getText().toString();
				if(false == dataHelper.IsEmailAndPasswdOk(emailString, passwdString)) {
					Toast.makeText(LoginActivity.this, "密码错误啦", Toast.LENGTH_LONG).show();
					((EditText)findViewById(R.id.passwd_edittext)).setText("");
					((EditText)findViewById(R.id.passwd_edittext)).requestFocus();
					return;
				}

				//set login status
				Editor statusPreferences = getSharedPreferences("firstlog", 0).edit();
				statusPreferences.putString("username", emailString);
				CheckBox remeberCheckBox = (CheckBox)findViewById(R.id.remeber_checkbox);
				if(remeberCheckBox.isChecked()) {
					statusPreferences.putString("haveLogined", "yes");
				}
				else {
					statusPreferences.putString("haveLogined", "no");
				}
				statusPreferences.commit();
				
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, GuideActivity.class);
				startActivity(intent);
				finish();
			}
		});
       	
    }
    
    private void gotoRegActivity() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, RegActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
		//startActivityForResult(intent, 1);
    }

}
