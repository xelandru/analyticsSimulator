package stats;

import data.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

public class StatisticsCollector {

    private final long windowSize;
    private final ConcurrentHashMap<String, Event> eventDB;
    private final Set<String> allUniqueUsers = new HashSet<>();

    private long loginTime = 0;
    private long logoutTime = 0;
    private int sessionsLoggedInInWindow = 0;
    private int sessionsLoggedOutInWindow = 0;

    public StatisticsCollector(ConcurrentHashMap<String, Event> eventDB, long windowSize) {
        this.eventDB = eventDB;
        this.windowSize = windowSize / 1000;
//        this.usersConnectedNow = new HashSet<>();
//        allUsersDisconnected = new HashSet<>();


    }


    public String collect() {

        loginTime = 0;
        logoutTime = 0;
        sessionsLoggedInInWindow = 0;
        sessionsLoggedOutInWindow = 0;

        long startSim = System.currentTimeMillis();

        eventDB.forEach((key, event) -> {

            if (event.getEventType().equals(LOGOUT)) {
                logoutTime += event.getTimeStamp();
                sessionsLoggedOutInWindow++;
            }
            if (event.getEventType().equals(LOGIN)) {
                event.incrementWindow();
                loginTime += event.getWindow() * windowSize - event.getTimeStamp();
                sessionsLoggedInInWindow++;
            }
            allUniqueUsers.add(event.getUserId());
        });

        long totalTime = loginTime + logoutTime;
        int totalSessions = sessionsLoggedInInWindow + sessionsLoggedOutInWindow;
        double avg = (double) totalTime / totalSessions;

        long endSim = System.currentTimeMillis();

        System.out.println("Seconds: " + ((double) endSim - startSim) / 1000);


        return "Avg: " + avg + " connected:" + sessionsLoggedInInWindow;
    }

    public int getUniqueUsers() {
        return allUniqueUsers.size();
    }
}