import interfaces.HistoryManager;
import tasks.Task;
import types.CustomLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList lastTasks = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return lastTasks.getHistory();
    }

    @Override
    public void addHistoryTask(Task task) {
        lastTasks.addLastTask(task);
    }

    @Override
    public void removeHistoryTask(int id) {
        lastTasks.removeTask(id);
    }
}
