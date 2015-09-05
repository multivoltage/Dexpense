package com.tonini.diego.dexpense;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.opencsv.CSVWriter;
import com.squareup.otto.Subscribe;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.fragments.AboutFragment;
import com.tonini.diego.dexpense.fragments.AdFragment;
import com.tonini.diego.dexpense.fragments.MainFragment;
import com.tonini.diego.dexpense.fragments.MovementFragment;
import com.tonini.diego.dexpense.fragments.RepeatingFragment;
import com.tonini.diego.dexpense.fragments.SettingFragment;
import com.tonini.diego.dexpense.fragments.StatsFragment;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.model.Wallet;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class MainActivity extends ActionBarActivity implements MainFragment.OnWalletAddedListener, BillingProcessor.IBillingHandler{

    public static final String TAG = "com.dexpence";
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private AccountHeader.Result headerResult = null;
    private Drawer.Result result = null;
    private DBHelper dbHelper;
    private ArrayList<IProfile> profiles;

    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bp = new BillingProcessor(this, getString(R.string.license_key), this);

        MyApplication.bus.register(this);

        dbHelper = new DBHelper(this);

        setUpAds();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.finance7)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public void onProfileChanged(View view, IProfile iProfile) {
                        Log.i(MainActivity.TAG, "clicked id: with OnAccountHeaderListener " + iProfile.getIdentifier());
                        setDefaultWalletIdPref(iProfile.getIdentifier());
                        headerResult.setActiveProfile(iProfile);
                        Fragment f = MovementFragment.newInstance("movement_fragment");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                    }
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public void onClick(View view, IProfile iProfile) {
                        setDefaultWalletIdPref(iProfile.getIdentifier());
                        headerResult.setActiveProfile(iProfile);
                    }
                })
                .build();


        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.Wallets)).withTextColorRes(R.color.primary).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(getString(R.string.Movements)).withIcon(FontAwesome.Icon.faw_bar_chart),   // entrate
                        //
                        new SectionDrawerItem().withName(getString(R.string.Operations)),
                        new SecondaryDrawerItem().withName(getString(R.string.Automatic_Movement)).withIcon(FontAwesome.Icon.faw_calendar),
                        //new SecondaryDrawerItem().withName("nuova scadenza").withIcon(FontAwesome.Icon.faw_bomb),
                        new SecondaryDrawerItem().withName(getString(R.string.Stats)).withIcon(FontAwesome.Icon.faw_tasks),
                        //
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(getString(R.string.Setting)).withIcon(FontAwesome.Icon.faw_wrench),
                        new SecondaryDrawerItem().withName("info").withIcon(FontAwesome.Icon.faw_info)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem != null) {// && iDrawerItem instanceof Nameable){
                            Log.i(MainActivity.TAG,"pos item: "+pos);

                            Fragment f;
                            switch (pos) {
                                case 0:    // Balance (home)
                                    getSupportActionBar().setTitle(MainFragment.KEY_TITLE);
                                    //Load MainFragment
                                    f = MainFragment.newInstance("main_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                                    break;

                                case 1: // Entrate
                                    getSupportActionBar().setTitle(MovementFragment.KEY_TITLE);
                                    f = MovementFragment.newInstance("movement_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f,"my_tag").commit();

                                    break;

                                case 3:
                                    f = RepeatingFragment.newInstance("repeating_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                                    break;

                                case 4 :
                                    f = StatsFragment.newInstance("stats_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                                    break;

                                case 6:
                                    getSupportActionBar().setTitle(SettingFragment.KEY_TITLE);
                                    f = SettingFragment.newInstance("setting_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                                    break;

                                case 7:
                                    getSupportActionBar().setTitle(AboutFragment.KEY_TITLE);
                                    f = AboutFragment.newInstance("about_fragment");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                                    break;
                            }
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        loadProfiles();
        setUpProfileHeaderResult();

        //react on the keyboard
        new KeyboardUtil(this, findViewById(R.id.fragment_container));

        getSupportActionBar().setTitle(MainFragment.KEY_TITLE);
        //Load MainFragment
        Fragment f = MainFragment.newInstance("main_fragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();



      /*  Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        String[] mov = new String[]{"stipendio","acquisto","bar","scuola","spesa"};
        for(int i=0;i<50;i++){

            Movement m = new Movement(mov[new Random().nextInt(3)],c.getTimeInMillis(),new Random().nextInt(150),2,0);
            dbHelper.insertMovement(m);
            c.add(Calendar.DAY_OF_MONTH,new Random().nextInt(5));
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_rate :
                final String appPackageName = getPackageName(); // Can also use getPackageName(), as below
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                break;

            case R.id.action_buy_pro : buyProDialog();
                break;

            case R.id.action_csv :     csvDialog();
                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    private boolean isFreeVersion() {
        SharedPreferences preferences = getSharedPreferences(Const.MY_PREFERENCES, Context.MODE_PRIVATE);
        // returned false the first run of app -> true becase is free now;
        boolean isFree = preferences.getBoolean(Const.KEY_PREF_VERSION_APP, true);
        return isFree;
    }
    private void setUpVersion(boolean isFree){
        SharedPreferences.Editor editor = getSharedPreferences(Const.MY_PREFERENCES,Context.MODE_PRIVATE).edit();
        editor.putBoolean(Const.KEY_PREF_VERSION_APP,isFree);
        editor.apply();
    }

    //---------------------------------------------- THIS IS FOR SET UP AND REFRESH DRAWER--------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param w w passed from MainFragment. Considering re-use this method also for other message between Fragment and activity
     */
    @Override
    public void onWalletAdded(Wallet w) {
        loadProfiles();
        setUpProfileHeaderResult();
    }

    @Subscribe
    public void getMessage(String s) {
        loadProfiles();
        setUpProfileHeaderResult();
        Log.i(MainActivity.TAG, "invoked getMessage by Bus");
    }

    private void setUpProfileHeaderResult() {
        headerResult.setProfiles(profiles);
    }

    private void loadProfiles(){
        profiles = Utils.getProfilesByWallets(this);
        if(profiles.size()==1){
            Utils.setDefaultWalletIdPref(profiles.get(0).getIdentifier(),this);
        }
    }

    private void setDefaultWalletIdPref(int id){
        Utils.setDefaultWalletIdPref(id,this);
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(f instanceof MainFragment){
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.Really_exit))
                    .negativeText("No")
                    .positiveText(getString(R.string.Yes))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            MainActivity.super.onBackPressed();
                        }
                    }).show();
        } else {
            f = MainFragment.newInstance("main_fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
        //Log.i(MainActivity.TAG,"MainActivity called onActivityResult(), with requestCode "+requestCode+", resultCode: "+resultCode);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(f instanceof MovementFragment){
            f.onActivityResult(requestCode, resultCode, data);
            //Log.i(MainActivity.TAG,"MainActivity called onActivityResult and pass the same method to MovementFragment");
        }
    }

    /* THIS IS FOR IN APP PURCHASE */
    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        Log.i(MainActivity.TAG,"InAppPurchase: called onProductPurchased()");
        setUpVersion(false);
        setUpAds();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        Log.i(MainActivity.TAG,"InAppPurchase: called onBillingError() with error: "+i);
    }

    @Override
    public void onBillingInitialized() {
        Log.i(MainActivity.TAG, "InAppPurchase: called onBillingInitialized()");
    }

    private void buyProDialog(){

        new MaterialDialog.Builder(this)
                .title(getString(R.string.Upgrade_to_PRO))
                .content(getString(R.string.remove_all_ads))
                .positiveText(getString(R.string.Buy))
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        bp.purchase(MainActivity.this, getString(R.string.product_id));
                    }
                }).show();
    }

    private void setUpAds() {
        if(isFreeVersion()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.adFragment, new AdFragment())
                    .commit();
        }
        else {
            Fragment fr = getSupportFragmentManager().findFragmentById(R.id.adFragment);
            if(fr!=null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fr)
                        .commit();
            }
        }
    }

    private void csvDialog(){

        new MaterialDialog.Builder(this)
                .title("Generate Cvs")
                .content("Cvs are located in /Dexpense/today.csv")
                .positiveText(getString(R.string.Confirm))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        generateCsv();
                    }
                }).show();
    }

    private void generateCsv(){
        String fileName = Utils.getDateFromMillis(Calendar.getInstance().getTimeInMillis())+".csv";
        File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"Dexpense");
        if(!dir.exists())
            dir.mkdir();

        File f = new File(Environment.getExternalStorageDirectory()+File.separator+"Dexpense",fileName);

        boolean saved = false;
        try {
            boolean b = f.createNewFile();
            if(b){
                CSVWriter writer = new CSVWriter(new FileWriter(f));
                int defaulId = Utils.getDefaultWalletIdPref(this);

                String arrStr1[] ={"id", "Name", "Date","Value"};
                writer.writeNext(arrStr1);

                for(Movement m : Utils.getMovementFromCursor(defaulId,dbHelper.getMovementsOfWallet(defaulId,Const.KEY_MOVEMENT_DATE,false))){
                    String[] fields = new String[]{
                            String.valueOf(m.getId()),
                            m.getName(),
                            Utils.getDateFromMillis(m.getDateInMillis()),
                            String.valueOf(m.getValue()),
                    };
                    writer.writeNext(fields);
                }

                writer.close();
            }
            saved = true;
        } catch (IOException e) {
            Log.i(MainActivity.TAG,e.toString());
        }

        Toast.makeText(this,saved ? getString(R.string.generated_csv_ok) : getString(R.string.generated_csv_ko),Toast.LENGTH_SHORT).show();
    }

}
