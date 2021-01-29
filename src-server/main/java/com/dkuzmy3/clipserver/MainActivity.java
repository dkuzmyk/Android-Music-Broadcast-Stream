package com.dkuzmy3.clipserver;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.dkuzmy3.clipserver.musicAIDL;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MainActivity extends Service {
    MediaPlayer metalPlayer;            // because we listen to heavy metal here
    private int mStartID;
    private Notification notification ;
    private final musicAIDL.Stub mBinder = new musicAIDL.Stub(){

        @Override
        public void playMusic(int number) throws RemoteException {
            Log.i("playMusic", "Activated!");

            //synchronized (metalPlayer) {
                if (metalPlayer == null) {

                    switch (number) {
                        case 1:
                            metalPlayer = MediaPlayer.create(MainActivity.this, R.raw.track1);
                            break;
                        case 2:
                            metalPlayer = MediaPlayer.create(MainActivity.this, R.raw.track2);
                            break;
                        case 3:
                            metalPlayer = MediaPlayer.create(MainActivity.this, R.raw.track3);
                            break;
                        case 4:
                            metalPlayer = MediaPlayer.create(MainActivity.this, R.raw.track4);
                            break;
                        case 5:
                            metalPlayer = MediaPlayer.create(MainActivity.this, R.raw.track5);
                            break;
                    } // end switch
                } // end if
            //} // end synchronized
            Log.i("metalPlayer", "player active, track "+number);

            startForeground(1, notification);

            metalPlayer.setLooping(false);
            metalPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSelf(mStartID);
                }
            });
            if(metalPlayer.isPlaying()){metalPlayer.seekTo(0);}
            else metalPlayer.start();        // start player
        }

        @Override
        public void pauseMusic(int number) throws RemoteException {
            Log.i("pauseMusic", "Activated!");
            synchronized (metalPlayer) {
                if(metalPlayer != null) metalPlayer.pause();    // if player exists, pause
            }
        }

        @Override
        public void stopMusic(int number) throws RemoteException {
            Log.i("stopMusic", "Activated!");
            synchronized (metalPlayer) {
                if (metalPlayer != null) {
                    metalPlayer.release();
                    metalPlayer = null;
                }
            }
        }
    };

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music player notification";
            String description = "The channel for music player notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;    // disabled that stupid annoying sound
            NotificationChannel channel = new NotificationChannel("Metal Player", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.createNotificationChannel();

        final Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        notification =
                new NotificationCompat.Builder(getApplicationContext(), "Metal Player")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true).setContentTitle("Metal ON!")
                .setContentText("Click to open")
                .setTicker("Metal is Metalling!")
                .setFullScreenIntent(pi, false).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(metalPlayer!=null){mStartID = startId;}
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(metalPlayer!=null){
            synchronized (metalPlayer) {
                if (metalPlayer != null) {
                    metalPlayer.release();
                    metalPlayer = null;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}