package com.tonini.diego.dexpense.wizard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;
import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.model.Wallet;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class WizardActivity extends ActionBarActivity implements PageFragmentCallbacks,ReviewFragment.Callbacks , ModelCallbacks {

    private static final int INDEX = 0;
    private SharedPreferences prefs;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdaper;
    private AbstractWizardModel mWizardModel;
    private boolean mEditingAfterReview;
    private boolean mConsumePageSelectedEvent;
    private LinearLayout layoutNext,layoutPrev;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWizardModel = new SandwichWizardModel(WizardActivity.this);

        setContentView(R.layout.activity_wizard);

        // we check if is first time running app. if not start MainActivity
        checkWizard();

        setUpDB();

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdaper = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdaper);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdaper.getCount() - 1,
                        position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        layoutNext = (LinearLayout) findViewById(R.id.layoutNext);
        layoutPrev = (LinearLayout) findViewById(R.id.layoutPrev);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        layoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    new MaterialDialog.Builder(WizardActivity.this)
                            .title(getString(R.string.Confirm))
                            .positiveText("Ok")
                            .negativeText(getString(R.string.Back))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);

                                    notAnymoreFirstRun();
                                    insertWallet();
                                    startActivity(new Intent(WizardActivity.this,MainActivity.class));
                                    finish();
                                }
                            }).show();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdaper.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        layoutPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean isFirstRun(){
        prefs = getSharedPreferences(Const.MY_PREFERENCES, Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(Const.KEY_PREF_IS_FIRST_RUN, true);

        //return true;
        return  isFirstRun;
    }
    private void notAnymoreFirstRun(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Const.KEY_PREF_IS_FIRST_RUN,false);
        editor.apply();
    }

    private void checkWizard(){
        if(!isFirstRun()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }


    // MyPagerAdapter
    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            //mNextButton.setText(R.string.finish);
            //mNextButton.setBackgroundResource(R.drawable.finish_background);
            //mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);

        } else {
            /*mNextButton.setText(mEditingAfterReview ? R.string.review
                    : R.string.next);
            mNextButton
                    .setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
                    true);
            mNextButton.setTextAppearance(this, v.resourceId);*/


            layoutNext.setEnabled(position != mPagerAdaper.getCutOffPage());
        }

        layoutPrev
                .setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();


        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
        // review
        // step
        mPagerAdaper.notifyDataSetChanged();
        updateBottomBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
        dbHelper.close();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public void onEditScreenAfterReview(String key) {

        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {

            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();


                break;
            }


        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdaper.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdaper.getCutOffPage() != cutOffPage) {
            mPagerAdaper.setCutOffPage(cutOffPage);

            return true;
        }

        return false;
    }

    private void insertWallet(){

        ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();
        for(Page p : mCurrentPageSequence){
            p.getReviewItems(list);
        }

        String name = list.get(SandwichWizardModel.NAME_WALLET_INDEX).getDisplayValue();
        if(name.length()==0){ name = "My Wallet"; }
        String valuta  = list.get(SandwichWizardModel.VALUTA_INDEX).getDisplayValue();
        if(valuta.equals(getString(R.string.WithoutCurrency))){ valuta=""; }
        double limit = list.get(SandwichWizardModel.LIMI_INDEX).getDisplayValue().equals("") ? 0 : Double.parseDouble(list.get(SandwichWizardModel.LIMI_INDEX).getDisplayValue());
        double balance   = Double.parseDouble(list.get(SandwichWizardModel.BALANCE_INDEX).getDisplayValue());

        Wallet w = new Wallet(name,limit, GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis(),balance);
        dbHelper.insertWallet(w);

        int id = Utils.getWalletsFromCursors(dbHelper.getWallets()).get(0).getId();
        Utils.setDefaultWalletIdPref(id,getApplicationContext()); // settiamo 1 perch+ + il primo
        Utils.setCurrency(valuta,this);
        Log.i(MainActivity.TAG,"WizardActivity setted "+valuta+" as default currency");
    }

    private void setUpDB(){
        // set-up DB
        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDB();
        } catch (IOException e){
            Log.i(MainActivity.TAG,e.toString());
        }
        try {
            dbHelper.openDataBase();
        } catch (SQLException e){
            Log.i(MainActivity.TAG,e.toString());
        }
    }

}
