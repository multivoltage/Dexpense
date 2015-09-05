package com.tonini.diego.dexpense.model;

/**
 * Created by Diego on 27/03/2015.
 */
public interface IMovement {

    public void setName(String name);

    public void setDateInMillis(long dateInMillis);

    public void setValue(double amount);

    public void setParentWalletid(int id_wallet);

    public void setRepeatingTime(long repeatingTime);

    public void setId(int id);

    public long getDateInMillis();

    public int getId();

    public int getParentWalletId();

    public double getValue();

    public String getName();

    public long getRepeatingTime();


}
