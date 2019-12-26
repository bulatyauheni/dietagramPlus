package bulat.diet.helper_plus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.reciver.CaloryAppWidgetProvider;
import bulat.diet.helper_plus.utils.MessagesUpdater;
import bulat.diet.helper_plus.utils.SaveUtils;

public class BaseActivity extends Activity {
	Handler myHandler ;
	public static final String CUSTOM_INTENT = "bulatplus.intent";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Locale locale = new Locale(SaveUtils.getLang(this));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
		      getBaseContext().getResources().getDisplayMetrics());
		// TODO Auto-generated method stub
		myHandler = new Handler() {
            @Override
            public void handleMessage (Message msg) {
            	Bundle b = msg.getData();
                String message = b.getString("message");
                updateMessageCount(message);
            }
		};
		super.onCreate(savedInstanceState);

	}
	protected void updateMessageCount(String message) {
		// TODO Auto-generated method stub
		 DietHelperActivity parentActivity;
	        parentActivity = (DietHelperActivity) this.getParent().getParent();
	        parentActivity.changeSocialTabIndicator(3, message);
	     TextView tv = (TextView) findViewById(R.id.textViewMessNumber);
	     if(tv!=null){
		     if(!"0".equals(message)){
		    	 tv.setText(" (" +message+ ")");
				}else{
					tv.setText("");
				}
	     }
	}
	
	@Override
	protected void onResume() {
		Locale locale = new Locale(SaveUtils.getLang(this));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
		      getBaseContext().getResources().getDisplayMetrics());
		// TODO Auto-generated method stub
		super.onResume();
		// TODO Auto-generated method stub
		if(SaveUtils.getUserUnicId(this)!=0){
			new MessagesUpdater(this, myHandler).execute();
			Intent i = new Intent();
			i.setAction(CUSTOM_INTENT);
			this.sendBroadcast(i);
		}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.sendBroadcast(
				new Intent(this,
						CaloryAppWidgetProvider.class).
						setAction(BaseActivity.CUSTOM_INTENT));
	}
	
	public void checkLimit(int sum){
		int mode = SaveUtils.getMode(this);
		int customLimit = SaveUtils.getCustomLimit(this);
		if(customLimit>0){
			SaveUtils.saveBMR(String.valueOf(customLimit), this);
			SaveUtils.saveMETA(String.valueOf(customLimit), this);
		}
		TextView tvSurplus = (TextView) findViewById(R.id.textViewSurplusValue);
		switch (mode) {
		case 0:
			if(sum > Integer.parseInt(SaveUtils.getBMR(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			TextView tvLimit = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit.setText(String.valueOf(SaveUtils.getBMR(this)));
			if(tvSurplus!=null){tvSurplus.setText(String.valueOf(Integer.valueOf(SaveUtils.getBMR(this)) - sum));}
			break;
		case 1:
			if(sum > Integer.parseInt(SaveUtils.getMETA(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}	
			TextView tvLimit2 = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit2.setText(String.valueOf(SaveUtils.getMETA(this)));
			if(tvSurplus!=null){tvSurplus.setText(String.valueOf(Integer.valueOf(SaveUtils.getMETA(this)) - sum));}
			break;
		case 2:
			TextView tvLimit3 = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit3.setText(String.valueOf(SaveUtils.getMETA(this)));
			if(sum < Integer.parseInt(SaveUtils.getMETA(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
								
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			if(tvSurplus!=null){tvSurplus.setText(String.valueOf(Integer.valueOf(SaveUtils.getMETA(this)) - sum));}
			break;
		default:
			break;
		}
		
	}
}
