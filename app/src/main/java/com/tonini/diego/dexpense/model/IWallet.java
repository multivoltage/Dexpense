package com.tonini.diego.dexpense.model;

/**
 * Created by Diego on 27/03/2015.
 */
public interface IWallet {

    public void setId(int id);

    public void setName(String name);

    public void setMonthLimit(double limit);

    public void setDate(long date);

    public void setBalance(double balance);

    public double getMonthLimit();

    public String getName();

    public double getBalance();

    public long getDateInMillis();

    public int getId();

    public boolean hasLimi();

}
