package com.xeris.zinj.model;

public class Budget 
{
	private int categoryId;
    private String categoryName;
	private float budgeted;
	private float used;
	private float remain;
	private int percentage;
    private String month;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

	public float getBudgeted() {
		return budgeted;
	}
	public void setBudgeted(float budgeted) {
		this.budgeted = budgeted;
	}
		
	public float getUsed() {
		return used;
	}
	public void setUsed(float used) {
		this.used = used;
	}
	
	public float getRemain() {
		return remain;
	}
	public void setRemain(float remain) {
		this.remain = remain;
	}
	
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }



}
