package com.xeris.zinj.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.xeris.zinj.R;
import com.xeris.zinj.adapter.SpinnerItem;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;
import com.xeris.zinj.repository.Store;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends Activity
{
    static final int DATE_DIALOG_FROM = 888;
    static final int DATE_DIALOG_TO = 999;

    private int year;
    private int month;
    private int day;

    private EditText txtDescription;
    private EditText txtNotes;
    private EditText txtAmount;
    private Spinner cboType;
    private Button btnFrom;
    private Button btnTo;

    private CheckBox chkType;
    private CheckBox chkDate;
    private Bundle extras;
    private String searchDescription;

    private SettingRepository settingRepository;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        setTitle("Search");
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4682b4")));
        bar.setHomeButtonEnabled(true);

        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView title = (TextView)findViewById(titleId);
        title.setTextColor(Color.WHITE);

        extras=getIntent().getExtras();

        settingRepository=new SettingRepository(this);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        txtDescription=(EditText)findViewById(R.id.txtSearchDescription);
        if (extras!=null) {
            searchDescription=extras.getString("SEARCH_TRANSACTION");
            txtDescription.setText(searchDescription);
        }

        txtNotes=(EditText)findViewById(R.id.txtSearchNotes);
        txtAmount=(EditText)findViewById(R.id.txtSearchAmount);

        cboType=(Spinner)findViewById(R.id.cboSearchType);
        cboType.setEnabled(false);

        btnFrom=(Button)findViewById(R.id.btnSearchFromDate);
        btnTo=(Button)findViewById(R.id.btnSearchToDate);

        chkType=(CheckBox)findViewById(R.id.chkSearchDate);
        chkDate=(CheckBox)findViewById(R.id.chkSearchDate);

        btnFrom.setText("  From");
        btnTo.setText("  To");

        btnFrom.setTextColor(Color.GRAY);
        btnTo.setTextColor(Color.GRAY);

        btnFrom.setEnabled(false);
        btnTo.setEnabled(false);


        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_FROM);
            }
        });

        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_TO);
            }
        });

        fillType();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_FROM:
                return new DatePickerDialog(this, dateFromPickerListener,
                        year, month,day);
            case DATE_DIALOG_TO:
                return new DatePickerDialog(this, dateToPickerListener,
                        year, month,day);

        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener dateToPickerListener=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            btnTo.setText("  " + (month+1) + "/" + day + "/" + year);

        }
    };

    private DatePickerDialog.OnDateSetListener dateFromPickerListener=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            btnFrom.setText("  " + (month+1) + "/" + day + "/" + year);

        }
    };


    private void fillType()
    {
        String[] types={"All","Income","Expense","Transfer","Withdrawal","Deposit","Credit","Payment"};

        ArrayAdapter<String> dataAdapter  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        cboType.setAdapter(dataAdapter);
        cboType.setSelection(0);
    }


    private String generateClause()
    {
        String clause="";

        int month=0;
        int year=0;
        String strMonth="";

        Setting setting=settingRepository.getById(1);
        if (setting!=null) {
            month=setting.getMonth();
            year=setting.getYear();
        }

        if (month < 10 )
        {
            strMonth="0" + month;
        }
        else
        {
            strMonth=String.valueOf(month);
        }

        String descriptionFilter="_description LIKE '%" + txtDescription.getText().toString().replace("'","") + "%'";
        String notesFilter="_notes LIKE '%" + txtNotes.getText().toString().replace("'","") + "%'";
        String amountFilter="_amount =" + txtAmount.getText().toString();
        String incomeFilter = "_type ='INCOME'";
        String expenseFilter = "_type IN ('EXPENSE','PAYMENT')";
        String transferFilter = "_type='TRANSFER'";
        String withdrawalFilter = "_type='WITHDRAWAL'";
        String depositFilter = "_type='DEPOSIT'";
        String creditFilter = "_type='CREDIT'";
        String paymentFilter = "_type='PAYMENT'";

        String dateFilter="";

        if (chkDate.isChecked())
        {
            SimpleDateFormat dateFormat=new SimpleDateFormat("MM/dd/yyyy");

            String fromDate="";
            String toDate="";

            Date d1=null;
            Date d2=null;

            try {
                d1=dateFormat.parse(btnFrom.getText().toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                d2=dateFormat.parse(btnTo.getText().toString().trim());
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");

            fromDate = timeFormat.format(d1);
            toDate = timeFormat.format(d2);

            dateFilter="_date BETWEEN '" + fromDate + "' AND '" + toDate + "'";
        }

        if (!txtDescription.getText().toString().isEmpty())
        {
            if (clause.isEmpty()) {
                clause=clause + descriptionFilter;
            }else {
                clause=clause+ " AND " + descriptionFilter;
            }
        }

        if (!txtNotes.getText().toString().isEmpty())
        {
            if (clause.isEmpty()) {
                clause=clause + notesFilter;
            }else {
                clause=clause+ " AND " + notesFilter;
            }
        }

        if (!txtAmount.getText().toString().isEmpty())
        {
            if (clause.isEmpty()) {
                clause=clause + amountFilter;
            }else {
                clause=clause+ " AND " + amountFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Income"))
        {
            if (clause.isEmpty()) {
                clause=clause + incomeFilter;
            }
            else {
                clause=clause+ " AND " + incomeFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Expense"))
        {
            if (clause.isEmpty()) {
                clause=clause + expenseFilter;
            }
            else {
                clause=clause+ " AND " + expenseFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Transfer"))
        {
            if (clause.isEmpty()) {
                clause=clause + transferFilter;
            }
            else {
                clause=clause+ " AND " + transferFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Withdrawal"))
        {
            if (clause.isEmpty()) {
                clause=clause + withdrawalFilter;
            }
            else {
                clause=clause+ " AND " + withdrawalFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Deposit"))
        {
            if (clause.isEmpty()) {
                clause=clause + depositFilter;
            }
            else {
                clause=clause+ " AND " + depositFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Credit"))
        {
            if (clause.isEmpty()) {
                clause=clause + creditFilter;
            }
            else {
                clause=clause+ " AND " + creditFilter;
            }
        }

        if (cboType.getSelectedItem().toString().equals("Payment"))
        {
            if (clause.isEmpty()) {
                clause=clause + paymentFilter;
            }
            else {
                clause=clause+ " AND " + paymentFilter;
            }
        }

        if (chkDate.isChecked())
        {
            if (clause.isEmpty()) {
                clause=clause + dateFilter;
            }
            else {
                clause=clause+ " AND " + dateFilter;
            }
        }
        else
        {

            clause=clause + " ORDER BY ";

            /*
            String yearFilter="strftime('%Y',_date)='" + year + "'";

            if (clause.isEmpty()) {
                clause=clause + yearFilter;
            }
            else {
                clause=clause + " AND " + yearFilter;
            }

            */

        }

        return clause;

    }


    public void onTypeChecked(View v)
    {
        if (((CheckBox) v).isChecked())
        {
            cboType.setEnabled(true);
        }
        else
        {
            cboType.setEnabled(false);
            cboType.setSelection(0);
        }
    }


    public void onDateChecked(View v)
    {
        if (((CheckBox) v).isChecked())
        {
            btnFrom.setEnabled(true);
            btnTo.setEnabled(true);
            btnFrom.setTextColor(Color.BLACK);
            btnTo.setTextColor(Color.BLACK);
        }
        else
        {
            btnFrom.setText("  From");
            btnTo.setText("  To");
            btnFrom.setEnabled(false);
            btnTo.setEnabled(false);
            btnFrom.setTextColor(Color.GRAY);
            btnTo.setTextColor(Color.GRAY);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.menuitem_done:

                if (chkDate.isChecked() && btnFrom.getText().toString().trim().equals("From"))
                {
                    Toast.makeText(this, "Please select from date",Toast.LENGTH_LONG).show();
                }
                else if (chkDate.isChecked() && btnTo.getText().toString().trim().equals("To"))
                {
                    Toast.makeText(this, "Please select to date",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("query", generateClause());
                    resultIntent.putExtra("type",cboType.getSelectedItem().toString());
                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();

                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}