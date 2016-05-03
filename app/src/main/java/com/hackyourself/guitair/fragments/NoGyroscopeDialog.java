package com.hackyourself.guitair.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.hackyourself.guitair.R;
import android.util.Log;

import java.io.File;

/**
 * Created by don on 24.04.2016.
 */
public class NoGyroscopeDialog extends DialogFragment implements DialogInterface.OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error_gyroscope_title)
                .setPositiveButton(R.string.ok, this)
                .setMessage(R.string.no_gyroscope_message)
                .setPositiveButton("Поделиться", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Эта игра сделает из тебя рок звезду! http://vk.com/guitair");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Поделись!"));
                    }
                });
        return adb.create();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
