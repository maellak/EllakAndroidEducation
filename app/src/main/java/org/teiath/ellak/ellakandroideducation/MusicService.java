package org.teiath.ellak.ellakandroideducation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Η MusicService είναι υπεύθυνη για το παιξιμο μουσικής στο  background. Η μουσική ενεργοποιείται και απενεργοποιείται
 * από το μενού το οποίο πρέπει να ενσωματωθεί σε κάθε activity εκτός SplashScreen. Τα αρχεία της μουσικής μπορεί να
 * βρίσκονται ενσωματωμένα στο project ή να διαβάζονται από τη συσκευή.
 */
public class MusicService extends Service
{
    public MusicService ()
    {
    }

    @Override
    public IBinder onBind (Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException ("Not yet implemented");
    }
}
