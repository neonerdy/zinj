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
import com.xeris.zinj.model.CreditCard;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;

public class CreditCardBalanceAdapter extends ArrayAdapter<CreditCard>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<CreditCard> data;
    private String currency="";

    public CreditCardBalanceAdapter(Context context, int layoutResourceId, ArrayList<CreditCard> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        chartRepository=new ChartRepository(context);
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
        CreditCardHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new CreditCardHolder();

            holder.lblName=(TextView)v.findViewById(R.id.lblChartCreditCardName);
            holder.lblBalance=(TextView)v.findViewById(R.id.lblChartCreditCardAmount);
            holder.lblDebt=(TextView)v.findViewById(R.id.lblChartCreditCardDebt);
            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (CreditCardHolder)v.getTag();
        }

        CreditCard creditCard = data.get(position);

        if (creditCard!=null)
        {
            float total=chartRepository.getTotalCreditCardBalance();

            float balance=creditCard.getBalance();
            float limit=creditCard.getLimit();
            float unpaid=limit-balance;

            float percentage=(balance/total)*100;
            float val=percentage/100;

            String strBalance=String.valueOf(balance);
            String strLimit=String.valueOf(limit);
            String strUnpaid=String.valueOf(unpaid);

            holder.lblName.setText(creditCard.getName());

            if (balance==0) {
                holder.lblBalance.setText(currency + " 0 (0%)");
            }else if (Math.round(percentage)==0) {
                holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", "")
                        + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", "")
                        + " (" + Math.round(percentage) + "%)");
            }

            holder.lblDebt.setText("Unpaid : " + currency + strUnpaid.replaceAll("\\.0*$", ""));

            if (balance > 0 && limit > 0)
            {
                Bitmap bmp=bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.purple_back);

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
                img.setImageBitmap(resizedbitmap);
            }


        }

        return v;

    }

    static class CreditCardHolder
    {
        TextView lblName;
        TextView lblBalance;
        TextView lblDebt;
        ImageView imgBar;
    }


}
