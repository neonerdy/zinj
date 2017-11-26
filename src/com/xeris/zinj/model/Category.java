package com.xeris.zinj.model;

public class Category 
{	
	private int id;
	private String name;
	private String type;
	private String group;
	private int isBudgeted;
	private float budget;
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
	
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
		
	
	public int getIsBudgeted() {
		return isBudgeted;
	}
	public void setIsBudgeted(int isBudgeted) {
		this.isBudgeted = isBudgeted;
	}
	
	
	public float getBudget() {
		return budget;
	}
	public void setBudget(float budget) {
		this.budget = budget;
	}


    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
