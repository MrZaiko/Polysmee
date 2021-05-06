package io.github.polysmee.appointments;

import java.util.Set;

/**
 * Interface that enables communication between fragments and Activities.
 * A method for each data type that can be transferred.
 */
public interface DataPasser {
    /**
     * Used by the fragments to send data to an activity implementing this interface
     * @param data sent data
     * @param id id of this data
     */
    void dataPass(Set<String> data, String id);
}
