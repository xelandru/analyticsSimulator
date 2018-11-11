package data;

import static data.Event.*;

public final class EventUtils {

    private EventUtils() {
    }

    public static Event getEventFromString(String eventAsString) {

        String[] fields = eventAsString.split(",");
        long timeStamp = Long.valueOf(fields[0].trim());
        EventType type = Event.EventType.valueOf(fields[1].trim());
        String userId = fields[2].trim();
        String sessionId = fields[3].trim();
        return new Event(timeStamp, type, userId, sessionId);
    }

}
