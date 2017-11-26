package com.xeris.zinj.ui;

import com.xeris.zinj.R;
import com.xeris.zinj.adapter.CategoryAdapter;
import com.xeris.zinj.adapter.ChartAdapter;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.model.Chart;
import com.xeris.zinj.repository.CategoryRepository;
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

public class CategoryFragment extends Fragment
{
	private Activity context;
	
	private CategoryRepository categoryRepository;
	private ListView lvwCategory;
	private String categoryId;
	private String categoryName;
	
 
	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	     	
		  setHasOptionsMenu(true);
	     
		  categoryRepository=new CategoryRepository(context);
		
		  View rootView =inflater.inflate(R.layout.category_fragment, container, false);
			
		  int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		  TextView title = (TextView)context.findViewById(titleId);
		  title.setTextColor(Color.WHITE); 
		  title.setText("Categories");
		  		  
		  return rootView;
	 }
	 
	  @Override
	  public void onAttach(Activity activity)
	  {
		  	context=activity;
	        super.onAttach(activity);
	  }	  
	  
	  
	  public OnItemLongClickListener listViewOnItemLongClickListner=new OnItemLongClickListener() 
	  {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) 
			{
			
				categoryName = (String)((TextView) v.findViewById(R.id.lblCategoryName)).getText();
		    	categoryId = (String)((TextView) v.findViewById(R.id.lblCategoryId)).getText();
		    	
		    	AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
		    	dialog.setTitle(categoryName);
                                       
                String[] items=new String[]{"Edit","Delete"};
                
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    	
                    	switch(item)
                    	{
                    		//edit
                    		
                    		case 0 :
                    	    
                    			Bundle extras=new Bundle();
                            	extras.putString(Store.CATEGORY_ID, categoryId);
                            	
                            	Intent i=new Intent(context,CategoryActivity.class);
                            	i.putExtras(extras);
                            	
                            	startActivityForResult(i, 1);
                            
                            	break;
                            
                            	//delete
                            	
                    		case 1 :
                    			deleteCategory();
                    			break;
                    			
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
		 
		 lvwCategory.setOnItemLongClickListener(listViewOnItemLongClickListner);
			
	  }
	  
	  
	  public void onDataLoaded() 
	  {
          ArrayList<Category> data=categoryRepository.getCategoryList();

          CategoryAdapter adapter = new CategoryAdapter(context, R.layout.category_template, data);
          lvwCategory = (ListView)context.findViewById(R.id.lvwCategory);
          lvwCategory.setAdapter(adapter);
      }
	  
	  
	  @Override
	  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	  {   	
		   inflater.inflate(R.menu.list_menu, menu);
	  }
	  
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) 
	  {
	        switch (item.getItemId()) {
	            case R.id.menuitem_add:
	            	startActivity(new Intent(context,CategoryActivity.class));
	            	break;
	         }

	        return super.onOptionsItemSelected(item);
	  }
	  
	@Override
	public void onResume() 
	{
		super.onResume();
		onDataLoaded();
	}
	  
	  
	
	
	private void deleteCategory()
	{
		if (!categoryRepository.isCategoryUsedByTransaction(Integer.parseInt(categoryId)))
		{		
			new AlertDialog.Builder(context)
			.setIcon(R.drawable.trash)
			.setTitle("Delete")
		    .setMessage("Are you sure want to delete '" + categoryName + "'?")
			.setPositiveButton("Yes",
	                            new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog,
	                                int whichButton) 
	          {
	        	  categoryRepository.delete(Integer.parseInt(categoryId));
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
                Toast.makeText(context, "You can't delete this category.'" + categoryName +
                        "' has already used by transaction.\n\nPlease delete associated transaction",Toast.LENGTH_LONG).show();
            }
        }
	}
	
	  
	  
	  
	  

}
