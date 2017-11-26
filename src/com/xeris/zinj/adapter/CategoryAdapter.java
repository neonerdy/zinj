package com.xeris.zinj.adapter;

import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import com.xeris.zinj.R;
import com.xeris.zinj.model.Category;
import com.xeris.zinj.model.Setting;
import com.xeris.zinj.repository.SettingRepository;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;


public class CategoryAdapter extends ArrayAdapter<Category>
{

    private SettingRepository settingRepository;

    private Context context;
    int layoutResourceId;
    private ArrayList<Category> data;
    private String currency="";

    public CategoryAdapter(Context context, int layoutResourceId, ArrayList<Category> data)
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
        CategoryHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new CategoryHolder();

            holder.lblId=(TextView)v.findViewById(R.id.lblCategoryId);
            holder.lblName=(TextView)v.findViewById(R.id.lblCategoryName);
            holder.lblGroup=(TextView)v.findViewById(R.id.lblCategoryGroup);
            holder.lblBudget=(TextView)v.findViewById(R.id.lblCategoryBudget);
            holder.pnlCategory=(View)v.findViewById(R.id.pnlCategory);

            v.setTag(holder);
  }
        else
        {
            holder = (CategoryHolder)v.getTag();
        }


        Category category = data.get(position);

        if (category!=null)
        {
            holder.lblId.setText(String.valueOf(category.getId()));
            holder.lblName.setText(category.getName());
            holder.lblGroup.setText(category.getGroup());

            if (category.getIsBudgeted()==1)
            {
                //holder.pnlCategory.setBackgroundColor(Color.parseColor("#b03060"));

                if (category.getType().equals("Income")){
                    holder.pnlCategory.setBackgroundColor(Color.parseColor("#cd5c5c"));
                }else if (category.getType().equals("Expense")) {
                    holder.pnlCategory.setBackgroundColor(Color.parseColor("#f4a460"));
                }
                String strBudget=String.valueOf(category.getBudget());
                holder.lblBudget.setText(currency + strBudget.replaceAll("\\.0*$", ""));
            }
            else
            {

                holder.pnlCategory.setBackgroundColor(Color.parseColor("#eee9e9"));

                holder.lblBudget.setText("");
            }

        }


        return v;
    }

    static class CategoryHolder
    {
        TextView lblId;
        TextView lblName;
        TextView lblGroup;
        TextView lblBudget;
        View pnlCategory;
    }
}

