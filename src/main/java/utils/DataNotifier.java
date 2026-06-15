package utils;

import java.util.ArrayList;
import java.util.List;

public class DataNotifier {

    private static DataNotifier instance;
    private final List<Runnable> listeners = new ArrayList<>();

    private DataNotifier() {
    }

    public static DataNotifier getInstance() {
        if (instance == null) {
            instance = new DataNotifier();
        }
        return instance;
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void notifyDataChanged() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
