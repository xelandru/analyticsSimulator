import data.Event;

import java.util.concurrent.*;

import static data.Event.EventType.*;

public class EventConsumer implements Runnable {

    private final ConcurrentHashMap<String, Event> eventMap;
    private final BlockingQueue<Event> sourceEventsQueue;


    private final Object o = new Object();
    private final CyclicBarrier barrier;
    private volatile boolean paused = false;

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        synchronized (o) {
            o.notifyAll();
        }
    }


    //    public EventConsumer(ConcurrentHashMap<String, Event> eventMap, BlockingQueue<Event> sourceEventsQueue) {
//        this.eventMap = eventMap;
//        this.sourceEventsQueue = sourceEventsQueue;
//    }
    public EventConsumer(ConcurrentHashMap<String, Event> eventMap,
                         BlockingQueue<Event> sourceEventsQueue,
                         CyclicBarrier barrier) {
        this.eventMap = eventMap;
        this.sourceEventsQueue = sourceEventsQueue;
        this.barrier = barrier;
    }

    @Override
    public void run() {

        try {

            while (!Thread.currentThread().isInterrupted()) {
                if (!paused) {
                    Event currentEvent = sourceEventsQueue.take();
                    String key = currentEvent.getUserId() + "|" + currentEvent.getSessionId();

                    Event.EventType type = currentEvent.getEventType();

                    if (type.equals(LOGIN)) {
                        eventMap.putIfAbsent(key, currentEvent);
                    } else {
                        eventMap.computeIfPresent(key, (k, v) -> {
                            Long sessionDuration = currentEvent.getTimeStamp() - eventMap.get(key).getTimeStamp();

                            return new Event(sessionDuration,
                                    currentEvent.getEventType(),
                                    currentEvent.getUserId(),
                                    currentEvent.getSessionId());
                        });
                    }
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
