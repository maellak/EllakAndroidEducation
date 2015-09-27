package org.teiath.ellak.ellakandroideducation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Αποτελεί την αρχική οθόνη της εφαρμογής. Παρουσιάζει τα στοιχεία του project (ΕΛ/ΛΑΚ, τίτλος εφαρμογής, κλπ).
 * Καλό είναι να καταλαμβάνει όλη την οθόνη (να κρύβει την notification bar).
 * Ελέγχει αν υπάρχει update της βάσης και αν υπάρχει ρωτάει τον χρήστη αν θέλει να πραγματοποιηθεί και...
 * πράττει ανάλογα.
 * Η Splash Screen οδηγεί στην Option Screen.
 */
public class SplashScreen extends Activity
{
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private UpdateHandler UpdateHandler;
    private DBHandler DBHandler;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_splash);

        UpdateHandler= new UpdateHandler(this);
        DBHandler = new DBHandler(this);

        float db = DBHandler.GetVersion();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){

            float Newdb = UpdateHandler.GetVersion();
            if(db != Newdb) {
                new AlertDialog.Builder(SplashScreen.this)
                        .setTitle(R.string.choice)
                        .setMessage(R.string.newdb)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LoadNewDb start = new LoadNewDb();
                                start.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mainIntent = new Intent(SplashScreen.this, OptionScreen.class);
                                SplashScreen.this.startActivity(mainIntent);
                                SplashScreen.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

            }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent mainIntent = new Intent(SplashScreen.this, OptionScreen.class);
                        SplashScreen.this.startActivity(mainIntent);
                        SplashScreen.this.finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        }

        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent mainIntent = new Intent(SplashScreen.this, OptionScreen.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    class LoadNewDb extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            UpdateHandler.GetNewDB();
            return "finished";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(getApplicationContext(), "Η βάση δεδομένων ανανεώνεται, παρακαλώ περιμένετε ...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String result) {
            Intent mainIntent = new Intent(SplashScreen.this, OptionScreen.class);
            SplashScreen.this.startActivity(mainIntent);
            SplashScreen.this.finish();
        }
    }

}
