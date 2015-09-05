package com.tonini.diego.dexpense.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    public static final String KEY_TITLE = "Info";
    private ListView listViewLibraries;
    private TextView tvAboutMail;

    public AboutFragment() {
    }

    public static AboutFragment newInstance(String title){
        AboutFragment f = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

            setFloatingButtonVisible(false);

            TextView t1 = (TextView) rootView.findViewById(R.id.tvmaterialdrawer);
            t1.setOnClickListener(this);
            TextView t2 = (TextView) rootView.findViewById(R.id.tvfloatingactionbutton);
            t2.setOnClickListener(this);
            TextView t3 = (TextView) rootView.findViewById(R.id.tvcardlibs);
            t3.setOnClickListener(this);
            TextView t4 = (TextView) rootView.findViewById(R.id.tvcrouton);
            t4.setOnClickListener(this);
            TextView t5 = (TextView) rootView.findViewById(R.id.tvmaterialdialogs);
            t5.setOnClickListener(this);
            TextView t6 = (TextView) rootView.findViewById(R.id.tvwizardpager);
            t6.setOnClickListener(this);
            TextView t7 = (TextView) rootView.findViewById(R.id.tvbetterpickers);
            t7.setOnClickListener(this);
            TextView t8 = (TextView) rootView.findViewById(R.id.tvotto);
            t8.setOnClickListener(this);
            TextView t9 = (TextView) rootView.findViewById(R.id.tvmpandroidchartlibrary);
            t9.setOnClickListener(this);

        tvAboutMail = (TextView) rootView.findViewById(R.id.tvAboutMail);
        tvAboutMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","multivoltage@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Dexpense");
                startActivity(Intent.createChooser(emailIntent, "Send with"));
            }
        });

        PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch(PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }

            TextView tvAboutName = (TextView) rootView.findViewById(R.id.tvAboutName);
            if(pInfo!=null)
                tvAboutName.append(" - version: " + pInfo.versionName);


            return rootView;
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
    public void onClick(View v) {
        Intent browserIntent = null;

        switch (v.getId()){

            case R.id.tvmaterialdrawer : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/MaterialDrawer")); break;
            case R.id.tvfloatingactionbutton : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/makovkastar/FloatingActionButton")); break;
            case R.id.tvcardlibs : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabrielemariotti/cardslib")); break;
            case R.id.tvcrouton : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/keyboardsurfer/Crouton")); break;
            case R.id.tvmaterialdialogs : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/afollestad/material-dialogs")); break;
            case R.id.tvwizardpager : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TechFreak/WizardPager")); break;
            case R.id.tvbetterpickers : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/derekbrameyer/android-betterpickers")); break;
            case R.id.tvotto : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://square.github.io/otto/")); break;
            case R.id.tvmpandroidchartlibrary : browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PhilJay/MPAndroidChart")); break;

        }
        startActivity(browserIntent);
    }
}
