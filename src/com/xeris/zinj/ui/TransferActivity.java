package com.xeris.zinj.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.SpinnerAdapter;
import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.repository.AccountRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.TransactionRepository;
import com.xeris.zinj.repository.AccountRepository.AccountType;


import android.os.Bundle;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnTouchListener;

import android.widget.AdapterView.OnItemSelectedListener;

public class TransferActivity extends Activity 
{
	
	private AccountRepository accountRepository;
	private TransactionRepository transactionRepository;
	
	static final int DATE_DIALOG_ID = 999;
	
	private int year;
	private int month;
	private int day;
	private Calendar cal;
	
	private Bundle extras;
	
	private EditText txtAmount;
	private TextView lblFromAccount;
	private Spinner cboToAccount;
	private Button btnDate;
	private EditText txtNotes;
	private TextView lblFromAccountId;
    private TextView lblFromAccountBalance;
    private TextView lblToAccountId;

	private List<SpinnerItem> accountItems;
	
	private String accountId;
	private String transactionType;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_activity);
	
		accountRepository=new AccountRepository(this);
		transactionRepository=new TransactionRepository(this);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
					
		txtAmount=(EditText)findViewById(R.id.txtTransferAmount);
		txtAmount.setRawInputType(Configuration.KEYBOARD_QWERTY);

		lblFromAccount=(TextView)findViewById(R.id.lblTransferFrom);
		cboToAccount=(Spinner)findViewById(R.id.cboTransferToAccount);

		lblFromAccountId=(TextView)findViewById(R.id.lblTransferFromAccountId);
        lblFromAccountBalance=(TextView)findViewById(R.id.lblTransferFromAccountBalance);

        lblToAccountId=(TextView)findViewById(R.id.lblTransferToAccountId);
		
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		
		btnDate=(Button)findViewById(R.id.btnTransferDate);
		btnDate.setText("  " + (month+1) + "/" + day + "/" + year);
		btnDate.setTextColor(Color.GRAY);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });

		txtNotes=(EditText)findViewById(R.id.txtTransferNotes);
		
		extras=getIntent().getExtras();
		
		if (extras!=null)
		{
			accountId=extras.getString(Store.ACCOUNT_ID);
			transactionType=extras.getString(Store.TRANSACTION_TYPE);
			
			Account account=accountRepository.getById(Integer.parseInt(accountId));
			if (account!=null) lblFromAccount.setText(account.getName());
			lblFromAccountId.setText(accountId);
            lblFromAccountBalance.setText(String.valueOf(accountRepository.getBalance(Integer.parseInt(accountId))));

			if (transactionType.equals(Constant.TRANSACTION_TRANSFER))
			{
				setTitle("Transfer");
				fillAccount(AccountType.Savings);
			}
            else if (transactionType.equals(Constant.TRANSACTION_TRANSFER_CASH))
            {
                setTitle("Cash Transfer");
                fillAccount(AccountType.Cash);
            }
			else if (transactionType.equals(Constant.TRANSACTION_WITHDRAWAL))
			{
				setTitle("Withdrawal");
				fillAccount(AccountType.Cash);
			}
			else if (transactionType.equals(Constant.TRANSACTION_DEPOSIT))
			{
				setTitle("Deposit");
				fillAccount(AccountType.Savings);
			}
            else if (transactionType.equals(Constant.TRANSACTION_TRANSFER_AS_SAVINGS))
            {
                setTitle("Transfer As Savings");
                fillAccount((AccountType.Savings));
            }
		}
	}
		
	
	
	private void fillAccount(AccountType accountType) 
	{
		accountItems=accountRepository.getSpinnerItemsByType(accountType);
	
		if (accountItems.size()>0)
		{		
			 SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, accountItems);
			 cboToAccount.setAdapter(adapter);
			 cboToAccount.setSelection(accountItems.size() - 1);
		     
			 cboToAccount.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int posiiton, long id) {
					
						SpinnerItem data = accountItems.get(posiiton);
						if (data.isHint())
						{
							 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
							 ((TextView) parent.getChildAt(0)).setTextSize(15);
						}else {
							Account account=accountRepository.getByName(cboToAccount.getSelectedItem().toString());
							
							if (account!=null)
							{
								lblToAccountId.setText(String.valueOf(account.getId()));
							}
						}
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
		
		}
	}


    private boolean isBalanceNotSufficient(float amount)
    {
        boolean isNotSufficient=false;

        float balance=Float.parseFloat(lblFromAccountBalance.getText().toString());
        if (balance < amount) {
            isNotSufficient=true;
        }

        return isNotSufficient;
    }

	
	private void transfer()
	{

        if (txtAmount.getText().toString().equals(""))
        {
            Toast.makeText(this, "Amount can not be empty",Toast.LENGTH_LONG).show();
            txtAmount.requestFocus();
        }
        else if (cboToAccount.getSelectedItem()==null)
        {
            Toast.makeText(this, "Please add account",Toast.LENGTH_LONG).show();
        }
        else if (cboToAccount.getSelectedItem().toString().equals("Please Select"))
        {
            Toast.makeText(this, "Please select account", Toast.LENGTH_LONG).show();
        }
        else if (cboToAccount.getSelectedItem().toString().equals(lblFromAccount.getText().toString()))
        {
            Toast.makeText(this, "Please select other account", Toast.LENGTH_LONG).show();
        }
        else if (isBalanceNotSufficient(Float.parseFloat(txtAmount.getText().toString())))
        {
            Toast.makeText(this, "Insufficient " + transactionType.toLowerCase() + ". Please check your account balance",Toast.LENGTH_LONG).show();
        }
        else
        {
            Transaction transaction=new Transaction();

            transaction.setAmount(Float.parseFloat(txtAmount.getText().toString()));
            transaction.setType(transactionType);
            transaction.setCategoryId(Integer.parseInt(lblFromAccountId.getText().toString()));
            transaction.setAccountId(Integer.parseInt(lblToAccountId.getText().toString()));

            SimpleDateFormat dateFormat=new SimpleDateFormat("MM/dd/yyyy");
            String date=btnDate.getText().toString().trim();

            Date d=null;
            try {
                d=dateFormat.parse(date);
            } catch (ParseException e) {
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String sDate = df.format(d);
            transaction.setDate(sDate);

            if (txtNotes.getText().toString().equals("")) {
                transaction.setNotes("");
            } else {
                transaction.setNotes(txtNotes.getText().toString().substring(0,1).toUpperCase() + txtNotes.getText().toString().substring(1));
            }

            transaction.setPaymentAccountId(0);
            transaction.setDescription(lblFromAccount.getText().toString() + " To " + cboToAccount.getSelectedItem().toString());

            transactionRepository.save(transaction);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("type", transactionType);
            setResult(Activity.RESULT_OK, resultIntent);


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
		switch(item.getItemId())
		{
            case android.R.id.home:
                finish();
                break;
			case R.id.menuitem_save:
				transfer();
				break;
        }
		
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month,day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener=new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			btnDate.setText("  " + (month+1) + "/" + day + "/" + year);
			
		}
	};
	

}
