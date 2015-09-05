package com.tonini.diego.dexpense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.model.Category;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.model.Wallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Diego on 06/04/2015.
 */
public class Utils {

    private  static List<String> months = Arrays.asList("gen", "feb", "mar", "apr", "mag","giu","lug","ago","set","ott","nov","dic");

    public static String getDateFromMillis(long millis){

        Calendar c = new GregorianCalendar(Locale.getDefault());
        c.setTimeInMillis(millis);
        return ""+c.get(Calendar.DAY_OF_MONTH)+"-"+months.get(c.get(Calendar.MONTH))+"-"+c.get(Calendar.YEAR);
    }

    /* public static String adjustValue(String value){

       return value;
    }*/

    public static List<Wallet> getWalletsFromCursors(Cursor c){

        List<Wallet> list = new ArrayList<Wallet>();
        if(c.getCount()>0) {
            c.moveToFirst();
            do {

                Wallet w = new Wallet(c.getString(Const.WALLET_NAME_INDEX), c.getDouble(Const.WALLET_LIMIT_MONTH_INDEX), c.getLong(Const.WALLET_DATE_INDEX), c.getDouble(Const.WALLET_BALANCE_INDEX));
                w.setId(c.getInt(Const.WALLET_ID_INDEX));
                list.add(w);

                Log.i(MainActivity.TAG, w.toString());

            } while (c.moveToNext());

            c.close();
        }

        return list;
    }

    public static ArrayList<IProfile> getProfilesByWallets(Context c){

        DBHelper dbHelper = new DBHelper(c);

        List<Wallet> wallets = Utils.getWalletsFromCursors(dbHelper.getWallets());


        ArrayList<IProfile> arrayList = new ArrayList<IProfile>();
        for (Wallet w : wallets) {
            //headerResult.setActiveProfile(new ProfileDrawerItem().withName(w.getName()).withIcon(getResources().getDrawable(R.drawable.wallet_128)));
            ProfileDrawerItem p = new ProfileDrawerItem().withIdentifier(w.getId()).withName(w.getName()).withIcon(c.getResources().getDrawable(R.drawable.wallet_128))
                                  .withTextColor(c.getResources().getColor(w.getBalance() >= 0 ? R.color.md_green_600 : R.color.md_red_600));
            arrayList.add(p);
        }
        dbHelper.close();
        return arrayList;
    }

    public static int getDefaultWalletIdPref(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(Const.MY_PREFERENCES,Context.MODE_PRIVATE);
        int r = sharedPreferences.getInt(Const.KEY_PREF_DEFAULT_WALLET_ID, -1);
        //Log.i(MainActivity.TAG,"getDefaultWalletIdPref() return: "+r);
        return r;
    }

    public static void setDefaultWalletIdPref(int id,Context c){
        Log.i(MainActivity.TAG,"setDefaultWalletId() set: "+String.valueOf(id));
        SharedPreferences.Editor editor = c.getSharedPreferences(Const.MY_PREFERENCES,Context.MODE_PRIVATE).edit();
        editor.putInt(Const.KEY_PREF_DEFAULT_WALLET_ID,id);
        editor.apply();
    }

    public static List<Movement> getMovementFromCursor(int idWallet,Cursor c){
        List<Movement> list = new ArrayList<Movement>();

        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                Movement m = new Movement(
                        c.getString(Const.MOVEMENT_NAME_INDEX),
                        c.getLong(Const.MOVEMENT_DATE_INDEX),
                        c.getDouble(Const.MOVEMENT_VALUE_INDEX),
                        c.getInt(Const.MOVEMENT_WALLET_ID_INDEX),
                        c.getLong(Const.MOVEMENT_TIME_REPEATING_INDEX));
                m.setId(c.getInt(Const.MOVEMENT_ID_INDEX));

                list.add(m);
            } while (c.moveToNext());

            c.close();
        }
        return list;
    }

    public static void setCurrency(String currency,Context c){
        SharedPreferences.Editor editor = c.getSharedPreferences(Const.MY_PREFERENCES,Context.MODE_PRIVATE).edit();
        editor.putString(Const.KEY_PREF_CURRENCY,currency);
        editor.apply();
    }
    public static String getCurrency(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(Const.MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Const.KEY_PREF_CURRENCY,"");
    }

    public static List<Category> getCategoryFromCursor(Cursor c){
        List<Category> categories = new ArrayList<Category>();

        if(c.getCount()>0){
            c.moveToFirst();
            do {
                Category cat = new Category(c.getString(Const.KEY_CATEGORY_NAME_INDEX));
                cat.setId(c.getInt(Const.KEY_CATEGORY_ID_INDEX));
                categories.add(cat);
            } while (c.moveToNext());
        }
        return categories;
    }

    public static Map<Category,Float> getTotalForCategoryFromCursor(Cursor c){
        Map<Category,Float> map = new HashMap<Category,Float>();

        if(c.getCount()>0){
            c.moveToFirst();
            do {
                float totalCategory = Math.abs(c.getFloat(0));
                String nameCategory = c.getString(1);
                map.put(new Category(nameCategory),totalCategory);
                //Log.i(MainActivity.TAG,"getTotalForCategoryFromCursor"+totalCategory+" "+nameCategory);
            }while (c.moveToNext());
        }

        return map;
    }
}
