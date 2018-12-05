package data;

import java.util.Map;
import java.util.Optional;

import static data.Event.*;
import static data.Event.EventType.LOGOUT;

public final class EventUtils {

    private EventUtils() {
    }

    public static Optional<Event> parseEventFromString(String eventAsString) {

        String[] fields = eventAsString.split(",");

        if (fields.length == 4 && isNumeric(fields[0])
                && (fields[1].trim().equalsIgnoreCase("LOGIN") || fields[1].trim().equalsIgnoreCase("LOGOUT"))) {

            long timeStamp = Long.valueOf(fields[0].trim());
            EventType type = Event.EventType.valueOf(fields[1].trim().toUpperCase());
            String userId = fields[2].trim();
            String sessionId = fields[3].trim();
            return Optional.of(new Event(timeStamp, type, userId, sessionId));
        }
        return Optional.empty();
    }

    public static void eventMapCleaner(Map<String, Event> eventDB) {
        eventDB.entrySet().removeIf(entry -> entry.getValue().getEventType().equals(LOGOUT));
    }

    private static boolean isNumeric(String str) {
        return str.trim().matches("\\d+");
    }

}
