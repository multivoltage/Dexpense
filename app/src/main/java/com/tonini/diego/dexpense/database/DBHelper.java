package com.tonini.diego.dexpense.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.model.Wallet;
import com.tonini.diego.dexpense.utils.Const;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Diego on 27/03/2015.
 */
public class DBHelper extends SQLiteOpenHelper implements IDBHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/"+"com.tonini.diego.dexpense"+"/databases/";

    private static String DB_NAME = "dexpense";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private int walletCount = 0;

    public DBHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDB() throws IOException {
        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
            //Log.i(MainActivity.TAG,"db already exist");
        }else{
            //Log.i(MainActivity.TAG,"db created");
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException{

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public Cursor getWallets() {
        Cursor c = null;

        try {
             myDataBase = getReadableDatabase();
            c = myDataBase.query(Const.TABLE_WALLET_NAME, null, null, null, null, null, null);
            walletCount = c.getCount();

        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,"getWallets(), "+e.toString());
        }

        return c;
    }

    public Cursor getMovementWithFiler(int id,String orderBy,boolean desc,String category,long from, long to){

        String isDesc = desc ? " DESC" : " ASC";
        Cursor c = null;

        try {
            myDataBase = getReadableDatabase();
            if(category==null)
                c = myDataBase.query(Const.TABLE_MOVEMENT_NAME, null,Const.KEY_MOVEMENT_WALLET_ID + "=?", new String[]{String.valueOf(id)}, null, null, orderBy + isDesc, null);
            else
                c = myDataBase.query(Const.TABLE_MOVEMENT_NAME,null, Const.KEY_MOVEMENT_WALLET_ID + "=? AND "+Const.KEY_MOVEMENT_NAME + "=?",new String[]{String.valueOf(id),category},null,null,orderBy+isDesc,null);
        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,e.toString());
        }
        return c;
    }

    @Override
    public Cursor getMovementsOfWallet(int id,String orderBy,boolean desc) {
        return getMovementWithFiler(id,orderBy,desc,null,0, GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis());
    }

    @Override
    public void insertWallet(Wallet wallet) {

       ContentValues values = new ContentValues();
            values.put(Const.KEY_WALLET_NAME,wallet.getName());
            values.put(Const.KEY_WALLET_LIMIT_MONTH,wallet.getMonthLimit());
            values.put(Const.KEY_WALLET_DATE,wallet.getDateInMillis());
            values.put(Const.KEY_WALLET_BALANCE, wallet.getBalance());

        try {

            myDataBase = getWritableDatabase();
            myDataBase.insert(Const.TABLE_WALLET_NAME,null,values);

        } catch (Exception e){
            Log.i(MainActivity.TAG,"insertWallet(), "+e.toString());
        }
    }

    public double getBalanceByIdWallet(int idWallet){

        double balance = -1;

        Cursor c = null;
        try {
            myDataBase = getReadableDatabase();
            c = myDataBase.query(Const.TABLE_WALLET_NAME,new String[]{Const.KEY_WALLET_BALANCE},Const.KEY_WALLET_ID+"=?",new String[]{String.valueOf(idWallet)},null,null,null);
            c.moveToFirst();

            balance = c.getDouble(0);
        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,"getBalanceByWalletId(), "+e.toString());
        }

        return balance;
    }

    public String getWalletNameById(int idWallet){


        String name = myContext.getString(R.string.no_wallets);

        Cursor c = null;
        try {
            myDataBase = getReadableDatabase();
            c = myDataBase.query(Const.TABLE_WALLET_NAME, new String[]{Const.KEY_WALLET_NAME}, Const.KEY_WALLET_ID + "=?", new String[]{String.valueOf(idWallet)}, null, null, null);

            if(c.getCount()>0) {
                c.moveToFirst();
                name = c.getString(0);
            }

        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,"getBalanceByWalletId(), "+e.toString());
        }

        return name;
    }

    public long insertMovement(Movement m){
        ContentValues values = new ContentValues();
        values.put(Const.KEY_MOVEMENT_NAME,m.getName());
        values.put(Const.KEY_MOVEMENT_VALUE,m.getValue());
        values.put(Const.KEY_MOVEMENT_DATE,m.getDateInMillis());
        values.put(Const.KEY_MOVEMENT_WALLET_ID,m.getParentWalletId());
        values.put(Const.KEY_MOVEMENT_TIME_REPEATING,m.getRepeatingTime());
        long id = -1;
        try {
            myDataBase = getWritableDatabase();
            id = myDataBase.insert(Const.TABLE_MOVEMENT_NAME,null,values);

            // update balance
            double newBalance = getBalanceByIdWallet(m.getParentWalletId()) + m.getValue();
            values.clear();
            values.put(Const.KEY_WALLET_BALANCE,newBalance);
            myDataBase.update(Const.TABLE_WALLET_NAME,values,Const.KEY_WALLET_ID+"=?",new String[]{String.valueOf(m.getParentWalletId())});

            if(m.getRepeatingTime()>0){
                values.clear();
                values.put(Const.KEY_RECURRENCY_ID, id);
                myDataBase.insert(Const.TABLE_RECURRENCY,null,values);
            }

        } catch (Exception e){
            Log.i(MainActivity.TAG,"insertMovement(), "+e.toString());
        }
        return id;
    }

    public void deleteMovement(Movement m){
        try {
            myDataBase = getWritableDatabase();
            myDataBase.delete(Const.TABLE_MOVEMENT_NAME,Const.KEY_MOVEMENT_ID+"=?",new String[]{String.valueOf(m.getId())});

            // update balance
            double newBalance = getBalanceByIdWallet(m.getParentWalletId()) - m.getValue();
            ContentValues values = new ContentValues();
            values.put(Const.KEY_WALLET_BALANCE,newBalance);
            myDataBase.update(Const.TABLE_WALLET_NAME,values,Const.KEY_WALLET_ID+"=?",new String[]{String.valueOf(m.getParentWalletId())});

        } catch (Exception e){
            Log.i(MainActivity.TAG,"deleteMovement(), "+e.toString());
        }
    }

    public void deleteWallet(int idWallet){
        try {
            myDataBase = getWritableDatabase();
            myDataBase.delete(Const.TABLE_MOVEMENT_NAME, Const.KEY_MOVEMENT_WALLET_ID + "=?", new String[]{String.valueOf(idWallet)});
            myDataBase.delete(Const.TABLE_WALLET_NAME, Const.KEY_WALLET_ID + "=?", new String[]{String.valueOf(idWallet)});

        } catch (Exception e){
            Log.i(MainActivity.TAG,"deleteWallet(), "+e.toString());
        }
    }

    public void updateMovement(Movement m,double deltaValue){

        ContentValues values = new ContentValues();
        values.put(Const.KEY_MOVEMENT_NAME,m.getName());
        values.put(Const.KEY_MOVEMENT_VALUE,m.getValue());
        values.put(Const.KEY_MOVEMENT_DATE,m.getDateInMillis());
        values.put(Const.KEY_MOVEMENT_WALLET_ID,m.getParentWalletId());
        values.put(Const.KEY_MOVEMENT_TIME_REPEATING,m.getRepeatingTime());

        Log.i(MainActivity.TAG,"DBHelper updateMovement: "+m.toString());
        try {
            myDataBase = getWritableDatabase();
            myDataBase.update(Const.TABLE_MOVEMENT_NAME,values,Const.KEY_MOVEMENT_ID+"=?",new String[]{String.valueOf(m.getId())});

            double newBalance = getBalanceByIdWallet(m.getParentWalletId()) + deltaValue;

            values.clear();
            values.put(Const.KEY_WALLET_BALANCE, newBalance);
            myDataBase.update(Const.TABLE_WALLET_NAME, values, Const.KEY_WALLET_ID + "=?", new String[]{String.valueOf(m.getParentWalletId())});

        } catch (Exception e){
            Log.i(MainActivity.TAG,"updateMovement() with edit, "+e.toString());
        }
    }

    public Cursor getCategory(String tbl_category){
        Cursor c = null;

        try {
            myDataBase = getReadableDatabase();
            c = myDataBase.query(tbl_category, null, null, null, null, null, null);

        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,"getCategory(), "+e.toString());
        }

        return c;
    }

    public void insertCategory(boolean isEntry,String category){
        ContentValues values = new ContentValues();
        values.put(Const.KEY_CATEGORY_NAME, category);

        try {
            myDataBase = getWritableDatabase();
            myDataBase.insert(isEntry ? Const.TABLE_CATEGORYENTRY : Const.TABLE_CATEGORYEXIT,null,values);

        } catch (Exception e){
            Log.i(MainActivity.TAG,"insertCategory(), "+e.toString());
        }
    }


    public Cursor getTotalForCategory(int idWallet, boolean isEntry, long from, long to){
        Cursor c = null;
        try {
            String id = String.valueOf(idWallet);
            String f  = String.valueOf(from);
            String t  = String.valueOf(to);
            String entryClause = isEntry ? ">=0" : "<0";

            myDataBase = getReadableDatabase();
            String query = "select sum(value),name from MOVEMENT " +
                        "WHERE wallet_id=".concat(id)+" AND date BETWEEN ".concat(f)+" AND ".concat(t)+" AND value ".concat(entryClause)+" group by name"; //where wallet_id="2"

            c = myDataBase.rawQuery(query,null);
        } catch (SQLiteException e){
            Log.i(MainActivity.TAG,"getRecurrencyMovements(), "+e.toString());
        }

        return c;
    }

    public double getPercOfTotal(int idWallet,long from, long to){

        double sum = 0;
        Cursor cur;
        cur = getTotalForCategory(idWallet,false,from,to);
        if(cur.getCount()>0){
            cur.moveToFirst();
            do {
                sum += cur.getDouble(0);
            } while (cur.moveToNext());
        }
        return sum;

    }

}
