package com.jaynewstrom.autoModuleSample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public final class MainActivityTest {

    @Rule public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test public void testInjectionWorks() {
        onView(withId(R.id.text_view)).check(matches(withText("It's injected")));
    }
}
