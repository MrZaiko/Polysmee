package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import io.github.polysmee.interfaces.User;
import io.github.polysmee.room.RoomActivityInfo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;

@RunWith(JUnit4.class)
public class roomActivityInfoTest {
    @Test
    public void appointmentShouldBeDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        long startTime = 1212121;
        long duration = 75787;
        String course = "A course";
        String title = "A title";
        Set<User> participants = new HashSet<>();

        Serializable expectedAppointment = new TestAppointment(startTime, duration, course, title, participants);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, expectedAppointment);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            assertDisplayed(course);
            assertDisplayed(title);
            assertDisplayed(new Date(startTime).toString());
        }
    }
}
