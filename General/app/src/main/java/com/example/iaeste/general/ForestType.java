package com.example.iaeste.general;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;



public class ForestType extends DialogFragment{
    final CharSequence[] forestType ={"A","B","C","D","E"};
    String selection;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose one of forest type").setSingleChoiceItems(forestType, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0 :
                        selection = (String) forestType[which];
                        break;
                    case 1:
                        selection = (String) forestType[which];
                        break;
                    case 2:
                        selection = (String) forestType[which];
                        break;
                    case 3:
                        selection = (String) forestType[which];
                        break;
                    case 4:
                        selection = (String) forestType[which];
                        break;
                }
            }
        }).setPositiveButton("Summit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "You selected "+ selection+ " type", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }
}
