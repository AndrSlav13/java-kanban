package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.net.URI;
import java.net.URISyntaxException;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        URI uri = null;
        try {
            uri = new URI("http://localhost:8080");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return new HttpTaskManager(uri);
    }

    public static TaskManager getHttpTaskManager(URI uri) {
        return new HttpTaskManager(uri);
    }

    public static TaskManager getFileBackedWithInitialData(String title) {
        FileBackedTasksManager fb = new FileBackedTasksManager(title);
        fb.load(title);
        return fb;
    }

    public static TaskManager getFileBacked(String title) {
        return new FileBackedTasksManager(title);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
