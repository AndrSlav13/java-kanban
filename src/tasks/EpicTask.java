package tasks;

import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<Integer> subTasks;

    public EpicTask(final String title, final String description) {
        super(title, description);
        subTasks = new ArrayList<>();
    }

    public EpicTask(final String title) {
        this(title, "");
    }

    public EpicTask(EpicTask eTask) {
        super(eTask);
        subTasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIDs() {
        return subTasks;
    }

    public void removeReferenceToSubTask(Integer id) {
        subTasks.remove(id);
    }

    public void removeReferences() {
        subTasks.clear();
    }

    public void deleteSubTask(int index) {   //Remove subtask by index
        if (index <= subTasks.size() && index > 0)
            subTasks.remove(index - 1);
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
