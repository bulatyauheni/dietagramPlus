package bulat.diet.helper_plus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bulat.diet.helper_plus.R;
import bulat.diet.helper_plus.item.Account;
import bulat.diet.helper_plus.utils.Constants;

import com.perm.kate.api.Api;

public class VkActivity extends Activity{
	
	private String vkUrl = "https://oauth.vk.com/authorize?client_id=3583596&scope=wall,offline&redirect_uri=oauth.vk.com/blank.html&display=touch&response_type=token";
    private String postUrl = "https://oauth.vk.com/method/wall.post?message=%s&attachment=%s&access_token=%s";
	private final String ACCESS_TOKEN = "blank.html#access_token";
	private final String SEND = "{\"response\":{\"processing\":1}}";
	public static final String MESSAGE = "MESSAGE";
	public static final String LINK = "LINK";
	 private final int REQUEST_LOGIN=1;
	    
	    Button authorizeButton;
	    Button logoutButton;
	    Button postButton;
	    EditText messageEditText;
	    
	    Account account=new Account();
	    Api api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startLoginActivity();
		
	}
	  private void startLoginActivity() {
	        Intent intent = new Intent();
	        intent.setClass(this, LoginActivity.class);
	        startActivityForResult(intent, REQUEST_LOGIN);
	    }
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == REQUEST_LOGIN) {
	            if (resultCode == RESULT_OK) {
	                //авторизовались успешно 
	                account.access_token=data.getStringExtra("token");
	                account.user_id=data.getLongExtra("user_id", 0);
	                account.save(VkActivity.this);
	                api=new Api(account.access_token, Constants.API_ID);
	                postToWall();
	                joinToGroup();
	                onBackPressed();
	            }
	        }
	    } 
	  
	   private void postToWall() {
	        new Thread(){
	            @Override
	            public void run(){
	                try {
	                    String text=getString(R.string.vk_recomend);
	                    ArrayList<String> attach = new ArrayList<String>();
	                    attach.add("https://play.google.com/store/apps/details?id=bulat.diet.helper_plus");
	                    api.createWallPost(account.user_id, text, attach, null, false, false, false, null, null, null, null);
	                    //Показать сообщение в UI потоке 
	                    runOnUiThread(successRunnable);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
	    
	  private void joinToGroup() {
	        //Общение с сервером в отдельном потоке чтобы не блокировать UI поток
	        new Thread(){
	            @Override
	            public void run(){
	                try {
	                    String text=getString(R.string.vk_recomend);
	                    ArrayList<String> attach = new ArrayList<String>();
	                    attach.add("https://play.google.com/store/apps/details?id=bulat.diet.helper_ru");
	                    api.joinGroup(47759767, null, null);
	                   // api.createWallPost(account.user_id, text, attach, null, false, false, false, null, null, null, null);
	                    //Показать сообщение в UI потоке                
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
	    
	    Runnable successRunnable=new Runnable(){
	        public void run() {
	            Toast.makeText(getApplicationContext(), getString(R.string.vk_toast), Toast.LENGTH_LONG).show();
	        }
	    };
	    	
	
}
