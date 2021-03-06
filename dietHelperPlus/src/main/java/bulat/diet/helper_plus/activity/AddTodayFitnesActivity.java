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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.db.DishListHelper;
import bulat.diet.helper_plus.db.TemplateDishHelper;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.item.FitnesType;
import bulat.diet.helper_plus.item.TodayDish;
import bulat.diet.helper_plus.reciver.CaloryAppWidgetProvider;
import bulat.diet.helper_plus.utils.Constants;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;

public class AddTodayFitnesActivity extends BaseActivity {
	public static final String DISH_NAME = "dish_name";
	public static final String DISH_CALORISITY = "dish_calorisity";
	public static final String DISH_CATEGORY = "dish_category";
	public static final String DISH_ABSOLUTE_CALORISITY = "dish_absolute_calorisity";
	public static final String TITLE = "title_header";
	public static final String DISH_WEIGHT = "dish_weight";
	public static final String DISH_TYPE = "dish_type";
	public static final String ID = "id";
	public static final String ADD = "add_dish";
	TextView dishCaloricityVTW;
	boolean templateFlag = false;
	EditText weightView;
	Button okbutton;
	Button nobutton;
	int dc = 0;
	String id = null;
	int flag_add = 0;
	Integer category;
	String currDate;
	InputMethodManager imm;
	private String parentName;
	Spinner fitnesSpinner;
	Spinner weightSpinner;
	private String activitiName;
	private int selection;
	private Spinner spinnerTimeHH;
	private Spinner spinnerTimeMM;
	private String timeHHValue;
	private String timeMMValue;
	private String sportName;

	// public static final String DISH_NAME = "dish_name";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.add_today_fitnes, null);
		TextView header = (TextView) viewToLoad
				.findViewById(R.id.textViewTitle);
		// weightSpinner = (Spinner)
		// viewToLoad.findViewById(R.id.SpinnerWeight);
		weightView = (EditText) viewToLoad
				.findViewById(R.id.timepicker_input_fit);
		dishCaloricityVTW = (TextView) viewToLoad
				.findViewById(R.id.textCaloricityValue);
		Bundle extras = getIntent().getExtras();
		sportName = extras.getString(DISH_NAME);
		parentName = extras.getString(DishActivity.PARENT_NAME);
		int weight = extras.getInt(DISH_WEIGHT);
		dc = extras.getInt(DISH_CALORISITY);
		templateFlag = extras.getBoolean(NewTemplateActivity.TEMPLATE);
		category = extras.getInt(DISH_CATEGORY);
		flag_add = extras.getInt(ADD);
		id = extras.getString(ID);
		activitiName = "";

		// time
		spinnerTimeHH = (Spinner) viewToLoad.findViewById(R.id.SpinnerHour);
		ArrayList<DishType> hours = new ArrayList<DishType>();
		for (int i = 0; i < 24; i++) {
			hours.add(new DishType(i, String.valueOf(i)));
		}
		ArrayAdapter<DishType> adapterHH = new ArrayAdapter<DishType>(this,
				android.R.layout.simple_spinner_item, hours);
		adapterHH
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTimeHH.setAdapter(adapterHH);
		spinnerTimeHH.setSelection(12);
		spinnerTimeHH.setOnItemSelectedListener(spinnerListener);
		timeHHValue = extras.getString(AddTodayDishActivity.DISH_TIME_HH);

		if (timeHHValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeHHValue);
				spinnerTimeHH.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		spinnerTimeMM = (Spinner) viewToLoad.findViewById(R.id.SpinnerMin);
		ArrayList<DishType> min = new ArrayList<DishType>();
		for (int i = 0; i < 60; i++) {
			min.add(new DishType(i, String.valueOf(i)));
		}
		ArrayAdapter<DishType> adapterMM = new ArrayAdapter<DishType>(this,
				android.R.layout.simple_spinner_item, min);
		adapterMM
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTimeMM.setAdapter(adapterMM);
		spinnerTimeMM.setSelection(30);
		spinnerTimeMM.setOnItemSelectedListener(spinnerListener);
		timeMMValue = extras.getString(AddTodayDishActivity.DISH_TIME_MM);

		if (timeMMValue != null) {
			try {
				int timeValueint = Integer.valueOf(timeMMValue);
				spinnerTimeMM.setSelection(timeValueint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// other
		if (id != null) {
			TodayDish td;
			if (templateFlag) {
				td = TemplateDishHelper.getDishById(id, this);
			} else {
				td = TodayDishHelper.getDishById(id, this);
			}

			weight = td.getWeight();
			activitiName = td.getName();
		}
		// set header
		String title = getString(R.string.today_fitnes);
		header.setText(title);
		if (flag_add == 0) {
			header.setText(getString(R.string.edit_today_fitnes));
		}
		// spiner
		fitnesSpinner = (Spinner) viewToLoad
				.findViewById(R.id.fitnes_type_spinner);

		Button backButton = (Button) viewToLoad.findViewById(R.id.buttonBack);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					((DishActivityGroup) AddTodayFitnesActivity.this
							.getParent()).getFirst();
				} catch (Exception e) {
					((CalendarActivityGroup) AddTodayFitnesActivity.this
							.getParent()).pop();
				}
			}
		});
		// set name of dish

		// set weight
		weightView.addTextChangedListener(searchEditTextWatcher);
		weightView.setOnEditorActionListener(onEditListener);
		if (weight == 0) {
			weightView.setText(String.valueOf(10));
		} else {
			weightView.setText(String.valueOf(weight));
		}
		// set caloriity

		okbutton = (Button) viewToLoad.findViewById(R.id.buttonYes);
		okbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				StartActivity.checkCalendar(AddTodayFitnesActivity.this);
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
						TodayDish td = new TodayDish(SaveUtils
								.getRealWeight(AddTodayFitnesActivity.this),
								((FitnesType) fitnesSpinner.getSelectedItem())
										.getDescription(), String
										.valueOf(Constants.ACTIVITY), -Integer
										.valueOf(dishCaloricityVTW.getText()
												.toString()), category
										.toString(), Integer.valueOf(weightView
										.getText().toString()), -Integer
										.parseInt(dishCaloricityVTW.getText()
												.toString()), currDate,
								curentDateandTime.getTime(), 0, "", 0, 0, 0, 0,
								0, 0, (int) spinnerTimeHH.getSelectedItemId(),
								(int) spinnerTimeMM.getSelectedItemId());
						if (templateFlag) {
							td.setId(TemplateDishHelper.addNewDish(td,
									AddTodayFitnesActivity.this));
						} else {
							td.setId(TodayDishHelper.addNewDish(td,
									AddTodayFitnesActivity.this));
							if (0 != SaveUtils
									.getUserUnicId(AddTodayFitnesActivity.this)) {
								new SocialUpdater(AddTodayFitnesActivity.this,
										td, false).execute();
							}
						}

					} else {
						if (id != null) {
							TodayDish td = new TodayDish(id,
									((FitnesType) fitnesSpinner
											.getSelectedItem())
											.getDescription(), String
											.valueOf(Constants.ACTIVITY), dc,
									"", Integer.parseInt(weightView.getText()
											.toString()), -Integer
											.valueOf(dishCaloricityVTW
													.getText().toString()),
									currDate, 0, 0, 0, 0, 0, 0,
									(int) spinnerTimeHH.getSelectedItemId(),
									(int) spinnerTimeMM.getSelectedItemId());

							if (templateFlag) {
								TemplateDishHelper.updateDish(td,
										AddTodayFitnesActivity.this);
							} else {
								TodayDishHelper.updateDish(td,
										AddTodayFitnesActivity.this);
								if (0 != SaveUtils
										.getUserUnicId(AddTodayFitnesActivity.this)) {
									new SocialUpdater(
											AddTodayFitnesActivity.this, td,
											true).execute();
								}
							}
						}
					}
					if (CalendarActivityGroup.class.toString().equals(
							parentName)) {
						CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
						activityStack.pop();
					} else {
						try {
							DishActivityGroup activityStack = (DishActivityGroup) getParent();
							activityStack.getFirst();
						} catch (Exception e) {
							CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
							activityStack.pop();
						}
					}

				} else {
					// weightView.setBackgroundColor(Color.RED);
				}
				AddTodayFitnesActivity.this.sendBroadcast(
						new Intent(AddTodayFitnesActivity.this,
								CaloryAppWidgetProvider.class).
								setAction(BaseActivity.CUSTOM_INTENT));
			}
		});
		nobutton = (Button) viewToLoad.findViewById(R.id.buttonNo);
		nobutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (CalendarActivityGroup.class.toString().equals(parentName)) {
					CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
					activityStack.pop();
				} else {
					try {
						DishActivityGroup activityStack = (DishActivityGroup) getParent();
						activityStack.getFirst();
					} catch (Exception e) {
						CalendarActivityGroup activityStack = (CalendarActivityGroup) getParent();
						activityStack.pop();
					}
				}
			}
		});
		currDate = extras.getString(DishActivity.DATE);

		// DialogUtils.setArraySpinnerValues(weightSpinner,CalcActivity.MIN_WEIGHT,CalcActivity.MAX_WEIGHT,
		// AddTodayFitnesActivity.this);
		// weightSpinner.setOnItemSelectedListener(spinnerListener);
		// weightSpinner.setSelection(SaveUtils.getWeight(this));

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

		ArrayList<FitnesType> types = DishListHelper.LoadFitnesMap(this);
		selection = 0;
		for (FitnesType fitnesType : types) {
			if(fitnesType.getDescription().equals(activitiName)){
				selection = types.indexOf(fitnesType);
			}
		}
		// TODO Auto-generated method stub
		// Need to get number or smth else of activity to show current activity
		// in editing window.

		ArrayAdapter<FitnesType> adapter = new ArrayAdapter<FitnesType>(this,
				android.R.layout.simple_spinner_item, types);
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
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fitnesSpinner.setAdapter(adapter);
		fitnesSpinner.setSelection(selection);
		fitnesSpinner.setOnItemSelectedListener(spinnerListener);

		imm = (InputMethodManager) AddTodayFitnesActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		setCaloryLoosingView();

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
				setCaloryLoosingView();
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
					setCaloryLoosingView();
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
			setCaloryLoosingView();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	};

	protected void setCaloryLoosingView() {
		// TODO Auto-generated method stub
		dishCaloricityVTW
				.setText(String.valueOf((int) (((FitnesType) fitnesSpinner
						.getSelectedItem()).getTypeKey()
						* SaveUtils.getRealWeight(AddTodayFitnesActivity.this) * Integer
						.valueOf(weightView.getText().toString()))));

	}

}
