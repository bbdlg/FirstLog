package com.bbdlg.firstlog;

import com.baidu.mobstat.StatService;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RegActivity extends Activity {
	private static final String activityTag = "RegActivity";
	private final String mbApiKey = FirstLogHelper.mbApiKey;
	private Button getAccessToken;//添加响应按钮
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.reg);
        getAccessToken = (Button) this.findViewById(R.id.getAccessToken);//响应按钮类

        getAccessToken.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            BaiduOAuth BaiduOAuthoauthClient = new BaiduOAuth();

            BaiduOAuthoauthClient.startOAuth(RegActivity.this, mbApiKey, new String[]{"basic", "netdisk"},new BaiduOAuth.OAuthListener() {
                    @Override
                    public void onException(String msg) {
                        Toast.makeText(getApplicationContext(), "授权失败，原因： " + msg + "\n请检查网络是否正常:)", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete(BaiduOAuthResponse response) {
                        if(null != response){
                            String accessToken = response.getAccessToken();
                            Toast.makeText(getApplicationContext(), "用户<"+response.getUserName()+">授权成功^_^", Toast.LENGTH_SHORT).show();
                            
                            //save in SharedPreferences
                            Editor statusPreferences = getSharedPreferences("firstlog", 0).edit();
            				statusPreferences.putString("token_of_"+response.getUserName(), accessToken);
            				statusPreferences.commit();
            				
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

    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(RegActivity.this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(RegActivity.this);
	}
}
