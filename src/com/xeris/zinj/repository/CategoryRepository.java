package com.xeris.zinj.repository;

import java.util.ArrayList;
import java.util.List;

import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryRepository
{
	private Store store;
		
	public enum CategoryType
	{
		Income,Expense,Debt
	}
	
	public CategoryRepository(Context ctx) 
	{
		store=new Store(ctx);
	}
	
	private void putValues(Category category, ContentValues values) 
	{
		values.put(Store.CATEGORY_NAME, category.getName());
		values.put(Store.CATEGORY_TYPE, category.getType());
		values.put(Store.CATEGORY_GROUP, category.getGroup());
		values.put(Store.CATEGORY_IS_BUDGETED, category.getIsBudgeted());
		values.put(Store.CATEGORY_BUDGET, category.getBudget());
        values.put(Store.CATEGORY_IS_ACTIVE, category.getIsActive());
     }
	
	
	private Category mapCategory(Cursor cursor)
	{
	
		Category category=new Category();
		
		category.setId(cursor.getInt(0));
		category.setName(cursor.getString(1));
		category.setType(cursor.getString(2));
		category.setGroup(cursor.getString(3));
		category.setIsBudgeted(cursor.getInt(4));
		category.setBudget(cursor.getFloat(5));
        category.setIsActive(cursor.getInt(6));

        return category;
	}
	
	
	public boolean isCategoryUsedByTransaction(int categoryId)
	{
		boolean isUsed=false;
		
		String sql="SELECT * FROM " + Store.TRANSACTION_TABLE + " WHERE _category_id = " +  categoryId;
		
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
	

	public boolean isCategoryExist(String name)
	{
		boolean isExist=false;
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE + " WHERE UPPER(_name)='" + name.toUpperCase() + "'";
		
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
	 
	
		
	public Category getById(int id)
	{
		Category category=null;
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE + " WHERE _id = " +  id;
		
		SQLiteDatabase db=store.getReadableDatabase();
						
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
			category = mapCategory(cursor);
		}				
		
		db.close();
		
		return category;
	}


	public Category getByName(String name)
	{		
		Category category=null;
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE + " WHERE _name = '" +  name + "'";
			
		SQLiteDatabase db=store.getReadableDatabase();
		
		Cursor cursor=db.rawQuery(sql, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
			category = mapCategory(cursor);
		}	
		
		db.close();
		
		return category;
	}
	
	
	
	public ArrayList<Category> getCategoryList()
	{
        ArrayList<Category> list=new ArrayList<Category>();
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE + " ORDER BY _name";
		
		SQLiteDatabase db=store.getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do{
		       	Category category = mapCategory(cursor);
                list.add(category);
			}
			while(cursor.moveToNext());
		}
		
		db.close();
	
		return list;
	}


	
	
	public String[] getNames()
	{		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE;
		
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
	
	
	
	public List<SpinnerItem> getSpinnerItems()
	{
		List<SpinnerItem> items=new ArrayList<SpinnerItem>();
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE;
		
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


	public ArrayList<SpinnerItem> getSpinnerItemsByType(CategoryType categoryType)
	{
        ArrayList<SpinnerItem> items=new ArrayList<SpinnerItem>();
		
		String sql="SELECT * FROM " + Store.CATEGORY_TABLE + " WHERE _type='" + categoryType.toString() + "' ORDER BY " + Store.CATEGORY_NAME;
		
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
		}else{
			items.add(new SpinnerItem("Please Select", true));
		}
		
		db.close();
		
		return items;
	}
	

	
	
	public void save(Category category)
	{
		SQLiteDatabase db=store.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		putValues(category, values);
		db.insert(Store.CATEGORY_TABLE, null, values);
	
		db.close();
	}

	public void update(Category category)
	{
		SQLiteDatabase db=store.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		putValues(category, values);
		
		db.update(Store.CATEGORY_TABLE, values, "_id=?", new String[] {String.valueOf(category.getId())});
	
		db.close();
	}
	
	
	public void delete(int id)
	{		
		SQLiteDatabase db=store.getWritableDatabase();
		db.delete(Store.CATEGORY_TABLE, "_id=?", new String[] {String.valueOf(id)});
		db.close();
	}

	
	

}
