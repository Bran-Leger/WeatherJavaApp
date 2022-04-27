package com.example.weatherjavaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

public class ZipcodeDialogFragment extends AppCompatActivity {

    public static class ZipcodeDialogFragmentInner extends AppCompatDialogFragment {
        ZipcodeDialogInterface z;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.enterZip);

            // inflate the layout
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.zipcode_fragment, null));

            //builder.setMessage(R.string.enterZip)

            builder
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // Put zipcode into Roomdatabase!
                            z.onDialogPositiveClick(ZipcodeDialogFragmentInner.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            z.onDialogNegativeClick(ZipcodeDialogFragmentInner.this);
                        }
                    });
            // Create the AlertDialog object and return it

            AlertDialog dialog = builder.create();

            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            return dialog;
        }

        @Override
        public void onAttach(Activity activity){
            super.onAttach(activity);
            z = (ZipcodeDialogInterface) activity;
        }
    }

}
