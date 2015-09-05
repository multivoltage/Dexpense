package com.tonini.diego.dexpense.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Diego on 27/03/2015.
 */
public class Movement implements IMovement , Parcelable{

    private int id;
    private String name;
    private long dateInMillis;
    private double value;
    private int parentWalletId;
    private long repeatingTime = 0;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Movement(String name,long date,double value,int parentWalletId,long repeatingTime){

        setName(name);
        setDateInMillis(date);
        setValue(value);
        setParentWalletid(parentWalletId);
        setRepeatingTime(repeatingTime);

        //id = java.lang.System.identityHashCode(this);
    }

    public Movement(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.dateInMillis = in.readLong();
        this.value = in.readDouble();
        this.parentWalletId = in.readInt();
        this.repeatingTime = in.readLong();
    }

    @Override
    public void setParentWalletid(int id_wallet) {
        this.parentWalletId = id_wallet;
    }

    @Override
    public int getParentWalletId() {
        return parentWalletId;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setRepeatingTime(long repeatingTime) {
        this.repeatingTime = repeatingTime;
    }

    @Override
    public long getRepeatingTime() {
        return repeatingTime;
    }

    @Override
    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public long getDateInMillis() {
        return dateInMillis;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeLong(dateInMillis);
            dest.writeDouble(value);
            dest.writeInt(parentWalletId);
            dest.writeLong(repeatingTime);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Movement>() {
        public Movement createFromParcel(Parcel in) {
            return new Movement(in);
        }

        @Override
        public Movement[] newArray(int size) {
            return new Movement[size];
        }
    };

    @Override
    public String toString(){
        return "Movement "+"id: "+id+", name: "+name+", date: "+dateInMillis+", value: "+value+" parentWalledId: "+parentWalletId+"with repeating: "+repeatingTime;
    }
}
