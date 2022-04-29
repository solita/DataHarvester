package com.example.dataharvester;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

public class LabelsActivityTest {

    @Test
    public void test_areElementsInView() {

        ActivityScenario.launch(SettingsActivity.class);

        //Labelling screen
        onView(withId(R.id.rectangle_6)).check(matches(isDisplayed()));
        //Title
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        //Label text boxes
        onView(withId(R.id.textInputEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.editText)).check(matches(isDisplayed()));
        onView(withId(R.id.editText2)).check(matches(isDisplayed()));
        onView(withId(R.id.editText3)).check(matches(isDisplayed()));
        //Buttons
        onView(withId(R.id.btn_exit)).check(matches(isDisplayed()));
        onView(withId(R.id.check)).check(matches(isDisplayed()));

    }

    @Test
    public void test_areTextsCorrect() {

        ActivityScenario.launch(SettingsActivity.class);

        //Title
        onView(withId(R.id.textView2)).check(matches(withText("Labels")));
        //Label Text boxes
        onView(withId(R.id.textInputEditText)).check(matches(withHint("Write label")));
        onView(withId(R.id.editText)).check(matches(withHint("Write label")));
        onView(withId(R.id.editText2)).check(matches(withHint("Write label")));
        onView(withId(R.id.editText3)).check(matches(withHint("Write label")));
        //buttons
        onView(withId(R.id.btn_exit)).check(matches(withText("Cancel")));
        onView(withId(R.id.check)).check(matches(withText("Submit")));
    }
}