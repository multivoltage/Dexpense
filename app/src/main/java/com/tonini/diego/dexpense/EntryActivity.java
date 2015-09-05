package com.tonini.diego.dexpense;

import android.os.Bundle;

import com.tonini.diego.dexpense.model.Category;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.List;

public class EntryActivity extends AddMovementActivity {


    @Override
    public int getLayout() {
        return R.layout.activity_add_entry;
    }

    @Override
    public boolean isEntry() {
        return true;
    }

    @Override
    public void init(Bundle savedInstance) {
        getSupportActionBar().setTitle(getString(R.string.New_Entry));
    }

    @Override
    public void setUpOptions() {

        List<Category> categories = Utils.getCategoryFromCursor(dbHelper.getCategory(Const.TABLE_CATEGORYENTRY));

        // add before fro resource
        for(String o : getResources().getStringArray(R.array.categoryentry))
            optiosn.add(o);
        for(Category cat : categories)
            optiosn.add(cat.getName());
        // finish add last
        optiosn.add(getString(R.string.custom));
    }
}
