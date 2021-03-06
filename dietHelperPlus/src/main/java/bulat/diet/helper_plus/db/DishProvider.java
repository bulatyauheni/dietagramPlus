package bulat.diet.helper_plus.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class DishProvider extends ContentProvider{
	
	private static final String DISHLIST_URI_STRING = "content://bulat.diet.helper_plus.db.DishProvider/list_items";
	private static final String TODAYDISH_URI_STRING = "content://bulat.diet.helper_plus.db.DishProvider/day_items";
	private static final String MESSAGES_URI_STRING = "content://bulat.diet.helper_plus.db.DishProvider/message_table";
	private static final String TEMPLATE_URI_STRING = "content://bulat.diet.helper_plus.db.DishProvider/template_table";

	
	public static final Uri TODAYDISH_CONTENT_URI = Uri.parse(TODAYDISH_URI_STRING);	
	public static final Uri DISHLIST_CONTENT_URI = Uri.parse(DISHLIST_URI_STRING);	
	public static final Uri MESSAGES_CONTENT_URI = Uri.parse(MESSAGES_URI_STRING);	
	public static final Uri TEMPLATE_CONTENT_URI = Uri.parse(TEMPLATE_URI_STRING);	
	
	private static final String LOG_TAG = "DishProvider";	
	
	private static final String DATABASE_NAME = "dishes2.db";
	private static final int DATABASE_VERSION = 8;
	
	private static final String DAYLY_TEMPLATE_TABLE_NAME = "day_template";
	private static final String DAYLY_DISHES_TABLE_NAME = "day_dishes";
	private static final String DISHES_TABLE_NAME = "base";
	
	public static final String ISRECEPY = "recepy_1000";
	//all dish table
	public static final String DISH_ID = "dish_id";
	public static final String NAME = "name";
	public static final String SEARCHNAME = "searchname";
	public static final String CATEGORY_NAME = "category_name";
	public static final String DESCRIPTION = "description";
	public static final String CALORICITY = "caloricity";
	public static final String CATEGORY = "category";
	public static final String CARBON = "carbon";
	public static final String FAT = "fat";
	public static final String PROTEIN = "protein";
	public static final String ISCATEGORY = "iscategory";
	public static final String LOCALE = "locale";
	public static final String POPULARITY = "popular";
	public static final String TYPE = "type";
	public static final String BARCODE = "barcode";
	
	public static final int DISH_ID_COLUMN = 1;
	public static final int NAME_COLUMN = 2;
	public static final int DESCRIPTION_COLUMN = 3;
	public static final int CALORICITY_COLUMN = 4;
	public static final int CATEGORY_COLUMN = 5;
	public static final int IS_CATEGORY_COLUMN = 6;
	public static final int LOCALE_COLUMN = 7;
	public static final int POPULARITY_COLUMN = 8;
	public static final int TYPE_COLUMN = 9;
	
	//today dish table
	public static final String TODAY_DISH_ID = "t_dish_id";
	public static final String TODAY_NAME = "name";
	public static final String TODAY_DESCRIPTION = "t_description";
	public static final String TODAY_CALORICITY = "t_caloricity";
	public static final String TODAY_CATEGORY = "t_category";
	public static final String TODAY_DISH_WEIGHT = "t_weight";
	public static final String TODAY_DISH_CALORICITY = "t_absolutecaloricity";
	public static final String TODAY_DISH_DATE = "t_date";
	public static final String TODAY_DISH_FAT = "t_absfat";
	public static final String TODAY_DISH_CARBON = "t_abscarbon";
	public static final String TODAY_DISH_PROTEIN = "t_absprotein";
	public static final String TODAY_FAT = "t_fat";
	public static final String TODAY_CARBON = "t_carbon";
	public static final String TODAY_PROTEIN = "t_protein";
	public static final String TODAY_DISH_DATE_LONG = "t_datelong";
	public static final String TODAY_IS_DAY = "t_isday";
	public static final String TODAY_TYPE = "type";
	public static final String TODAY_DISH_TIME_HH = "t_hh";
	public static final String TODAY_DISH_TIME_MM = "t_mm";
	
	public static final int TODAY_DISH_ID_COLUMN = 1;
	public static final int TODAY_NAME_COLUMN = 2;
	public static final int TODAY_DESCRIPTION_COLUMN = 3;
	public static final int TODAY_CALORICITY_COLUMN = 4;
	public static final int TODAY_CATEGORY_COLUMN = 5;
	public static final int TODAY_DISH_WEIGHT_COLUMN = 6;
	public static final int TODAY_DISH_CALORICITY_COLUMN = 7;
	public static final int TODAY_IS_DAY_COLUMN = 8;
	public static final int TODAY_TYPE_COLUMN = 9;
	
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	private static final int DISHLIST = 300;
	private static final int TODAYDISHLIST = 400;
	private static final int MESSAGELIST = 500;
	private static final int TEMPLATELIST = 600;

	private static final UriMatcher uriMatcher;
	public static final String DATE = "m_date";
	public static final String USER_ID = "m_user_id";
	public static final String MESSAGES_TABLE_NAME = "message_table";
	public static final String DATA = "m_data";
	public static final String MESSAGE_TEXT = "m_mess_text";
	public static final String USER_NAME = "m_user_name";
	public static final String DATE_INT = "m_date_int";
	public static final String USER_ID_FROM = "m_user_id_from";
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//uriMatcher.addURI("bulat.diet.helper.db.DishProvider", "items",
		//		ALLROWS);
		//uriMatcher.addURI("bulat.diet.helper.db.DishProvider", "items/#",
		//		SINGLE_ROW);
	
		uriMatcher.addURI("bulat.diet.helper_plus.db.DishProvider", "list_items", DISHLIST);        
		uriMatcher.addURI("bulat.diet.helper_plus.db.DishProvider", "day_items", TODAYDISHLIST);		
		uriMatcher.addURI("bulat.diet.helper_plus.db.DishProvider", "message_table", MESSAGELIST);
		uriMatcher.addURI("bulat.diet.helper_plus.db.DishProvider", "template_table", TEMPLATELIST);
       

	}

	private DatabaseHelper dbHelper;
	private SQLiteDatabase allDishesDB;
	
	public static class DatabaseHelper extends SQLiteOpenHelper {
		private Context ctx = null;
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			ctx= context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.execSQL("CREATE TABLE " + DISHES_TABLE_NAME + " (_id INTEGER PRIMARY KEY," + DISH_ID + " INTEGER," + NAME
			//		+ " TEXT," + DESCRIPTION + " TEXT,"  + CALORICITY + " TEXT," + CATEGORY + " INTEGER," + ISCATEGORY + " INTEGER," + POPULARITY + " INTEGER," + TYPE + " TEXT" + ");");
			 
		//	db.execSQL(BaseLoader.getStringFromFile(R.raw.base_create, ctx));
			try{
			db.execSQL("CREATE TABLE `base` ( `_id` INTEGER PRIMARY KEY, `name` TEXT, `protein` NUMBER, " +
					" `fat` NUMBER,  `carbon` NUMBER,`caloricity` INTEGER , `category_name` TEXT, `type` TEXT," +
					" `category` INTEGER , `dish_id` INTEGER , `description` TEXT , `searchname` TEXT, barcode TEXT );");
			db.execSQL("CREATE TABLE " + DAYLY_DISHES_TABLE_NAME + " (_id INTEGER PRIMARY KEY," + TODAY_DISH_ID + " INTEGER," + TODAY_NAME
					+ " TEXT," + TODAY_DESCRIPTION + " TEXT,"  + TODAY_CALORICITY + " TEXT," + TODAY_CATEGORY + " INTEGER," + TODAY_DISH_WEIGHT
					+ " TEXT," + TODAY_DISH_CALORICITY + " TEXT," + TODAY_DISH_DATE + " TEXT," + TODAY_DISH_DATE_LONG + " NUMBER,"+ TODAY_DISH_FAT + " NUMBER,"+ TODAY_DISH_CARBON + " NUMBER,"+ TODAY_DISH_PROTEIN + " NUMBER,"+ TODAY_FAT + " NUMBER,"+ TODAY_CARBON + " NUMBER,"+ TODAY_PROTEIN + " NUMBER," + TODAY_IS_DAY   + " INTEGER," + TYPE + " TEXT," + TODAY_DISH_TIME_HH + " INTEGER," + TODAY_DISH_TIME_MM + " INTEGER" +  ");");
			db.execSQL("CREATE TABLE " + DAYLY_TEMPLATE_TABLE_NAME + " (_id INTEGER PRIMARY KEY," + TODAY_DISH_ID + " INTEGER," + TODAY_NAME
					+ " TEXT," + TODAY_DESCRIPTION + " TEXT,"  + TODAY_CALORICITY + " TEXT," + TODAY_CATEGORY + " INTEGER," + TODAY_DISH_WEIGHT
					+ " TEXT," + TODAY_DISH_CALORICITY + " TEXT," + TODAY_DISH_DATE + " TEXT," + TODAY_DISH_DATE_LONG + " NUMBER,"+ TODAY_DISH_FAT + " NUMBER,"+ TODAY_DISH_CARBON + " NUMBER,"+ TODAY_DISH_PROTEIN + " NUMBER,"+ TODAY_FAT + " NUMBER,"+ TODAY_CARBON + " NUMBER,"+ TODAY_PROTEIN + " NUMBER," + TODAY_IS_DAY   + " INTEGER," + TYPE + " TEXT," + TODAY_DISH_TIME_HH + " INTEGER," + TODAY_DISH_TIME_MM + " INTEGER" +  ");");
			db.execSQL("CREATE TABLE " + MESSAGES_TABLE_NAME + " (_id INTEGER PRIMARY KEY," + USER_ID + " INTEGER,"+ USER_ID_FROM + " INTEGER," + DATE
					+ " TEXT," + USER_NAME + " TEXT,"  + DATE_INT + " TEXT," + MESSAGE_TEXT + " TEXT," + DATA
					+ " TEXT"  +  ");");
			}catch (SQLiteException e) {
				e.printStackTrace();
			}
			
			//(BaseLoader.getStringFromFile(R.raw.base_sql, ctx),null).moveToFirst();;
			/*db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql2, ctx),null).moveToFirst();;
			db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql3, ctx),null).moveToFirst();;
			db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql4, ctx),null).moveToFirst();;
			db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql5, ctx),null).moveToFirst();;
			db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql6, ctx),null).moveToFirst();;	
			db.rawQuery(BaseLoader.getStringFromFile(R.raw.base_sql7, ctx),null).moveToFirst();;*/	

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			if(oldVersion != newVersion){
				//db.execSQL("DROP TABLE IF EXISTS " + DISHES_TABLE_NAME);
				//db.execSQL("DROP TABLE IF EXISTS " + DAYLY_DISHES_TABLE_NAME);
				//db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
				//db.execSQL("DROP TABLE IF EXISTS " + DAYLY_TEMPLATE_TABLE_NAME);
			
				/*db.execSQL("ALTER TABLE "+DAYLY_DISHES_TABLE_NAME+" add column " + TODAY_DISH_TIME_HH + " INTEGER ;");
				
				}catch (SQLiteException e) {
					e.printStackTrace();
				}
				try{
				db.execSQL("ALTER TABLE "+DAYLY_DISHES_TABLE_NAME+" add column " + TODAY_DISH_TIME_MM + " INTEGER ;");
				}catch (SQLiteException e) {
					e.printStackTrace();
				}
				try{
				db.execSQL("ALTER TABLE "+DAYLY_TEMPLATE_TABLE_NAME+" add column " + TODAY_DISH_TIME_HH + " INTEGER ;");
				}catch (SQLiteException e) {
					e.printStackTrace();
				}
				try{
				db.execSQL("ALTER TABLE "+DAYLY_TEMPLATE_TABLE_NAME+" add column " + TODAY_DISH_TIME_MM + " INTEGER ;");
				*/
				try{
					db.execSQL("ALTER TABLE "+DISHES_TABLE_NAME+" add column " + BARCODE + " TEXT ;");
				}catch (SQLiteException e) {
					e.printStackTrace();
				}
			
			}
		}
		
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		//case SINGLE_ROW: 
		//	qb.appendWhere(DISH_ID + "=" + uri.getPathSegments().get(1));
		//	break;

		case DISHLIST:
			qb.setTables(DISHES_TABLE_NAME);
			break;
		case TODAYDISHLIST:
			qb.setTables(DAYLY_DISHES_TABLE_NAME);
			break;
		case MESSAGELIST:
			qb.setTables(MESSAGES_TABLE_NAME);
			break;
		case TEMPLATELIST:
			qb.setTables(DAYLY_TEMPLATE_TABLE_NAME);
			break;
			
		}

		// If no sort order is specified sort by date / time
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = NAME;
		} else {
			orderBy = sort;
		}

		// Apply the query to the underlying database.
		Cursor c = qb.query(allDishesDB, projection, selection, selectionArgs,
				null, null, orderBy);
		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		// Return a cursor to the query result.
		return c;
	}

	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		// Insert the new row, will return the row number if
		// successful.
		final int match = uriMatcher.match(_uri);
        switch (match) {
        	case DISHLIST: {
        		long rowID = allDishesDB.insert(DISHES_TABLE_NAME, "quake",
        				_initialValues);
        		// Return a URI to the newly inserted row on success.
        		if (rowID > 0) {
        			Uri uri = ContentUris.withAppendedId(DISHLIST_CONTENT_URI, rowID);
        			getContext().getContentResolver().notifyChange(uri, null);
        			return uri;
        		}
            }
            case TODAYDISHLIST: {
            	long rowID = allDishesDB.insert(DAYLY_DISHES_TABLE_NAME, "quake",
        				_initialValues);
        		// Return a URI to the newly inserted row on success.
        		if (rowID > 0) {
        			Uri uri = ContentUris.withAppendedId(TODAYDISH_CONTENT_URI, rowID);
        			getContext().getContentResolver().notifyChange(uri, null);
        			return uri;
        		}
            }
            case MESSAGELIST: {
            	long rowID = allDishesDB.insert(MESSAGES_TABLE_NAME, "quake",
        				_initialValues);
        		// Return a URI to the newly inserted row on success.
        		if (rowID > 0) {
        			Uri uri = ContentUris.withAppendedId(MESSAGES_CONTENT_URI, rowID);
        			getContext().getContentResolver().notifyChange(uri, null);
        			return uri;
        		}
            }
            case TEMPLATELIST: {
            	long rowID = allDishesDB.insert(DAYLY_TEMPLATE_TABLE_NAME, "quake",
        				_initialValues);
        		// Return a URI to the newly inserted row on success.
        		if (rowID > 0) {
        			Uri uri = ContentUris.withAppendedId(TEMPLATE_CONTENT_URI, rowID);
        			getContext().getContentResolver().notifyChange(uri, null);
        			return uri;
        		}
            }
        }
		
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case DISHLIST:
			count = allDishesDB.delete(DISHES_TABLE_NAME, where, whereArgs);
			break;
		case TODAYDISHLIST:
			//String segment = uri.getPathSegments().get(0);
			count = allDishesDB.delete(DAYLY_DISHES_TABLE_NAME, where, whereArgs);
//					PID
//							+ "="
//							+ segment
//							+ (!TextUtils.isEmpty(where) ? " AND (" + where
//									+ ")" : ""), whereArgs);
			break;
		case TEMPLATELIST:
			//String segment = uri.getPathSegments().get(0);
			count = allDishesDB.delete(DAYLY_TEMPLATE_TABLE_NAME, where, whereArgs);
//					PID
//							+ "="
//							+ segment
//							+ (!TextUtils.isEmpty(where) ? " AND (" + where
//									+ ")" : ""), whereArgs);
			break;
			
		case MESSAGELIST:
			//String segment = uri.getPathSegments().get(0);
			count = allDishesDB.delete(MESSAGES_TABLE_NAME, where, whereArgs);
//					PID
//							+ "="
//							+ segment
//							+ (!TextUtils.isEmpty(where) ? " AND (" + where
//									+ ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case ALLROWS:
			count = allDishesDB.update(DISHES_TABLE_NAME, values, where,
					whereArgs);
			break;
		case DISHLIST:
			count = allDishesDB.update(DISHES_TABLE_NAME, values, where,
					whereArgs);
			break;
		case TODAYDISHLIST:
			//String segment = uri.getPathSegments().get(0);
			count = allDishesDB.update(DAYLY_DISHES_TABLE_NAME, values, where,
					whereArgs);
//					PID
//							+ "="
//							+ segment
//							+ (!TextUtils.isEmpty(where) ? " AND (" + where
//									+ ")" : ""), whereArgs);
			break;
		case MESSAGELIST:

			count = allDishesDB.update(MESSAGES_TABLE_NAME, values, where,
					whereArgs);

			break;
		case TEMPLATELIST:

			count = allDishesDB.update(DAYLY_TEMPLATE_TABLE_NAME, values, where,
					whereArgs);

			break;
		case SINGLE_ROW:
			String segment = uri.getPathSegments().get(1);
			count = allDishesDB.update(DISHES_TABLE_NAME, values,
					DISH_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri _uri) {
		switch (uriMatcher.match(_uri)) {
		//case ALLROWS:
		//	return "vnd.DishProvider.cursor.dir/itemscontent";
		//case SINGLE_ROW:
		//	return "vnd.DishProvider.cursor.item/itemscontent";
		case TODAYDISHLIST:
			return "vnd.DishProvider.cursor.item/todayitemscontent";
		case TEMPLATELIST:
			return "vnd.DishProvider.cursor.item/templatecontent";	
		case DISHLIST:
			return "vnd.DishProvider.cursor.item/itemscontent";
		case MESSAGELIST:
			return "vnd.DishProvider.cursor.item/messagescontent";

		default:
			throw new IllegalArgumentException("Unsupported URI: " + _uri);
		}
	}

	
	
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		allDishesDB = dbHelper.getWritableDatabase();
		//if(SaveUtils.isFirstTime(getContext())){
		//	dbHelper.onUpgrade(allDishesDB, DATABASE_VERSION, DATABASE_VERSION+1);
		//}
		return (allDishesDB != null);
	}

}
