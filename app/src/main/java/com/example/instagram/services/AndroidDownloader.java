package com.example.instagram.services;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.example.instagram.services.interfaces.Downloader;

public class AndroidDownloader implements Downloader {
    private final Context context;
    private final DownloadManager downloadManager;

    public AndroidDownloader(Context context) {
        this.context = context;
        downloadManager = context.getSystemService(DownloadManager.class);
    }

    @Override
    public void downloadFile(Uri url, String extension, String title) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        DownloadManager.Request request = new DownloadManager.Request(url);

        request.setMimeType(mimeType);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI & DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(title);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "...");

        downloadManager.enqueue(request);
    }
}
