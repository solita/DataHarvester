package com.example.dataharvester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity<Button> extends AppCompatActivity {

	public static final String EXTRA_MESSAGE = "name";
	public static final String DATABASE_NAME = "database";
    // for raw audio, use MediaRecorder.AudioSource.UNPROCESSED, see note in MediaRecorder section
    static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    static final int SAMPLE_RATE = 44100;
    static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    static String recordType = "Mono";
    private int channels = 1;
    private int CHANNEL_CONFIG;
    private int BUFFER_SIZE_RECORDING;

    private AudioRecord audioRecord;
    private Thread recordingThread;

    ImageButton btnRecord;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private String recordFile;
    private String recordPath;

    public static DatabaseHelper databaseHelper;

    //settings
    static boolean keepLocalFiles;
    static String apiUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up a record button
        btnRecord = (ImageButton) findViewById(R.id.btn_record);

        databaseHelper = new DatabaseHelper(this);

        btnRecord.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (isRecording) {
                    // Stop Recording
                    isRecording = false;
                    stopRecording();


                    Intent intent = (new Intent(MainActivity.this, LabelsActivity.class));
                    Bundle extras = new Bundle();
                    intent.putExtra(EXTRA_MESSAGE,recordFile);
                    MainActivity.this.startActivity(intent);
                } else {
                    // Start Recording
                    if (checkPermissions()) {
                        isRecording = true;
                        startRecording();
                        Toast.makeText(MainActivity.this, "Recording on.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void stopRecording() {
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        recordingThread = null;

        databaseHelper.addRecording(recordFile, recordPath, "");

        Toast.makeText(MainActivity.this, "Recording stopped.", Toast.LENGTH_SHORT).show();
    }

    private void startRecording() {

        recordPath = this.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();


        recordFile =formatter.format(now) + ".wav";

        String AudioName = recordPath + "/" + "Recording" + formatter.format(now) + ".pcm";
        String NewAudioName = recordPath + "/" + formatter.format(now)  + ".wav";

        Toast.makeText(this,"recordType is" + recordType, Toast.LENGTH_SHORT).show();

        setAudioRecord();

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(AudioName);
                copyWaveFile(AudioName, NewAudioName);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    @SuppressLint("MissingPermission")
    private void setAudioRecord() {

        if(recordType.equals("Mono")) {
            channels = 1;
            CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
        }
        if(recordType.equals("Stereo")) {
            channels = 2;
            CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
        }


        BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);


        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE_RECORDING);
        audioRecord.startRecording();
    }

    private void writeAudioDataToFile(String filePath) {

        // Write the output audio in byte
        byte[] data = new byte[BUFFER_SIZE_RECORDING];
        FileOutputStream outputStream = null;

        try {
            outputStream  = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            int read = audioRecord.read(data, 0, data.length);
            try {
                outputStream.write(data,0, read);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SAMPLE_RATE;
        long byteRate = 16 * SAMPLE_RATE * channels/8;
        byte[] data = new byte[BUFFER_SIZE_RECORDING];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File pcmFile = new File(inFilename);
        pcmFile.delete();
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }


    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
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

        if (isRecording) {
            isRecording = false;
            stopRecording();
        }

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