package com.example.instagram.services;

import android.graphics.Bitmap;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGenerator {
    public static MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    public static Bitmap generateQR(String userLogin) throws WriterException { // TODO write path to user page
        BitMatrix matrix = multiFormatWriter.encode(userLogin, BarcodeFormat.QR_CODE, 150, 150);
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.createBitmap(matrix);
    }
}
