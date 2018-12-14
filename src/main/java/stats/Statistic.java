package stats;

import data.Event;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Statistic {

    private int sessionsIn;
    private int sessionsOut;
    private long timeIn;
    private long timeOut;
    private int wSize;
    private final ConcurrentLinkedQueue<Event> eventsIn;
    private final ConcurrentLinkedQueue<Event> eventsOut;


    public Statistic(ConcurrentLinkedQueue<Event> eventsIn, ConcurrentLinkedQueue<Event> eventsOut) {
        this.sessionsIn = sessionsIn;
        this.sessionsOut = sessionsOut;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.eventsIn = eventsIn;
        this.eventsOut = eventsOut;
    }

    public Statistic() {
        eventsIn = new ConcurrentLinkedQueue<>();
        eventsOut = new ConcurrentLinkedQueue<>();
    }

    public int getSessionsIn() {
        return sessionsIn;
    }

    public void incrementSessionsIn() {
        sessionsIn++;
    }

    public int getSessionsOut() {
        return sessionsOut;
    }

    public void incrementSessionsOut() {
        sessionsOut++;
    }

    public long getTimeIn() {
        return timeIn;
    }

    public void incrementTimeIn(long timeIn) {
        this.timeIn += timeIn;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void incrementTimeOut(long timeOut) {
        this.timeOut += timeOut;
    }

    public void addEventsIn(Event event) {
        eventsIn.add(event);
    }

    public void addEventsOut(Event event) {
        eventsOut.add(event);
    }

    public ConcurrentLinkedQueue<Event> getEventsIn() {
        return eventsIn;
    }

    public ConcurrentLinkedQueue<Event> getEventsOut() {
        return eventsOut;
    }

    public void increaseWindow() {
        wSize += 10;
    }

    public int getWindowSize() {
        return wSize;
    }
}
