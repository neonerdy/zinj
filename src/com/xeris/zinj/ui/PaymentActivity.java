package com.xeris.zinj.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.SpinnerAdapter;
import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.repository.AccountRepository;
import com.xeris.zinj.repository.CategoryRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.TransactionRepository;
import com.xeris.zinj.repository.AccountRepository.AccountType;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

public class PaymentActivity extends Activity {

	private AccountRepository accountRepository;
	private CategoryRepository categoryRepository;
	private TransactionRepository transactionRepository;
	
	static final int DATE_DIALOG_ID = 999;
	
	private int year;
	private int month;
	private int day;
	private Calendar cal;
	
	private Bundle extras;
	
	private EditText txtAmount;
	private TextView lblForAccount;
	private Spinner cboFromAccount;
	private Spinner cboForExpense;
	private Button btnDate;
	private EditText txtNotes;
	private TextView lblForAccountId;
	private TextView lblFromAccountId;
    private TextView lblFromAccountBalance;
    private TextView lblForExpenseId;
	
	private List<SpinnerItem> accountItems;
	private List<SpinnerItem> categoryItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_activity);
	
		accountRepository=new AccountRepository(this);
		categoryRepository=new CategoryRepository(this);
		transactionRepository=new TransactionRepository(this);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
		
		setTitle("Payment");
			
		txtAmount=(EditText)findViewById(R.id.txtPaymentAmount);
		txtAmount.setRawInputType(Configuration.KEYBOARD_QWERTY);

		lblForAccount=(TextView)findViewById(R.id.lblPaymentForAccount);
		cboFromAccount=(Spinner)findViewById(R.id.cboPaymentFromAccount);
		cboForExpense=(Spinner)findViewById(R.id.cboPaymentForExpense);
		
		lblForAccountId=(TextView)findViewById(R.id.lblPaymentForAccountId);
		lblFromAccountId=(TextView)findViewById(R.id.lblPaymentFromAccountId);
        lblFromAccountBalance=(TextView)findViewById(R.id.lblPaymentFromAccountBalance);
        lblForExpenseId=(TextView)findViewById(R.id.lblPaymentForExpenseId);

		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		
		btnDate=(Button)findViewById(R.id.btnPaymentDate);
		btnDate.setText("  " + (month+1) + "/" + day + "/" + year);
		btnDate.setTextColor(Color.GRAY);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });
		
		txtNotes=(EditText)findViewById(R.id.txtPaymentNotes);
		extras=getIntent().getExtras();
		
		if (extras!=null)
		{
			String accountId=extras.getString(Store.ACCOUNT_ID);
						
			Account account=accountRepository.getById(Integer.parseInt(accountId));
			if (account!=null) lblForAccount.setText(account.getName());
			
			lblForAccountId.setText(accountId);
			fillAccount(AccountType.CashAndSavings);
			fillCategory(CategoryType.Expense);
		}
	
	}
	
	
	
	private void fillAccount(AccountType accountType) 
	{
		accountItems=accountRepository.getSpinnerItemsByType(accountType);
	
		if (accountItems.size()>0)
		{		
			 SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, accountItems);
			 cboFromAccount.setAdapter(adapter);
			 cboFromAccount.setSelection(accountItems.size() - 1);
		     
			 cboFromAccount.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int posiiton, long id) {
					
						SpinnerItem data = accountItems.get(posiiton);
						if (data.isHint())
						{
							 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
							 ((TextView) parent.getChildAt(0)).setTextSize(15);
						}else {
							Account account=accountRepository.getByName(cboFromAccount.getSelectedItem().toString());
							
							if (account!=null)
							{
								lblFromAccountId.setText(String.valueOf(account.getId()));
                                lblFromAccountBalance.setText(String.valueOf(account.getBalance()));
							}
						}
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
		
		}
	}
	
	

	private void fillCategory(CategoryType categoryType) 
	{
		categoryItems=categoryRepository.getSpinnerItemsByType(categoryType);
	
		if (categoryItems.size()>0)
		{		
			 SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, categoryItems);
			 cboForExpense.setAdapter(adapter);
			 cboForExpense.setSelection(categoryItems.size() - 1);
		     
			 cboForExpense.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int posiiton, long id) {
					
						SpinnerItem data = categoryItems.get(posiiton);
						if (data.isHint())
						{
							 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
							 ((TextView) parent.getChildAt(0)).setTextSize(16);
						}else{
							
							Category category=categoryRepository.getByName(cboForExpense.getSelectedItem().toString());
							if (category!=null) lblForExpenseId.setText(String.valueOf(category.getId()));
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
	
	private void payCreditCard()
	{				
		if (txtAmount.getText().toString().equals(""))
		{
 			Toast.makeText(this, "Amount can not be empty", Toast.LENGTH_LONG).show();
 	   	}
        else if (cboFromAccount.getSelectedItem()==null)
        {
            Toast.makeText(this, "Please add account",Toast.LENGTH_LONG).show();
        }
        else if (cboForExpense.getSelectedItem()==null)
        {
            Toast.makeText(this, "Please add category",Toast.LENGTH_LONG).show();
        }
        else if (cboFromAccount.getSelectedItem().toString().equals("Please Select"))
		{
			Toast.makeText(this, "Please select account", Toast.LENGTH_LONG).show();
		}
		else if (cboForExpense.getSelectedItem().toString().equals("Please Select"))
		{
			Toast.makeText(this, "Please select expense", Toast.LENGTH_LONG).show();
		}
        else if (isBalanceNotSufficient(Float.parseFloat(txtAmount.getText().toString())))
        {
            Toast.makeText(this, "Insufficient payment.Please check your account balance",Toast.LENGTH_LONG).show();
        }
		else
		{
			Transaction transaction=new Transaction();
			
			transaction.setAmount(Float.parseFloat(txtAmount.getText().toString()));
			transaction.setType(Constant.TRANSACTION_PAYMENT);
		
			transaction.setCategoryId(Integer.parseInt(lblForExpenseId.getText().toString()));
			transaction.setAccountId(Integer.parseInt(lblForAccountId.getText().toString()));
			transaction.setPaymentAccountId(Integer.parseInt(lblFromAccountId.getText().toString()));

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

			transaction.setDescription(lblForAccount.getText().toString() + " From " + cboFromAccount.getSelectedItem().toString());
			if (txtNotes.getText().toString().equals(""))
			{
				transaction.setNotes("");
			}
			else
			{
				transaction.setNotes(txtNotes.getText().toString().substring(0,1).toUpperCase() + txtNotes.getText().toString().substring(1));
            }

            transaction.setPaymentInfo("For Expense : " + cboForExpense.getSelectedItem().toString());
			transactionRepository.save(transaction);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("type", "Payment");
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
                payCreditCard();
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
