package com.tonini.diego.dexpense.wizard;

import android.support.v4.app.Fragment;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;

/**
 * Created by Diego on 17/04/2015.
 */
public class InfoPage extends Page {

    public InfoPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {

        return InfoFragment.create(getKey());

    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        /*dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY),
                getKey()));*/

    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    public InfoPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
