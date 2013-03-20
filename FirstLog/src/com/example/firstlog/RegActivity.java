package com.example.firstlog;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.oauth.BaiduOAuth.OAuthListener;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RegActivity extends Activity {
	private static final String activityTag = "RegActivity";
	private final String mbApiKey = "5tyi3bBpNWPXZUZjpu7QacxP";//请替换申请客户端应用时获取的Api Key串
	private Button getAccessToken;//添加响应按钮
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
        getAccessToken = (Button) this.findViewById(R.id.getAccessToken);//响应按钮类

        getAccessToken.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            BaiduOAuth BaiduOAuthoauthClient = new BaiduOAuth();

            BaiduOAuthoauthClient.startOAuth(RegActivity.this, mbApiKey, new String[]{"basic"},new BaiduOAuth.OAuthListener() {
                    @Override
                    public void onException(String msg) {
                        Toast.makeText(getApplicationContext(), "授权失败 " + msg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete(BaiduOAuthResponse response) {
                        if(null != response){
                            String accessToken = response.getAccessToken();
                            Toast.makeText(getApplicationContext(), "Token: " + accessToken + "    User name:" + response.getUserName(), Toast.LENGTH_SHORT).show();
 
            				//register in local database
            				String emailString = response.getUserName();
            				UserInfo userInfo = new UserInfo();
            				userInfo.setEmail(emailString);
            				userInfo.setPasswd("default");
            				Log.i(RegActivity.activityTag, "new DataHelper()");
            				DataHelper dataHelper = new DataHelper(RegActivity.this);
            				Log.i(RegActivity.activityTag, "finish new DataHelper()");
            				
            				Boolean retBoolean = dataHelper.HaveUserInfo(emailString);
            				Log.i(RegActivity.activityTag, "after HaveUserInfo()");
            				if(true == retBoolean) {
            					Log.i(RegActivity.activityTag, "email<"+emailString+"> has registered");
            				}
            				else {
            					dataHelper.SaveUserInfo(userInfo);
            					Log.i(RegActivity.activityTag, "register email<"+emailString+">");
	            				retBoolean = dataHelper.HaveUserInfo(emailString);
	            				if(false == retBoolean) {
	            					Log.i(RegActivity.activityTag, "email<"+emailString+"> register failed!");
	            					return;
	            				}
	            				Log.i(RegActivity.activityTag, "email<"+emailString+"> register success!");
            				}
            				Intent intent = new Intent();
            				Bundle bundle = new Bundle();
            				bundle.putString("email", emailString);
            				intent.putExtras(bundle);
            				//setResult(Activity.RESULT_OK, intent);
            				intent.setClass(RegActivity.this, LoginActivity.class);
            				startActivity(intent);
            				RegActivity.this.finish();
                        }
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "授权被取消", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        /*
        Button reg = (Button)findViewById(R.id.reg_button);
        reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(RegActivity.activityTag, "click reg button");
				// TODO Auto-generated method stub
				TextView tips = (TextView)findViewById(R.id.tips_textview);
				tips.setText("");
				
				//register in local database
				String emailString = "testEmail";
				UserInfo userInfo = new UserInfo();
				userInfo.setEmail(emailString);
				Log.i(RegActivity.activityTag, "new DataHelper()");
				DataHelper dataHelper = new DataHelper(RegActivity.this);
				Log.i(RegActivity.activityTag, "finish new DataHelper()");
				
				Boolean retBoolean = dataHelper.HaveUserInfo(emailString);
				Log.i(RegActivity.activityTag, "after HaveUserInfo()");
				if(true == retBoolean) {
					Log.i(RegActivity.activityTag, "email<"+emailString+"> has registered");
					tips.setText("这个邮箱已经被注册过啦");
					return;
				}
				else {
					dataHelper.SaveUserInfo(userInfo);
					Log.i(RegActivity.activityTag, "register email<"+emailString+">");
				}
				
				retBoolean = dataHelper.HaveUserInfo(emailString);
				if(false == retBoolean) {
					Log.i(RegActivity.activityTag, "email<"+emailString+"> register failed!");
					tips.setText("未知错误，请联系楼主:(");
					return;
				}
				tips.setText("注册成功喽:)");
				Log.i(RegActivity.activityTag, "email<"+emailString+"> register success!");
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("email", emailString);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				RegActivity.this.finish();
			}
		});
		*/
    }
}
