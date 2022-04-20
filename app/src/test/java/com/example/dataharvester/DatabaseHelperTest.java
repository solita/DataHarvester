package com.example.dataharvester;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.ar.core.Config;

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
    }

    @Test
    public void getSize() {
        assertEquals(2,database.getSize());
    }
}