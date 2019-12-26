package bulat.diet.helper_plus.adapter;

import java.text.DecimalFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.activity.DishListActivityGroup;
import bulat.diet.helper_plus.activity.SportListActivity;
import bulat.diet.helper_plus.db.DishProvider;
import bulat.diet.helper_plus.utils.SaveUtils;

public class SportAdapter extends CursorAdapter {

	private Context context;
	private DishListActivityGroup parent = null;
	SportListActivity page;
	protected boolean remove;
	int weight = 0;
	public SportAdapter(SportListActivity sportListActivity, Context context, Cursor c, DishListActivityGroup parent) {
		super(context, c);
		this.page = sportListActivity;
		this.context = context;
		this.parent = parent;
		weight = (int)SaveUtils.getRealWeight(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.fitnes_list_row, parent, false);
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
		String itemName = c.getString(c.getColumnIndex(DishProvider.NAME));
		String itemId = c.getString(c.getColumnIndex("_id"));
		String type = c.getString(c.getColumnIndex(DishProvider.TYPE));

		TextView nameTextView = (TextView) v
				.findViewById(R.id.textViewDishName);

		nameTextView.setText(itemName);

		DecimalFormat df = new DecimalFormat("###.#");

		TextView textViewСonsumption = (TextView) v
				.findViewById(R.id.textViewСonsumption);
		textViewСonsumption.setText(df.format(c.getFloat(c
				.getColumnIndex(DishProvider.PROTEIN))*60*weight));
	
		TextView typeView = (TextView) v
				.findViewById(R.id.textViewDishType);
		typeView.setText(type);

		TextView idView = (TextView) v.findViewById(R.id.textViewId);

		idView.setText(itemId);

		if (parent != null) {

			Button remButton = (Button) v.findViewById(R.id.buttonDell);

			if (remButton != null) {

				remButton.setVisibility(View.VISIBLE);
				remButton.setId(Integer.parseInt(itemId));
				remButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Button rbut = (Button) v;
						TextView tvi = (TextView) ((View) rbut.getParent())
									.findViewById(R.id.textViewId);										
						page.showingDialogCancel(tvi.getText().toString());
					}
				});
				remButton.setId(Integer.parseInt(itemId));
			}
		}
	}

	

}
