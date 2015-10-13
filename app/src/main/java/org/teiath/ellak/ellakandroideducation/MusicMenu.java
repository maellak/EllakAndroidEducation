package org.teiath.ellak.ellakandroideducation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class TestActivity extends ActionBarActivity  {


    MusicService MS;
    boolean mBound = false;
    int length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent (this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    public ServiceConnection mConnection = new ServiceConnection() {

        //@Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            MS = binder.getService();
            mBound = true;
        }

        //@Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.Start:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                Intent ServInt = new Intent (this, MusicService.class);
                MS.play();

                return true;
            case R.id.Pause:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                MS.pause();

                return true;
            case R.id.Resume:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);


                MS.start();

                return true;
            case R.id.Stop:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                MS.stop ();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}