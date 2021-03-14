package io.github.polysmee.database.decoys;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class FakeDatabase {
    private FakeDatabase(){}

    public static AtomicLong idGenerator = new AtomicLong(0);

    public static Map<String, TestAppointmentInfo> appId2App = new HashMap<>();
}
