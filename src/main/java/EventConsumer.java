import data.Event;

import java.util.concurrent.*;

import static data.Event.EventType.*;

public class EventConsumer implements Runnable {

    private final ConcurrentHashMap<String, Event> eventMap;
    private final BlockingQueue<Event> sourceEventsQueue;


    private final Object o = new Object();
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


        public EventConsumer(ConcurrentHashMap<String, Event> eventMap, BlockingQueue<Event> sourceEventsQueue) {
        this.eventMap = eventMap;
        this.sourceEventsQueue = sourceEventsQueue;
    }

    @Override
    public void run() {

        try {

            while (!Thread.currentThread().isInterrupted()) {
                if (!paused) {
                    Event queueEvent = sourceEventsQueue.take();
                    String key = queueEvent.getUserId() + "|" + queueEvent.getSessionId();

                    Event.EventType type = queueEvent.getEventType();

                    if (type.equals(LOGIN)) {
                        eventMap.putIfAbsent(key, queueEvent);
                    } else {
                        eventMap.computeIfPresent(key, (k, mapEvent) -> {
                            Long sessionDuration = queueEvent.getTimeStamp() - mapEvent.getTimeStamp();

                            return new Event(sessionDuration,
                                    queueEvent.getEventType(),
                                    queueEvent.getUserId(),
                                    queueEvent.getSessionId());
                        });
                    }
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
