package bulat.diet.helper_plus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import bulat.diet.helper_plus.item.TodayDish;
import android.content.Context;
import android.os.AsyncTask;


public class RecepyUpdater extends AsyncTask<Void, Void, Void> {

	private Context context;

	
	List<TodayDish> toSincList;
	
	public RecepyUpdater(Context context) {
		this.context = context;		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		InputStream inputStream = null;
        String result = "";
        StringBuilder builder = new StringBuilder();
        try {
 
        	    JSONObject Parent = new JSONObject();
        	    JSONArray array = new JSONArray();
        	    JSONObject version = new JSONObject();
        	    version.put("lastid", 0);  
        	    JSONObject userid = new JSONObject();
        	    userid.put("userid", SaveUtils.getUserUnicId(context));
        	    Parent.put("data", array);       
        	    Parent.put("version", version);
        	    Parent.put("userid", userid);
        	    
        	    HttpClient client = new DefaultHttpClient();
        	    HttpPost post = new HttpPost("http://calorycman.net/server.php");
        	    StringEntity se = new StringEntity(Parent.toString());  //new ByteArrayEntity(json.toString().getBytes(            "UTF8"))
        	    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        	    post.setHeader("Accept", "application/json");
        	    post.setHeader("Content-type", "application/json");
        	    post.setEntity(se);
        	    HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				}
				String resultString = builder.toString().trim();
				try {
					JSONObject jsonRoot = new JSONObject(resultString);
					JSONArray jsonArray = new JSONObject(jsonRoot.getString("update")).getJSONArray("dishes");			
					
					JSONArray mapArray = new JSONObject(jsonRoot.getString("update")).getJSONArray("map");				
					
					ArrayList<TodayDish> newDishes = new ArrayList<TodayDish>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						TodayDish dish = new TodayDish();
						
						JSONObject jsonObj = new JSONObject(); //jsonObject.getString("name");
						//dish.setServerId(jsonObject.getInt("id"));
						dish.setBodyweight(Float.parseFloat(jsonObject.getString("bodyweight")));
						dish.setName(jsonObject.getString("name"));
						dish.setDescription(jsonObject.getString("daytime"));
						dish.setCaloricity(jsonObject.getInt("calorisity"));
						dish.setAbsolutCaloricity(jsonObject.getInt("calorie"));
						dish.setCategory(jsonObject.getString("category"));
						dish.setWeight(jsonObject.getInt("dishweight"));
						dish.setDate(jsonObject.getString("datetext"));
						dish.setDateTime(jsonObject.getLong("datelong"));
						dish.setFat(Float.parseFloat(jsonObject.getString("fat")));
						dish.setCarbon(Float.parseFloat(jsonObject.getString("carbon")));
						dish.setProtein(Float.parseFloat(jsonObject.getString("protein")));
						dish.setAbsFat(Float.parseFloat(jsonObject.getString("fatabs")));        	        
						dish.setAbsCarbon(Float.parseFloat(jsonObject.getString("carbonabs")));
						dish.setAbsProtein(Float.parseFloat(jsonObject.getString("proteinabs")));
						dish.setType(jsonObject.getString("type"));
						dish.setDateTimeHH(jsonObject.getInt("hh"));
						dish.setDateTimeMM(jsonObject.getInt("mm"));
						
						newDishes.add(dish);
					}
					//list = new ArrayList<Dish>(unicDish);
				} catch (Exception e) {
					e.printStackTrace();
				}
 
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}



	
}