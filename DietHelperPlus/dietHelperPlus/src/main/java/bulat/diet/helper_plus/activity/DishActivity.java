package bulat.diet.helper_plus.activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.dm.zbar.android.scanner.ZBarConstants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.adapter.TodayDishAdapter;
import bulat.diet.helper_plus.db.DishProvider;
import bulat.diet.helper_plus.db.TemplateDishHelper;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.Dish;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.item.TodayDish;
import bulat.diet.helper_plus.utils.Constants;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;

public class DishActivity extends BaseActivity {
	public static final String DATE = "date";
	public static final String PARENT_NAME = "parentname";
	public static final String BACKBTN = "backbtn";
	private static final int DIALOG_TEMPLATE_NAME = 0;
	private Spinner templateSpinner;
	String curentDateandTime;
	ListView dishesList;
	Cursor c;
	TextView header;
	String parentName = null;
	int sum;
	float sumF;
	float sumC;
	float sumP;
	private DishActivityGroup parentDishActivity;
	private CalendarActivityGroup parentCalendarActivity;
	private boolean flagLoad = false;
	TodayDishAdapter da;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.today_list, null);
		header = (TextView) viewToLoad.findViewById(R.id.textViewTitle);
		Button exitButton = (Button) viewToLoad.findViewById(R.id.buttonExit);
		exitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent();
				i.setAction(BaseActivity.CUSTOM_INTENT);
				DishActivity.this.sendBroadcast(i);
				finish();
				finish();
				System.exit(0);
			}
		});
		if (extras != null) {
			Boolean backb = extras.getBoolean(BACKBTN);
			if (backb) {
				Button backButton = (Button) viewToLoad
						.findViewById(R.id.buttonBack);
				backButton.setVisibility(View.VISIBLE);
				exitButton.setVisibility(View.GONE);
				backButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						onBackPressed();
					}
				});
			}
		}
		Button addButton = (Button) viewToLoad.findViewById(R.id.buttonAdd);
		addButton.setOnClickListener(addTodayDishListener);
		Button fitesButton = (Button) viewToLoad.findViewById(R.id.buttonFitnes);
		fitesButton.setOnClickListener(addTodayFitnesListener);
		Button saveButton = (Button) viewToLoad.findViewById(R.id.savetemplate);
		saveButton.setOnClickListener(saveListener);
		Button loadButton = (Button) viewToLoad.findViewById(R.id.loadtemplate);
		loadButton.setOnClickListener(loadListener);
		
		
		setContentView(viewToLoad);
	}

	
	@Override
	protected void onResume() {
		//colors
		//main FFF0E5
		//header FF9730
		//title FFF6EF
		// TODO Auto-generated method stub
		super.onResume();
		templateSpinner = (Spinner) findViewById(R.id.template_spinner);
		loadTemplates();
	
		Bundle extras = getIntent().getExtras();
		String date = null;
		parentName = DishActivityGroup.class.toString();
		if (extras != null) {
			date = extras.getString(DATE);
			parentName = extras.getString(PARENT_NAME);
		}
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMMM",new Locale(getString(R.string.locale)));
			curentDateandTime = sdf.format(new Date());
		} else {
			curentDateandTime = date;
		}
		header.setText(curentDateandTime);
		c = TodayDishHelper.getDishesByDate(getApplicationContext(),
				curentDateandTime);
		if (c != null) {
			try {
				if (CalendarActivityGroup.class.toString().equals(parentName)) {
					da = new TodayDishAdapter(getApplicationContext(), c,
							(CalendarActivityGroup) getParent());
				} else {
					da = new TodayDishAdapter(getApplicationContext(), c,
							(DishActivityGroup) getParent());
				}

				dishesList = (ListView) findViewById(R.id.listViewTodayDishes);
				dishesList.setAdapter(da);
				dishesList.setItemsCanFocus(true);

				da.registerDataSetObserver(new DataSetObserver() {
					@Override
					public void onChanged() {
						sum = 0;
						sumF = 0;
						sumC = 0;
						sumP = 0;
						initDishTable();									
					}
					
				});
				dishesList.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View v,
							int arg2, long arg3) {
					}
				});
				initDishTable();
				
			} catch (Exception e) {
				e.printStackTrace();
				if (c != null)
					c.close();
			} finally {
				// c.close();
			}
		}
	}

	

	private void loadTemplates() {
		ArrayList<DishType> types = new ArrayList<DishType>();
		types.add(new DishType(0, getString(R.string.choosetemplate)));
		types.addAll(TemplateDishHelper.getDaysArray(DishActivity.this));		
		
		ArrayAdapter<DishType> adapter2 = new ArrayAdapter<DishType>(DishActivity.this, android.R.layout.simple_dropdown_item_1line, types);
		
		((ArrayAdapter<DishType>) adapter2).setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		templateSpinner.setAdapter(adapter2);
		templateSpinner.setOnItemSelectedListener(spinnerListener);
		
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, "Scan Result = " , Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);               
        switch (requestCode) {
        	case DishListActivity.ZBAR_SCANNER_REQUEST:
        	case DishListActivity.ZBAR_QR_SCANNER_REQUEST:
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED && data != null) {
                String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                if(!TextUtils.isEmpty(error)) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
	        case 1: {	        		 				        
	            if (resultCode == RESULT_OK && null != data) {
	 
	                ArrayList<String> text = data
	                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);	 
	                Constants.ST = text.get(0);	  	               
	            }
	            
	            break;
	        }    
	        	 
        }


    }
/*
	public void checkLimit(int sum){
		int mode = SaveUtils.getMode(this);
		switch (mode) {
		case 0:
			if(sum > Integer.parseInt(SaveUtils.getBMR(DishActivity.this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			TextView tvLimit = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit.setText(String.valueOf(SaveUtils.getBMR(DishActivity.this)) + getString(R.string.kcal));
			break;
		case 1:
			if(sum > Integer.parseInt(SaveUtils.getMETA(DishActivity.this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}	
			TextView tvLimit2 = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit2.setText(String.valueOf(SaveUtils.getMETA(DishActivity.this)) + getString(R.string.kcal));
			break;
		case 2:
			TextView tvLimit3 = (TextView) findViewById(R.id.textViewLimitValue);
			tvLimit3.setText(String.valueOf(SaveUtils.getMETA(DishActivity.this)) + getString(R.string.kcal) );
			if(sum < Integer.parseInt(SaveUtils.getMETA(DishActivity.this))){
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_red);
								
			}else{
				LinearLayout totalLayout = (LinearLayout) findViewById(R.id.linearLayoutTotal);
				totalLayout.setBackgroundResource(R.color.light_green);
			}
			break;
		default:
			break;
		}
		
	}
*/	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (c != null)
			c.close();
		Intent i = new Intent();
		i.setAction(BaseActivity.CUSTOM_INTENT);
		DishActivity.this.sendBroadcast(i);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (c != null)
			c.close();

	}
	
	private OnClickListener addTodayDishListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra(DishActivity.DATE, curentDateandTime);
			intent.putExtra(PARENT_NAME, parentName);
			intent.setClass(getParent(), DishListActivity.class);
			if (CalendarActivityGroup.class.toString().equals(parentName)) {
				CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
				activityStack.push("DishListActivityCalendar", intent);
			} else {
				DishActivityGroup activityStack = (DishActivityGroup) getParent();
				activityStack.push("DishListActivity", intent);
			}
		}
	};
	

	private OnClickListener addTodayFitnesListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra(DishActivity.DATE, curentDateandTime);
			intent.putExtra(PARENT_NAME, parentName);
			intent.putExtra(AddTodayDishActivity.ADD, 1);
			intent.setClass(getParent(), AddTodayFitnesActivity.class);
			if (CalendarActivityGroup.class.toString().equals(parentName)) {
				CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
				activityStack.push("FitnesActivityCalendar", intent);
			} else {
				DishActivityGroup activityStack = (DishActivityGroup) getParent();
				activityStack.push("FitnesActivity", intent);
			}
		}
	};
	
private OnClickListener saveListener = new OnClickListener() {
		
		public void onClick(View v) {			
			showDialog(DIALOG_TEMPLATE_NAME);
		}
	};
	
	private OnClickListener loadListener = new OnClickListener() {
		
		

		public void onClick(View v) {			
			templateSpinner.setSelection(0);
			flagLoad = true;
			if(TemplateDishHelper.getDaysArray(DishActivity.this).size() == 0){
				Toast.makeText(DishActivity.this, getString(R.string.templatesempty),
						Toast.LENGTH_LONG).show();
			}else{
				templateSpinner.performClick();
			}
			
		}
	};
	

	private void initDishTable(){
		if (c.getCount() > 0) {
			c.moveToFirst();
			if (c.getString(c
					.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)) != null) {
				sum = sum
						+ Integer
								.parseInt(c.getString(c
										.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)));

				sumF = sumF
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_FAT));
				sumC = sumC
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_CARBON));
				sumP = sumP
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_PROTEIN));
			}
			while (c.moveToNext()) {

				sum = sum
						+ Integer
								.parseInt(c.getString(c
										.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)));
				sumF = sumF
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_FAT));
				sumC = sumC
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_CARBON));
				sumP = sumP
						+ c.getFloat(c
								.getColumnIndex(DishProvider.TODAY_DISH_PROTEIN));
			}
			TextView tv = (TextView) findViewById(R.id.textViewTotalValue);			
			TextView tvF = (TextView) findViewById(R.id.textViewFatTotal);			
			TextView tvC = (TextView) findViewById(R.id.textViewCarbonTotal);			
			TextView tvP = (TextView) findViewById(R.id.textViewProteinTotal);
			TextView tvFp = (TextView) findViewById(R.id.textViewFatPercent);
			TextView tvCp = (TextView) findViewById(R.id.textViewCarbonPercent);
			TextView tvPp = (TextView) findViewById(R.id.textViewProteinPercent);
			tv.setText(String.valueOf(sum));
			DecimalFormat df = new DecimalFormat("###.#");
			tvF.setText(df.format(Float.valueOf(sumF)));
			tvC.setText(df.format(Float.valueOf(sumC)));
			tvP.setText(df.format(Float.valueOf(sumP)));
			float sum = Float.valueOf(sumF)+Float.valueOf(sumC)+Float.valueOf(sumP);
			tvFp.setText("("+df.format(Float.valueOf(sumF)*100/sum)+"%)");
			tvCp.setText("("+df.format(Float.valueOf(sumC)*100/sum)+"%)");
			tvPp.setText("("+df.format(Float.valueOf(sumP)*100/sum)+"%)");
		} else {
			LinearLayout emptyHeader = (LinearLayout)findViewById(R.id.linearLayoutEmptyListHeader);
			emptyHeader.setVisibility(View.VISIBLE);
			emptyHeader.setOnClickListener(addTodayDishListener);
			TextView tv = (TextView) findViewById(R.id.textViewTotalValue);
			TextView tvF = (TextView) findViewById(R.id.textViewFatTotal);
			TextView tvC = (TextView) findViewById(R.id.textViewCarbonTotal);
			TextView tvP = (TextView) findViewById(R.id.textViewProteinTotal);
			TextView tvFp = (TextView) findViewById(R.id.textViewFatPercent);
			TextView tvCp = (TextView) findViewById(R.id.textViewCarbonPercent);
			TextView tvPp = (TextView) findViewById(R.id.textViewProteinPercent);
			tv.setText(String.valueOf(0));
			DecimalFormat df = new DecimalFormat("###.#");
			tvF.setText(df.format(Float.valueOf(sumF)));
			tvC.setText(df.format(Float.valueOf(sumC)));
			tvP.setText(df.format(Float.valueOf(sumP)));
			float sum = Float.valueOf(sumF)+Float.valueOf(sumC)+Float.valueOf(sumP);
			tvFp.setText("("+df.format(Float.valueOf(sumF)*100/sum)+"%)");
			tvCp.setText("("+df.format(Float.valueOf(sumC)*100/sum)+"%)");
			tvPp.setText("("+df.format(Float.valueOf(sumP)*100/sum)+"%)");
		}
		
		checkLimit(sum);
	}
	
	private OnClickListener waterTodayListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent();

			try {
				
				intent.setClass(getParent(), AddTodayDishActivity.class);
				intent.putExtra(AddTodayDishActivity.TITLE,
						getString(R.string.add_today_dish));
				intent.putExtra(AddTodayDishActivity.ADD, 1);
				intent.putExtra(DishActivity.DATE, curentDateandTime);
				intent.putExtra(AddTodayDishActivity.DISH_NAME,
						getString(R.string.water_name));
				intent.putExtra(AddTodayDishActivity.DISH_CALORISITY,
						0);
				intent.putExtra(AddTodayDishActivity.DISH_FAT, "0");
				intent.putExtra(AddTodayDishActivity.DISH_CARBON, "0");
				intent.putExtra(AddTodayDishActivity.DISH_PROTEIN, "0");
				intent.putExtra(AddTodayDishActivity.DISH_ABSOLUTE_CALORISITY,0);	
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// TODO Auto-generated method stub
			// ((TextView)arg1.findViewById(R.id.textViewDishName)).getText()

			if (CalendarActivityGroup.class.toString().equals(parentName)) {
				CalendarActivityGroup activityStack = (CalendarActivityGroup) DishActivity.this
						.getParent();
				activityStack.push("AddTodayDishActivityCalendar", intent);
			} else {
				DishActivityGroup activityStack = (DishActivityGroup) DishActivity.this
						.getParent();
				activityStack.push("AddTodayDishActivity", intent);
			}

		}
	};

	
	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(flagLoad){
				flagLoad=false;
				AlertDialog.Builder builder = null;
				
				builder = new AlertDialog.Builder(
							DishActivity.this.getParent());
				

				builder.setMessage(R.string.remove_dialog)
						.setPositiveButton(DishActivity.this.getString(R.string.yes),
								dialogClickListener)
						.setNegativeButton(DishActivity.this.getString(R.string.no),
								dialogClickListener).show();
				
			
			}

			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				try {
					LinearLayout emptyHeader = (LinearLayout)findViewById(R.id.linearLayoutEmptyListHeader);
					emptyHeader.setVisibility(View.GONE);
					Cursor cTemp=null;
					SimpleDateFormat sdf = new SimpleDateFormat(
							"EEE dd MMMM",new Locale(getString(R.string.locale)));
					Date curentDateandTimeLong = null;			
						try {
							curentDateandTimeLong = sdf.parse(curentDateandTime);
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					Date nowDate = new Date();
					try{
						curentDateandTimeLong.setYear(nowDate.getYear());
					}catch (Exception e) {
						e.printStackTrace();
					}
					try{
					cTemp = TemplateDishHelper.getDishesByDate( DishActivity.this, ((DishType) templateSpinner.getSelectedItem()).getDescription());
					if(cTemp.getCount()>0){	
						if (0!=SaveUtils.getUserUnicId(DishActivity.this)) {
						//	removeDaySocial(curentDateandTime);
						}
						//TodayDishHelper.removeDishesByDay(curentDateandTime, DishActivity.this);
						da.newCursor = true;
						
					}else{
						Toast.makeText(DishActivity.this, getString(R.string.loadtemplateempty),
								Toast.LENGTH_LONG).show();
					}
					while (cTemp.moveToNext())
			        {	
						try{
							TodayDish td = new TodayDish(
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_ID))>0?cTemp.getFloat(cTemp
													.getColumnIndex(DishProvider.TODAY_DISH_ID)):SaveUtils.getRealWeight(DishActivity.this),
									cTemp.getString(cTemp
											.getColumnIndex(DishProvider.TODAY_NAME)),
									cTemp.getString(cTemp
											.getColumnIndex(DishProvider.TODAY_DESCRIPTION)),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_CALORICITY)),
									cTemp.getString(cTemp
											.getColumnIndex(DishProvider.TODAY_CATEGORY)),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_WEIGHT)),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)),
									curentDateandTime,
									curentDateandTimeLong.getTime(),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_IS_DAY)),
									cTemp.getString(cTemp
											.getColumnIndex(DishProvider.TODAY_TYPE)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_FAT)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_FAT)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_CARBON)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_CARBON)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_PROTEIN)),
									cTemp.getFloat(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_PROTEIN)),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_TIME_HH)),
									cTemp.getInt(cTemp
											.getColumnIndex(DishProvider.TODAY_DISH_TIME_MM)));
							td.setId(TodayDishHelper.addNewDish(td, DishActivity.this));	
							if (0!=SaveUtils.getUserUnicId(DishActivity.this)) {
								new SocialUpdater(DishActivity.this, td,false).execute();
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
			        }
					}catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(cTemp!= null){
							cTemp.close();
						}
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
				templateSpinner.setSelection(0);
				Toast.makeText(DishActivity.this, getString(R.string.loadtemplate),
						Toast.LENGTH_LONG).show();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				templateSpinner.setSelection(0);
				break;
			}
		}
	};
	private EditText templateName;
	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		Button buttonOk;
		Button nobutton;
		switch (id) {
		
		case DIALOG_TEMPLATE_NAME:
			dialog = new Dialog(this.getParent());
			dialog.setContentView(R.layout.user_name_dialog);
			dialog.setTitle(R.string.templates);

			templateName = (EditText) dialog.findViewById(R.id.editTextUserName);
			TextView maintext = (TextView) dialog.findViewById(R.id.textViewSerchActivityLevelLabel);
			maintext.setText(getText(R.string.template_save));
			TextView nametext = (TextView) dialog.findViewById(R.id.textViewNameLabel);
			nametext.setText(getText(R.string.template_name));
			buttonOk = (Button) dialog.findViewById(R.id.buttonYes);
			buttonOk.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					
					if(templateName.getText().length()<1){
						templateName.setBackgroundColor(Color.RED);
					}else{
						Cursor cTemp=null;
						try{
						cTemp = TodayDishHelper.getDishesByDate( DishActivity.this, curentDateandTime);
						while (cTemp.moveToNext())
				        {	
							try{
								TemplateDishHelper.addNewDish(new TodayDish(
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_ID)),
										cTemp.getString(cTemp
												.getColumnIndex(DishProvider.TODAY_NAME)),
										cTemp.getString(cTemp
												.getColumnIndex(DishProvider.TODAY_DESCRIPTION)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_CALORICITY)),
										cTemp.getString(cTemp
												.getColumnIndex(DishProvider.TODAY_CATEGORY)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_WEIGHT)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_CALORICITY)),
										templateName.getText().toString(),
										cTemp.getLong(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_DATE_LONG)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_IS_DAY)),
										cTemp.getString(cTemp
												.getColumnIndex(DishProvider.TODAY_TYPE)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_FAT)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_FAT)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_CARBON)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_CARBON)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_PROTEIN)),
										cTemp.getFloat(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_PROTEIN)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_TIME_HH)),
										cTemp.getInt(cTemp
												.getColumnIndex(DishProvider.TODAY_DISH_TIME_MM))), DishActivity.this);					
							}catch (Exception e) {
								e.printStackTrace();
							}
				        }
						}catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(cTemp!= null){
								cTemp.close();
							}
						}	
						Toast.makeText(DishActivity.this, getString(R.string.savetemplate),
								Toast.LENGTH_LONG).show();
						loadTemplates();
						dialog.cancel();
					}
					
				}
			});
			nobutton = (Button) dialog.findViewById(R.id.buttonNo);
			nobutton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.cancel();
				}
			});

			break;
		default:
			dialog = null;
		}

		return dialog;
	}



	protected void removeDaySocial(String date) {
		// TODO Auto-generated method stub
		Cursor c =TodayDishHelper.getDishesByDate( DishActivity.this, date);
		if (c != null) {
			try {
				while (c.moveToNext()) {					
					new SocialUpdater(DishActivity.this, c.getString(c.getColumnIndex("_id"))).execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				c.close();
			}
		}
		
	}
}
