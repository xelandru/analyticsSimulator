package stats;

import data.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

public class StatisticsGatherer {

    private final ConcurrentHashMap<String, Event> eventDB;
    private long allSessionsDuration = 0;
    private int logoutCounter = 0;
    private Set<String> usersConnectedNow;
    private static Set<String> allUsersDisconnected;

    public StatisticsGatherer(ConcurrentHashMap<String, Event> eventDB) {
        this.eventDB = eventDB;
        this.usersConnectedNow = new HashSet<>();
        allUsersDisconnected = new HashSet<>();
    }

    public String get() {

        eventDB.forEach((key, event) -> {

            if (event.getEventType().equals(LOGOUT)) {
                allSessionsDuration += event.getTimeStamp();
                allUsersDisconnected.add(event.getUserId());
                logoutCounter++;
            }
            if (event.getEventType().equals(LOGIN)) {
                usersConnectedNow.add(event.getUserId());
            }
        });


        int numberOfUsersConnected = usersConnectedNow.size();
        double averageSessionDuration = (double) allSessionsDuration / logoutCounter;


        long totalUsers = allUsersDisconnected
                .stream()
                .filter(s -> !usersConnectedNow.contains(s))
                .count() + numberOfUsersConnected;

        usersConnectedNow.clear();


        //add this to cleanup
        eventDB.entrySet().removeIf(entry -> entry.getValue().getEventType().equals(LOGOUT));
        return new Statistics(numberOfUsersConnected, averageSessionDuration, totalUsers).toString();
    }

    private static class Statistics {
        private int usersConnected;
        private double averageUserSession;
        private static long totalUsers;

        Statistics(int usersConnected, double averageUserSession, long totalUsers) {
            this.usersConnected = usersConnected;
            this.averageUserSession = averageUserSession;
            Statistics.totalUsers = totalUsers;
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "usersConnectedNow=" + usersConnected +
                    ", averageUserSessionDuration=" + averageUserSession +
                    ", totalUniqueUsers=" + totalUsers +
                    '}';
        }
    }
}