package com.tonini.diego.dexpense.model;

import java.io.Serializable;

public class Wallet implements IWallet, Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    //  _id not exist at creation. I' will assigned from db
    private int _id;
    private String name;// deve essere univoco rispetto agli altri Wallet
    private double limit_month = -1;
    private long date;
    private double balance = 0;


    public Wallet(String name, double limitMonth, long date,double balance){

        setName(name);
        setMonthLimit(limitMonth);
        setDate(date);
        setBalance(balance);
    }

    @Override
    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public void setId(int id) {
        this._id = id;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setMonthLimit(double limit) {
        this.limit_month = limit;
    }

    @Override
    public double getMonthLimit() {
        return limit_month;
    }

    @Override
    public boolean hasLimi() {
        return limit_month != -1;
    }

    @Override
    public long getDateInMillis() {
        return this.date;
    }

    @Override
    public String toString(){
        return ""+name+","+limit_month+","+date+","+balance;
    }
}
