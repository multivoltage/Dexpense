package com.tonini.diego.dexpense;

import android.os.Bundle;

import com.tonini.diego.dexpense.model.Category;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.List;


public class ExitActivity extends AddMovementActivity {


    @Override
    public void setUpOptions() {
        List<Category> categories = Utils.getCategoryFromCursor(dbHelper.getCategory(Const.TABLE_CATEGORYEXIT));

        // add before fro resource
        for(String o : getResources().getStringArray(R.array.categoryexit))
            optiosn.add(o);
        for(Category cat : categories)
            optiosn.add(cat.getName());
        // finish add last
        optiosn.add(getString(R.string.custom));
    }

    @Override
    public void init(Bundle savedInstance) {

            getSupportActionBar().setTitle(getString(R.string.New_Exit));
    }

    @Override
    public int getLayout() {
        return R.layout.activity_add_exit;
    }

    @Override
    public boolean isEntry() {
        return false;
    }
}
