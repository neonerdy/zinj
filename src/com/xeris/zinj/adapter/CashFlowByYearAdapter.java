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
import com.xeris.zinj.model.CashFlow;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.Constant;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;

public class CashFlowByYearAdapter extends ArrayAdapter<CashFlow>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<CashFlow> data;
    private String currency="";
    private int month;
    private int year;

    public CashFlowByYearAdapter(Context context, int layoutResourceId, ArrayList<CashFlow> data)
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

            holder.lblType=(TextView)v.findViewById(R.id.lblCashFlowByYearType);
            holder.lblMonth=(TextView)v.findViewById(R.id.lblCashFlowByYearMonth);
            holder.lblAmount=(TextView)v.findViewById(R.id.lblCashFlowByYearAmount);
            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (ChartHolder)v.getTag();
        }

        CashFlow cashFlow = data.get(position);

        if (cashFlow!=null)
        {
            float total=0;

            if (cashFlow.getType().equals(Constant.TRANSACTION_INCOME))
            {
                total=chartRepository.getTotalAmount(CategoryType.Income, year);
            }
            else if (cashFlow.getType().equals(Constant.TRANSACTION_EXPENSE) || cashFlow.getType().equals(Constant.TRANSACTION_PAYMENT))
            {
                total=chartRepository.getTotalAmount(CategoryType.Expense, year);
            }

            float amount=cashFlow.getAmount();
            float percentage=(amount/total)*100;
            float val=percentage/100;

            String strAmount=String.valueOf(amount);

            holder.lblType.setText(cashFlow.getType());
            holder.lblMonth.setText(getMonthInitial(cashFlow.getMonth()));

            if (Math.round(percentage)==0) {
                holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " (" + Math.round(percentage) + "%)");
            }

            if (amount > 0 && total > 0)
            {
                Bitmap bmp=null;

                if (cashFlow.getType().equals(Constant.TRANSACTION_INCOME))
                {
                    bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.green_back);
                }
                else if (cashFlow.getType().equals(Constant.TRANSACTION_EXPENSE) || cashFlow.getType().equals(Constant.TRANSACTION_PAYMENT))
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
        TextView lblType;
        TextView lblMonth;
        TextView lblAmount;
        ImageView imgBar;
    }

}
