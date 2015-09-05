package com.tonini.diego.dexpense.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.MenuItem;
import android.widget.SectionIndexer;

import com.tonini.diego.dexpense.EntryActivity;
import com.tonini.diego.dexpense.ExitActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.model.ColoredCardHeader;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.model.MovementCard;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

import static com.tonini.diego.dexpense.R.color;

/**
 * Created by Diego on 29/03/2015.
 */
public class MovementCursorCardAdapter extends CardCursorAdapter implements SectionIndexer{

    private String currency;
    LinkedHashMap<String,Integer> dateIndexer;
    String[] section;
    public MovementCursorCardAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        currency = Utils.getCurrency(context);

        dateIndexer = new LinkedHashMap<String,Integer>();
        int size = c.getCount();
        String prDate = " ";

        if(c.getCount()>0){
            c.moveToFirst();

            int i=0;
            do {
                //long date = c.getLong(Const.MOVEMENT_DATE_INDEX);
                Calendar date = GregorianCalendar.getInstance(Locale.getDefault());
                date.setTimeInMillis(c.getLong(Const.MOVEMENT_DATE_INDEX));
                String month = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                String year = String.valueOf(date.get(Calendar.YEAR));
                String shortDate = month + " " + year;

                if( !shortDate.equals(prDate))
                {
                    this.dateIndexer.put(shortDate,i);
                    prDate = shortDate;
                }
                i++;

            } while (c.moveToNext());

            Set<String> sectionDates = dateIndexer.keySet();

            // create a list from the set to sort;
            ArrayList<String> sectionList = new ArrayList<String>(sectionDates);
            section = new String[sectionList.size()];
            sectionList.toArray(section);
        }


    }

    @Override
    protected Card getCardFromCursor(Cursor cursor) {

        final Movement m = getMovementFromCursor(cursor);

        final MovementCard card = new MovementCard(super.getContext(),R.layout.my_carddemo_content_movement);
        card.setUpMovement(m);
        card.setUpGeneralColor(m.getValue() > 0 ? color.md_green_600 : color.md_red_600);

        ColoredCardHeader header = new ColoredCardHeader(super.getContext(),m.getValue() > 0 ? color.md_green_600 : color.md_red_600);
        header.setTitle(m.getName());
        header.setPopupMenu(R.menu.menu_item_movement, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard baseCard, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_share : share(m);
                        break;

                    case R.id.action_delete : delete(m);

                        break;

                    case R.id.action_edit :   edit(m);
                        break;

                    //case R.id.action_to_pdf: generatePdf(m,card);

                }
            }
        });
        card.addCardHeader(header);

       return card;

    }

    private Movement getMovementFromCursor(Cursor c) {

        Movement m = new Movement(c.getString(Const.MOVEMENT_NAME_INDEX), c.getLong(Const.MOVEMENT_DATE_INDEX), c.getDouble(Const.MOVEMENT_VALUE_INDEX), c.getInt(Const.MOVEMENT_WALLET_ID_INDEX), c.getLong(Const.MOVEMENT_TIME_REPEATING_INDEX));
        m.setId(c.getInt(Const.MOVEMENT_ID_INDEX));
        return m;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    private String getDateFromMillis(long millis){
        return Utils.getDateFromMillis(millis);
    }

    private void share(Movement m){

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.Dexpense_summy));
        String message =
                getContext().getString(R.string.Date)+" :"+getDateFromMillis(m.getDateInMillis())+"\n"+
                        getContext().getString(R.string.Movements)+" :" +m.getName()+"\n"+
                        currency+" :"+String.valueOf(m.getValue());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message);

        super.getContext().startActivity(sharingIntent);
    }

    private void delete(Movement m){
        DBHelper db = new DBHelper(getContext());
        db.deleteMovement(m);
        swapCursor(db.getMovementsOfWallet(m.getParentWalletId(),Const.KEY_MOVEMENT_NAME,false));
    }

    private void edit(Movement m){
        Intent i = new Intent(getContext(),m.getValue() > 0 ? EntryActivity.class : ExitActivity.class);
        i.putExtra("key_serialize_mov",m); // correct repeating time
        ((Activity) getContext()).startActivityForResult(i, 1);
    }

    @Override
    public Object[] getSections() {
        return section;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return dateIndexer.get(section[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private void generatePdf(Movement m,Card card){


    }


}
