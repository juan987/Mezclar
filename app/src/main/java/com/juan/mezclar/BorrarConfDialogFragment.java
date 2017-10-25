package com.juan.mezclar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Juan on 25/10/2017.
 * Como en:
 * https://developer.android.com/reference/android/app/DialogFragment.html
 * y como en:
 * https://developer.android.com/guide/topics/ui/dialogs.html
 *
 */

public class BorrarConfDialogFragment extends DialogFragment {

    public static BorrarConfDialogFragment newInstance(int title) {
        BorrarConfDialogFragment frag = new BorrarConfDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((FragmentAlertDialog)getActivity()).doPositiveClick();
                                getActivity().doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((FragmentAlertDialog)getActivity()).doNegativeClick();
                                (getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }  */
}
