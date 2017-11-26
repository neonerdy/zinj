package com.xeris.zinj.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xeris.zinj.R;
import com.xeris.zinj.model.Account;
import com.xeris.zinj.model.CashFlow;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;

public class CashSavingsAdapter extends ArrayAdapter<Account>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Account> data = null;
    private String currency="";
    private int month;
    private int year;

    public CashSavingsAdapter(Context context, int layoutResourceId, ArrayList<Account> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        chartRepository=new ChartRepository(context);
        settingRepository=new SettingRepository(context);

        Setting setting=settingRepository.getById(1);
        if (setting!=null)
        {
            month=setting.getMonth();
            year=setting.getYear();
            if (setting.getIsHideSymbol()==0)
            {
                currency=setting.getCurrency();
            }
        }

    }


    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        CashAndSavingsHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new CashAndSavingsHolder();

            holder.lblAccount=(TextView)v.findViewById(R.id.lblCashSavingAccount);
            holder.lblBalance=(TextView)v.findViewById(R.id.lblCashSavingBalance);
            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (CashAndSavingsHolder)v.getTag();
        }

        Account account = data.get(position);

        float totalCashAndSavings=chartRepository.getTotalCashAndSavings();
        float balance=account.getBalance();

        float percentage=(balance/totalCashAndSavings)*100;
        float val=percentage/100;

        String strBalance=String.valueOf(balance);

        holder.lblAccount.setText(account.getName());

        if (balance==0) {
            holder.lblBalance.setText(currency + " 0 (0%)");
        }else if (Math.round(percentage)==0) {
            holder.lblBalance.setText(currency + " " + strBalance.replaceAll("\\.0*$", "") + " (" + String.format("%.2f",percentage) + "%)");
        } else {
            holder.lblBalance.setText(currency + " " + strBalance.replaceAll("\\.0*$", "") + " (" + Math.round(percentage) + "%)");
        }

        if (balance > 0 && totalCashAndSavings > 0)
        {
            Bitmap bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.darkgreen_back);

            Display d = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int screenWidth = d.getWidth()-20;
            int screenHeight = d.getHeight();

            ImageView img=(ImageView)v.findViewById(R.id.imageView1);
            img.setVisibility(1);

            int width=Math.round(screenWidth * val);
            int height=30;

            if (width >= screenWidth) width=screenWidth;
            if (width <= 0) width=1;

            Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
            holder.imgBar.setImageBitmap(resizedbitmap);
        }


        return v;
    }

    static class CashAndSavingsHolder
    {
        TextView lblAccount;
        TextView lblBalance;
        ImageView imgBar;
    }


}
