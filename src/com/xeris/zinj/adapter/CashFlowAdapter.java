package com.xeris.zinj.adapter;

import com.xeris.zinj.R;
import com.xeris.zinj.model.CashFlow;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.CategoryRepository;
import com.xeris.zinj.repository.ChartRepository;
import com.xeris.zinj.repository.SettingRepository;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CashFlowAdapter extends ArrayAdapter<CashFlow>
{
	private ChartRepository chartRepository;
	private SettingRepository settingRepository;
	
	private Context context; 
	int layoutResourceId;    
	private CashFlow data[] = null;
	private String currency="";
    private int month;
    private int year;
	
	public CashFlowAdapter(Context context, int layoutResourceId, CashFlow[] data) {

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

    private void drawBar(CashFlow cashFlow, View v, CashFlowHolder holder, float val)
    {
        Bitmap bmp=null;

        if (cashFlow.getType().equals("Income"))
        {
            bmp=BitmapFactory.decodeResource(v.getResources(), R.drawable.green_back);
        }
        else if (cashFlow.getType().equals("Expense"))
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


	
	@Override
    public View getView(int position, View v, ViewGroup parent)
	{
        CashFlowHolder holder = null;
        
        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new CashFlowHolder();
            
            holder.lblType=(TextView)v.findViewById(R.id.lblCashflowType);
            holder.lblAmount=(TextView)v.findViewById(R.id.lblCashflowAmount);
            holder.lblIncomeSpent=(TextView)v.findViewById(R.id.lblCashflowIncomeSpent);
            holder.lblBudgetSpent=(TextView)v.findViewById(R.id.lblCashflowBudgetSpent);

            holder.imgBar=(ImageView)v.findViewById(R.id.imageView1);
            
            v.setTag(holder);
        }
        else
        {
            holder = (CashFlowHolder)v.getTag();
        }        

        CashFlow cashFlow = data[position];

        float budgetPercentage=0;

        float totalCashFlow=chartRepository.getTotalCashFlow(month,year);
        CashFlow[] incomeExpense=chartRepository.getCashFlow(month,year);
        float totalIncome=incomeExpense[0].getAmount();

        float amount=cashFlow.getAmount();
        float totalBudgeted=chartRepository.getTotalBudgeted(cashFlow.getType());

        float totalTransfer = chartRepository.getTotalTransferAsSavings(month,year);
        String strTotalTransfer=String.valueOf(totalTransfer);

        float remain=totalBudgeted-amount;
        String strRemain=String.valueOf(Math.abs(remain));

        String strBudgeted=String.valueOf(totalBudgeted);
        String xBudget=currency +  strBudgeted.replaceAll("\\.0*$", "");

        if (totalBudgeted > 0) budgetPercentage=(amount/totalBudgeted)*100;

        float val=budgetPercentage/100;
        String strAmount=String.valueOf(amount);

        String leftOver;
        if (remain<0)
        {
            leftOver="Over";
        }
        else
        {
            leftOver="Left";
        }

        holder.lblType.setText(cashFlow.getType());

        if ((cashFlow.getType().equals("Income")))
        {
            float transferPercentage=0;
            if (totalIncome > 0) transferPercentage=(totalTransfer/totalIncome)*100;

            holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " of " + xBudget);

            if (Math.round(budgetPercentage)==0) {
                holder.lblBudgetSpent.setText(String.format("%.2f",budgetPercentage) + "% of target is fulfilled (" +  currency + strRemain.replaceAll("\\.0*$", "") + " " + leftOver + ")");
            }else {
                holder.lblBudgetSpent.setText(Math.round(budgetPercentage) + "% of target is fulfilled (" +  currency + strRemain.replaceAll("\\.0*$", "") +  " " + leftOver + ")");
            }

            if (Math.round(transferPercentage)==0) {
                holder.lblIncomeSpent.setText(String.format("%.2f",transferPercentage) + "% of income for savings (" + currency + strTotalTransfer.replaceAll("\\.0*$", "") + ")");
            } else {
                holder.lblIncomeSpent.setText(Math.round(transferPercentage) + "% of income for savings (" + currency + strTotalTransfer.replaceAll("\\.0*$", "") + ")");
            }
            //holder.lblIncomeSpent.setHeight(0);
        }
       else  if ((cashFlow.getType().equals("Expense")))
       {
           float incomeSpentPercentage=0;
           if (totalIncome > 0) incomeSpentPercentage=(amount/totalIncome)*100;

           holder.lblAmount.setText(currency + strAmount.replaceAll("\\.0*$", "") + " of " + xBudget);

           if (Math.round(budgetPercentage)==0) {
               holder.lblBudgetSpent.setText(String.format("%.2f",budgetPercentage) + "% of budget is used (" +  currency + strRemain.replaceAll("\\.0*$", "") + " " + leftOver + ")");
           }else {
               holder.lblBudgetSpent.setText(Math.round(budgetPercentage) + "% of budget is used (" +  currency + strRemain.replaceAll("\\.0*$", "") +  " " + leftOver + ")");
           }

           if (Math.round(incomeSpentPercentage)==0) {
               holder.lblIncomeSpent.setText(String.format("%.2f",incomeSpentPercentage) + "% of income is spent");
           } else {
               holder.lblIncomeSpent.setText(Math.round(incomeSpentPercentage) + "% of income is spent");
           }

       }

        if (amount > 0 && totalCashFlow > 0)
        {
	        drawBar(cashFlow, v, holder, val);
        }
        
        return v;
    }
    
    static class CashFlowHolder
    {
        TextView lblType;
        TextView lblAmount;
        TextView lblPercent;
        TextView lblRemain;
        TextView lblIncomeSpent;
        TextView lblBudgetSpent;
        ImageView imgBar;
        
    }
	
	
}
