package data;

public class Event {

    private final long timeStamp;
    private final String userId;
    private final String sessionId;
    private final EventType eventType;

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Event(long timeStamp,EventType eventType, String userId, String sessionId ) {
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.sessionId = sessionId;
        this.eventType = eventType;
    }

    public enum EventType {
        LOGIN,
        LOGOUT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;
        return userId.equals(event.userId) && sessionId.equals(event.sessionId);
    }

    @Override
    public String toString() {
        return "Event{" +
                "timeStamp=" + timeStamp +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", eventType=" + eventType +
                '}';
    }
}
