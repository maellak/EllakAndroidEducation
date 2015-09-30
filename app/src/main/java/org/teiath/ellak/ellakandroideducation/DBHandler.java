package org.teiath.ellak.ellakandroideducation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Η κλάση DBHandler επιστρέφει πληροφορίες από τη βάση δεδομένων και δημιουργεί ερωτηματολόγια. Περιγράφονται κάποιες
 * από τις απαιτούμενες μεθόδους οι οποίες είναι απαραίτητες για την λειτουργία άλλων κλάσεων της εφαρμογής.
 * Η κλάση DBHandler είναι singleton.
 */
public class DBHandler {
    private static Context mcontext;
    private static DBHandler ourInstance;
    private Cursor cursor;
    private Cursor catCursor;
    private Cursor sumCursor;
    private Cursor ansCursor;
    private Cursor subCursor;
    private SQLiteDatabase sqldb;


    /**
     * Επιστρέφει αναφορά στο μοναδικό αντικείμενο που δημιουργείται από την κλάση.
     * Πρεπει να καλειται πρωτη για να λαμβανεται η αναφορα στο αντικειμενο.
     *
     * @param context ενα αντικειμενο context ειτε σαν μεταβλητη context ειτε σαν μεθοδο(getApplicationContext()).
     * @return Η αναφορά στο αντικείμενο.
     */
    public static DBHandler getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DBHandler(context.getApplicationContext());
        }
        return ourInstance;
    }

    /**
     * Ο κατασκευαστής του αντικειμένου. Εδώ τοποθετούνται οι αρχικοποιήσεις του αντικειμένου. Μία από τις λειτουργίες
     * πρέπει να είναι ο έλεγχος ύπαρξης του sqlite αρχείου στον αποθεκευτικό χώρο της εφαρμογής και η μεταφορά του από
     * τα assets αν χρειάζεται.
     */
    private DBHandler(Context context) {
        mcontext = context;
        if (!CheckDB())
            CopyDB();
    }

    /**
     * Επιστρέφει την λίστα με τα γνωστικά αντικείμενα τα οποίες βρίσκονται στη Βάση Δεδομένων. Τα γνωστικά αντικείμενα
     * επιστρέφονται ως LinkedList με αντικείμενα {@link SubjectRec}.
     *
     * @return Η λίστα με τις κατηγορίες εξέτασης.
     */
    public LinkedList<SubjectRec> GetKategories() {
        LinkedList<SubjectRec> list = new LinkedList<>();
        String MyDB = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/EllakDB.sqlite";
        SQLiteDatabase sqldb = SQLiteDatabase.openDatabase(MyDB, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor cursor = sqldb.rawQuery("SELECT COUNT(*) FROM Subjects", null);
        cursor.moveToFirst();
        int x = cursor.getInt(0);
        SubjectRec[] sr = new SubjectRec[x];
        for (int j = 0; j < sr.length; j++) {
            sr[j] = new SubjectRec();
        }
        cursor = sqldb.rawQuery("SELECT * FROM Subjects", null);
        cursor.moveToFirst();
        for (int i = 0; i < sr.length; i++) {
            sr[i].SubjectID = cursor.getInt(0);
            sr[i].SubjectName = cursor.getString(1);
            list.add(sr[i]);
            cursor.moveToNext();
        }
        cursor.close();
        sqldb.close();
        return list;
    }

    /**
     * Δημιουργεί και επιστρέφει ένα ολόκληρο ερωτηματολόγιο. Οι ερωτήσεις επιλέγονται τυχαία από τις διαθέσιμες
     * υποκατηγορίες και οι διαθέσιμες απαντήσεις των ερωτήσεων τοποθετούνται με, επίσης, τυχαία σειρά.
     *
     * @param Subject Ο κωδικός του γνωστικού αντικειμένου της εξέτασης.
     * @return Το ερωτηματολόγιο ως στιγμιότυπο της κλάσης {@link TestSheet}.
     */
    public TestSheet CreateTestSheet(int Subject) {
        List<Integer> list;
        List<Integer> ansList = new LinkedList<>();
        /** Χρησιμοποιούμε λίστες για να αποτρέψουμε την random από το να παράξει το ίδιο αποτέλεσμα **/
        TestSheet ts = new TestSheet();
        ts.SubjectID = Subject;
        int count = 0;
        cursorInit(Subject);
        int[] categories = categInit();
        ts.Quests = makeQuest();
        ts.ReqCorAnswers = reqAnswers(ts.Quests);
        for (int i = 0; i < categories.length; i++) {
            int q = categories[i];
            list = getSubCateg(i);
            for (int j = 0; j < q; j++) {
                ts.Quests[count] = insertQuestions(list, ansList);
                ansList.clear();
                count++;
            }
            list.clear();
        }
        cursor = sqldb.rawQuery("SELECT STime FROM Subjects WHERE SubjectCode = " + Subject, null);
        cursor.moveToFirst();
        ts.ExamTime = cursor.getInt(0);
        cursor.close();
        catCursor.close();
        ansCursor.close();
        subCursor.close();
        sumCursor.close();
        sqldb.close();
        return ts;
    }

    /**
     * Επιστρέφει την έκδοση της τρέχουσας έκδοσης της βάσης δεδομένων.
     *
     * @return Η τρέχουσα έκδοση της βάσης δεδομένων.
     */
    public float GetVersion() {
        String MyDB = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/EllakDB.sqlite";
        SQLiteDatabase sqldb = SQLiteDatabase.openDatabase(MyDB, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor cursor = sqldb.rawQuery("SELECT * FROM Misc", null);
        cursor.moveToFirst();
        float ver = cursor.getFloat(0);
        cursor.close();
        sqldb.close();
        return ver;
    }

    private boolean CheckDB() {
        String DB_PATH = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/";
        File dbf = new File(DB_PATH + "EllakDB.sqlite");
        return dbf.exists();
    }

    private void CopyDB() {
        InputStream myInput = null;
        try {
            myInput = mcontext.getApplicationContext().getAssets().open("EllakDB.sqlite");
            CreateDirectory();
            String DB_PATH = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/";
            String outFileName = DB_PATH + "EllakDB.sqlite";
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            System.err.println("Error in copying: " + e.getMessage());
        }
    }

    private void CreateDirectory() {
        String DB_DIR = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/";
        File Dir = new File(DB_DIR);
        if (!Dir.exists())
            Dir.mkdir();
    }

    /**
     * Τοποθετεί τα δεδομένα απο τη βάση σε cursors για να χρησιμοποιηθούν στην createTestSheet.
     *
     * @param Subject ο αριθμός του γνωστικού αντικειμένου στη βάση.
     */
    private void cursorInit(int Subject) {
        String MyDB = mcontext.getApplicationContext().getFilesDir().getAbsolutePath() + "/databases/EllakDB.sqlite";
        sqldb = SQLiteDatabase.openDatabase(MyDB, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        cursor = sqldb.rawQuery("SELECT QCODE,QSUBJECT,QKATEG,QLECT,QPHOTO from questions where qsubject=" + Subject + " order by qkateg;", null);
        catCursor = sqldb.rawQuery("SELECT Kategory,Numb FROM Numbers WHERE SCode=" + Subject, null);
        sumCursor = sqldb.rawQuery("SELECT SUM(Numb) FROM Numbers WHERE SCode=" + Subject, null);
        subCursor = sqldb.rawQuery("SELECT SLect FROM Subjects WHERE SubjectCode=" + Subject, null);
    }

    /**
     * @return ο πίνακας των υποκατηγοριών και ο απαιτούμενος αριθμός ερωτήσεων για κάθε υποκατηγορία.
     */
    private int[] categInit() {
        int[] categories = new int[catCursor.getCount()];
        catCursor.moveToFirst();
        for (int i = 0; i < categories.length; i++) {
            categories[i] = catCursor.getInt(1);
            catCursor.moveToNext();
        }
        return categories;
    }

    /**
     * Αρχικοποιεί έναν πίνακα με αντικείμενα Question
     *
     * @return ο πίνακας με τα αντικείμενα τύπου Question
     */
    private Question[] makeQuest() {
        sumCursor.moveToFirst();
        Question[] Quests = new Question[sumCursor.getInt(0)];
        for (int i = 0; i < Quests.length; i++) {
            Quests[i] = new Question();
        }
        return Quests;
    }

    /**
     * Λαμβάνοντας το μήκος του πίνακα των ερωτήσεων υπολογίζει τις απαιτούμενες σωστές απαντήσεις.
     *
     * @param Quests Ο πίνακας με τις ερωτήσεις
     * @return Τον αριθμό των απαιτούμενων σωστών απαντήσεων
     */
    private int reqAnswers(Question[] Quests) {
        int ReqCorAnswers;
        subCursor.moveToFirst();
        if (subCursor.getString(0).equals("ΚΩΔΙΚΑΣ")) {
            ReqCorAnswers = Quests.length - 2;
        } else {
            ReqCorAnswers = Quests.length - 1;
        }
        return ReqCorAnswers;
    }

    /**
     * Γεμίζει μία λίστα με ερωτήσεις που ανήκουν σε μια συγκεκριμένη υποκατηγορία.
     *
     * @param i Ο αριθμός υποκατηγορίας
     * @return Την λίστα με τις ερωτήσεις μιας συγκεκριμένης υποκατηγορίας
     */
    private List<Integer> getSubCateg(int i) {
        List<Integer> list = new LinkedList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getInt(2) == i + 1) {
                list.add(cursor.getPosition());
            }
            cursor.moveToNext();
        }
        return list;
    }

    /**
     * Χρησιμοποιείται για να γεμίσει τον πίνακα των ερωτήσεων με τυχαίες ερωτήσεις
     * και τοποθετεί τις απαντήσεις επίσης με τυχαια σειρά.
     *
     * @param list    η λίστα των ερωτήσεων
     * @param ansList η λίστα των απαντήσεων
     * @return Το αντικέιμενο τύπου Question που περιέχει όλα τα δεδομένα.
     */
    private Question insertQuestions(List<Integer> list, List<Integer> ansList) {
        Question quest = new Question();
        int numb = new Random().nextInt(list.size());
        System.out.println(list.size());
        cursor.moveToPosition(list.remove(numb));
        ansCursor = sqldb.rawQuery("SELECT ALect,ACorr FROM Answers WHERE AQcod=" + cursor.getInt(0), null);
        quest.AText = new String[ansCursor.getCount()];
        quest.QNum = cursor.getInt(0);
        quest.QText = cursor.getString(3);
        if (cursor.getString(4).equals("0")) {
            quest.PicName = "-";
        } else {
            quest.PicName = cursor.getString(4) + ".jpg";
        }
        for (int k = 0; k < ansCursor.getCount(); k++) {
            ansList.add(k);
        }
        for (int k = 0; k < ansCursor.getCount(); k++) {
            int ansNumb = new Random().nextInt(ansList.size());
            ansCursor.moveToPosition(ansList.remove(ansNumb));
            quest.AText[k] = ansCursor.getString(0);
            if (ansCursor.getInt(1) == 1) {
                quest.CorAnswer = k;
            }
        }
        return quest;
    }
}


/**
 * Παριστά,ως record, ένα γνωστικό αντικείμενο εξέτασης.
 */
class SubjectRec {
    /**
     * Ο κωδικός του γνωστικού αντικειμένου εξέτασης.
     */
    public int SubjectID;
    /**
     * Το λεκτικό (όνομα) του γνωστικού αντικειμένου.
     */
    public String SubjectName;
}

/**
 * Παριστά, ως Record, μία ερώτηση του ερωτηματολογίου.
 */
class Question {
    /**
     * Ο Αύξωντας Αριθμός της Ερώτησης στο ερωτηματολόγιο
     */
    public int QNum;
    /**
     * Το κείμενο της ερώτησης
     */
    public String QText;
    /**
     * Το όνομα του αρχείου εικόνας το οποίο αντιστοιχεί στην ερώτηση ("-" αν η ερώτηση δεν έχει εικόνα).
     */
    public String PicName;
    /**
     * Πίνακας με τα κείμενα των απαντήσεων. Το μέγεθος του πίνακα δηλώνει και το πλήθος των απαντήσεων.
     */
    public String[] AText;
    /**
     * Η θέση της σωστής απάντησης στον προηγούμενο πίνακα
     */
    int CorAnswer;
}

/**
 * Παριστά, ως Record, ένα ολόκληρο ερωτηματολόγιο.
 */
class TestSheet {
    /**
     * Ο κωδικός του γνωστικού αντικειμένου του ερωτηματολογίου
     */
    public int SubjectID;
    /**
     * Ο χρόνος εξέτασης σε πρώτα λεπτά της ώρας.
     */
    public int ExamTime;
    /**
     * Πίνακας με τις ερωτήσεις του ερωτηματολογίου. Κάθε ερώτηση είναι ένα αντικείμενο της κλάσης {@link Question}
     */
    public Question[] Quests;
    /**
     * Το πλήθος των ερωτήσεων που πρέπει να απαντηθούν σωστά προκειμένου η εξέταση να θεωρηθεί επιτυχής.
     */
    int ReqCorAnswers;
}
