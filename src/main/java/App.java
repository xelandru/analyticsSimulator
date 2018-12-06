import data.Event;
import stats.StatisticsCollector;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

import static data.EventUtils.cleanLogoutEventsFromMap;

public class App {

    private static final String PATH_TO_FILE = "src/main/resources/output.csv";
    private static final int QUEUE_SIZE = 1_000_000;
    private static final long WINDOW_SIZE = 10_000L;

    public static void main(String[] args) throws InterruptedException, IOException {

        int simulations = 20;
        int iteration = 0;


        BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, Event> eventMap = new ConcurrentHashMap<>();
        StatisticsCollector statistics = new StatisticsCollector(eventMap, WINDOW_SIZE);

        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(eventMap, eventQueue);

        ExecutorService service = Executors.newFixedThreadPool(2);

        service.submit(producer);
        service.submit(consumer);

        Instant start, end;
        end = start = Instant.now();
        long timeElapsed;


        while (iteration < simulations) {
            timeElapsed = Duration.between(start, end).toMillis();
            if (timeElapsed > WINDOW_SIZE) {
                consumer.pause();
                System.out.println(statistics.collect());
                cleanLogoutEventsFromMap(eventMap);
                consumer.resume();
                iteration++;
                start = end = Instant.now();
            } else {
                end = Instant.now();
            }
        }

        System.out.println("Unique: " + statistics.getUniqueUsers());
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

