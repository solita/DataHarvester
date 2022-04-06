package com.example.dataharvester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton btnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up a record button
        btnRecord = (ImageButton) findViewById(R.id.btn_record);

        btnRecord.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(MainActivity.this, "Recording on.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Methods for app menu (Sara)
     */

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
                //TODO: add functionality: return to app home screen
                break;
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
}