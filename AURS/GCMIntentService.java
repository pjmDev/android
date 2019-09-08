package com.example.arus;
import java.util.Iterator;
import com.example.arus.dialog.MessageDialog;
import com.example.arus.util.Config;
import com.example.arus.util.Shared;
import com.example.arus.util.Utils;
import com.google.android.gcm.GCMBaseIntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
public class GCMIntentService extends GCMBaseIntentService {
	public final static String SERVICE_BROADCAST_UPDATE_FAMILY = "//";
	public final static String SERVICE_BROADCAST_UPDATE_FAMILY_SERIVCE = "//";
	private String type,no;
	private String si;
	private String gu;
	private String dong;
	private String radius;
	private String radiusDistance;
	private String email;
	private String phone;
	private String famliyType;
	private String myIdx;
	private String name;
	private static Handler mHandler = new Handler();
	public GCMIntentService() {
		super(Config.SENDER_ID);
	}
	@Override
	protected void onError(Context arg0, String arg1) {
		
	}
	@Override
	protected void onRegistered(Context arg0, String arg1) {
		
	}
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}
	@Override
	protected void onMessage(Context context, Intent intent) {
	    Bundle extras = intent.getExtras();
	    if(type == null) type = "";
        if (!extras.isEmpty()) {
            Iterator<String> iterator = extras.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = extras.get(key).toString();
                Log.d("CHECK", "GCM ["+key+"="+value+"]");
                if (key.equals("type")) {type = value;}
                else if (key.equals("si")) {si = value;}
                else if (key.equals("gu")) {gu = value;}
                else if (key.equals("dong")) {dong = value;}
                else if (key.equals("radius")) {radius= value;}
                else if (key.equals("radiusDistance")) {radiusDistance = value;}
                else if (key.equals("email")) {email = value;}
                else if (key.equals("phone")) {phone=value;}
                else if (key.equals("type")) {type = value;}
                else if (key.equals("no")) {no= value;}
                else if	(key.equals("myIdx")) {myIdx = value;}
                else if (key.equals("famliyType")) {famliyType = value;}
                else if (key.equals("name")) {name = value;}
            }
            if (type.equals("1")) {
			} else if (type.equals("2")) {
			} else if (type.equals("3")) {
			} else if (type.equals("4")) {
				intent = new Intent();
		        intent.putExtra("title", "..");
		        intent.putExtra("email", si);
		        intent.putExtra("msg", "01000000000인"+name+"님 \n"+"완료.");
		        intent.putExtra("signal", "1" );
		        intent.putExtra("type",type);
		        intent.putExtra("si",si);
		        intent.putExtra("gu",gu);
                intent.putExtra("dong",dong);
                intent.putExtra("radius",radius);
                intent.putExtra("phone",Utils.getMyPhoneNumber(context));
                intent.putExtra("radiusDistance",radiusDistance);
                intent.putExtra("type",type);
                intent.putExtra("no",no);
                intent.putExtra("name",name);
                intent.putExtra("myIdx",myIdx);
                intent.putExtra("famliyType",famliyType);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        openDialog(context, intent);
			} else if (type.equals("5")) {
				intent = new Intent();
		        intent.putExtra("title", " ");
		        intent.putExtra("email", si);
		        intent.putExtra("msg", si+" ?" );
		        intent.putExtra("signal", "2" );
		        intent.putExtra("type",type);
		        intent.putExtra("si",si);
		        intent.putExtra("gu",gu);
		        intent.putExtra("name",name);
                intent.putExtra("dong",dong);
                intent.putExtra("radius",radius);
                intent.putExtra("radiusDistance",radiusDistance);
                intent.putExtra("email",email);
                intent.putExtra("type",type);
                intent.putExtra("no",no);
                intent.putExtra("myIdx",myIdx);
                intent.putExtra("famliyType",famliyType);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        openDialog(context, intent);
			} else if (type.equals("6")) {
				intent = new Intent();
		        intent.putExtra("title", " ");
		        intent.putExtra("email", si);
		        intent.putExtra("msg", si+" ?" );
		        intent.putExtra("signal", "3" );
		        intent.putExtra("type",type);
		        intent.putExtra("si",si);
		        intent.putExtra("gu",gu);
		        intent.putExtra("name",name);
                intent.putExtra("dong",dong);
                intent.putExtra("radius",radius);
                intent.putExtra("radiusDistance",radiusDistance);
                intent.putExtra("phone",Utils.getMyPhoneNumber(context));
                intent.putExtra("email",email);
                intent.putExtra("type",type);
                intent.putExtra("no",no);
                intent.putExtra("myIdx",myIdx);
                intent.putExtra("famliyType",famliyType);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        openDialog(context, intent);
			}
        }
	}	
	private String p_title, p_msg, p_signal,p_email  = null;
	private String p_type,p_si,p_gu,p_dong,p_radius,p_radiusDistance,p_no,p_phone , p_famliyType , p_myIdx,p_name ;
		private void openDialog(Context context, Intent intent) {
//		Shared shared = new Shared(context);
//		String myEmail = (String) shared.get(Shared.TYPE_STRING, "SERVICE_EMAIL");
//		String myPhone = (String) shared.get(Shared.TYPE_STRING, "SERVICE_PHONE");
//
//		 
//		if(myEmail.equals("") || myPhone.equals("")) {
//			return;
//		}
		p_title = intent.getStringExtra("title");
		p_signal = intent.getStringExtra("signal");
		p_email = intent.getStringExtra("email");
		p_phone = intent.getStringExtra("phone");
		p_type = intent.getStringExtra("type");
		p_si = intent.getStringExtra("si");
		p_gu = intent.getStringExtra("gu");
		p_dong = intent.getStringExtra("dong");
		p_radius = intent.getStringExtra("radius");
		p_myIdx = intent.getStringExtra("myIdx");
		p_radiusDistance = intent.getStringExtra("radiusDistance");
		p_no = intent.getStringExtra("no");
		p_famliyType = intent.getStringExtra("famliyType");
		p_name = intent.getStringExtra("name");
		if (p_signal.equals("1")) {// 
			p_msg = intent.getStringExtra("msg");
			caseOneOK(context);
		} else if (p_signal.equals("2")) {// 
			p_msg = " 메세지";
			caseThree(context);
		} 
	}
	private void caseOneOK(final Context context) {
		mHandler.post(new Runnable() {
			public void run() {
				final MessageDialog alertDialog = new MessageDialog(context);
				alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
				alertDialog.setPositiveButton("�솗�씤", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						sendAuthPush(context, "6", p_email,p_phone , p_famliyType , p_myIdx, p_name); // �슂泥��쓣 �듅�씤
//						SettingConfigurationVo settingConfiguration = new SettingConfigurationVo();
//						settingConfiguration.setGps(si);
//						settingConfiguration.setPhone(p_phone);
//						sqliteDBHelperDao.addSettingConfiguration(settingConfiguration);
						alertDialog.dismiss();
					}
				});
				alertDialog.setTitle(p_title);
				alertDialog.setMessage(p_msg);
				alertDialog.show();
			}
		});
	}
	private void caseThree(final Context context) {
		mHandler.post(new Runnable() {
			public void run() {
				final MessageDialog alertDialog = new MessageDialog(context);
				alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
				alertDialog.setPositiveButton("�떕湲�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//context.finish();
					}
				});
				alertDialog.setNegativeButton("�솗�씤", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.sendBroadcast(new Intent(SERVICE_BROADCAST_UPDATE_FAMILY_SERIVCE));
					}
				});
		
				alertDialog.setTitle(p_title);
				alertDialog.setMessage(p_msg);
				alertDialog.show();
			}
		});
	}
}