package io.github.polysmee.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.polysmee.database.databaselisteners.MapStringStringValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

public final class DatabaseUser implements User {

    private static final String DESCRIPTION_RELATIVE_PATH = "description";
    private static final String USERS_RELATIVE_PATH = "users";
    private static final String APPOINTMENTS_RELATIVE_PATH = "appointments";
    private static final String INVITES_RELATIVE_PATH = "invites";
    private static final String NAME_RELATIVE_PATH = "name";
    private static final String FRIENDS_RELATIVE_PATH = "friends";
    private static final String FRIENDS_INVITES_RELATIVE_PATH = "friendsInvites";
    private static final String PICTURE_RELATIVE_PATH = "picture";
    private static final String CALENDAR_ID_RELATIVE_PATH = "calendarId";
    private final String self_id;

    public DatabaseUser(String id) {
        self_id = id;
    }

    @Override
    public String getId() {
        return self_id;
    }

    @Override
    public void addAppointment(Appointment appointment, String eventId) {
        DatabaseSingleton.getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(eventId);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(null);
    }

    @Override
    public void getInvitesAndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getInvites_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeInvitesListener(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void addInvite(Appointment newAppointment) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .child(newAppointment.getId())
                .setValue(true);
    }

    @Override
    public void removeInvite(Appointment appointment) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(null);
    }


    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getName_Once_AndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }


    @Override
    public void setName(@NonNull String value) {
        if (value.isEmpty()) {
            return;
        }
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .setValue(value);
    }

    @Override
    public void removeNameListener(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndEventIdsAndThen(MapStringStringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndEventIds_Once_AndThen(MapStringStringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void getAppointmentEventId_Once_AndThen(Appointment appointment, StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void setAppointmentEventId(Appointment appointment, String eventId) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(eventId);
    }

    @Override
    public void getAppointments_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeAppointmentsListener(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate) {
        DatabaseReference ref = DatabaseSingleton.getAdaptedInstance().getReference("appointments").push();

        Map<String, Object> newAppo = new HashMap<>();
        newAppo.put("owner", self_id);
        newAppo.put("id", ref.getKey());
        newAppo.put("members", new HashMap<String, Boolean>().put(self_id, true));
        newAppo.put("start", start);
        newAppo.put("duration", duration);
        newAppo.put("course", course);
        newAppo.put("title", name);
        newAppo.put("private", isPrivate);
        ref.setValue(newAppo);

        Appointment appointment = new DatabaseAppointment(ref.getKey());
        this.addAppointment(appointment, "");
        appointment.addParticipant(new DatabaseUser(self_id));
        return ref.getKey();
    }

    @Override
    public void addFriend(User user) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .child(user.getId())
                .setValue(true);
    }

    @Override
    public void removeFriend(User user) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .child(user.getId())
                .setValue(null);
    }

    @Override
    public void getFriendsAndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getFriends_Once_And_Then(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeFriendsListener(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void setProfilePicture(String pictureId) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .setValue(pictureId);
    }

    @Override
    public void removeProfilePicture() {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .setValue(null);
    }

    @Override
    public void getProfilePictureAndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getProfilePicture_Once_And_Then(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeProfilePictureListener(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }
    @Override
    public void setDescription(String description) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .setValue(description);
    }


    @Override
    public void getDescriptionAndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }


    @Override
    public void getDescription_Once_AndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeDescriptionListener(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void sendFriendInvitation(User user) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(user.getId())
                .child(FRIENDS_INVITES_RELATIVE_PATH)
                .child(self_id)
                .setValue(true);
    }

    @Override
    public void removeFriendInvitation(User user) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_INVITES_RELATIVE_PATH)
                .child(user.getId())
                .setValue(null);
    }

    @Override
    public void getFriendsInvitationsAndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_INVITES_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getFriendsInvitations_Once_And_Then(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_INVITES_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeFriendsInvitationsListener(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_INVITES_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }


    @Override
    public void getCalendarId_Once_AndThen(StringValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(CALENDAR_ID_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void setCalendarId(String calendarId) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(CALENDAR_ID_RELATIVE_PATH)
                .setValue(calendarId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseUser that = (DatabaseUser) o;
        return self_id.equals(that.self_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self_id);
    }
}
