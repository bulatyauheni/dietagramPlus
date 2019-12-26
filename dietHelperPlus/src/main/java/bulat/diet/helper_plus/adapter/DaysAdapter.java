package bulat.diet.helper_plus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.activity.CalendarActivity;
import bulat.diet.helper_plus.activity.CalendarActivityGroup;
import bulat.diet.helper_plus.activity.Info;
import bulat.diet.helper_plus.db.DishProvider;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.utils.DialogUtils;
import bulat.diet.helper_plus.utils.SaveUtils;
import bulat.diet.helper_plus.utils.SocialUpdater;


public class DaysAdapter extends CursorAdapter {
	DecimalFormat df = new DecimalFormat("###.#");
	private Context ctx;
	private CalendarActivityGroup parent;
	CalendarActivity page;
	public DaysAdapter(CalendarActivity page, Context context, Cursor c,
			CalendarActivityGroup calendarActivityGroup) {
		super(context, c);
		this.page = page;
		ctx = context;
		this.parent = calendarActivityGroup;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.days_list_row, parent, false);
		return v;
		// return inflater.inflate(R.layout.item_list_row, null);
	}

	@Override
	public int getCount() {
		if (super.getCount() != 0) {
			return super.getCount();
		}
		return 0;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		String itemName = c.getString(c
				.getColumnIndex(DishProvider.TODAY_DISH_DATE));
		String itemCaloricity = c.getString(c.getColumnIndex("val"));
		String itemWoterWeight = c.getString(c.getColumnIndex("woterweight"));
		String itemWeight = c.getString(c.getColumnIndex("weight"));
		String itemBodyWeight = "";
		if (Integer.parseInt(itemCaloricity) > 0) {

			try {
				if ((c.getInt(c.getColumnIndex("count")) - 1) > 0) {
					itemBodyWeight = String.valueOf(c.getFloat(c
							.getColumnIndex("bodyweight")));
					v.setBackgroundResource(R.color.main_color);
				} else {
					itemBodyWeight = String.valueOf(c.getFloat(c
							.getColumnIndex("bodyweight")));
					v.setBackgroundResource(R.color.main_color);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			itemBodyWeight = String.valueOf(SaveUtils.getWeight(context)
					+ Info.MIN_WEIGHT);
			//v.setBackgroundResource(R.color.light_broun);
		}
		TextView nameTextView = (TextView) v.findViewById(R.id.textViewDay);
		nameTextView.setText(itemName);

		ImageView iv = (ImageView) v.findViewById(R.id.imageViewDay);
		if (!checkLimit(Integer.parseInt(itemCaloricity))) {
			iv.setImageResource(R.drawable.fat_man);
		} else {
			iv.setImageResource(R.drawable.halth_man);
		}
		LinearLayout le = (LinearLayout) v.findViewById(R.id.layoutEmpty);
		LinearLayout lf = (LinearLayout) v.findViewById(R.id.layoutParams);
		if ("0".equals(itemWeight) && "0".equals(itemCaloricity)) {
			lf.setVisibility(View.GONE);
			le.setVisibility(View.VISIBLE);

		} else {
			lf.setVisibility(View.VISIBLE);
			le.setVisibility(View.GONE);

			TextView fatView = (TextView) v.findViewById(R.id.textViewFat);
			fatView.setText(String.valueOf(c.getString(c.getColumnIndex("fat"))));

			TextView carbonView = (TextView) v
					.findViewById(R.id.textViewCarbon);
			carbonView.setText(String.valueOf(c.getString(c.getColumnIndex("carbon"))));

			TextView proteinView = (TextView) v
					.findViewById(R.id.textViewProtein);
			proteinView.setText(String.valueOf(c.getString(c.getColumnIndex("protein"))));
			
			TextView tvFatPercent = (TextView) v
					.findViewById(R.id.textViewFatPercent);
			TextView tvCarbPercent = (TextView) v
					.findViewById(R.id.textViewCarbonPercent);
			TextView tvProtPercent = (TextView) v
					.findViewById(R.id.textViewProteinPercent);
			
			float sum = Float.valueOf(c.getString(c.getColumnIndex("protein"))==null?"0":c.getString(c.getColumnIndex("protein"))) + 
					Float.valueOf(c.getString(c.getColumnIndex("carbon"))==null?"0":c.getString(c.getColumnIndex("carbon")))+
					Float.valueOf(c.getString(c.getColumnIndex("fat"))==null?"0":c.getString(c.getColumnIndex("fat")));
			
			tvFatPercent.setText("("+df.format(Float.valueOf(c.getString(c.getColumnIndex("fat"))==null?"0":c.getString(c.getColumnIndex("fat")))*100/sum)+"%)");
			tvCarbPercent.setText("("+df.format(Float.valueOf(c.getString(c.getColumnIndex("carbon"))==null?"0":c.getString(c.getColumnIndex("carbon")))*100/sum)+"%)");
			tvProtPercent.setText("("+df.format(Float.valueOf(c.getString(c.getColumnIndex("protein"))==null?"0":c.getString(c.getColumnIndex("protein")))*100/sum)+"%)");
						
			
			TextView waterweightView = (TextView)v.findViewById(R.id.textViewWoter);
		      waterweightView.setText(itemWoterWeight + " " + context.getString(R.string.gram));
			TextView caloricityView = (TextView) v
					.findViewById(R.id.textViewCaloricity);

			caloricityView.setText(itemCaloricity + " "
					+ context.getString(R.string.kcal));
			TextView weightView = (TextView) v
					.findViewById(R.id.textViewWeightTotal);

			weightView.setText(itemWeight == null ? "0" : itemWeight + " "
					+ context.getString(R.string.gram));

			TextView bodyweightView = (TextView) v
					.findViewById(R.id.textViewWeightBodyTotal);

			float currWeight = SaveUtils.getRealWeight(ctx);

			if (itemBodyWeight == null) {
				itemBodyWeight = "" + currWeight;
			}
			bodyweightView.setText(itemBodyWeight + " "
					+ context.getString(R.string.kgram));
		}

		Button weightButton = (Button) v.findViewById(R.id.buttonWeight);
		if (weightButton != null) {
			weightButton.setId(c.getInt(c.getColumnIndex("_id")));
		}
		TextView tvi = (TextView) v.findViewById(R.id.textViewId);
		tvi.setText(c.getString(c
				.getColumnIndex(DishProvider.TODAY_DISH_DATE_LONG)));
		if (weightButton != null) {

			// weightButton.setId(Integer.parseInt(c.getString(c.getColumnIndex(DishProvider.TODAY_DISH_DATE_LONG))));
			weightButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					Button mbut = (Button) v;
					final TextView tvi2 = (TextView) ((View) mbut.getParent())
							.findViewById(R.id.textViewId);
					final Dialog dialog = new Dialog(parent);
					dialog.setContentView(R.layout.update_weight_dialog);
					dialog.setTitle(R.string.change_weight_dialog_title);

					final Spinner weightSpinner = (Spinner) dialog
							.findViewById(R.id.search_weight);
					final Spinner weightSpinnerDec = (Spinner) dialog
							.findViewById(R.id.search_weight_decimal);
					DialogUtils.setArraySpinnerValues(weightSpinner,
							Info.MIN_WEIGHT, Info.MAX_WEIGHT, ctx);
					DialogUtils.setArraySpinnerValues(weightSpinnerDec, 0, 10,
							ctx);
					weightSpinner.setSelection(SaveUtils.getWeight(ctx));
					weightSpinnerDec.setSelection(SaveUtils.getWeightDec(ctx));
					Button buttonOk = (Button) dialog
							.findViewById(R.id.buttonYes);
					buttonOk.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {

							SimpleDateFormat sdf = new SimpleDateFormat(
									"EEE dd MMMM", new Locale(ctx
											.getString(R.string.locale)));
							String lastDate = TodayDishHelper.getLastDate(ctx);
							Date curentDateandTime = new Date();
							if (lastDate != null) {
								try {
									curentDateandTime = sdf.parse(lastDate);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Date nowDate = new Date();
								curentDateandTime.setYear(nowDate.getYear());
								if (tvi2.getText()
										.toString()
										.equals(String
												.valueOf(curentDateandTime
														.getTime()))) {
									SaveUtils.saveWeight((int) weightSpinner
											.getSelectedItemId(), ctx);
									SaveUtils.saveWeightDec(
											(int) weightSpinnerDec
													.getSelectedItemId(), ctx);
									if (SaveUtils.getUserUnicId(ctx) != 0) {
										new SocialUpdater(ctx).execute();
									}
								}
								TodayDishHelper.updateDayWeight(
										ctx,
										tvi2.getText().toString(),
										String.valueOf(((float) weightSpinner
												.getSelectedItemId() + Info.MIN_WEIGHT)
												+ (float) weightSpinnerDec
														.getSelectedItemId()
												/ 10));
								dialog.cancel();
								page.resume();
							}
						}
					});
					Button nobutton = (Button) dialog
							.findViewById(R.id.buttonNo);
					nobutton.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							dialog.cancel();
						}
					});
					dialog.show();
					
				}
			});
		}

	}

	public boolean checkLimit(int sum) {
		int mode = SaveUtils.getMode(ctx);
		try {
			switch (mode) {
			case 0:
				if (sum > Integer.parseInt(SaveUtils.getBMR(ctx))) {
					return false;
				} else {
					return true;
				}
			case 1:
				if (sum > Integer.parseInt(SaveUtils.getMETA(ctx))) {
					return false;
				} else {
					return true;
				}
			case 2:
				if (sum < Integer.parseInt(SaveUtils.getMETA(ctx))) {
					return false;
				} else {
					return true;
				}
			default:
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
