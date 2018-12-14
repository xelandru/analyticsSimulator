package producer;

import data.Event;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static data.EventUtils.parseEventFromString;

public class EventProducer implements Runnable {

    private final BlockingQueue<Event> eventQueue;
    private final String pathToEventsFile;

    public EventProducer(BlockingQueue<Event> eventQue, String pathToSource) {
        this.eventQueue = eventQue;
        this.pathToEventsFile = pathToSource;
    }

    @Override
    public void run() {
        Path pathToFile = Paths.get(pathToEventsFile).toAbsolutePath();
        MyTailerListener listener = new MyTailerListener();
        Tailer tailer = new Tailer(pathToFile.toFile(), listener,250);
        tailer.run();
        tailer.stop();
    }

    private class MyTailerListener extends TailerListenerAdapter {
        public void handle(String line) {
            if (line != null && !(line.isEmpty())) {
                Optional<Event> optionalEvent = parseEventFromString(line);
                optionalEvent.ifPresent(event -> {
                    try {
                        eventQueue.put(event);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
