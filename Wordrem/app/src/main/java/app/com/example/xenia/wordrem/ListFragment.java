package app.com.example.xenia.wordrem;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import app.com.example.xenia.wordrem.R;
import app.com.example.xenia.wordrem.TestActivity;
import app.com.example.xenia.wordrem.data.WordsContract;
import app.com.example.xenia.wordrem.data.WordsDBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LISTVIEW = "listv";

    Button btnAdd, btnTrain;
    EditText etWord, etTranslation;

    public static boolean check = false;
    private WordsDBHelper dbHelper;
    private ListView listView;
    private Cursor cursor;
    private AddBtnClickListener addBtnClickListener = null;
    private final int WORDS_LOADER = 1;
    private WordsAdapter wordsAdapter;


    public static final String[] WORDS_COLUMNS_PROJECTION = {
            WordsContract.WordsEntry._ID,
            WordsContract.WordsEntry.COLUMN_WORD,
            WordsContract.WordsEntry.COLUMN_TRANSLATION
    };

    public static final int COLUMN_WORD = 1;
    public static final int COLUMN_TRANSLATION = 2;

    public static final String ORDER = WordsContract.WordsEntry._ID + " DESC";

    int d;

    protected static final int OPTION_DELETE = 2;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        etWord = (EditText) rootView.findViewById(R.id.etWord); //для получения данных с полей
        etTranslation = (EditText) rootView.findViewById(R.id.etTranslation);
        listView = (ListView) rootView.findViewById(R.id.listView);

        final ListView lv = getListView();
        registerForContextMenu(lv);

        displayListView(); //создаем ListView

        if (savedInstanceState != null) {
            listView.scrollListBy(savedInstanceState.getInt(LISTVIEW));
        }

        btnAdd = (Button) rootView.findViewById(R.id.btnAdd); //экземпляр класса Button
        btnTrain = (Button) rootView.findViewById(R.id.btnTrain); //экземпляр класса Button
        if (addBtnClickListener == null) {
            addBtnClickListener = new AddBtnClickListener();
        }
        btnAdd.setOnClickListener(addBtnClickListener);

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
            }
        });



        if (check){
            if (getActivity().getIntent().getStringExtra("bool").compareTo(" Not all of the words were right, try once again! ") == 0){
                check = false;
                Toast.makeText(getActivity(), getActivity().getIntent().getStringExtra("bool"), Toast.LENGTH_LONG).show();

            }
            else {
                Toast.makeText(getActivity(), "Good job, now you can delete old and add some new words!", Toast.LENGTH_LONG).show();
                    check = false;

            }

        }


        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(WORDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void displayListView() {
        wordsAdapter = new WordsAdapter(getActivity(), cursor, 0);
        listView.setAdapter(wordsAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Add all the menu options
        menu.add(Menu.NONE, OPTION_DELETE, 1, "Delete");
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get extra info about list item that was long-pressed
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        getActivity()
                .getContentResolver()
                .delete(
                        WordsContract.WordsEntry.buildWordUri((Long) menuInfo.id),
                        null,
                        null
                );
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(LISTVIEW, listView.getScrollY());
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                WordsContract.WordsEntry.CONTENT_URI,
                WORDS_COLUMNS_PROJECTION,
                null,
                null,
                ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        if (data != null) {
            wordsAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        wordsAdapter.swapCursor(null);
    }

    public class WordsAdapter extends CursorAdapter {

        public WordsAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        // Создаёт и вовзвращает новый ЭЛЕМЕНТ СПИСКА
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_layout, parent, false);
            return view;
        }

        // Вставляет данные в только что созданный элемент
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView worde = (TextView) view.findViewById(R.id.word);
            worde.setText(cursor.getString(COLUMN_WORD)); // Зададим константы для номеров столбцов - мы их точно знаем

            TextView transe = (TextView) view.findViewById(R.id.translation);
            transe.setText(cursor.getString(COLUMN_TRANSLATION)); // то же самое
        }

    }

    private class AddBtnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            // создаем объект для данных
            ContentValues cv = new ContentValues();

            // получаем данные из полей ввода
            String word = etWord.getText().toString();
            String translation = etTranslation.getText().toString();


            word = word.trim();


            EditText myEditText = (EditText) getActivity().findViewById(R.id.etTranslation);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);


            if (word.isEmpty() || translation.isEmpty()) {
                Toast.makeText(getActivity(), "You should enter the word and the translation.", Toast.LENGTH_LONG).show();
            } else {
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(WordsContract.WordsEntry.COLUMN_WORD, word); //помещаем в базу данных связку ключ-значение
                cv.put(WordsContract.WordsEntry.COLUMN_TRANSLATION, translation);
                // вставляем запись
                getActivity().getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, cv);
                etWord.setText(null); //чтобы текст стирался после набора
                etTranslation.setText(null);
            }
        }
    }
}