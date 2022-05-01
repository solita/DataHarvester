package com.example.dataharvester;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        //creating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //file id, which file's results to display
        int id = -1;
        //read parameters, set id as received parameter if parameter isn't null
        Bundle params = getIntent().getExtras();
        if(params != null){
            id = params.getInt("id");
        }

        //get corresponding JSON from database
        DatabaseHelper db = MainActivity.databaseHelper;
        String json = "";
        if(id != -1){
            //if valid id, get JSON from database
            json = db.getJSON(id);
            //replace illegal characters in case there are any
            json.replace("“", "\"");
            json.replace("”", "\"");
            displayResults(json);
        }
        else{
            //this is just for testing purposes
            displayResults("{\"filename\":{\"name\":\"StarWars3.wav\",\"type\":\"audio\\/wav\",\"tmp_name\":\"\\/tmp\\/phpN1bvwR\",\"error\":0,\"size\":132344},\"analysis\":{\"header\":{\"chunkid\":\"RIFF\",\"chunksize\":132336,\"format\":\"WAVE\"},\"subchunk1\":{\"id\":\"fmt \",\"size\":16,\"audioformat\":1,\"numchannels\":1,\"samplerate\":22050,\"byterate\":44100,\"blockalign\":2,\"bitspersample\":16,\"extraparamsize\":0,\"extraparams\":null},\"subchunk2\":{\"id\":\"data\",\"size\":132300,\"data\":null}},\"labels\": [\"label1\", \"label2\"]}");
        }
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
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.analysis:
                break;
            case R.id.history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.help_info:
                startActivity(new Intent(this, InfoActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: get JSON from database, extract and display values from JSON
    public void displayResults(String jsonResults) {
        //filename
        TextView filenamevalueView = (TextView) findViewById(R.id.file_filename);
        TextView filetypeView = (TextView) findViewById(R.id.file_filetype);
        TextView filetempnameView = (TextView) findViewById(R.id.file_tempname);
        TextView filenameerrorView = (TextView) findViewById(R.id.file_filename_error);
        TextView filenamesizeView = (TextView) findViewById(R.id.file_filename_size);

        //header
        TextView headerView = (TextView) findViewById(R.id.file_header);
        TextView headerchunkidView = (TextView) findViewById(R.id.header_chunkid);
        TextView headerchunksizeView = (TextView) findViewById(R.id.header_chunksize);
        TextView headerformatView = (TextView) findViewById(R.id.header_format);

        //labels
        TextView labelsView = (TextView) findViewById(R.id.file_labels_values);

        //chunk 1
        TextView id1View = (TextView) findViewById(R.id.file_chunk1_id);
        TextView size1View = (TextView) findViewById(R.id.file_chunk1_size);
        TextView audioformatView = (TextView) findViewById(R.id.file_chunk1_audioformat);
        TextView channelnumView = (TextView) findViewById(R.id.file_chunk1_numchannels);
        TextView samplerateView = (TextView) findViewById(R.id.file_chunk1_samplerate);
        TextView byterateView = (TextView) findViewById(R.id.file_chunk1_byterate);
        TextView blockalignView = (TextView) findViewById(R.id.file_chunk1_blockalign);
        TextView bpsView = (TextView) findViewById(R.id.file_chunk1_bitspersample);
        TextView extraparamsizeView = (TextView) findViewById(R.id.file_chunk1_extraparamsize);
        TextView extraparamView = (TextView) findViewById(R.id.file_chunk1_extraparams);

        //chunk 2
        TextView id2View = (TextView) findViewById(R.id.file_chunk2_id);
        TextView size2View = (TextView) findViewById(R.id.file_chunk2_size);
        TextView dataView = (TextView) findViewById(R.id.file_chunk2_data);

        try{
            JSONObject obj = new JSONObject(jsonResults);
            String help;
            JSONObject subObj;
            JSONObject subSubObj;

            //filename
            help = obj.getJSONObject("filename").toString();
            subObj = new JSONObject(help);
            filenamevalueView.setText(getString(R.string.analysis_filename_value, subObj.getString("name")));
            filetypeView.setText(getString(R.string.analysis_filetype_value, subObj.getString("type")));
            filetempnameView.setText(getString(R.string.analysis_tempname_value, subObj.getString("tmp_name")));
            filenameerrorView.setText(getString(R.string.analysis_file_error, subObj.getInt("error")));
            filenamesizeView.setText(getString(R.string.analysis_file_size, subObj.getInt("size")));

            subObj = obj.getJSONObject("analysis");

            //header
            subSubObj = subObj.getJSONObject("header");
            headerchunkidView.setText(getString(R.string.analysis_chunk_id, subSubObj.getString("chunkid")));
            headerchunksizeView.setText(getString(R.string.analysis_chunk_size, subSubObj.getInt("chunksize")));
            headerformatView.setText(getString(R.string.analysis_format, subSubObj.getString("format")));

            //subchunk1
            subSubObj = subObj.getJSONObject("subchunk1");
            id1View.setText(getString(R.string.analysis_chunk1_id, subSubObj.getString("id")));
            size1View.setText(getString(R.string.analysis_chunk1_size, subSubObj.getInt("size")));
            audioformatView.setText(getString(R.string.analysis_audio_format, subSubObj.getInt("audioformat")));
            channelnumView.setText(getString(R.string.analysis_numchannels, subSubObj.getInt("numchannels")));
            samplerateView.setText(getString(R.string.analysis_samplerate, subSubObj.getInt("samplerate")));
            byterateView.setText(getString(R.string.analysis_byterate, subSubObj.getInt("byterate")));
            blockalignView.setText(getString(R.string.analysis_blockalign, subSubObj.getInt("blockalign")));
            bpsView.setText(getString(R.string.analysis_bps, subSubObj.getInt("bitspersample")));
            extraparamsizeView.setText(getString(R.string.analysis_extraparamsize, subSubObj.getInt("extraparamsize")));
            extraparamView.setText(getString(R.string.analysis_extra_params, subSubObj.getString("extraparams")));

            //subchunk2
            subSubObj = subObj.getJSONObject("subchunk2");
            id2View.setText(getString(R.string.analysis_chunk2_id, subSubObj.getString("id")));
            size2View.setText(getString(R.string.analysis_chunk2_size, subSubObj.getInt("size")));
            dataView.setText(getString(R.string.analysis_chunk2_data, subSubObj.getString("data")));

            //labels
            JSONArray labelsArray = obj.getJSONArray("labels");
            String labelsString = "";
            for(int i = 0; i<labelsArray.length(); i++){
                labelsString += labelsArray.get(i).toString();
                if(i<labelsArray.length()-1){
                    labelsString += ", ";
                }
            }
            labelsView.setText(labelsString);
        }
        catch(JSONException e){
            Log.d("Error!", e.toString());
        }
    }

}
