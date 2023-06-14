package com.example.instagram.services.interfaces;

import android.net.Uri;

public interface Downloader {
    void downloadFile(Uri url, String extension, String title);
}
