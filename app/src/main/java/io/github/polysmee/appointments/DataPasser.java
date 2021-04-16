package io.github.polysmee.appointments;

import java.util.Set;

/**
 * Interface that enables communication between fragments and Activities.
 * A method for each data type that can be transferred.
 */
public interface DataPasser {
    void dataPass(Set<String> data, String id);
}
