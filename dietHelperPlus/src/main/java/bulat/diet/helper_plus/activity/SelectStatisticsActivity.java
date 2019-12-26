package bulat.diet.helper_plus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.utils.Constants;

public class SelectStatisticsActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.statistics_list, null);
		setContentView(viewToLoad);	
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LinearLayout linear=(LinearLayout) findViewById(R.id.linearLayoutChart);
		final String[] items = new String[] { getString(R.string.statistic_food),getString(R.string.statistic_weight), getString(R.string.statistic_FCP),getString(R.string.statistic_export_history),getString(R.string.statistic_import_history),getString(R.string.statistic_export_base),getString(R.string.statistic_import_bases), getString(R.string.statistic_privacy_policy) };
		
		ListView listView = (ListView) findViewById(R.id.listViewStatistics);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	            this,android.R.layout.simple_list_item_single_choice, items){
			 @Override
		        public View getView(int position, View convertView,ViewGroup parent) {
		            View view =super.getView(position, convertView, parent);

		            TextView textView=(TextView) view.findViewById(android.R.id.text1);

		            /*YOUR CHOICE OF COLOR*/
		            textView.setTextColor(Color.DKGRAY);

		            return view;
		        }
	    };		 
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();	
				if(arg2==0){
					intent.setClass(getParent(), Statistics.class);
				}else if(arg2==1){
					intent.setClass(getParent(), WeightChartActivity.class);
				}else if(arg2==2){
					intent.setClass(getParent(), StatisticFCPActivity.class);
				}else if(arg2==3){
					intent.setClass(getParent(), StatisticExportActivity.class);
				}else if(arg2==4){
					intent.setClass(getParent(), StatisticImportActivity.class);
				}else if(arg2==5){
					intent.setClass(getParent(), DataBaseExportActivity.class);
				}else if(arg2==6){
					intent.setClass(getParent(), DataBaseImportActivity.class);
				}else if (arg2 == 7) {
					Uri webpage = Uri.parse(Constants.PRIVACI_POLICY);
					Intent intentWeb = new Intent(Intent.ACTION_VIEW, webpage);
					startActivity(intentWeb);
					return;
				}
				StatisticActivityGroup activityStack = (StatisticActivityGroup) getParent();
					activityStack.push("StatisticActivity", intent);
				
				
			}
		});
	
	
	}

	
}
