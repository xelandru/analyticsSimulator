package stats;

import data.Event;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


/*
Collects statistics and removes paired events(in with outs)
 */
public class BarrierAction implements Runnable {

    /*
    Avg formula: timePerWindow/sessionInPerWindow

    timePerWindow = windowSize * (usersLoggedIn-usersLoggedOut) - sum(timeSessionsIn) + sum(timeSessionsOut)
    sessionInPerWindow= sum(usersLoggedIn)
     */


    Statistic statistic;

    public BarrierAction( Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public void run() {

        int wSize = statistic.getWindowSize();
        long totalTime = wSize * (statistic.getSessionsIn() - statistic.getSessionsOut())
                - statistic.getTimeIn() + statistic.getTimeOut();

        double avg = (double) totalTime / statistic.getSessionsIn();
        System.out.println("Average: " + avg);

        statistic.increaseWindow();
        removePairedEvents();
    }

    private void removePairedEvents() {

        ConcurrentLinkedQueue<Event> sessionsIn = statistic.getEventsIn();
        ConcurrentLinkedQueue<Event> sessionsOut = statistic.getEventsOut();

        Iterator<Event> iterator = sessionsOut.iterator();
        while (iterator.hasNext()) {
            Event removableEvent = iterator.next();
            if (sessionsIn.contains(iterator.next())) {
                sessionsIn.remove(removableEvent);
                sessionsOut.remove(removableEvent);
            }
        }
    }
}
