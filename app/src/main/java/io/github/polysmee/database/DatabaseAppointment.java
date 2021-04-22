package io.github.polysmee.database;

import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.MessageChildListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.messages.Message;

public class DatabaseAppointment implements Appointment {

    private final String id;

    public DatabaseAppointment(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .addValueEventListener(l);
    }

    @Override
    public void getStartTime_Once_AndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .addListenerForSingleValueEvent(l);
    }

    @Override
    public void removeStartListener(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .removeEventListener(l);
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .addValueEventListener(l);
    }

    @Override
    public void getDuration_Once_AndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .addListenerForSingleValueEvent(l);
    }

    @Override
    public void removeDurationListener(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .removeEventListener(l);
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .addValueEventListener(s);
    }

    @Override
    public void getCourse_Once_AndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeCourseListener(StringValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .removeEventListener(l);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .addValueEventListener(s);
    }

    @Override
    public void getTitle_Once_AndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeTitleListener(StringValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .removeEventListener(l);
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .addValueEventListener(s);
    }

    @Override
    public void getParticipantsId_Once_AndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeParticipantsListener(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .removeEventListener(s);
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .addValueEventListener(s);
    }

    @Override
    public void getOwnerId_Once_AndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeOwnerListener(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .removeEventListener(s);
    }

    @Override
    public void setStartTime(long startTime) {
        if (startTime < 0)
            return;

        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .setValue(startTime);
    }

    @Override
    public void setDuration(long duration) {
        if (duration < 0 || duration > 3600000 * 4)
            return;

        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .setValue(duration);
    }

    @Override
    public void setCourse(String course) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .setValue(course);
    }

    @Override
    public void setTitle(String title) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .setValue(title);
    }

    @Override
    public void addParticipant(User newParticipant) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(newParticipant.getId())
                .setValue(true);
    }

    @Override
    public void removeParticipant(User participant) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(participant.getId())
                .setValue(null);
    }

    @Override
    public void getBansAndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .addValueEventListener(s);
    }

    @Override
    public void getBans_Once_AndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeBansListener(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .removeEventListener(s);
    }

    @Override
    public void getPrivateAndThen(BooleanValueListener b) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .addValueEventListener(b);
    }

    @Override
    public void getPrivate_Once_AndThen(BooleanValueListener b) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .addListenerForSingleValueEvent(b);
    }

    @Override
    public void removePrivateListener(BooleanValueListener bool) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .removeEventListener(bool);
    }


    @Override
    public void setPrivate(boolean isPrivate) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .setValue(isPrivate);
    }

    @Override
    public void addBan(User banned) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(banned.getId())
                .setValue(true);
    }

    @Override
    public void removeBan(User unbanned) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(unbanned.getId())
                .setValue(null);
    }

    @Override
    public void addInCallUser(User inCall) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(inCall.getId())
                .setValue(false);
    }

    @Override
    public void muteUser(User user, boolean muted) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(user.getId())
                .setValue(muted);
    }

    @Override
    public void removeOfCall(User outOfCall) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(outOfCall.getId())
                .setValue(null);
    }

    @Override
    public void addInCallListener(BooleanChildListener listener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .addChildEventListener(listener);
    }

    @Override
    public void removeInCallListener(BooleanChildListener listener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .removeEventListener(listener);
    }

    @Override
    public void addMessage(Message message) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .push()
                .setValue(message);

    }

    @Override
    public void removeMessage(String key) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .removeValue();
    }

    @Override
    public void editMessage(String key, String newContent) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .child("content")
                .setValue(newContent);
    }

    @Override
    public void addMessageListener(MessageChildListener listener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .addChildEventListener(listener);
    }

    @Override
    public void removeMessageListener(MessageChildListener listener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .removeEventListener(listener);
    }
}
