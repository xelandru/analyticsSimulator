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

    @Override
    public void run() {
        Path pathToFile = Paths.get(pathToEventsFile);
        String line;

        try (
                InputStream in = Files.newInputStream(pathToFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))
        ) {
            while (true) {
                line = reader.readLine();
                if (line != null)
                    eventQueue.put(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
