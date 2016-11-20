package bulat.diet.helper_plus.activity;

import java.util.ArrayList;
import java.util.Stack;

import com.dm.zbar.android.scanner.ZBarConstants;

import bulat.diet.helper_plus.utils.Constants;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

public class DishActivityGroup extends ActivityGroup{
	
	private Stack<String> stack;
	private boolean flag=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		
		if (stack == null) {
			stack = new Stack<String>();
		}
		//String lastLoadDate = SaveUtils.loadLastLoadDate(this);
		//if (lastLoadDate.length() == 0){
			
		//} else {
			push("DishActivity", new Intent(this, DishActivity.class));
	//	}
			
	}
	
	@Override
		protected void onStart() {
		super.onStart();
			// TODO Auto-generated method stub
			
		}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
        super.onActivityResult(requestCode, resultCode, data);               
        switch (requestCode) {
        	
        case 2: {	        		 				        
            if (resultCode == RESULT_OK && null != data) {
            	String code = data.getStringExtra(ZBarConstants.SCAN_RESULT); 	               
                if(code!= null && code.length()>3){
                	Constants.ST = code;
                }
            } else if(resultCode == RESULT_CANCELED && data != null) {
                String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                if(!TextUtils.isEmpty(error)) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
            break;}
	        case 1: {	        		 				        
	            if (resultCode == RESULT_OK && null != data) {
	            	String code = data.getStringExtra(ZBarConstants.SCAN_RESULT);
	            	
	                ArrayList<String> text = data
	                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);	 
	                Constants.ST = text.get(0);	  	               
	                if(code!= null && code.length()>3){
	                	Constants.ST = code;
	                }
	            } else if(resultCode == RESULT_CANCELED && data != null) {
	                String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
	                if(!TextUtils.isEmpty(error)) {
	                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
	                }
	            }
	            break;
	        }    
	        	 
        }
        flag=false;
       // push("DishListActivity", intent);

    }
	@Override
	protected void onResume() {
		//boolean firstLaunch = SaveUtils.loadFirstLaunchFlag(this);
       // if (firstLaunch){
		if(flag){
			getFirst();
		}else{
			flag=true;
		}
       // }
		super.onResume();
	}
	
	@Override
	  public void finishFromChild(Activity child) {
	    pop();
	  }

	  @Override
	  public void onBackPressed() {
	    pop();
	  }


	  public void push(String id, Intent intent) {
	    Window window = getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    if (window != null) {
	      stack.push(id);
	      setContentView(window.getDecorView());
	    }
	  }

	  public void pop() {
		  try{
	    if (stack.size() == 1) {
	    	finish();
	    }else{
		    LocalActivityManager manager = getLocalActivityManager();
		    manager.destroyActivity(stack.pop(), true);
		    if (stack.size() > 0) {
		      Intent lastIntent = manager.getActivity(stack.peek()).getIntent();
		      Window newWindow = manager.startActivity(stack.peek(), lastIntent);
		      setContentView(newWindow.getDecorView());
		    }
	    }
		  }catch (Exception e) {
			e.printStackTrace();
		}
	  }
	  
	  public void getFirst(){
		  if(stack.size() > 0){
			  LocalActivityManager manager = getLocalActivityManager();
			  while (stack.size() != 1){
				  manager.destroyActivity(stack.pop(), true);
			  }
			  Intent lastIntent = manager.getActivity(stack.peek()).getIntent();
		      Window newWindow = manager.startActivity(stack.peek(), lastIntent);
		      setContentView(newWindow.getDecorView());
		  }
	  }
	
	  @Override
		protected void onStop() {
			//stack = null;
			super.onStop();
		}
	  
}
