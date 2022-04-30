package com.example.dataharvester;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.ar.core.Config;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest{

    DatabaseHelper database;

    @Before
    public void setUp() {
        database = new DatabaseHelper(InstrumentationRegistry.getInstrumentation().getContext());
    }

    @Before
    public void init(){
        database.addRecording("first","firstPath","");
        database.addRecording("second","secondPath","");
        database.addLabels("Label1",1);
        database.addLabels("Label2",1);
    }

    @After
    public void finish() {
        database.close();
    }

    @Test
    public void getSize() {
        assertEquals(2,database.getSize());
    }

    @Test
    public void addAndGetSize(){
        database.addRecording("third","thirdPath","");
         assertEquals(3,database.getSize());
    }

    @Test
    public void delete(){
        database.deleteRecording(database.getID("first"));
        assertEquals(1,database.getSize());
    }

    @Test
    public void addLabels(){
        database.addLabels("Label1",2);
        assertEquals(3,database.getLabelsSize());
        assertEquals("Label1", database.getLabels(2).get(0));
    }

    @Test
    public void getIDbyName() {
        assertEquals(1,database.getID("first"));
    }


    @Test
    public void upload(){
        assertEquals(0,database.isUploaded(0));
        database.addUpload(1);
        assertEquals(1,database.isUploaded(1));
    }

    @Test
    public void AddJSON(){
        String JSON_1 = "{“filename”:{“name”:“StarWars3.wav”,“type”:“audio\\/wav”,“tmp_name”:“\\/tmp\\/phpN1bvwR”,“error”:0,“size”:132344},“analysis”:{“header”:{“chunkid”:“RIFF”,“chunksize”:132336,“format”:“WAVE”},“subchunk1”:{“id”:“fmt “,”size”:16,“audioformat”:1,“numchannels”:1,“samplerate”:22050,“byterate”:44100,“blockalign”:2,“bitspersample”:16,“extraparamsize”:0,“extraparams”:null},“subchunk2”:{“id”:“data”,“size”:132300,“data”:null}}}";
        database.addJSON(JSON_1,1);
        assertEquals(JSON_1,database.getJSON(1));
    }




}