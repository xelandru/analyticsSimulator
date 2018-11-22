import data.Event;
import data.EventUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import stats.StatisticsGatherer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static data.Event.EventType.LOGOUT;

public class App {

    private static final String PATH_TO_FILE = "src/main/resources/output.csv";
    private static final int QUEUE_SIZE = 200_000;
    private static final long WINDOW_SIZE = 10_000L;

    public static void main(String[] args) throws InterruptedException, IOException {

        int simulations = 10;
        int iteration = 0;


        BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, Event> dataBase = new ConcurrentHashMap<>();
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(dataBase, eventQueue);

        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(producer);
        service.submit(consumer);
        StatisticsGatherer statistics = new StatisticsGatherer(dataBase, WINDOW_SIZE);

        Instant start, end;
        end = start = Instant.now();
        long timeElapsed;

        while (iteration < simulations) {
            timeElapsed = Duration.between(start, end).toMillis();
            if (timeElapsed > WINDOW_SIZE) {
                consumer.pause();
                System.out.println(statistics.get(iteration));
                consumer.resume();
                iteration++;
                start = end = Instant.now();
            } else {
                end = Instant.now();
            }
        }
        service.shutdown();
        try {
            if (!service.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }
}

