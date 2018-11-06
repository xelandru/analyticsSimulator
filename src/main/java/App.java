import data.Event;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class App {

    private static final String PATH_TO_FILE = "/home/adascalu/Desktop/analyticsSimulator/src/main/resources/data.csv";
    private static final int QUEUE_SIZE = 200_000;

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, Event> dataBase = new ConcurrentHashMap<>();
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        EventConsumer consumer = new EventConsumer(dataBase, eventQueue);
        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(consumer);
        t1.start();
        t2.start();

        while (true) {
         Thread.sleep(2000);
            dataBase.forEach((key, value) -> System.out.println(key+":"+ value));
//            dataBase.forEach(dataBase::remove);
            System.out.println("--------------------------------------------------------------------------------------");
        }
    }
}
