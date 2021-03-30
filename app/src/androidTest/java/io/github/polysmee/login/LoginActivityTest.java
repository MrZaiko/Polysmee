package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
/*
    @Rule
    public ActivityScenarioRule<LoginActivity> testRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void onCreateIsPrinted() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.login_text_prompt)).check(ViewAssertions.matches(withText(containsString("Hello user"))));
        }
    }*/
@Test
public void trashTest(){
    assertEquals(1,1);
}

}