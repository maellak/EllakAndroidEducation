package org.teiath.ellak.ellakandroideducation;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Η κλάση ManageQuestions έχει σκοπό να βοηθήσει στην καλύτερη διαχείριση των ερωτήσεων και τη μείωση της πολυπλοκότητας της MainScreen activity .
 * Αναλαμβάνει αποκλειστικά  την διαχείριση των ερωτήσεων και επιτρέπει στη MainScreen activity να ασχολείται μόνο με την εμφάνιση της διεπαφής .
 * Σχεδιάστηκε για να σπάσει την υλοποίηση (του κομματιού της ομάδας μας) σε 2 κομμάτια ούτως ώστε να είναι ο κώδικας πιο κατανοητός πιο διαχειρίσιμος
 * και πιο εύκολα μελλοντικά επεκτάσιμος ...
 */
public class ManageQuestions {
	/**
	 * Τύπος για τη κατηγορία του Test .
	 */
	public enum TestType {
		EDUCATION, EXAM ;
	}
	/**
	 * Κρατάει τις απαντήσεις του χρήστη στις ερωτήσεις .
	 */
	private int[] userAnswerIndexs  ;
	/**
	 * Οι εικόνες των ερωτήσεων .
	 */
	private Bitmap[] questionImages ;
	/**
	 * Το σύνολο των ερωτήσεων .
	 */
	public int allTotalQuestions ;
	/**
	 * Ο δείκτης της τρέχων ερώτησης .
	 */
	private int index  ;
	/**
	 * Κρατάει πόσες ερωτήσεις απομένουν για να τελειώσει το test .
	 */
	private int remaining ;
	/**
	 * Κρατάει το τύπο του test .
	 */
	private TestType currentType ;
	/**
	 * Κρατάει ένα Context με απώτερο σκοπό να "βλέπει" η κλάση τα Assets .
	 * Δίνετε στη κλάση στην δημιουργία της (Constactor) .
	 */
	private Context myParentContext ;
	/**
	 * Έχει όλη την πληροφορία των ερωτήσεων του Test που έρχονται από τη βάση δεδομένων .
	 */
	private TestSheet tsTest ;
	/**
	 * Έχει σαν τιμή τα δευτερόλεπτα που κρατάει το test .
	 */
	private int secondsForTest ;
	/**
	 * Έχει σαν τιμή τα δευτερόλεπτα που χρειάζονται για να ολοκληρωθεί το 75% του χρόνου που έχει το test .
	 */
	private int seventyFivePercentOfsecondsForTest ;

	/**
	 * Ο κατασκευαστής του αντικειμένου . Χρησιμοποιείται για την αρχικοποίηση του αντικειμένου προκειμένου να είναι
	 * έτοιμο για να διαχειριστούμε τις ερωτήσεις ...
	 * @param _myParentContext Παίρνει ένα context με απώτερο σκοπό να "βλέπει" η κλάση τα Assets.
	 * @param testType Ο κωδικός του τύπου για το test .
	 */
	public ManageQuestions(Context _myParentContext, String testType, String subject)
	{
		myParentContext = _myParentContext ;

		int intTestType ;
		if (isNumeric(testType))
			intTestType = Integer.parseInt(testType) ;
		else
			intTestType =  new Random().nextInt(2) ;
		int intSubject ;
		if (isNumeric(subject))
			intSubject = Integer.parseInt(subject) ;
		else
			intSubject = new Random().nextInt(4)+1 ;

		tsTest = DBHandler.getInstance(myParentContext).CreateTestSheet(intSubject);

		//System.out.println("ReqCorAnswers" + tsTest.ReqCorAnswers) ;
		//System.out.println("Quest lenght" + tsTest.Quests.length) ;
		//System.out.println("Test type" + testType) ;
		//System.out.println("Subject" + testType) ;

		allTotalQuestions = tsTest.Quests.length ;
		remaining = allTotalQuestions ;

		if (intTestType == 0)
			currentType = TestType.EDUCATION ;
		else
		{
			currentType = TestType.EXAM ;
			secondsForTest = tsTest.ExamTime * 60 ;
			seventyFivePercentOfsecondsForTest = (int)((secondsForTest) * 0.75)  ;
		}

		fillImagesAndAnswersArray(tsTest) ;
		index = 0 ;
	}

	/**
	 * Πρακτικά , αλλάζει την τρέχουσα ερώτηση με την επόμενη μη απαντημένη ... (Ουσιαστικά αλλάζει ο δείκτης της τρέχουσας ερώτησης)
	 * @return Επιστρέφει true αν άλλαξε η τρέχουσα ερώτηση με την αμέσως μη απαντημένη ερώτηση. Ή false αν δεν άλλαξε η τρέχουσα ερώτηση (Είναι όλες οι ερωτήσεις απαντημένες)
	 */
	public boolean next()
	{
		if (remaining == 0)
			return false ;

		do
		{
			if ( ++index >= allTotalQuestions)
				index = 0 ;
		} while(userAnswerIndexs[index] != -1) ;

		return true ;
	}
	/**
	 * Καταχωρείτε η απάντηση του χρήστη στην τρέχουσα ερώτηση.
	 * @param answerIndex Η απάντηση του χρήστη (Ο δείκτης από το πίνακα των απαντήσεων της τρέχουσας ερώτησης) .
	 */
	public void setUserResponseToCurQuestion(int answerIndex) //-1 for Cancel
	{
		if (answerIndex != -1)
			remaining -=  userAnswerIndexs[index] == -1 ? 1 : 0 ;
		else
			remaining -=  userAnswerIndexs[index]  == -1 ? 0 : -1 ;

		userAnswerIndexs[index] = answerIndex;
	}


	/**
	 * Επιστρέφει To μέγιστο πλήθος απαντήσεων από όλες τις ερωτήσεις του test
	 * @return To μέγιστο πλήθος απαντήσεων .
	 */
	public int getMaxAnswers()
	{
		int max = 0 ;
		for (Question quest : tsTest.Quests)
			if (quest.AText.length > max)
				max = quest.AText.length ;

		return max ;
	}
	/**
	 * Επιστρέφει την απάντηση του χρήστη για την τρέχουσας ερώτησης .
	 * @return απάντηση του χρήστη (Ο δείκτης από το πίνακα των απαντήσεων της τρέχουσας ερώτησης).
	 */
	public int getUserResponseIndexToCurQuestion()
	{
		return userAnswerIndexs[index];
	}
	/**
	 * Επιστρέφει τον αριθμό της τρέχουσας ερώτησης.
	 * @return Αριθμός τρέχων ερώτησης
	 */
	public int getCurQuestionNumber()
	{
		return (index+1) ;
	}
	/**
	 * Επιστρέφει της εικόνα της ερώτησης .
	 * @return Εικόνα {@link Bitmap} η null αν δεν έχει εικόνα η τρέχων ερώτηση .
	 */
	public Bitmap getCurQuestionImage()
	{
		return questionImages[index] ;
	}
	/**
	 * Επιστρέφει το λεκτικό της ερώτησης .
	 * @return Λεκτικό της τρέχων ερώτησης .
	 */
	public String getCurQuestionText()
	{
		return tsTest.Quests[index].QText ;
	}
	/**
	 * Επιστρέφει το μέγιστο χρόνο σε λεπτά για τη διάρκεια του test (Exam) .
	 * @return Διαθέσιμα λεπτά για το test.
	 */
	public int getExamTime()
	{
		return tsTest.ExamTime ;
	}
	/**
	 * Επιστρέφει τα λεκτικά των απαντήσεων .
	 * @return Tα λεκτικά των απαντήσεων της τρέχων ερώτησης.
	 */
	public String[] getCurAnswerTexts()
	{
		return tsTest.Quests[index].AText ;
	}
	/**
	 * Επιστρέφει την σωστή απάντηση .
	 * @return Η σωστή απάντηση για την τρέχων ερώτηση (Ο δείκτης από το πίνακα των απαντήσεων της τρέχουσας ερώτησης).
	 */
	public int getCurCorrectAnswer()
	{
		return tsTest.Quests[index].CorAnswer ;
	}
	/**
	 * Επιστρέφει το μέγιστο χρόνο σε δευτερόλεπτα για τη διάρκεια του test (Exam) .
	 * @return Διαθέσιμα δευτερόλεπτα για το test.
	 */
	public int getSecondsForTest()
	{
		return secondsForTest ;
	}
	/**
	 * Επιστρέφει το χρόνο σε δευτερόλεπτα για την ολοκλήρωση του 75% του test (Exam) .
	 * @return Δευτερόλεπτα για την ολοκλήρωση του 75% του test.
	 */
	public int getSeventyFivePercentOfsecondsForTest()
	{
		return seventyFivePercentOfsecondsForTest ;
	}
	/**
	 * Επιστρέφει το πλήθος όλων των ερωτήσεων .
	 * @return Σύνολο ερωτήσεων .
	 */
	public int getAllTotalQuestions()
	{
		return allTotalQuestions;
	}
	/**
	 * Επιστρέφει την κατηγορία του Test .
	 * @return Κατηγορία του Test .
	 */
	public TestType getCurrentType()
	{
		return currentType ;
	}
	/**
	 * Επιστρέφει ένα λεκτικό με το "score" του χρήστη example : "(3/7)" σωστές από τις συνολικά μέχρι στιγμής απαντημένες ερωτήσεις.
	 * @return Το "score" του χρήστη .
	 */
	public String getScoreEducation()
	{
		int right = 0 ;
		for (int i = 0 ;i<allTotalQuestions;i++)
		{
			if (tsTest.Quests[i].CorAnswer == userAnswerIndexs[i])
				right++ ;

		}
		return ("(" + (right) + "/" + (allTotalQuestions - remaining) + ")") ;
	}

	/**
	 * Επιστρέφει ένα {@link Bundle} με τις πληροφορίες που χρειάζονται για τα στατιστικά αποτελέσματα του test .
	 * @return Τα στατιστικά αποτελέσματα του test.
	 */
	public Bundle getBundleOfResults()
	{
		Bundle bu = new Bundle() ;
		bu.putString("Mode",(currentType.name().toString())) ;
		bu.putString("Subject", String.valueOf(tsTest.SubjectID));
		bu.putString("Pretime", String.valueOf(tsTest.ExamTime));
		bu.putString("Squestions", String.valueOf(allTotalQuestions));

		int allAnsQuests, rightQuests, mistakeQuests ;
		allAnsQuests = rightQuests = mistakeQuests = 0 ;

		for (int i = 0 ;i<allTotalQuestions;i++)
			if (userAnswerIndexs[i] != -1)
				if (userAnswerIndexs[i] == tsTest.Quests[i].CorAnswer)
					rightQuests++ ;
				else
					mistakeQuests++ ;

		bu.putString("Aquestions", String.valueOf(allTotalQuestions - remaining));
		bu.putString("Rquestions", String.valueOf(rightQuests));
		bu.putString("Fquestions", String.valueOf(mistakeQuests));

		return bu ;
	}

	/** Επιστρέφει true αν έχουν απαντηθεί όλες οι ερωτήσεις ή false αν δεν έχουν απαντηθεί όλες
	 * @return Επιστρέφει μια μεταβλητή τύπου {@link Boolean}.
	 */
	public boolean haveFinishedQuestions()
	{
		if (remaining == 0)
			return true ;
		else
			return false ;
	}

	/**
	 * Γεμίζει-αρχικοποιεί τους πίνακες των εικόνων και απαντήσεων του χρήστη .
	 * @param tsTmp Ένα αντικείμενο τύπου {@link TestSheet} για να πάρει τα path names των εικόνων και το πλήθος των ερωτήσεων .
	 */
	private void fillImagesAndAnswersArray(TestSheet tsTmp)
	{
		userAnswerIndexs = new int[allTotalQuestions] ;
		questionImages = new Bitmap[allTotalQuestions] ;
		Random random = new Random();
		for (int i = 0 ; i< allTotalQuestions; i++)
		{
			userAnswerIndexs[i] = -1 ;
			questionImages[i] = getBitmapFromAsset(myParentContext,tsTmp.Quests[i].PicName);
		}
		//Λογικά θα αλλάξει η συνάρτηση "getBitmapFromAsset" για να επιστρέφει την εικόνα από το δίσκο και όχι από τα Assets .
	}

	/**
	 *  Επιστρέφει  μια εικόνα bitmap από τα Assets .
	 *   @param context  Το context που έχει τα Assets.
	 *   @param filePath Το μονοπάτι του αρχείου στα Assets.
	 * 	 @return Ένα αντικείμενο μιας εικόνας τύπου {@link Bitmap}.
	 */
	private static Bitmap getBitmapFromAsset(Context context, String filePath) {

		Bitmap bitmap = null;

		String path = "/" ;
		File imgFile = new File(path + filePath);

		if(imgFile.exists()) {
			bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}
		else
		{
			AssetManager assetManager = context.getAssets();


			try {
				InputStream istr = assetManager.open(filePath);
				bitmap = BitmapFactory.decodeStream(istr);
			} catch (IOException e) {
				// handle exception
			}
		}


		return bitmap;
	}
	public static boolean isNumeric(String str)
	{
		try
		{
			double d = Integer.parseInt(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}

