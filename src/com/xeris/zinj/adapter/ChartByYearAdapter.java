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
import com.xeris.zinj.model.Chart;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;

public class ChartByYearAdapter extends ArrayAdapter<Chart>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Chart> data;
    private String currency="";
    private int month;
    private int year;

    public ChartByYearAdapter(Context context, int layoutResourceId, ArrayList<Chart> data)
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

    private String getMonthInitial(String month)
    {
        String strMonth="";

        if (month.equals("01")) {
            strMonth="January";
        }else if (month.equals("02")) {
            strMonth="February";
        }else if (month.equals("03")) {
            strMonth="March";
        }else if(month.equals("04")) {
            strMonth="April";
        }else if(month.equals("05")) {
            strMonth="May";
        }else if(month.equals("06")) {
            strMonth="June";
        }else if(month.equals("07")) {
            strMonth="July";
        }else if(month.equals("08")) {
            strMonth="August";
        }else if(month.equals("09")) {
             strMonth="September";
        }else if(month.equals("10")) {
             strMonth="October";
        }else if(month.equals("11")) {
             strMonth="November";
        }else if(month.equals("12")) {
             strMonth="December";
        }

        return strMonth;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v=convertView;
        ChartHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new ChartHolder();

            holder.lblID=(TextView)v.findViewById(R.id.lblChartCategoryId);
            holder.lblMonth=(TextView)v.findViewById(R.id.lblChartMonth);
            holder.lblAmount=(TextView)v.findViewById(R.id.lblChartAmount);
            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (ChartHolder)v.getTag();
        }

        Chart chart = data.get(position);

        if (chart!=null)
        {
            float total=0;

            if (chart.getType().equals(Constant.TRANSACTION_INCOME))
            {
                total=chartRepository.getTotalAmountByYear(CategoryType.Income,chart.getId(),year);
            }
            else if (chart.getType().equals(Constant.TRANSACTION_EXPENSE) || chart.getType().equals(Constant.TRANSACTION_PAYMENT))
            {
                total=chartRepository.getTotalAmountByYear(CategoryType.Expense,chart.getId(),year);
            }

            float amount=chart.getAmount();
            float percentage=(amount/total)*100;
            float val=percentage/100;

            String strAmount=String.valueOf(amount);

            holder.lblID.setText(String.valueOf(chart.getId()));
            holder.lblMonth.setText(getMonthInitial(chart.getMonth()));

            if (Math.round(percentage)==0) {
                holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " (" + Math.round(percentage) + "%)");
            }

            if (amount > 0 && total > 0)
            {
                Bitmap bmp=null;

                if (chart.getType().equals(Constant.TRANSACTION_INCOME))
                {
                    bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.green_back);
                }
                else if (chart.getType().equals(Constant.TRANSACTION_EXPENSE) || chart.getType().equals(Constant.TRANSACTION_PAYMENT))
                {
                    bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.red_back);
                }

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

    static class ChartHolder
    {
        TextView lblID;
        TextView lblMonth;
        TextView lblAmount;
        ImageView imgBar;
    }

}
