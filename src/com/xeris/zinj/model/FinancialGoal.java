package com.xeris.zinj.model;

public class FinancialGoal
{
    private int accountId;
    private String accountName;
    private float balance;
    private String goalName;
    private float goalTarget;
    private float remain;
    private int percentage;


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }


    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public float getGoalTarget() {
        return goalTarget;
    }

    public void setGoalTarget(float goalTarget) {
        this.goalTarget = goalTarget;
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


}
