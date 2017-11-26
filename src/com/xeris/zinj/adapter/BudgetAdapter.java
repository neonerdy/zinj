package com.xeris.zinj.adapter;


import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import com.xeris.zinj.R;
import com.xeris.zinj.model.*;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class BudgetAdapter extends ArrayAdapter<Budget>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Budget> data;
    private String currency="";
    private int month;
    private int year;

    public BudgetAdapter(Context context, int layoutResourceId, ArrayList<Budget> data)
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v=convertView;
        BudgetHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new BudgetHolder();

            holder.lblID=(TextView)v.findViewById(R.id.lblChartBudgetCategoryId);
            holder.lblCategory=(TextView)v.findViewById(R.id.lblChartBudgetCategory);
            holder.lblUsed=(TextView)v.findViewById(R.id.lblChartBudgetUsed);
            holder.lblRemain=(TextView)v.findViewById(R.id.lblChartBudgetRemain);

            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (BudgetHolder)v.getTag();
        }

        Budget budget = data.get(position);

        if (budget!=null)
        {
            float used=chartRepository.getUsedBudget(budget.getCategoryId(),month,year);
            float remain=budget.getBudgeted()-used;
            float percentage=(used/budget.getBudgeted())*100;
            float val=percentage/100;

            String strUsed=String.valueOf(used);
            String strRemain=String.valueOf(Math.abs(remain));
            String strBudgeted=String.valueOf(budget.getBudgeted());

            String budgeted=currency + " " + strBudgeted.replaceAll("\\.0*$", "");

            holder.lblID.setText(String.valueOf(budget.getCategoryId()));
            holder.lblCategory.setText(budget.getCategoryName());

            String xBudget=currency +  strBudgeted.replaceAll("\\.0*$", "");

            if (used==0)
            {
                holder.lblUsed.setText(currency + "0 of " + xBudget + " (0%)");
            }
            else if(remain < 0 )
            {
                holder.lblUsed.setText(currency + strUsed.replaceAll("\\.0*$", "") + " of " + xBudget + " ( > 100%)");
            }
            else if (Math.round(percentage)==0) {
                holder.lblUsed.setText(currency + strUsed.replaceAll("\\.0*$", "") + " of " + xBudget + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblUsed.setText(currency + strUsed.replaceAll("\\.0*$", "") + " of " + xBudget + " (" + Math.round(percentage) + "%)");
            }


            if (remain<0)
            {
                holder.lblRemain.setText(currency + strRemain.replaceAll("\\.0*$", "") + " Over");
            }
            else
            {
                holder.lblRemain.setText(currency +  strRemain.replaceAll("\\.0*$", "") + " Left");
            }

            if (used > 0 )
            {
                Bitmap bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.orange_back);

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
        }

        return v;

    }


    static class BudgetHolder
    {
        TextView lblID;
        TextView lblCategory;
        TextView lblBudgeted;
        TextView lblUsed;
        TextView lblRemain;
        ImageView imgBar;
    }

}
