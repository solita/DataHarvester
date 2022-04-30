package com.example.dataharvester;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import com.example.dataharvester.DatabaseHelper;

import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private DatabaseHelper testdb;

    @Before
    public void init(){
        testdb = new DatabaseHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @After
    public void close(){
        testdb.close();
    }

    @Test
    public void getSize(){
        assertEquals(0, testdb.getSize());
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

}