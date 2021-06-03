package io.github.polysmee.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.childListeners.BooleanChildListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.childListeners.MessageChildListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;

/**
 * A generic appointment
 */
public interface Appointment {

    /**
     * @return the id of this appointment, which defines equality
     */
    String getId();

    //====================== START TIME ===================

    /**
     * @param l the listener to be added for changes to the start time.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getStartTimeAndThen(LongValueListener l);

    /**
     * @param l the listener to be added for changes to the start time.
     *          It is scheduled only once.
     */
    void getStartTime_Once_AndThen(LongValueListener l);

    /**
     * @param l the listener to be removed from listening to the start time
     */
    void removeStartListener(LongValueListener l);

    /**
     * Sets the appointment's start time
     *
     * @param startTime the new start time
     */
    void setStartTime(long startTime);


    //===================== DURATION ============================

    /**
     * @param l the listener to be added for changes to the duration time.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getDurationAndThen(LongValueListener l);

    /**
     * @param l the listener to be added for changes to the duration time.
     *          It is scheduled only once.
     */
    void getDuration_Once_AndThen(LongValueListener l);

    /**
     * @param l the listener to be removed from listening to the duration time
     */
    void removeDurationListener(LongValueListener l);

    /**
     * Sets the appointment's duration, which cannot be longer than 4 hours
     *
     * @param duration the new duration
     */
    void setDuration(long duration);

    //======================== COURSE NAME ============================

    /**
     * @param s the listener to be added for changes to the course name.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getCourseAndThen(StringValueListener s);

    /**
     * @param s the listener to be added for changes to the course name.
     *          It is scheduled only once.
     */
    void getCourse_Once_AndThen(StringValueListener s);

    /**
     * @param s the listener to be removed from listening to the course name
     */
    void removeCourseListener(StringValueListener s);

    /**
     * Sets the appointment's course
     *
     * @param course the new course
     */
    void setCourse(String course);

    //=========================== TITLE ==============================

    /**
     * @param s the listener to be added for changes to the title of the appointment.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getTitleAndThen(StringValueListener s);

    /**
     * @param s the listener to be added for changes to the title of the appointment.
     *          It is scheduled only once.
     */
    void getTitle_Once_AndThen(StringValueListener s);

    /**
     * @param l the listener to be removed from listening to the title of the appointment
     */
    void removeTitleListener(StringValueListener l);

    /**
     * Sets the appointment's title
     *
     * @param title the new title
     */
    void setTitle(String title);


    //============================= PARTICIPANTS ====================

    /**
     * @param s the listener to be added for changes to the participant list.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getParticipantsIdAndThen(StringSetValueListener s);

    /**
     * @param s the listener to be added for changes to the participant list.
     *          It is scheduled only once.
     */
    void getParticipantsId_Once_AndThen(StringSetValueListener s);

    /**
     * @param s the listener to be removed from listening to the participant list
     */
    void removeParticipantsListener(StringSetValueListener s);

    /**
     * Adds the given user to the set of participant
     *
     * @param newParticipant the user to be added
     */
    void addParticipant(User newParticipant);

    /**
     * Removes the given user to the set of participant
     *
     * @param participant the user to be removed
     */
    void removeParticipant(User participant);


    //========================= OWNER ===========================

    /**
     * @param s the listener to be added for changes to the owner id.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getOwnerIdAndThen(StringValueListener s);

    /**
     * @param s the listener to be added for changes to the owner id.
     *          It is scheduled only once.
     */
    void getOwnerId_Once_AndThen(StringValueListener s);

    /**
     * @param s the listener to be removed from listening to the owner id
     */
    void removeOwnerListener(StringValueListener s);

    //===================== BANS ====================

    /**
     * @param s the listener to be added for changes to the banned list.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getBansAndThen(StringSetValueListener s);

    /**
     * @param s the listener to be added for changes to the banned list.
     *          It is scheduled only once.
     */
    void getBans_Once_AndThen(StringSetValueListener s);


    /**
     * @param s the listener to be removed from listening to the banned list
     */
    void removeBansListener(StringSetValueListener s);

    /**
     * Adds the given user to the set of banned users
     *
     * @param banned the user to be banned
     */
    void addBan(User banned);

    /**
     * Removes the given user from the set of banned users
     *
     * @param unbanned the user to be unbanned
     */
    void removeBan(User unbanned);

    //======================== PRIVATE ======================

    /**
     * @param bool the listener to be added for changes to the 'private' boolean attribute.
     *             It is scheduled once when added, even if there is no change at that moment.
     */
    void getPrivateAndThen(BooleanValueListener bool);

    /**
     * @param bool the listener to be added for changes to the 'private' boolean attribute.
     *             It is scheduled only once.
     */
    void getPrivate_Once_AndThen(BooleanValueListener bool);

    /**
     * @param bool the listener to be removed from listening to the 'private' boolean attribute.
     */
    void removePrivateListener(BooleanValueListener bool);

    /**
     * @param isPrivate do you really need an explanation ? ;)
     */
    void setPrivate(boolean isPrivate);

    //================= INVITES ====================

    /**
     * @param s the listener to be added for changes to the participant list.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getInvitesIdAndThen(StringSetValueListener s);

    /**
     * @param s the listener to be added for changes to the participant list.
     *          It is scheduled only once.
     */
    void getInvitesId_Once_AndThen(StringSetValueListener s);

    /**
     * @param s the listener to be removed from listening to the participant list
     */
    void removeInvitesListener(StringSetValueListener s);

    /**
     * Adds the given user to the set of participant
     *
     * @param newParticipant the user to be added
     */
    void addInvite(User newParticipant);

    /**
     * Removes the given user to the set of participant
     *
     * @param participant the user to be removed
     */
    void removeInvite(User participant);


    //================= CALLS =======================

    /**
     * Adds the given user to the set of in call users
     *
     * @param inCall
     */
    void addInCallUser(User inCall);

    /**
     * Set the given User as muted (unmuted) if muted is true (false)
     *
     * @param user
     * @param muted
     */
    void muteUser(User user, boolean muted);

    /**
     * Removes the given user from in call users
     *
     * @param outOfCall
     */
    void removeOfCall(User outOfCall);

    /**
     * @param listener the listener to be added to the changes of the inCall set of the appointment
     *                 The childAdded method is executed for every child when added
     */
    void addInCallListener(BooleanChildListener listener);

    /**
     * @param listener the listener to be removed from listening the inCall set
     */
    void removeInCallListener(BooleanChildListener listener);

    //===================== MESSAGES ==================

    /**
     * Adds the given message to the set of Message
     *
     * @param message
     */
    void addMessage(Message message);

    /**
     * Removes the message with given key from the database
     *
     * @param key
     */
    void removeMessage(String key);

    /**
     * replaces the content of the message with given key by the new content given
     *
     * @param key
     * @param newContent
     */
    void editMessage(String key, String newContent);

    /**
     * replaces the reaction of the message with given key by the new reaction given
     *
     * @param key
     * @param newContent
     */
    void editMessageReaction(String key, int newContent);

    /**
     * return the current reaction
     *
     * @param key
     * @param listener
     */
    void getMessageReaction_Once_AndThen(String key, LongValueListener listener);

    /**
     * Adds given listener to the set of messages of the appointment
     *
     * @param listener
     */
    void addMessageListener(MessageChildListener listener);

    /**
     * Removes the given listener from the set of messages of the appointment
     *
     * @param listener
     */
    void removeMessageListener(MessageChildListener listener);


    /**
     * @param user
     * @param listener the listener to be added for changes to the token of the given user.
     *                 It is scheduled only once.
     */
    void getTimeCodeOnceAndThen(User user, LongValueListener listener);


    /**
     * sets the timeCode of the given user to the given value
     *
     * @param user
     * @param timeCode
     */
    void setTimeCode(User user, Long timeCode);

    /**
     * Deletes the appointment
     */
    void selfDestroy();

    /**
     * Sets the owner of the appointment to the given user
     * @param user
     */
    void setOwner(User user);

    /**
     * @param ssv a listener that will be run once and will receive the list of all appointments declared as
     *            public
     */
    @SuppressWarnings({"unchecked"})
    static void getAllPublicAppointmentsOnce(StringSetValueListener ssv) {

        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("appointments")
                .get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.getValue() != null) {
                Set<String> appos = new HashSet<>();
                HashMap<String, Object> hash = (HashMap<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : hash.entrySet()) {
                    if (!((Boolean) ((HashMap<String, Object>) entry.getValue()).get("private"))) {
                        appos.add(entry.getKey());
                    }
                }
                ssv.onDone(appos);
            }

        });
    }
}
