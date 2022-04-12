package com.example.dataharvester;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import org.junit.runner.RunWith;

import org.junit.Test;

public class MainActivityTest {

    @Test
    public void test_isRecorderInView(){

         ActivityScenario.launch(MainActivity.class);

         onView(withId(R.id.btn_record)).check(matches(isDisplayed()));
    }

    @Test
    public void test_isToolbarInView(){

        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

}