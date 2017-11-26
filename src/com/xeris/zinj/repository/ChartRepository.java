package com.xeris.zinj.repository;

import java.util.ArrayList;
import com.xeris.zinj.model.*;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChartRepository
{

	private Store store;

	public enum ChartType
	{
		Income,Expense
	}

    public enum CategoryOrderBy
    {
        Category,Amount
    }

    public enum AccountOrderBy
    {
        Account,Balance
    }

	
	public ChartRepository(Context ctx) 
	{
		store=new Store(ctx);
    }

	private Chart mapChart(Cursor cursor)
	{		
		Chart chart=new Chart();
		
		chart.setId(cursor.getInt(0));
		chart.setCategoryName(cursor.getString(1));
        chart.setCategoryGroup(cursor.getString(2));
        chart.setType(cursor.getString(3));
		chart.setAmount(cursor.getFloat(4));
		
		return chart;
	}


    private CashFlow mapCashFlowByYear(Cursor cursor)
    {
        CashFlow cashFlow=new CashFlow();

        cashFlow.setType(cursor.getString(0));
        cashFlow.setMonth(cursor.getString(1));
        cashFlow.setAmount(cursor.getFloat(2));

        return cashFlow;
    }


    private Chart mapChartByYear(Cursor cursor)
    {
        Chart chart=new Chart();

        chart.setId(cursor.getInt(0));
        chart.setType(cursor.getString(1));
        chart.setMonth(cursor.getString(2));
        chart.setAmount(cursor.getFloat(3));

        return chart;
    }


    private Budget mapBudget(Cursor cursor)
    {
        Budget budget=new Budget();

        budget.setCategoryId(cursor.getInt(0));
        budget.setCategoryName(cursor.getString(1));
        budget.setBudgeted(cursor.getFloat(2));

        return budget;
     }

    private Budget mapBudgetByYear(Cursor cursor)
    {
        Budget budget=new Budget();

        budget.setCategoryId(cursor.getInt(0));
        budget.setMonth(cursor.getString(1));
        budget.setBudgeted(cursor.getFloat(2));

        return budget;

    }



    private Account mapAccount(Cursor cursor)
    {
        Account account=new Account();

        account.setId(cursor.getInt(0));
        account.setName(cursor.getString(1));
        account.setType(cursor.getString(2));
        account.setBalance(cursor.getFloat(3));

        return account;
    }

    private CreditCard mapCreditCard(Cursor cursor)
    {
        CreditCard creditCard=new CreditCard();

        creditCard.setId(cursor.getInt(0));
        creditCard.setName(cursor.getString(1));
        creditCard.setBalance(cursor.getFloat(2));
        creditCard.setLimit(cursor.getFloat(3));

        return creditCard;
    }


    private Payment mapCreditCardPayment(Cursor cursor)
    {
        Payment creditCardPayment=new Payment();

        creditCardPayment.setCategoryId(cursor.getInt(0));
        creditCardPayment.setCategoryName(cursor.getString(1));
        creditCardPayment.setCreditCardName(cursor.getString(2));
        creditCardPayment.setPaymentAmount(cursor.getFloat(3));

        return creditCardPayment;
    }


    private FinancialGoal mapFinancialGoal(Cursor cursor)
    {
        FinancialGoal financialGoal=new FinancialGoal();

        financialGoal.setAccountId(cursor.getInt(0));
        financialGoal.setAccountName(cursor.getString(1));
        financialGoal.setBalance(cursor.getFloat(2));
        financialGoal.setGoalName(cursor.getString(3));
        financialGoal.setGoalTarget(cursor.getFloat(4));

        return financialGoal;
    }


    //for Statistics

   public ArrayList<Float> getAmountData(CategoryType type,int year)
   {
       ArrayList<Float> amounts=new ArrayList<Float>();
       String sql="";

       if (type==CategoryType.Income)
       {
           sql="SELECT t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                   + "WHERE t._type IN('INCOME') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";

       }
       else if (type==CategoryType.Expense)
       {
           sql="SELECT t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                   + "WHERE t._type IN('EXPENSE','PAYMENT') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";
       }

       SQLiteDatabase db=store.getReadableDatabase();
       Cursor cursor=db.rawQuery(sql, null);

       if(cursor.moveToFirst())
       {
           do{
               amounts.add(cursor.getFloat(2));
           }
           while(cursor.moveToNext());
       }

       db.close();

       return amounts;

   }


    public ArrayList<Chart> getChartList(CategoryType type,int month,int year,CategoryOrderBy orderBy)
    {
        ArrayList<Chart> list=new ArrayList<Chart>();

        String sql="";
        String strMonth="";
        String order="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        if (orderBy==CategoryOrderBy.Category) {
            order="ORDER BY c._name ASC";
        }else if (orderBy==CategoryOrderBy.Amount) {
            order="ORDER BY SumOfAmount DESC";
        }

        if (type==CategoryType.Income)
        {
            sql="SELECT c._id,c._name AS CategoryName,c._group,t._type,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                + "INNER JOIN Categories c ON t._category_id=c._id "
                + "WHERE t._type IN ('INCOME') AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "' "
                + "GROUP BY c._name " + order;
         }
        else if (type==CategoryType.Expense)
        {
            sql="SELECT c._id,c._name AS CategoryName,c._group,t._type,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                + "INNER JOIN Categories c ON t._category_id=c._id "
                + "WHERE t._type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "' "
                + "GROUP BY c._name " + order;
        }
        else if (type==CategoryType.Debt)
        {
            sql="SELECT c._id,c._name AS CategoryName,c._group,t._type,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                + "INNER JOIN Categories c ON t._category_id=c._id "
                + "WHERE (t._type IN ('PAYMENT') OR c._group IN ('Debt/Payable')) AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "' "
                + "GROUP BY c._name " + order;
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Chart chart = mapChart(cursor);
                list.add(chart);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }



    public ArrayList<Chart> getChartByTodayList(CategoryType type,CategoryOrderBy orderBy)
    {
        ArrayList<Chart> list=new ArrayList<Chart>();

        String sql="";
        String order="";

        if (orderBy==CategoryOrderBy.Category) {
            order="ORDER BY c._name ASC";
        }else if (orderBy==CategoryOrderBy.Amount) {
            order="ORDER BY SumOfAmount DESC";
        }

        if (type==CategoryType.Income)
        {
            sql="SELECT c._id,c._name AS CategoryName,c._group,t._type,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                    + "INNER JOIN Categories c ON t._category_id=c._id "
                    + "WHERE t._type IN ('INCOME') AND t._date=CURRENT_DATE "
                    + "GROUP BY c._name " + order;
        }
        else if (type==CategoryType.Expense)
        {
            sql="SELECT c._id,c._name AS CategoryName,c._group,t._type,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                    + "INNER JOIN Categories c ON t._category_id=c._id "
                    + "WHERE t._type IN ('EXPENSE','PAYMENT') AND t._date=CURRENT_DATE "
                    + "GROUP BY c._name " + order;
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Chart chart = mapChart(cursor);
                list.add(chart);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }


    public ArrayList<CashFlow> getCashFlowByYearList(CategoryType type,int year)
    {
        ArrayList<CashFlow> list=new ArrayList<CashFlow>();

        String sql="";

        if (type==CategoryType.Income)
        {
            sql="SELECT t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                    + "WHERE t._type IN('INCOME') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";

        }
        else if (type==CategoryType.Expense)
        {
            sql="SELECT t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                    + "WHERE t._type IN('EXPENSE','PAYMENT') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                CashFlow cashFlow = mapCashFlowByYear(cursor);
                list.add(cashFlow);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }



    public ArrayList<Chart> getChartByYearList(CategoryType type,int categoryId,int year)
    {
        ArrayList<Chart> list=new ArrayList<Chart>();

        String sql="";

        if (type==CategoryType.Income)
        {
            sql="SELECT t._category_id,t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                + "WHERE t._category_id =" + categoryId + " AND t._type IN('INCOME') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";

        }
        else if (type==CategoryType.Expense)
        {
            sql="SELECT t._category_id,t._type,strftime('%m',_date) AS month,SUM(t._amount) AS SumOfAmount FROM Transactions t "
                    + "WHERE t._category_id =" + categoryId + " AND t._type IN('EXPENSE','PAYMENT') AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";
        }

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Chart chart = mapChartByYear(cursor);
                list.add(chart);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }



    public float getTotalAmountByYear(CategoryType type,int categoryId,int year)
    {
        float total=0;

        String sql="";

        if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _category_id=" + categoryId + " AND _type IN ('INCOME') AND strftime('%Y',_date)='" + year + "'";
        }
        else if(type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _category_id=" + categoryId + " AND _type IN ('EXPENSE','PAYMENT') AND strftime('%Y',_date)='" + year + "'";
        }

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

	

    public ArrayList<Account> getCashAndSavingsList(AccountOrderBy orderBy)
    {
        String order="";

        if (orderBy==AccountOrderBy.Account) {
            order="ORDER BY _name ASC";
        }else if (orderBy==AccountOrderBy.Balance) {
            order="ORDER BY _balance DESC";
        }

        ArrayList<Account> list=new ArrayList<Account>();

        String sql="SELECT * FROM " + Store.ACCOUNT_TABLE  + " WHERE _type IN ('Cash','Savings') " + order;

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Account account = mapAccount(cursor);
                list.add(account);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }



    public ArrayList<Budget> getBudgetList()
    {
        ArrayList<Budget> list=new ArrayList<Budget>();

        String sql="SELECT _id,_name,_budget FROM " + Store.CATEGORY_TABLE
                + " WHERE _type='Expense' AND _isbudgeted=1 ORDER BY _name";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Budget chart = mapBudget(cursor);
                list.add(chart);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }


    public float getUsedBudget(int categoryId,int month,int year)
    {
        float usedBudget=0;
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql = "SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('EXPENSE','PAYMENT') AND _category_id=" + categoryId +
                " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
                usedBudget=cursor.getFloat(0);
        }

        db.close();

        return usedBudget;
    }

    public float getTotalBudgetSpent(int month,int year)
    {
        float total=0;

        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql="SELECT SUM(t._amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE + " t" +
                " INNER JOIN " + Store.CATEGORY_TABLE + " c ON t._category_id=c._id " +
                " WHERE t._type IN ('EXPENSE','PAYMENT') AND c._isbudgeted=1 and strftime('%m',t._date)='" + strMonth +
                "' AND strftime('%Y',t._date)='" + year + "'";

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


    public float getTotalBudgeted(String type)
    {
        float total=0;

        String sql="SELECT SUM(_budget) AS SumOfBudget FROM " + Store.CATEGORY_TABLE
                + " WHERE _type='" + type + "' AND _isbudgeted=1";

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





    public ArrayList<Budget> getBudgetListByYear(int categoryId,int year)
    {
        ArrayList<Budget> list=new ArrayList<Budget>();

        String sql="SELECT c._id,strftime('%m',_date) as month,c._budget FROM " + Store.TRANSACTION_TABLE + " t" +
                  " INNER JOIN categories c ON t._category_id=c._id WHERE t._type IN ('EXPENSE','PAYMENT') " +
                  " AND _isbudgeted=1 AND c._id=" + categoryId + " AND strftime('%Y',_date)='" + year + "' GROUP BY strftime('%m',_date)";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Budget chart = mapBudgetByYear(cursor);
                list.add(chart);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }


    public float getUsedBudgetByYear(int categoryId,int month,int year)
    {
        float usedBudget=0;
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql = "SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('EXPENSE','PAYMENT') AND _category_id=" + categoryId + " AND strftime('%m',_date)='" + strMonth +
                "' AND strftime('%Y',_date)='" + year + "'";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
                usedBudget=cursor.getFloat(0);
        }

        db.close();

        return usedBudget;
    }



    public float getTotalBudgetSpentByYear(int categoryId,int year)
    {
        float total=0;

        String sql="SELECT SUM(t._amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE + " t" +
                " INNER JOIN " + Store.CATEGORY_TABLE + " c ON t._category_id=c._id " +
                " WHERE t._type IN ('EXPENSE','PAYMENT') AND c._id=" + categoryId + " AND strftime('%Y',t._date)='" + year + "'";

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


    public float getTotalCreditCardBalance()
    {

        float total=0;

        String sql="SELECT SUM(_balance) AS SumOfBalance FROM " + Store.ACCOUNT_TABLE +
                " WHERE _type='Credit Card'";

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


    public ArrayList<CreditCard> getCreditCardList()
    {
        ArrayList<CreditCard> list=new ArrayList<CreditCard>();

        String sql="SELECT _id,_name,_balance,_limit FROM " + Store.ACCOUNT_TABLE + " WHERE _type='Credit Card' ORDER BY _balance DESC";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                CreditCard creditCard = mapCreditCard(cursor);
                list.add(creditCard);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
  }



    public float getTotalTransferAsSavings(int month,int year)
    {
        float total=0;
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        String sql = "SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('TRANSFER AS SAVINGS') AND strftime('%m',_date)='" + strMonth +
                "' AND strftime('%Y',_date)='" + year + "'";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
                total=cursor.getFloat(0);
        }

        db.close();

        return total;
  }



    public float getAccountBalance(int accountId)
    {
        float balance=0;

        String sql="SELECT _balance FROM " + Store.ACCOUNT_TABLE +
                " WHERE _id=" + accountId;

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                balance=cursor.getFloat(0);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return balance;
    }


    public ArrayList<FinancialGoal> getFinancialGoalList()
    {
        ArrayList<FinancialGoal> list=new ArrayList<FinancialGoal>();

        String sql="SELECT _id,_name,_balance,_financial_goal_name,_financial_goal_target FROM "
                + Store.ACCOUNT_TABLE + " WHERE _type IN ('Cash','Savings') AND _financial_goal_target <> 0 "
                + "AND _isactive=1 ORDER BY _financial_goal_name DESC";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                FinancialGoal financialGoal = mapFinancialGoal(cursor);
                list.add(financialGoal);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }







    public ArrayList<Payment> getCreditCardPayment()
    {
        ArrayList<Payment> list=new ArrayList<Payment>();

        String sql="select c._id,c._name,a._name,sum(t._amount)  from transactions t " +
                "inner join accounts a on a._id=t._account_id " +
                "inner join categories c on c._id=t._category_id " +
                "where t._type='PAYMENT' group by c._id";

        SQLiteDatabase db=store.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                Payment creditCardPayment = mapCreditCardPayment(cursor);
                list.add(creditCardPayment);
            }
            while(cursor.moveToNext());
        }

        db.close();

        return list;
    }




    public float getTotalAmount(CategoryType type,int year)
    {
        float total=0;

        String sql="";

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%Y',_date)='" + year + "'";


        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type='INCOME' AND strftime('%Y',_date)='" + year + "'";
        }

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


    public float getTotalAmount(CategoryType type,int month,int year)
    {
        float total=0;

        String sql="";
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth + "'" +
                    " AND strftime('%Y',_date)='" + year + "'";
        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type='INCOME' AND strftime('%m',_date)='" + strMonth + "'" +
                " AND strftime('%Y',_date)='" + year + "'";
        }
        else if (type==CategoryType.Debt)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE + " t " +
                "INNER JOIN Categories c ON t._category_id=c._id " +
                " WHERE (t._type IN ('PAYMENT') OR c._group IN ('Debt/Payable')) AND strftime('%m',_date)='" + strMonth + "'" +
                " AND strftime('%Y',_date)='" + year + "'";
        }


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


    public float getTotalAmountByToday(CategoryType type)
    {
        float total=0;

        String sql="";

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type IN ('EXPENSE','PAYMENT') AND _date=CURRENT_DATE";
        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type='INCOME' AND _date=CURRENT_DATE";
        }

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


    public float getTotalAmount(CategoryType type,String clause)
    {
        float total=0;

        String sql="";

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('EXPENSE','PAYMENT') AND " + clause;

        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('INCOME') AND " + clause;
        }

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


    public float getTotalAmount(CategoryType type,int month, int year,String query)
    {
        float total=0;

        String sql="";
        String strMonth="";

        if (month < 10 ) {
            strMonth="0" + month;
        } else {
            strMonth=String.valueOf(month);
        }

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth + "'" +
                    " AND strftime('%Y',_date)='" + year + "' AND (_description LIKE '%" + query + "%' OR _payment_info LIKE '%" +  query + "%')";
        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                    " WHERE _type IN ('INCOME') AND strftime('%m',_date)='" + strMonth + "'" +
                    " AND strftime('%Y',_date)='" + year + "' AND (_description LIKE '%" + query + "%' OR _payment_info LIKE '%" +  query + "%')";
        }

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


    public float getTotalAmount(CategoryType type,String month,int year)
    {
        float total=0;

        int m=Store.getMonthInt(month);

        String strMonth="";

        if (m < 10 ) {
            strMonth="0" + m;
        } else {
            strMonth=String.valueOf(m);
        }

        String sql="";

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('EXPENSE','PAYMENT') AND strftime('%m',_date)='" + strMonth +
                "' AND strftime('%Y',_date)='" + year + "'";
        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('INCOME') AND strftime('%m',_date)='" + strMonth +
                "' AND strftime('%Y',_date)='" + year + "'";
        }

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





    public float getTotalAmount(CategoryType type,int categoryId, String month,int year)
    {
        float total=0;

        int m=Store.getMonthInt(month);

        String strMonth="";

        if (m < 10 ) {
            strMonth="0" + m;
        } else {
            strMonth=String.valueOf(m);
        }

        String sql="";

        if (type==CategoryType.Expense)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type IN ('EXPENSE','PAYMENT') AND _category_id=" + categoryId +
               " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'";
        }
        else if (type==CategoryType.Income)
        {
            sql="SELECT SUM(_amount) AS SumOfAmount FROM " + Store.TRANSACTION_TABLE +
               " WHERE _type IN ('INCOME') AND _category_id=" + categoryId +
               " AND strftime('%m',_date)='" + strMonth + "' AND strftime('%Y',_date)='" + year + "'";
        }

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



    public float getTotalCashAndSavings()
    {
        float total=0;

        String sql="SELECT SUM(_balance) AS SumOfBalance FROM " + Store.ACCOUNT_TABLE +
                " WHERE _type IN ('Cash','Savings')";

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


    public float getTotalCreditCardUsage(int categoryId)
    {
        float total=0;

        String sql="SELECT SUM(_amount) AS SumOfBalance FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('CREDIT') AND _category_id=" + categoryId;

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


    public float getTotalCreditCardPayment()
    {

        float total=0;

        String sql="SELECT SUM(_amount) AS SumOfPayment FROM " + Store.TRANSACTION_TABLE +
                " WHERE _type IN ('PAYMENT')";

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



    public float getTotalCreditCardDebt()
    {

        float total=0;

        String sql="SELECT SUM(_limit-_balance) AS SumOfDebt FROM " + Store.ACCOUNT_TABLE +
                " WHERE _type IN ('Credit Card')";

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




    public float getTotalCashFlow(int month,int year)
	{
		float income=getTotalAmount(CategoryType.Income,month,year);
		float expense=getTotalAmount(CategoryType.Expense,month,year);
		
		return income+expense;
	}
	
	public float getSurplusDefisit(int month,int year)
	{
		float income=getTotalAmount(CategoryType.Income,month,year);
		float expense=getTotalAmount(CategoryType.Expense,month,year);
		
		return income-expense;
	}
	
	
	public CashFlow[] getCashFlow(int month,int year)
	{	
	
		float income=getTotalAmount(CategoryType.Income,month,year);
		float expense=getTotalAmount(CategoryType.Expense,month,year);
		
		CashFlow cashFlow1=new CashFlow("Income", income);
		CashFlow cashFlow2=new CashFlow("Expense", expense);
		
		CashFlow[] cashFlows=new CashFlow[2];
		
		cashFlows[0]=cashFlow1;
		cashFlows[1]=cashFlow2;
		
		return cashFlows;
	}	
	
		
	
	public float getCreditCardDebt(int accountId)
	{
		float debt=0;

		String sql1 = "SELECT a._name,SUM(t._amount) AS Amount FROM Transactions t "
                + "INNER JOIN Accounts a ON t._account_id=a._id "
                + "WHERE t._type<>'PAYMENT' AND a._type='Credit Card' AND a._id=" + accountId
                + " GROUP BY a._name";

        SQLiteDatabase db=store.getReadableDatabase();
		
		Cursor cursor1=db.rawQuery(sql1, null);
		
		if(cursor1.moveToFirst())
		{
			do
			{
				 boolean isRdr2Exist = false;
				
			     String sql2 = "SELECT a._name,SUM(t._amount) AS Amount FROM Transactions t "
                         + "INNER JOIN Accounts a ON t._account_id=a._id "
                         + "WHERE t._type='PAYMENT' AND a._name='" + cursor1.getString(0) + "' "
                         + "GROUP BY a._name";
			    
			     Cursor cursor2=db.rawQuery(sql2, null);
			     
			     if (cursor2.moveToFirst())
			     {
				     do{
				    	  isRdr2Exist = true;
	                   	  debt=cursor1.getFloat(1)- cursor2.getFloat(1);
	                 }
				     while(cursor2.moveToNext());
			     }
			     
			     if (!isRdr2Exist)
                 {
                     isRdr2Exist = false;
                     debt=cursor1.getFloat(1);
                 }

                cursor2.close();
				
			}
			while(cursor1.moveToNext());
		}

        cursor1.close();

		return debt;
		
	}
	
	
	

}
