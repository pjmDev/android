package com.example.arus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;

import com.example.arus.dialog.MessageDialog;
import com.example.arus.dto.ApiResultDTO;
import com.example.arus.util.Config;
import com.example.arus.util.ExcuteTask;
import com.example.arus.util.ExcuteTask.OnTaskRunnable;
import com.example.arus.util.PushHelper;
import com.example.arus.util.PushHelper.OnRegisterListener;
import com.example.arus.util.Utils;
import com.example.arus.util.XHttpClient;
import com.example.arus.util.XHttpClient.OnHttpResponseListener;
import com.google.gson.Gson;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RestoreAcitivity extends Activity {
	private Context mContext = null;
	private Button storeBtn;
	private String regId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_store_list);
		storeBtn = (Button)findViewById(R.id.storeBtn);
		storeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RestoreAcitivity.this , StoreListActivity.class);
				startActivity(intent);
			}
		});
		
		initGCM();
		
	}
	
	@SuppressLint("NewApi")
	private void initGCM() {
		mContext = getApplicationContext();
		if (Utils.checkPlayServices(this)) {
			regId = PushHelper.getRegistrationId(mContext);
			if (regId.isEmpty()) {
				PushHelper.register(this, new OnRegisterListener() {
					@Override
					public void onRegister(String registerId) {
						regId = registerId;
						// registration ID 
						registerPush();
					}
				});
			} else {
			}
		}
	}
	
	public void registerPush() {
		new ExcuteTask(RestoreAcitivity.this).start(new OnTaskRunnable() {
			@Override
			public void run() {
				requestPush();
			}
		});
	}

	public void requestPush() {
		String url = Config.SERVER_URL_MEMBER_UPDATE_REGID;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("reg_id", regId));

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
					final ApiResultDTO result = gson.fromJson(data, ApiResultDTO.class);

					if (result == null) {
						Log.d("CHCEK", "..");
						Log.d("CHCEK", data);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final MessageDialog alert = new MessageDialog(RestoreAcitivity.this);
								alert.setTitle("�븣由�");
								alert.setMessage("..");
								alert.show();
							}
						});
						return;
					}

					final String message = (String) result.getData();
					Log.d("CHECK", "register id .. .. : " + message);
				}
			});
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
