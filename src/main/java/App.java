import data.Event;
import stats.StatisticsCollector;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static data.EventUtils.eventMapCleaner;

public class App {

    private static final String PATH_TO_FILE = "src/main/resources/data.csv";
    private static final int QUEUE_SIZE = 200_000;
    private static final long WINDOW_SIZE = 2_000L;

    public static void main(String[] args) throws InterruptedException, IOException, BrokenBarrierException {

        int simulations = 2;
        int iteration = 0;


        BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, Event> eventMap = new ConcurrentHashMap<>();
        StatisticsCollector statistics = new StatisticsCollector(eventMap, WINDOW_SIZE);

        final CyclicBarrier barrier = new CyclicBarrier(2);
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(eventMap, eventQueue, barrier);

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
                System.out.printf(statistics.collect());
                eventMapCleaner(eventMap);
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

