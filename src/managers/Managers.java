package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedWithInitialData(String title) {
        return FileBackedTasksManager.loadFromFile(title);
    }

    public static TaskManager getFileBacked(String title) {
        return new FileBackedTasksManager(title);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
