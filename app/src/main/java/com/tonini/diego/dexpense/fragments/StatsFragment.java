package com.tonini.diego.dexpense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.MainActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.model.Category;
import com.tonini.diego.dexpense.model.Movement;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class StatsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, CalendarDatePickerDialog.OnDateSetListener, View.OnClickListener ,OnChartValueSelectedListener {

    public static final String KEY_TITLE = "stats_fragment";
    private DBHelper dbHelper;
    private ViewGroup croutonParent = null;
    private FloatingActionButton fabButton;
    private Map<Category,Float> map = new HashMap<Category,Float>();
    private int idWallet;
    private boolean isEntry = false;
    private long from,to;
    private PieChart mChart;
    private SwitchCompat switchEntryExit;
    private RadioButton rbLastWeek,rbLastMonth,rbLastYear,rbCustom;
    private ImageView imgOpenTo,imgOpenFrom;
    private TextView tvOpenTo,tvOpenFrom;
    private TextView tvEntryExit;
    private LinearLayout layoutFrom,layoutTo;
    private Movement movementToEdit;
    private CalendarDatePickerDialog calendarDatePickerDialog;
    private static final String FRAG_TAG_FROM = "TAG_FROM";
    private static final String FRAG_TAG_TO = "TAG_TO";
    //private Typeface tf;

    public StatsFragment() {
    }

    public static StatsFragment newInstance(String title){
        StatsFragment f = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        dbHelper = new DBHelper(getActivity());
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(dbHelper.getWalletNameById(Utils.getDefaultWalletIdPref(getActivity())));
        idWallet = Utils.getDefaultWalletIdPref(getActivity());
        from = 0;
        to = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance) {

        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        croutonParent = container;

        fabButton = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);
        fabButton.hide(true);

        Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(this,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

        tvEntryExit = (TextView) v.findViewById(R.id.tvEntryExit);
        tvOpenTo = (TextView) v.findViewById(R.id.tvOpenTo);
        tvOpenFrom = (TextView) v.findViewById(R.id.tvOpenFrom);

        imgOpenTo = (ImageView) v.findViewById(R.id.imgOpenTo);
        imgOpenFrom = (ImageView) v.findViewById(R.id.imgOpenFrom);
        imgOpenTo.setOnClickListener(this);
        imgOpenFrom.setOnClickListener(this);

        layoutFrom = (LinearLayout) v.findViewById(R.id.layoutFrom);
        layoutTo   = (LinearLayout) v.findViewById(R.id.layoutTo);
        showCustomPeriod(false);

        rbLastWeek = (RadioButton) v.findViewById(R.id.rbLastWeek);
        rbLastMonth = (RadioButton) v.findViewById(R.id.rbLastMonth);
        rbLastYear = (RadioButton) v.findViewById(R.id.rbLastYear);
        rbCustom = (RadioButton) v.findViewById(R.id.rbCustom);
        rbLastWeek.setOnCheckedChangeListener(this);
        rbLastMonth.setOnCheckedChangeListener(this);
        rbLastYear.setOnCheckedChangeListener(this);
        rbCustom.setOnCheckedChangeListener(this);

        mChart = (PieChart) v.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // mChart.setDrawUnitsInChart(true);
        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        switchEntryExit = (SwitchCompat) v.findViewById(R.id.switchEntryExit);
        switchEntryExit.setOnCheckedChangeListener(this);
        mChart.setNoDataTextDescription(getString(R.string.Add_wallet_movement_to_generate_chart));

        refreshChart();
        return v;
    }

    private void setData() {

        // count is how many type of entry.

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.

        int i=0;
        for(Category c : map.keySet()){
            xVals.add(c.getName());
            float f = map.get(c);
            Entry e = new Entry(f,i);
            yVals.add(e);
            //Log.i(MainActivity.TAG,"added entry: "+e.toString());
            i++;
        }

        PieDataSet dataSet = new PieDataSet(yVals,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(R.color.primary));
        //data.setValueTypeface(tf);
        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    // load only import category
    private void loadMap(boolean entry,long fromm, long too){
        map = Utils.getTotalForCategoryFromCursor(dbHelper.getTotalForCategory(idWallet,entry,fromm,too));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Calendar c;
        switch (buttonView.getId()){


            case R.id.rbLastWeek :
                if(isChecked) {
                    showCustomPeriod(false);
                    c = GregorianCalendar.getInstance(Locale.getDefault());
                    c.add(Calendar.DAY_OF_MONTH, -7);
                    from = c.getTimeInMillis();
                    to = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    refreshChart();
                }
                break;

            case R.id.rbLastMonth :
                if(isChecked) {
                    showCustomPeriod(false);
                    c = GregorianCalendar.getInstance(Locale.getDefault());
                    c.add(Calendar.MONTH, -1);
                    from = c.getTimeInMillis();
                    to = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    refreshChart();
                }
                break;

            case R.id.rbLastYear :
                if(isChecked) {
                    showCustomPeriod(false);
                    c = GregorianCalendar.getInstance(Locale.getDefault());
                    c.add(Calendar.YEAR, -1);
                    from = c.getTimeInMillis();
                    to = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    refreshChart();
                }
                break;

            case  R.id.rbCustom :
                if(isChecked)
                    showCustomPeriod(true);
                    refreshChart();
                break;
            case R.id.switchEntryExit :
                this.isEntry = !this.isEntry;
                String title = isEntry ? getString(R.string.Entry) : getString(R.string.Exit);
                refreshChart();
                tvEntryExit.setText(title);
                break;

        }


    }

    private void animateChart(){


        mChart.animateY(1200, AnimationEasing.EasingOption.EaseInOutQuad);
        mChart.spin(1500, 0, 360);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.PIECHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
    }

    private void refreshChart(){
        Log.i(MainActivity.TAG, "load from: " + Utils.getDateFromMillis(this.from) + " and " + Utils.getDateFromMillis(this.to));
        loadMap(this.isEntry, this.from, this.to);
        for(Category c : map.keySet()){
            //Log.i(MainActivity.TAG,"map has:"+c.getName()+ " "+map.get(c));
        }
        setData();
        animateChart();
    }

    private void showCustomPeriod(boolean show){
        layoutTo.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        layoutFrom.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        tvOpenFrom.setText(Utils.getDateFromMillis(this.from));
        tvOpenTo.setText(Utils.getDateFromMillis(this.to));
    }

    private void calendarDialog(String tag){
        Calendar c = GregorianCalendar.getInstance(Locale.getDefault());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(this,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

        calendarDatePickerDialog.show(fm, tag);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        Calendar c = new GregorianCalendar(year,monthOfYear,dayOfMonth);
        long millis = c.getTimeInMillis();
        switch (dialog.getTag()){
            case FRAG_TAG_FROM :
                this.from = millis;
                tvOpenFrom.setText(Utils.getDateFromMillis(this.from));
                break;
            case FRAG_TAG_TO :
                this.to = millis;
                tvOpenTo.setText(Utils.getDateFromMillis(this.to));
                break;
        }
        refreshChart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgOpenTo : calendarDialog(FRAG_TAG_TO);
                break;

            case R.id.imgOpenFrom : calendarDialog(FRAG_TAG_FROM);
                break;
        }
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        Style primaryBlue = new Style.Builder().setBackgroundColorValue(getResources().getColor(R.color.primary)).build();
        Crouton.makeText(getActivity(),Utils.getCurrency(getActivity())+" "+String.valueOf(entry.getVal()), primaryBlue, croutonParent).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
