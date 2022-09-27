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
        return new ArrayList<Integer>(subTasks);
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

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        int i1 = sb.indexOf(",");
        int i2 = sb.indexOf(",", i1 + 1);
        sb.replace(i1 + 1, i2, "EPICTASK");
        return sb.toString();
    }
}
