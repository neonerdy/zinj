package com.xeris.zinj.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.xeris.zinj.repository.CategoryRepository.CategoryType;

 public class Statistics
{

   private ChartRepository chartRepository;
   private ArrayList<Float> data;

   public Statistics(Context ctx, ArrayList<Float> data)
   {
       chartRepository=new ChartRepository(ctx);
       this.data=data;
   }


    public int getDataCount()
    {
        return data.size();
    }


    public float getAverage()
    {
        float average=0;

        //ArrayList<Float> data=chartRepository.getAmountData(type,year);

        float total=0;
        for (int i=0;i<=data.size()-1;i++)
        {
            total=total+data.get(i);
        }

        average=total/data.size();

        return average;
   }


}
