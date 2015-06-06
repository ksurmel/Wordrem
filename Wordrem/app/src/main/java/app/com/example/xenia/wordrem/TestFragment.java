package app.com.example.xenia.wordrem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


import android.content.Intent;


import app.com.example.xenia.wordrem.data.WordsContract;
import app.com.example.xenia.wordrem.data.WordsDBHelper;


public class TestFragment extends Fragment {

    ArrayList<Integer> arrayList;
    int maxQuantitys, inList;

    String everythRight = "";
    final String MAXQUAN = "maxQuantity", ALL = "all", EVERYTHR = "everyth_right", BTNNAME = "btnName", ARRLIST = "arrList" , INLIST = "inlist", WRONGR = "wronright",
            INBASE = "inbase", YOURWORD = "yourword";

    int all = 0 , quantity;
    Cursor cursor;

    public static final String[] WORDS_COLUMNS_PROJECTION = {
            WordsContract.WordsEntry._ID,
            WordsContract.WordsEntry.COLUMN_WORD,
            WordsContract.WordsEntry.COLUMN_TRANSLATION
    };

    EditText wordEd;


    Button button;

    TextView wrong, inBase, yourWord, trans;
    ImageView imageview1, imageview2;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_test, container, false);

        imageview1 =(ImageView) rootView.findViewById(R.id.imageView1);
        imageview2 =(ImageView) rootView.findViewById(R.id.imageView2);
        button = (Button) rootView.findViewById(R.id.next);
        wordEd = (EditText) rootView.findViewById(R.id.worda);
        wrong = (TextView) rootView.findViewById(R.id.wrong_or_not);
        inBase = (TextView) rootView.findViewById(R.id.inBase);
        yourWord = (TextView) rootView.findViewById(R.id.yourWord);
        trans = (TextView) rootView.findViewById(R.id.trans);

        cursor = getActivity().getContentResolver().query(WordsContract.WordsEntry.CONTENT_URI, WORDS_COLUMNS_PROJECTION, null, null,
                null);

        cursor.moveToLast();
        maxQuantitys = cursor.getPosition()+1;



        quantity = maxQuantitys;

        if (savedInstanceState == null) {
            arrayList = new ArrayList<Integer>();
            for (int i = 0; i <= maxQuantitys; i++) {
                arrayList.add(quantity--);
            }
            quantity = maxQuantitys;
        }

        if (savedInstanceState != null){
            maxQuantitys = savedInstanceState.getInt(MAXQUAN);
            all = savedInstanceState.getInt(ALL);
            everythRight = savedInstanceState.getString(EVERYTHR);
            button.setText(savedInstanceState.getString(BTNNAME));
            arrayList = new ArrayList<Integer>(savedInstanceState.getIntegerArrayList(ARRLIST));
            wrong.setText(savedInstanceState.getString(WRONGR));
            yourWord.setText(savedInstanceState.getString(YOURWORD));
            inBase.setText(savedInstanceState.getString(INBASE));

            if (wrong.getText().toString().compareTo("Wrong") == 0) {


                imageview1.setVisibility(View.VISIBLE);
                imageview2.setVisibility(View.VISIBLE);
            }
        }


        if (savedInstanceState == null){ //esli ne bylo povorota
            inList = randr();
        }else {
            inList = savedInstanceState.getInt(INLIST);
        }

        cursor.moveToPosition(arrayList.get(inList)-1);

        trans.setText(cursor.getString(2));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText myEditText = (EditText) getActivity().findViewById(R.id.worda);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

                String wordE = wordEd.getText().toString();
                wordE = wordE.trim();

                switch (button.getText().toString()){
                    case "check":
                        if (wordE.compareTo(cursor.getString(1)) == 0) {
//                                wordEd.setText("");
                            wrong.setText("Right!");
                        } else {
                            inBase.setText(cursor.getString(1));
                            yourWord.setText(wordEd.getText().toString());
//                                wordEd.setText("");
                            wrong.setText("Wrong");

                            imageview1.setVisibility(View.VISIBLE);
                            imageview2.setVisibility(View.VISIBLE);

                            everythRight = " Not all of the words were right, try once again! ";
                        }

                        all++;

                        if (quantity != all) {

                            button.setText("next");

                        } else {
                            button.setText("end");
                        }
                        break;
                    case "next":

                        imageview1.setVisibility(View.INVISIBLE);
                        imageview2.setVisibility(View.INVISIBLE);
                        afterRand(inList);
                        inList = randr();
                        cursor.moveToPosition(arrayList.get(inList) - 1);
                        button.setText("check");
                        inBase.setText("");
                        yourWord.setText("");
                        wrong.setText("");
                        trans.setText(cursor.getString(2));
                        wordEd.setText("");
                        break;
                    case "end":

                        afterRand(inList);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("bool", everythRight);
//                        intent.putExtra("check", )
                        ListFragment.check = true;
                        startActivity(intent);


                        break;


                }


            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(MAXQUAN, maxQuantitys);
        outState.putInt(ALL,all);
        outState.putString(EVERYTHR, everythRight);
        outState.putString(BTNNAME, button.getText().toString());
        outState.putIntegerArrayList(ARRLIST, arrayList);
        outState.putInt(INLIST, inList);
        outState.putString(WRONGR, wrong.getText().toString());
        outState.putString(YOURWORD, yourWord.getText().toString());
        outState.putString(INBASE, inBase.getText().toString());
        super.onSaveInstanceState(outState);
    }


    private int randomWord1(){
        Random random = new Random();
        int number = random.nextInt(maxQuantitys);
        ArrayList<Integer> list = new ArrayList<Integer>(arrayList);
        arrayList.clear();
        for (int i = 0; i <= maxQuantitys; i++) {
            if (i != number)
                arrayList.add(list.get(i));
        }
        maxQuantitys--;

        return list.get(number);
    }

    private int randr(){
        Random random = new Random();
        return random.nextInt(maxQuantitys);
    }

    private void afterRand(int randNumb){
        ArrayList<Integer> list = new ArrayList<Integer>(arrayList);
        arrayList.clear();
        for (int i = 0; i <= maxQuantitys; i++) {
            if (i != randNumb)
                arrayList.add(list.get(i));
        }
        maxQuantitys--;
    }


}
