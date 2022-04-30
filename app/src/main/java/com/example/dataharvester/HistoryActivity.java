package com.example.dataharvester;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryActivity extends AppCompatActivity implements AudioListAdapter.onItemListClick {

    private RecyclerView audioList;
    private File[] allFiles;

    private AudioListAdapter audioListAdapter;

    private File fileToPlay = null;
    public DatabaseHelper databaseHelper = MainActivity.databaseHelper;
    public static final String EXTRA_MESSAGE = "name";

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


        File recordingFile = file;
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), recordingFile);
        MultipartBody.Part partAudio = MultipartBody.Part.createFormData("filename", recordingFile.getName(), reqBody);
        UploadApis api = NetworkClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadFiles(partAudio);

        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {

                        jsonObject = new JSONObject(response.body().string());
                        //String s = jsonObject.toString();

                        System.out.println(jsonObject);
                        //System.out.println(jsonObject.getString("filename"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    uploadLabel(file);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                System.out.println("failure");
            }
        });
    }

    private void uploadLabel(File file) {

        String fileName = file.getName();
        String[] labelList = {"text1", "text2"};
        String allLabel = "";
        for(int i = 0; i < labelList.length; ++i) {
            allLabel += labelList[i];

            if (i < labelList.length - 1) {
                allLabel += ",";
            }
        }

        UploadApis api = NetworkClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadLabel(fileName, allLabel);

        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(HistoryActivity.this, "Upload succeed", Toast.LENGTH_SHORT).show();
                    System.out.println("label uploaded");
                    System.out.println(response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                System.out.println("failure");
            }
        });
    }
}