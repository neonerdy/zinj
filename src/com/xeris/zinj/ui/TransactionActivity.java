package com.xeris.zinj.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.xeris.zinj.model.Category;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.repository.AccountRepository;
import com.xeris.zinj.repository.CategoryRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.TransactionRepository;
import com.xeris.zinj.repository.AccountRepository.AccountType;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;
import com.xeris.zinj.repository.Store.ActivityMode;

import android.os.Bundle;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView.OnItemSelectedListener;

public class TransactionActivity extends Activity {
   
    private int year;
	private int month;
	private int day;
	
	private ArrayList<SpinnerItem> categoryItems;
	private List<SpinnerItem> accountItems;
	
	static final int DATE_DIALOG_ID = 999;
	
	private CategoryRepository categoryRepository;
	private AccountRepository accountRepository;
	private TransactionRepository transactionRepository;

    private EditText txtAmount;
	private RadioButton rbIncome;
	private RadioButton rbExpense;
	private TextView lblInfo;
	private Spinner cboCategory;
	private Spinner cboAccount;
	private Button btnDate;
    private EditText txtNotes;
	private TextView lblCategoryId;
	private TextView lblAccountId;
    private TextView lblFromAccountBalance;
    private TextView lblAccountBalance;
    private TextView lblPaymentAccountBalance;
	private TextView lblSeparator1;
	private TextView lblSeparator2;
    private TextView lblCurrentAmount;
	
	private Bundle extras;
	private ActivityMode activityMode;
	private String transactionId;
    private String transactionType;

    private float transactionAmount;
    private int transactionPaymentAccountId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_activity);
	
		categoryRepository=new CategoryRepository(this);
		accountRepository=new AccountRepository(this);
		transactionRepository=new TransactionRepository(this);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
		
		categoryItems = new ArrayList<SpinnerItem>();
		accountItems=new ArrayList<SpinnerItem>();
				
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		txtAmount=(EditText)findViewById(R.id.txtTransactionAmount);
		cboCategory=(Spinner)findViewById(R.id.cboTransactionCategory);
		cboAccount=(Spinner)findViewById(R.id.cboTransactionAcount);
		
		rbIncome=(RadioButton)findViewById(R.id.rbTransactionIncome);
		rbExpense=(RadioButton)findViewById(R.id.rbTransactionExpense);
		lblInfo=(TextView)findViewById(R.id.lblTransactionInfo);

		txtNotes=(EditText)findViewById(R.id.txtTransactionNotes);
		lblCategoryId=(TextView)findViewById(R.id.lblTransactionCategoryId);
		lblAccountId=(TextView)findViewById(R.id.lblTransactionAccountId);
        lblFromAccountBalance=(TextView)findViewById(R.id.lblTransactionFromAccountBalance);
        lblAccountBalance=(TextView)findViewById(R.id.lblTransactionAccountBalance);
        lblPaymentAccountBalance=(TextView)findViewById(R.id.lblTransactionPaymentAccountBalance);
        lblCurrentAmount=(TextView)findViewById(R.id.lblTransactionCurrentAmount);

      	btnDate=(Button)findViewById(R.id.btnTransactionDate);
        btnDate.setText("  " + (month+1) + "/" + day + "/" + year);
        btnDate.setTextColor(Color.GRAY);

        btnDate.setOnClickListener(btnDateOnClickListener);

		txtAmount.requestFocus();
		
		lblSeparator1=(TextView)findViewById(R.id.lblTransactionSeparator1);
		lblSeparator2=(TextView)findViewById(R.id.lblTransactionSeparator2);
				
		extras=getIntent().getExtras();
				
		if (extras!=null)
		{
			activityMode=ActivityMode.Edit;
			
			setTitle("Edit Transaction");
			transactionId=extras.getString(Store.TRANSACTION_ID);
			
			rbIncome.setVisibility(View.INVISIBLE);
			rbExpense.setVisibility(View.INVISIBLE);
			lblInfo.setVisibility(View.VISIBLE);
			
			cboCategory.setEnabled(false);
			cboAccount.setEnabled(false);
			
			editTransaction();
		}
		else
		{
			activityMode=ActivityMode.Add;
			setTitle("Add Transaction");
			
			fillCategory(CategoryType.Expense);
			fillAccount(AccountType.All);

			transactionType=Constant.TRANSACTION_EXPENSE;
		}
	}


     private View.OnClickListener btnDateOnClickListener= new View.OnClickListener() {
         @Override
         public void onClick(View view)
         {
             showDialog(DATE_DIALOG_ID);
         }
     };
	
	private void editTransaction()
	{
		Transaction transaction=transactionRepository.getById(Integer.parseInt(transactionId));
	
		if (transaction!=null) 
		{
			
			String amount=String.valueOf(transaction.getAmount());
			txtAmount.setText(amount.replaceAll("\\.0*$", ""));

            transactionAmount=transaction.getAmount();

			txtNotes.setText(String.valueOf(transaction.getNotes()));

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            String date=transaction.getDate();

            Date d=null;
            try {
                d=dateFormat.parse(date);
            } catch (ParseException e) {
            }

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String sDate = df.format(d);

            btnDate.setText("  " + sDate);
            lblCategoryId.setText(String.valueOf(transaction.getCategoryId()));
			lblAccountId.setText(String.valueOf(transaction.getAccountId()));
            lblAccountBalance.setText(String.valueOf(accountRepository.getBalance(transaction.getAccountId())));
            lblCurrentAmount.setText(String.valueOf(transaction.getAmount()));

            lblInfo.setText(transaction.getType().toUpperCase());
            transactionType=lblInfo.getText().toString();

			if (transaction.getType().equals(Constant.TRANSACTION_TRANSFER)
                   || transaction.getType().equals(Constant.TRANSACTION_DEPOSIT)
                   || transaction.getType().equals(Constant.TRANSACTION_WITHDRAWAL))
			{
				lblSeparator1.setText("FROM ACCOUNT");
				lblSeparator2.setText("TO ACCOUNT");
				
				fillAccountForEdit(transaction,transaction.getCategoryId(),cboCategory);
                Account account=accountRepository.getById(transaction.getCategoryId());
                if (account!=null) lblFromAccountBalance.setText(String.valueOf(account.getBalance()));

                fillAccountForEdit(transaction,transaction.getAccountId(),cboAccount);
			}
			else if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
			{
				lblSeparator1.setText("CREDIT CARD");
				lblSeparator2.setText("FROM ACCOUNT");
			
				fillAccountForEdit(transaction, transaction.getAccountId(), cboCategory);
				fillAccountForEdit(transaction, transaction.getPaymentAccountId(), cboAccount);

                transactionPaymentAccountId=transaction.getPaymentAccountId();

                Account account=accountRepository.getByName(cboAccount.getSelectedItem().toString());

                lblPaymentAccountBalance.setText(String.valueOf(account.getBalance()));

			}
			else 
			{
				lblSeparator1.setText("CATEGORY");
				lblSeparator2.setText("TO ACCOUNT");
				
				fillCategoryForEdit(transaction);
				fillAccountForEdit(transaction,transaction.getAccountId(),cboAccount);
			}	
		}
	}

    private void fillCategoryArray(CategoryType categoryType)
    {
        String[] data={"From","January","February","March","April","May","June","July","August","September","October","November","December"};

        ArrayAdapter<String> dataAdapter  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        cboCategory.setAdapter(dataAdapter);
        cboCategory.setSelection(0);
    }

	
	private void fillCategory(CategoryType categoryType) 
	{
		categoryItems=categoryRepository.getSpinnerItemsByType(categoryType);


		if (categoryItems.size()>0)
		{		
			 SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, categoryItems);
			 	cboCategory.setAdapter(adapter);

                if (categoryItems.size()==1) {
                    cboCategory.setSelection(0);
                }else {
                    cboCategory.setSelection(categoryItems.size() - 1);
                }

			 	cboCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int posiiton, long id) {
					
						SpinnerItem data = categoryItems.get(posiiton);
						if (data.isHint())
						{
							 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
							 ((TextView) parent.getChildAt(0)).setTextSize(16);
						}else{
							
							Category category=categoryRepository.getByName(cboCategory.getSelectedItem().toString());
							if (category!=null) lblCategoryId.setText(String.valueOf(category.getId()));
						}
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
		
		}
	}
	
	
	private void fillCategoryForEdit(Transaction transaction)
	{				
		String[] categories=categoryRepository.getNames();
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
	    cboCategory.setAdapter(dataAdapter);
	    
	    String categoryName="";
	    Category category=categoryRepository.getById(transaction.getCategoryId());
	    if (category!=null) categoryName=category.getName();
	    
	    cboCategory.setSelection(((ArrayAdapter<String>)cboCategory.getAdapter()).getPosition((categoryName)));
   }
	
	
	
	
	private void fillAccount(AccountType accountType) 
	{
		accountItems=accountRepository.getSpinnerItemsByType(accountType);
	
		if (accountItems.size()>0)
		{		
			 SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, accountItems);
			 	cboAccount.setAdapter(adapter);
			 	cboAccount.setSelection(accountItems.size() - 1);
		     
			 	cboAccount.setOnItemSelectedListener(new OnItemSelectedListener() {
	
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int posiiton, long id) {
					
						SpinnerItem data = accountItems.get(posiiton);
						if (data.isHint())
						{
							 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
							 ((TextView) parent.getChildAt(0)).setTextSize(15);
						}else {
							Account account=accountRepository.getByName(cboAccount.getSelectedItem().toString());
							if (account!=null)
                            {
                                lblAccountId.setText(String.valueOf(account.getId()));
                                lblAccountBalance.setText(String.valueOf(account.getBalance()));
						    }
                        }
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
		
		}
	}
	
	
	
	private void fillAccountForEdit(Transaction transaction,int accountId,Spinner spinner)
	{		
		String[] accounts=accountRepository.getNames();
						
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accounts);
		
		String accountName="";
	    Account account=accountRepository.getById(accountId);
	    if (account!=null)
        {
            accountName=account.getName();
        }

	    spinner.setAdapter(dataAdapter);
	    spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition((accountName)));


	}

	
	public void onRadioButtonClick(View v) 
	{	    
		RadioButton button = (RadioButton) v;
	 
		transactionType=button.getText().toString().toUpperCase();
		
		categoryItems.clear();
			
		if (transactionType.equals(Constant.TRANSACTION_INCOME))
		{
			lblSeparator2.setText("TO ACCOUNT");
			
			fillCategory(CategoryType.Income);  
			fillAccount(AccountType.CashAndSavings);
		}
		else if (transactionType.equals(Constant.TRANSACTION_EXPENSE))
		{
			lblSeparator2.setText("FROM ACCOUNT");
						
			fillCategory(CategoryType.Expense);
			fillAccount(AccountType.All);
		}				
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
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


    private boolean isBalanceNotSufficient()
    {
         boolean isNotSufficient=false;

         float amount=Float.parseFloat(txtAmount.getText().toString());

         if (transactionType.equals(Constant.TRANSACTION_EXPENSE))
         {
            float balance=Float.parseFloat(lblAccountBalance.getText().toString());
            if (balance < amount) {
                isNotSufficient=true;
            }
         }

         return isNotSufficient;
    }


    private boolean isBalanceNotSufficientWhenEdit()
    {
        boolean isNotSufficient=false;

        float amount=Float.parseFloat(txtAmount.getText().toString());
        float currentAmount=Float.parseFloat(lblCurrentAmount.getText().toString());
        float balance=0;

        if (transactionType.equals(Constant.TRANSACTION_TRANSFER)
            || transactionType.equals(Constant.TRANSACTION_DEPOSIT)
            || transactionType.equals(Constant.TRANSACTION_WITHDRAWAL))
        {
            balance=Float.parseFloat(lblFromAccountBalance.getText().toString());
        }
        else if(transactionType.equals(Constant.TRANSACTION_PAYMENT)) {
            balance=Float.parseFloat(lblPaymentAccountBalance.getText().toString());
        }
        else
        {
            balance=Float.parseFloat(lblAccountBalance.getText().toString());
        }

        float x=amount-currentAmount;

        if (amount!=balance && x > 0)
        {
          if (x > balance) isNotSufficient=true;
        }

        return isNotSufficient;
    }


	private void saveTransaction(ActivityMode activityMode)
	{

        if (txtAmount.getText().toString().equals(""))
        {
            Toast.makeText(this, "Amount can not be empty",Toast.LENGTH_LONG).show();
        }
        else if (txtAmount.getText().toString().equals("."))
        {
            Toast.makeText(this, "Incorrect amount",Toast.LENGTH_LONG).show();
        }
        else if (cboCategory.getSelectedItem()==null)
        {
            Toast.makeText(this, "Please add category",Toast.LENGTH_LONG).show();
        }
        else if (cboAccount.getSelectedItem()==null)
        {
            Toast.makeText(this, "Please add account",Toast.LENGTH_LONG).show();
        }
        else if (cboCategory.getSelectedItem().toString().equals("Please Select"))
        {
            Toast.makeText(this, "Please select category",Toast.LENGTH_LONG).show();
        }
        else if (cboAccount.getSelectedItem().toString().equals("Please Select"))
        {
            Toast.makeText(this, "Please select account",Toast.LENGTH_LONG).show();
        }
        else
        {

            Transaction transaction=new Transaction();

            transaction.setAmount(Float.parseFloat(txtAmount.getText().toString()));
            transaction.setCategoryId(Integer.parseInt(lblCategoryId.getText().toString()));
            transaction.setAccountId(Integer.parseInt(lblAccountId.getText().toString()));

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
            }else {
                transaction.setNotes(txtNotes.getText().toString().substring(0,1).toUpperCase() + txtNotes.getText().toString().substring(1));
            }

            if (transactionType.equals(Constant.TRANSACTION_PAYMENT)){
                transaction.setPaymentAccountId(transactionPaymentAccountId);
            } else {

                transaction.setPaymentAccountId(0);
            }

            if (activityMode==ActivityMode.Add)
            {
                Account account=accountRepository.getById(Integer.parseInt(lblAccountId.getText().toString()));
                if (account!=null)
                {
                    if (account.getType().equals("Credit Card"))
                    {
                        transaction.setType(Constant.TRANSACTION_CREDIT);
                    }
                    else
                    {
                        transaction.setType(transactionType);
                    }
                }

                if (transaction.getType().equals(Constant.TRANSACTION_INCOME))
                {
                    transaction.setDescription(cboCategory.getSelectedItem().toString() + " To " + cboAccount.getSelectedItem().toString());
                }
                else if (transaction.getType().equals(Constant.TRANSACTION_EXPENSE) || transaction.getType().equals(Constant.TRANSACTION_CREDIT))
                {
                    transaction.setDescription(cboCategory.getSelectedItem().toString() + " From " + cboAccount.getSelectedItem().toString());
                }

                if (isBalanceNotSufficient())
                {
                    Toast.makeText(this, "Insufficient transaction. Please check your account balance",Toast.LENGTH_LONG).show();
                }
                else
                {
                    transactionRepository.save(transaction);
                    finish();
                }
            }
            else
            {

                transaction.setId(Integer.parseInt(transactionId));
                transaction.setType(lblInfo.getText().toString());

                if (transaction.getType().equals(Constant.TRANSACTION_TRANSFER) || transaction.getType().equals(Constant.TRANSACTION_WITHDRAWAL)
                        || transaction.getType().equals(Constant.TRANSACTION_DEPOSIT) || transaction.getType().equals(Constant.TRANSACTION_INCOME))
                {
                    transaction.setDescription(cboCategory.getSelectedItem().toString() + " To " + cboAccount.getSelectedItem().toString());
                }
                else if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT) || transaction.getType().equals(Constant.TRANSACTION_EXPENSE)
                        || transaction.getType().equals(Constant.TRANSACTION_CREDIT)|| transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
                {

                    transaction.setDescription(cboCategory.getSelectedItem().toString() + " From " + cboAccount.getSelectedItem().toString());
                }

                if (isBalanceNotSufficientWhenEdit()) {
                    Toast.makeText(this, "Insufficient transaction. Please check your account balance",Toast.LENGTH_LONG).show();
                }
                else
                {
                    transactionRepository.update(transaction);

                    finish();
                }
            }
         }

	}


    private void showPaidVersion()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        StringBuilder msg=new StringBuilder();

        msg.append("This free version limited to 30 transactions.");
        msg.append("Please buy the full version for unlimited features.\n");

        alertDialog.setTitle("Zinj");
        alertDialog.setMessage(msg.toString());

        alertDialog.setIcon(R.drawable.zlogo);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Uri marketUri = Uri.parse("market://details?id=com.xeris.zinj");
                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));

            }
        });

        alertDialog.show();
    }


	 @Override
     public boolean onOptionsItemSelected(MenuItem item) 
	 {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        	case R.id.menuitem_save:
        		saveTransaction(ActivityMode.Add);
        		break;
        	case R.id.menuitem_update :
        		saveTransaction(ActivityMode.Edit);
        		break;
        }

        return super.onOptionsItemSelected(item);
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
	
	private void makeToast(String msg)
	{
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		View view = toast.getView();
		view.setBackgroundResource(R.drawable.maroon_back);
		TextView text = (TextView) view.findViewById(android.R.id.message);
		text.setTextColor(Color.WHITE);
		toast.show();
	}


}
