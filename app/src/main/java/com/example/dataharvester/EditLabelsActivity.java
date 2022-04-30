package com.example.dataharvester;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EditLabelsActivity extends AppCompatActivity {
    EditText EditText1;
    EditText EditText2;
    EditText EditText3;
    EditText EditText4;
    String text1;
    String text2;
    String text3;
    String text4;
    String fileName;
    String oldText1;
    String oldText2;
    String oldText3;
    String oldText4;


    int size;
    private RecyclerView audioList;
    private File[] allFiles;
    private boolean editing;

    private List<String> allLabels;


    public DatabaseHelper databaseHelper = MainActivity.databaseHelper;

    //changes name of the file from directory where Files are located
    /*
    private void changeName(File directory, String oldName, Editable newName) {
        if (directory.exists()) {
            File from = new File(directory, oldName);
            File to = new File(directory, newName + ".wav");
            if (from.exists())
                from.renameTo(to);
        }
    }
    */

    private void addLabels( int id, List<String> list){
        for (String i : list) {
            if (!i.isEmpty()){
                databaseHelper.addLabels(i,id);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labels);
        Intent intent = getIntent();
        fileName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //System.out.println(fileName);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allLabels = databaseHelper.getLabels(databaseHelper.getID(fileName));

        String path = this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        size = allLabels.size();

        EditText1 = (EditText) findViewById(R.id.textInputEditText);
        EditText2 = (EditText) findViewById(R.id.editText);
        EditText3 = (EditText) findViewById(R.id.editText2);
        EditText4 = (EditText) findViewById(R.id.editText3);

        if(size == 1) {
            EditText1.setText(allLabels.get(0));
            oldText1 = EditText1.getText().toString();
        } else  if ( size == 2){
            EditText1.setText(allLabels.get(0));
            oldText1 = EditText1.getText().toString();
            EditText2.setText(allLabels.get(1));
            oldText2 = EditText2.getText().toString();
        } else if (size == 3) {
            EditText1.setText(allLabels.get(0));
            oldText1 = EditText1.getText().toString();
            EditText2.setText(allLabels.get(1));
            oldText2 = EditText2.getText().toString();
            EditText3.setText(allLabels.get(2));
            oldText3 = EditText3.getText().toString();
        } else if (size == 4) {
            EditText1.setText(allLabels.get(0));
            oldText1 = EditText1.getText().toString();
            EditText2.setText(allLabels.get(1));
            oldText2 = EditText2.getText().toString();
            EditText3.setText(allLabels.get(2));
            oldText3 = EditText3.getText().toString();
            EditText4.setText(allLabels.get(3));
            oldText4 = EditText4.getText().toString();
        }

    }

    /*
    Function when pressing the check button, it will save file names from editText fields
     */
    public void submitLabels(View view) {

        text1 = EditText1.getText().toString();
        text2 = EditText2.getText().toString();
        text3 = EditText3.getText().toString();
        text4 = EditText4.getText().toString();

        List<String> texts = new ArrayList<>();
        Collections.addAll(texts,text1,text2,text3,text4);
        databaseHelper.deleteLabel(databaseHelper.getID(fileName));
        addLabels(databaseHelper.getID(fileName),texts);

        //System.out.println(databaseHelper.getID(fileName));
        //System.out.println(databaseHelper.getLabels(databaseHelper.getID(fileName)));
        Intent intent = (new Intent(EditLabelsActivity.this, HistoryActivity.class));
        EditLabelsActivity.this.startActivity(intent);
        finish();
    }

    public void cancelLabels(View view) {

        Intent intent = (new Intent(EditLabelsActivity.this, HistoryActivity.class));
        EditLabelsActivity.this.startActivity(intent);
        finish();
    }

}

