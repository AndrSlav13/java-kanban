package interfaces;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addHistoryTask(Task task);

    List<Task> getHistory();

    void removeHistoryTask(int id);
}
