package com.example.dataharvester;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity implements AudioListAdapter.onItemListClick {

    private static final String TAG = "History";

    private RecyclerView audioList;
    private File[] allFiles;

    private AudioListAdapter audioListAdapter;

    private File fileToPlay = null;
    public DatabaseHelper databaseHelper = MainActivity.databaseHelper;
    public static final String EXTRA_MESSAGE = "name";

    ImageButton upAnalysis;
    boolean upload = false;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        //creating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        audioList = (RecyclerView) findViewById(R.id.audio_list_view);

        String path = this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        audioListAdapter = new AudioListAdapter(allFiles, this, this);

        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(this));
        audioList.setAdapter(audioListAdapter);

        // Set up the Upload/Analysis button.
        upAnalysis = findViewById(R.id.btn_upload_analysis);
        upAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the file is uploaded or not and based on that set the image for the button.
                if (upload){
                    upAnalysis.setImageResource(R.drawable.ic_analysis_bar);
                }
                else {
                    upAnalysis.setImageResource(R.drawable.ic_file_upload);
                }
            }
        });


    }


    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Menu item handling
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.help_info:
                //TODO: add functionality: open app help and information
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickListener(int position) {

    }

    @Override
    public void onClickListener(File file, int position) {

        //fileToPlay = file;
        //System.out.println(position);
        //fileToPlay.delete();
    }
}