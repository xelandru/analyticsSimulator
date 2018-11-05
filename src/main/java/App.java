import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class App {

    private static final String PATH_TO_FILE = "/home/lex/Desktop/analyticsSimulator/src/main/resources/test.txt";
    private static final int QUEUE_SIZE = 200_000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        EventProducer producer = new EventProducer(eventQueue, PATH_TO_FILE);
        Thread thread = new Thread(producer);
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        System.out.println(map.put("key", "val1"));
        System.out.println(map.put("key", "val2"));
    }
}
