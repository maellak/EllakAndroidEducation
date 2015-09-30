package org.teiath.ellak.ellakandroideducation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.LinkedList;

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
    private Button End;
    private Spinner spinner;
    private RadioGroup rg;
    private String field;
    private String option;
    private LinkedList list = new LinkedList();
    DBHandler mydb;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_option);

        mydb = new DBHandler(this);

        spinner = (Spinner) findViewById(R.id.spinner);
        End = (Button) findViewById(R.id.end);
        rg = (RadioGroup) findViewById(R.id.radiogroup);
        list = mydb.GetKategories();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                field = spinner.getSelectedItem().toString();
                option = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

                new AlertDialog.Builder(OptionScreen.this)
                        .setTitle(R.string.choice)
                        .setMessage(R.string.next)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OptionScreen.this, MainScreen.class);
                        intent.putExtra("field", field);
                        intent.putExtra("option", option);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onCreate(null);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        End.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
