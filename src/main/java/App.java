import consumer.EventConsumer;
import data.Event;
import producer.EventProducer;
import stats.BarrierAction;
import stats.Statistic;

import java.io.IOException;
import java.util.concurrent.*;

public class App {

    private static final String PATH_TO_FILE = "src/main/resources/output.csv";
    private static final int QUEUE_SIZE = 1_000_000;

    public static void main(String[] args) throws InterruptedException, IOException {

        BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        Statistic statistic = new Statistic();
        BarrierAction action = new BarrierAction(statistic);
        CyclicBarrier barrier = new CyclicBarrier(1, action);
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(eventQueue, statistic, barrier);
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(producer);
        service.submit(consumer);

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

