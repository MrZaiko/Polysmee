package io.github.polysmee.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

public final class DatabaseUser implements User {

    private final String self_id;

    public DatabaseUser(String id) {
        self_id = id;
    }

    @Override
    public String getId() {
        return self_id;
    }

    @Override
    public void addAppointment(Appointment appointment) {
        DatabaseFactory.getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .child(appointment.getId())
                .setValue(true);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .child(appointment.getId())
                .setValue(null);
    }

    @Override
    public void getInvitesAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("invites")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getInvites_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("invites")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeInvitesListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("invites")
                .removeEventListener(valueListener);
    }

    @Override
    public void addInvite(Appointment newAppointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("invites")
                .child(newAppointment.getId())
                .setValue(true);
    }

    @Override
    public void removeInvite(Appointment appointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("invites")
                .child(appointment.getId())
                .setValue(null);
    }


    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getName_Once_AndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .addListenerForSingleValueEvent(valueListener);
    }


    @Override
    public void setName(@NonNull String value) {
        if (value.isEmpty()){
            return;
        }
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .setValue(value);
    }

    @Override
    public void removeNameListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .removeEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointments_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeAppointmentsListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
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
        this.addAppointment(appointment);
        appointment.addParticipant(new DatabaseUser(self_id));
        return ref.getKey();
    }

    @Override
    public void addFriend(User user) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("friends")
                .child(user.getId())
                .setValue(true);
    }

    @Override
    public void removeFriend(User user) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("friends")
                .child(user.getId())
                .setValue(null);
    }

    @Override
    public void getFriendsAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("friends")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getFriends_Once_And_Then(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("friends")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeFriendsListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("friends")
                .removeEventListener(valueListener);
    }

    @Override
    public void setProfilePicture(String pictureId) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("picture")
                .setValue(pictureId);
    }

    @Override
    public void removeProfilePicture() {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("picture")
                .setValue(null);
    }

    @Override
    public void getProfilePictureAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("picture")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getProfilePicture_Once_And_Then(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("picture")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeProfilePictureListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("picture")
                .removeEventListener(valueListener);
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
