package app.com.example.xenia.wordrem.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordsDBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 2;



    public WordsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_WORDS = "CREATE TABLE " + app.com.example.xenia.wordrem.data.WordsContract.WordsEntry.TABLE_NAME + " (" +

                app.com.example.xenia.wordrem.data.WordsContract.WordsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                app.com.example.xenia.wordrem.data.WordsContract.WordsEntry.COLUMN_WORD + " TEXT NOT NULL, " +
                app.com.example.xenia.wordrem.data.WordsContract.WordsEntry.COLUMN_TRANSLATION + " TEXT NOT NULL);";

        db.execSQL(SQL_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + app.com.example.xenia.wordrem.data.WordsContract.WordsEntry.TABLE_NAME);
        onCreate(db);
    }

}
