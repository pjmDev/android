package com.example.arus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;

import com.example.arus.adapter.AcessAdatper;
import com.example.arus.dto.ApiResultDTO;
import com.example.arus.dto.CommListDto;
import com.example.arus.dto.FamilyDTO;
import com.example.arus.util.Config;
import com.example.arus.util.ExcuteTask;
import com.example.arus.util.ExcuteTask.OnTaskRunnable;
import com.example.arus.util.XHttpClient;
import com.example.arus.util.XHttpClient.OnHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class ManagerAcitivty extends Activity {
	private ListView listItem;
	private CommListDto mAdapterData = new CommListDto();
	private AcessAdatper mAcessAdatper= null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_manager);
		mAcessAdatper = new AcessAdatper(ManagerAcitivty.this,mAdapterData );
		listItem = (ListView)findViewById(R.id.listItem);
		listItem.setAdapter(mAcessAdatper);
		
		loadData();
		
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private void loadData() {
		new ExcuteTask(ManagerAcitivty.this).start(new OnTaskRunnable() {
			@Override
			public void run() {
				requestData();
			}
		});
	}

	public void setData(final String data) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Gson gson = new Gson();
				Type listType = new TypeToken<ApiResultDTO<FamilyDTO, FamilyDTO>>() {}.getType();
				ApiResultDTO<FamilyDTO, FamilyDTO> result = gson.fromJson(data, listType);

				if(result.getList() != null) {
					for (FamilyDTO row : result.getList()) {
						mAdapterData.getLists().add(row);
					}
				}
				mAcessAdatper.notifyDataSetChanged();
			}
		});

	}

	public void requestData() {
		String url = Config.SERVER_URL_ACESS;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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
						Log.d("CHECK",data);
						setData(data);
					}
				});
		}
	}
}
