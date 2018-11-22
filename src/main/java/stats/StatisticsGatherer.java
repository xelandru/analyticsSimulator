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
    private long allUsersConnectedTimeStamp = 0;
    private int logoutCounter = 0;
    private Set<String> usersConnectedNow;
    private static Set<String> allUsersDisconnected;
    private final long windowSize;

    public StatisticsGatherer(ConcurrentHashMap<String, Event> eventDB, long windowSize) {
        this.eventDB = eventDB;
        this.usersConnectedNow = new HashSet<>();
        allUsersDisconnected = new HashSet<>();
        this.windowSize = windowSize;
    }

    public String get(int iteration) {

        eventDB.forEach((key, event) -> {

            if (event.getEventType().equals(LOGOUT)) {
                allSessionsDuration += event.getTimeStamp();
                allUsersDisconnected.add(event.getUserId());
                logoutCounter++;
            }
            if (event.getEventType().equals(LOGIN)) {
                usersConnectedNow.add(event.getUserId());
                allUsersConnectedTimeStamp+=event.getTimeStamp();
            }
        });


        double windowSizeInSeconds = (double) windowSize / 1000;

        /*
                Avg = TotalTime/totalUsers
                totalUsers=connected+ disconnected
                TotalTime = TimeConnected + TimeDisconnected
                TimeDisconnected =  allSessionsDuration
                TimeConnected = connected*iteration*WindowSize-sum(timeStampOfLogin)

         */
        int numberOfUsersConnected = usersConnectedNow.size();

        double averageSessionDuration = (allSessionsDuration + numberOfUsersConnected * windowSizeInSeconds * iteration - allUsersConnectedTimeStamp)
                / (logoutCounter + numberOfUsersConnected);


        long totalUsers = allUsersDisconnected
                .stream()
                .filter(s -> !usersConnectedNow.contains(s))
                .count() + numberOfUsersConnected;

        //reset
        allUsersConnectedTimeStamp =0;
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