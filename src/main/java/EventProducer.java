import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

public class EventProducer implements Runnable {

    private final BlockingQueue<String> eventQueue;
    private final String pathToEventsFile;



    public EventProducer(BlockingQueue<String> eventQue, String pathToSource) {
        this.eventQueue = eventQue;
        this.pathToEventsFile = pathToSource;
    }

//    @Override
//    public void run() {
//        Path pathToFile = Paths.get(pathToEventsFile).toAbsolutePath();
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

    @Override
    public void run() {
        Path pathToFile = Paths.get(pathToEventsFile).toAbsolutePath();

        MyTailerListener listener = new MyTailerListener();
        Tailer tailer = new Tailer(pathToFile.toFile(), listener, 0L);

        tailer.run();

    }

    private  class MyTailerListener extends TailerListenerAdapter {
        public void handle(String line) {
            if (line != null && !(line.isEmpty())) {
                try {
                    eventQueue.put(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
