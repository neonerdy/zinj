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
import com.xeris.zinj.model.Payment;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;

public class CreditCardPaymentAdapter extends ArrayAdapter<Payment>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Payment> data;
    private String currency="";

    public CreditCardPaymentAdapter(Context context, int layoutResourceId, ArrayList<Payment> data)
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

            holder.lblCategory=(TextView)v.findViewById(R.id.lblChartPaymentCategory);
            holder.lblCreditCard=(TextView)v.findViewById(R.id.lblChartPaymentCreditCard);
            holder.lblPaymentAmount=(TextView)v.findViewById(R.id.lblChartPaymentAmount);
            holder.lblPaymentLeft=(TextView)v.findViewById(R.id.lblChartPaymentLeft);

            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (CreditCardHolder)v.getTag();
        }

        Payment creditCardPayment = data.get(position);

        if (creditCardPayment!=null)
        {
            float totalUsage=chartRepository.getTotalCreditCardUsage(creditCardPayment.getCategoryId());
            float paymentAmount=creditCardPayment.getPaymentAmount();
            float paymentLeft=totalUsage-paymentAmount;

            float percentage=(paymentAmount/totalUsage)*100;
            float val=percentage/100;

            String strPaymentAmount=String.valueOf(paymentAmount);
            String strPaymentLeft=String.valueOf(paymentLeft);
            String strTotalUsage=String.valueOf(totalUsage);

            holder.lblCategory.setText(creditCardPayment.getCategoryName());
            holder.lblCreditCard.setText(creditCardPayment.getCreditCardName());

            if (paymentAmount==0) {
                holder.lblPaymentAmount.setText(currency + " 0 (0%)");
            }else if (Math.round(percentage)==0) {
                holder.lblPaymentAmount.setText(currency + strPaymentAmount.replaceAll("\\.0*$", "")
                        + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblPaymentAmount.setText(currency + strPaymentAmount.replaceAll("\\.0*$", "")
                        + " of " + strTotalUsage + " (" + Math.round(percentage) + "%)");
            }

            holder.lblPaymentLeft.setText(currency + strPaymentLeft.replaceAll("\\.0*$", "") + " Left");

            if (paymentAmount > 0 && totalUsage > 0)
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
        TextView lblCategory;
        TextView lblCreditCard;
        TextView lblPaymentAmount;
        TextView lblPaymentLeft;
        ImageView imgBar;
    }


}
