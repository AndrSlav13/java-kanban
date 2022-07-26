package managers;

import errors.FunctionParameterException;
import interfaces.HistoryManager;
import tasks.Task;
import util.CustomLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList lastTasks = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return lastTasks.getHistory();
    }

    @Override
    public void addHistoryTask(Task task) {
        if (task == null) throw new FunctionParameterException("wrong param");
        lastTasks.addLastTask(task);
    }

    @Override
    public void removeHistoryTask(int id) {
        lastTasks.removeTask(id);
    }
}
