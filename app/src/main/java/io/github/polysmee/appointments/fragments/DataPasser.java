package io.github.polysmee.appointments.fragments;

import java.util.ArrayList;
import java.util.Calendar;

public interface DataPasser {
    void dataPass(boolean data, String id);
    void dataPass(ArrayList<String> data, String id);
    void dataPass(String data, String id);
    void dataPass(Calendar data, String id);
}
