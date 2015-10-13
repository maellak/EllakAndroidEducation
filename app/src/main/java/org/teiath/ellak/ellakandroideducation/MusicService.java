package org.teiath.ellak.ellakandroideducation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener
{
    private final IBinder mBinder = new LocalBinder ();

    public class LocalBinder extends Binder
    {
        MusicService getService ()
        {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind (Intent intent)
    {
        return mBinder;
    }

    final String[] Tracks = {"Motherlode.mp3","Epic Song.mp3","Fater Lee.mp3","Hustle.mp3","Just Us.mp3",
            "SlowBurn.mp3", "SummontheRawk.mp3", "WhiskeyontheMississippi.mp3"};
    MediaPlayer MP = null;
    int CurTrack;
    static MusicService MSRef;

    public MusicService ()
    {
        MSRef = this;
    }



    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {

        System.out.println ("Starting...");
        return START_STICKY;
    }

    public void play ()
    {
        CurTrack = 0;
        if (MP != null)
        {
            MP.stop();
            MP.release();
        }
        MP = new MediaPlayer ();
        MP.setOnCompletionListener (this);
        StartSong();

    }

    public void pause (){
        MP.pause();
    }

    public void stop (){
        MP.stop ();
    }

    public void onDestroy () {
        MP.stop();

    }

    @Override
    public void onCompletion (MediaPlayer mediaPlayer)
    {
        if (++CurTrack == Tracks.length)
            CurTrack = 0;
        StartSong();
    }

    public void StartSong ()
    {
        try
        {
            MP.reset ();
            AssetFileDescriptor afd = getAssets ().openFd (Tracks[CurTrack]);
            MP.setDataSource (afd.getFileDescriptor (), afd.getStartOffset (), afd.getLength ());
            afd.close ();
            MP.setAudioStreamType (AudioManager.STREAM_MUSIC);
            MP.prepare ();
            MP.start ();
        }
        catch (IllegalArgumentException e)
        {
            System.out.println ("Error #1" + e.getMessage ());
        }
        catch (IllegalStateException e)
        {
            System.out.println ("Error #2" + e.getMessage ());
        }
        catch (IOException e)
        {
            System.out.println ("Error #3 " + e.getMessage ());
        }
    }

    public void start() {
        MP.start();
    }
    public int getCurrentPosition() {
        return MP.getCurrentPosition();

    }

    public void seekTo(int pausePosition) {
        MP.seekTo((int) pausePosition);
    }

    public boolean isPlaying() {
        MP.isPlaying();
        return true; }
}
