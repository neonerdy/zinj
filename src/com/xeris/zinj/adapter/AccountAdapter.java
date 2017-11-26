package com.xeris.zinj.adapter;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xeris.zinj.R;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;

import android.content.Context;

import java.util.ArrayList;



public class AccountAdapter extends ArrayAdapter<Account>
{

    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Account> data;
    private String currency="";

    public AccountAdapter(Context context, int layoutResourceId, ArrayList<Account> data)
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
        AccountHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new AccountHolder();

            holder.lblId=(TextView)v.findViewById(R.id.lblAccountId);
            holder.lblName=(TextView)v.findViewById(R.id.lblAccountName);
            holder.lblType=(TextView)v.findViewById(R.id.lblAccountType);
            holder.lblBalance=(TextView)v.findViewById(R.id.lblAccountBalance);
            holder.lblNotes=(TextView)v.findViewById(R.id.lblAccountNotes);
            holder.lblLimit=(TextView)v.findViewById(R.id.lblAccountLimit);
            holder.imgBookmark=(ImageView)v.findViewById(R.id.imgBookmark);

            v.setTag(holder);
        }
        else
        {
            holder = (AccountHolder)v.getTag();
        }

        Account account = data.get(position);

        if (account!=null)
        {

            if (account.getType().equals("Credit Card"))
            {
                holder.imgBookmark.setBackgroundResource(R.drawable.bookmark_purple);
            }else {
                holder.imgBookmark.setBackgroundResource(R.drawable.bookmark_cadetblue);
            }

            holder.lblId.setText(String.valueOf(account.getId()));
            holder.lblName.setText(account.getName());
            holder.lblType.setText(account.getType());

            if (account.getNotes().equals(""))
            {
                holder.lblNotes.setHeight(0);
            }
            else
            {
                holder.lblNotes.setHeight(50);
                holder.lblNotes.setText(account.getNotes());
            }

            if (account.getLimit()==0)
            {
                holder.lblLimit.setHeight(0);
            }
            else
            {
                String strLimit=String.valueOf(account.getLimit());

                holder.lblLimit.setHeight(50);
                holder.lblLimit.setText("Limit : " + currency + strLimit.replaceAll("\\.0*$", ""));
            }

            String strBalance=String.valueOf(account.getBalance());
            holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", ""));
        }

        return v;
    }

    static class AccountHolder
    {
        TextView lblId;
        TextView lblName;
        TextView lblType;
        TextView lblBalance;
        TextView lblNotes;
        TextView lblLimit;
        ImageView imgBookmark;
    }
}



