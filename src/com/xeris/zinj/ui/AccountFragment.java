package com.xeris.zinj.ui;

import com.xeris.zinj.R;
import com.xeris.zinj.adapter.AccountAdapter;
import com.xeris.zinj.adapter.CategoryAdapter;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.repository.AccountRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.Store;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AccountFragment extends Fragment
{
	private Activity context;
	 
	private String[] items;
      
    private String accountName;
    private String accountId;
    private String type;
    private ListView lvwAccount;
    private String transactionType="";
    
    private AccountRepository accountRepository;
    
	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	      setHasOptionsMenu(true);
	     
		  accountRepository=new AccountRepository(context);
		  View rootView =inflater.inflate(R.layout.account_fragment, container, false);
		  
		  int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		  TextView title = (TextView)context.findViewById(titleId);
		  title.setTextColor(Color.WHITE); 
		  title.setText("Accounts");
		  
		  return rootView;
	 }
	 
	  
	  @Override
	  public void onResume()
	  {
		  super.onResume();
		  onDataLoaded();

          if (!transactionType.equals(""))
          {
              context.getActionBar().setSelectedNavigationItem(0);
              transactionType="";
          }

	  }
	  
	  
	  public OnItemLongClickListener listviewOnItemLongClickListener=new OnItemLongClickListener() 
	  {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id) 
			{
		    	accountId=(String)((TextView)v.findViewById(R.id.lblAccountId)).getText();
		    	accountName = (String)((TextView) v.findViewById(R.id.lblAccountName)).getText();
		    	type= (String)((TextView) v.findViewById(R.id.lblAccountType)).getText(); 
		    	
		    	if (type.equals("Savings"))
		    	{		    		
		    		items=new String[]{"Edit","Delete","Transfer","Transfer As Savings","Withdrawal"};
		    	}
		    	else if (type.equals("Cash"))
		    	{
		    		items=new String[]{"Edit","Delete","Deposit","Transfer","Transfer As Savings"};
			    }
		    	else if (type.equals("Credit Card"))
		    	{
		     		items=new String[]{"Edit","Delete","Payment"};
				}
		    	
		    	AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
		    	dialog.setTitle(accountName);
		                                        
		    	dialog.setItems(items, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		            	
		            	switch(item)
		            	{
		            		case 0 :
		            			showActivity("",accountId, AccountActivity.class);
		            			break;
		            		case 1 :
		            			deleteAccount();
		            			break;
		            	
		            		case 2 :
		            			
		            			if (type.equals("Credit Card"))
		            			{
		            				showActivity(Constant.TRANSACTION_PAYMENT,accountId,PaymentActivity.class);
		                        }
		            			else if (type.equals("Savings"))
		                        {
		            				showActivity(Constant.TRANSACTION_TRANSFER,accountId, TransferActivity.class);	
		                        }
		            			else if (type.equals("Cash"))
		            			{
		            				showActivity(Constant.TRANSACTION_DEPOSIT,accountId, TransferActivity.class);	
			                    }
		            		
		            			break;

                            case 3 :

                                if (type.equals("Cash"))
                                {
                                    showActivity(Constant.TRANSACTION_TRANSFER_CASH,accountId, TransferActivity.class);
                                }
                                else if (type.equals("Savings"))
                                {
                                    showActivity(Constant.TRANSACTION_TRANSFER_AS_SAVINGS,accountId, TransferActivity.class);
                                }

                                break;

		            		case 4 :

                                if (type.equals("Cash"))
                                {
                                    showActivity(Constant.TRANSACTION_TRANSFER_AS_SAVINGS,accountId, TransferActivity.class);
                                }
		            			else if (type.equals("Savings"))
		            			{
		            				showActivity(Constant.TRANSACTION_WITHDRAWAL,accountId, TransferActivity.class);	
		            			}
		            	}
		            	
		            }
		        });
		        
		        AlertDialog alert = dialog.create();
		        alert.show();
		        
		    	return true;
			}
	};
	
	
	  	 
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
		 super.onActivityCreated(savedInstanceState);
		 onDataLoaded();
		 
		 lvwAccount.setOnItemLongClickListener(listviewOnItemLongClickListener);
	 }
	 

	 private void showActivity(String type,String accountId,Class<?> cls)
	{
		Bundle extras=new Bundle();
		
		extras.putString(Store.TRANSACTION_TYPE, type);
    	extras.putString(Store.ACCOUNT_ID, accountId);
    	
    	Intent i=new Intent(context,cls);
    	i.putExtras(extras);
    	
    	startActivityForResult(i, 1);
	 }
	
		
	  @Override
	  public void onAttach(Activity activity)
	  {
		  	context=activity;
	        super.onAttach(activity);
	  }

	  
	  public void onDataLoaded() 
	  {
          ArrayList<Account> data=accountRepository.getAccountList();
          AccountAdapter dataAdapter=new AccountAdapter(context, R.layout.account_template, data);
		  lvwAccount = (ListView) context.findViewById(R.id.lvwAccount);
			
		  lvwAccount.setAdapter(dataAdapter);
	}
	  

	  @Override
	  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	  {   	
		   inflater.inflate(R.menu.list_menu, menu);
	  }
	
	  
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menuitem_add:
	            	startActivity(new Intent(context,AccountActivity.class));
	            	break;
	         }

	        return super.onOptionsItemSelected(item);
	    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    transactionType=data.getStringExtra("type");
                }
                break;
            }
        }
    }

	  
	  
	  private void deleteAccount()
	  {
			if (!accountRepository.isAccountUsedByTransaction(Integer.parseInt(accountId)))
			{		
				new AlertDialog.Builder(context)
				.setIcon(R.drawable.trash)
				.setTitle("Delete")
				.setMessage("Are you sure want to delete '" + accountName + "'?")
			    .setPositiveButton("Yes",
		                            new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog,
		                                int whichButton) 
		          {
		        	  accountRepository.delete(Integer.parseInt(accountId));
		        	  onDataLoaded();
		          }
		        })
		        .setNegativeButton("No",
		                            new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog,
		                                int whichButton) {
		          }
		        })
		        .show();
			}
			else
			{
                for (int i=0; i < 2; i++)
                {
				    Toast.makeText(context, "You can't delete this account.'" + accountName +
                          "' has already used by transaction.\n\nPlease delete associated transaction",Toast.LENGTH_LONG).show();
			    }
            }
	  }
	  

}
