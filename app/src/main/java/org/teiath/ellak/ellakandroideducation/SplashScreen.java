package org.teiath.ellak.ellakandroideducation;

import android.app.Activity;
import android.os.Bundle;

/**
 * Αποτελεί την αρχική οθόνη της εφαρμογής. Παρουσιάζει τα στοιχεία του project (ΕΛ/ΛΑΚ, τίτλος εφαρμογής, κλπ).
 * Καλό είναι να καταλαμβάνει όλη την οθόνη (να κρύβει την notification bar).
 * Ελέγχει αν υπάρχει update της βάσης και αν υπάρχει ρωτάει τον χρήστη αν θέλει να πραγματοποιηθεί και...
 * πράττει ανάλογα.
 * Η Splash Screen οδηγεί στην Option Screen.
 */
public class SplashScreen extends Activity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.lay_splash);
    }

}
