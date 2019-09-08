package com.example.arus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import com.example.arus.util.Utils;
import com.example.arus.util.XHttpClient;
import com.example.arus.util.XHttpClient.OnHttpResponseListener;
import com.google.gson.Gson;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class StoreListActivity extends Activity {
	private Button reservationBtn;
	private int year, month, day, hour, minute;
	private String data;
	private String time;
	private Dialog doDialog;
	private String logText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_store_info);
		reservationBtn = (Button) findViewById(R.id.reservationBtn);

		GregorianCalendar calendar = new GregorianCalendar();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		reservationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(StoreListActivity.this, dateSetListener, year, month, day).show();

			}
		});
 
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			String msg = String.format("%d 년 %d 월 %d 일", year, monthOfYear + 1, dayOfMonth);
			data = msg;
			new TimePickerDialog(StoreListActivity.this, timeSetListener, hour, minute, false).show();
		}
	};

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			String msg = String.format("%d 시: %d 분", hourOfDay, minute);
			time = msg;
			showDia();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void showDia() {
		doDialog = new Dialog(StoreListActivity.this, R.style.Dialog_No_Border);
		doDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		LayoutInflater updateNewInflater = LayoutInflater.from(StoreListActivity.this);
		final View updateNewView = updateNewInflater.inflate(R.layout.dialog_common, null);

		Button okBtn = (Button) updateNewView.findViewById(R.id.okBtn);
		TextView titleTxt = (TextView) updateNewView.findViewById(R.id.titleTxt);
		TextView messageTxt = (TextView) updateNewView.findViewById(R.id.messageTxt);
		Button cancelBtn = (Button) updateNewView.findViewById(R.id.cancelBtn);
		final EditText inputEdt = (EditText) updateNewView.findViewById(R.id.inputEdt);
		inputEdt.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
		titleTxt.setText("..");
		messageTxt.setText(data + "\n" + time + "\n ..");
		
		OnClickListener updateClickListener = new OnClickListener() {

			@Override
			public void onClick(View p_v) {

				if (p_v.getId() == R.id.okBtn) {
					if(inputEdt.getText().toString().length() <= 0) {
						Toast toast = Toast.makeText(getApplicationContext(), "..", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else {
						logText = data + "\n" + time +"\n"+inputEdt.getText().toString(); 
						sendAuthPush("..@naver.com");	
						doDialog.dismiss();
					}
					
				}
				if (p_v.getId() == R.id.cancelBtn) {
					Toast toast = Toast.makeText(getApplicationContext(), "..", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				
					doDialog.dismiss();
				}
				
			}
		};

		okBtn.setOnClickListener(updateClickListener);
		cancelBtn.setOnClickListener(updateClickListener);

		doDialog.setContentView(updateNewView);
		doDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		doDialog.show();
	}
	
	
	public void sendAuthPush(final String email) {
		new ExcuteTask(StoreListActivity.this).start(new OnTaskRunnable() {
			@Override
			public void run() {
				sendAuthPush(".." , ".." );
			}
		});
	}

	public void sendAuthPush(final String phone , final String idx) {
		new ExcuteTask(StoreListActivity.this).start(new OnTaskRunnable() {
			@Override
			public void run() {
				requestAuthPush(phone , idx);
			}
		});
	}
	public void requestAuthPush(String phone , String idx) {
		String url = Config.SERVER_URL_PUSH_REQUEST_FAMILY_NEW;
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("child", phone));
		params.add(new BasicNameValuePair("famliyType", "2"));

\\\\

		SharedPreferences pref = getSharedPreferences("radius_child", MODE_PRIVATE);
		String radius_juso = pref.getString("radius_juso", "1");
		String radius_meter = pref.getString("radius_meter", "1");
		String division_gu= pref.getString("division_gu", "");
		String division_dong = pref.getString("division_dong", "");

		params.add(new BasicNameValuePair("gu", "no"));
		params.add(new BasicNameValuePair("dong", "no"));

		params.add(new BasicNameValuePair("radius", radius_juso));
		params.add(new BasicNameValuePair("radiusDistance", radius_meter));
		params.add(new BasicNameValuePair("phone", "01068217712"));

		params.add(new BasicNameValuePair("type", "4"));
		params.add(new BasicNameValuePair("no", "1"));
		params.add(new BasicNameValuePair("idx",String.valueOf(idx) ));
		params.add(new BasicNameValuePair("name",logText));
		
		if (XHttpClient.isConnectedNetwork( this )) {

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
					Log.d("CHECK", "."+data);
					Gson gson = new Gson();
					
\

				    runOnUiThread(new Runnable() {
						public void run() {
							final MessageDialog alertDialog = new MessageDialog( StoreListActivity.this );
							   alertDialog.setTitle("..");
							   alertDialog.setMessage("..");
							   alertDialog.show();
						}
					});\

				}
			});
		}

	}
}
