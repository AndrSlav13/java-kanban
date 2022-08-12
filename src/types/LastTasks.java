package types;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LastTasks {
    private static final int NUM_TASKS_TO_SAVE = 10;
    private LinkedList<Task> lastTasks = new LinkedList<>();

    public void addTask(Task task) {
        if (lastTasks.size() == NUM_TASKS_TO_SAVE) lastTasks.removeLast();
        lastTasks.addFirst(task);
    }

    public List<Task> getHistory() {
        return new ArrayList<>(lastTasks);
    }
}