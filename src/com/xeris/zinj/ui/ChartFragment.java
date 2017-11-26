package com.xeris.zinj.ui;


import android.app.*;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.*;

import com.xeris.zinj.model.*;
import com.xeris.zinj.repository.*;
import com.xeris.zinj.repository.ChartRepository.CategoryOrderBy;
import com.xeris.zinj.repository.ChartRepository.AccountOrderBy;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChartFragment extends Fragment
{
	private Activity context;
	private ChartRepository chartRepository;
	private TransactionRepository transactionRepository;
	private AccountRepository accountRepository;
	private SettingRepository settingRepository;
    private CategoryRepository categoryRepository;
	
	private Button pnlChartTotal;
	private String chartActive;
    private int month;
    private int year;
    private Setting setting;

    private String type;
    private String categoryId;
    private String category;
    private String account;
    private String creditCard;
    private String chartMonth;

    private Map<String,String> map;
    private String[] items;

  	 @Override
	 public void onAttach(Activity activity)
	 {
	  	context=activity;
        super.onAttach(activity);
	 }	  
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	     	
		  setHasOptionsMenu(true);
	     
		  chartRepository=new ChartRepository(context);
		  transactionRepository=new TransactionRepository(context);
		  accountRepository=new AccountRepository(context);
		  settingRepository=new SettingRepository(context);
          categoryRepository=new CategoryRepository(context);

		  View rootView=inflater.inflate(R.layout.chart_fragment, container, false);
			
		  int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		  TextView title = (TextView)context.findViewById(titleId);
		  title.setTextColor(Color.WHITE); 
		  title.setText("Chart");





          return rootView;
	     
	 }
	 
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
		 super.onActivityCreated(savedInstanceState);
		 pnlChartTotal=(Button)context.findViewById(R.id.pnlChartTotal);

          map =((MainActivity)getActivity()).getQuery();

         if(map!=null && map.containsKey("view") && map.get("view").equals("pnlIncome"))
         {
            drawChart(CategoryType.Income, CategoryOrderBy.Amount);

            map=new HashMap<String, String>();
            map.put("view","");

            ((MainActivity)getActivity()).setQuery(map);
         }
         else if(map!=null && map.containsKey("view") && map.get("view").equals("pnlExpense"))
         {
             drawChart(CategoryType.Expense,CategoryOrderBy.Amount);
             map=new HashMap<String, String>();
             map.put("view","");

             ((MainActivity)getActivity()).setQuery(map);

         }
         else {


            getChartDefault();
         }
      }

    private void getChartDefault()
    {
        setting=settingRepository.getById(1);
        String chartDefault="";

        if (setting!=null)
        {
            chartDefault=setting.getDefaultChart();
        }

        if (chartDefault.equals("Cash Flow")) {
            drawCashFlow();
        }else if (chartDefault.equals("Income")) {
            drawChart(CategoryType.Income, CategoryOrderBy.Amount);
        }else if (chartDefault.equals("Expense")) {
            drawChart(CategoryType.Expense, CategoryOrderBy.Amount);
        }else if (chartDefault.equals("Monthly Budget")) {
            drawBudget();
        }else if (chartDefault.equals("Cash and Savings")) {
            drawCashAndSavings(AccountOrderBy.Balance);
        }else if (chartDefault.equals("Credit Card")) {
            drawCreditCardBalance();
        }
    }

    public AdapterView.OnItemLongClickListener lvwCashFlowOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            type=(String)((TextView)v.findViewById(R.id.lblCashflowType)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(type);

            items=new String[]{"Today","This Month","This Year","View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item)
                    {
                        case 0 :
                            if (type.equals("Income"))
                            {
                                drawChartByToday(CategoryType.Income,CategoryOrderBy.Amount);
                            }
                            else if (type.equals("Expense"))
                            {
                                drawChartByToday(CategoryType.Expense,CategoryOrderBy.Amount);
                            }
                            break;

                        case 1 :
                            if (type.equals("Income"))
                            {
                                drawChart(CategoryType.Income,CategoryOrderBy.Amount);
                            }
                            else if (type.equals("Expense"))
                            {
                                drawChart(CategoryType.Expense,CategoryOrderBy.Amount);
                            }
                            break;


                        case 2 :
                            if (type.equals("Income"))
                            {
                                drawCashFlowByYear(CategoryType.Income);
                            }
                            else if (type.equals("Expense"))
                            {
                                drawCashFlowByYear(CategoryType.Expense);
                            }
                            break;

                        case 3 :

                            if (type.equals("Income"))
                            {
                                drawCashFlowByYear(CategoryType.Income);
                            }
                            else if(type.equals("Expense"))
                            {
                                drawCashFlowByYear(CategoryType.Expense);
                            }
                            break;

                        case 4 :

                            map=new HashMap<String, String>();
                            map.put("view","CashFlow");
                            map.put("q",type);

                            ((MainActivity) getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;

                    }

                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };


    public AdapterView.OnItemLongClickListener lvwCashFlowYearOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            chartMonth=(String)((TextView)v.findViewById(R.id.lblCashFlowByYearMonth)).getText();
            type=(String)((TextView)v.findViewById(R.id.lblCashFlowByYearType)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(chartMonth);

            items=new String[]{"View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item)
                    {
                        case 0 :

                            map=new HashMap<String, String>();

                            map.put("view","CashFlowByYear");
                            map.put("type",type);
                            map.put("q",chartMonth);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;
                     }
               }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };



    public AdapterView.OnItemLongClickListener lvwChartOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            categoryId=(String)((TextView)v.findViewById(R.id.lblChartCategoryId)).getText();
            category=(String)((TextView)v.findViewById(R.id.lblChartCategory)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(category);

           items=new String[]{"This Year","View Transaction"};

           dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item)
                    {
                        case 0 :
                            if (chartActive.equals("Income"))
                            {
                                drawChartByYear(CategoryType.Income,Integer.parseInt(categoryId),category);
                            }
                            else if(chartActive.equals("Expense"))
                            {
                                drawChartByYear(CategoryType.Expense,Integer.parseInt(categoryId),category);
                            }

                            break;

                        case 1 :

                            map=new HashMap<String, String>();

                            map.put("view","IncomeExpense");
                            map.put("q",category);

                            ((MainActivity) getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;

                    }

                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };




    public AdapterView.OnItemLongClickListener lvwChartByYearOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            categoryId=(String)((TextView)v.findViewById(R.id.lblChartCategoryId)).getText();
            chartMonth=(String)((TextView)v.findViewById(R.id.lblChartMonth)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(chartMonth);

            items=new String[]{"View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item)
                    {
                        case 0 :

                            map=new HashMap<String, String>();

                            map.put("view","IncomeExpenseByYear");
                            map.put("id",categoryId);
                            map.put("q",chartMonth);
                            map.put("type",chartActive);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;

                    }

                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };


    public AdapterView.OnItemLongClickListener lvwBudgetOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            categoryId=(String)((TextView)v.findViewById(R.id.lblChartBudgetCategoryId)).getText();
            category = (String)((TextView) v.findViewById(R.id.lblChartBudgetCategory)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(category);

            items=new String[]{"This Year","View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                        case 0 :
                            drawBudgetByYear(Integer.parseInt(categoryId),category);
                            break;
                        case 1 :

                            map=new HashMap<String, String>();

                            map.put("view","Budget");
                            map.put("q",category);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;
                    }
                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };



    public AdapterView.OnItemLongClickListener lvwBudgetByYearOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            categoryId=(String)((TextView)v.findViewById(R.id.lblChartCategoryId)).getText();
            chartMonth=(String)((TextView)v.findViewById(R.id.lblChartBudgetMonth)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(category);

            items=new String[]{"View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                        case 0 :

                            map=new HashMap<String, String>();

                            map.put("view","BudgetByYear");
                            map.put("id",categoryId);
                            map.put("q",chartMonth);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;

                    }
                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };



    public AdapterView.OnItemLongClickListener lvwCashAndSavingsOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            account = (String)((TextView) v.findViewById(R.id.lblCashSavingAccount)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(account);

            items=new String[]{"View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                        case 0 :
                            map=new HashMap<String, String>();

                            map.put("view","Savings");
                            map.put("q",account);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);
                            break;
                    }
                 }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };


    public AdapterView.OnItemLongClickListener lvwCreditCardOnItemLongClickListener=new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id)
        {
            creditCard = (String)((TextView) v.findViewById(R.id.lblChartCreditCardName)).getText();

            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle(creditCard);

            items=new String[]{"View Transaction"};

            dialog.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                        case 0 :
                            map=new HashMap<String, String>();

                            map.put("view","CreditCard");
                            map.put("q",creditCard);

                            ((MainActivity)getActivity()).setQuery(map);
                            context.getActionBar().setSelectedNavigationItem(0);

                            break;
                    }
                }
            });

            AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    };



    public View.OnClickListener pnlChartTotalOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (chartActive.equals("CashAndSavings")) {
                popupCashSavingsClick(view);
            } else if (chartActive.equals("CreditCard")) {
                popupCreditCardClick(view);
            } else if (chartActive.equals("IncomeByYear")
                    || chartActive.equals("ExpenseByYear")) {
                //popupCashFlowByYearClick(view);
            }
            else
            {
                popupChartClick(view);
            }
        }
    };

    private void popupChartClick(View view)
    {
        PopupMenu menu=new PopupMenu(context,view);
        menu.getMenuInflater().inflate(R.menu.popup_chart_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String title=menuItem.getTitle().toString();

                if (title.equals("Sort By Category") && chartActive.equals("Income"))
                {
                    drawChart(CategoryType.Income, CategoryOrderBy.Category);
                }
                else if (title.equals("Sort By Category") && chartActive.equals("Expense"))
                {
                    drawChart(CategoryType.Expense, CategoryOrderBy.Category);
                }
                else if (title.equals("Sort By Category") && chartActive.equals("Debt"))
                {
                    drawChart(CategoryType.Debt, CategoryOrderBy.Category);
                }
                else if (title.equals("Sort By Amount") && chartActive.equals("Income"))
                {
                    drawChart(CategoryType.Income, CategoryOrderBy.Amount);
                }
                else if (title.equals("Sort By Amount") && chartActive.equals("Expense"))
                {
                    drawChart(CategoryType.Expense, CategoryOrderBy.Amount);
                }
                else if (title.equals("Sort By Amount") && chartActive.equals("Debt"))
                {
                    drawChart(CategoryType.Debt, CategoryOrderBy.Amount);
                }

              return true;
            }
        });


        menu.show();
    }


    private void popupCashSavingsClick(View view)
    {
        PopupMenu menu=new PopupMenu(context,view);
        menu.getMenuInflater().inflate(R.menu.popup_cashsavings_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String title=menuItem.getTitle().toString();

                if (title.equals("Sort By Account"))
                {
                    drawCashAndSavings(AccountOrderBy.Account);
                }
                else if (title.equals("Sort By Balance"))
                {
                    drawCashAndSavings(AccountOrderBy.Balance);
                }

                return true;
            }
        });


        menu.show();
    }

    private void popupCreditCardClick(View view)
    {

        final PopupMenu menu=new PopupMenu(context,view);
        menu.getMenuInflater().inflate(R.menu.popup_creditcard_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String title=menuItem.getTitle().toString();

                menu.getMenu().getItem(0).setChecked(true);

                if (title.equals("Total Balance"))
                {
                    drawCreditCardBalance();
                }
                else if (title.equals("Total Unpaid"))
                {
                    //drawCreditCardDebt();

                    String strTotal=String.valueOf(chartRepository.getTotalCreditCardDebt());
                    pnlChartTotal.setText(settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

                }

                return true;
            }
        });

        menu.show();

    }




    private void showStatistics(String average)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        StringBuilder msg=new StringBuilder();

        msg.append("Average : " + average);

        alertDialog.setTitle("Statistics");
        alertDialog.setMessage(msg.toString());

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }



    /*
    private void popupCashFlowByYearClick(View view)
    {
        PopupMenu menu=new PopupMenu(context,view);
        menu.getMenuInflater().inflate(R.menu.popup_statistics_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String title=menuItem.getTitle().toString();

                if (title.equals("Show Statistics"))
                {
                    Statistics stat=new Statistics(context);
                    float average=0;

                    if (chartActive.equals("IncomeByYear"))
                    {
                        average=stat.calculateAverage(CategoryType.Income, setting.getYear());
                    }
                    else if (chartActive.equals("ExpenseByYear"))
                    {
                        average=stat.calculateAverage(CategoryType.Expense, setting.getYear());
                    }

                    showStatistics(String.valueOf(average));

                }

                return true;
            }
        });


        menu.show();
    }

*/


    public void drawCashFlow()
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

          CashFlow[] list=chartRepository.getCashFlow(month,year);
		  CashFlowAdapter adapter = new CashFlowAdapter(context, R.layout.cashflow_template, list);

          ListView lvwCashFlow = (ListView)context.findViewById(R.id.lvwChart);
          lvwCashFlow.setAdapter(adapter);

		  chartActive="CashFlow";
		  context.setTitle("Cash Flow | " + Store.getMonthInitial(month) + " " + year);
		
		  float surplusDeficit=chartRepository.getSurplusDefisit(month,year);
		  String strTotal="";

		  if (surplusDeficit > 0)
		  {
              strTotal=String.valueOf(surplusDeficit);
              pnlChartTotal.setText("  + " + currency + strTotal.replaceAll("\\.0*$", "") + " (Surplus)");
		  }
		  else if (surplusDeficit < 0)
		  {
              strTotal=String.valueOf(Math.abs(surplusDeficit));
              pnlChartTotal.setText("  - " + currency  + strTotal.replaceAll("\\.0*$", "") + " (Deficit)");
		  }
		  else
		  {
              strTotal=String.valueOf(surplusDeficit);
              pnlChartTotal.setText("  " + currency + strTotal.replaceAll("\\.0*$", ""));
		  }

          lvwCashFlow.setOnItemClickListener(lvwCashflowOnItemClickListener);
          lvwCashFlow.setOnItemLongClickListener(lvwCashFlowOnItemLongClickListener);

          pnlChartTotal.setOnClickListener(null);
          pnlChartTotal.setCompoundDrawables(null,null,null,null);
	      
	  }


    public void drawChartByToday(CategoryType type, CategoryOrderBy orderBy)
    {
        setting=settingRepository.getById(1);
        if (setting!=null) {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<Chart> data=chartRepository.getChartByTodayList(type,orderBy);

        ChartByTodayAdapter adapter = new ChartByTodayAdapter(context, R.layout.chart_template, data,type);

        ListView lvwIncomeExpense = (ListView)context.findViewById(R.id.lvwChart);
        lvwIncomeExpense = (ListView)context.findViewById(R.id.lvwChart);
        lvwIncomeExpense.setAdapter(adapter);

        chartActive=type.toString();

        context.setTitle(type.toString() + " | " + Store.getMonthInitial(month) + " " + year);

        String strTotal=String.valueOf(chartRepository.getTotalAmountByToday(type));
        pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);
        lvwIncomeExpense.setOnItemLongClickListener(lvwChartOnItemLongClickListener);
    }



    public void drawCashFlowByYear(CategoryType type)
    {
        setting=settingRepository.getById(1);
        if (setting!=null) {
            year=setting.getYear();
        }

        ArrayList<CashFlow> data=chartRepository.getCashFlowByYearList(type,year);

        CashFlowByYearAdapter adapter = new CashFlowByYearAdapter(context, R.layout.cashflow_byyear_template, data);
        ListView lvwCashFlow = (ListView)context.findViewById(R.id.lvwChart);
        lvwCashFlow.setAdapter(adapter);

        chartActive=type.toString() + "ByYear";

        context.setTitle(type.toString() + " (" + year + ")");

        String strTotal=String.valueOf(chartRepository.getTotalAmount(type,year));
        pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));



        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);

        lvwCashFlow.setOnItemClickListener(null);
        lvwCashFlow.setOnItemLongClickListener(lvwCashFlowYearOnItemLongClickListener);



    }




    public void drawChart(CategoryType type, CategoryOrderBy orderBy)
    {
        setting=settingRepository.getById(1);
        if (setting!=null) {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<Chart> data=chartRepository.getChartList(type,month,year,orderBy);

        ChartAdapter adapter = new ChartAdapter(context, R.layout.chart_template, data,type);

        ListView lvwIncomeExpense = (ListView)context.findViewById(R.id.lvwChart);
        lvwIncomeExpense = (ListView)context.findViewById(R.id.lvwChart);
        lvwIncomeExpense.setAdapter(adapter);

        chartActive=type.toString();

        if (type.toString().equals("Debt"))
        {
            context.setTitle("Debt Payment | " + Store.getMonthInitial(month) + " " + year);

            float ratio=0;

            float totalDebt=chartRepository.getTotalAmount(type,month,year);
            float totalIncome=chartRepository.getTotalAmount(CategoryType.Income, month, year);

            if (totalIncome > 0) ratio=(totalDebt/totalIncome)*100;

            String strTotal=String.valueOf(totalDebt);

            if (Math.round(ratio)==0) {
                pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", "") + " (" + String.format("%.2f",ratio) + "% of income is spent)");
            } else {
                pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", "") + " (" + Math.round(ratio) + "% of income is spent)");
            }
        }
        else
        {
            context.setTitle(type.toString() + " | " + Store.getMonthInitial(month) + " " + year);

            String strTotal=String.valueOf(chartRepository.getTotalAmount(type, month, year));
            pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));
        }

        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);
        lvwIncomeExpense.setOnItemLongClickListener(lvwChartOnItemLongClickListener);
    }



    public void drawChartByYear(CategoryType type,int categoryId,String categoryName)
    {
        setting=settingRepository.getById(1);
        if (setting!=null) {
            year=setting.getYear();
        }

        ArrayList<Chart> data=chartRepository.getChartByYearList(type,categoryId, year);

        ChartByYearAdapter adapter = new ChartByYearAdapter(context, R.layout.chart_byyear_template, data);
        ListView lvwIncomeExpense = (ListView)context.findViewById(R.id.lvwChart);
        lvwIncomeExpense.setAdapter(adapter);

        context.setTitle(categoryName + " (" + year + ")");

        String strTotal=String.valueOf(chartRepository.getTotalAmountByYear(type,categoryId,year));
        pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

        pnlChartTotal.setCompoundDrawables(null,null,null,null);
        pnlChartTotal.setOnClickListener(null);
        lvwIncomeExpense.setOnItemLongClickListener(lvwChartByYearOnItemLongClickListener);
    }


    public void drawCashAndSavings(AccountOrderBy orderBy)
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<Account> data=chartRepository.getCashAndSavingsList(orderBy);

        CashSavingsAdapter dataAdapter=new CashSavingsAdapter(context,R.layout.cashsavings_template,data);
        ListView lvwCashAndSaving = (ListView) context.findViewById(R.id.lvwChart);
        lvwCashAndSaving.setAdapter(dataAdapter);

        chartActive="CashAndSavings";
        context.setTitle("Cash \u0026 Savings");

        String strTotal=String.valueOf(chartRepository.getTotalCashAndSavings());
        pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);
        lvwCashAndSaving.setOnItemLongClickListener(lvwCashAndSavingsOnItemLongClickListener);

    }


    public void drawBudget()
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
        }

        String currency= settingRepository.getCurrency();

        ArrayList<Budget> data=chartRepository.getBudgetList();

        BudgetAdapter dataAdapter=new BudgetAdapter(context, R.layout.budget_template,data);
        ListView lvwBudget = (ListView) context.findViewById(R.id.lvwChart);
        lvwBudget.setAdapter(dataAdapter);

        chartActive="Budget";
        context.setTitle("Budget | " + Store.getMonthInitial(month) + " " + year);

        float totalBudgetSpent=chartRepository.getTotalBudgetSpent(month,year);
        float totalBudgeted=chartRepository.getTotalBudgeted("Expense");

        String strTotalBudgetSpent = String.valueOf(totalBudgetSpent).replaceAll("\\.0*$", "");
        String strTotalBudgeted = String.valueOf(totalBudgeted).replaceAll("\\.0*$", "");

        pnlChartTotal.setText(currency + strTotalBudgetSpent + " of " + currency + strTotalBudgeted);

        pnlChartTotal.setOnClickListener(null);
        pnlChartTotal.setCompoundDrawables(null, null, null, null);

        lvwBudget.setOnItemLongClickListener(lvwBudgetOnItemLongClickListener);

    }


    public void drawBudgetByYear(int categoryId,String categoryName)
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            year=setting.getYear();
        }

        String currency= settingRepository.getCurrency();

        ArrayList<Budget> data=chartRepository.getBudgetListByYear(categoryId,year);

        BudgetByYearAdapter dataAdapter=new BudgetByYearAdapter(context, R.layout.budget_byyear_template,data);
        ListView lvwBudget = (ListView) context.findViewById(R.id.lvwChart);
        lvwBudget.setAdapter(dataAdapter);

        context.setTitle(categoryName + " (" + year + ")");

        float totalBudgetSpentByYear=chartRepository.getTotalBudgetSpentByYear(categoryId,year);
        Category category=categoryRepository.getById(categoryId);
        float totalBudgetedByYear=lvwBudget.getCount() * category.getBudget();

        String strTotalBudgetSpentByYear = String.valueOf(totalBudgetSpentByYear).replaceAll("\\.0*$", "");
        String strTotalBudgetedByYear = String.valueOf(totalBudgetedByYear).replaceAll("\\.0*$", "");

        pnlChartTotal.setText(currency + strTotalBudgetSpentByYear + " of " + currency +  strTotalBudgetedByYear);

        pnlChartTotal.setOnClickListener(null);
        pnlChartTotal.setCompoundDrawables(null, null, null, null);


        lvwBudget.setOnItemLongClickListener(lvwBudgetByYearOnItemLongClickListener);

    }



    public void drawCreditCardBalance()
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<CreditCard> data=chartRepository.getCreditCardList();

        CreditCardBalanceAdapter dataAdapter=new CreditCardBalanceAdapter(context, R.layout.creditcard_balance_template,data);
        ListView lvwCreditCard = (ListView) context.findViewById(R.id.lvwChart);
        lvwCreditCard.setAdapter(dataAdapter);

        chartActive="CreditCard";
        context.setTitle("Credit Card Balance");

        String strTotal=String.valueOf(chartRepository.getTotalCreditCardBalance());
        pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);
        lvwCreditCard.setOnItemLongClickListener(lvwCreditCardOnItemLongClickListener);
     }



    public void drawCreditCardPayment()
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<Payment> data=chartRepository.getCreditCardPayment();

        CreditCardPaymentAdapter dataAdapter=new CreditCardPaymentAdapter(context, R.layout.creditcard_payment_template,data);
        ListView lvwCreditCardPayment = (ListView) context.findViewById(R.id.lvwChart);
        lvwCreditCardPayment.setAdapter(dataAdapter);

        chartActive="Payment";
        context.setTitle("Payment");

        //String strTotal=String.valueOf(chartRepository.getTotalCreditCardBalance());
        //pnlChartTotal.setText("  " + settingRepository.getCurrency() + strTotal.replaceAll("\\.0*$", ""));

        Drawable img = context.getResources().getDrawable( R.drawable.popup);
        img.setBounds( 5, 0, 55, 50 );
        pnlChartTotal.setCompoundDrawables(img,null,null,null);

        pnlChartTotal.setOnClickListener(pnlChartTotalOnClickListener);
        lvwCreditCardPayment.setOnItemLongClickListener(lvwCreditCardOnItemLongClickListener);
    }




    public void drawFinancialGoal()
    {
        ArrayList<FinancialGoal> data=chartRepository.getFinancialGoalList();

        FinancialGoalAdapter dataAdapter=new FinancialGoalAdapter(context, R.layout.financialgoal_template,data);
        ListView lvwFinancialGoal = (ListView) context.findViewById(R.id.lvwChart);
        lvwFinancialGoal.setAdapter(dataAdapter);

        chartActive="FinancialGoal";
        context.setTitle("Financial Goal");
    }






    /*
    public void drawCreditCardDebt()
    {
        setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
        }

        ArrayList<CreditCard> data=chartRepository.getCreditCardList();

        CreditCardDebtAdapter dataAdapter=new CreditCardDebtAdapter(context, R.layout.creditcard_debt_template,data);
        ListView lvwCreditCard = (ListView) context.findViewById(R.id.lvwChart);
        lvwCreditCard.setAdapter(dataAdapter);

        chartActive="CreditCard";
        context.setTitle("Credit Card Debt");
        pnlChartTotal.setText("");

        lvwCreditCard.setOnItemLongClickListener(lvwCreditCardOnItemLongClickListener);

    }

    */


	  public OnItemClickListener lvwCashflowOnItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) 
		{
			
			if (chartActive.equals("CashFlow"))
			{
				String type = (String)((TextView) v.findViewById(R.id.lblCashflowType)).getText();
							
				if (type.equals("Income"))
				{
					drawChart(CategoryType.Income,CategoryOrderBy.Amount);
				}
				else if(type.equals("Expense"))
				{
                    drawChart(CategoryType.Expense,CategoryOrderBy.Amount);
			    }
			}
			
		}
	  };
	  


    @Override
	  public boolean onOptionsItemSelected(MenuItem item) 
	  {
		    
		    switch (item.getItemId()) 
	        {	           
	        	case R.id.menuitem_chart_cashflow :
	        		pnlChartTotal.setVisibility(View.VISIBLE);
	        		drawCashFlow();
	        		break;
	        		
	        	case R.id.menuitem_chart_income:
	           		pnlChartTotal.setVisibility(View.VISIBLE);
                    drawChart(CategoryType.Income,CategoryOrderBy.Amount);
                    break;

	        	case R.id.menuitem_chart_expense :
	           		pnlChartTotal.setVisibility(View.VISIBLE);
                    drawChart(CategoryType.Expense,CategoryOrderBy.Amount);
                    break;

                case R.id.menuitem_chart_debt_payment :
                    pnlChartTotal.setVisibility(View.VISIBLE);
                    drawChart(CategoryType.Debt,CategoryOrderBy.Amount);
                    break;

                case R.id.menuitem_chart_budget :
	        		pnlChartTotal.setVisibility(View.VISIBLE);
	           		drawBudget();
	        		break;

                case R.id.menuitem_chart_cashsavings :
                    pnlChartTotal.setVisibility(View.VISIBLE);
                    drawCashAndSavings(AccountOrderBy.Balance);
                    break;

                case R.id.menuitem_chart_creditcard:
            		pnlChartTotal.setVisibility(View.VISIBLE);
                    drawCreditCardBalance();
                    break;

                case R.id.menuitem_chart_financialgoal:
                    pnlChartTotal.setVisibility(View.INVISIBLE);
                    drawFinancialGoal();
                    break;

            }

	        return super.onOptionsItemSelected(item);
	  }
	  
	  
	  @Override
	  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	  {   	
		   inflater.inflate(R.menu.chart_menu, menu);
	  }
	  
}
