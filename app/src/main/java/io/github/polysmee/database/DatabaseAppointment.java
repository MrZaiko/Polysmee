package io.github.polysmee.database;

import com.google.firebase.database.ValueEventListener;

import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.MessageChildListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

public class DatabaseAppointment implements Appointment {

    private final String id;

    public DatabaseAppointment(String id) {
        this.id = id;
    }

    private void getStuffAndThen(String stuff, ValueEventListener l) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child(stuff)
                .addValueEventListener(l);
    }

    private void getStuff_Once_AndThen(String stuff, ValueEventListener l) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child(stuff)
                .addListenerForSingleValueEvent(l);
    }

    private void removeStuffListener(String stuff, ValueEventListener l) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child(stuff)
                .removeEventListener(l);
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {
        getStuffAndThen("start", l);
    }

    @Override
    public void getStartTime_Once_AndThen(LongValueListener l) {
        getStuff_Once_AndThen("start", l);
    }

    @Override
    public void removeStartListener(LongValueListener l) {
        removeStuffListener("start", l);
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {
        getStuffAndThen("duration", l);
    }

    @Override
    public void getDuration_Once_AndThen(LongValueListener l) {
        getStuff_Once_AndThen("duration", l);
    }

    @Override
    public void removeDurationListener(LongValueListener l) {
        removeStuffListener("duration", l);
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {
        getStuffAndThen("course", s);
    }

    @Override
    public void getCourse_Once_AndThen(StringValueListener s) {
        getStuff_Once_AndThen("course", s);
    }

    @Override
    public void removeCourseListener(StringValueListener l) {
        removeStuffListener("course", l);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        getStuffAndThen("title", s);
    }

    @Override
    public void getTitle_Once_AndThen(StringValueListener s) {
        getStuff_Once_AndThen("title", s);
    }

    @Override
    public void removeTitleListener(StringValueListener l) {
        removeStuffListener("title", l);
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {
        getStuffAndThen("participants", s);
    }

    @Override
    public void getParticipantsId_Once_AndThen(StringSetValueListener s) {
        getStuff_Once_AndThen("participants", s);
    }

    @Override
    public void removeParticipantsListener(StringSetValueListener s) {
        removeStuffListener("participants", s);
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {
        getStuffAndThen("owner", s);
    }

    @Override
    public void getOwnerId_Once_AndThen(StringValueListener s) {
        getStuff_Once_AndThen("owner", s);
    }

    @Override
    public void removeOwnerListener(StringValueListener s) {
        removeStuffListener("owner", s);
    }

    @Override
    public void getInvitesIdAndThen(StringSetValueListener s) {
        getStuffAndThen("invites", s);
    }

    @Override
    public void getInvitesId_Once_AndThen(StringSetValueListener s) {
        getStuff_Once_AndThen("invites", s);
    }

    @Override
    public void removeInvitesListener(StringSetValueListener s) {
        removeStuffListener("invites", s);
    }

    @Override
    public void getPrivateAndThen(BooleanValueListener b) {
        getStuffAndThen("private", b);
    }

    @Override
    public void getPrivate_Once_AndThen(BooleanValueListener b) {
        getStuff_Once_AndThen("private", b);
    }

    @Override
    public void removePrivateListener(BooleanValueListener b) {
        removeStuffListener("private", b);
    }

    @Override
    public void setStartTime(long startTime) {
        if (startTime < 0)
            return;

        DatabaseSingleton
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

        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .setValue(duration);
    }

    @Override
    public void setCourse(String course) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .setValue(course);
    }

    @Override
    public void setTitle(String title) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .setValue(title);
    }

    @Override
    public void addParticipant(User newParticipant) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(newParticipant.getId())
                .setValue(true);
    }

    @Override
    public void removeParticipant(User participant) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(participant.getId())
                .setValue(null);
    }

    @Override
    public void getBansAndThen(StringSetValueListener s) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .addValueEventListener(s);
    }

    @Override
    public void getBans_Once_AndThen(StringSetValueListener s) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .addListenerForSingleValueEvent(s);
    }

    @Override
    public void removeBansListener(StringSetValueListener s) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .removeEventListener(s);
    }

    @Override
    public void setPrivate(boolean isPrivate) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .setValue(isPrivate);
    }

    @Override
    public void addInvite(User newParticipant) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("invites")
                .child(newParticipant.getId())
                .setValue(true);
    }

    @Override
    public void removeInvite(User participant) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("invites")
                .child(participant.getId())
                .setValue(null);
    }

    @Override
    public void addBan(User banned) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(banned.getId())
                .setValue(true);
    }

    @Override
    public void removeBan(User unbanned) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(unbanned.getId())
                .setValue(null);
    }

    @Override
    public void addInCallUser(User inCall) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(inCall.getId())
                .setValue(false);
    }

    @Override
    public void muteUser(User user, boolean muted) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(user.getId())
                .setValue(muted);
    }

    @Override
    public void removeOfCall(User outOfCall) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .child(outOfCall.getId())
                .setValue(null);
    }

    @Override
    public void addInCallListener(BooleanChildListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .addChildEventListener(listener);
    }

    @Override
    public void removeInCallListener(BooleanChildListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("inCall")
                .removeEventListener(listener);
    }

    @Override
    public void addMessage(Message message) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .push()
                .setValue(message);

    }

    @Override
    public void removeMessage(String key) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .removeValue();
    }

    @Override
    public void editMessage(String key, String newContent) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .child("content")
                .setValue(newContent);
    }

    @Override
    public void editMessageReaction(String key, int newContent) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .child("reaction")
                .setValue(newContent);
    }

    @Override
    public void getMessageReaction_Once_AndThen(String key, LongValueListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .child(key)
                .child("reaction")
                .addListenerForSingleValueEvent(listener);
    }

    @Override
    public void addMessageListener(MessageChildListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .addChildEventListener(listener);
    }

    @Override
    public void removeMessageListener(MessageChildListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("messages")
                .removeEventListener(listener);
    }


    @Override
    public void getTimeCodeOnceAndThen(User user, LongValueListener listener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("callStates")
                .child(user.getId())
                .child("timeCode")
                .addListenerForSingleValueEvent(listener);
    }


    @Override
    public void setTimeCode(User user, Long timeCode) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("callStates")
                .child(user.getId())
                .child("timeCode")
                .setValue(timeCode);
    }

    @Override
    public void selfDestroy() {
        getParticipantsId_Once_AndThen(participants -> {
            for(String userId : participants) {
                User user = new DatabaseUser(userId);
                user.removeAppointment(this);
            }
        });
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id).removeValue();
    }

    @Override
    public void setOwner(User user) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .setValue(user.getId());
    }
}