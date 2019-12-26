package bulat.diet.helper_plus.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.controls.ImagedSelector;
import bulat.diet.helper_plus.controls.TitledSwitch;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.utils.CustomAlertDialogBuilder;
import bulat.diet.helper_plus.utils.CustomBottomAlertDialogBuilder;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;

public class Info extends AppCompatActivity {
	private ImagedSelector modeSpinner;
	private TextView BMItextView;
	private TextView BMRtextView;
	private TextView MetatextView;

	public static final int MIN_WEIGHT = 30;
	public static final int MAX_WEIGHT = 200;
	public static final int MIN_HEIGHT = 140;
	public static final int MAX_HEIGHT = 210;
	public static final int MIN_AGE = 8;
	public static final int MAX_AGE = 90;
	
	double BMI;
	int BMR;
	int META;
	private CheckBox chkIos;
	EditText limitET;
	private TitledSwitch sexSelector;
	private TextView highSpinner;
	private TextView weightSelector;
	private TextView ageSpinner;
	private TextView textViewMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ArrayList<String> activity = new ArrayList<String>();
		activity.add(getString(R.string.level_1));
		activity.add(getString(R.string.level_2));
		activity.add(getString(R.string.level_3));
		activity.add(getString(R.string.level_4));
		activity.add(getString(R.string.level_5));

		final View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.settings, null);
		setContentView(viewToLoad);	
		chkIos = (CheckBox)  viewToLoad.findViewById(R.id.cbLimit);
		limitET = (EditText) viewToLoad.findViewById(R.id.editTextLimitValue);
		int cl = SaveUtils.getCustomLimit(this);
		if(cl>0){
			chkIos.setChecked(true);
			limitET.setText(String.valueOf(cl));
			LinearLayout limitsLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutLimits);
			limitsLayout.setVisibility(View.GONE);
			LinearLayout castomLimitLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutCastomLimit);
			castomLimitLayout.setVisibility(View.VISIBLE);
		}
		 
		chkIos.setOnClickListener(new OnClickListener() {

		  public void onClick(View v) {
	                //is chkIos checked?
			if (((CheckBox) v).isChecked()) {
				SaveUtils.setCustomLimit(0, Info.this);
				LinearLayout limitsLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutLimits);
				limitsLayout.setVisibility(View.GONE);
				LinearLayout castomLimitLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutCastomLimit);
				castomLimitLayout.setVisibility(View.VISIBLE);
			}else{
				SaveUtils.setCustomLimit(0, Info.this);
				LinearLayout limitsLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutLimits);
				limitsLayout.setVisibility(View.VISIBLE);
				LinearLayout castomLimitLayout = (LinearLayout) viewToLoad.findViewById(R.id.linearLayoutCastomLimit);
				castomLimitLayout.setVisibility(View.GONE);
			}
	 
		  }
		});
		BMItextView = (TextView) findViewById(R.id.textViewBMI);
		BMRtextView = (TextView) findViewById(R.id.textViewMeta);
		MetatextView = (TextView) findViewById(R.id.textViewBMR);

		Button saveLimitButton = (Button) viewToLoad.findViewById(R.id.buttonYes);
		saveLimitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				limitET.setBackgroundColor(Color.WHITE);
				if(limitET.getText().length()<3){
					limitET.setBackgroundColor(Color.RED);
				}else{
					try{
						SaveUtils.setCustomLimit(Integer.valueOf(limitET.getText().toString()), Info.this);
						Toast.makeText(Info.this, getString(R.string.save_limit),
								Toast.LENGTH_LONG).show();
					}catch (Exception e) {
						limitET.setBackgroundColor(Color.RED);
						e.printStackTrace();
					}
				}
			}
		});

		final TextView textViewActivity = (TextView) findViewById(R.id.textViewActivity);
		ImagedSelector activityLevelSelector = (ImagedSelector) findViewById(R.id.activityLevelSelector);
		activityLevelSelector.setOnItemSelectListener(new ImagedSelector.ItemSelectedListener() {
			@Override
			public void onItemSelected(int position) {
				SaveUtils.saveActivity((int) position, Info.this);
				onValuesUpdated();
				textViewActivity.setText(activity.get(position - 1));
			}
		});
		activityLevelSelector.setSelectedItem(SaveUtils.getActivity(this));
		ageSpinner = (TextView) findViewById(R.id.SpinnerAge);
		ageSpinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomBottomAlertDialogBuilder bld = new CustomBottomAlertDialogBuilder(Info.this.getParent());
				bld.setLayout(R.layout.section_alert_dialog_single_picker_one_button)
						.setFirstPicker(MIN_AGE, MAX_AGE, MIN_AGE + SaveUtils.getAge(Info.this))
						.setDimensionLabel(getBaseContext().getString(R.string.years))
						.setMessage(getBaseContext().getString(R.string.age_colon))
						.setPositiveButton(R.id.dialogButtonOk, new CustomAlertDialogBuilder.DialogValueListener() {

							@Override
							public void onNewDialogValue(Map<String, String> value) {
								SaveUtils.saveAge(Integer.parseInt(value.get(CustomAlertDialogBuilder.FIRST_VALUE)) - MIN_AGE, Info.this);
								ageSpinner.setText(value.get(CustomAlertDialogBuilder.FIRST_VALUE));
								onValuesUpdated();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				bld.show();
			}
		});
		sexSelector = (TitledSwitch) findViewById(R.id.sex_mode);
		sexSelector.setChecked(SaveUtils.getSex(getBaseContext()) == 1);
		sexSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
												   @Override
												   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
													   if (isChecked) {
														   SaveUtils.saveSex(1, getBaseContext());
													   } else {
														   SaveUtils.saveSex(0, getBaseContext());
													   }
													   onValuesUpdated();
												   }
											   }
		);
		highSpinner = (TextView) findViewById(R.id.SpinnerHeight);
		highSpinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomBottomAlertDialogBuilder bld = new CustomBottomAlertDialogBuilder(Info.this.getParent());
				bld.setLayout(R.layout.section_alert_dialog_single_picker_one_button)
						.setFirstPicker(MIN_HEIGHT, MAX_HEIGHT, MIN_HEIGHT + SaveUtils.getHeight(Info.this))
						.setDimensionLabel(getBaseContext().getString(R.string.santimetr))
						.setMessage(Info.this.getParent().getString(R.string.high_colon))

						.setPositiveButton(R.id.dialogButtonOk, new CustomAlertDialogBuilder.DialogValueListener() {

							@Override
							public void onNewDialogValue(Map<String, String> value) {
								SaveUtils.saveHeight(Integer.parseInt(value.get(CustomAlertDialogBuilder.FIRST_VALUE)) - MIN_HEIGHT, Info.this);
								highSpinner.setText(value.get(CustomAlertDialogBuilder.FIRST_VALUE));
								onValuesUpdated();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				bld.show();
			}
		});
		weightSelector = (TextView) findViewById(R.id.SpinnerWeight);
		weightSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomBottomAlertDialogBuilder bld = new CustomBottomAlertDialogBuilder(Info.this.getParent());
				bld.setLayout(R.layout.section_alert_dialog_picker_one_button)
						.setFirstPicker(MIN_WEIGHT, MAX_WEIGHT, MIN_WEIGHT + SaveUtils.getWeight(Info.this))
						.setSecondPicker(0, 9, SaveUtils.getWeightDec(Info.this))
						.setDimensionLabel(getBaseContext().getString(R.string.kgram))
						.setMessage(Info.this.getString(R.string.body_weight))
						.setPositiveButton(R.id.dialogButtonOk, new CustomAlertDialogBuilder.DialogValueListener() {
							@Override
							public void onNewDialogValue(Map<String, String> value) {
								SaveUtils.saveWeight(Integer.parseInt(value.get(CustomAlertDialogBuilder.FIRST_VALUE)) - MIN_WEIGHT, Info.this);
								SaveUtils.saveWeightDec(Integer.parseInt(value.get(CustomAlertDialogBuilder.SECOND_VALUE)), Info.this);
								weightSelector.setText(value.get(CustomAlertDialogBuilder.FIRST_VALUE) + ", " + value.get(CustomAlertDialogBuilder.SECOND_VALUE));
								onValuesUpdated();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				bld.show();
			}
		});

		textViewMode = (TextView) findViewById(R.id.textViewMode);
		final ArrayList<String> mode = new ArrayList<String>();
		mode.add(getString(R.string.losing_weight));
		mode.add(getString(R.string.keeping_weight));
		mode.add(getString(R.string.gaining_weight));

		modeSpinner = (ImagedSelector) findViewById(R.id.dietaModeSelector);
		modeSpinner.setOnItemSelectListener(new ImagedSelector.ItemSelectedListener() {
			@Override
			public void onItemSelected(int position) {
				SaveUtils.saveMode(position-1, Info.this);
				onValuesUpdated();
				textViewMode.setText(mode.get(position - 1));
			}
		});
		modeSpinner.setSelectedItem(SaveUtils.getMode(this)+1);
		setSpinnerValues();

	}

	private void onValuesUpdated() {

		BMI = SaveUtils.getRealWeight(this)/
				(0.01*((SaveUtils.getHeight(this) + MIN_HEIGHT))*
						0.01*(SaveUtils.getHeight(this) + MIN_HEIGHT));
		BMI = Math.round(BMI * 10.0) / 10.0;
		String addText = "";
		if(BMI < 18.5 ){
			addText = getString(R.string.underweight);
		} else
		if(BMI < 24.9 ){
			addText = getString(R.string.normal_weight);
		} else
		if(BMI < 29.9 ){
			addText = getString(R.string.overweight);
		} else {
			addText = getString(R.string.obese);
		}

		BMItextView.setText(String.valueOf(BMI) + " " + addText);//new BigDecimal(BMI).round(new MathContext(1, RoundingMode.HALF_EVEN))) + " " + addText);


		BMR = (int)((10*(SaveUtils.getRealWeight(this))) +
				(6.25*(SaveUtils.getHeight(this) + MIN_HEIGHT)) -
				(5*(SaveUtils.getAge(this) + MIN_AGE)) - 161 +
				(SaveUtils.getSex(this) == 1 ? 0 : 166));
		BMR = (int)BMR;
		int activity = SaveUtils.getActivity(this);
		if(activity==1){
			META = (int)(BMR*1.2);
		}else if(activity==2){
			META =(int)( BMR*1.35);
		}else if(activity==3){
			META = (int)(BMR*1.55);
		}else if(activity==4){
			META = (int)(BMR*1.75);
		}else if(activity==5){
			META = (int)(BMR*1.92);
		}
		BMRtextView.setText(String.valueOf((int)(META * 0.8)) + " " + getString(R.string.calorie_day));//new BigDecimal(BMR).round(new MathContext(1, RoundingMode.HALF_EVEN))));

		MetatextView.setText(String.valueOf(META) + " " + getString(R.string.calorie_day));//new BigDecimal(BMR).round(new MathContext(1, RoundingMode.HALF_EVEN))));

		saveAll();

		//ageSpinner;
		//sexSpinner;
		//weightSpinner;
		//highSpinner;
		//activitySpinner

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void setSpinnerValues() {
		try{
			ageSpinner.setText("" + (SaveUtils.getAge(this) + MIN_AGE));
			weightSelector.setText("" + (SaveUtils.getRealWeight(this)));
			highSpinner.setText("" + (SaveUtils.getHeight(this) + MIN_HEIGHT));

			ArrayList<DishType> genders = new ArrayList<DishType>();
			genders.add(new DishType( 166, getString(R.string.male)));
			genders.add(new DishType( 0, getString(R.string.female)));		
			ArrayAdapter<DishType>  adapter = new ArrayAdapter<DishType>(this, android.R.layout.simple_spinner_item, genders);		
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}catch (Exception e) {
			e.printStackTrace();
			SaveUtils.saveAge(0, Info.this);
			SaveUtils.saveActivity(0, Info.this);
			SaveUtils.saveHeight(0, Info.this);
			SaveUtils.saveSex(0, Info.this);
			SaveUtils.saveWeight(0, Info.this);
		}
			
		}	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setSpinnerValues();
		try {
			BMI = Double.parseDouble(SaveUtils.getBMI(this));
			String addText = "";
			if (BMI < 18.5) {
				addText = "(" + getString(R.string.underweight) + ")";

			} else if (BMI < 24.9) {
				addText = "(" + getString(R.string.normal_weight) + ")";

			} else if (BMI < 29.9) {
				addText = "(" + getString(R.string.overweight) + ")";

			} else {
				addText = "(" + getString(R.string.obese) + ")";

			}
			BMItextView.setText(BMI + addText);
			BMRtextView.setText(SaveUtils.getBMR(this) + "("
					+ getString(R.string.kcal_day) + ")");
			MetatextView.setText(SaveUtils.getMETA(this) + "("
					+ getString(R.string.kcal_day) + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private OnItemSelectedListener spinnerModeListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			SaveUtils.saveMode(arg2, Info.this);
			//DialogUtils.showDialog(Info.this.getParent(), getString(R.string.save_prove));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	};
	
	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener(){

		

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			onValuesUpdated();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}		
	};


	public void saveAll() {				
		
		try{
			
			SaveUtils.saveBMI(String.valueOf(BMI), Info.this);
			SaveUtils.saveBMR(String.valueOf((int)(META*0.8)), Info.this);
			SaveUtils.saveMETA(String.valueOf(META), Info.this);
			//update social profile
			if(SaveUtils.getUserUnicId(this) != 0){
				new SocialUpdater(this).execute();
			}
		/*	BMI = Double.parseDouble(SaveUtils.getBMI(Info.this));
			if(BMI < 18.5 ){
				SaveUtils.saveMode(2,Info.this);		
			} else
			if(BMI < 24.9 ){
				SaveUtils.saveMode(1,Info.this);
			} else
			if(BMI < 29.9 ){
				SaveUtils.saveMode(0,Info.this);
			} else {
				SaveUtils.saveMode(0,Info.this);
			}
			*/
			SaveUtils.setActivated(Info.this, true);
		//	DialogUtils.showDialog(Info.this.getParent(), getString(R.string.save_prove));
		}catch (Exception e) {
			e.printStackTrace();
		}
}

}
