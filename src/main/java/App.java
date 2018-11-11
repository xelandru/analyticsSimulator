import data.Event;
import stats.StatisticsGatherer;

import java.util.Iterator;
import java.util.concurrent.*;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

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


//        Event event1 = new Event(0, LOGIN, "user-93", "6a87b19b-7aed-45a7-b960-5d3830923a79");
//        Event event2 = new Event(0, LOGIN, "user-168", "b65a919d-7456-4eea-b65d-3c2e91ae9272");
//        Event event3 = new Event(0, LOGIN, "user-186", "0cd66c6c-8f58-410c-8b1b-a465d3b43b24");
//        Event event4 = new Event(0, LOGIN, "user-460", "14742a0a-f822-4cfb-a282-be5966349642");
//        Event event31 = new Event(1, LOGIN, "user-186", "0cd66c6c-8f58-410c-8b1b-a465d3b43b25");
//        Event event311 = new Event(2, LOGOUT, "user-186", "0cd66c6c-8f58-410c-8b1b-a465d3b43b24");
//        Event event11 = new Event(4, LOGOUT, "user-93", "6a87b19b-7aed-45a7-b960-5d3830923a79");
//        Event event21 = new Event(7, LOGOUT, "user-168", "b65a919d-7456-4eea-b65d-3c2e91ae9272");
//
//
//        dataBase.put(event1.getUserId() + "|" + event1.getSessionId(), event1);
//        dataBase.put(event2.getUserId() + "|" + event2.getSessionId(), event2);
//        dataBase.put(event3.getUserId() + "|" + event3.getSessionId(), event3);
//        dataBase.put(event4.getUserId() + "|" + event4.getSessionId(), event4);
//        dataBase.put(event11.getUserId() + "|" + event11.getSessionId(), event11);
//        dataBase.put(event21.getUserId() + "|" + event21.getSessionId(), event21);
//        dataBase.put(event311.getUserId() + "|" + event311.getSessionId(), event311);
//        dataBase.put(event31.getUserId() + "|" + event31.getSessionId(), event31);

        StatisticsGatherer gatherer = new StatisticsGatherer(dataBase);

        while (true) {
            Thread.sleep(3000);
            System.out.println(gatherer.get());
        }


    }
}
