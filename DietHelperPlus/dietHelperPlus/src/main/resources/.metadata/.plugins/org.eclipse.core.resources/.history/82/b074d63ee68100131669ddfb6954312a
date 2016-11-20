package bulat.diet.helper_plus.activity;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.db.TodayDishHelper;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.utils.SaveUtils;

public class StatisticFCPActivity extends Activity {

	
	private static final String Fat = "f";
	private static final String Carbon = "c";
	private static final String Protein = "p";
	//белков, жиров и углеводов должно составлять 1:1:4, 
	//при больших физических нагрузках - 1:1:5, 
	//для работников умственного труда - 1:0.8:3. 
	float valuesNormal[]={1,4,1};
	float valuesFith[]={1,5,1};
	float valuesBrain[]={1,3,(float) 0.8};
	float values2[]={0,0,0};
	
	private Spinner spinnerDiet;
	private int lifestyle=0;
	private float[] values;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View viewToLoad = LayoutInflater.from(SettingsActivity.ctx).inflate(
				R.layout.statistics_fcp, null);
		setContentView(viewToLoad);	
		spinnerDiet = (Spinner) viewToLoad.findViewById(R.id.diet_type_spinner);
		ArrayList<DishType> time = new ArrayList<DishType>();
		time.add(new DishType( 0, getString(R.string.balance_norm)));
		time.add(new DishType( 1, getString(R.string.balance_fith)));
		time.add(new DishType( 2, getString(R.string.balance_clever)));
		ArrayAdapter<DishType>  adapter = new ArrayAdapter<DishType>(SettingsActivity.ctx, android.R.layout.simple_spinner_item, time);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDiet.setAdapter(adapter);	
		lifestyle =  SaveUtils.getLifeStyle(this);
		spinnerDiet.setSelection(lifestyle);
		spinnerDiet.setOnItemSelectedListener(spinnerListener);
		Button backButton = (Button) viewToLoad.findViewById(R.id.buttonBack);				
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		}

		private float[] getValues(Map<String, Float> statistic) {
		float res[]={0,0,0};	
		if(statistic == null){
			return res;
		}
		try{
			if(statistic.get(Fat) != null){		
				res[0]=statistic.get(Protein);
			}
			if(statistic.get(Carbon) != null){
				res[1]=statistic.get(Carbon);
			}
			if(statistic.get(Protein) != null){
				res[2]=statistic.get(Fat);
			}			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
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
		lifestyle = SaveUtils.getLifeStyle(this);
		LinearLayout linear=(LinearLayout) findViewById(R.id.linearLayoutChart);
		switch (lifestyle) {
		case 0:
			values=calculateData(valuesNormal);
			break;
		case 1:
			values=calculateData(valuesFith);
			break;
		case 2:
			values=calculateData(valuesBrain);
			break;
		default:
			values=calculateData(valuesNormal);
			break;
		}
		linear.removeAllViews();
        linear.addView(new MyGraphview(this,values));
        
        LinearLayout linear2=(LinearLayout) findViewById(R.id.linearLayoutChart2);
        values2 = getValues(TodayDishHelper.getStatisticFCP(this));
        values2=calculateData(values2);
        linear2.removeAllViews();
        linear2.addView(new MyGraphview(this,values2));
	}

	private float[] calculateData(float[] data) {
        // TODO Auto-generated method stub
        float total=0;
        for(int i=0;i<data.length;i++)
        {
            total+=data[i];
        }
        for(int i=0;i<data.length;i++)
        {
        data[i]=360*(data[i]/total);            
        }
        return data;

    }
    public class MyGraphview extends View
    {
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;
        private int[] COLORS={Color.BLUE,Color.GREEN,Color.RED};
        RectF rectf = new RectF (getDIP(5), getDIP(5), getDIP(155), getDIP(155));
        int temp=-90;
        
        public MyGraphview(Context context, float[] values) {

            super(context);
            value_degree=new float[values.length];
            for(int i=0;i<values.length;i++)
            {
                value_degree[i]=values[i];
            }
        }
        
        private float getDIP(int i) {
        	return getResources().getDisplayMetrics().density * i;
		}
		@Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            temp = -90;
            for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, -90, value_degree[i], true, paint);
                } 
                else
                {
                        temp += (int) value_degree[i - 1];
                        paint.setColor(COLORS[i]);
                        canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        }

    }
    
    public class LinevChariew extends View
    {
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
      //  private float[] value_degree;
      //  private int[] COLORS={Color.CYAN,Color.LTGRAY,Color.YELLOW,Color.RED,Color.GREEN};
      //  RectF rectf = new RectF (getDIP(5), getDIP(5), getDIP(155), getDIP(155));
      //  int temp=-90;
        
        public LinevChariew(Context context, float[] values) {

            super(context);
        //    value_degree=new float[values.length];
            for(int i=0;i<values.length;i++)
            {
        //        value_degree[i]=values[i];
            }
        }
        
        private float getDIP(int i) {
        	return getResources().getDisplayMetrics().density * i;
		}
		@Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            
            ArrayList<Integer> myArrayListOfValues = new ArrayList<Integer>();
            myArrayListOfValues.add(120 - 19);
            myArrayListOfValues.add(120 - 29);
            myArrayListOfValues.add(120 - 19);
            myArrayListOfValues.add(120 - 39);
            myArrayListOfValues.add(120 - 92);
            int step = 0;
           // canvas.translate(0,canvas.getHeight());   // reset where 0,0 is located
           
            
			for(int i=1; i<myArrayListOfValues.size(); i++){
				step = step + 10;
                Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                myPaint.setStrokeWidth(8/*1 /getResources().getDisplayMetrics().density*/);
                myPaint.setColor(0xffff0000);   //color.RED

                canvas.drawLine(step, myArrayListOfValues.get(i - 1), step + 10, myArrayListOfValues.get(i), myPaint);       

            }
			canvas.save();
			canvas.translate(0,canvas.getHeight());   // reset where 0,0 is located
            canvas.scale(1,-1); 
            canvas.restore();
        }

    }

    private OnItemSelectedListener spinnerListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {			
			
			SaveUtils.saveLifeStyle((int)(
					((DishType)spinnerDiet.getSelectedItem()).getTypeKey()), StatisticFCPActivity.this);
			onResume();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}		
	};
}
