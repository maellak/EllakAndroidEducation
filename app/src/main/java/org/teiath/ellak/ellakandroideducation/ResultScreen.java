package org.teiath.ellak.ellakandroideducation;

import android.app.Activity;
import android.os.Bundle;

/**
 * H ResultScreen παρουσιάζει στον χρήστη τα αποτελέσματα της εκπαίδευσης / εξέτασής του. Συγκεκριμένα παρουσιάζει: <lu>
 *     <li>Το γνωστικό αντικείμενο εξέτασης</li>
 *     <li>Τον ορισμένο χρόνο εξέτασης</li>
 *     <li>Το πλήθος των ερωτήσεων του ερωτηματολογίου</li>
 *     <li>Το πλήθος των ερωτήσεων που απαντήθηκαν</li>
 *     <li>Το πλήθος των ερωτήσεων που απαντήθηκαν σωστά</li>
 *     <li>Το πλήθος των ερωτήσεων που απαντήθηκαν λανθασμένα</li>
 *     <li>Τη βαθμολογία του χρήστη (ποσοστό σωστών απαντήσεων σε σχέση με το συνολικό αριθμό ερωτήσεων</li>
 *     <li>Αποτέλεσμα εξέτασης "Επιτυχία / Αποτυχία" (μόνο σε κατάσταση εξέτασης).</li>
 * </lu>
 * H Result Screen οδηγεί στην Option Screen.
 */
public class ResultScreen extends Activity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.lay_result);
    }


}
