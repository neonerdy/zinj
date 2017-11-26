package com.xeris.zinj.ui;



import java.lang.reflect.Field;
import java.util.Map;

import android.content.Intent;
import com.xeris.zinj.R;

import android.os.Bundle;
import android.view.ViewConfiguration;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;

public class MainActivity extends Activity {

	private ActionBar bar;
	private SettingRepository settingRepository;
    private Bundle extras;
    private String isLogin;
    private Map<String,String> query;

    public void setQuery(Map<String,String> query)
    {
        this.query=query;
    }

    public Map<String,String> getQuery()
    {
        return query;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);

        extras=getIntent().getExtras();

        if (extras!=null)
        {
            isLogin=extras.getString("IS_LOGIN");
        }

        settingRepository=new SettingRepository(this);
        Setting setting=settingRepository.getById(1);

        int isProtected=setting.getIsProtected();
        if (isProtected==1 && extras==null)
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else
        {
            //super.onCreate(savedInstanceState);
            setContentView(R.layout.main_activity);

            try
            {
                ViewConfiguration config=ViewConfiguration.get(this);
                Field menuKeyField=ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField!=null)
                {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            }
            catch(Exception ex)
            {

            }

             bar = getActionBar();


             bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

             bar.addTab(bar.newTab()
                .setIcon(R.drawable.transaction)
                .setTabListener(new TabListener<TransactionFragment>(
                 this,"Transaction", TransactionFragment.class)));

             bar.addTab(bar.newTab()
                .setIcon(R.drawable.account)
                .setTabListener(new TabListener<AccountFragment>(
                  this, "Account", AccountFragment.class)));

             bar.addTab(bar.newTab()
                .setIcon(R.drawable.category)
                .setTabListener(new TabListener<CategoryFragment>(
                this, "Category", CategoryFragment.class)));



             bar.addTab(bar.newTab()
                .setIcon(R.drawable.pie_chart)
                     .setTabListener(new TabListener<ChartFragment>(
                 this, "Chart", ChartFragment.class)));

             bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
             bar.setDisplayShowTitleEnabled(true);
             bar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.gray_back));

             setTitle("Transaction");

        }
		
	}
	
	
	
	
	
	
	 public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
	        private final Activity mActivity;
	        private final String mTag;
	        private final Class<T> mClass;


         private final Bundle mArgs;
         private Fragment mFragment;
	        public TabListener(Activity activity, String tag, Class<T> clz) {
	            this(activity, tag, clz, null);
	        }

	        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
	            mActivity = activity;
	            mTag = tag;
	            mClass = clz;
	            mArgs = args;
	            
	            // Check to see if we already have a fragment for this tab, probably
	            // from a previously saved state.  If so, deactivate it, because our
	            // initial state is that a tab isn't shown.
	         
	            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
	            if (mFragment != null && !mFragment.isDetached()) {
	                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
	                ft.detach(mFragment);
	                ft.commit();
	            }
	        }

	        public void onTabSelected(Tab tab, FragmentTransaction ft) {
	            if (mFragment == null) {
	            	mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
	                ft.add(android.R.id.content, mFragment, mTag);
	                
	             } else {
	                ft.attach(mFragment);
	            }
	       }
	        
	       
	        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	            if (mFragment != null) {
	                ft.detach(mFragment);
	            }
	        }

	        public void onTabReselected(Tab tab, FragmentTransaction ft) {
	      
	        }
	    }
	 

	

}
