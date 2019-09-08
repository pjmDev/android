package com.example.arus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;

import com.example.arus.dialog.MessageDialog;
import com.example.arus.dto.ApiResultDTO;
import com.example.arus.dto.MemberDto;
import com.example.arus.util.Config;
import com.example.arus.util.ExcuteTask;
import com.example.arus.util.ExcuteTask.OnTaskRunnable;
import com.example.arus.util.PushHelper;
import com.example.arus.util.Shared;
import com.example.arus.util.Utils;
import com.example.arus.util.XHttpClient;
import com.example.arus.util.XHttpClient.OnHttpResponseListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SplashActivity extends Activity {
	private Shared mShared;
	private boolean isLoginSuccess = false;
	private String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);
		mShared = new Shared(this);

		Boolean autoLogin = (Boolean) mShared.get(Shared.TYPE_BOOL, "autoLogin");
		if (autoLogin) {
			new ExcuteTask(SplashActivity.this, false).start(new OnTaskRunnable() {
				@Override
				public void run() {
					requestLogin();
					if (isLoginSuccess)
						requestMemberInfo();
				}
			});
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}, 2000);
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	public void requestLogin() {

		String email = "";
		String password = ""; 

		email = (String) mShared.get(Shared.TYPE_STRING, "userid");
		password = (String) mShared.get(Shared.TYPE_STRING, "password");

		String url = Config.SERVER_URL_LOGIN;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("j_username", email));
		params.add(new BasicNameValuePair("j_password", password));

		// ?꽕?듃?썙?겕 泥댄겕
		if (XHttpClient.isConnectedNetwork(this)) { 
			final CookieStore cookieStore = XHttpClient.getCookieStore(this);
			XHttpClient.getHttpPost(this, url, params, cookieStore, new OnHttpResponseListener() {
				@Override
				public void getHttpResponse(HttpResponse response) {
					String data = "";
					try {
						data = XHttpClient.convertStreamToString(response.getEntity().getContent());
					} catch (IllegalStateException e) {
					} catch (IOException e) {
					}

					Gson gson = new Gson();
					final ApiResultDTO result = gson.fromJson(data, ApiResultDTO.class);

					if (result == null) {
						Log.d("CHCEK", "..");
						Log.d("CHCEK", data);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final MessageDialog alert = new MessageDialog(SplashActivity.this);
								alert.setTitle(".");
								alert.setMessage("..");
								alert.show();
							}
						});
						return;
					}

					if (result.getSuccess() == true) {
						isLoginSuccess = true;
					} else {
						// 濡쒓렇?씤 ?씠?룞
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
			});

		}
	}

	@SuppressWarnings("deprecation")
	public void requestMemberInfo() {
		String url = Config.SERVER_URL_MEMBER_INFO;
		TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
		PhoneNumber = "0" + PhoneNumber;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", PhoneNumber));
		if (Utils.checkPlayServices(getApplicationContext())) {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			String msg = "";
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			}
			try {
				regId = gcm.register("..");
				msg = "... \nregistration ID=" + regId;
				PushHelper.storeRegistrationId(getApplicationContext(), regId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				msg = "?뿉?윭 : " + e.getMessage();
			}
		}
		params.add(new BasicNameValuePair("reg_id", regId));

		// ?꽕?듃?썙?겕 泥댄겕
		if (XHttpClient.isConnectedNetwork(this)) {

			final CookieStore cookieStore = XHttpClient.getCookieStore(this);
			XHttpClient.getHttpPost(this, url, params, cookieStore, new OnHttpResponseListener() {
				@Override
				public void getHttpResponse(HttpResponse response) {
					String data = "";
					try {
						data = XHttpClient.convertStreamToString(response.getEntity().getContent());
					} catch (IllegalStateException e) {
					} catch (IOException e) {
					}

					Gson gson = new Gson();
					Type listType = new TypeToken<ApiResultDTO<Object, MemberDto>>() {
					}.getType();
					final ApiResultDTO<Object, MemberDto> result = gson.fromJson(data, listType);

					if (result == null) {
						Log.d("CHCEK", "..");
						Log.d("CHCEK", data);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final MessageDialog alert = new MessageDialog(SplashActivity.this);
								alert.setTitle("..");
								alert.setMessage("..");
								alert.show();
							}
						});
						return;
					}
					
					String email = (String) mShared.get(Shared.TYPE_STRING, "userid");
					if (email.equals("..@naver.com")) {
						Intent intent = new Intent(SplashActivity.this, ManagerAcitivty.class);
						startActivity(intent);
						finish();

					} else {
						Intent intent = new Intent(SplashActivity.this, RestoreAcitivity.class);
						startActivity(intent);
						finish();
					}

					// }
				}

			});
		}

	}
}
