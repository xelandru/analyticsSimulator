import data.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static data.Event.EventType.LOGOUT;
import static data.EventUtils.*;

public class EventConsumer implements Runnable {

    private final ConcurrentHashMap<String, Event> eventDataBase;
    private final BlockingQueue<String> sourceEventsQueue;

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


    public EventConsumer(ConcurrentHashMap<String, Event> eventDataBase, BlockingQueue<String> sourceEventsQueue) {
        this.eventDataBase = eventDataBase;
        this.sourceEventsQueue = sourceEventsQueue;
    }

    @Override
    public void run() {

        try {

            while (!Thread.currentThread().isInterrupted()) {
                if (!paused) {
                    String eventAsString = sourceEventsQueue.take();
                    Event currentEvent = getEventFromString(eventAsString);
                    String key = currentEvent.getUserId() + "|" + currentEvent.getSessionId();
                    Event replaceableEvent = eventDataBase.putIfAbsent(key, currentEvent);
                    if (replaceableEvent != null) {
                        Event updatedEvent = new Event(
                                currentEvent.getTimeStamp() - replaceableEvent.getTimeStamp(),
                                currentEvent.getEventType(),
                                currentEvent.getUserId(),
                                currentEvent.getSessionId());
                        eventDataBase.replace(key, replaceableEvent, updatedEvent);
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
