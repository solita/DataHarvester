package com.example.dataharvester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

    public static String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECORDING = "recordings";
    private static final String TABLE_LABEL = "labels";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RECORDING_NAME = "name";
    private static final String COLUMN_RECORDING_PATH = "path";
    private static final String COLUMN_LABEL_NAME = "name";


    private static final String CREATE_TABLE_RECORDING = "CREATE TABLE "
            + TABLE_RECORDING + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_RECORDING_NAME + " TEXT,"
            + COLUMN_RECORDING_PATH + " TEXT);";

    private static final String CREATE_TABLE_LABEL = "CREATE TABLE "
            + TABLE_LABEL + "(" + COLUMN_ID + " INTEGER,"+ COLUMN_RECORDING_NAME + " TEXT );";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_RECORDING);
        sqLiteDatabase.execSQL(CREATE_TABLE_LABEL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_RECORDING + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_LABEL + "'");
        onCreate(sqLiteDatabase);

    }

    public void addRecording(String name, String path, String label) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues valuesRecording = new ContentValues();
        valuesRecording.put(COLUMN_RECORDING_NAME, name);
        valuesRecording.put(COLUMN_RECORDING_PATH, path);
        long id = sqLiteDatabase.insertWithOnConflict(TABLE_RECORDING, null,
                valuesRecording, SQLiteDatabase.CONFLICT_IGNORE);

        /*
        ContentValues valuesLabel = new ContentValues();
        valuesLabel.put(COLUMN_ID, id);
        valuesLabel.put(COLUMN_LABEL_NAME, label);
        sqLiteDatabase.insert(TABLE_LABEL, null, valuesLabel);
         */

    }

    public void addLabels(String label, int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues valuesRecording = new ContentValues();

        ContentValues valuesLabel = new ContentValues();
        valuesLabel.put(COLUMN_ID, id);
        valuesLabel.put(COLUMN_LABEL_NAME, label);
        sqLiteDatabase.insert(TABLE_LABEL, null, valuesLabel);
    }

    //find recording id with its name
    //names should be unique, as it has date and timestamp
    public int getID(String name){
        int temp = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT id FROM recordings WHERE name = ?", new String[] {name});;
        if (c.moveToFirst()) {
            temp = Integer.parseInt(c.getString(0));
        }
        c.close();
        return temp;
    }

    //get labels for recording with id
    public List<String> getLabels(int id){
        List<String> labels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT name FROM labels WHERE id = ?", new String[] {Integer.toString(id) });;
        while (c.moveToNext()) {
            labels.add(c.getString(0));
        }

        return labels;
    }

    public void updateLabels(int id, String label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesRecording = new ContentValues();

        valuesRecording.put(COLUMN_ID, id);
        valuesRecording.put(COLUMN_LABEL_NAME, label);

        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(COLUMN_LABEL_NAME, label);
        db.update(TABLE_LABEL, valuesHobby, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public void updateRecording(int id, String name, String label, String path){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesRecording = new ContentValues();
        valuesRecording.put(COLUMN_RECORDING_NAME, name);
        valuesRecording.put(COLUMN_RECORDING_PATH, path);
        db.update(TABLE_RECORDING, valuesRecording, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});


        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(COLUMN_LABEL_NAME, label);
        db.update(TABLE_LABEL, valuesHobby, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

    }
    public long getLabelsSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db,TABLE_LABEL);
        db.close();
        return count;
    }


    public void deleteRecording(int id){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deletes recording for recordings
        sqLiteDatabase.delete(TABLE_RECORDING, COLUMN_ID + " = ?",new String[]{String.valueOf(id)});
        //deletes labels for recording
        sqLiteDatabase.delete(TABLE_LABEL, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }



    public List<String> getNames(){
        List<String> labels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT name FROM recordings", null);;
        while (c.moveToNext()) {
            labels.add(c.getString(0));
        }

        return labels;
    }
    //get all lables to list with id

    public int getSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        int temp = (int) DatabaseUtils.longForQuery(db,"SELECT count(id) FROM recordings",null);
        return temp;
    }

    //this was to check that labels get added to the table

}
