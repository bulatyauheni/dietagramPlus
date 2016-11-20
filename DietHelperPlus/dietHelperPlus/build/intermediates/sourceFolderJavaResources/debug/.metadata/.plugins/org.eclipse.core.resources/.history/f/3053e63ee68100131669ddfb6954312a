package bulat.diet.helper_plus.activity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.Day;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class WeightChartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(
				R.layout.statistics_weight, null);
		setContentView(viewToLoad);	
		
		///
		Button backButton = (Button) viewToLoad.findViewById(R.id.buttonBack);				
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// init example series data  
		// graph with dynamically genereated horizontal and vertical labels
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
			TextView tv = (TextView) findViewById(R.id.textViewEmpty);  
			tv.setVisibility(View.GONE);
			String[] hor = new String[days.size()];
			int j=0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM",new Locale(getString(R.string.locale)));
			for (Day day : days) {	
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
			graphView.setHorizontalLabels(hor);
			graphView.setScalable(false);
			graphView.setVerticalLabels(null);
			graphView.addSeries(exampleSeries); // data
		      
		    LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutChart);  
		    layout.addView(graphView); 
		}else{
			TextView tv = (TextView) findViewById(R.id.textViewEmpty);  
			tv.setVisibility(View.VISIBLE);
		}
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
/*		LinearLayout linear=(LinearLayout) findViewById(R.id.linearLayoutChart);
        values=calculateData(values);
        linear.addView(new MyGraphview(this,values));
        
        LinearLayout linear2=(LinearLayout) findViewById(R.id.linearLayoutChart2);
        values2 = getValues(TodayDishHelper.getStatistic(this));
        values2=calculateData(values2);
        linear2.removeAllViews();
        linear2.addView(new MyGraphview(this,values2));*/
	}

}
