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
import com.xeris.zinj.model.CashFlow;
import com.xeris.zinj.model.FinancialGoal;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import java.util.ArrayList;


public class FinancialGoalAdapter extends ArrayAdapter<FinancialGoal>
{
    private ChartRepository chartRepository;
    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<FinancialGoal> data;
    private String currency="";

    public FinancialGoalAdapter(Context context, int layoutResourceId, ArrayList<FinancialGoal> data)
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
        FinancialGoalHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new FinancialGoalHolder();

            holder.lblID=(TextView)v.findViewById(R.id.lblChartFinancialGoalAccountId);
            holder.lblFinancialGoal=(TextView)v.findViewById(R.id.lblChartFinancialGoalName);
            holder.lblAccount=(TextView)v.findViewById(R.id.lblChartFinancialGoalAccountName);
            holder.lblBalance=(TextView)v.findViewById(R.id.lblChartFinancialGoalAccountBalance);
            holder.lblRemain=(TextView)v.findViewById(R.id.lblChartFinancialGoalRemain);
            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);

            v.setTag(holder);
        }
        else
        {
            holder = (FinancialGoalHolder)v.getTag();
        }

        FinancialGoal financialGoal = data.get(position);

        if (financialGoal!=null)
        {
            float balance=chartRepository.getAccountBalance(financialGoal.getAccountId());
            float target=financialGoal.getGoalTarget();
            float remain=financialGoal.getGoalTarget()-balance;
            float percentage=(balance/target)*100;
            float val=percentage/100;

            String strBalance=String.valueOf(balance);
            String strRemain=String.valueOf(Math.abs(remain));
            String strTarget=String.valueOf(target);

            //String budgeted=currency + " " + strTarget.replaceAll("\\.0*$", "");

            holder.lblID.setText(String.valueOf(financialGoal.getAccountId()));
            holder.lblFinancialGoal.setText(financialGoal.getGoalName());
            holder.lblAccount.setText(financialGoal.getAccountName());


            String xTarget=currency +  strTarget.replaceAll("\\.0*$", "");

            if (balance==0) {
                holder.lblBalance.setText(currency + "0 of " + xTarget + " (0%)");
            }else if(remain < 0 ) {
                holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", "") + " of " + xTarget);
            }else if (Math.round(percentage)==0) {
                holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", "") + " of " + xTarget + " (" + String.format("%.2f",percentage) + "%)");
            }else {
                holder.lblBalance.setText(currency + strBalance.replaceAll("\\.0*$", "") + " of " + xTarget + " (" + Math.round(percentage) + "%)");
            }


            if (remain<0)
            {
                holder.lblRemain.setText(currency + strRemain.replaceAll("\\.0*$", "") + " Over");
            }
            else
            {
                holder.lblRemain.setText(currency +  strRemain.replaceAll("\\.0*$", "") + " Left");
            }

            if (balance > 0 )
            {
                drawBar(v,holder,val);
            }
        }

        return v;

    }


    private void drawBar(View v, FinancialGoalHolder holder, float val)
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

    static class FinancialGoalHolder
    {
        TextView lblID;
        TextView lblFinancialGoal;
        TextView lblAccount;
        TextView lblBalance;
        TextView lblRemain;
        ImageView imgBar;
    }

}
