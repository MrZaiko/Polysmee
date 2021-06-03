package io.github.polysmee.database;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;

public class Course {
    public static void getAllCourses_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseSingleton
                .getAdaptedInstance()
                .getReference("courses")
                .addListenerForSingleValueEvent(valueListener);
    }
}
