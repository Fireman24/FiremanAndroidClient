package com.pedro.encoder.utils.gl;

import android.graphics.Bitmap;
import android.util.Log;

import com.pedro.encoder.utils.gl.gif.GifDecoder;
import com.pedro.encoder.utils.gl.watermark.WatermarkUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pedro on 22/09/17.
 */

public class GifStreamObject extends StreamObjectBase {

    private static final String TAG = "GifStreamObject";

    private int streamWidth, streamHeight;
    private int numFrames;
    private Bitmap[] gifBitmaps;
    private int[] gifDelayFrames;
    private long startDelayFrame;
    private int currentGifFrame;

    public GifStreamObject(int streamWidth, int streamHeight) {
        this.streamWidth = streamWidth;
        this.streamHeight = streamHeight;
    }

    public void load(InputStream inputStreamGif) throws IOException {
        GifDecoder gifDecoder = new GifDecoder();
        if (gifDecoder.read(inputStreamGif, inputStreamGif.available()) == 0) {
            Log.i(TAG, "read gif ok");
            numFrames = gifDecoder.getFrameCount();
            gifDelayFrames = new int[numFrames];
            gifBitmaps = new Bitmap[numFrames];
            for (int i = 0; i < numFrames; i++) {
                gifDecoder.advance();
                gifBitmaps[i] = gifDecoder.getNextFrame();
                gifDelayFrames[i] = gifDecoder.getNextDelay();
            }
            resize(gifDecoder.getWidth(), gifDecoder.getHeight());
            setPosition(0, 0);
            Log.i(TAG, "finish load gif frames!!!");
        } else {
            throw new RuntimeException("read gif error");
        }
    }

    @Override
    public void resize(int width, int height) {
        for (int i = 0; i < numFrames; i++) {
            gifBitmaps[i] = Bitmap.createScaledBitmap(gifBitmaps[i], width, height, false);
        }
    }

    @Override
    public void setPosition(int positionX, int positionY) {
        WatermarkUtil watermarkUtil = new WatermarkUtil(streamWidth, streamHeight);
        for (int i = 0; i < numFrames; i++) {
            gifBitmaps[i] = watermarkUtil.createWatermarkBitmap(gifBitmaps[i], positionX, positionY);
        }
    }

    @Override
    public void recycle() {
        for (int i = 0; i < numFrames; i++) {
            gifBitmaps[i].recycle();
        }
    }

    @Override
    public int getNumFrames() {
        return numFrames;
    }

    public int[] getGifDelayFrames() {
        return gifDelayFrames;
    }

    public Bitmap[] getGifBitmaps() {
        return gifBitmaps;
    }

    @Override
    public int updateFrame() {
        if (startDelayFrame == 0) {
            startDelayFrame = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - startDelayFrame >= gifDelayFrames[currentGifFrame]) {
            if (currentGifFrame >= numFrames - 1) {
                currentGifFrame = 0;
            } else {
                currentGifFrame++;
            }
            startDelayFrame = 0;
        }
        return currentGifFrame;
    }
}
