package tools.bindiff.utils;

import java.util.HashMap;

/**
 * Created by ptasha on 14/02/15.
 */
public class Timer {
    private long time;

    private final HashMap<String, Long> map = new HashMap<String, Long>();

    public Timer() {
        start();
    }

    private void start() {
        time = System.currentTimeMillis();
    }

    public void time(String name) {
        long curr = System.currentTimeMillis();
        Long last = map.get(name);
        if (last==null) {
            last = 0l;
        }
        map.put(name, last+curr-time);
        start();
    }

    public void print() {
        System.out.println(map);
    }
}
