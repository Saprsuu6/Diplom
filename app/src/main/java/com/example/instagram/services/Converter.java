package com.example.instagram.services;

import java.io.File;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoAttributes;
import ws.schild.jave.VideoSize;

;

public class Converter {
    public static File videToEmbed(File source, int height, int width) {
        /* Step 1. Declaring source file and Target file */
        String extension = source.getPath().substring(source.getPath().lastIndexOf("."));
        String newPath = source.getPath().replace(extension, ".embed");
        File target = new File(newPath);

        /* Step 2. Set Audio Attrributes for conversion*/
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        audio.setBitRate(64000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        /* Step 3. Set Video Attributes for conversion*/
        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setX264Profile(VideoAttributes.X264_PROFILE.BASELINE);
        video.setBitRate(160000);
        video.setFrameRate(15);
        video.setSize(new VideoSize(width, height));

        /* Step 4. Set Encoding Attributes*/
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("embed");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        /* Step 5. Do the Encoding*/
        try {
            Encoder encoder = new Encoder();
            //encoder.encode(source, target, attrs); // TODO find solution
            return target;
        } catch (Exception e) {
            /*Handle here the video failure*/
            e.printStackTrace();
        }

        return null;
    }
}
