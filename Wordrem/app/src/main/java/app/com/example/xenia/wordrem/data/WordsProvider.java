package app.com.example.xenia.wordrem.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import app.com.example.xenia.wordrem.data.WordsContract;
import app.com.example.xenia.wordrem.data.WordsDBHelper;

public class WordsProvider extends ContentProvider {

    private WordsDBHelper dbHelper;
    private static SQLiteQueryBuilder queryBuilder;
    private static final int WORDS = 10;
    private static final int WORD_ONE = 11;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final String wordByIdSelection =
            WordsContract.WordsEntry._ID + " = ?";

    @Override
    public boolean onCreate() {
        dbHelper = new WordsDBHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS, WORDS);
        matcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS + "/*", WORD_ONE);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) { //Проверяем какой uri нам дали
            case WORDS: { //для всех слов
                cursor = dbHelper.getReadableDatabase() // получаем ДБ в режиме чтения
                        .query( //выполним запрос
                                WordsContract.WordsEntry.TABLE_NAME, //имя таблицы откуда берем данные
                                projection, //проекция (какие колонки будем отбирать) (проецируем рез-тат на нужный шаблон)
                                selection, //выборка. В стиле: WHERE column = ? AND column2 = ?  <=== всё, начиная с WHERE - сама строка selection
                                selectionArgs, //аргументы выборки: массив данных, которые будут подставлены в выборку (та, что сверху, вместо вопросов)
                                //расположенных в НУЖНОМ ПОРЯДКЕ
                                null,           //группируем по
                                null,           // ...
                                sortOrder       // порядок выборки, например ASC (по возрастанию)
                        ); // всё это компонуется в одну строку - sql-запрос и отправляется базе
                break;
            }
            case WORD_ONE : { //для одного слова
                cursor = getWordById(uri, projection, sortOrder);
            }
            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri); //возвращаемому курсору присваиваем
        // uri адрес, по которому можно получить изменения (или делаем привязку: этот курсор <=> этот URI)
        return cursor;
    }

    public Cursor getWordById(Uri uri, String[] projection, String sortOrder) {

        long id = WordsContract.getWordIdFromUri(uri);
        String[] selectionArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = dbHelper.getReadableDatabase()
                .query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        wordByIdSelection, //смотри наш селектор
                        selectionArgs, //собственно, аргументы. У нас он один
                        null,
                        null,
                        sortOrder)
                ;
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Тип ContentValues это контейнер данных: ключ -> значение
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WORDS: {
                long _id = db.insert(WordsContract.WordsEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 ) {
                    returnUri = WordsContract.WordsEntry.buildWordUri(_id); //создаём uri для только что вставленного слова
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri; // возвращаем uri
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        final long wordId = WordsContract.getWordIdFromUri(uri);
        final int match = uriMatcher.match(uri);
        int deleted = 0; //счетчик удаленных записей

        selection = selection == null ? "1" : null; //если селектор пуст, то мы устанавливаем его равным "1"


        // чтобы удалить все записи

        switch (match) {
            case WORDS: {
                deleted = writableDatabase.delete(
                        WordsContract.WordsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case WORD_ONE: {
                deleted = writableDatabase.delete(
                        WordsContract.WordsEntry.TABLE_NAME,
                        wordByIdSelection,
                        new String[] {
                                String.valueOf(wordId)
                        }
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }
        if (deleted != 0) { //Если что-то удалено
            getContext()
                    .getContentResolver()
                    .notifyChange(uri, null); //отправляем сообщение о том, что что-то изменилось
        }

        return deleted; //вернём счётчик
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int updated = 0;

        selection = selection == null ? "1" : null;

        switch (match) {
            case WORDS: {
                updated = writableDatabase.update(WordsContract.WordsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }
        if (updated != 0){
            getContext()
                    .getContentResolver()
                    .notifyChange(uri, null);
        }

        return updated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) { // вставку "сразу кучей" мы не поддерживаем
        throw new UnsupportedOperationException("unsupported operation found for uri:  " + uri);
    }
}