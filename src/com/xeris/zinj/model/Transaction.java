package com.xeris.zinj.model;

import java.util.Date;


public class Transaction 
{	

	private int id;
	private String type;
    private String date;
	private String description;
	private String additionalInfo;
	private float amount;
	private int categoryId;
	private Category category;
	private int accountId;
	private Account account;
	private String notes;
	private int paymentAccountId;
    private String paymentInfo;

    public enum TransactionType
	{		
		Income,
		Expense,
		Transfer,
		Withdrawal,
		Deposit,
	    Credit,
	    Payment
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo=additionalInfo;
	}
	 
	
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	
	public Category getCategory() 
	{
		if (category == null ) category=new Category();
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	
	public Account getAccount() {
		if (account==null) account=new Account();
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
		
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	public int getPaymentAccountId() {
		return paymentAccountId;
	}
	public void setPaymentAccountId(int paymentAccountId) {
		this.paymentAccountId = paymentAccountId;
	}

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }




}
