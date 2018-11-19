import data.Event;
import stats.StatisticsGatherer;

import java.util.concurrent.*;

public class App {

    private static final String PATH_TO_FILE = "src/main/resources/output.csv";
    private static final int QUEUE_SIZE = 200_000;

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, Event> dataBase = new ConcurrentHashMap<>();
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(dataBase, eventQueue);

        ExecutorService serviceProducer = Executors.newSingleThreadExecutor();
        ExecutorService serviceConsumer = Executors.newSingleThreadExecutor();


        serviceProducer.submit(producer);
        serviceConsumer.submit(consumer);
        StatisticsGatherer statistics = new StatisticsGatherer(dataBase);
        while (true) {
            Thread.sleep(9000);
            System.out.println("Size:" + eventQueue.size());
            consumer.pause();
            System.out.println(statistics.get());
            consumer.resume();

        }

    }
}
