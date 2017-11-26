package com.xeris.zinj.repository;

import java.util.ArrayList;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.model.Transaction.TransactionType;
import com.xeris.zinj.repository.AccountRepository.ActionType;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TransactionRepository 
{	
	private Store store;
    private AccountRepository accountRepository;

    public enum TransactionFilter
	{
		All,Income,Expense,Transfer,Withdrawal,Deposit,Payment
	}
	
	public TransactionRepository(Context ctx) 
	{
		store=new Store(ctx);
        accountRepository=new AccountRepository(ctx);
    }

	private void putValues(Transaction transaction, ContentValues values) 
	{

		values.put(Store.TRANSACTION_TYPE, transaction.getType());
		values.put(Store.TRANSACTION_DATE,transaction.getDate());
        values.put(Store.TRANSACTION_DESCRIPTION, transaction.getDescription());
		values.put(Store.TRANSACTION_ADDITIONAL_INFO, transaction.getAdditionalInfo());
		values.put(Store.TRANSACTION_AMOUNT, transaction.getAmount());
		values.put(Store.TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
		values.put(Store.TRANSACTION_ACCOUNT_ID, transaction.getAccountId());
		values.put(Store.TRANSACTION_NOTES, transaction.getNotes());
		values.put(Store.TRANSACTION_PAYMENT_ACCOUNT_ID, transaction.getPaymentAccountId());
        values.put(Store.TRANSACTION_PAYMENT_INFO, transaction.getPaymentInfo());
    }

    private void putUpdateValues(Transaction transaction, ContentValues values)
    {

        values.put(Store.TRANSACTION_TYPE, transaction.getType());
        values.put(Store.TRANSACTION_DATE,transaction.getDate());
        values.put(Store.TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(Store.TRANSACTION_ADDITIONAL_INFO, transaction.getAdditionalInfo());
        values.put(Store.TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(Store.TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
        values.put(Store.TRANSACTION_ACCOUNT_ID, transaction.getAccountId());
        values.put(Store.TRANSACTION_NOTES, transaction.getNotes());
        values.put(Store.TRANSACTION_PAYMENT_ACCOUNT_ID, transaction.getPaymentAccountId());
    }



    private Transaction mapTransaction(Cursor cursor)
	{	
		Transaction transaction=new Transaction();
	
		transaction.setId(cursor.getInt(0));
		transaction.setType(cursor.getString(1));
        transaction.setDate(cursor.getString(2));
        transaction.setDescription(cursor.getString(3));
		transaction.setAdditionalInfo(cursor.getString(4));
		transaction.setAmount(cursor.getFloat(5));
		transaction.setCategoryId(cursor.getInt(6));
		transaction.setNotes(cursor.getString(7));
		transaction.setAccountId(cursor.getInt(8));
		transaction.setPaymentAccountId(cursor.getInt(9));
        transaction.setPaymentInfo(cursor.getString(10));
		
		return transaction;
	}


    public Transaction getById(int id)
    {
        Transaction transaction=null;

        SQLiteDatabase db=store.getReadableDatabase();
        transaction=getById(id,db);

        return transaction;
    }



    private Transaction getById(int id,SQLiteDatabase db)
	{
		Transaction transaction=null;
		
		String sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE _id = " +  id;
		
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
			transaction = mapTransaction(cursor);
		}				
		
		return transaction;
	}


    public ArrayList<Transaction> getCashFlowTransactionQueryByMonth(int month,int year,String query)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();

        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql="";

        if(query.equals("Income"))
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE _type IN ('INCOME') AND strftime('%m',_date)='" + strMonth
              + "' AND strftime('%Y',_date)='" + year + "' ORDER BY _date DESC,_id DESC";
        }
        else if(query.equals("Expense"))
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth
              + "' AND strftime('%Y',_date)='" + year + "' ORDER BY _date DESC,_id DESC";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Transaction transaction = mapTransaction(cursor);
                list.add(transaction);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;

    }


    public ArrayList<Transaction> getTransactionQueryByMonth(int month,int year,String query)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();

        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'"
              + " AND (_description LIKE '%" +  query + "%' OR _payment_info LIKE '%" +  query + "%') ORDER BY _date DESC,_id DESC";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Transaction transaction = mapTransaction(cursor);
                list.add(transaction);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;

    }


    public ArrayList<Transaction> getCashFlowTransactionQueryByYear(CategoryType type,String month,int year)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();

        int m=Store.getMonthInt(month);

        String strMonth="";

        if (m < 10 ) {
            strMonth="0" + m;
        } else {
            strMonth=String.valueOf(m);
        }

        String sql="";

        if(type==CategoryType.Income)
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('INCOME') AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'" +
                    " ORDER BY _date DESC,_id DESC";
        }
        else if(type==CategoryType.Expense)
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'" +
                    " ORDER BY _date DESC,_id DESC";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Transaction transaction = mapTransaction(cursor);
                list.add(transaction);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;

    }


    public ArrayList<Transaction> getTransactionQueryByYear(CategoryType type,String month,int year,int categoryId)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();

        int m=Store.getMonthInt(month);

        String strMonth="";

        if (m < 10 ) {
            strMonth="0" + m;
        } else {
            strMonth=String.valueOf(m);
        }

        String sql="";

        if(type==CategoryType.Income)
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type IN ('INCOME') AND _category_id=" + categoryId +
               " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'" +
               " ORDER BY _date DESC,_id DESC";
        }
        else if(type==CategoryType.Expense)
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type IN ('EXPENSE','PAYMENT') AND _category_id=" + categoryId +
               " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'" +
               " ORDER BY _date DESC,_id DESC";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Transaction transaction = mapTransaction(cursor);
                list.add(transaction);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;

    }



    public float getFilterAmount(String filter,String clause)
    {
        float total=0;

        String sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type ='" + filter.toUpperCase() + "' AND " + clause;

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                total=total+cursor.getFloat(0);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return total;

    }


    public ArrayList<Transaction> getTransactionList(TransactionFilter filter,int month,int year)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();

        String sql="";
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        if (filter==TransactionFilter.All)
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'"
              + " ORDER BY _date DESC,_id DESC";
        }
        else
        {
            sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE _type='" + filter.toString().toUpperCase() + "'"
              + " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "' ORDER BY _date DESC,_id DESC";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Transaction transaction = mapTransaction(cursor);
                list.add(transaction);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }


    public int getTransactionCount()
    {
        int total=0;

        String sql="SELECT COUNT(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE;

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                total=total+cursor.getInt(0);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return total;

    }


    public ArrayList<Transaction> search(int month,int year,String clause)
    {
        ArrayList<Transaction> list=new ArrayList<Transaction>();


        try {
            String sql = "SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE " + clause + " ORDER BY _date DESC,_id DESC";

            SQLiteDatabase db = store.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = mapTransaction(cursor);
                    list.add(transaction);
                }
                while (cursor.moveToNext());
            }

            db.close();
        }
        catch(Exception ex) {
            throw ex;
        }
        return list;
    }


	
	public void save(Transaction transaction)
	{		
		SQLiteDatabase db=null;
		
		try
		{
			db=store.getWritableDatabase();
			
			db.beginTransaction();
			
			ContentValues values = new ContentValues();
			putValues(transaction, values);
		
			db.insert(Store.TRANSACTION_TABLE, null, values);
			
			if (transaction.getType().equals(Constant.TRANSACTION_TRANSFER)
                    || transaction.getType().equals(Constant.TRANSACTION_WITHDRAWAL)
                    || transaction.getType().equals(Constant.TRANSACTION_DEPOSIT)
                    || transaction.getType().equals(Constant.TRANSACTION_TRANSFER_CASH)
                    || transaction.getType().equals(Constant.TRANSACTION_TRANSFER_AS_SAVINGS))
            {
                float fromAccountBalance = accountRepository.getBalance(transaction.getCategoryId(),db);
                float toAccountBalance = accountRepository.getBalance(transaction.getAccountId(),db);

                accountRepository.updateBalance(transaction.getCategoryId(), fromAccountBalance - transaction.getAmount(),db);
                accountRepository.updateBalance(transaction.getAccountId(), toAccountBalance + transaction.getAmount(),db);
            }
            else if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
            {
                float creditCardBalance = accountRepository.getBalance(transaction.getAccountId(),db);
                float paymentAccountBalance = accountRepository.getBalance(transaction.getPaymentAccountId(),db);

                accountRepository.updateBalance(transaction.getAccountId(), creditCardBalance + transaction.getAmount(),db);
                accountRepository.updateBalance(transaction.getPaymentAccountId(), paymentAccountBalance - transaction.getAmount(),db);
            }
            else if (transaction.getType().equals(Constant.TRANSACTION_INCOME)
                || transaction.getType().equals(Constant.TRANSACTION_EXPENSE)
                || transaction.getType().equals(Constant.TRANSACTION_CREDIT))
            {
                float currentBalance = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), transaction.getAmount(),
                    transaction.getAmount(), ActionType.Add,db).getCurrentBalance2();

                accountRepository.updateBalance(transaction.getAccountId(), currentBalance,db);
            }
						
			db.setTransactionSuccessful();
			
	
		}
		catch(Exception ex)
		{
			
		}
		finally
		{			
			db.endTransaction();
			db.close();
		}
	}
	
	
	

	public void update(Transaction transaction)
	{
		SQLiteDatabase db=null;
		try
		{
		
			db=store.getWritableDatabase();
			db.beginTransaction();

            Transaction savedTransaction = getById(transaction.getId(),db);

            ContentValues values = new ContentValues();
            putUpdateValues(transaction, values);

			db.update(Store.TRANSACTION_TABLE, values, "_id=?", new String[] {String.valueOf(transaction.getId())});
		
	        if (transaction.getType().equals(Constant.TRANSACTION_INCOME) || transaction.getType().equals(Constant.TRANSACTION_EXPENSE)
	              || transaction.getType().equals(TransactionType.Credit.toString().toUpperCase()))
	        {
	              float currentBalance = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), savedTransaction.getAmount(),
	                  transaction.getAmount(), ActionType.Edit,db).getCurrentBalance2();
	
	              accountRepository.updateBalance(transaction.getAccountId(), currentBalance,db);
	        }
	        else if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
	        {
	              float currentBalance1 = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), savedTransaction.getAmount(),
	                      transaction.getAmount(), ActionType.Edit,db).getCurrentBalance1();
	
	              accountRepository.updateBalance(transaction.getAccountId(), currentBalance1,db);
	
	              float currentBalance2 = accountRepository.getBalanceAfter(transaction.getPaymentAccountId(), transaction.getType(), savedTransaction.getAmount(),
	                      transaction.getAmount(), ActionType.Edit,db).getCurrentBalance2();
	
	              accountRepository.updateBalance(transaction.getPaymentAccountId(), currentBalance2,db);
	        }
	        else if (transaction.getType().equals(Constant.TRANSACTION_TRANSFER)
                    || transaction.getType().equals(Constant.TRANSACTION_WITHDRAWAL)
                    || transaction.getType().equals(Constant.TRANSACTION_DEPOSIT))
            {
	              float currentBalance1 = accountRepository.getBalanceAfter(transaction.getCategoryId(), transaction.getType(), savedTransaction.getAmount(),
	                      transaction.getAmount(), ActionType.Edit,db).getCurrentBalance1();
	
	              accountRepository.updateBalance(transaction.getCategoryId(), currentBalance1,db);
	
	              float currentBalance2 = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), savedTransaction.getAmount(),
	                      transaction.getAmount(), ActionType.Edit,db).getCurrentBalance2();
	
	              accountRepository.updateBalance(transaction.getAccountId(), currentBalance2,db);
	        }
             
			db.setTransactionSuccessful();
			
		}
		catch(Exception ex)
		{
			
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
	}
	
	
	public void delete(Transaction transaction)
	{	
		SQLiteDatabase db=null;
		
		try
		{
			db=store.getWritableDatabase();
			
			db.beginTransaction();
			
			db.delete(Store.TRANSACTION_TABLE, "_id=?", new String[] {String.valueOf(transaction.getId())});
		
			if (transaction.getType().equals(Constant.TRANSACTION_INCOME) || transaction.getType().equals(Constant.TRANSACTION_EXPENSE)
		       || transaction.getType().equals(Constant.TRANSACTION_CREDIT))
            {
                float currentBalance = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), transaction.getAmount(),
                    transaction.getAmount(), ActionType.Delete,db).getCurrentBalance2();

                accountRepository.updateBalance(transaction.getAccountId(), currentBalance,db);
            }
            else if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
            {
                float currentBalance1 = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), transaction.getAmount(),
                     transaction.getAmount(), ActionType.Delete,db).getCurrentBalance1();

                accountRepository.updateBalance(transaction.getAccountId(), currentBalance1,db);

                float currentBalance2 = accountRepository.getBalanceAfter(transaction.getPaymentAccountId(), transaction.getType(), transaction.getAmount(),
                        transaction.getAmount(), ActionType.Delete,db).getCurrentBalance2();

                accountRepository.updateBalance(transaction.getPaymentAccountId(), currentBalance2,db);
            }
            else if (transaction.getType().equals(Constant.TRANSACTION_TRANSFER)
                    || transaction.getType().equals(Constant.TRANSACTION_WITHDRAWAL)
                    || transaction.getType().equals(Constant.TRANSACTION_DEPOSIT))
            {
                float currentBalance1 = accountRepository.getBalanceAfter(transaction.getCategoryId(), transaction.getType(), transaction.getAmount(),
                        transaction.getAmount(), ActionType.Delete,db).getCurrentBalance1();

                accountRepository.updateBalance(transaction.getCategoryId(), currentBalance1,db);

                float currentBalance2 = accountRepository.getBalanceAfter(transaction.getAccountId(), transaction.getType(), transaction.getAmount(),
                        transaction.getAmount(), ActionType.Delete,db).getCurrentBalance2();

                accountRepository.updateBalance(transaction.getAccountId(), currentBalance2,db);
            }
		 
			
			db.setTransactionSuccessful();
			
		}catch(Exception ex) {
			
		}finally{
			db.endTransaction();
			db.close();
		}
		
		
	}
	
	
	

}
