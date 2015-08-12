package org.teiath.ellak.ellakandroideducation;

import android.app.Activity;
import android.os.Bundle;

/**
 * Παρουσιάζει και τις επιλογές εκτέλεσης ενός ερωτηματολογίου. Οι επιλογές αφορούν: <lu>
 *     <li> Το γνωστικό αντικείμενο της εξέτασης </li>
 *     <li> Την κατάσταση λειτουργίας</li>
 * </lu>
 * Τα διαθέσημα γνωστικά αντικείμενα διαβάζονται από τη Βάση Δεδομένων. Η κατάσταση λειτουργίας είναι "Εκπαίδευση" ή
 * "Εξέταση".
 * Η activity μπορεί να περιλαμβάνει κουμπί τερματισμού του προγράμματος.
 * H Option Screen οδηγεί στην Main Screen ή στον τερματισμό της εφαρμογής.
 */
public class OptionScreen extends Activity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.lay_option);
    }


}
