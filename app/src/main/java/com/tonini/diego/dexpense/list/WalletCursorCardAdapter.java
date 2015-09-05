package com.tonini.diego.dexpense.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.MyApplication;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.fragments.MovementFragment;
import com.tonini.diego.dexpense.model.Wallet;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;

/**
 * Created by Diego on 29/03/2015.
 */
public class WalletCursorCardAdapter extends CardCursorAdapter {

    private int i = 0;
    private String currency;
    private DBHelper db;
    private int idWallet;
    private long from,to;

    public WalletCursorCardAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        db = new DBHelper(getContext());
        currency = Utils.getCurrency(context);
        Calendar cal = GregorianCalendar.getInstance(Locale.getDefault());
        cal.add(Calendar.YEAR,1);
        to = cal.getTimeInMillis();
        cal.add(Calendar.YEAR,-1);
        cal.add(Calendar.MONTH,-1);
        from = cal.getTimeInMillis();
    }

    @Override
    protected Card getCardFromCursor(Cursor cursor) {

        final Wallet w = getWalletFromCursor(cursor);
        final int n = (int) Math.abs(Math.round(100 * db.getPercOfTotal(w.getId(), from, to) / w.getMonthLimit()));
        final  String s = String.valueOf(n)+"%";
        final double balance = Math.round(w.getBalance() * 100.0) / 100.0;

        TopColoredCard tcard = TopColoredCard.with(super.getContext())
                .setColorResId(w.getBalance() >= 0 ? R.color.md_green_600 : R.color.md_red_600)
                .setTitleOverColor(w.getName())
                .setSubTitleOverColor(getContext().getString(R.string.Monthly_limit)+": "+String.valueOf((int)w.getMonthLimit())+currency+" - "+s)
                .setupSubLayoutId(R.layout.carddemo_native_halfcolored_simple_title)

                .setupInnerElements(new TopColoredCard.OnSetupInnerElements() { // removing this, app not show the textview
                    @Override
                    public void setupInnerViewElementsSecondHalf(View secondHalfView) {

                        TextView tvCurrentBalance = (TextView) secondHalfView.findViewById(R.id.tvCurrentBalance);
                        tvCurrentBalance.setText(String.valueOf(balance) + " " + currency);

                        TextView tvDateWalletRow = (TextView) secondHalfView.findViewById(R.id.tvDateWalletRow);
                        tvDateWalletRow.setText(getDateFromMillis(w.getDateInMillis()));

                        ImageView imgDeleteWallet = (ImageView) secondHalfView.findViewById(R.id.imgDeleteWallet);
                        imgDeleteWallet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog(w.getId());
                            }
                        });

                    }
                })

                .build();

        tcard.setClickable(false);
        tcard.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                Utils.setDefaultWalletIdPref(w.getId(),getContext());
                FragmentTransaction ft = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
                Fragment f = MovementFragment.newInstance("movement_fragment");
                ft.replace(R.id.fragment_container, f,"my_tag").commit();
            }
        });
        return tcard;

    }

    private Wallet getWalletFromCursor(Cursor c) {

        Wallet w = new Wallet(
                c.getString(Const.WALLET_NAME_INDEX),
                c.getDouble(Const.WALLET_LIMIT_MONTH_INDEX),
                c.getLong(Const.WALLET_DATE_INDEX),
                c.getDouble(Const.WALLET_BALANCE_INDEX));

        w.setId(c.getInt(Const.WALLET_ID_INDEX));
        return w;
    }

    private String getDateFromMillis(long millis) {
        return Utils.getDateFromMillis(millis);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    private void confirmDialog(final int id){


        MaterialDialog am = new MaterialDialog.Builder(getContext()).build();

        new MaterialDialog.Builder(getContext())
                .title(getContext().getString(R.string.Delete_wallet_and_all_its_movement))
                .negativeText("back")
                .positiveText("confirm")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        db = new DBHelper(getContext());
                        db.deleteWallet(id);



                        int newDefaultId = Utils.getWalletsFromCursors(db.getWallets()).size() > 0 ? Utils.getWalletsFromCursors(db.getWallets()).get(0).getId() : -1;
                        Log.i(MainActivity.TAG, "newDefaultWalledId: " + newDefaultId);
                        Utils.setDefaultWalletIdPref(newDefaultId, getContext());

                        MyApplication.bus.post("WalletCursorAdapter_delete");

                        swapCursor(db.getWallets());
                        db.close();
                    }
                }).show();
    }


   /* @Override
    public void onClick(Card card, View view) {
        getContext().g
    }*/
}
