package com.example.dataharvester;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import org.junit.runner.RunWith;

import org.junit.Test;

public class SettingsActivityTest {

    @Test
    public void test_areElementsInView(){

        ActivityScenario.launch(SettingsActivity.class);

        //Audio type switch elements
        onView(withId(R.id.audiotype_label)).check(matches(isDisplayed()));
        onView(withId(R.id.audiotype_switch)).check(matches(isDisplayed()));

        //API URL elements
        onView(withId(R.id.apiurl_label)).check(matches(isDisplayed()));
        onView(withId(R.id.apiurl_value)).check(matches(isDisplayed()));
        onView(withId(R.id.apiurl_button_save)).check(matches(isDisplayed()));
        onView(withId(R.id.apiurl_button_default)).check(matches(isDisplayed()));

        //Local files switch
        onView(withId(R.id.localfiles_label)).check(matches(isDisplayed()));
        onView(withId(R.id.files_setting_switch)).check(matches(isDisplayed()));

    }

    @Test
    public void test_textsAreCorrect(){
        onView(withId(R.id.audiotype_label)).check(matches(withText("Audio type")));
        onView(withId(R.id.apiurl_label)).check(matches(withText("Change API URL")));
        onView(withId(R.id.localfiles_label)).check(matches(withText("Keep local files after upload")));

        onView(withId(R.id.apiurl_button_default)).check(matches(withText("default")));
        onView(withId(R.id.apiurl_button_save)).check(matches(withText("save")));

    }

}