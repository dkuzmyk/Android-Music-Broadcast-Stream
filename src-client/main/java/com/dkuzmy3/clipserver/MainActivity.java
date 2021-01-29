package com.dkuzmy3.clipserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int number;
    protected boolean isBound = false;
    private boolean stoppable = false;
    private boolean pausable = false;
    private boolean playable = false;
    private musicAIDL musicObj;


    EditText numTrack;
    String input;

    private Intent musicServiceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numTrack = findViewById(R.id.track_num);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(isBound){unbindService(this.myConnection);}
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void start_service(View w){
        // start service
        Log.i("onStartCommand", "isBound: "+isBound);
        if(!isBound) {
            boolean connection = false;
            Intent intent = new Intent(musicAIDL.class.getName());

            ResolveInfo info = getPackageManager().resolveService(intent, 0);

            intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            connection = bindService(intent, this.myConnection, Context.BIND_AUTO_CREATE);

            if (connection) Log.i("onStartCommand", "connected!");
            else Log.i("onStartCommand", "failed to connect.");
            playable = true;
        }
    }

    public void stop_service(View w){
        // stop service
        if(playable) {
            Log.i("stop_service", "STOPPED!");
            if (isBound) {
                unbindService(this.myConnection);
            }
            try {
                musicObj.stopMusic(number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            playable = false;
            isBound = false;
            pausable = false;
            stoppable = false;
        }
    }

    public void play_func(View w) {
        if(playable){
            stoppable = true;
            pausable = true;
            input = numTrack.getText().toString();
            try {
                number = Integer.parseInt(input);
            } catch (Exception e) {
                Log.i("Error!", "input is not an integer");
            }
            if(number == 1 || number == 2 || number == 3 || number == 4 || number == 5) {
                try {
                    musicObj.playMusic(number);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                numTrack.setFocusable(false);
                Log.i("play_func", "running with track " + number);
            }
            else {
                Toast.makeText(getApplicationContext(), "Invalid track", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pause_func(View w) {
        if(pausable){
            try {
                musicObj.pauseMusic(number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i("pause_func", "paused");
            pausable = false;
        }
    }

    public void stop_func(View w){
        if(stoppable) {
            Log.i("stop_func", "stopped");
            try {
                musicObj.stopMusic(number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            numTrack.setFocusableInTouchMode(true);
            stoppable = false;
            pausable = false;
        }
    }


    private final ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicObj = musicAIDL.Stub.asInterface(iBinder);
            isBound = true;
            Log.i("onServiceConnected", "onServiceConnected: works");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicObj = null;
            isBound = false;
            Log.i("onServiceDisconnected", "onServiceDisconnected: works");
        }
    };

}