package com.tonini.diego.dexpense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.utils.Utils;
import com.tonini.diego.dexpense.wizard.SandwichWizardModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    public static final String KEY_TITLE = "Setting";
    private DBHelper dbHelper;
    private Spinner spinnerValuta;
    private List<String> listValute;
    private ViewGroup croutonParent = null;
    private boolean secondRun = false;
    private TextView tvNewCustomEntry,tvNewCustomExit;
    private Style primaryBlue;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(String title){
        SettingFragment f = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        dbHelper = new DBHelper(getActivity());
        primaryBlue = new Style.Builder().setBackgroundColorValue(getResources().getColor(R.color.primary)).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        croutonParent = container;
        setFloatingButtonVisible(false);

        spinnerValuta = (Spinner) v.findViewById(R.id.spinnerValuta);
        Set<String> setValute = SandwichWizardModel.valute.keySet();
        listValute = new ArrayList<>(setValute);
        ArrayAdapter<String> adapterValute = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,listValute);
        spinnerValuta.setAdapter(adapterValute);
        spinnerValuta.setOnItemSelectedListener(this);

        tvNewCustomEntry = (TextView) v.findViewById(R.id.tvNewCustomEntry);
        tvNewCustomEntry.setOnClickListener(this);
        tvNewCustomExit  = (TextView) v.findViewById(R.id.tvNewCustomExit);
        tvNewCustomExit.setOnClickListener(this);

        return v;
    }

    private void setFloatingButtonVisible(boolean visible){
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        setFloatingButtonVisible(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(secondRun) {
            String valutaKeySelected = listValute.get(position);
            String newCurrency = SandwichWizardModel.valute.get(valutaKeySelected);
            Utils.setCurrency(newCurrency, getActivity());
            Crouton.makeText(getActivity(), getString(R.string.Chanched_currency), primaryBlue, croutonParent).show();
        }

        secondRun = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvNewCustomEntry: addNewCustomDescription(true);
                break;

            case R.id.tvNewCustomExit: addNewCustomDescription(false);
                break;
        }
    }

    private void addNewCustomDescription(final boolean isEntry){

        MaterialDialog.Builder m = new MaterialDialog.Builder(getActivity());
        LayoutInflater inflater = m.build().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_desc, null);
        final EditText editNewDesc = (EditText) dialogView.findViewById(R.id.editNewDesc);
        m.customView(dialogView,true);
        m.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                String desc = editNewDesc.getText().toString();
                if(desc.length()>3){
                    dbHelper.insertCategory(isEntry,editNewDesc.getText().toString());
                    Crouton.makeText(getActivity(), getString(R.string.Added)+" "+desc, primaryBlue, croutonParent).show();
                } else {
                    Crouton.makeText(getActivity(), getString(R.string.Insert_at_leats_3_chars), Style.ALERT, croutonParent).show();
                }

            }
        });
        m.title(getString(R.string.New_description));
        m.negativeText("back");
        m.positiveText("confirm");
        m.show();
    }


}
