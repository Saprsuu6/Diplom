package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.instagram.R;

import java.io.File;

public class DownloadCompletedReceiver extends BroadcastReceiver {
    private final static long defaultValue = -1L;

    @Override
    public void onReceive(Context context, Intent intent) {
        File file = null;

        String action = intent.getAction();
        if (action.equals("android.intent.action.DOWNLOAD_COMPLETE")) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, defaultValue);

            if (id != defaultValue) {
                Toast.makeText(context, context.getResources().getString(R.string.file_loader), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
