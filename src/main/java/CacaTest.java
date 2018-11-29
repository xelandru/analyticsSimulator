import data.Event;
import data.EventUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static data.Event.EventType.LOGIN;
import static data.Event.EventType.LOGOUT;

/**
 * Created by lex on 11/23/18.
 */
public class CacaTest {
    public static void main(String[] args) throws IOException {
        String source = "src/main/resources/data.csv";
        Path path = Paths.get(source).toAbsolutePath();
        List<String> strings = Files.readAllLines(path);
        final List<Event> allEvents = new ArrayList<>();
        allEvents.add(null);
        strings.forEach(s -> allEvents.add(EventUtils.getEventFromString(s)));
        int wSize = 1000;

        long[] time = nominalTime(allEvents, wSize);
        long[] session = sessionPerWindow(allEvents, wSize);
        double[] avg = new double[time.length];

        for (int i = 1; i < session.length; i++) {
            avg[i] = (double) time[i] / session[i];
            System.out.printf("%.3f ", avg[i]);
        }


    }


    static long[] sessionPerWindow(List<Event> allEvents, int wSize) {
        long[] windows = new long[(allEvents.size() - 1) / wSize + 1];

        int logOut = 0;
        int userPerWindow = 0;
        int num = 1;
        int j = 1;

        while (num * wSize <= allEvents.size()) {
            for (; j <= num * wSize; j++) {
                if (allEvents.get(j).getEventType().equals(LOGIN))
                    userPerWindow++;
                else
                    logOut++;
            }
            windows[num] = userPerWindow;
            userPerWindow -= logOut;
            logOut = 0;
            j = (num * wSize) + 1;
            num++;
        }
        List<Long> list = Arrays.stream(windows).boxed().collect(Collectors.toList());
        System.out.println(list);
        return windows;
    }


    static boolean match(Event e1, Event e2) {

        return e1.getUserId().concat(e1.getSessionId())
                .equals(e2.getUserId().concat(e2.getSessionId()));
    }

    static int getWindow(int windowSize, int pos) {

        if (pos % windowSize == 0)
            return pos / windowSize;
        return pos / windowSize + 1;
    }

    static long[] nominalTime(List<Event> allEvents, int wSize) {
        int duration = 10;
        long[] windows = new long[(allEvents.size() - 1) / wSize + 1];


        for (int i = 1; i < allEvents.size(); i++) {
            Event startEvent = allEvents.get(i);
            int startW = getWindow(wSize, i);
            if (startEvent.getEventType().equals(LOGIN)) {
                for (int j = i + 1; j < allEvents.size(); j++) {
                    Event currentEvent = allEvents.get(j);
                    if (currentEvent.getEventType().equals(LOGOUT)) {
                        if (match(currentEvent, startEvent)) {
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
        return windows;
    }


}
