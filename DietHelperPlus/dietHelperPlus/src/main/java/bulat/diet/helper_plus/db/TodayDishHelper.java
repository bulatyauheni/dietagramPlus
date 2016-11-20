package bulat.diet.helper_plus.db;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateUtils;
import bulat.diet.helper_plus.db.DishProvider.DatabaseHelper;
import bulat.diet.helper_plus.item.Day;
import bulat.diet.helper_plus.item.TodayDish;
import bulat.diet.helper_plus.utils.SaveUtils;


public class TodayDishHelper {
	
	public static String getLastDateLong(Context context) {
		ContentResolver cr = context.getContentResolver();		
		 String[] columns = new String[] { DishProvider.TODAY_DISH_DATE, DishProvider.TODAY_DISH_DATE_LONG  };
		 Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, null, null, DishProvider.TODAY_DISH_DATE_LONG +" DESC");
		 if (c!=null){
				try {
					
					while (c.moveToNext())
			        {	
						return c.getString(1);	
			        }
				}catch (Exception e) {
				}finally{
					c.close();
				}
		 }
		return null;
		
	
	}
	
	public static Cursor getAllDishes(Context context, String date) {
		ContentResolver cr = context.getContentResolver();
		//String selection =  DishProvider.TODAY_IS_DAY + " <> " + "1 and "  + DishProvider.TODAY_DISH_DATE_LONG + " > " + "?";
		String[] val = new String[] { date };
		Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, null, null, null, null);
		return c;
	}
	
	public static Map<String, Integer> getStatistic(Context context) {		
		ContentResolver cr = context.getContentResolver();
		String selection = DishProvider.TODAY_DISH_DATE_LONG + ">" + "? and " + DishProvider.TODAY_IS_DAY + "<> 1 and " + DishProvider.TODAY_CALORICITY + "> 1" + ") GROUP BY (" + DishProvider.TODAY_TYPE;
		 String[] columns = new String[] { DishProvider.TODAY_TYPE, " sum("+DishProvider.TODAY_DISH_WEIGHT +") as weight"};
		 Long date = ((new Date()).getTime() - 30*DateUtils.DAY_IN_MILLIS) ;
		 String[] val = new String[] {date.toString()};
		 Cursor c  = null;
		 try {
			 c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val, DishProvider.TODAY_DISH_DATE_LONG + " DESC");

			Map<String, Integer> result = new TreeMap<String, Integer>();
			if (c!=null){
					
					while (c.moveToNext())
			        {	
						int temp = c.getInt(1);
						try{
							if(c.getString(0)!=null){
								result.put(c.getString(0), temp);
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
			        }
					return result;
			}
		} catch (Exception e) {
				e.printStackTrace();
		}finally{
			if(c!= null){
				c.close();
			}
		}
		return null;		
}
	public static Map<String, Float> getStatisticFCP(Context context) {		
		ContentResolver cr = context.getContentResolver();
		String selection = DishProvider.TODAY_DISH_DATE_LONG + ">" + "? and " + DishProvider.TODAY_IS_DAY + "<> 1 and " + DishProvider.TODAY_TYPE + "<>''";
		 String[] columns = new String[] { DishProvider.TODAY_TYPE, " sum("+DishProvider.TODAY_DISH_FAT +") as fat"," sum("+DishProvider.TODAY_DISH_PROTEIN +") as protein"," sum("+DishProvider.TODAY_DISH_CARBON +") as carbon"};
		 Long date = ((new Date()).getTime() - 30*DateUtils.DAY_IN_MILLIS) ;
		 String[] val = new String[] {date.toString()};
		 Cursor c  = null;
		 try {
			 c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val, DishProvider.TODAY_DISH_DATE_LONG + " DESC");

			Map<String, Float> result = new TreeMap<String, Float>();
			if (c!=null){
					
					while (c.moveToNext())
			        {	
						int temp = c.getInt(1);
						try{
							if(c.getString(0)!=null){
								result.put("f", c.getFloat(1));
								result.put("p", c.getFloat(2));
								result.put("c", c.getFloat(3));
							}
							
							return result;
						}catch (Exception e) {
							e.printStackTrace();
						}
			        }
					return result;
			}
		} catch (Exception e) {
				e.printStackTrace();
		}finally{
			if(c!= null){
				c.close();
			}
		}
		return null;		
}	
public static TodayDish getDishById(String id, Context context) {
		
		ContentResolver cr = context.getContentResolver();
		String selection = "_id" + "=" + "?" ;
		String[] columns = new String[] { DishProvider.TODAY_NAME, DishProvider.TODAY_CALORICITY, 
				DishProvider.TODAY_DISH_WEIGHT, "_id", DishProvider.TODAY_DISH_DATE_LONG, DishProvider.TYPE };
		String[] val = new String[] { id };
		Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val,null);
		TodayDish res = new TodayDish();
		if (c!=null){
			try {
				
				while (c.moveToNext())
		        {					
					res.setName(c.getString(0));
					res.setCaloricity(c.getInt(1));
					res.setWeight(c.getInt(2));
					res.setId(c.getString(3));
					res.setDateTime(4);
					res.setType(c.getString(5));
					return res;
		        }
				return null;
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				c.close();
			}
		}
		return null;	
	}
	
public static float getBodyWeightByDate(long date, Context context) {
	
	ContentResolver cr = context.getContentResolver();
	String selection = DishProvider.TODAY_DISH_DATE_LONG + "=" + "?" ;
	String[] columns = new String[] {DishProvider.TODAY_DISH_ID};
	String[] val = new String[] { String.valueOf(date) };
	Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val,null);
	TodayDish res = new TodayDish();
	if (c!=null){
		try {
			
			while (c.moveToNext())
	        {									
				if(c.getFloat(0)>0){
					return c.getFloat(0);
				}
	        }
			return SaveUtils.getRealWeight(context);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
	}
	return SaveUtils.getRealWeight(context);
}
	public static String addNewDish(TodayDish dish, Context context) {
		
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();

			//values.put(DishProvider.DISH_ID, couponId);

			values.put(DishProvider.TODAY_DISH_ID, dish.getBodyweight());
			values.put(DishProvider.TODAY_NAME, dish.getName());
			values.put(DishProvider.TODAY_DESCRIPTION, dish.getDescription());
			values.put(DishProvider.TODAY_CALORICITY, dish.getCaloricity());
			values.put(DishProvider.TODAY_DISH_CALORICITY, dish.getAbsolutCaloricity());
			values.put(DishProvider.TODAY_CATEGORY, dish.getCategory());
			values.put(DishProvider.TODAY_DISH_DATE, dish.getDate());
			values.put(DishProvider.TODAY_DISH_WEIGHT, dish.getWeight());
			values.put(DishProvider.TODAY_DISH_DATE_LONG, dish.getDateTime());
			values.put(DishProvider.TODAY_IS_DAY, dish.getIsdate());
			values.put(DishProvider.TODAY_TYPE, dish.getType());
			values.put(DishProvider.TODAY_FAT, dish.getFat());
			values.put(DishProvider.TODAY_CARBON, dish.getCarbon());
			values.put(DishProvider.TODAY_PROTEIN, dish.getProtein());
			values.put(DishProvider.TODAY_DISH_FAT, dish.getAbsFat());
			values.put(DishProvider.TODAY_DISH_CARBON, dish.getAbsCarbon());
			values.put(DishProvider.TODAY_DISH_PROTEIN, dish.getAbsProtein());
			values.put(DishProvider.TODAY_DISH_TIME_HH, dish.getDateTimeHH());
			values.put(DishProvider.TODAY_DISH_TIME_MM, dish.getDateTimeMM());
			long id = ContentUris.parseId(cr.insert(DishProvider.TODAYDISH_CONTENT_URI, values));
			String f = String.valueOf(id);
			return f;		
	}
	
	public static boolean updateDish(TodayDish dish, Context context) {
		
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();

		//values.put(DishProvider.DISH_ID, couponId);
		values.put(DishProvider.TODAY_NAME, dish.getName());
		values.put(DishProvider.TODAY_DESCRIPTION, dish.getDescription());
		values.put(DishProvider.TODAY_CALORICITY, dish.getCaloricity());
		values.put(DishProvider.TODAY_DISH_CALORICITY, dish.getAbsolutCaloricity());
		values.put(DishProvider.TODAY_FAT, dish.getFat());
		values.put(DishProvider.TODAY_CARBON, dish.getCarbon());
		values.put(DishProvider.TODAY_PROTEIN, dish.getProtein());
		values.put(DishProvider.TODAY_DISH_FAT, dish.getAbsFat());
		values.put(DishProvider.TODAY_DISH_CARBON, dish.getAbsCarbon());
		values.put(DishProvider.TODAY_DISH_PROTEIN, dish.getAbsProtein());
		values.put(DishProvider.TODAY_CATEGORY, dish.getCategory());
		values.put(DishProvider.TODAY_DISH_WEIGHT, dish.getWeight());
		values.put(DishProvider.TODAY_DISH_TIME_HH, dish.getDateTimeHH());
		values.put(DishProvider.TODAY_DISH_TIME_MM, dish.getDateTimeMM());
		String where = "_id" + " = " + String.valueOf(dish.getId());
		cr.update(DishProvider.TODAYDISH_CONTENT_URI, values, where, null);
		return true;		
	}
	
	public static boolean updateDayWeight( Context context, String date_long, String weight) {
		
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();

		//values.put(DishProvider.DISH_ID, couponId);
		values.put(DishProvider.TODAY_DISH_ID, weight);		
		String where = DishProvider.TODAY_DISH_DATE_LONG + " = " + date_long;
		cr.update(DishProvider.TODAYDISH_CONTENT_URI, values, where, null);
		return true;		
	}

	public static boolean removeDish(String id, Context context) {
		
		ContentResolver cr = context.getContentResolver();
		
		String where = "_id" + " = " + String.valueOf(id );
		//String[] val = new String[] { String.valueOf(id )};
		
		cr.delete(DishProvider.TODAYDISH_CONTENT_URI, where, null);
		return true;		
	}
	public static boolean removeDishesByDay(String day, Context context) {
		
		ContentResolver cr = context.getContentResolver();
		
		String where = DishProvider.TODAY_DISH_DATE + " = " + "'" + String.valueOf(day) + "'" ;
		//String[] val = new String[] { String.valueOf(id )};
		
		cr.delete(DishProvider.TODAYDISH_CONTENT_URI, where, null);
		return true;		
	}

	
	public static Cursor getDishesByDate(Context context, String date) {
		ContentResolver cr = context.getContentResolver();
		String selection =  DishProvider.TODAY_IS_DAY + " <> " + "1 and "  + DishProvider.TODAY_DISH_DATE + " = " + "?";
		String[] val = new String[] { date };
		Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, null, selection, val, DishProvider.TODAY_DESCRIPTION);
		return c;
	}

	public static int getAllCouponsCount(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor c = cr
				.query(DishProvider.TODAYDISH_CONTENT_URI, null, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}
	//0-caloricity 1-fat 2-carbon 3-protein
	public static String[] getAvgDishCalorisity(Context context) {		
		Cursor c = getDaysNew(context);
		int sumCal = 0;
		float sumFat = 0;
		float sumCarb = 0;
		float sumProt = 0;
		int daysCount = 0;
		
		if (c!=null){
			try {
				
				while (c.moveToNext())
		        {	
					int tempCal = c.getInt(c.getColumnIndex("val"));	
					float tempFat = c.getFloat(c.getColumnIndex("fat"));	
					float tempCarb = c.getFloat(c.getColumnIndex("carbon"));	
					float tempProt = c.getFloat(c.getColumnIndex("protein"));	
					if(tempCal > 0){
						sumCal = sumCal + tempCal;
						sumFat = sumFat + tempFat;
						sumCarb = sumCarb + tempCarb;
						sumProt = sumProt + tempProt;
						daysCount++;
					}
		        }
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				c.close();
			}
	 }
		if(daysCount == 0){
			daysCount++;
		}
		DecimalFormat df = new DecimalFormat("###.#");

		return new String[]{String.valueOf(sumCal/daysCount),df.format(sumFat/daysCount),df.format(sumCarb/daysCount),df.format(sumProt/daysCount)};
	
	}
	
	public static Cursor getDays(Context context) {
		
		//'SELECT date, sum(value) as value  FROM dishs GROUP BY date ORDER BY id';
		
		ContentResolver cr = context.getContentResolver();
		//need to change DishProvider.TODAY_DISH_DATE to DishProvider.TODAY_DISH_DATE_LONG in other version
		String selection = DishProvider.TODAY_TYPE + "<> '' and " + DishProvider.TODAY_IS_DAY + "<>" + "? ) GROUP BY (" + DishProvider.TODAY_DISH_DATE;
		String[] columns = new String[] { DishProvider.TODAY_DISH_DATE, "sum("+DishProvider.TODAY_DISH_FAT +") as fat, sum("+DishProvider.TODAY_DISH_CARBON +") as carbon, sum("+DishProvider.TODAY_DISH_PROTEIN +") as protein, sum("+DishProvider.TODAY_DISH_CALORICITY +") as val, sum("+DishProvider.TODAY_DISH_WEIGHT +") as weight, _id, "+DishProvider.TODAY_DISH_ID+" as bodyweight, count(*) as count" , DishProvider.TODAY_DISH_DATE_LONG};
		String[] val = new String[] { "test" };
		Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val, DishProvider.TODAY_DISH_DATE_LONG + " DESC");
		
		return c;
	}
	static SQLiteDatabase allDishesDB = null;
public static Cursor getDaysNew(Context context) {
		
		//'SELECT date, sum(value) as value  FROM dishs GROUP BY date ORDER BY id';
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		if(allDishesDB == null){
			allDishesDB = dbHelper.getWritableDatabase();
		}
		
		ContentResolver cr = context.getContentResolver();
		//need to change DishProvider.TODAY_DISH_DATE to DishProvider.TODAY_DISH_DATE_LONG in other version
				
		Cursor c =  allDishesDB.rawQuery(
		"Select "+ DishProvider.TODAY_DISH_DATE + " , sum("+DishProvider.TODAY_DISH_CALORICITY +") as val, sum(fatw) as fat, sum(carbw) as carbon, sum(protw) as protein, "+
        " sum(clearweight) as weight, sum(waterweight) as woterweight, _id, "+DishProvider.TODAY_DISH_ID+" as bodyweight, count(_id) as count ,"+ DishProvider.TODAY_DISH_DATE_LONG +
        " from day_dishes as a " +
        "left JOIN (SELECT _id as _id2, "+DishProvider.TODAY_DISH_WEIGHT+" as clearweight , "+DishProvider.TODAY_DISH_FAT +" as fatw, "+DishProvider.TODAY_DISH_CARBON +" as carbw, "+DishProvider.TODAY_DISH_PROTEIN +" as protw" +
        		" FROM    day_dishes"+
        		" WHERE " + DishProvider.TODAY_CATEGORY + " <> 0 and "+ DishProvider.TODAY_CALORICITY + " <> 0) as b ON a._id=b._id2 " +
       "left JOIN (SELECT _id as _id3, "+DishProvider.TODAY_DISH_WEIGHT+" as waterweight "+
   				"FROM  day_dishes " +
   				"WHERE " + DishProvider.TODAY_DISH_CALORICITY +" = 0 ) as c ON a._id=c._id3 "+
       "GROUP BY " + DishProvider.TODAY_DISH_DATE_LONG + " order by " +  DishProvider.TODAY_DISH_DATE_LONG + " DESC" , null);
		return c;
	}
	
	public static List<Day> getDaysStat(Context context) {
		
		//'SELECT date, sum(value) as value  FROM dishs GROUP BY date ORDER BY id';
		
		ContentResolver cr = context.getContentResolver();
		String selection = DishProvider.TODAY_IS_DAY + "<>" + "?" + ") GROUP BY (" + DishProvider.TODAY_DISH_DATE;
		 String[] columns = new String[] { DishProvider.TODAY_DISH_DATE, "sum("+DishProvider.TODAY_DISH_CALORICITY +") as val, sum("+DishProvider.TODAY_DISH_WEIGHT +") as weight, _id, "+DishProvider.TODAY_DISH_ID+" as bodyweight" , DishProvider.TODAY_DISH_DATE_LONG};
		 String[] val = new String[] { "1" };
		Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, selection, val, DishProvider.TODAY_DISH_DATE_LONG + " DESC LIMIT 30");
		ArrayList<Day> dayList = new ArrayList<Day>();
		Day d = new Day();
		try {
			
			while (c.moveToNext())
	        {
				d = new Day();
				d.setBodyWeight(Float.parseFloat(c.getString(4)));
				d.setDateInt(Long.parseLong(c.getString(5)));
				dayList.add(d);
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		return dayList;
	}
	
	public static void clearAll(Context context) {
		ContentResolver cr = context.getContentResolver();
		cr.delete(DishProvider.TODAYDISH_CONTENT_URI, null, null);
	}

	public static String getLastDate(Context context) {
		ContentResolver cr = context.getContentResolver();		
		 String[] columns = new String[] { DishProvider.TODAY_DISH_DATE, DishProvider.TODAY_DISH_DATE_LONG  };
		 Cursor c = cr.query(DishProvider.TODAYDISH_CONTENT_URI, columns, null, null, DishProvider.TODAY_DISH_DATE_LONG +" DESC");
		 if (c!=null){
				try {
					
					while (c.moveToNext())
			        {	
						return c.getString(0);	
			        }
				}catch (Exception e) {
				}finally{
					c.close();
				}
		 }
		return null;
		
	
	}

	public static void importDays(List<TodayDish> list, Context ctx) {
		clearAll(ctx);
		for (TodayDish todayDish : list) {
			addNewDish(todayDish, ctx);
		}
		
	}
	
	//for solving problem with 31 december
		public static boolean changeLastDate(Context ctx, String lastdate) {
			ContentResolver cr = ctx.getContentResolver();
			ContentValues values = new ContentValues();
			
			Date ld = new Date(Long.valueOf(lastdate));
			ld.setYear(ld.getYear()-1);	
			values.put(DishProvider.TODAY_DISH_DATE_LONG, ld.getTime());
			String where = DishProvider.TODAY_DISH_DATE_LONG + " = " + lastdate;
			cr.update(DishProvider.TODAYDISH_CONTENT_URI, values, where, null);
			return true;		
			
		}
}
