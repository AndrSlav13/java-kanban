package tasks;

import java.util.ArrayList;

public class SubTask extends Task {

    private ArrayList<Integer> idEpic;     //One subtask may be stage of different epic tasks

    public SubTask(SubTask sTask, EpicTask eTask) {
        super(sTask);
        idEpic = new ArrayList<>();
        idEpic.add(eTask.toInt());
    }

    public SubTask(String title, String description, EpicTask eTask) {
        super(title, description);
        idEpic = new ArrayList<>();
        idEpic.add(eTask.toInt());
    }

    public SubTask(SubTask sTask) {
        super(sTask);
        idEpic = new ArrayList<>();
    }

    public void removeReferences() {
        idEpic.clear();
    }

    public ArrayList<Integer> getEpicTasksIDs() {
        return idEpic;
    }

    public void addEpicTaskID(int i) {
        idEpic.add(i);
    }

    public boolean containsEpicTasks() {
        return !idEpic.isEmpty();
    }

    public void removeReferenceToEpicTask(Integer id) {
        idEpic.remove(id);
    }

    @Override
    public String toString() {
        return "\t" + super.toString(); //Indention for subtasks
    }
}
