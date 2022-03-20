package com.example.dataharvester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class LabelsActivity extends AppCompatActivity {
    EditText EditText1;
    EditText EditText2;
    EditText EditText3;
    EditText EditText4;
    int size;
    private RecyclerView audioList;
    private File[] allFiles;

    //changes name of the file from directory where Files are located
    private void changeName(File directory, String oldName, Editable newName) {
        if (directory.exists()) {
            File from = new File(directory, oldName);
            File to = new File(directory, newName + ".wav");
            if (from.exists())
                from.renameTo(to);
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labels);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String path = this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        size = allFiles.length;

        EditText1 = (EditText) findViewById(R.id.textInputEditText);
        EditText2 = (EditText) findViewById(R.id.editText);
        EditText3 = (EditText) findViewById(R.id.editText2);
        EditText4 = (EditText) findViewById(R.id.editText3);
        //takes up to last 4 files from the directory where Files are, and shows current labels
        //that can be edited
        if (size == 1) {
            EditText1.setText(allFiles[size - 1].getName());
        } else if (size == 2) {
            EditText1.setText(allFiles[size - 2].getName());
            EditText2.setText(allFiles[size - 1].getName());
        } else if (size == 3) {
            EditText1.setText(allFiles[size - 3].getName());
            EditText2.setText(allFiles[size - 2].getName());
            EditText3.setText(allFiles[size - 1].getName());
        } else if (size >= 4) {
            EditText1.setText(allFiles[size - 4].getName());
            EditText2.setText(allFiles[size - 4].getName());
            EditText3.setText(allFiles[size - 2].getName());
            EditText4.setText(allFiles[size - 1].getName());
        }

    }

    /*
    Function when pressing the check button, it will save file names from editText fields
     */
    public void submitLabels(View view) {
        //System.out.println(EditText1.getText());

        String path = this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        size = allFiles.length;
        if (size == 1) {
            changeName(directory, allFiles[size-1].getName(), EditText1.getText());
        } else if (size == 2) {
            changeName(directory, allFiles[size-2].getName(), EditText1.getText());
            changeName(directory, allFiles[size-1].getName(), EditText2.getText());
        } else if (size == 3) {
            changeName(directory, allFiles[size-3].getName(), EditText1.getText());
            changeName(directory, allFiles[size-2].getName(), EditText2.getText());
            changeName(directory, allFiles[size-1].getName(), EditText3.getText());
        } else if (size >= 4) {
            changeName(directory, allFiles[size-4].getName(), EditText1.getText());
            changeName(directory, allFiles[size-3].getName(), EditText2.getText());
            changeName(directory, allFiles[size-2].getName(), EditText3.getText());
            changeName(directory, allFiles[size-1].getName(), EditText4.getText());
        }
            EditText1.getText().clear();
            EditText2.getText().clear();
            EditText3.getText().clear();
            EditText4.getText().clear();
    }

    public void cacnelLabels(View view) {
        EditText1.getText().clear();
        EditText2.getText().clear();
        EditText3.getText().clear();
        EditText4.getText().clear();

        if (size == 1) {
            EditText1.setText(allFiles[size - 1].getName());
        } else if (size == 2) {
            EditText1.setText(allFiles[size - 2].getName());
            EditText2.setText(allFiles[size - 1].getName());
        } else if (size == 3) {
            EditText1.setText(allFiles[size - 3].getName());
            EditText2.setText(allFiles[size - 2].getName());
            EditText3.setText(allFiles[size - 1].getName());
        } else if (size >= 4) {
            EditText1.setText(allFiles[size - 4].getName());
            EditText2.setText(allFiles[size - 4].getName());
            EditText3.setText(allFiles[size - 2].getName());
            EditText4.setText(allFiles[size - 1].getName());
        }
    }

}

