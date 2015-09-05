package com.tonini.diego.dexpense.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonini.diego.dexpense.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Diego on 30/03/2015.
 */
public class ColoredCardHeader extends CardHeader {

    private int colorResId = R.color.md_green_600;
    private String title;

    public ColoredCardHeader(Context context,int colorResId) {
        super(context, R.layout.my_colored_cardheader_layout);
        this.colorResId = colorResId;
    }



    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.title = title;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view!=null) {
            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle2);
            if (tvTitle != null)
                tvTitle.setTextColor(getContext().getResources().getColor(colorResId));
                tvTitle.setText(title);


        }
    }

}
