package com.xeris.zinj.ui;


import android.view.View;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.repository.AccountRepository;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.Store.ActivityMode;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.Menu;
import android.view.MenuItem;

public class AccountActivity extends Activity 
{
	
	private EditText txtName;
	private EditText txtBalance;
    private EditText txtNotes;
    private EditText txtLimit;
    private TextView lblFinancialGoal;
    private TextView lblFinancialGoalCaption;
    private EditText txtFinancialGoalName;
    private EditText txtFinancialGoalTarget;
    private CheckBox chkIsActive;

    private String accountId;
	private AccountRepository accountRepository;

	private Bundle extras;
    private String type;
	
  	   
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.account_activity);
	
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
		
		accountRepository=new AccountRepository(this);
    	
		txtName=(EditText)findViewById(R.id.txtAccountName);
		txtBalance=(EditText)findViewById(R.id.txtAccountBalance);
        txtNotes=(EditText)findViewById(R.id.txtAccountNotes);
        lblFinancialGoal=(TextView)findViewById(R.id.lblSeparatorFinancialGoal);
        lblFinancialGoalCaption=(TextView)findViewById(R.id.lblFinancialGoalCaption);
        txtFinancialGoalName=(EditText)findViewById(R.id.txtFinancialGoalName);
        txtFinancialGoalTarget=(EditText)findViewById(R.id.txtFinancialGoalTarget);
        chkIsActive=(CheckBox)findViewById(R.id.chkIsActive);
        txtLimit=(EditText)findViewById(R.id.txtAccountLimit);


        extras=getIntent().getExtras();
        type="Cash";

		if (extras!=null)
		{
			accountId=extras.getString(Store.ACCOUNT_ID);
			
			setTitle("Edit Account");
			editAccount();
		}
		else 
		{
			setTitle("Add Account");
		}
		
	}

    public void onRadioButtonClick(View v)
    {
        RadioButton button = (RadioButton) v;

        type=button.getText().toString();

        if (type.equals("Credit Card"))
        {
            lblFinancialGoal.setVisibility(View.INVISIBLE);
            lblFinancialGoalCaption.setVisibility(View.INVISIBLE);
            txtFinancialGoalName.setVisibility(View.INVISIBLE);
            txtFinancialGoalTarget.setVisibility(View.INVISIBLE);
            txtLimit.setVisibility(View.VISIBLE);
        }
        else
        {
            lblFinancialGoal.setVisibility(View.VISIBLE);
            lblFinancialGoalCaption.setVisibility(View.VISIBLE);
            txtFinancialGoalName.setVisibility(View.VISIBLE);
            txtFinancialGoalTarget.setVisibility(View.VISIBLE);
            txtLimit.setVisibility(View.INVISIBLE);
        }
    }




    private void editAccount()
	{		
		Account account=accountRepository.getById(Integer.parseInt(accountId));
		
		RadioButton rbCash=(RadioButton)findViewById(R.id.rbCash);
		RadioButton rbBank=(RadioButton)findViewById(R.id.rbBank);
		RadioButton rbCC=(RadioButton)findViewById(R.id.rbCC);
		
		if (account!=null) 
		{	
			txtName.setText(account.getName());
			String balance=String.valueOf(account.getBalance());
			txtBalance.setText(balance.replaceAll("\\.0*$", ""));
            txtNotes.setText(account.getNotes());

            chkIsActive.setChecked(account.getIsActive()==1?true:false);

			if (account.getType().equals("Cash"))
            {
                rbCash.setChecked(true);
                type="Cash";
            }
            else if (account.getType().equals("Savings"))
            {
                rbBank.setChecked(true);
                type="Savings";
            }
            else if (account.getType().equals("Credit Card"))
            {
                rbCC.setChecked(true);
                type="Credit Card";
            }

            if (type.equals("Credit Card"))
            {
                lblFinancialGoal.setVisibility(View.INVISIBLE);
                lblFinancialGoalCaption.setVisibility(View.INVISIBLE);
                txtFinancialGoalName.setVisibility(View.INVISIBLE);
                txtFinancialGoalTarget.setVisibility(View.INVISIBLE);
                txtLimit.setVisibility(View.VISIBLE);

                String limit=String.valueOf(account.getLimit());
                txtLimit.setText(limit.replaceAll("\\.0*$", ""));
            }
            else
            {

                txtFinancialGoalName.setText(account.getFinancialGoalName());
                String goalTarget=String.valueOf(account.getFinancialGoalTarget());
                txtFinancialGoalTarget.setText(goalTarget.replaceAll("\\.0*$", ""));

                lblFinancialGoal.setVisibility(View.VISIBLE);
                lblFinancialGoalCaption.setVisibility(View.VISIBLE);
                txtFinancialGoalName.setVisibility(View.VISIBLE);
                txtFinancialGoalTarget.setVisibility(View.VISIBLE);

                txtLimit.setVisibility(View.INVISIBLE);

            }




        }
	}

	
	public void saveAccount(ActivityMode activityMode)
	{
		String name=txtName.getHint().toString();
		String balance=txtBalance.getHint().toString();
		
		if (txtName.getText().toString().equals(""))
		{
			Toast.makeText(this, "Account name can not be empty", Toast.LENGTH_LONG).show();
			txtName.requestFocus();
		}
        else if (txtBalance.getText().toString().equals(""))
		{
			Toast.makeText(this, "Account balance can not be empty", Toast.LENGTH_LONG).show();
			txtBalance.requestFocus();
		}
        else if (txtBalance.getText().toString().equals("."))
        {
            Toast.makeText(this, "Incorrect balance",Toast.LENGTH_LONG).show();
            txtBalance.requestFocus();
        }
        else if (type.equals("Credit Card") && txtLimit.getText().toString().equals(""))
        {
            Toast.makeText(this, "Credit Card limit can not be empty",Toast.LENGTH_LONG).show();
            txtLimit.requestFocus();
        }
        else if (type.equals("Credit Card") && txtLimit.getText().toString().equals("0"))
        {
            Toast.makeText(this, "Credit Card limit can not zero",Toast.LENGTH_LONG).show();
            txtLimit.requestFocus();
        }
        else if (type.equals("Credit Card") && Float.parseFloat(txtLimit.getText().toString())
                < Float.parseFloat(txtBalance.getText().toString()))
        {
            Toast.makeText(this, "Limit can not less than balance",Toast.LENGTH_LONG).show();
            txtLimit.requestFocus();
        }
        else
		{	        	
			RadioGroup rbGroupType=(RadioGroup)findViewById(R.id.rbAccountType);
    		
    		int selectedId = rbGroupType.getCheckedRadioButtonId();
    		RadioButton rbType=(RadioButton)findViewById(selectedId);
            String type=rbType.getText().toString();

    		Account account=new Account();

    		account.setName(txtName.getText().toString().substring(0,1).toUpperCase() + txtName.getText().toString().substring(1));
        	account.setType(type);
    		account.setBalance(Float.parseFloat(txtBalance.getText().toString()));

            if (chkIsActive.isChecked())  {
                account.setIsActive(1);
            } else {
                account.setIsActive(0);
            }

            if(type.equals("Credit Card"))
            {
                account.setOrder(2);
                account.setLimit(Float.parseFloat(txtLimit.getText().toString()));
            }
            else
            {
                account.setFinancialGoalName(txtFinancialGoalName.getText().toString());
                account.setFinancialGoalTarget(Float.parseFloat(txtFinancialGoalTarget.getText().toString()));
                account.setOrder(0);
                account.setLimit(0);
            }

            if (txtNotes.getText().toString().equals("")) {
                account.setNotes("");
            }else {
                account.setNotes(txtNotes.getText().toString().substring(0,1).toUpperCase() + txtNotes.getText().toString().substring(1));
            }

            if (activityMode==ActivityMode.Add)
    		{
                if (accountRepository.isAccountExist(txtName.getText().toString().toUpperCase()))
                {
                    Toast.makeText(this, "Account already exist", Toast.LENGTH_LONG).show();
                }else {
    			    accountRepository.save(account);
                    finish();
    		   }
            }
    		else 
    		{
    			account.setId(Integer.parseInt(accountId));
	        	accountRepository.update(account);
                finish();
        	}
       }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		
		if (extras==null){
			getMenuInflater().inflate(R.menu.add_menu, menu);
		}else{
			getMenuInflater().inflate(R.menu.update_menu, menu);
		}
		
		return true;
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId())
            {
                case android.R.id.home:
                    finish();
                    break;
	        	case R.id.menuitem_save:
        			saveAccount(ActivityMode.Add);
	        		break;
	        	case R.id.menuitem_update :
	        		saveAccount(ActivityMode.Edit);
	    	    	break;
	        }

	        return super.onOptionsItemSelected(item);
	    }

	 
	 

}
