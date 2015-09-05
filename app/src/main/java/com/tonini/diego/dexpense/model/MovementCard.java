package com.tonini.diego.dexpense.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Diego on 30/03/2015.
 */
public class MovementCard extends Card {

    protected View colorView;
    protected TextView tvTitle;
    protected TextView tvDate;
    protected TextView tvParentWallet;
    private ImageView  imageViewEntryExit;
    private ImageView imgRepeating;
    protected int colorRedId;

    private Movement movement;

    private String currency;

    public MovementCard(Context context, int innerLayout) {
        super(context, innerLayout);
        colorRedId = super.getContext().getResources().getColor(R.color.primary);
        currency = Utils.getCurrency(context);
    }

    public void setUpGeneralColor(int colorRedId){
        this.colorRedId = super.getContext().getResources().getColor(colorRedId);
    }

    public void setUpMovement(Movement m){
        this.movement = m;
    }

    public Movement getMovement(){
        return movement;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        colorView = parent.findViewById(R.id.colorView);
        tvTitle = (TextView) parent.findViewById(R.id.tvValue);
        tvDate  = (TextView) parent.findViewById(R.id.tvDate);
        tvParentWallet = (TextView) parent.findViewById(R.id.tvParentWallet);
        imageViewEntryExit = (ImageView) parent.findViewById(R.id.imageViewEntryExit);
        imgRepeating = (ImageView) parent.findViewById(R.id.imgRepeating);

        imageViewEntryExit.setImageResource(movement.getValue() > 0 ? R.drawable.arrow_entry : R.drawable.arrow_exit);

        if(movement.getRepeatingTime() == 0){
            imgRepeating.setImageDrawable(null);
        } else if(movement.getRepeatingTime() == Const.DELTA_WEEK_REPEATING) {
            imgRepeating.setImageResource(R.drawable.repeatweek);
        } else if(movement.getRepeatingTime() == Const.DELTA_MONTH_REPEATING)
            imgRepeating.setImageResource(R.drawable.repeatmonth);

        colorView.setBackgroundColor(colorRedId);
        tvTitle.setTextColor(colorRedId);
        tvDate.setTextColor(colorRedId);
        tvParentWallet.setTextColor(colorRedId);

        tvTitle.setText(String.valueOf(movement.getValue())+" "+currency);
        /* if (tvTitle!=null)
            tvTitle.setTextColor(colorRedId);*/

        tvDate.setText(Utils.getDateFromMillis(movement.getDateInMillis()));
        tvParentWallet.setVisibility(View.GONE);//setText("portafoglio: 1");

    }




}
