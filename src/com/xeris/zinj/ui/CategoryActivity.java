package com.xeris.zinj.ui;

import java.util.ArrayList;


import com.xeris.zinj.R;
import com.xeris.zinj.adapter.SpinnerAdapter;
import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.repository.CategoryRepository;
import com.xeris.zinj.repository.Store;
import com.xeris.zinj.repository.Store.ActivityMode;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.widget.Spinner;
import android.widget.TextView;

public class CategoryActivity extends Activity {

	private ArrayList<SpinnerItem> items;
	private CategoryRepository categoryRepository;
	
	private String categoryId;
	
	private EditText txtName;
	private RadioButton rbIncome;
	private RadioButton rbExpense;
	private CheckBox chkIsBudgeted;
    private CheckBox chkIsActive;
	private Spinner cboGroup;
	private EditText txtBudget;

    private TextView lblMonthlyBudget;
	
	private Bundle extras;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
		bar.setHomeButtonEnabled(true);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.WHITE);
		
		categoryRepository=new CategoryRepository(this);
		
		items = new ArrayList<SpinnerItem>();

		txtName=(EditText)findViewById(R.id.txtCategoryName);
		rbIncome=(RadioButton)findViewById(R.id.rbIncome);
		rbExpense=(RadioButton)findViewById(R.id.rbExpense);
		chkIsBudgeted=(CheckBox)findViewById(R.id.chkIsBudgeted);
        chkIsActive=(CheckBox)findViewById(R.id.chkIsActive);
        cboGroup = (Spinner) findViewById(R.id.cboGroup);
		txtBudget=(EditText)findViewById(R.id.txtCategoryBudget);
        lblMonthlyBudget=(TextView)findViewById(R.id.lblMonthlyBudget);

        addExpenseGroup();
			
		extras=getIntent().getExtras();
		
		if (extras!=null)
		{
			categoryId=extras.getString(Store.CATEGORY_ID);
			setTitle("Edit Category");
			editCategory();
		}
		else 
		{
			setTitle("Add Category");
			fillSpinnerForAdd();		
		}
		
 	}
	



	private void fillSpinnerForEdit(Category category)
	{
		ArrayAdapter<String> dataAdapter=null;
		
		String[] incomeItems={"Asset","Bonus","Business","Investment","Other Income","Receivable","Salary"};
		
		String[] expenseItems={"Bills","Car","Debt/Payable","Education","Entertainment","Food","Gadget",
				  "Groceries","Hobbies","Household","Health Care","Insurance","Other Expense","Personal Care","Pets",
				  "Shopping","Social","Sport & Recreation","Tax","Transportation","Utilities","Vacation"};
	
		if (category.getType().equals("Income"))
		{
			rbIncome.setChecked(true);
			dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, incomeItems);
		}
		
		if (category.getType().equals("Expense"))
		{
			rbExpense.setChecked(true);
			dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseItems);
		}
		
	    cboGroup.setAdapter(dataAdapter);
	    cboGroup.setSelection(((ArrayAdapter<String>)cboGroup.getAdapter()).getPosition((category.getGroup())));
	    
	    
	}

	
	
	private void editCategory()
	{
		Category category=categoryRepository.getById(Integer.parseInt(categoryId));
	
		if (category!=null) 
		{	
			txtName.setText(category.getName());
			fillSpinnerForEdit(category);

            String budget=String.valueOf(category.getBudget());

            if (category.getIsBudgeted()==1)
			{
				chkIsBudgeted.setChecked(true);
				txtBudget.setEnabled(true);
                txtBudget.setText(budget.replaceAll("\\.0*$", ""));
			}
            else
            {
                txtBudget.setText("");
            }

            if (category.getType().equals("Income"))
            {
                lblMonthlyBudget.setText("MONTHLY TARGET");
                chkIsBudgeted.setText("Is Targeted");
                txtBudget.setHint("Target");

                //lblMonthlyBudget.setVisibility(View.INVISIBLE);
                //chkIsBudgeted.setVisibility(View.INVISIBLE);
                //txtBudget.setVisibility(View.INVISIBLE);
            }
            else if(category.getType().equals("Expense"))
            {
                lblMonthlyBudget.setText("MONTHLY BUDGET");
                chkIsBudgeted.setText("Is Budgeted");
                txtBudget.setHint("Budget");

                //lblMonthlyBudget.setVisibility(View.VISIBLE);
                //chkIsBudgeted.setVisibility(View.VISIBLE);
                //txtBudget.setVisibility(View.VISIBLE);
            }

            chkIsActive.setChecked(category.getIsActive()==1?true:false);

		}
	}

	

	private void fillSpinnerForAdd() 
	{

        SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, items);
        cboGroup.setAdapter(adapter);
        cboGroup.setSelection(items.size() - 1);
               
        cboGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int posiiton, long id) {
			
				SpinnerItem data = items.get(posiiton);
				if (data.isHint())
				{
					 ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(148, 150, 148));
					 ((TextView) parent.getChildAt(0)).setTextSize(16);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	
	public void onRadioButtonClick(View v) 
	{	    
		RadioButton button = (RadioButton) v;
	 
		String label=button.getText().toString();
		
		items.clear();
		
		if (label.equals("Income"))
		{
            lblMonthlyBudget.setText("MONTHLY TARGET");
            chkIsBudgeted.setText("Is Targeted");
            txtBudget.setHint("Target");

            /*
            lblMonthlyBudget.setVisibility(View.GONE);
            chkIsBudgeted.setVisibility(View.GONE);
            txtBudget.setVisibility(View.GONE);
            */
            addIncomeGroup();
		}
		else if (label.equals("Expense"))
		{
            lblMonthlyBudget.setText("MONTHLY BUDGET");
            chkIsBudgeted.setText("Is Budgeted");
            txtBudget.setHint("Budget");

            /*
            lblMonthlyBudget.setVisibility(View.VISIBLE);
            chkIsBudgeted.setVisibility(View.VISIBLE);
            txtBudget.setVisibility(View.VISIBLE);
             */

            addExpenseGroup();
		}
		
		fillSpinnerForAdd(); 
		
	}
	
	public void onChkBudgetedClick(View v)
	{
		if (((CheckBox) v).isChecked())
		{
			txtBudget.setEnabled(true);
			txtBudget.setFocusable(true);
			txtBudget.requestFocus();
		}
		else 
		{
			txtBudget.setEnabled(false);
			txtBudget.setText("");
		}
		
	}


	private void addExpenseGroup()
	{		
		items.add(new SpinnerItem("Bills", false));
		items.add(new SpinnerItem("Car", false));
		items.add(new SpinnerItem("Debt/Payable", false));
		items.add(new SpinnerItem("Education", false));
		items.add(new SpinnerItem("Entertainment", false));
		items.add(new SpinnerItem("Food", false));
		items.add(new SpinnerItem("Gadget", false));
		items.add(new SpinnerItem("Groceries", false));
		items.add(new SpinnerItem("Hobbies", false));
		items.add(new SpinnerItem("Household", false));
		items.add(new SpinnerItem("Health Care", false));
		items.add(new SpinnerItem("Insurance", false));
		items.add(new SpinnerItem("Personal Care", false));
		items.add(new SpinnerItem("Pets", false));
		items.add(new SpinnerItem("Shopping", false));
		items.add(new SpinnerItem("Social", false));
		items.add(new SpinnerItem("Sport & Recreation", false));
		items.add(new SpinnerItem("Tax", false));
		items.add(new SpinnerItem("Transportation", false));
		items.add(new SpinnerItem("Utilities", false));
		items.add(new SpinnerItem("Vacation", false));
        items.add(new SpinnerItem("Other Expense", false));

		items.add(new SpinnerItem("Please Select", true));
	}


	private void addIncomeGroup() 
	{
		items.add(new SpinnerItem("Asset", false));
		items.add(new SpinnerItem("Bonus", false));
		items.add(new SpinnerItem("Business", false));
		items.add(new SpinnerItem("Investment", false));
		items.add(new SpinnerItem("Receivable", false));
		items.add(new SpinnerItem("Salary", false));
        items.add(new SpinnerItem("Other Income", false));

		items.add(new SpinnerItem("Please Select", true));
	}
	
	
	
	
	private void saveCategory(ActivityMode activityMode)
	{
		
		if (txtName.getText().toString().equals(""))
		{
 			Toast.makeText(this, "Category name can not be empty", Toast.LENGTH_LONG).show();
 	   	}
		else if (cboGroup.getSelectedItem().toString().equals("Please Select"))
		{
			Toast.makeText(this, "Please select category group", Toast.LENGTH_LONG).show();
		}
		else if (chkIsBudgeted.isChecked() && txtBudget.getText().toString().equals(""))
		{
			Toast.makeText(this, "Category needs monthly budget", Toast.LENGTH_LONG).show();
		}
        else if (chkIsBudgeted.isChecked() && txtBudget.getText().toString().equals("."))
        {
            Toast.makeText(this, "Incorrect budget", Toast.LENGTH_LONG).show();
        }
		else
		{

			Category category=new Category();
			
			category.setName(txtName.getText().toString().substring(0,1).toUpperCase() + txtName.getText().toString().substring(1));
					
			RadioGroup rbGroupType=(RadioGroup)findViewById(R.id.rbCategoryType);
			int selectedId = rbGroupType.getCheckedRadioButtonId();
			RadioButton rbType=(RadioButton)findViewById(selectedId);
			
			category.setType(rbType.getText().toString());
			category.setGroup(cboGroup.getSelectedItem().toString());
			
			if (chkIsBudgeted.isChecked())
			{
				category.setIsBudgeted(1);
				category.setBudget(Float.parseFloat(txtBudget.getText().toString()));
			}else {
				category.setIsBudgeted(0);
				category.setBudget(0);
			}

            if (chkIsActive.isChecked()) {
                category.setIsActive(1);
            }else {
                category.setIsActive(0);
            }

			if (activityMode==ActivityMode.Add)
			{
                if (categoryRepository.isCategoryExist(txtName.getText().toString()))
                {
                    Toast.makeText(this, "Category already exist", Toast.LENGTH_LONG).show();
                }
                else
                {
				    categoryRepository.save(category);
                    finish();
			    }
            }
			else
			{
                category.setId(Integer.parseInt(categoryId));
                categoryRepository.update(category);

                finish();
            }
			

	
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		if (extras==null){
			getMenuInflater().inflate(R.menu.add_menu, menu);
		}else{
			getMenuInflater().inflate(R.menu.update_menu, menu);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{		
		switch(item.getItemId())
		{
            case android.R.id.home:
                finish();
                break;
			case R.id.menuitem_save :
				saveCategory(ActivityMode.Add);
				break;
			case R.id.menuitem_update :
				saveCategory(ActivityMode.Edit);
				break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
}
