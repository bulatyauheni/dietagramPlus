package bulat.diet.helper_plus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.db.TemplateDishHelper;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.item.TodayDish;
import bulat.diet.helper_plus.reciver.CaloryAppWidgetProvider;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;

public class AddTodayDishActivity extends BaseActivity {
	public static final String DISH_NAME = "dish_name";
	public static final String DISH_CALORISITY = "dish_calorisity";
	public static final String DISH_CATEGORY = "dish_category";
	public static final String DISH_STAT_TYPE = "dish_stat_type";
	public static final String DISH_TIME = "dish_time";
	public static final String DISH_ABSOLUTE_CALORISITY = "dish_absolute_calorisity";
	public static final String TITLE = "title_header";
	public static final String DISH_WEIGHT = "dish_weight";
	public static final String DISH_TYPE = "dish_type";
	public static final String BARCODE = "barcode";
	public static final String ID = "id";
	public static final String ADD = "add_dish";
	public static final String DISH_FAT = "fat";
	public static final String DISH_CARBON = "carbon";
	public static final String DISH_PROTEIN = "protein";
	public static final String DISH_TIME_HH = "DISH_TIME_HH";
	public static final String DISH_TIME_MM = "DISH_TIME_MM";
	TextView dishCaloricityVTW;
	EditText weightView;
	TextView dishNameTW;
	Button okbutton;
	Button changebutton;
	Button nobutton;
	int dc = 0;
	String id = null;
	int flag_add = 0;
	Integer category;
	String currDate;
	boolean templateFlag = false;
	InputMethodManager imm;
	private String parentName;
	Spinner spinnerTime;
	private String timeValue;
	private String typeValue;
	private String fatValue;
	private String proteinValue;
	private String carbonValue;
	private TextView dishFatVTW;
	private TextView dishCarbonVTW;
	private TextView dishProteinVTW;
	private Spinner spinnerTimeHH;
	private Spinner spinnerTimeMM;
	private String timeMMValue;
	private String timeHHValue;
	private String recepyId;
	private boolean edit = true;

	// public static final String DISH_NAME = "dish_name";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.add_today_dish, null);
		TextView header = (TextView) viewToLoad
				.findViewById(R.id.textViewTitle);
		weightView = (EditText) viewToLoad.findViewById(R.id.timepicker_input);
		dishNameTW = (TextView) viewToLoad.findViewById(R.id.textViewDishName);

		dishCaloricityVTW = (TextView) viewToLoad
				.findViewById(R.id.textCaloricityValue);
		dishFatVTW = (TextView) viewToLoad.findViewById(R.id.textFatValue);
		dishCarbonVTW = (TextView) viewToLoad
				.findViewById(R.id.textCarbonValue);
		dishProteinVTW = (TextView) viewToLoad
				.findViewById(R.id.textProteinValue);
		Bundle extras = getIntent().getExtras();
		recepyId = extras.getString(RecepyActivity.ID);
		String dn = extras.getString(DISH_NAME);
		parentName = extras.getString(DishActivity.PARENT_NAME);
		int weight = extras.getInt(DISH_WEIGHT);
		templateFlag = extras.getBoolean(NewTemplateActivity.TEMPLATE);
		if (weight == 0) {
			edit = false;
			weight = 100;
		}

		dc = extras.getInt(DISH_CALORISITY);
		category = extras.getInt(DISH_CATEGORY);
		fatValue = extras.getString(DISH_FAT);
		carbonValue = extras.getString(DISH_CARBON);
		proteinValue = extras.getString(DISH_PROTEIN);

		flag_add = extras.getInt(ADD);
		id = extras.getString(ID);
		// set header
		String title = extras.getString(TITLE);
		header.setText(title);
		if (title == null && flag_add == 0) {
			header.setText(getString(R.string.edit_today_dish));
		}
		Button backButton = (Button) viewToLoad.findViewById(R.id.buttonBack);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					((DishActivityGroup) AddTodayDishActivity.this.getParent())
							.getFirst();
				} catch (Exception e) {
					try {((CalendarActivityGroup) AddTodayDishActivity.this
							.getParent()).popNum(2);
					} catch (Exception e2) {
						((DishListActivityGroup) AddTodayDishActivity.this
								.getParent()).pop(2);
					}
				}
			}
		});
		// set name of dish
		dishNameTW.setText(dn);
		// set time spiner
		spinnerTime = (Spinner) viewToLoad.findViewById(R.id.SpinnerPartOfDay);
		
		ArrayList<DishType> time = new ArrayList<DishType>();
		time.add(new DishType(0, getString(R.string.time_breakfast)));
		time.add(new DishType(1, getString(R.string.time_branch)));
		time.add(new DishType(2, getString(R.string.time_dinner)));
		time.add(new DishType(3, getString(R.string.time_lanch)));
		time.add(new DishType(4, getString(R.string.time_supper)));
		ArrayAdapter<DishType> adapter = new ArrayAdapter<DishType>(this,
				android.R.layout.simple_spinner_item, time);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTime.setAdapter(adapter);
		spinnerTime.setSelection(SaveUtils.getLastTime(this));
		spinnerTime.setOnItemSelectedListener(spinnerListener);
		timeValue = extras.getString(DISH_TIME);
		typeValue = extras.getString(DISH_STAT_TYPE);
		if (timeValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeValue);
				spinnerTime.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		spinnerTimeHH = (Spinner) viewToLoad.findViewById(R.id.SpinnerHour);
		
		ArrayList<DishType> hours = new ArrayList<DishType>();
		for (int i = 0; i < 24; i++) {
			hours.add(new DishType(i, String.valueOf(i)));
		}
		ArrayAdapter<DishType> adapterHH = new ArrayAdapter<DishType>(this,
				android.R.layout.simple_spinner_item, hours);
		Calendar c = Calendar.getInstance(); 
		
		adapterHH
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTimeHH.setAdapter(adapterHH);
		spinnerTimeHH.setSelection(c.getTime().getHours());
		spinnerTimeHH.setOnItemSelectedListener(spinnerListener);
		timeHHValue = extras.getString(DISH_TIME_HH);

		if (timeHHValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeHHValue);
				spinnerTimeHH.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		spinnerTimeMM = (Spinner) viewToLoad.findViewById(R.id.SpinnerMin);
		if(recepyId!=null){
			LinearLayout linearLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutTime);
			linearLayout.setVisibility(View.GONE);
		}
		ArrayList<DishType> min = new ArrayList<DishType>();
		for (int i = 0; i < 60; i++) {
			min.add(new DishType(i, String.valueOf(i)));
		}
		ArrayAdapter<DishType> adapterMM = new ArrayAdapter<DishType>(this,
				android.R.layout.simple_spinner_item, min);
		adapterMM
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTimeMM.setAdapter(adapterMM);
		spinnerTimeMM.setSelection(c.getTime().getMinutes());
		spinnerTimeMM.setOnItemSelectedListener(spinnerListener);
		timeMMValue = extras.getString(DISH_TIME_MM);

		if (timeMMValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeMMValue);
				spinnerTimeMM.setSelection(timeValueint);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

		// set weight
		weightView.addTextChangedListener(searchEditTextWatcher);
		weightView.setOnEditorActionListener(onEditListener);
		weightView.setText(String.valueOf(weight));
		// set caloriity
		dishCaloricityVTW.setText(String.valueOf(dc
				* Integer.valueOf(weightView.getText().toString()) / 100));
		DecimalFormat df = new DecimalFormat("###.#");
		try {
			if (fatValue != null && fatValue.length() > 0) {
				dishFatVTW
						.setText(df.format(Float.valueOf(fatValue.trim()
								.replace(',', '.'))
								* Float.valueOf(weightView.getText().toString())
								/ 100));
			} else {
				dishCarbonVTW.setText(df.format(0));
			}
			if (carbonValue != null && carbonValue.length() > 0) {
				dishCarbonVTW
						.setText(df.format(Float.valueOf(carbonValue.trim()
								.replace(',', '.'))
								* Float.valueOf(weightView.getText().toString())
								/ 100));
			} else {
				dishCarbonVTW.setText(df.format(0));
			}
			if (proteinValue != null && proteinValue.length() > 0) {

				dishProteinVTW
						.setText(df.format(Float.valueOf(proteinValue.trim()
								.replace(',', '.'))
								* Float.valueOf(weightView.getText().toString())
								/ 100));
			} else {
				dishCarbonVTW.setText(df.format(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		okbutton = (Button) viewToLoad.findViewById(R.id.buttonYes);
		okbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				StartActivity.checkCalendar(AddTodayDishActivity.this);
				if (!"".endsWith(weightView.getText().toString())) {
					if (flag_add == 1) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"EEE dd MMMM", new Locale(
										getString(R.string.locale)));
						Date curentDateandTime = null;

						try {
							if (templateFlag) {
								curentDateandTime = new Date();
							} else {
								curentDateandTime = sdf.parse(currDate);

								Date nowDate = new Date();
								curentDateandTime.setYear(nowDate.getYear());
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//SaveUtils
						//.getRealWeight(AddTodayDishActivity.this)
						
						Float fat = (float) 0;
						Float dishfat = (float) 0;						
						Float protein = (float) 0;		
						Float dishprotein = (float) 0;	
						Float carbon = (float) 0;	
						Float dishcarbon = (float) 0;	
						try{
							fat = Float.parseFloat(fatValue.trim().replace(',',
								'.'));
							dishfat = Float.parseFloat(dishFatVTW
									.getText().toString().trim()
									.replace(',', '.'));
							protein = Float.parseFloat(proteinValue.trim()
									.replace(',', '.'));
														
							dishprotein = Float
									.parseFloat(dishProteinVTW.getText()
											.toString().trim()
											.replace(',', '.'));
							carbon = Float
									.parseFloat(carbonValue.trim().replace(
											',', '.'));
							dishcarbon = Float
									.parseFloat(dishCarbonVTW.getText()
											.toString().trim()
											.replace(',', '.'));
						}catch(Exception e){
							e.printStackTrace();							
						}
						
						
						TodayDish td = new TodayDish(TodayDishHelper.getBodyWeightByDate(curentDateandTime.getTime(), AddTodayDishActivity.this),
										dishNameTW.getText().toString(), 
										String.valueOf(spinnerTime.getSelectedItemId()), 
										dc,
										category.toString(), 
										Integer.parseInt(weightView.getText()
												.toString()), 
										Integer.parseInt(dishCaloricityVTW.getText()
												.toString()), 
										currDate,
										curentDateandTime.getTime(), 
										0, 
										typeValue,
										fat, 
										dishfat, 
										carbon,
										dishcarbon, 
										protein,
										dishprotein,
								(int) spinnerTimeHH.getSelectedItemId(),
								(int) spinnerTimeMM.getSelectedItemId());
						if (templateFlag) {
							if(recepyId != null){
								td.setDateTime(0);
								td.setDate(recepyId);
							}
							td.setId(TemplateDishHelper.addNewDish(td,
									AddTodayDishActivity.this));
						} else {
							td.setId(TodayDishHelper.addNewDish(td,
									AddTodayDishActivity.this));
							if (0 != SaveUtils
									.getUserUnicId(AddTodayDishActivity.this)) {
								new SocialUpdater(AddTodayDishActivity.this,
										td, false).execute();
							}
						}

					} else {
						if (id != null) {
							TodayDish td = new TodayDish(id, dishNameTW
									.getText().toString(), String
									.valueOf(spinnerTime.getSelectedItemId()),
									dc, "", Integer.parseInt(weightView
											.getText().toString()), Integer
											.parseInt(dishCaloricityVTW
													.getText().toString()),
									currDate, Float.parseFloat(fatValue), Float
											.parseFloat(dishFatVTW.getText()
													.toString().trim()
													.replace(',', '.')), Float
											.parseFloat(carbonValue), Float
											.parseFloat(dishCarbonVTW.getText()
													.toString().trim()
													.replace(',', '.')), Float
											.parseFloat(proteinValue), Float
											.parseFloat(dishProteinVTW
													.getText().toString()
													.trim().replace(',', '.')),
									(int) spinnerTimeHH.getSelectedItemId(),
									(int) spinnerTimeMM.getSelectedItemId());
							if (templateFlag) {
								TemplateDishHelper.updateDish(td,
										AddTodayDishActivity.this);
							} else {
								TodayDishHelper.updateDish(td,
										AddTodayDishActivity.this);
								if (0 != SaveUtils
										.getUserUnicId(AddTodayDishActivity.this)) {
									new SocialUpdater(
											AddTodayDishActivity.this, td, true)
											.execute();
								}
							}
						}
					}
					if (CalendarActivityGroup.class.toString().equals(
							parentName)) {
						CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
						activityStack.popNum(2);
					} else {
						try {
							DishActivityGroup activityStack = (DishActivityGroup) getParent();
							activityStack.getFirst();
						} catch (Exception e) {
							try {
								CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
								activityStack.pop();															
							} catch (Exception e2) {
								DishListActivityGroup activityStack2 = (DishListActivityGroup) getParent();
								if (parentName == null) {
									activityStack2.popNum(1);
								} else {
									activityStack2.popNum(2);
								}								
								
								e.printStackTrace();
							}
						}
					}

				} else {
					// weightView.setBackgroundColor(Color.RED);
				}
				AddTodayDishActivity.this.sendBroadcast(
						new Intent(AddTodayDishActivity.this,
								CaloryAppWidgetProvider.class).
								setAction(BaseActivity.CUSTOM_INTENT));


			}
		});
		nobutton = (Button) viewToLoad.findViewById(R.id.buttonNo);
		nobutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (CalendarActivityGroup.class.toString().equals(parentName)) {
					CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
					activityStack.popNum(2);
				} else {
					try {
						DishActivityGroup activityStack = (DishActivityGroup) getParent();
						activityStack.getFirst();
					} catch (Exception e) {
						try {
							CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
							activityStack.pop();															
						} catch (Exception e2) {
							DishListActivityGroup activityStack2 = (DishListActivityGroup) getParent();
							activityStack2.popNum(2);
							e.printStackTrace();
						}
					}
				}
			}
		});

		changebutton = (Button) viewToLoad.findViewById(R.id.buttonChangeDish);
		changebutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(DishActivity.DATE, currDate);
				intent.putExtra(ID, id);
				intent.putExtra(DishActivity.PARENT_NAME, parentName);
				intent.setClass(getParent(), DishListActivity.class);
				if (CalendarActivityGroup.class.toString().equals(parentName)) {
					CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
					activityStack.push("DishListActivityCalendar", intent);
				} else {
					try {
						DishActivityGroup activityStack = (DishActivityGroup) getParent();
						activityStack.push("DishListActivity", intent);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();						
							activityStack.push("DishListActivityCalendar", intent);
						}catch (Exception e2) {						
							DishListActivityGroup activityStack = (DishListActivityGroup) getParent();						
							activityStack.push("DishListActivityCalendar", intent);
						}
					}
				}
			}
		});

		currDate = extras.getString(DishActivity.DATE);
		setContentView(viewToLoad);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {

		super.onPause();
		imm.hideSoftInputFromWindow(weightView.getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		imm = (InputMethodManager) AddTodayDishActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		spinnerTime.setSelection(SaveUtils.getLastTime(this));
		if(recepyId == null && !edit){
			spinnerTime.post(new Runnable(){
				@Override
				public void run() {
					spinnerTime.performClick();
				}
			});
		}
		if (timeMMValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeMMValue);
				spinnerTimeMM.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (timeHHValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeHHValue);
				spinnerTimeHH.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (timeValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeValue);
				spinnerTime.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private OnEditorActionListener onEditListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			try {
				dishCaloricityVTW.setText(String.valueOf(dc
						* Integer.valueOf(weightView.getText().toString())
						/ 100));

				DecimalFormat df = new DecimalFormat("###.#");
				dishFatVTW.setText(df.format(Float.valueOf(fatValue.trim()
						.replace(',', '.'))
						* Float.valueOf(weightView.getText().toString().trim()
								.replace(',', '.')) / 100));
				dishCarbonVTW.setText(df.format(Float.valueOf(carbonValue
						.trim().replace(',', '.'))
						* Float.valueOf(weightView.getText().toString().trim()
								.replace(',', '.')) / 100));
				dishProteinVTW.setText(df.format(Float.valueOf(proteinValue
						.trim().replace(',', '.'))
						* Float.valueOf(weightView.getText().toString().trim()
								.replace(',', '.')) / 100));
			} catch (Exception e) {
				dishCaloricityVTW.setText("0");
				e.printStackTrace();
			}
			return false;
		}
	};

	private TextWatcher searchEditTextWatcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void afterTextChanged(Editable s) {
			try {
				if (!"".endsWith(weightView.getText().toString())) {

					dishCaloricityVTW.setText(String.valueOf(dc
							* Integer.valueOf(weightView.getText().toString())
							/ 100));
					DecimalFormat df = new DecimalFormat("###.#");
					dishFatVTW.setText(df.format(Float.valueOf(fatValue)
							* Float.valueOf(weightView.getText().toString())
							/ 100));
					dishCarbonVTW.setText(df.format(Float.valueOf(carbonValue)
							* Float.valueOf(weightView.getText().toString())
							/ 100));
					dishProteinVTW.setText(df.format(Float
							.valueOf(proteinValue)
							* Float.valueOf(weightView.getText().toString())
							/ 100));
					// weightView.setBackgroundColor(Color.WHITE);
				} else {
					// weightView.setBackgroundColor(Color.RED);
				}
			} catch (Exception e) {
				dishCaloricityVTW.setText("0");
			}
		}
	};
	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			SaveUtils.saveLastTime((int) (((DishType) spinnerTime
					.getSelectedItem()).getTypeKey()),
					AddTodayDishActivity.this);

		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
	
}
