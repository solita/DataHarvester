package com.example.dataharvester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

    public static String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECORDING = "recordings";
    private static final String TABLE_LABEL = "labels";
    private static final String TABLE_JSON = "jsons";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RECORDING_NAME = "name";
    private static final String COLUMN_RECORDING_PATH = "path";
    private static final String COLUMN_LABEL_NAME = "name";
    private static final String COLUMN_JSON = "json";
    private static final String COLUMN_UPLOADED = "uploaded";

    private static final String CREATE_TABLE_RECORDING = "CREATE TABLE "
            + TABLE_RECORDING + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_RECORDING_NAME + " TEXT,"
            + COLUMN_RECORDING_PATH + " TEXT," + COLUMN_UPLOADED + " BIT);";

    private static final String CREATE_TABLE_LABEL = "CREATE TABLE " +
            TABLE_LABEL + "(" + COLUMN_ID + " INTEGER,"
            + COLUMN_RECORDING_NAME + " TEXT );";

    private static final String CREATE_TABLE_JSONS = "CREATE TABLE " +
            TABLE_JSON + "(" + COLUMN_ID + " INTEGER,"
            + COLUMN_JSON + " LONGTEXT );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_RECORDING);
        sqLiteDatabase.execSQL(CREATE_TABLE_LABEL);
        sqLiteDatabase.execSQL(CREATE_TABLE_JSONS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_RECORDING + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_LABEL + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_JSON + "'");
        onCreate(sqLiteDatabase);

    }

    public void addRecording(String name, String path, String label) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues valuesRecording = new ContentValues();
        valuesRecording.put(COLUMN_RECORDING_NAME, name);
        valuesRecording.put(COLUMN_RECORDING_PATH, path);
        valuesRecording.put(COLUMN_UPLOADED, 0);
        sqLiteDatabase.insert(TABLE_RECORDING, null, valuesRecording);

        /*
        long id = sqLiteDatabase.insertWithOnConflict(TABLE_RECORDING, null,
                valuesRecording, SQLiteDatabase.CONFLICT_IGNORE);
        */


        /*
        ContentValues valuesLabel = new ContentValues();
        valuesLabel.put(COLUMN_ID, id);
        valuesLabel.put(COLUMN_LABEL_NAME, label);
        sqLiteDatabase.insert(TABLE_LABEL, null, valuesLabel);
         */

    }

    public void addJSON(String json, int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_JSON, json);
        sqLiteDatabase.insert(TABLE_JSON, null, values);
    }

    public String getJSON(int id) {
        String temp = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT json FROM jsons WHERE id = ?", new String[] {Integer.toString(id)});;
        //Cursor c = db.rawQuery("SELECT * FROM jsons,"null);
        //Cursor c = db.rawQuery("SELECT * FROM jsons", null);
        ;
        if (c.moveToFirst()) {
            temp = c.getString(0);
        }
        c.close();
        return temp;
    }

    public void addLabels(String label, int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues valuesLabel = new ContentValues();
        valuesLabel.put(COLUMN_ID, id);
        valuesLabel.put(COLUMN_LABEL_NAME, label);
        sqLiteDatabase.insert(TABLE_LABEL, null, valuesLabel);
    }

    //find recording id with its name
    //names should be unique, as it has date and timestamp
    public int getID(String name) {
        int temp = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT id FROM recordings WHERE name = ?", new String[]{name});
        ;
        if (c.moveToFirst()) {
            temp = Integer.parseInt(c.getString(0));
        }
        c.close();
        return temp;
    }

    //get labels for recording with id
    public List<String> getLabels(int id) {
        List<String> labels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT name FROM labels WHERE id = ?", new String[]{Integer.toString(id)});
        ;
        while (c.moveToNext()) {
            labels.add(c.getString(0));
        }

        return labels;
    }

    //change value of Uploaded bit from 0 (false) to 1 (true)
    public void addUpload(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UPLOADED, 1);
        db.update(TABLE_RECORDING, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //returns 1 if uploaded, 0 if not
    public int isUploaded(int id) {
        int temp = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT uploaded FROM recordings WHERE id = ?", new String[]{Integer.toString(id)});
        ;
        if (c.moveToFirst()) {
            temp = Integer.parseInt(c.getString(0));
        }
        c.close();
        return temp;
    }

    public void updateLabels(int id, String newLabel, String oldLabel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(COLUMN_LABEL_NAME, newLabel);
        db.update(TABLE_LABEL, valuesHobby, COLUMN_ID + " = ? AND " + COLUMN_LABEL_NAME + " = ?", new String[]{String.valueOf(id),oldLabel});

    }

    public void updateRecording(int id, String name, String label, String path) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesRecording = new ContentValues();
        valuesRecording.put(COLUMN_RECORDING_NAME, name);
        valuesRecording.put(COLUMN_RECORDING_PATH, path);
        db.update(TABLE_RECORDING, valuesRecording, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public long getLabelsSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_LABEL);
        db.close();
        return count;
    }


    public void deleteRecording(int id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deletes recording for recordings
        sqLiteDatabase.delete(TABLE_RECORDING, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        //deletes labels for recording
        sqLiteDatabase.delete(TABLE_LABEL, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});


    }
    public void deleteLabel(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_LABEL, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }


    public List<String> getNames() {
        List<String> labels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues valuesRecording = new ContentValues();
        Cursor c = db.rawQuery("SELECT name FROM recordings", null);
        ;
        while (c.moveToNext()) {
            labels.add(c.getString(0));
        }

        return labels;
    }
    //get all lables to list with id

    public int getSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        int temp = (int) DatabaseUtils.longForQuery(db, "SELECT count(id) FROM recordings", null);
        return temp;
    }

    public String getColumnNames(){
        String temp = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info(jsons)",null);
        while (c.moveToNext()) {
            temp = temp + " " + c.getString(1);
        }
        return temp;

    }
}
