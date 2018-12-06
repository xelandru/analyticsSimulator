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
    //    @Override
//    public void run() {
//        Path pathToFile = Paths.collect(pathToEventsFile).toAbsolutePath();
//        String line;
//
//        try (
//                InputStream in = Files.newInputStream(pathToFile);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in))
//        ) {
//            while (!Thread.currentThread().isInterrupted()) {
//                line = reader.readLine();
//                if (line != null && !(line.isEmpty())) {
//                    eventQueue.put(line);
//                }
//            }
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            Thread.currentThread().interrupt();
//        }
//    }
}
