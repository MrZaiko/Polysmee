package io.github.polysmee.database;

import junit.framework.TestCase;

import java.util.HashSet;

import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

public class AppointmentTest extends TestCase {

    Appointment appo = new Appointment() {
        @Override
        public void getStartTimeAndThen(LongValueListener l) {}

        @Override
        public void removeStartListener(LongValueListener l) {}

        @Override
        public void getDurationAndThen(LongValueListener l) {}

        @Override
        public void removeDurationListener(LongValueListener l) {}

        @Override
        public String getId() {return "";}

        @Override
        public void getCourseAndThen(StringValueListener s) {}

        @Override
        public void removeCourseListener(StringValueListener l) {}

        @Override
        public void getTitleAndThen(StringValueListener s) {}

        @Override
        public void removeTitleListener(StringValueListener l) {}

        @Override
        public void getParticipantsIdAndThen(StringSetValueListener s) {}

        @Override
        public void removeParticipantsListener(StringSetValueListener s) {}

        @Override
        public void getOwnerIdAndThen(StringValueListener s) {}

        @Override
        public void removeOwnerListener(StringValueListener s) {}

        @Override
        public boolean setStartTime(long startTime) {
            return false;
        }

        @Override
        public boolean setDuration(long duration) {
            return false;
        }

        @Override
        public void setCourse(String course) {}

        @Override
        public void setTitle(String title) {}

        @Override
        public boolean addParticipant(User newParticipant) {
            return false;
        }

        @Override
        public boolean removeParticipant(User participant) {
            return false;
        }

        @Override
        public void getBansAndThen(StringSetValueListener s) {}

        @Override
        public void removeBansListener(StringSetValueListener s) {}

        @Override
        public void getPrivateAndThen(BooleanValueListener bool) {}

        @Override
        public void removePrivateListener(BooleanValueListener bool) {}

        @Override
        public void setPrivate(boolean isPrivate) {}

        @Override
        public boolean addBan(User banned) {
            return false;
        }

        @Override
        public boolean removeBan(User unbanned) {
            return false;
        }
    };

    public void testAll(){
        appo.getBansAndThen(null);
        appo.getCourseAndThen(null);
        appo.getDurationAndThen(null);
        assertEquals("", appo.getId());
        appo.getOwnerIdAndThen(null);
        appo.getParticipantsIdAndThen(null);
        appo.getPrivateAndThen(null);
        appo.getStartTimeAndThen(null);
        appo.getTitleAndThen(null);
        assertFalse(appo.addBan(null));
        assertFalse(appo.removeBan(null));
        assertFalse(appo.addParticipant(null));
        assertFalse(appo.removeParticipant(null));
        appo.setCourse("");
        appo.setDuration(0);
        appo.setPrivate(false);
        appo.setStartTime(0);
        appo.setTitle("");
        appo.removeBansListener(null);
        appo.removeCourseListener(null);
        appo.removeDurationListener(null);
        appo.removeOwnerListener(null);
        appo.removeParticipantsListener(null);
        appo.removePrivateListener(null);
        appo.removeStartListener(null);
        appo.removeTitleListener(null);
    }
}