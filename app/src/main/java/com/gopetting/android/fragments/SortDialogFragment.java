package com.gopetting.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gopetting.android.R;

/**
 * Created by Sumit on 9/7/2016.
 */


public class SortDialogFragment extends DialogFragment {


    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */
    public interface SortDialogListener {
        public void onDialogItemClick(SortDialogFragment dialogFragment, int position);
    }

    // Use this instance of the interface to deliver action events
    SortDialogListener mDialogListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setItems(R.array.sort_by, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        mDialogListener.onDialogItemClick(SortDialogFragment.this, position);
                    }
                    });

        return builder.create();
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SortDialogListener so we can send events to the host
            mDialogListener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SortDialogListener");
        }
    }
}