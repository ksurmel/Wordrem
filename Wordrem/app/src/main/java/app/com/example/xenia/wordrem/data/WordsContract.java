package app.com.example.xenia.wordrem.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class WordsContract {

    public static final String CONTENT_AUTHORITY = "app.com.example.xenia.wordrem";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WORDS = "words";

    public static final class WordsEntry implements BaseColumns {

        public static final String TABLE_NAME = "words";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSLATION = "translation";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static Uri buildWordUri(long id) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            return uri;
        }


    }

    public static long getWordIdFromUri(Uri uri) {
        return ContentUris.parseId(uri); //Отделяем id от адреса
    }


}