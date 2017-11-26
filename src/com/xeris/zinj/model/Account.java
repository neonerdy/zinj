package com.xeris.zinj.model;

public class Account 
{
	private int id;
	private String name;
	private String type;
	private float balance;
    private int order;
    private String notes;
    private float limit;
    private String financialGoalName;
    private float financialGoalTarget;
    private int isActive;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }


    public String getFinancialGoalName() {
        return financialGoalName;
    }

    public void setFinancialGoalName(String financialGoalName) {
        this.financialGoalName = financialGoalName;
    }

    public float getFinancialGoalTarget() {
        return financialGoalTarget;
    }

    public void setFinancialGoalTarget(float financialGoalTarget) {
        this.financialGoalTarget = financialGoalTarget;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

}
