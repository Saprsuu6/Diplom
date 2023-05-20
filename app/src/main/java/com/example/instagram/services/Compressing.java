package com.example.instagram.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Compressing {
    public static byte[] compress(byte[] data) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream deflater = new DeflaterOutputStream(out)) {

            deflater.write(data);
            deflater.close();
            deflater.flush();

            return out.toByteArray();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new byte[0];
    }

    public static byte[] decompress(byte[] compressedData) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream inflater = new DeflaterOutputStream(out)) {

            inflater.write(compressedData);
            inflater.close();
            inflater.flush();

            return out.toByteArray();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new byte[0];
    }
}
