package bulat.diet.helper_plus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.adapter.DaysAdapter;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.Day;
import bulat.diet.helper_plus.utils.SaveUtils;

public class CalendarActivity extends BaseActivity {
	protected static final int DIALOG_CHART = 0;
	protected static final int DIALOG_WEIGHT = 1;
	TextView header;
	ListView daysList;
	Cursor c ;
	int sum;
	private TextView avgFatView;
	private TextView avgProteinView;
	private TextView avgCarbonView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		
		super.onCreate(savedInstanceState);		
			
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(R.layout.days_list, null);
		header = (TextView) viewToLoad.findViewById(R.id.textViewTitle);
		
		Button templateTab= (Button) viewToLoad.findViewById(R.id.templateTab);
		templateTab.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();					
					intent.setClass(getParent(), TemplateActivity.class);					
					CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
					activityStack.push("TemplateActivity", intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Button tommorrowButton = (Button) viewToLoad.findViewById(R.id.buttonTommorrow);
		tommorrowButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = null;

				builder = new AlertDialog.Builder(CalendarActivity.this
						.getParent());
				builder.setMessage(R.string.tommorrow_dialog)
						.setPositiveButton(getString(R.string.yes),
								tommorrowClickListener)
						.setNegativeButton(getString(R.string.no),
								tommorrowClickListener).show();
			}
		});
		Button exitButton = (Button) viewToLoad.findViewById(R.id.buttonExit);
		exitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				System.exit(0);
			}
		});
		Button resetButton = (Button) viewToLoad.findViewById(R.id.buttonReset);
		resetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = null;
				
				builder = new AlertDialog.Builder(CalendarActivity.this.getParent());
				builder.setMessage(R.string.reset_dialog)
						.setPositiveButton(getString(R.string.yes),
								dialogClickListener)
						.setNegativeButton(getString(R.string.no),
								dialogClickListener).show();		
			}
		});
		Button chartButton = (Button) viewToLoad.findViewById(R.id.buttonChart);
		chartButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_CHART);				
			}
		});
		
		setContentView(viewToLoad);
		LinearLayout graphHeader = (LinearLayout)findViewById(R.id.linearLayoutChart);
		graphHeader.setOnClickListener(showChartListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(c!=null)
			c.close();
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Button buttonOk;
		Button nobutton;
		switch (id) {
		
		case DIALOG_CHART:		
			GraphView graphView;
			// graph with custom labels and drawBackground				    	
			graphView = new LineGraphView(
			this
			, ""
			);
			((LineGraphView) graphView).setDrawBackground(false);
			
			// custom static labels
			GraphViewData[] data = new GraphViewData[]{};
			List<Day> days  = TodayDishHelper.getDaysStat(getApplicationContext());
			Collections.reverse(days);
			try {
				
				data = new GraphViewData[days.size()];
				int i = 0;
				for (Day day : days) {							
					data[i]= new GraphViewData(i, day.getBodyWeight());
					i++;
		        }
		
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			GraphViewSeries exampleSeries = new GraphViewSeries(data);
			LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.linearLayoutChart);  
		    layout.removeAllViews();
			if(days.size() > 1){				
				String[] hor = new String[days.size()];
				int j=0;
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM",new Locale(getString(R.string.locale)));
				TreeSet<Float> weights = new TreeSet<Float>();
				for (Day day : days) {
					weights.add(day.getBodyWeight());
					if(days.size()>8 && days.size()<16){
						if((j%2)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else if(days.size()>=16 && days.size()<24){
						if((j%3)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else if(days.size()>=24){
						if((j%4)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else{
						hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
					}
					j++;
				}
				/*String[] vertical = new String[data.length];
				for (int i = 0; i < data.length; i++) {
					vertical[i]=String.valueOf(data[i].valueY);
				}*/
				if(weights.size() > 1){
					graphView.setHorizontalLabels(hor);
					graphView.setScalable(false);
					graphView.setVerticalLabels(null);
					graphView.addSeries(exampleSeries); // data
					
					TextView empty = (TextView) dialog.findViewById(R.id.textViewEmpty);
			    	empty.setVisibility(View.GONE);
				    
				    layout.addView(graphView); 
			    }else {
			    	TextView empty = (TextView) dialog.findViewById(R.id.textViewEmpty);
			    	empty.setVisibility(View.VISIBLE);
				}
			}else {
		    	TextView empty = (TextView) dialog.findViewById(R.id.textViewEmpty);
		    	empty.setVisibility(View.VISIBLE);
			}
			
			Button vkButton = (Button)dialog.findViewById(R.id.buttonVk);
			vkButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), VkActivity.class);
					String msg = getString(R.string.app_name) + " Android : " + "Калькулятор калорий";
					
					
					i.putExtra(VkActivity.MESSAGE, msg);
					i.putExtra(VkActivity.LINK, "http:/www.google.com");
					startActivity(i);
				}
			});
			if(!"".equals(SaveUtils.getLang(this))){
				vkButton.setVisibility(View.GONE);
			}
			break;
		
		default:
			dialog = null;
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		Button buttonOk;
		Button nobutton;
		switch (id) {
		case DIALOG_CHART:
			dialog = new Dialog(this.getParent());
			dialog.setContentView(R.layout.user_chart_dialog);
			dialog.setTitle(R.string.user_chart_dialog);
			
			
			GraphView graphView;
			// graph with custom labels and drawBackground
			
			graphView = new LineGraphView(
			this
			, ""
			);
			((LineGraphView) graphView).setDrawBackground(false);
			
			// custom static labels
			GraphViewData[] data = new GraphViewData[]{};
			List<Day> days  = TodayDishHelper.getDaysStat(getApplicationContext());
			Collections.reverse(days);
			try {
				
				data = new GraphViewData[days.size()];
				int i = 0;
				for (Day day : days) {							
					data[i]= new GraphViewData(i, day.getBodyWeight());
					i++;
		        }
		
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			GraphViewSeries exampleSeries = new GraphViewSeries(data);
			if(days.size() > 1){				
				String[] hor = new String[days.size()];
				int j=0;
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM",new Locale(getString(R.string.locale)));
				TreeSet<Float> weights = new TreeSet<Float>();
				for (Day day : days) {
					weights.add(day.getBodyWeight());
					if(days.size()>8 && days.size()<16){
						if((j%2)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else if(days.size()>=16 && days.size()<24){
						if((j%3)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else if(days.size()>=24){
						if((j%4)==0){				
							hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
						}else{
							hor[j]="";
						}
					}else{
						hor[j]=String.valueOf(sdf.format(new Date(day.getDateInt())));
					}
					j++;
				}
				/*String[] vertical = new String[data.length];
				for (int i = 0; i < data.length; i++) {
					vertical[i]=String.valueOf(data[i].valueY);
				}*/
				if(weights.size() > 1){
					graphView.setHorizontalLabels(hor);
					graphView.setScalable(false);
					graphView.setVerticalLabels(null);
					graphView.addSeries(exampleSeries); // data
					
				    LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.linearLayoutChart);  
				    layout.addView(graphView); 
			    }else {
			    	TextView empty = (TextView) dialog.findViewById(R.id.textViewEmpty);
			    	empty.setVisibility(View.VISIBLE);
				}
			}else {
		    	TextView empty = (TextView) dialog.findViewById(R.id.textViewEmpty);
		    	empty.setVisibility(View.VISIBLE);
			}
			
			
			buttonOk = (Button) dialog.findViewById(R.id.buttonYes);
			buttonOk.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {					
					dialog.dismiss();
				}
			});

			break;
		
		default:
			dialog = null;
		}

		return dialog;
	}

	DialogInterface.OnClickListener tommorrowClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					try {
						Intent intent = new Intent();
						Date dateNow = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMMM", new Locale(CalendarActivity.this
								.getString(R.string.locale)));

						String day = sdf.format(new Date(dateNow.getTime() + DateUtils.DAY_IN_MILLIS));
						intent.putExtra(AddTodayDishActivity.TITLE,
								getString(R.string.edit_today_dish));
						intent.putExtra(DishActivity.DATE, day);
						intent.putExtra(DishActivity.BACKBTN, true);
						intent.putExtra(DishActivity.PARENT_NAME,
								CalendarActivityGroup.class.toString());
						intent.setClass(getParent(), DishActivity.class);
						CalendarActivityGroup activityStack = (CalendarActivityGroup) CalendarActivity.this.getParent();
						activityStack.push("DishDayActivity", intent);

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(c!=null)
			c.close();
	}
	DaysAdapter da;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// TODO Auto-generated method stub		
		avgCalorisityView = (TextView) findViewById(R.id.textViewAVGValue);
		avgFatView = (TextView) findViewById(R.id.textViewFat);
		avgProteinView = (TextView) findViewById(R.id.textViewProtein);
		avgCarbonView = (TextView) findViewById(R.id.textViewCarbon);
		String[] avgVals = TodayDishHelper.getAvgDishCalorisity(this);
		TextView tvFp = (TextView) findViewById(R.id.textViewFatPercent);
		TextView tvCp = (TextView) findViewById(R.id.textViewCarbonPercent);
		TextView tvPp = (TextView) findViewById(R.id.textViewProteinPercent);
		
		avgCalorisityView.setText(avgVals[0] + " " + getString(R.string.kcal));
		
		avgFatView.setText(avgVals[1]);
		avgCarbonView.setText(avgVals[2]);
		avgProteinView.setText(avgVals[3]);
		
		DecimalFormat df = new DecimalFormat("###.#");
		
		float sum = Float.valueOf(avgVals[1].replace(",", "."))+Float.valueOf(avgVals[2].replace(",", "."))+Float.valueOf(avgVals[3].replace(",", "."));
		tvFp.setText("("+df.format(Float.valueOf(avgVals[1].replace(",", "."))*100/sum)+"%)");
		tvCp.setText("("+df.format(Float.valueOf(avgVals[2].replace(",", "."))*100/sum)+"%)");
		tvPp.setText("("+df.format(Float.valueOf(avgVals[3].replace(",", "."))*100/sum)+"%)");
		
		checkLimit(Integer.valueOf(avgVals[0]));
		header.setText(getString(R.string.tab_calendar_header));
		c = TodayDishHelper.getDaysNew(getApplicationContext());
		if (c!=null){
			try {
				int customLimit = SaveUtils.getCustomLimit(this);
				if(customLimit>0){
					SaveUtils.saveBMR(String.valueOf(customLimit), this);
					SaveUtils.saveMETA(String.valueOf(customLimit), this);
				}
				da = new DaysAdapter(this, getApplicationContext(), c, (CalendarActivityGroup) getParent());
				//TodayDishAdapter  da = new TodayDishAdapter(getApplicationContext(), c,(DishActivityGroup) getParent());
				daysList = (ListView) findViewById(R.id.listViewDays);
				daysList.setAdapter(da);
				daysList.setItemsCanFocus(true);
				
				
				daysList.setOnItemClickListener(daysListOnItemClickListener);
	        
				
				/*if (c.getCount() > 0){
                	c.moveToFirst();
                	sum = sum + Integer.parseInt(c.getString(c.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)));
                	while(c.moveToNext()){
						
						sum = sum + Integer.parseInt(c.getString(c.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)));
					}
                	TextView tv = (TextView) findViewById(R.id.textViewTotalValue);
    				tv.setText(String.valueOf(sum));
                	}else{
                		TextView tv = (TextView) findViewById(R.id.textViewTotalValue);
	    				tv.setText(String.valueOf(0));	
                	}
                	*/
			}catch (Exception e) {
				if(c!=null)
					c.close();
			}finally{
				//c.close();
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(c!=null)
			c.close();
	}
	
	private OnItemClickListener daysListOnItemClickListener= new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			//switchTabInActivity(1)
			Intent intent = new Intent();
			TextView day = (TextView) v.findViewById(R.id.textViewDay);
			
			intent.putExtra(AddTodayDishActivity.TITLE,  getString (R.string.edit_today_dish));			
			intent.putExtra(DishActivity.DATE, day.getText());		
			intent.putExtra(DishActivity.BACKBTN, true);		
			intent.putExtra(DishActivity.PARENT_NAME, CalendarActivityGroup.class.toString());
			intent.setClass(getParent(), DishActivity.class);
			CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
			activityStack.push("DishDayActivity", intent);			
		}
	};
	private TextView avgCalorisityView;  
	
	public void switchTabInActivity(int indexTabToSwitchTo){
        DietHelperActivity parentActivity;
        parentActivity = (DietHelperActivity) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo);
	}

	public void checkLimit(int sum){
		int mode = SaveUtils.getMode(this);
		int customLimit = SaveUtils.getCustomLimit(this);
		if(customLimit>0){
			SaveUtils.saveBMR(String.valueOf(customLimit), this);
			SaveUtils.saveMETA(String.valueOf(customLimit), this);
		}
		try{
		switch (mode) {
		case 0:
			if(sum > Integer.parseInt(SaveUtils.getBMR(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			
			break;
		case 1:
			if(sum > Integer.parseInt(SaveUtils.getMETA(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_green);
			}	
			break;
		case 2:
			if(sum < Integer.parseInt(SaveUtils.getMETA(this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_red);
								
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutAVG);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			break;
		default:
			break;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private OnClickListener showChartListener = new OnClickListener() {
		
		public void onClick(View v) {
			showDialog(DIALOG_CHART);
			
		}
	};	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				try {
					TodayDishHelper.clearAll(CalendarActivity.this);	
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:				
				break;
			}
		}
	};
	public void resume(){
		c.requery();
		da.notifyDataSetChanged();
	}
}
