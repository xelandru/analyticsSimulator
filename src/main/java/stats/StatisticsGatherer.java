package stats;

import data.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

public class StatisticsGatherer {

    private final ConcurrentHashMap<String, Event> eventDataBase;
    private int NumberOfUniqUsers;
    private double averageSessionDuration;
    private long totalDuration = 0;
    private int logoutSessions = 0;
    private Set<String> uniqueUsersConnected;
    private static Set<String> totalUsersConnected;

    public StatisticsGatherer(ConcurrentHashMap<String, Event> eventDataBase) {
        this.eventDataBase = eventDataBase;
        uniqueUsersConnected = new HashSet<>();
    }

    public Statistics gather() {

        eventDataBase.forEach((key, event) -> {

            if (event.getEventType().equals(LOGOUT)) {
                totalDuration += event.getTimeStamp();
                logoutSessions++;
            }
            if (event.getEventType().equals(LOGIN)) {
                uniqueUsersConnected.add(event.getUserId());
            }
        });

        totalUsersConnected.addAll(uniqueUsersConnected);
        NumberOfUniqUsers = uniqueUsersConnected.size();
        uniqueUsersConnected.clear();

        return null;
    }
}
