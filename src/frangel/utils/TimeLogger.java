package frangel.utils;

import java.util.*;

import frangel.Settings;

public class TimeLogger {
    private static final boolean CAREFUL_LOGGING = false;

    private static Map<String, Long> totals = new HashMap<String, Long>();
    private static Map<String, Long> last = new HashMap<String, Long>(); // -1 if not started
    private static Set<String> current = new HashSet<String>();
    private static long timerTime = 0;

    public static void start(String name) {
        if (!Settings.LOG_TIMING)
            return;
        long now = System.nanoTime();
        last.put(name, now);
        if (CAREFUL_LOGGING) {
            if (current.contains(name))
                System.err.println("Trying to start timer, but already started: " + name);
            if (current.size() >= 2)
                System.err.println("Timers running: " + current + ", trying to start: " + name);
            current.add(name);
        }
        timerTime += System.nanoTime() - now;
    }

    public static void stop(String name) {
        if (!Settings.LOG_TIMING)
            return;
        long now = System.nanoTime();
        totals.put(name, totals.getOrDefault(name, 0L) + now - last.get(name));
        if (CAREFUL_LOGGING) {
            if (!current.contains(name))
                System.out.println("Trying to stop timer, but isn't running: " + name);
            current.remove(name);
        }
        timerTime += System.nanoTime() - now;
    }

    public static void printLog() {
        if (!Settings.LOG_TIMING)
            return;
        System.out.println("------------------------------\n\nTiming breakdown:\n");
        totals.put("TimeLogger", timerTime);
        List<String> sortedNames = new ArrayList<String>(totals.keySet());
        Collections.sort(sortedNames, new Comparator<String>() {
            @Override
            public int compare(final String a, String b) {
                return totals.get(b).compareTo(totals.get(a));
            }
        });
        for (String name : sortedNames)
            System.out.printf("%9.2f sec, %5.1f%%: %s\n", totals.get(name) / 1.0e9, 100.0 * totals.get(name) / totals.get("Total"), name);
    }
}
