package com.tonini.diego.dexpense.utils;

/**
 * Created by Diego on 27/03/2015.
 */
public class Const {

    // FOR WALLET
    public static final String TABLE_WALLET_NAME = "WALLET";
    public static final String TABLE_MOVEMENT_NAME = "MOVEMENT";
    public static final String TABLE_CATEGORYENTRY = "CATEGORYENTRY";
    public static final String TABLE_CATEGORYEXIT = "CATEGORYEXIT";
    public static final String TABLE_RECURRENCY   = "RECURRENCY";

    // FOR WALLET
    public static final String KEY_WALLET_ID = "_id";
    public static final String KEY_WALLET_NAME = "name";
    public static final String KEY_WALLET_LIMIT_MONTH = "limit_month";
    public static final String KEY_WALLET_DATE = "date";
    public static final String KEY_WALLET_BALANCE = "balance";

    // FOR WALLET
    public static final int WALLET_ID_INDEX = 0;
    public static final int WALLET_NAME_INDEX = 1;
    public static final int WALLET_LIMIT_MONTH_INDEX = 2;
    public static final int WALLET_DATE_INDEX = 3;
    public static final int WALLET_BALANCE_INDEX = 4;


    // FOR MOVEMENT
    public static final String KEY_MOVEMENT_ID = "_id";
    public static final String KEY_MOVEMENT_NAME = "name";
    public static final String KEY_MOVEMENT_VALUE = "value";
    public static final String KEY_MOVEMENT_DATE = "date";
    public static final String KEY_MOVEMENT_WALLET_ID = "wallet_id";
    public static final String KEY_MOVEMENT_TIME_REPEATING = "time_repeating";

  // for query index
    // FOR MOVEMENT
    public static final int MOVEMENT_ID_INDEX = 0;
    public static final int MOVEMENT_NAME_INDEX = 1;
    public static final int MOVEMENT_VALUE_INDEX = 2;
    public static final int MOVEMENT_DATE_INDEX = 3;
    public static final int MOVEMENT_WALLET_ID_INDEX = 4;
    public static final int MOVEMENT_TIME_REPEATING_INDEX = 5;

    // FOR CATEGORY
    public static final String KEY_CATEGORY_ID = "_id";
    public static final String KEY_CATEGORY_NAME = "name";

    // FOR CATEGORYENTRY
    public static final int KEY_CATEGORY_ID_INDEX = 0;
    public static final int KEY_CATEGORY_NAME_INDEX = 1;

    // FOR RECURRENCY
    public static final String KEY_RECURRENCY_ID = "_id";
    public static final int RECURRENCY_ID_INDEX = 0;


    // shared preferences
    public static String MY_PREFERENCES = "com.tonini.diego.dexpense.pro";
    public static String KEY_PREF_VERSION_APP = "com.tonini.diego.dexpense.version.app";
    public static String KEY_PREF_DEFAULT_WALLET_ID = "com.tonini.diego.dexpence.defaulwalletid";
    public static String KEY_PREF_IS_FIRST_RUN = "com.tonini.diego.dexpence.isfirstrun";
    public static String KEY_PREF_CURRENCY = "com.tonini.diego.dexpence.currency";

    // for repeating
    public static long DELTA_MONTH_REPEATING = 2592000000L;
    public static long DELTA_WEEK_REPEATING = 604800000L;
    public static long DELTA_TEN_SEC_REPEATING = 1000 * 10; // 10 secondi

}
