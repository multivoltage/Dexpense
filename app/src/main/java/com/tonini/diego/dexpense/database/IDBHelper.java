package com.tonini.diego.dexpense.database;

import android.database.Cursor;

import com.tonini.diego.dexpense.model.Wallet;

/**
 * Created by Diego on 29/03/2015.
 */
public interface IDBHelper {

    public Cursor getWallets();

    public Cursor getMovementsOfWallet(int id,String orderBy,boolean desc);

    public void insertWallet(Wallet wallet);


}
