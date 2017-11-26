package com.xeris.zinj.repository;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.xeris.zinj.model.Setting;

public class Store extends SQLiteOpenHelper 
{
	
   public static final int DATABASE_VERSION = 1;
   public static final String  DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/com.xeris.zinj";

   public static final String DATABASE_NAME = "zinj.db";
	   
   public static final String ACCOUNT_TABLE="Accounts";
   public static final String ACCOUNT_ID="_id";
   public static final String ACCOUNT_NAME="_name";
   public static final String ACCOUNT_TYPE="_type";
   public static final String ACCOUNT_BALANCE="_balance";
   public static final String ACCOUNT_ORDER="_order";
   public static final String ACCOUNT_NOTES="_notes";
   public static final String ACCOUNT_LIMIT="_limit";
   public static final String ACCOUNT_FINANCIAL_GOAL_NAME="_financial_goal_name";
   public static final String ACCOUNT_FINANCIAL_GOAL_TARGET="_financial_goal_target";
   public static final String ACCOUNT_IS_ACTIVE="_isactive";

   public static final String CATEGORY_TABLE="Categories";
   public static final String CATEGORY_ID="_id";
   public static final String CATEGORY_NAME="_name";
   public static final String CATEGORY_TYPE="_type";
   public static final String CATEGORY_GROUP="_group";
   public static final String CATEGORY_IS_BUDGETED="_isbudgeted";
   public static final String CATEGORY_BUDGET="_budget";
   public static final String CATEGORY_IS_ACTIVE="_isactive";

   public static final String TRANSACTION_TABLE="Transactions";
   public static final String TRANSACTION_ID="_id";
   public static final String TRANSACTION_TYPE="_type";
   public static final String TRANSACTION_DATE="_date";
   public static final String TRANSACTION_DESCRIPTION="_description";
   public static final String TRANSACTION_ADDITIONAL_INFO="_additional_info";
   public static final String TRANSACTION_AMOUNT="_amount";
   public static final String TRANSACTION_CATEGORY_ID="_category_id";
   public static final String TRANSACTION_ACCOUNT_ID="_account_id";
   public static final String TRANSACTION_NOTES="_notes";
   public static final String TRANSACTION_PAYMENT_ACCOUNT_ID="_payment_account_id";
   public static final String TRANSACTION_PAYMENT_INFO="_payment_info";

   public static final String SETTING_TABLE="Settings";
   public static final String SETTING_ID="_id";
   public static final String SETTING_EMAIL="_email";
   public static final String SETTING_IS_PROTECTED="_is_protected";
   public static final String SETTING_PASSWORD="_password";
   public static final String SETTING_MONTH="_month";
   public static final String SETTING_YEAR="_year";
   public static final String SETTING_DEFAULT_CHART="_default_chart";
   public static final String SETTING_CURRENCY="_currency";
   public static final String SETTING_IS_HIDE_CURRENCY="_is_hide_currency";
      
  	public enum ActivityMode
    {
		Add,Edit
    }
   
   public Store(Context ctx)
   {
       super(ctx, getDatabaseFileName(), null, DATABASE_VERSION);
   }

    public static String getDatabaseFileName()
    {
        String fileName="";
        boolean isSuceess=createDirIfNotExists(DATABASE_FILE_PATH);
        if (isSuceess) fileName=DATABASE_FILE_PATH + "/" + DATABASE_NAME;

        return fileName;

    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("Zinj Log", "Problem creating folder");
                ret = false;
            }
        }
        return ret;
    }

    public static int getMonthInt(String monthString)
    {
        int month=0;

        if (monthString=="January") {
            month=1;
        }else if (monthString=="February") {
            month=2;
        } else if (monthString=="March") {
            month=3;
        }else if (monthString=="April") {
            month=4;
        }else if (monthString=="May") {
            month=5;
        }else if (monthString=="June") {
            month=6;
        }else if (monthString=="July") {
            month=7;
        }else if (monthString=="August") {
            month=8;
        }else if (monthString=="September") {
            month=9;
        }else if (monthString=="October") {
            month=10;
        }else if (monthString=="November") {
            month=11;
        }else if (monthString=="December") {
            month=12;
        }


        return month;
    }

   public static String getMonthString(int month)
   {
	    String strMonth="";
	   
		switch(month)
		{
			case 1 :
				strMonth="January";
				break;
			case 2 :
				strMonth="February";
				break;
			case 3 :
				strMonth="March";
				break;
			case 4 :
				strMonth="April";
				break;
			case 5 :
				strMonth="May";
				break;
			case 6 :
				strMonth="June";
				break;
			case 7 :
				strMonth="July";
				break;
			case 8 :
				strMonth="August";
				break;
			case 9 :
				strMonth="September";
				break;
			case 10 :
				strMonth="October";
				break;
			case 11 :
				strMonth="November";
				break;
			case 12 :
				strMonth="December";
				break;
				
		}
		
		return strMonth;
	   
   }


    public static String getMonthInitial(int month)
    {
        String strMonth="";

        switch(month)
        {
            case 1 :
                strMonth="Jan";
                break;
            case 2 :
                strMonth="Feb";
                break;
            case 3 :
                strMonth="Mar";
                break;
            case 4 :
                strMonth="Apr";
                break;
            case 5 :
                strMonth="May";
                break;
            case 6 :
                strMonth="Jun";
                break;
            case 7 :
                strMonth="Jul";
                break;
            case 8 :
                strMonth="Aug";
                break;
            case 9 :
                strMonth="Sep";
                break;
            case 10 :
                strMonth="Oct";
                break;
            case 11 :
                strMonth="Nov";
                break;
            case 12 :
                strMonth="Dec";
                break;

        }

        return strMonth;
    }


	@Override
	public void onCreate(SQLiteDatabase db) throws SQLiteException
	{
		final Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);

		String sql1="CREATE TABLE " + ACCOUNT_TABLE + " (" + 
		   		ACCOUNT_ID + " INTEGER PRIMARY KEY, " + ACCOUNT_NAME + " VARCHAR(100)," +
		   		ACCOUNT_TYPE + " VARCHAR(50), " +  ACCOUNT_BALANCE + " DECIMAL," +
                ACCOUNT_ORDER + " INT," + ACCOUNT_NOTES + " NVARCHAR(500)," +
                ACCOUNT_LIMIT + " DECIMAL," + ACCOUNT_IS_ACTIVE + " INT" +
		   	")";

		String sql2="CREATE TABLE " + CATEGORY_TABLE + " (" + 
		   		CATEGORY_ID + " INTEGER PRIMARY KEY, " + CATEGORY_NAME + " NVARCHAR(50)," +
		   		CATEGORY_TYPE + " NVARCHAR(50), " +  CATEGORY_GROUP + " NVARCHAR(50)," +
		   		CATEGORY_IS_BUDGETED + " INT, " +  CATEGORY_BUDGET + " DECIMAL" +
                CATEGORY_IS_ACTIVE + " INT " +
		    ")";
	
		String sql3="CREATE TABLE " + TRANSACTION_TABLE + " (" + 
		   		TRANSACTION_ID + " INTEGER PRIMARY KEY, " + TRANSACTION_TYPE + " NVARCHAR(50)," +
		   		TRANSACTION_DATE + " NVARCHAR(50), " +  TRANSACTION_DESCRIPTION + " NVARCHAR(500)," +
		   		TRANSACTION_ADDITIONAL_INFO + " NVARCHAR(500)," + TRANSACTION_AMOUNT + " DECIMAL, " +
		   		TRANSACTION_CATEGORY_ID + " INT," + TRANSACTION_NOTES + " NVARCHAR(500)," +  
		   		TRANSACTION_ACCOUNT_ID + " INT, " +	TRANSACTION_PAYMENT_ACCOUNT_ID + " INT," +
                TRANSACTION_PAYMENT_INFO + " NVARCHAR(500)" +
		   ")";
		
		String sql4="CREATE TABLE " + SETTING_TABLE + " (" + 
		   		SETTING_ID + " INTEGER PRIMARY KEY, " + SETTING_EMAIL + " NVARCHAR(50)," +
		   		SETTING_IS_PROTECTED + " INT, " +  SETTING_PASSWORD + " NVARCHAR(4)," +
		   		SETTING_MONTH + " INT, " + SETTING_YEAR + " INT," + SETTING_DEFAULT_CHART + " NVARCHAR(50)," +
                SETTING_CURRENCY + " NVARCHAR(50)," + SETTING_IS_HIDE_CURRENCY + " INT" +
		   ")";
		
		String sql5="INSERT INTO Settings VALUES (1,'',0,''," + (month+1) + "," + year + ",'Cash Flow','$',0)";
	
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
		db.execSQL(sql5);
		
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		
	}
	
}
