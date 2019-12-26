package bulat.diet.helper_plus.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import java.util.Locale;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.utils.SaveUtils;

public class DietHelperActivity extends TabActivity implements OnTabChangeListener{
	TabHost tabHost;
	public static DietHelperActivity ctx;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.main);
        if(!SaveUtils.isFirstTime(DietHelperActivity.this)){ 
        	Locale locale = new Locale(SaveUtils.getLang(this));
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
			      getBaseContext().getResources().getDisplayMetrics());
        }
 		Resources res = getResources();
 		tabHost = getTabHost();
 		TabHost.TabSpec spec;
 		Intent intent;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels / 5;
        tabHost.setOnTabChangedListener(this);

		intent = new Intent().setClass(this, DishActivityGroup.class);
		View tabViewToday = getLayoutInflater().inflate(R.layout.custom_tab, tabHost, false);
		((TextView)tabViewToday.findViewById(R.id.title)).setText(R.string.tab_dish);
		((ImageView)tabViewToday.findViewById(R.id.icon)).setImageDrawable(res.getDrawable(R.drawable.today_selector));
		tabViewToday.getLayoutParams().width = width;
		spec = tabHost.newTabSpec("today")
				// .setIndicator(getString(R.string.tab_dish), res.getDrawable(R.drawable.today_selector))
				.setIndicator(tabViewToday)
				.setContent(intent);
		tabHost.addTab(spec);

		View tabViewCalendar2 = getLayoutInflater().inflate(R.layout.custom_tab, tabHost, false);
		((TextView)tabViewCalendar2.findViewById(R.id.title)).setText(R.string.tab_calendar);
		((ImageView)tabViewCalendar2.findViewById(R.id.icon)).setImageDrawable(res.getDrawable(R.drawable.calendar_selector));
		intent = new Intent().setClass(this, CalendarActivityGroup.class);
		tabViewCalendar2.getLayoutParams().width = width;
		spec = tabHost
				.newTabSpec("calendar3")
				.setIndicator(tabViewCalendar2)
				.setContent(intent);
		tabHost.addTab(spec);

		View tabViewList = getLayoutInflater().inflate(R.layout.custom_tab, tabHost, false);
		((TextView)tabViewList.findViewById(R.id.title)).setText(R.string.tab_dishlist);
		((ImageView)tabViewList.findViewById(R.id.icon)).setImageDrawable(res.getDrawable(R.drawable.list_selector));
		intent = new Intent().setClass(this, DishListActivityGroup.class);
		tabViewList.getLayoutParams().width = width;
		spec = tabHost
				.newTabSpec("list")
				.setIndicator(tabViewList)
				.setContent(intent);
		tabHost.addTab(spec);


		View tabViewSoc = getLayoutInflater().inflate(R.layout.custom_tab, tabHost, false);
		((TextView)tabViewSoc.findViewById(R.id.title)).setText(R.string.tab_calc);
		((ImageView)tabViewSoc.findViewById(R.id.icon)).setImageDrawable(res.getDrawable(R.drawable.calculator_selector));
		intent = new Intent().setClass(this, SocialActivityGroup.class);
		tabViewSoc.getLayoutParams().width = width;
		spec = tabHost
				.newTabSpec("social")
				.setIndicator(tabViewSoc)
				.setContent(intent);
		tabHost.addTab(spec);

		View tabViewSet = getLayoutInflater().inflate(R.layout.custom_tab, tabHost, false);
		((TextView)tabViewSet.findViewById(R.id.title)).setText(R.string.tab_setting);
		((ImageView)tabViewSet.findViewById(R.id.icon)).setImageDrawable(res.getDrawable(R.drawable.settings_selector));
		intent = new Intent().setClass(this, SettingsActivity.class);
		tabViewSet.getLayoutParams().width = width;
		spec = tabHost
				.newTabSpec("set")
				.setIndicator(tabViewSet)
				.setContent(intent);
		tabHost.addTab(spec);


		if(!SaveUtils.isActivated(this)){
 			tabHost.setCurrentTab(4);
 		}else{
 			tabHost.setCurrentTab(0);
 		}
 		
 		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
			tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.tab_color);
        } 
					
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.color.tab_selected_color);
		ctx = this;
    }
   
	public void switchTab(int tab){
	        tabHost.setCurrentTab(tab);
	        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) 
		    {
		        TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
		        tv.setTextColor(Color.WHITE);
		    } 
	}
	
	public void changeSocialTabIndicator(int tab, String additionText){
		TextView title = (TextView) tabHost.getTabWidget().getChildAt(tab).findViewById(android.R.id.title);
		if (title != null) {
			if (!"0".equals(additionText)) {
				title.setText(getString(R.string.tab_calc) + " (" + additionText + ")");
			} else {
				title.setText(getString(R.string.tab_calc));
			}
		}
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) 
	    {
	        TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	        //tv.setTextColor(Color.WHITE);
	    } 
	}

	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		int tab = tabHost.getCurrentTab();
		tabHost.getTabWidget().getChildAt(tab).setBackgroundColor(Color.CYAN);

		if (tabHost != null) {
			for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
				tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.tab_color);
			}

			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.color.tab_selected_color);

		}
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			//tv.setTextColor(Color.WHITE);
		}
	}

}