package consumer;

import data.Event;
import stats.Statistic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static data.Event.EventType.LOGIN;

public class EventConsumer implements Runnable {

    private final CyclicBarrier barrier;
    private final Statistic statistic;
    private final BlockingQueue<Event> events;

    public EventConsumer(BlockingQueue<Event> events,
                         Statistic statistic,
                         CyclicBarrier barrier) {
        this.events = events;
        this.barrier = barrier;
        this.statistic = statistic;
    }

    @Override
    public void run() {

        while (true) {
            int wSize = statistic.getWindowSize();
            if (events.peek().getTimeStamp() <= wSize) {
                try {

                    Event event = events.take();
                    Event.EventType type = event.getEventType();
                    long timeStamp = event.getTimeStamp();

                    if (type.equals(LOGIN)) {
                        statistic.incrementSessionsIn();
                        statistic.incrementTimeIn(timeStamp);
                        statistic.addEventsIn(event);
                    } else {
                        statistic.incrementSessionsOut();
                        statistic.incrementTimeOut(timeStamp);
                        statistic.addEventsOut(event);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
