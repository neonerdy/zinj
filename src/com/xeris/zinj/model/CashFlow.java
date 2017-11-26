package com.xeris.zinj.model;

public class CashFlow 
{
	private String type;
	private float amount;
    private String month;

	public CashFlow(String type,float amount)
	{
		this.type=type;
		this.amount=amount;
	}

    public CashFlow()
    {

    }

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }




}
