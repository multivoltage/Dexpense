package com.tonini.diego.dexpense.wizard;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.NumberPage;
import com.tech.freak.wizardpager.model.PageList;
import com.tech.freak.wizardpager.model.SingleFixedChoicePage;
import com.tech.freak.wizardpager.model.TextPage;
import com.tonini.diego.dexpense.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 05/04/2015.
 */
public class SandwichWizardModel extends AbstractWizardModel {

    public static int VALUTA_INDEX = 0;
    public static int NAME_WALLET_INDEX = 1;
    public static int BALANCE_INDEX = 2;
    public static int LIMI_INDEX = 3;


    public static Map<String,String> valute = new HashMap<String,String>();

    public SandwichWizardModel(Context context){
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {

        valute.put("euro","€");
        valute.put("dollar","$");
        valute.put("british pound","£");
        valute.put("peso","$");
        valute.put("lev","лв");
        valute.put("krone","kr");
        valute.put("forint","ft");
        valute.put("yen","¥");
        valute.put("lei","lei");
        valute.put("ruble","py6");
        valute.put("any","");

        return new PageList(
                new InfoPage(this,mContext.getString(R.string.Dexpense_need_conf)),
                new SingleFixedChoicePage(this,mContext.getString(R.string.Currency))
                    .setChoices(
                            valute.get("euro"),
                            valute.get("dollar"),
                            valute.get("british pound"),
                            valute.get("peso"),
                            valute.get("lev"),
                            valute.get("krone"),
                            valute.get("forint"),
                            valute.get("yen"),
                            valute.get("lei"),
                            mContext.getString(R.string.WithoutCurrency)
                            )
                        .setRequired(true),
                new TextPage(this,mContext.getString(R.string.Intro_wallet_name)).setValue(mContext.getString(R.string.Intro_main_wallet)).setRequired(false),
                new NumberPage(this,mContext.getString(R.string.Initial_Balance)).setValue("0").setRequired(true),
                new NumberPage(this,mContext.getString(R.string.Intro_month_limit_optional)).setValue("0").setRequired(false)
        );
    }

    public void linkContext(Context c){
        this.mContext = c;
    }


}
