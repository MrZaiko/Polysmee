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
    private static final String PICTURE_RELATIVE_PATH = "picture";
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
        DatabaseFactory.getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(eventId);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(null);
    }

    @Override
    public void getInvitesAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getInvites_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeInvitesListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void addInvite(Appointment newAppointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .child(newAppointment.getId())
                .setValue(true);
    }

    @Override
    public void removeInvite(Appointment appointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(INVITES_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(null);
    }


    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getName_Once_AndThen(StringValueListener valueListener) {
        DatabaseFactory
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
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .setValue(value);
    }

    @Override
    public void removeNameListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(NAME_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndEventIdsAndThen(MapStringStringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndEventIds_Once_AndThen(MapStringStringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void getAppointmentEventId_Once_AndThen(Appointment appointment, StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void setAppointmentEventId(Appointment appointment, String eventId) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .child(appointment.getId())
                .setValue(eventId);
    }

    @Override
    public void getAppointments_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeAppointmentsListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(APPOINTMENTS_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate) {
        DatabaseReference ref = DatabaseFactory.getAdaptedInstance().getReference("appointments").push();

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
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .child(user.getId())
                .setValue(true);
    }

    @Override
    public void removeFriend(User user) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .child(user.getId())
                .setValue(null);
    }

    @Override
    public void getFriendsAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getFriends_Once_And_Then(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeFriendsListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(FRIENDS_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }

    @Override
    public void setProfilePicture(String pictureId) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .setValue(pictureId);
    }

    @Override
    public void removeProfilePicture() {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .setValue(null);
    }

    @Override
    public void getProfilePictureAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }

    @Override
    public void getProfilePicture_Once_And_Then(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeProfilePictureListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(PICTURE_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }
    @Override
    public void setDescription(String description) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .setValue(description);
    }


    @Override
    public void getDescriptionAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .addValueEventListener(valueListener);
    }


    @Override
    public void getDescription_Once_AndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeDescriptionListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child(DESCRIPTION_RELATIVE_PATH)
                .removeEventListener(valueListener);
    }


    @Override
    public void getCalendarId_Once_AndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child("calendarId")
                .addValueEventListener(valueListener);
    }

    @Override
    public void setCalendarId(String calendarId) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference(USERS_RELATIVE_PATH)
                .child(self_id)
                .child("calendarId")
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
