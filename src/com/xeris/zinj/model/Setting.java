package com.xeris.zinj.model;

public class Setting 
{
	private int id;
	private String email;
	private int isProtected;
	private String password;
	private int month;
    private int year;
    private int closingDate;
    private String defaultChart;
	private String currency;
	private int isHideSymbol;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getIsProtected() {
		return isProtected;
	}
	public void setIsProtected(int isProtected) {
		this.isProtected = isProtected;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

    public int getYear() {
        return year;
    }

    public void setYear(int year){
        this.year=year;
    }

    public String getDefaultChart() {
        return defaultChart;
    }

    public void setDefaultChart(String defaultChart) {
        this.defaultChart = defaultChart;
    }


    public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public int getIsHideSymbol() {
		return isHideSymbol;
	}
	
	public void setIsHideSymbol(int isHideSymbol) {
		this.isHideSymbol = isHideSymbol;
	}


    public int getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(int closingDate) {
        this.closingDate = closingDate;
    }
}
