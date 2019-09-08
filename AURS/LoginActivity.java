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
import com.example.arus.dto.ApiLoginResultDTO;
import com.example.arus.dto.ApiResultDTO;
import com.example.arus.dto.MemberDto;
import com.example.arus.util.Config;
import com.example.arus.util.ExcuteTask;
import com.example.arus.util.GsonUtils;
import com.example.arus.util.ExcuteTask.OnTaskRunnable;
import com.example.arus.util.Shared;
import com.example.arus.util.Utils;
import com.example.arus.util.XHttpClient;
import com.example.arus.util.XHttpClient.OnHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity {
	private EditText idEdit;
	private EditText passEdit;
	private Button confirmBtn;
	private Shared mShared = null;
	private boolean isLoginSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		mShared = new Shared(this);
		idEdit = (EditText) findViewById(R.id.idEdit);
		passEdit = (EditText) findViewById(R.id.passEdit);
		confirmBtn = (Button) findViewById(R.id.confirmBtn);

		confirmBtn.setOnClickListener(new OnClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if (idEdit.getText().toString().length() < 0) {
					Toast.makeText(LoginActivity.this, "..", 500);
					return;
				}
				if (passEdit.getText().toString().length() < 0) {
					Toast.makeText(LoginActivity.this, "..", 500);
					return;
				}
				login();

			}
		});
	}

	public void login() {
		new ExcuteTask(LoginActivity.this, "..", "..").start(new OnTaskRunnable() {
			@Override
			public void run() {
				requestLogin();
				if (isLoginSuccess) {
					requestMemberInfo();
				}
			}
		});
	}

	public void requestLogin() {

		String email = "";
		String password = "";

		email = idEdit.getText().toString();
		password = passEdit.getText().toString();

		String url = Config.SERVER_URL_LOGIN;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("j_username", email));
		params.add(new BasicNameValuePair("j_password", password));

		// �꽕�듃�썙�겕 泥댄겕
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

					Gson gson = GsonUtils.getBuilder();
					final ApiLoginResultDTO result = gson.fromJson(data, ApiLoginResultDTO.class);

					if (result == null) {
						Log.d("CHCEK", "..");
						Log.d("CHCEK", data);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final MessageDialog alert = new MessageDialog(LoginActivity.this);
								alert.setTitle(".");
								alert.setMessage("..");
								alert.show();
							}
						});
						return;
					}

					final String message = result.getMessage();
					// Log.d("CHECK", message);
					if (result.getSuccess() != null && result.getSuccess() == true) {
						saveIdPw();
						// .
						isLoginSuccess = true;
					} else {
						// .
						mShared.set("autoLogin", false);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final MessageDialog alert = new MessageDialog(LoginActivity.this);
								alert.setPositiveButton(".", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// finish();
									}
								});
								alert.setTitle(".");
								alert.setMessage(message);
								alert.show();
							}
						});
					}
				}
			});
		}

	}

	public void requestMemberInfo() {
		String url = Config.SERVER_URL_MEMBER_INFO;

		TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
		PhoneNumber = "0" + PhoneNumber;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", PhoneNumber));
		// �꽕�듃�썙�겕 泥댄겕
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
								final MessageDialog alert = new MessageDialog(LoginActivity.this);
								alert.setTitle("/");
								alert.setMessage("/");
								alert.show();
							}
						});
						return;
					}
//					Utils.setMemberData(LoginActivity.this, result.getData());
					/*
					 * if(Utils.getMemberData(LoginActivity.this).getIsexpired()
					 * == 1) { runOnUiThread(new Runnable() {
					 * 
					 * @Override public void run() { final MessageDialog alert =
					 * new MessageDialog( LoginActivity.this );
					 * alert.setPositiveButton( "/", new
					 * DialogInterface.OnClickListener() {
					 * 
					 * @Override public void onClick( DialogInterface dialog,
					 * int which) { // finish(); Intent intent = new
					 * Intent(LoginActivity.this, MarketActivity.class);
					 * startActivity(intent); } }); alert.setTitle("/");
					 * alert.setMessage(" ."
					 * ); alert.show(); } }); } else {
					 */
					
					Shared shared = new Shared(LoginActivity.this);
//					shared.set("SERVICE_EMAIL", Utils.getMemberData(LoginActivity.this).getEmail());
//					shared.set("SERVICE_PHONE", Utils.getMemberData(LoginActivity.this).getPhone());

					if (idEdit.getText().toString().equals("aaaaaaaa@naver.com")) {
						Intent intent = new Intent(LoginActivity.this, ManagerAcitivty.class);
						startActivity(intent);
						finish();

					} else {
						Intent intent = new Intent(LoginActivity.this, RestoreAcitivity.class);
						startActivity(intent);
						finish();
					}

					// }

				}

			});
		}

	}

	public void saveIdPw() {

		String email = idEdit.getText().toString();
		String password = passEdit.getText().toString();

		mShared.set("userid", new String(email));
		mShared.set("password", new String(password));
		mShared.set("saveId", true);
		mShared.set("autoLogin", true);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
