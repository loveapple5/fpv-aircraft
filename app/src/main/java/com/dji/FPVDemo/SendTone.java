package com.dji.FPVDemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by LEX on 2016/10/14.
 */
public class SendTone {

    //    set tonesignal parameter
//    private final double freqOfTone; // hz

    private final int duration = 1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
//    public final int freq; // hz
    private final double freqOfTone1 = 1000; // hz
    private final double freqOfTone2 = 2000; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];
    public  AudioTrack audioTrack;



    public void genTone1(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone1));
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;

        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    public void genTone2(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone2));
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;

        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
    public void sendSig(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.flush();
        audioTrack.setStereoVolume(1, 0);   // set the L channel
//        audioTrack.setLoopPoints(0,numSamples/2, -1);

        audioTrack.play();
    }

    public void stopSig(){
        if(audioTrack!=null){
            audioTrack.release();
            audioTrack=null;
        }
    }

    // new thread for signal generation
//    Handler handler = new Handler();
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Use a new tread
//        final Thread thread = new Thread(new Runnable() {
//            public void run() {
//                genTone();
//                handler.post(new Runnable() {
//                    public void run() {
//
//                        sig.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if(sig.isChecked()) {  sendSig();}
//                                else {  stopSig();}
//                            }
//                        });
//
//                    }
//
//                });
//            }
//        });
//        thread.start();
//    }

}
