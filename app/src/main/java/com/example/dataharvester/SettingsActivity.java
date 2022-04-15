package com.example.dataharvester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //shared preferences file for saving user preferences
    private final String sharedPrefFile = "com.example.dataharvester";
    private SharedPreferences preferences;

    //for audio type
    static int position = 0;
    private String recordtype;
    private Spinner spinner;
    private final String KEY_AUDIOTYPE = "audioType";

    //for apiurl
    private EditText apiUrlField;
    private TextView currentlyUsing;
    private String currentApiUrl;
    private Button saveButton;
    private final String KEY_APIURL = "apiUrl";

    //for local file saving
    private boolean keepLocalFiles;
    private Switch localFilesSaveSwitch;
    private final String KEY_LOCALFILES = "keepLocalFiles";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //creating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //get setting values; default values if none present
        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        loadSettings();

        //creating spinner menu for audio types
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.recordTypes, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(position);

        //creating text field for url input
        apiUrlField = (EditText) findViewById(R.id.apiurl_value);
        currentlyUsing = (TextView) findViewById(R.id.current_apiurl);
        currentlyUsing.setText(getString(R.string.current_api_url_value, currentApiUrl));

        //creating button for saving new url value
        saveButton = (Button) findViewById(R.id.apiurl_button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentApiUrl = apiUrlField.getText().toString();
                MainActivity.apiUrl = currentApiUrl;
                currentlyUsing.setText(getString(R.string.current_api_url_value, currentApiUrl));
                saveSettings();
            }
        });

        //creating setting for switch
        //on: keep local files after download
        //off: don't
        localFilesSaveSwitch = (Switch) findViewById(R.id.files_setting_switch);
        localFilesSaveSwitch.setChecked(keepLocalFiles);
        localFilesSaveSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keepLocalFiles = localFilesSaveSwitch.isChecked();
                localFilesSaveSwitch.setChecked(keepLocalFiles);
                MainActivity.keepLocalFiles = keepLocalFiles;
                saveSettings();
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
                return true;
            case R.id.help_info:
                //TODO: add functionality: open app help and information
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        recordtype = adapterView.getItemAtPosition(i).toString();
        MainActivity.recordType = recordtype;
        position = i;
        saveSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //save settings
    public void saveSettings(){
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString(KEY_AUDIOTYPE, recordtype);
        prefEditor.putString(KEY_APIURL, currentApiUrl);
        prefEditor.putBoolean(KEY_LOCALFILES, keepLocalFiles);
        prefEditor.apply();
    }

    //load settings, default values if none present
    public void loadSettings(){
        recordtype = preferences.getString(KEY_AUDIOTYPE, "Mono");
        MainActivity.recordType = recordtype;
        currentApiUrl = preferences.getString(KEY_APIURL, "placeholder");
        MainActivity.apiUrl = currentApiUrl;
        keepLocalFiles = preferences.getBoolean(KEY_LOCALFILES, false);
        MainActivity.keepLocalFiles = keepLocalFiles;
    }

    //save settings on pause
    @Override
    public void onPause(){
        super.onPause();
        saveSettings();
    }

    //reload saved settings on resume
    @Override
    public void onResume(){
        super.onResume();
        loadSettings();
    }

}
