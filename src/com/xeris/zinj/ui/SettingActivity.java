package com.xeris.zinj.ui;



import java.util.ArrayList;

import android.content.Intent;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.SpinnerAdapter;
import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.Store.ActivityMode;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class SettingActivity extends Activity
{

	private ArrayList<SpinnerItem> items;
	private SettingRepository settingRepository;
	
	private EditText txtEmail;
	private EditText txtPassword;
	private CheckBox chkIsProtected;
	private Spinner cboMonth;
    private EditText txtYear;
	private EditText txtCurrency;
	private CheckBox chkHideSymbol;
    private Spinner cboDefaultChart;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		
		settingRepository=new SettingRepository(this);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
		
		items = new ArrayList<SpinnerItem>();
		
		txtEmail=(EditText)findViewById(R.id.txtSettingEmail);
		txtPassword=(EditText)findViewById(R.id.txtSettingPassword);
		chkIsProtected=(CheckBox)findViewById(R.id.chkSettingIsProtected);
        cboMonth=(Spinner)findViewById(R.id.cboSettingMonth);
		txtYear=(EditText)findViewById(R.id.txtSettingYear);
        txtCurrency=(EditText)findViewById(R.id.txtSettingCurrency);
		chkHideSymbol=(CheckBox)findViewById(R.id.chkSettingHideSymbol);
        cboDefaultChart=(Spinner)findViewById(R.id.cboSettingDefaultChart);

        getSetting();
		
	}
	

	private void getSetting()
	{
		Setting setting=settingRepository.getById(1);
		
		if (setting!=null)
		{
			txtEmail.setText(setting.getEmail());
			
			txtPassword.setText(String.valueOf(setting.getPassword()));
			
			if (setting.getIsProtected()==1) {
				chkIsProtected.setChecked(true);
			}else {
				chkIsProtected.setChecked(false);
			}
			
			getMonth();
            txtYear.setText(String.valueOf(setting.getYear()));

            getDefaultChart();

			txtCurrency.setText(setting.getCurrency());
			
			if (setting.getIsHideSymbol()==1) {
				chkHideSymbol.setChecked(true);
			}else {
				chkHideSymbol.setChecked(false);
			}
		}
		
	}


    private void getDefaultChart()
    {
        String[] charts={"Cash Flow","Income","Expense","Monthly Budget","Cash and Savings","Credit Card"};

        ArrayAdapter<String> dataAdapter=null;

        String defaultChart="";
        Setting setting=settingRepository.getById(1);

        if (setting!=null) defaultChart= setting.getDefaultChart();

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, charts);
        cboDefaultChart.setAdapter(dataAdapter);
        cboDefaultChart.setSelection(((ArrayAdapter<String>)cboDefaultChart.getAdapter()).getPosition((defaultChart)));

    }

	
	private void getMonth()
	{		
		
		String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};

		ArrayAdapter<String> dataAdapter=null;
		
		String activeMonth="";
	    Setting setting=settingRepository.getById(1);
	    
	    if (setting!=null) activeMonth= Store.getMonthString(setting.getMonth());

		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months);
		cboMonth.setAdapter(dataAdapter);
        cboMonth.setSelection(((ArrayAdapter<String>)cboMonth.getAdapter()).getPosition((activeMonth)));
	}

    private void getYear()
    {

    }

	
	
	private void saveSetting()
	{
	    if (chkIsProtected.isChecked() && txtPassword.getText().toString().equals(""))
        {
            Toast.makeText(this, "Password can not be empty",Toast.LENGTH_LONG).show();
            txtPassword.requestFocus();
        }
        else if (txtYear.getText().toString().equals(""))
        {
            Toast.makeText(this, "Year can not be empty",Toast.LENGTH_LONG).show();
            txtYear.requestFocus();
        }
		else if (txtCurrency.getText().toString().equals(""))
		{
			Toast.makeText(this, "Currency symbol can not be empty",Toast.LENGTH_LONG).show();
	        txtCurrency.requestFocus();
        }
		else
		{
			Setting setting=new Setting();
			
			setting.setId(1);
			setting.setEmail(txtEmail.getText().toString());
			setting.setPassword(txtPassword.getText().toString());
			
			if (chkIsProtected.isChecked())
			{			
				setting.setIsProtected(1);
			}else {
				setting.setIsProtected(0);
			}


        	setting.setMonth(Store.getMonthInt(cboMonth.getSelectedItem().toString()));
            setting.setYear(Integer.parseInt(txtYear.getText().toString()));

            setting.setDefaultChart(cboDefaultChart.getSelectedItem().toString());
			setting.setCurrency(txtCurrency.getText().toString());
			
			if (chkHideSymbol.isChecked()){
				setting.setIsHideSymbol(1);
			}else {
				setting.setIsHideSymbol(0);
			}
			
			settingRepository.update(setting);
			
			finish();
		}	
		
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{		
		getMenuInflater().inflate(R.menu.add_menu, menu);
		return true;
	}
	
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) 
	 {

         switch (item.getItemId()) {

             case android.R.id.home:
                 finish();
                 break;
             case R.id.menuitem_save:
                 saveSetting();
                 break;
       }

        return super.onOptionsItemSelected(item);
    }	

}
