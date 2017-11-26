package com.xeris.zinj.ui;


import java.io.File;
import java.util.*;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.*;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.ListItem;
import com.xeris.zinj.adapter.TransactionAdapter;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.model.Chart;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.repository.*;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;
import com.xeris.zinj.repository.ChartRepository.CategoryOrderBy;
import com.xeris.zinj.repository.TransactionRepository.TransactionFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.AdapterView.OnItemLongClickListener;

public class TransactionFragment extends Fragment
{
	private Activity context;

    private TransactionRepository transactionRepository;
	private SettingRepository settingRepository;
	private ChartRepository chartRepository;
	
	private ListView lvwTransaction;
	private Button pnlIncome;
	private Button pnlExpense;

    private String transactionId;
	private String description;


    private Setting setting=null;
    private String query="";
    private String type="";

    private Calendar cal;
    private int day;
    private int month;
    private int currentMonth;
    private int year;

    private boolean flag=false;
    private Map<String,String> map;
    private Bundle savedInstanceState;

    private  MenuItem menuRefresh;
    private MenuItem menuSearch;
    private MenuItem menuAdd;
    private boolean isQueryMode;

    private final String DB_FILEPATH = "/android/data/com.xeris.zinj";

	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState)
     {

         setHasOptionsMenu(true);

         transactionRepository=new TransactionRepository(context);
         chartRepository=new ChartRepository(context);
         settingRepository=new SettingRepository(context);

         View rootView =inflater.inflate(R.layout.transaction_fragment, container, false);

         int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
         TextView title = (TextView)context.findViewById(titleId);

         title.setText("Transactions");
         title.setTextColor(Color.WHITE);

         pnlIncome=(Button)context.findViewById(R.id.pnlIncome);
         pnlExpense=(Button)context.findViewById(R.id.pnlExpense);

         cal = Calendar.getInstance();
         day = cal.get(Calendar.DAY_OF_MONTH);
         currentMonth=cal.get(Calendar.MONTH)+1;
         year=cal.get(Calendar.YEAR);


         return rootView;
		  
	 }


    private void changeMonthIfNew()
    {
        final Calendar c = Calendar.getInstance();

        int day = c.get(Calendar.DAY_OF_MONTH);

        setting=settingRepository.getById(1);
        if (setting!=null)
        {
           if (day==1)
           {
               Setting newSetting=new Setting();

               newSetting.setId(1);
               newSetting.setMonth(currentMonth);
               newSetting.setYear(year);
               newSetting.setCurrency(setting.getCurrency());
               newSetting.setEmail(setting.getEmail());
               newSetting.setIsHideSymbol(setting.getIsHideSymbol());
               newSetting.setIsProtected(setting.getIsProtected());
               newSetting.setPassword(setting.getPassword());
               newSetting.setDefaultChart(setting.getDefaultChart());

               settingRepository.update(newSetting);

           }
        }
    }

    @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
    	 super.onActivityCreated(savedInstanceState);

         changeMonthIfNew();
         onDataLoaded(TransactionFilter.All);

     }


	 public void onDataLoaded(TransactionFilter filter)
	 {

         float income=0;
         float expense=0;

         setting=settingRepository.getById(1);
         String currency="";

         if (setting!=null)
         {
             month=setting.getMonth();
             year=setting.getYear();

             if (setting.getIsHideSymbol()==0)
             {
                 currency=setting.getCurrency();
             }
        }

         ArrayList<Transaction> data=new ArrayList<Transaction>();

         map =((MainActivity)getActivity()).getQuery();

         String type="";
         String query="";
         String id="";
         String view="";

         if(map!=null && map.containsKey("view") && map.get("view").equals("CashFlow"))
         {
             isQueryMode=true;
             query=map.get("q");

             context.setTitle(query);

             data=transactionRepository.getCashFlowTransactionQueryByMonth(month, year, query);

             if (query.toUpperCase().equals(Constant.TRANSACTION_INCOME))
             {
                income=chartRepository.getTotalAmount(CategoryType.Income,month,year);
                expense=0;
             }
             else if(query.toUpperCase().equals(Constant.TRANSACTION_EXPENSE))
             {
                 income=0;
                 expense=chartRepository.getTotalAmount(CategoryType.Expense,month,year);
             }
         }
         else if(map!=null && map.containsKey("view") && map.get("view").equals("CashFlowByYear"))
         {
             isQueryMode=true;
             query=map.get("q");
             type=map.get("type");

             context.setTitle(query + " " + year);

             if (type.equals(Constant.TRANSACTION_INCOME))
             {
                data=transactionRepository.getCashFlowTransactionQueryByYear(CategoryType.Income,query,year);
                income=chartRepository.getTotalAmount(CategoryType.Income,query,year);
                expense=0;
             }
             else if(type.equals(Constant.TRANSACTION_EXPENSE))
             {
                 data=transactionRepository.getCashFlowTransactionQueryByYear(CategoryType.Expense,query,year);
                 income=0;
                 expense=chartRepository.getTotalAmount(CategoryType.Expense,query,year);
            }
        }
         else if (map!=null && map.containsKey("view") && (map.get("view").equals("IncomeExpense") ||  map.get("view").equals("Budget")
                 || map.get("view").equals("Savings") || map.get("view").equals("CreditCard")))
         {
             isQueryMode=true;
             query=map.get("q");
             view=map.get("view");

             context.setTitle(query);
             data=transactionRepository.getTransactionQueryByMonth(month, year, query);

             income=chartRepository.getTotalAmount(CategoryType.Income, month, year, query);
             expense=chartRepository.getTotalAmount(CategoryType.Expense, month, year, query);
         }
         else if(map!=null && map.containsKey("view") && map.get("view").equals("IncomeExpenseByYear"))
         {
             isQueryMode=true;

             type=map.get("type");
             query=map.get("q");
             id=map.get("id");

             context.setTitle(query + " " + year);

             if (type.equals("Income"))
             {
                data=transactionRepository.getTransactionQueryByYear(CategoryType.Income,query,year,Integer.parseInt(id));
                income=chartRepository.getTotalAmount(CategoryType.Income,Integer.parseInt(id),query,year);
                expense=0;
             }
             else if(type.equals("Expense"))
             {
                 data=transactionRepository.getTransactionQueryByYear(CategoryType.Expense,query,year,Integer.parseInt(id));
                 income=0;
                 expense=chartRepository.getTotalAmount(CategoryType.Expense,Integer.parseInt(id),query,year);
           }

        }
        else if(map!=null && map.containsKey("view") && map.get("view").equals("BudgetByYear"))
        {
            isQueryMode=true;

            query=map.get("q");
            id=map.get("id");

            context.setTitle(query + " " + year);

            data=transactionRepository.getTransactionQueryByYear(CategoryType.Expense,query,year,Integer.parseInt(id));
            income=0;
            expense=chartRepository.getTotalAmount(CategoryType.Expense,Integer.parseInt(id),query,year);
        }
        else
         {
             context.setTitle("Transactions");
             data=transactionRepository.getTransactionList(filter,month,year);

             income=chartRepository.getTotalAmount(CategoryType.Income,month,year);
             expense=chartRepository.getTotalAmount(CategoryType.Expense,month,year);

         }

        TransactionAdapter dataAdapter=new TransactionAdapter(context, R.layout.transaction_template,data);
        lvwTransaction = (ListView) context.findViewById(R.id.lvwTransaction);

        lvwTransaction.setAdapter(dataAdapter);
        lvwTransaction.setOnItemLongClickListener(listviewOnItemClickListener);

        pnlIncome=(Button)context.findViewById(R.id.pnlIncome);
        pnlExpense=(Button)context.findViewById(R.id.pnlExpense);

        pnlIncome.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6b8e23")));
        pnlExpense.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b22222")));

        String strIncome=String.valueOf(income);
        String strExpense=String.valueOf(expense);

        pnlIncome.setText(currency + strIncome.replaceAll("\\.0*$", ""));
        pnlExpense.setText(currency + strExpense.replaceAll("\\.0*$", ""));

         pnlIncome.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 map=new HashMap<String, String>();
                 map.put("view","pnlIncome");

                 ((MainActivity) getActivity()).setQuery(map);

                 pnlIncome.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#556b2f")));
                 context.getActionBar().setSelectedNavigationItem(3);
             }
         });

         pnlExpense.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 map=new HashMap<String, String>();
                 map.put("view","pnlExpense");

                 ((MainActivity) getActivity()).setQuery(map);

                 pnlExpense.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8b4513")));
                 context.getActionBar().setSelectedNavigationItem(3);
             }
         });

     }


    public void onSearchLoaded()
    {
        setting=settingRepository.getById(1);
        String currency="";

        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();

            if (setting.getIsHideSymbol()==0)
            {
                currency=setting.getCurrency();
            }

        }

        ArrayList<Transaction> data=transactionRepository.search(month, year, query);

        TransactionAdapter dataAdapter=new TransactionAdapter(context, R.layout.transaction_template,data);
        lvwTransaction = (ListView) context.findViewById(R.id.lvwTransaction);

        lvwTransaction.setAdapter(dataAdapter);
        lvwTransaction.setOnItemLongClickListener(listviewOnItemClickListener);

        pnlIncome=(Button)context.findViewById(R.id.pnlIncome);
        pnlExpense=(Button)context.findViewById(R.id.pnlExpense);

        float income=0;
        float expense=0;
        float other=0;

        String strIncome="";
        String strExpense="";
        String strOther="";

        if (type.equals("All") || type.equals("Income") || type.equals("Expense"))
        {
            income=chartRepository.getTotalAmount(CategoryType.Income,query);
            expense=chartRepository.getTotalAmount(CategoryType.Expense,query);

            pnlIncome.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6b8e23")));
            pnlExpense.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b22222")));

            strIncome=String.valueOf(income);
            strExpense=String.valueOf(expense);

            pnlIncome.setText(currency + strIncome.replaceAll("\\.0*$", ""));
            pnlExpense.setText(currency + strExpense.replaceAll("\\.0*$", ""));

        }
        else
        {
            other=transactionRepository.getFilterAmount(type,query);

            pnlIncome.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d3d3d3")));
            pnlIncome.setText("");

            pnlExpense.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b03060")));

            strOther=String.valueOf(other);

            pnlExpense.setText(currency + strOther.replaceAll("\\.0*$", ""));

            pnlIncome.setOnClickListener(null);
            pnlExpense.setOnClickListener(null);
        }

    }
	 
	 
	 public OnItemLongClickListener listviewOnItemClickListener=new OnItemLongClickListener()
	 {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v,
				int arg2, long arg3) 
		{
			
			transactionId = (String)((TextView) v.findViewById(R.id.lblTransactionId)).getText();
			description = (String)((TextView) v.findViewById(R.id.lblTransactionDescription)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(description);
	    	
            String[] items=new String[]{"Edit","Delete"};
            
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                	
                	switch(item)
                	{
                		case 0 :
                	       	ShowActivity(transactionId,TransactionActivity.class);
                        	break;
                    	case 1 :
                			deleteTransaction();
                			break;
                			
                	}                  	
                }
            });
            
            AlertDialog alert = dialog.create();
            alert.show();
                            
	    	return true;
			
		}
	};

    private void showAbout()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        StringBuilder msg=new StringBuilder();

        msg.append("Personal Finanace Manager\n");
        msg.append("Version : 1.0\n\n");
        msg.append("\u00A9 2013, XERIS\n");

        alertDialog.setTitle("Zinj");
        alertDialog.setMessage(msg.toString());

        alertDialog.setIcon(R.drawable.zlogo);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }
	 
	  @Override
	  public void onAttach(Activity activity)
	  {
		  	context=activity;
            super.onAttach(activity);
	  }	  
	  

	 
	  
	  private void share()
	  {
          setting=settingRepository.getById(1);
		  String currency="";
          if (setting!=null)
          {
              month=setting.getMonth();
              year=setting.getYear();

              if (setting.getIsHideSymbol()==0)
              {
                  currency=setting.getCurrency();
              }
          }

    	  String monthString=Store.getMonthString(setting.getMonth());
		  
		  float totalIncome=chartRepository.getTotalAmount(CategoryType.Income, month, year);
		  float totalExpense=chartRepository.getTotalAmount(CategoryType.Expense,month,year);
		  float surplusDeficit=chartRepository.getSurplusDefisit(month,year);
		  
		  ArrayList<Chart> incomeList=chartRepository.getChartList(CategoryType.Income,month,year, CategoryOrderBy.Amount);
          ArrayList<Chart> expenseList=chartRepository.getChartList(CategoryType.Expense,month,year,CategoryOrderBy.Amount);

		  StringBuilder sb=new StringBuilder();
	
		  sb.append("INCOME : " + currency + String.valueOf(totalIncome).replaceAll("\\.0*$", "") + "\n");
		  sb.append("\n");

          float percentage=0;

		  for(Chart c:incomeList)
		  {
              percentage=(c.getAmount() / totalIncome) * 100;
              if (Math.round(percentage)==0)
              {
                  sb.append(c.getCategoryName() + " : " + currency + String.valueOf(c.getAmount()).replaceAll("\\.0*$", "")
                          + "  (" + String.format("%.2f",percentage) + "%)\n");
              }else
              {
                  sb.append(c.getCategoryName() + " : " + currency + String.valueOf(c.getAmount()).replaceAll("\\.0*$", "")
                          + "  (" + Math.round(percentage) + "%)\n");
              }

		  }
		  sb.append("\n");
		  
		  sb.append("EXPENSE : " + currency + String.valueOf(totalExpense).replaceAll("\\.0*$", "") + "\n");
		  sb.append("\n");
		  for(Chart c: expenseList)
		  {
              percentage=(c.getAmount()/totalExpense)*100;

              if (Math.round(percentage)==0)
              {
                  sb.append(c.getCategoryName() + " : " + currency +  String.valueOf(c.getAmount()).replaceAll("\\.0*$", "")
                          + "  (" + String.format("%.2f",percentage) + "%)\n");
              }
              else
              {
                  sb.append(c.getCategoryName() + " : " + currency + String.valueOf(c.getAmount()).replaceAll("\\.0*$", "")
                          + "  (" + Math.round(percentage) + "%)\n");

		      }
          }

		  sb.append("\n");

		  if (surplusDeficit > 0)
		  {
			  sb.append("+ " + currency + String.valueOf(surplusDeficit).replaceAll("\\.0*$", "") + " (Surplus)");
		  }
		  else if (surplusDeficit <0)
		  {
			  sb.append("- " + currency +  String.valueOf(Math.abs(surplusDeficit)).replaceAll("\\.0*$", ""));
		  }
		  else {
			  sb.append(currency + String.valueOf(Math.abs(surplusDeficit)).replaceAll("\\.0*$", "") + " (Deficit)");
		  }
		  
		  String body=sb.toString();
		  
		  Intent sharingIntent=new Intent(android.content.Intent.ACTION_SEND);
		  sharingIntent.setType("text/plain");
		  
		  sharingIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{settingRepository.getEmail()});
		  sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Cash Flow " + monthString + " " + year);
		  sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,body);
		  startActivity(Intent.createChooser(sharingIntent, "Share.."));


	  }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (10) : {
                if (resultCode == Activity.RESULT_OK) {
                    query = data.getStringExtra("query");
                    type=data.getStringExtra("type");

                }
                break;
            }
        }
    }

    private void backup(String type)
    {
        boolean found = false;
        setting=settingRepository.getById(1);

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) )
                {
                    share.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {setting.getEmail()});
                    share.putExtra(Intent.EXTRA_SUBJECT, "Zinj Backup " + currentMonth + "/" + day + "/" + year);
                    share.putExtra(Intent.EXTRA_TEXT, "For restore data please copy this attachment to directory " + DB_FILEPATH);

                    String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

                    Uri uri = Uri.fromFile(new File(sdCard + DB_FILEPATH + "/zinj.db"));
                    share.putExtra(Intent.EXTRA_STREAM, uri);

                    share.setPackage(info.activityInfo.packageName);

                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(share, "Backup"));
        }
    }



    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.transaction_menu, menu);

        menuRefresh = menu.findItem(R.id.menuitem_refresh);
        menuSearch = menu.findItem(R.id.menuitem_search);
        menuAdd = menu.findItem(R.id.menuitem_add);

        if (map!=null &&  map.containsKey("q") && !map.get("q").equals(""))
        {
            menuRefresh.setVisible(true);
            menuSearch.setVisible(false);
            menuAdd.setVisible(false);

        }
        else
        {
            menuRefresh.setVisible(false);
            menuSearch.setVisible(true);
            menuAdd.setVisible(true);
        }
     }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) 
	  {
	        switch (item.getItemId()) 
	        {
                case R.id.menuitem_refresh:

                    menuRefresh.setVisible(false);
                    menuSearch.setVisible(true);
                    menuAdd.setVisible(true);

                    onResume();

                    break;

	            case R.id.menuitem_add:
	            	startActivity(new Intent(context,TransactionActivity.class));
	            	break;
                case R.id.menuitem_search:
                    startActivityForResult(new Intent(context, SearchActivity.class), 10);
                    break;

                case R.id.menuitem_setting:
                	startActivity(new Intent(context, SettingActivity.class));
	            	break;

                case R.id.menuitem_share:
	            	share();
	            	break;

                case R.id.menuitem_backup:
                    backup("gmail");
                    break;

                case R.id.menuitem_about:
                    showAbout();

                    break;

            }

	        return super.onOptionsItemSelected(item);
	  }
	  
	@Override
	public void onResume()
	{
         if (query.equals(""))
         {
             context.setTitle("Transactions");
             super.onResume();

             onDataLoaded(TransactionFilter.All);

             Map<String,String> map=new HashMap<String, String>();
             map.put("q","");

             ((MainActivity)getActivity()).setQuery(map);

             if (isQueryMode) {

                 menuRefresh.setVisible(false);
                 menuSearch.setVisible(true);
                 menuAdd.setVisible(true);
             }


         }
         else
         {
             isQueryMode=true;

             context.setTitle("Search");
             super.onResume();

             onSearchLoaded();

             menuRefresh.setVisible(true);
             menuSearch.setVisible(false);
             menuAdd.setVisible(false);

             query="";
         }
    }
	 	
	private void ShowActivity(String id,Class<?> cls)
	{
		Bundle extras=new Bundle();
    	extras.putString(Store.TRANSACTION_ID, id);
    	
    	Intent i=new Intent(context,cls);
    	i.putExtras(extras);
    	
    	startActivityForResult(i, 1);
	}
	
	
	private void deleteTransaction()
	{	
		new AlertDialog.Builder(context)
		
		.setIcon(R.drawable.trash)
		.setTitle("Delete")
		.setMessage("Are you sure want to delete '" + description + "'?")
	    .setPositiveButton("Yes",
	                        new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog,
	                            int whichButton) 
	      {
	    	  Transaction transaction=transactionRepository.getById(Integer.parseInt(transactionId));
	    	  transactionRepository.delete(transaction);
	    	  
	    	  onDataLoaded(TransactionFilter.All);
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
		
}


