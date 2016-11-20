package bulat.diet.helper_plus.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.db.DishProvider;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.utils.SaveUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class StatisticExportActivity extends BaseExportActivity {
	Context ctx = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.statistics_export, null);
		setContentView(viewToLoad);
		ctx = this;
		// /
		filename = (TextView) viewToLoad.findViewById(R.id.textViewExportData);
		Button backButton = (Button) viewToLoad.findViewById(R.id.buttonBack);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		Button saveButton = (Button) viewToLoad.findViewById(R.id.buttonSave);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new ExportHistoryCSVTask(StatisticImportActivity.STATISTIC_FILE_PATTERN).execute("");
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (SaveUtils.getLastExportedHistoryFileName(StatisticExportActivity.this)
				.length() > 1) {
			filename.setText(getString(R.string.saved_history)
					+ " "
					+ SaveUtils
							.getLastExportedHistoryFileName(StatisticExportActivity.this));
		}
	}

	
}
