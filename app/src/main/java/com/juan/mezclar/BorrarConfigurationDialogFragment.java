package com.juan.mezclar;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import static android.R.drawable.ic_dialog_email;

/**
 * Created by Juan on 25/10/2017.
 * Como en:
 * https://developer.android.com/reference/android/app/DialogFragment.html
 * y como en:
 * https://developer.android.com/guide/topics/ui/dialogs.html
 *
 */

public class BorrarConfigurationDialogFragment extends DialogFragment {

    public static BorrarConfigurationDialogFragment newInstance(String title) {
        BorrarConfigurationDialogFragment frag = new BorrarConfigurationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                //.setIcon(R.drawable.imagen2)
                //.setIcon(android.R.drawable.ic_dialog_email)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.alert_dialog_description)
                //.setTitle(title)
                .setTitle(R.string.alert_dialog_title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((FragmentAlertDialog)getActivity()).doPositiveClick();
                            }
                        }
                )
                //.setNegativeButton(R.string.alert_dialog_cancel,
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((FragmentAlertDialog)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

    //Interface definition
    public interface FragmentAlertDialog{
        public void doPositiveClick();
        public void doNegativeClick();


    }



}
