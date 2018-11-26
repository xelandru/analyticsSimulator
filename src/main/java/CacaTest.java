import data.Event;
import data.EventUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

/**
 * Created by lex on 11/23/18.
 */
public class CacaTest {
    public static void main(String[] args) throws IOException {
        String source = "/home/lex/Desktop/analyticsSimulator/src/main/resources/data.csv";
        Path path = Paths.get(source);
        List<String> strings = Files.readAllLines(path);
        final List<Event> allEvents = new ArrayList<>();
        allEvents.add(null);
        strings.forEach(s -> allEvents.add(EventUtils.getEventFromString(s)));

        int duration = 10;
        int wSize = 100;
        long[] windows = new long[(allEvents.size() - 1) / wSize + 1];

        for (int i = 1; i < allEvents.size(); i++) {
            Event startEvent = allEvents.get(i);
            int startW = getWindow(wSize, i);
            if (startEvent.getEventType().equals(LOGIN)) {
                for (int j = i + 1; j < allEvents.size(); j++) {
                    Event currentEvent = allEvents.get(j);
                    if (currentEvent.getEventType().equals(LOGOUT)) {
                        if (currentEvent.equals(startEvent)) {
                            int currentW = getWindow(wSize, j);
                            windows[currentW] += currentEvent.getTimeStamp() - startEvent.getTimeStamp();
                            for (int k = startW; k < currentW; k++) {
                                windows[k] += (k - startW + 1) * duration - startEvent.getTimeStamp();
                            }
                            break;
                        }
                    }
                }
            }
        }
        List<Long> list = Arrays.stream(windows).boxed().collect(Collectors.toList());
        System.out.println(list);


    }

    static int getWindow(int windowSize, int pos) {

        if (pos % windowSize == 0)
            return pos / windowSize;
        return pos / windowSize + 1;
    }
}
