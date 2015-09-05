package com.tonini.diego.dexpense.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.MyApplication;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.list.WalletCursorCardAdapter;
import com.tonini.diego.dexpense.model.Wallet;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardListView;

public class MainFragment extends Fragment{

    public static final String KEY_TITLE = "Wallets";

    private CardListView listViewWallet;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private WalletCursorCardAdapter cursorCardAdapter;
    private DBHelper dbHelper;
    private ViewGroup croutonParent = null;
    private  FloatingActionButton fabButton;

    // this permited to comunicate t Activity parent a new inser of wallet
    OnWalletAddedListener mCallback;

    public interface OnWalletAddedListener {
        public void onWalletAdded(Wallet w);
    }

    public MainFragment() {
    }

    public static MainFragment newInstance(String title){
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnWalletAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnWalletAddedListener");
        }
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setUpDB();
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.Wallets));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance){

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        croutonParent = container;

        fabButton = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);
        fabButton.show(true);

        cursorCardAdapter = new WalletCursorCardAdapter(getActivity(),dbHelper.getWallets(),true);

        listViewWallet = (CardListView) v.findViewById(R.id.listViewWallet);
        if (listViewWallet!=null){
            listViewWallet.setAdapter(cursorCardAdapter);
        }

        View footer = inflater.inflate(R.layout.footer_layout_listview, container, false);
        listViewWallet.addFooterView(footer);
        listViewWallet.setEmptyView(v.findViewById(R.id.emtyView));
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.table_row_appear), 0.5f); //0.5f == time between appearance of listview items.
        listViewWallet.setLayoutAnimation(lac);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addWalletDialog();
            }
        });
        fabButton.attachToListView(listViewWallet);


        listViewWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listViewWallet.getItemAtPosition(position);
                Log.i(MainActivity.TAG,"click is"+o.toString());
            }
        });
        return v;
    }

    private void setUpDB(){
        // set-up DB
        dbHelper = new DBHelper(getActivity());

    }

    private void addWalletDialog(){

        final SeekBar seekBarWalletLimitMonth;
        final EditText editAlertAddWalletName;
        final EditText editAlertWalletBalance;

        MaterialDialog.Builder m = new MaterialDialog.Builder(getActivity());

        LayoutInflater inflater = m.build().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_wallet, null);
        m.customView(dialogView,true);
        m.title(getString(R.string.New_Wallet)).titleColorRes(R.color.primary);
        m.negativeText("Back");
        m.positiveText("Confirm");

        editAlertAddWalletName = (EditText) dialogView.findViewById(R.id.editAlertAddWalletName);
        editAlertWalletBalance = (EditText) dialogView.findViewById(R.id.editAlertWalletBalance);
        seekBarWalletLimitMonth = (SeekBar)  dialogView.findViewById(R.id.seekBarWalletLimitMonth);
        final TextView tvWalletLimitMonth     = (TextView) dialogView.findViewById(R.id.tvWalletLimitMonth);

        seekBarWalletLimitMonth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                tvWalletLimitMonth.setText(Utils.getCurrency(getActivity())+" "+String.valueOf(progress));
            }
        });


        m.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);

                String name = editAlertAddWalletName.getText().toString();
                if(name.length()==0)    name = getString(R.string.Generic_Wallet);
                double limit_month = seekBarWalletLimitMonth.getProgress();
                long dateInMillis  = GregorianCalendar.getInstance().getTimeInMillis();
                String b =  editAlertWalletBalance.getText().toString();
                if(b.length()==0)   b = "0";

                Wallet w = new Wallet(name,limit_month,dateInMillis,Double.parseDouble(b));
                dbHelper.insertWallet(w);

                MyApplication.bus.post("refresh_wallets");
                //mCallback.onWalletAdded(w);// notify to activiyt
                Style primaryBlue = new Style.Builder().setBackgroundColorValue(getResources().getColor(R.color.primary)).build();
                Crouton.makeText(getActivity(), getString(R.string.Added)+editAlertAddWalletName.getText().toString(), primaryBlue, croutonParent).show();
                cursorCardAdapter.swapCursor(dbHelper.getWallets());

            }
        });
        m.show();


    }

    private boolean isPro(){
        return true;
    }


}
