package bulat.diet.helper_plus.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.adapter.BaseLoader;
import bulat.diet.helper_plus.adapter.LocalsArrayAdapter;
import bulat.diet.helper_plus.db.DishListHelper;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.item.TodayDish;
import bulat.diet.helper_plus.utils.Constants;
import bulat.diet.helper_plus.utils.NetworkState;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;
import bulat.diet.helper_plus.utils.StringUtils;


public class StartActivity extends Activity{
	private static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	String lang = "";
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try{
			sliptask.cancel(true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//http://calorii.ru/classes/ajax/search.php?query=%D0%B1%D0%BE%D1%80%D1%89
	//http://www.ambal.ru/calories.php?do=3&st=%20%D1%82%D1%83%D1%88%D0%B5%D0%BD%D0%B0%D1%8F
	private final long SLEEP_TIME = 500;
	private static final long SLEEP_TIME_SHOT = 10;
	SleepTask sliptask;
	private ProgressDialog mProgressDialog;	
	ListView localList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this).inflate(R.layout.start, null);
		setContentView(viewToLoad);
			//BaseLoader.LoadBase(StartActivity.this);
		Date currDate = new Date();
		SaveUtils.setLastVisitTime(currDate.getTime(),this);
		if(SaveUtils.isFirstTime(StartActivity.this)){
			List<DishType> locations = new ArrayList<DishType>();
			locations.add(new DishType(R.drawable.ru, getString(R.string.russian)));
			locations.add(new DishType(R.drawable.bg, getString(R.string.bulgarian)));
			locations.add(new DishType(R.drawable.pl, getString(R.string.polish)));
			locations.add(new DishType(R.drawable.en, getString(R.string.english)));
			locations.add(new DishType(R.drawable.by, getString(R.string.belarusian)));
			LocalsArrayAdapter da = new LocalsArrayAdapter(getApplicationContext(),
						R.layout.locations_list_row, locations);
			localList = (ListView)viewToLoad.findViewById(R.id.ListViewLocation);			
			localList.setAdapter(da);
			localList.setOnItemClickListener(listener);
		}else{
			if(SaveUtils.isReseted(this) == 0){
				DishListHelper.resetPopularity(this);
				SaveUtils.setReseted(1,this);
			}
			Locale locale = new Locale(SaveUtils.getLang(this));
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
			      getBaseContext().getResources().getDisplayMetrics());
			sliptask = new SleepTask();	
			sliptask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			
			//register user	
			 if(0==SaveUtils.getUserAdvId(this)){			 				
		    	  new RegisterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		     }
		}
	}
	
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case DIALOG_DOWNLOAD_PROGRESS:
	            mProgressDialog = new ProgressDialog(this);
	            if(SaveUtils.isFirstTime(this)){
	            	mProgressDialog.setMessage(getString(R.string.loading_base));
	            }else{
	            	mProgressDialog.setMessage(getString(R.string.update_base));
	            }
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            mProgressDialog.setCancelable(false);
	            mProgressDialog.show();
	            return mProgressDialog;
	        default:
	            return null;
	        }
	    }
	
	private class SleepTask extends AsyncTask<Void, Void, Void>{

		int progress;
		@Override
		protected void onPreExecute() {
			if(SaveUtils.isFirstTime(StartActivity.this) || DishListHelper.getCount(StartActivity.this)<10){
				showDialog(DIALOG_DOWNLOAD_PROGRESS);
			}
		}
		@SuppressLint("WrongThread")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				//fill db by dishes during first time runing	
				//SaveUtils.setFirstTime(this, true);
				if(SaveUtils.isFirstTime(StartActivity.this)){
					new LoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					Thread.sleep(SLEEP_TIME_SHOT);
					int perc = 0;
					int kVal = 27;
					if(SaveUtils.getLang(StartActivity.this).equals(getString(R.string.localebg))){
						kVal = 13;
					}
					if(SaveUtils.getLang(StartActivity.this).equals(getString(R.string.localepl))){
						kVal = 25;
					}
					if(SaveUtils.getLang(StartActivity.this).equals(getString(R.string.localeen))){
						kVal = 10;
					}
					if(SaveUtils.getLang(StartActivity.this).equals(getString(R.string.localeby))){
						kVal = 26;
					}
					while(perc<95){
						perc = SaveUtils.getNumOfRows(StartActivity.this)/kVal;
						progress = perc;
						publishProgress();
						Thread.sleep(SLEEP_TIME);
					}
					SaveUtils.setNewMinAge(StartActivity.this);
				}else{
					//update on 2.5.8 min age = 8
					if(!SaveUtils.isNewMinAge(StartActivity.this)){
						SaveUtils.saveAge(SaveUtils.getAge(StartActivity.this)+18 - Info.MIN_AGE, StartActivity.this);
						SaveUtils.setNewMinAge(StartActivity.this);
					}
					if(DishListHelper.getCount(StartActivity.this)<10){
						SaveUtils.setNumOfRows(0, StartActivity.this);
						new LoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						Thread.sleep(SLEEP_TIME);
						int perc = 0;
						while(perc<90){							
							perc = SaveUtils.getNumOfRows(StartActivity.this)/27;
							progress = perc;
							publishProgress();	
							Thread.sleep(SLEEP_TIME);
						}
					}
					
					new SocialUpdater(StartActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					checkCalendar(StartActivity.this);
					Thread.sleep(SLEEP_TIME_SHOT);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	        
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			mProgressDialog.setProgress(progress);
		}
		@Override
		protected void onPostExecute(Void result) {
			if(SaveUtils.isFirstTime(StartActivity.this) || DishListHelper.getCount(StartActivity.this)<10){
				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			}
			
			Intent intent;			
				intent = new Intent(StartActivity.this, DietHelperActivity.class);
			startActivity(intent);			
			finish();
		}
		
	}
	
	private class LoadTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			//fill db by dishes during first time runing	
			//SaveUtils.setFirstTime(this, true);
			
				//DishListHelper.clearAll(getApplicationContext());
				//TodayDishHelper.clearAll(StartActivity.this);
				BaseLoader.LoadFitnesBase(StartActivity.this);
				BaseLoader.LoadBase(StartActivity.this);
				//BaseLoader.LoadText(R.raw.base, StartActivity.this);
				SaveUtils.setFirstTime(StartActivity.this, false);
				checkCalendar(StartActivity.this);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
					
			finish();
		}
		
	}
	public static void checkCalendar(Context ctx ) {
		// TODO Auto-generated method stub
		Locale locale;
		if("".endsWith(SaveUtils.getLang(ctx))){
			locale = new Locale("ru");
		}else{
			locale = new Locale(SaveUtils.getLang(ctx));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMMM", locale);
		
		String lastDate = TodayDishHelper.getLastDate(ctx);
		Date curentDateandTime = new Date();
		if(lastDate != null) {
			try {
				if (Long.valueOf(TodayDishHelper.getLastDateLong(ctx)) - (new Date()).getTime() > DateUtils.DAY_IN_MILLIS) {
					TodayDishHelper.changeLastDate(ctx, TodayDishHelper.getLastDateLong(ctx));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				curentDateandTime = sdf.parse(lastDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date nowDate = new Date();

			if (nowDate.getMonth() == 0 && curentDateandTime.getMonth() == 11) {
				curentDateandTime.setYear(nowDate.getYear() - 1);
			} else {
				curentDateandTime.setYear(nowDate.getYear());
			}
//remove day in history if it is older then 5 years
			TodayDishHelper.removeDishesBelowDate(nowDate.getTime() - 5 * DateUtils.YEAR_IN_MILLIS, ctx);

			//curentDateandTime = new Date(curentDateandTime.getTime() - 5 * DateUtils.DAY_IN_MILLIS);			
			if(nowDate.getTime() - curentDateandTime.getTime() > DateUtils.DAY_IN_MILLIS){
				while(nowDate.getTime() - curentDateandTime.getTime() > DateUtils.DAY_IN_MILLIS){
					curentDateandTime = new Date(curentDateandTime.getTime() + DateUtils.DAY_IN_MILLIS);
					TodayDishHelper.addNewDish(new TodayDish("", "", 0, "", 0, 0, sdf.format(curentDateandTime), curentDateandTime.getTime() ,1, ""), ctx);
				}
			}
		}else{
			curentDateandTime = new Date();
			Date start = new Date();
			try {
				start = sdf.parse(sdf.format(new Date(curentDateandTime.getTime())));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			start.setYear(curentDateandTime.getYear());

			TodayDishHelper.addNewDish(new TodayDish("", "", 0, "", 0, 0, sdf.format(new Date(curentDateandTime.getTime())), start.getTime()  ,1, ""), ctx);

		}
	}
	private OnItemClickListener listener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			
			TextView idView = (TextView) v.findViewById(R.id.textViewName);	
			if(idView.getText().toString().equals(getString(R.string.russian))){
				lang = "";
			}else if(idView.getText().toString().equals(getString(R.string.bulgarian))){
				lang = "bg";
			}else if(idView.getText().toString().equals(getString(R.string.polish))){
				lang = "pl";
			}else if(idView.getText().toString().equals(getString(R.string.english))){
				lang = "en";
			}else if(idView.getText().toString().equals(getString(R.string.belarusian))){
				lang = "by";
			}
			SaveUtils.setLang(lang, StartActivity.this);
			Locale locale = new Locale(lang);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
			      getBaseContext().getResources().getDisplayMetrics());
			AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
			builder.setMessage(R.string.remove_dialog)
					.setPositiveButton(getString(R.string.yes), dialogClickListener)
					.setNegativeButton(getString(R.string.no), dialogClickListener)
					.show();
			
		}
	};
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		

		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				try {
					sliptask = new SleepTask();	
					sliptask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				
				break;
			}
		}
	};
	 private class RegisterTask extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				if (NetworkState.isOnline(StartActivity.this)) {
					StringBuilder builder = new StringBuilder();
					HttpParams httpParameters = new BasicHttpParams();
					// Set the timeout in milliseconds until a connection is established.
					// The default value is zero, that means the timeout is not used. 
					int timeoutConnection = 5000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 5000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					// searchString = searchString.replaceAll(" ", "%20");
					Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
					Account[] accounts = AccountManager.get(StartActivity.this).getAccountsByType("com.google");
					String email = null;
					for (Account account : accounts) {
					    if (emailPattern.matcher(account.name).matches()) {
					        email = account.name;
					    }
					}
					
					StringBuffer parametersb = new StringBuffer("");
					parametersb.append("?updateadv=" + 1);			

									
						parametersb.append("&weight="
								+(SaveUtils
								.getWeight(StartActivity.this) + Info.MIN_WEIGHT));
						parametersb.append("&high="
								+(SaveUtils
								.getHeight(StartActivity.this) + Info.MIN_HEIGHT));
							parametersb.append("&age="
								+(SaveUtils
								.getAge(StartActivity.this) + Info.MIN_AGE));
							parametersb.append("&sex="
								+(SaveUtils
								.getSex(StartActivity.this)));
							parametersb.append("&activity="
								+(SaveUtils
								.getActivity(StartActivity.this)));
							parametersb.append("&email="
									+ StringUtils.encrypt(email));
						try {
							parametersb.append("&version=" + URLEncoder.encode(Constants.VERSION, "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					HttpGet httpGet = new HttpGet(Constants.URL_ADVERT + parametersb);
					try {
						HttpResponse response = client.execute(httpGet);
						StatusLine statusLine = response.getStatusLine();
						int statusCode = statusLine.getStatusCode();
						if (statusCode == 200) {
							HttpEntity entity = response.getEntity();
							InputStream content = entity.getContent();
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(content));
							String line;
							while ((line = reader.readLine()) != null) {
								builder.append(line);
							}
						}
						String resultString = builder.toString().trim();
											
							try{
								if(Integer.valueOf(resultString) > 0)
									SaveUtils.setUserAdvId(StartActivity.this, Integer.valueOf(resultString));
							}catch (Exception e) {
									e.printStackTrace();
							}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}			
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}

		}
	
}
