package com.flash.gotosleep;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ScreenFlashDialog extends AppCompatDialogFragment {

    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permission required")
                .setMessage("In order to use this setting, the app needs system permission," +
                        " so we can change the required settings, these permissions will only be used to control the chosen behaviour \n\n" +
                        "After pressing ALLOW find \"Go To Sleep\" and turn on the switch")
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.screenFlash.setChecked(false);
                    }
                })
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialogInterface, int i) {
                        Context currentContext = getContext();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!Settings.System.canWrite(currentContext)) {
                                Intent newI = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                currentContext.startActivity(newI);
                            }
                        }
                    }
                });


        return builder.create();
    }
}
