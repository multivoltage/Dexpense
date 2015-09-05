package com.tonini.diego.dexpense;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public abstract class AddMovementActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CalendarDatePickerDialog.OnDateSetListener , CompoundButton.OnCheckedChangeListener {

    public static final int RESULT_CANCELLED = 299;
    public static final int RESULT_ADDED = 300;
    public static final int RESULT_ADDED_WITH_REPEATING = 301;
    public static final int RESULT_EDIT = 303;

    protected DBHelper dbHelper;
    protected TextView tvBackMovement,tvConfirmMovement;
    protected TextView tvAddMovementDate;
    protected CheckBox checkboxAdmovementIsRepeating;

    protected EditText editAddMovementDesc;
    protected EditText editAddMovementImport;
    protected List<String> optiosn = new ArrayList<String>();
    protected RadioGroup radioGroupOptions,radioGroupRepeating;
    protected RadioButton radioButtonDeltaWeek,radioButtonDeltaMonth;

    private long date;
    private long deltaRepeating = 0;
    private String desc = "";
    private double initialValue = 0;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    private Movement movementToEdit;
    private CalendarDatePickerDialog calendarDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());


        dbHelper = new DBHelper(this);
        getSupportActionBar().hide();// hide black and show only toolbar with red/green color
        date = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();

        editAddMovementDesc = (EditText) findViewById(R.id.editAddMovementDesc);
        editAddMovementImport = (EditText) findViewById(R.id.editAddMovementImport);
        tvBackMovement = (TextView) findViewById(R.id.tvBackMovement);
        tvConfirmMovement    = (TextView) findViewById(R.id.tvConfirmMovement);
        tvBackMovement.setOnClickListener(this);
        tvConfirmMovement.setOnClickListener(this);

        radioGroupOptions = (RadioGroup) findViewById(R.id.radioGroupOptions);
        radioGroupOptions.setOnCheckedChangeListener(this);
        radioGroupRepeating = (RadioGroup) findViewById(R.id.radioGroupRepeating);
        radioGroupRepeating.setOnCheckedChangeListener(this);

        radioButtonDeltaWeek = (RadioButton) findViewById(R.id.radioButtonDeltaWeek);
        radioButtonDeltaMonth = (RadioButton) findViewById(R.id.radioButtonDeltaMonth);
        radioButtonDeltaMonth.setOnCheckedChangeListener(this);
        radioButtonDeltaWeek.setOnCheckedChangeListener(this);

        tvAddMovementDate = (TextView) findViewById(R.id.tvAddMovementDate);
        tvAddMovementDate.setText(Utils.getDateFromMillis(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
        tvAddMovementDate.setOnClickListener(this);
        checkboxAdmovementIsRepeating = (CheckBox) findViewById(R.id.checkboxAdmovementIsRepeating);
        checkboxAdmovementIsRepeating.setVisibility(View.INVISIBLE);
        //checkboxAdmovementIsRepeating.setOnCheckedChangeListener(this);


        setUpOptions();

        RadioButton radioButton;
        for(String option : optiosn){
            radioButton = new RadioButton(this);
            radioButton.setText(option);
            switch (getLayout()){
                case R.layout.activity_add_entry :
                    radioButton.setTextColor(getResources().getColor(R.color.md_green_600));
                    break;
                case R.layout.activity_add_exit :
                    radioButton.setTextColor(getResources().getColor(R.color.md_red_600));
                    break;
            }
            radioGroupOptions.addView(radioButton);
        }

        Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(this,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));


        /* we check is activity is invoked for edit movement */
        Bundle b = getIntent().getExtras();
        if(b!=null){
            movementToEdit = b.getParcelable("key_serialize_mov");
            initialValue = movementToEdit.getValue();
            editAddMovementDesc.setText(movementToEdit.getName());
            editAddMovementImport.setText(String.valueOf(Math.abs(initialValue)));
            if(movementToEdit.getRepeatingTime()>0){
                checkboxAdmovementIsRepeating.setChecked(true);
                if(movementToEdit.getRepeatingTime()==Const.DELTA_MONTH_REPEATING){ radioButtonDeltaMonth.setChecked(true); }
                else { radioButtonDeltaWeek.setChecked(true); }
            }
            tvAddMovementDate.setText(Utils.getDateFromMillis(movementToEdit.getDateInMillis()));

            boolean found = false;
            for(int i=0;i<optiosn.size()-1;i++){
                RadioButton rb = (RadioButton) radioGroupOptions.getChildAt(i);
                String textRb = rb.getText().toString();
                if(movementToEdit.getName().equals(textRb)){
                    rb.setChecked(true);
                    found = true;
                }
            }
            if(!found){
                RadioButton rbCustom = (RadioButton) radioGroupOptions.getChildAt(optiosn.size()-1);
                rbCustom.setChecked(true);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvConfirmMovement :

                if(checkboxAdmovementIsRepeating.isChecked()){
                    insertMovement(true);

                } else {
                    insertMovement(false);
                }
                Log.i(MainActivity.TAG,"AddMovementActivity: "+"licked Confirm");
                break;

            case R.id.tvBackMovement :
                setResult(RESULT_CANCELLED);
                finish();
                break;

            case R.id.tvAddMovementDate :
                calendarDialog();
                break;
        }
    }

    private void insertMovement(boolean repeating){

        // se i campi non sono vuoti
        if(checkField()){

                if(repeating){
                    deltaRepeating = radioButtonDeltaMonth.isChecked() ? Const.DELTA_MONTH_REPEATING : Const.DELTA_WEEK_REPEATING;
                } else {
                    deltaRepeating = 0;
                }

                if(editAddMovementDesc.getVisibility()==View.VISIBLE){
                    desc = editAddMovementDesc.getText().toString();
                    if(editAddMovementDesc.getText().toString().length()==0)
                        desc = editAddMovementDesc.getHint().toString();
                }

                double value = Double.parseDouble(editAddMovementImport.getText().toString());
                int parentWalletId = Utils.getDefaultWalletIdPref(this); // for temp

                if(!isEntry()){
                    value = value * -1;
                }

                Movement m = new Movement(desc,date,value,parentWalletId,deltaRepeating);

                // if is an edit
                if(getIntent().getExtras()!=null){
                    m.setId(movementToEdit.getId());
                    dbHelper.updateMovement(m,m.getValue() - initialValue);
                    setResult(RESULT_EDIT);
                    Log.i(MainActivity.TAG,"AddMovementActivity: "+"set RESULT_EDIT");
                    finish();

                } else {
                    long id = dbHelper.insertMovement(m);

                    setResult(isEntry() ? RESULT_ADDED : RESULT_ADDED_WITH_REPEATING);
                    Log.i(MainActivity.TAG,"AddMovementActivity: "+"set RESULT_Add");
                    finish();
                    // already setted result
                }


        } else {
            Crouton.makeText(this,getString(R.string.Incomplete_fields), Style.ALERT).show();
        }
    }

    // invoked by last radiogroup option
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

       switch (group.getId()){

            case R.id.radioGroupOptions :
                // checkedId is equals position, start from 1, not from 0

                if(group.getCheckedRadioButtonId()!=-1){
                    int id= group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(id);
                    int radioId = group.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) group.getChildAt(radioId);
                    desc = (String) btn.getText();

                    if(btn.getText().equals(optiosn.get(optiosn.size()-1))){
                        editAddMovementDesc.setVisibility(View.VISIBLE);

                    } else {
                        editAddMovementDesc.setVisibility(View.INVISIBLE);
                    }
                }
                break;
        }

    }

    // invoked by checkbox
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.checkboxAdmovementIsRepeating :
                if(isChecked){
                    radioGroupRepeating.setVisibility(View.VISIBLE);
                    // set month repeating as default
                    deltaRepeating = Const.DELTA_MONTH_REPEATING;
                    ((RadioButton)findViewById(R.id.radioButtonDeltaMonth)).setChecked(true);
                } else {
                    radioGroupRepeating.setVisibility(View.INVISIBLE);
                    deltaRepeating = 0;
                }
                break;
        }

    }

    private void calendarDialog(){
        Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        FragmentManager fm = getSupportFragmentManager();
        calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(this,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        calendarDatePickerDialog.show(fm,FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,monthOfYear);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        // to selected day at 12:05....because probabily user have phone on
        date = c.getTimeInMillis();

        tvAddMovementDate.setText(Utils.getDateFromMillis(date));
    }

    @Override
    public void onResume() {
        // reattaching to the fragment
        super.onResume();
        calendarDatePickerDialog = (CalendarDatePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }
    }

    private boolean checkField(){
        return editAddMovementImport.getText().toString().length()>0 && desc.length()>0;
    }


    public abstract void setUpOptions();
    public abstract void init(Bundle savedInstance);
    public abstract int getLayout();
    public abstract boolean isEntry();

    @Override
    public void onDestroy(){
        Log.i(MainActivity.TAG,"called onDestroy()");
        super.onDestroy();
    }
}

via 20 settembre 133 casa veloce 9.12: 16:19