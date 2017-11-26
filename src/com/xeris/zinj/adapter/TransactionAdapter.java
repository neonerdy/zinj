package com.xeris.zinj.adapter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xeris.zinj.R;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.model.Transaction;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.SettingRepository;
import com.xeris.zinj.repository.Store;


public class TransactionAdapter extends ArrayAdapter<Transaction>
{
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Transaction> data;
    private String currency="";

    public TransactionAdapter(Context context, int layoutResourceId, ArrayList<Transaction> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        settingRepository=new SettingRepository(context);
        Setting setting=settingRepository.getById(1);
        if (setting.getIsHideSymbol()==0)
        {
            currency=setting.getCurrency();
        }
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v=convertView;

        TransactionHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new TransactionHolder();

            holder.lblId=(TextView)v.findViewById(R.id.lblTransactionId);
            holder.lblDay=(TextView)v.findViewById(R.id.lblTransactionDay);
            holder.lblMonth=(TextView)v.findViewById(R.id.lblTransactionMonth);
            holder.lblDescription=(TextView)v.findViewById(R.id.lblTransactionDescription);
            holder.lblNotes=(TextView)v.findViewById(R.id.lblTransactionNotes);
            holder.lblType=(TextView)v.findViewById(R.id.lblTransactionType);
            holder.lblAmount=(TextView)v.findViewById(R.id.lblTransactionAmount);

            v.setTag(holder);
        }
        else
        {
            holder = (TransactionHolder)v.getTag();
        }

        Transaction transaction = data.get(position);

        if (transaction!=null)
        {
            holder.lblId.setText(String.valueOf(transaction.getId()));

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            String date=transaction.getDate();

            Calendar cal = Calendar.getInstance();

            int day=0;
            int month=0;

            try {
                Date td=dateFormat.parse(date);
                cal.setTime(td);

                day=cal.get(Calendar.DAY_OF_MONTH);
                month=cal.get(Calendar.MONTH)+1;
            } catch (ParseException e) {

            }

            holder.lblDay.setText(String.valueOf(day));
            holder.lblMonth.setText(Store.getMonthInitial(month).toUpperCase());
            holder.lblDescription.setText(transaction.getDescription());

            holder.lblNotes.setSingleLine(false);

            if (transaction.getType().equals(Constant.TRANSACTION_PAYMENT)) {
                holder.lblNotes.setText(transaction.getPaymentInfo()  + " (" + transaction.getNotes() + ")");
            } else {
                holder.lblNotes.setText(transaction.getNotes());

                if (transaction.getNotes().equals("")) {
                    holder.lblNotes.setHeight(0);
                }

            }

            holder.lblType.setText(transaction.getType());

            if (transaction.getType().equals(Constant.TRANSACTION_EXPENSE) || transaction.getType().equals(Constant.TRANSACTION_PAYMENT))
            {
                holder.lblAmount.setTextColor(Color.parseColor("#b22222"));
            }
            else if (transaction.getType().equals(Constant.TRANSACTION_INCOME))
            {
                holder.lblAmount.setTextColor(Color.parseColor("#228b22"));
            }
            else
            {
                holder.lblAmount.setTextColor(Color.parseColor("#000000"));
            }

            String strAmount=String.valueOf(transaction.getAmount());
            holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", ""));
        }




        return v;
   }



    static class TransactionHolder
    {
        TextView lblId;
        TextView lblDay;
        TextView lblMonth;
        TextView lblType;
        TextView lblDescription;
        TextView lblNotes;
        TextView lblAmount;
  }


}


