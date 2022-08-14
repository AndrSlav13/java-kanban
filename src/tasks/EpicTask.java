package tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTasks = new ArrayList<>();

    public EpicTask(final String title, final String description) {
        super(title, description);
    }

    public EpicTask(final String title) {
        this(title, "");
    }

    public List<Integer> getSubTasksIDs() {
        return subTasks;
    }

    public void removeReferenceToSubTask(Integer id) {
        subTasks.remove(id);
    }

    public void removeReferences() {
        subTasks.clear();
    }

    @Override
    public void update(Task task) {
        this.setDescription(task.getDescription());
        this.setTitle(task.getTitle());
    }

    public void addSubTaskID(int i) {
        subTasks.add(i);
    }

    public boolean containsSubTasks() {
        return !subTasks.isEmpty();
    }

    @Override
    public String toString() {
        String out = super.toString();
        return out;
    }
}
