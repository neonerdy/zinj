package com.xeris.zinj.repository;


import java.util.ArrayList;
import java.util.List;

import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountRepository 
{
	private Store store;

	public enum ActionType
    {
        Add,Edit,Delete
    }
	
	public enum AccountType
	{
		All,
		Cash,
		Savings,
		CreditCard,
		CashAndSavings
	}
	
	public AccountRepository(Context ctx) 
	{
		store=new Store(ctx);
	}

    private void putValues(Account account, ContentValues values)
    {
        values.put(Store.ACCOUNT_NAME, account.getName());
        values.put(Store.ACCOUNT_TYPE, account.getType());
        values.put(Store.ACCOUNT_BALANCE, account.getBalance());
        values.put(Store.ACCOUNT_ORDER, account.getOrder());
        values.put(Store.ACCOUNT_NOTES, account.getNotes());
        values.put(Store.ACCOUNT_LIMIT, account.getLimit());
        values.put(Store.ACCOUNT_FINANCIAL_GOAL_NAME, account.getFinancialGoalName());
        values.put(Store.ACCOUNT_FINANCIAL_GOAL_TARGET, account.getFinancialGoalTarget());
        values.put(Store.ACCOUNT_IS_ACTIVE,account.getIsActive());
    }

	private Account mapAccount(Cursor cursor)
	{
		Account account=new Account();

        account.setId(cursor.getInt(0));
        account.setName(cursor.getString(1));
        account.setType(cursor.getString(2));
        account.setBalance(cursor.getFloat(3));
        account.setBalance(cursor.getFloat(3));
        account.setOrder(cursor.getInt(4));
        account.setNotes(cursor.getString(5));
        account.setLimit(cursor.getFloat(6));
        account.setFinancialGoalName(cursor.getString(7));
        account.setFinancialGoalTarget(cursor.getFloat(8));
        account.setIsActive(cursor.getInt(9));

     	return account;
	}


	public boolean isAccountUsedByTransaction(int accountId)
	{
		boolean isUsed=false;
		
		String sql="SELECT * FROM " + Store.TRANSACTION_TABLE +
				" WHERE " + Store.TRANSACTION_ACCOUNT_ID + " = " +  accountId;
		
		SQLiteDatabase db=store.getReadableDatabase();
		
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			if (cursor.moveToFirst()) 
				isUsed=true;
		}
		
		db.close();
		
		return isUsed;
	}
	
	
	public boolean isAccountExist(String name)
	{
		boolean isExist=false;
		
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE + " WHERE UPPER(" + Store.ACCOUNT_NAME + ")='" + name.toUpperCase() + "'";
		
		SQLiteDatabase db=store.getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			if (cursor.moveToFirst()) 
				isExist=true;
		}
		
		db.close();
		
		return isExist;
	}


    public float getBalance(int accountId)
    {
        float balance=0;

        SQLiteDatabase db=store.getReadableDatabase();
        balance=getBalance(accountId,db);

        return balance;
    }

    public float getBalance(int accountId,SQLiteDatabase db)
    {
        float balance=0;

        String sql="SELECT * FROM " + Store.ACCOUNT_TABLE + " WHERE " + Store.ACCOUNT_ID + "=" + accountId;

        Cursor cursor=db.rawQuery(sql, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
                balance=cursor.getFloat(3);
        }

        return balance;
    }
	
	public Account getById(int id)
	{
		Account account=null;
		
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE +
				" WHERE " + Store.ACCOUNT_ID + " = " +  id;
		
		SQLiteDatabase db=store.getReadableDatabase();
						
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
			account = mapAccount(cursor);
		}				
		
		db.close();
		
		return account;
	}

	
	public Account getByName(String name)
	{		
		Account account=null;
		
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE +
				" WHERE " + Store.ACCOUNT_NAME + " = '" +  name + "'";
			
		SQLiteDatabase db=store.getReadableDatabase();
		
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
			account = mapAccount(cursor);
		}	
		
		db.close();
		
		return account;
	}
	
	
	
	public String[] getNames()
	{		
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE;
		
		SQLiteDatabase db=store.getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);
		
		String[] names= new String[cursor.getCount()];
		
		if (cursor.moveToFirst())
		{
			int i=0;
			do
			{
				names[i]=cursor.getString(1);
				i++;
				
			}while (cursor.moveToNext());
		}		

		db.close();
		
		return names;
	}


	public ArrayList<Account> getAccountList()
	{
		ArrayList<Account> accountList=new ArrayList<Account>();
		
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE + " ORDER BY _order,_name";
		
		SQLiteDatabase db=store.getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do{
		       	Account account = mapAccount(cursor);
		       	accountList.add(account);
			}
			while(cursor.moveToNext());
		}

        cursor.close();
		db.close();

        return accountList;
	}
	
	
		
	public List<SpinnerItem> getSpinnerItemsByType(AccountType accountType)
	{
		List<SpinnerItem> items=new ArrayList<SpinnerItem>();
		
		String condition="";
		
		if (accountType==AccountType.Cash)
		{
			condition="WHERE " + Store.ACCOUNT_TYPE + " IN ('Cash')";
		}
		else if(accountType==AccountType.Savings)
		{
			condition="WHERE " + Store.ACCOUNT_TYPE + " IN ('Savings')";
		}
		else if(accountType==AccountType.CreditCard)
		{
			condition="WHERE " + Store.ACCOUNT_TYPE + " IN ('Credit Card')";
		}
		else if(accountType==AccountType.CashAndSavings)
		{
			condition="WHERE " + Store.ACCOUNT_TYPE + " IN ('Cash','Savings')";
		}
		else if(accountType==AccountType.All)
		{
			condition="";
		}
	
		String sql="SELECT * FROM " + Store.ACCOUNT_TABLE + " " + condition + " ORDER BY _order,_name";
				
		SQLiteDatabase db=store.getReadableDatabase();
		
		Cursor cursor=db.rawQuery(sql, null);
		
		if (cursor.moveToFirst())
		{
			do
			{
				SpinnerItem item = new SpinnerItem(cursor.getString(1),false);
				items.add(item);
			}
			while (cursor.moveToNext());
			items.add(new SpinnerItem("Please Select", true));
		}else {
			items.add(new SpinnerItem("Please Select", true));
		}
		
		db.close();
		
		return items;
	}
	
	public void save(Account account)
	{		
		SQLiteDatabase db=store.getWritableDatabase();
		
		ContentValues values = new ContentValues();
        putValues(account, values);
			
		db.insert(Store.ACCOUNT_TABLE, null, values);
		
		db.close();
	
	}


    public void update(Account account)
	{
		SQLiteDatabase db=store.getWritableDatabase();
		
		ContentValues values = new ContentValues();
        putValues(account, values);

		db.update(Store.ACCOUNT_TABLE, values, "_id=?", new String[] {String.valueOf(account.getId())});
	
		db.close();
	}
	
	
	public void delete(int id)
	{		
		SQLiteDatabase db=store.getWritableDatabase();
		db.delete(Store.ACCOUNT_TABLE, "_id=?", new String[] {String.valueOf(id)});
	
		db.close();
	}
	
	

	public AccountBalance getBalanceAfter(int accountId, String transactionType, float currentAmount, float changedAmount, ActionType actionType,SQLiteDatabase db)
	{
		 AccountBalance accountBalance = new AccountBalance();
         float balance = getBalance(accountId,db);


         if (transactionType.equals(Constant.TRANSACTION_INCOME))
         {
             if (actionType == ActionType.Add)
             {
                 accountBalance.setCurrentBalance2(balance + changedAmount);
             }
             else if (actionType == ActionType.Edit)
             {
                 accountBalance.setCurrentBalance2((balance - currentAmount) + changedAmount);
             }
             else if (actionType == ActionType.Delete)
             {
                 accountBalance.setCurrentBalance2(balance - currentAmount);
             }
         }
         else if (transactionType.equals(Constant.TRANSACTION_EXPENSE) || transactionType.equals(Constant.TRANSACTION_CREDIT))
         {
             if (actionType == ActionType.Add)
             {
                 accountBalance.setCurrentBalance2(balance - changedAmount);
             }
             else if (actionType == ActionType.Edit)
             {
                 accountBalance.setCurrentBalance2((currentAmount - changedAmount) + balance);
             }
             else if (actionType == ActionType.Delete)
             {
                 accountBalance.setCurrentBalance2(balance + currentAmount);
             }
         }
         else if (transactionType.equals(Constant.TRANSACTION_TRANSFER)
             || transactionType.equals(Constant.TRANSACTION_WITHDRAWAL)
             || transactionType.equals(Constant.TRANSACTION_DEPOSIT))
             {
                 if (actionType == ActionType.Edit)
                 {
                     //from account
                     accountBalance.setCurrentBalance1(balance - (changedAmount - currentAmount));

                     //to account
                     accountBalance.setCurrentBalance2(balance + (changedAmount - currentAmount));
                 }
                 else if (actionType == ActionType.Delete)
                 {
                     //from account
                     accountBalance.setCurrentBalance1(balance + currentAmount);

                     //to account
                     accountBalance.setCurrentBalance2(balance - currentAmount);
                 }
             }
         else if (transactionType.equals(Constant.TRANSACTION_PAYMENT))
         {
             if (actionType == ActionType.Edit)
             {
                 //credit card account
                 accountBalance.setCurrentBalance1((balance - currentAmount) + changedAmount);

                 //bank account
                 accountBalance.setCurrentBalance2((currentAmount - changedAmount) + balance);

             }
             else if (actionType == ActionType.Delete)
             {
                 //credit card account
                 accountBalance.setCurrentBalance1(balance - currentAmount);

                 //bank account
                 accountBalance.setCurrentBalance2(balance + currentAmount);
             }
         }

         
         return accountBalance;
         
	}

	public void updateBalance(int accountId,float currentBalance,SQLiteDatabase inDb)
	{
		String sql="UPDATE " + Store.ACCOUNT_TABLE + " SET " + Store.ACCOUNT_BALANCE + "="
				 + currentBalance + " WHERE " + Store.ACCOUNT_ID + "=" + accountId;
				
		inDb.execSQL(sql);
	}
	

}


class AccountBalance
{
	private float currentBalance1;
	private float currentBalance2;
		
	public float getCurrentBalance1() {
		return currentBalance1;
	}
	public void setCurrentBalance1(float currentBalance1) {
		this.currentBalance1 = currentBalance1;
	}
	
	public float getCurrentBalance2() {
		return currentBalance2;
	}
	public void setCurrentBalance2(float currentBalance2) {
		this.currentBalance2 = currentBalance2;
	}
}


