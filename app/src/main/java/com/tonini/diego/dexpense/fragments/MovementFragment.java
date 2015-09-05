package com.tonini.diego.dexpense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.melnykov.fab.FloatingActionButton;
import com.tonini.diego.dexpense.AddMovementActivity;
import com.tonini.diego.dexpense.EntryActivity;
import com.tonini.diego.dexpense.ExitActivity;
import com.tonini.diego.dexpense.R;
import com.tonini.diego.dexpense.database.DBHelper;
import com.tonini.diego.dexpense.list.MovementCursorCardAdapter;
import com.tonini.diego.dexpense.utils.Const;
import com.tonini.diego.dexpense.utils.Utils;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardListView;

public class MovementFragment extends Fragment implements View.OnClickListener {

    public static final String KEY_TITLE = "Movement";
    public static final String KEY_BUNDLE_EDIT = "com.diego.tonini.dexpence.editmovement";
    public static final String KEY_BUNDLE_ADD  = "com.diego.tonini.dexpence.addmovement";

    private CardListView listViewMovement;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private MovementCursorCardAdapter cursorCardAdapter;
    private DBHelper dbHelper;
    private ViewGroup croutonParent = null;
    private FloatingActionButton fabButton; // no keep style

    private FloatingActionsMenu multiple_actions;
    private com.getbase.floatingactionbutton.FloatingActionButton fbNewExit,fbNewEntry;

    public MovementFragment() {
    }

    public static MovementFragment newInstance(String title){
        MovementFragment f = new MovementFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance) {

        View v = inflater.inflate(R.layout.fragment_movement, container, false);
        croutonParent = container;

        fabButton = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);
        fabButton.hide();
        /*if(Utils.getDefaultWalletIdPref(getActivity())==-1)
            fabButton.hide(true);
        else
            fabButton.show(true);*/


        cursorCardAdapter = new MovementCursorCardAdapter(
                getActivity(),
                dbHelper.getMovementsOfWallet(Utils.getDefaultWalletIdPref(getActivity()),Const.KEY_MOVEMENT_DATE,true),false);//dbHelper.getMovementsOfWallet(Utils.getDefaultWalletIdPref(getActivity()), Const.KEY_MOVEMENT_NAME),false, true);

        listViewMovement = (CardListView) v.findViewById(R.id.listViewMovement);
        if (listViewMovement != null) {
           listViewMovement.setAdapter(cursorCardAdapter);
        }
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.table_row_appear), 0.5f); //0.5f == time between appearance of listview items.
        listViewMovement.setLayoutAnimation(lac);
        listViewMovement.setFastScrollEnabled(true);
        listViewMovement.setEmptyView(v.findViewById(R.id.emtyView));

        /*fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddMovement();
            }
        });
        fabButton.attachToListView(listViewMovement);*/


        multiple_actions = (FloatingActionsMenu) v.findViewById(R.id.multiple_actions);
        fbNewExit = (com.getbase.floatingactionbutton.FloatingActionButton) v.findViewById(R.id.fbNewExit);
        fbNewEntry = (com.getbase.floatingactionbutton.FloatingActionButton) v.findViewById(R.id.fbNewEntry);
        fbNewEntry.setOnClickListener(this);
        fbNewExit.setOnClickListener(this);
        if(Utils.getDefaultWalletIdPref(getActivity())==-1)
            multiple_actions.setVisibility(View.GONE);
        else
            fabButton.setVisibility(View.VISIBLE);

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1){

            Style primaryBlue = new Style.Builder().setBackgroundColorValue(getResources().getColor(R.color.primary)).build();

            if(resultCode == AddMovementActivity.RESULT_EDIT){
                Crouton.makeText(getActivity(),getString(R.string.Updated),primaryBlue ,croutonParent).show();
            }
            else if(resultCode == AddMovementActivity.RESULT_ADDED){
                Crouton.makeText(getActivity(),getString(R.string.Added),primaryBlue,croutonParent).show();
            }
            else if(resultCode == AddMovementActivity.RESULT_ADDED_WITH_REPEATING){
                //Crouton.makeText(getActivity(),"Added with repeating",primaryBlue ,croutonParent).show();
            }
            else if(resultCode == AddMovementActivity.RESULT_CANCELLED){
                Crouton.makeText(getActivity(),getString(R.string.Cancelled),primaryBlue ,croutonParent).show();
            }

            cursorCardAdapter.swapCursor(dbHelper.getMovementsOfWallet(Utils.getDefaultWalletIdPref(getActivity()),Const.KEY_MOVEMENT_DATE,true));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fbNewExit :
                multiple_actions.collapse();
                getActivity().startActivityForResult(new Intent(getActivity(), ExitActivity.class), 1);
                break;
            case R.id.fbNewEntry :
                multiple_actions.collapse();
                getActivity().startActivityForResult(new Intent(getActivity(), EntryActivity.class), 1);
                break;
        }
    }
}
