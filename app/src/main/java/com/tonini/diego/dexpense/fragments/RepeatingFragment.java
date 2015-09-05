package com.tonini.diego.dexpense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;

public class RepeatingFragment extends Fragment {

    public static final String KEY_TITLE = "repeating_fragment";
    private DBHelper dbHelper;
    private ViewGroup croutonParent = null;
    private FloatingActionButton fabButton;

    private TextView tvDataAlarmar;
    private Button removeAlarms;

    public RepeatingFragment() {
    }

    public static RepeatingFragment newInstance(String title){
        RepeatingFragment f = new RepeatingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE,title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance) {

        View v = inflater.inflate(R.layout.fragment_repeating, container, false);
        croutonParent = container;
        fabButton = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);
        fabButton.hide();



        return v;
    }

}
