package com.xeris.zinj.repository;

import com.xeris.zinj.model.Setting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingRepository 
{
	private Store store;

	public SettingRepository(Context ctx) 
	{
		store=new Store(ctx);
	}
	
	private Setting mapSetting(Cursor cursor)
	{
		Setting setting=new Setting();
		
		setting.setId(cursor.getInt(0));
		setting.setEmail(cursor.getString(1));
		setting.setIsProtected(cursor.getInt(2));
		setting.setPassword(cursor.getString(3));
		setting.setMonth(cursor.getInt(4));
        setting.setYear(cursor.getInt(5));
        setting.setDefaultChart(cursor.getString(6));
        setting.setCurrency(cursor.getString(7));
		setting.setIsHideSymbol(cursor.getInt(8));
		
		return setting;
	}
	
	
	public Setting getById(int id)
	{
		Setting setting=null;
		
		String sql="SELECT * FROM " + Store.SETTING_TABLE + " WHERE _id=" + id;

        SQLiteDatabase db=store.getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			setting = mapSetting(cursor);
		}

        db.close();
		
		return setting;
	}
	
	public String getEmail()
	{		
		Setting setting=getById(1);
		return setting.getEmail();
	}
	
	public String getCurrency()
	{
		String currency="";
		Setting setting=getById(1);
		if (setting.getIsHideSymbol()==0)
		{
			currency=setting.getCurrency();
		}		
		
		return currency;
	}
	
	
	
	public void update(Setting setting)
	{
        SQLiteDatabase db=store.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put(Store.SETTING_EMAIL, setting.getEmail());
		values.put(Store.SETTING_IS_PROTECTED, setting.getIsProtected());
		values.put(Store.SETTING_PASSWORD, setting.getPassword());
		values.put(Store.SETTING_MONTH, setting.getMonth());
        values.put(Store.SETTING_YEAR,setting.getYear());
        values.put(Store.SETTING_DEFAULT_CHART,setting.getDefaultChart());
        values.put(Store.SETTING_CURRENCY, setting.getCurrency());
		values.put(Store.SETTING_IS_HIDE_CURRENCY, setting.getIsHideSymbol());
				
		db.update(Store.SETTING_TABLE, values, "_id=?", new String[] {String.valueOf(setting.getId())});
	
		db.close();
	}
	 
}
