package bulat.diet.helper_plus.reciver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.activity.DietHelperActivity;
import bulat.diet.helper_plus.item.DishType;
import bulat.diet.helper_plus.utils.SaveUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;


public class Recive extends BroadcastReceiver {
	public NotificationManager myNotificationManager;
	public static final int NOTIFICATION_ID = 1;
	public static final int NOTIFICATION_ID2 = 2;
	TreeMap<String,DishType> map = new TreeMap<String, DishType>();
	private boolean flagNotFirstTimeAdv=true;
	@Override
	public void onReceive(Context context, Intent intent) {
	
	    myNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

	      CharSequence NotificationTicket = context.getString(R.string.newmessage);
	      CharSequence NotificationTitle = context.getString(R.string.newmessage_title);
	     // CharSequence NotificationContent = "USB is Connected!";
	     
	      Date currDate = new Date();
    	  if(currDate.getTime() - SaveUtils.getLastVisitTime(context) > DateUtils.DAY_IN_MILLIS && currDate.getTime() - SaveUtils.getLastVisitTime(context) < 3 * DateUtils.DAY_IN_MILLIS  ){
    		  Notification notification = new Notification(R.drawable.icon_m, NotificationTicket, 0);
		      Intent notificationIntent = new Intent(context, DietHelperActivity.class);
		      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		      notification.setLatestEventInfo(context, context.getString(R.string.reminder), context.getString(R.string.reminder) , contentIntent);
		      notification.flags |= Notification.FLAG_AUTO_CANCEL;
		      myNotificationManager.notify(NOTIFICATION_ID2, notification);
    	  }
    	  if(currDate.getTime() - SaveUtils.getLastVisitTime(context) > 30*DateUtils.DAY_IN_MILLIS && currDate.getTime() - SaveUtils.getLastVisitTime(context) < 33 * DateUtils.DAY_IN_MILLIS  ){
    		  Notification notification = new Notification(R.drawable.icon_m, NotificationTicket, 0);
		      Intent notificationIntent = new Intent(context, DietHelperActivity.class);
		      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		      notification.setLatestEventInfo(context, context.getString(R.string.reminder), context.getString(R.string.reminder) , contentIntent);
		      notification.flags |= Notification.FLAG_AUTO_CANCEL;
		      myNotificationManager.notify(NOTIFICATION_ID2, notification);
    	  }
	      
	}	
}
