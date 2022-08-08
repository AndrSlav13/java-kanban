/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {
    private HashMap<Integer, Task> tasksStore = new HashMap<>();  //A task to be staged
    private HashMap<Integer, EpicTask> epicTasksStore = new HashMap<>();  //A task to be staged
    private HashMap<Integer, SubTask> subTasksStore = new HashMap<>();    //Stages to do

    public Task getTask(final int id) {
        if (tasksStore.containsKey(id)) return tasksStore.get(id);
        if (epicTasksStore.containsKey(id)) return epicTasksStore.get(id);
        if (subTasksStore.containsKey(id)) return subTasksStore.get(id);
        return null;
    }

    private int genID(Task task) {
        int id = Objects.hash(task.getTitle(), task.getDescription());
        while (getTask(id) != null || id == 0) ++id; //id==0 is invalid
        return id;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (EpicTask eTask : epicTasksStore.values()) {
            out.add(eTask);
            for (int i : eTask.getSubTasksIDs())
                out.add(subTasksStore.get(i));
        }
        out.addAll(getTasks());
        return out;
    }

    public ArrayList<Task> getEpicTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (Task task : epicTasksStore.values())
            out.add(task);
        return out;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (Task task : tasksStore.values())
            out.add(task);
        return out;
    }

    public ArrayList<Task> getSubTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (Task task : subTasksStore.values())
            out.add(task);
        return out;
    }

    public ArrayList<Task> getSubTasks(EpicTask eTask) {
        ArrayList<Task> out = new ArrayList<>();

        EpicTask epicTask = epicTasksStore.get(eTask.toInt());
        if (epicTask == null) return out;
        for (int i : epicTask.getSubTasksIDs())
            out.add(subTasksStore.get(i));

        return out;
    }

    public void addTask(Task task) {   //Epic-task insertion
        task.setID(genID(task));
        Task simpleTask = new Task(task.getTitle(), task.getDescription());
        simpleTask.setID(task.toInt());
        tasksStore.put(task.toInt(), simpleTask);
    }

    public void addEpicTask(EpicTask eTask) {   //Epic-task insertion
        eTask.setID(genID(eTask));
        EpicTask epicTask = new EpicTask(eTask.getTitle(), eTask.getDescription());
        epicTask.setID(eTask.toInt());
        epicTasksStore.put(eTask.toInt(), epicTask);
    }

    public void addSubTask(SubTask sTask) { //Subtask insertion
        int idEpic = sTask.getEpicTaskID();
        if (!epicTasksStore.containsKey(idEpic)) return;
        EpicTask eTask = epicTasksStore.get(idEpic);
        sTask.setID(genID(sTask));
        SubTask subTask = new SubTask(sTask.getTitle(), sTask.getDescription(), idEpic);
        subTask.setID(sTask.toInt());
        subTasksStore.put(sTask.toInt(), subTask);
        eTask.addSubTaskID(subTask.toInt());
        update(subTask);
    }

    public void deleteTasks() {
        tasksStore.clear();
    }

    public void deleteEpicTasks() {
        for (int i : epicTasksStore.keySet()) {
            EpicTask eTask = epicTasksStore.get(i);
            if (!eTask.containsSubTasks()) continue;
            for (int j : eTask.getSubTasksIDs())
                subTasksStore.remove(j);
            eTask.removeReferences();
        }
        epicTasksStore.clear();
    }

    public void deleteSubTasks() {
        subTasksStore.clear();
        for (EpicTask eTask : epicTasksStore.values())
            eTask.removeReferences();
    }

    public void deleteTask(final int id) {
        Task simpleTask;
        EpicTask eTask;
        SubTask sTask;
        eTask = epicTasksStore.get(id);
        if (eTask != null) {
            for (int i : eTask.getSubTasksIDs())
                subTasksStore.remove(i);
            eTask.removeReferences();
            epicTasksStore.remove(id);
        }
        sTask = subTasksStore.get(id);
        if (sTask != null) {
            eTask = epicTasksStore.get(sTask.getEpicTaskID());
            eTask.removeReferenceToSubTask(sTask.getEpicTaskID());
            subTasksStore.remove(id);
        }
        simpleTask = tasksStore.get(id);
        if (simpleTask != null) {
            tasksStore.remove(id);
        }
    }

    public void deleteSubTask(EpicTask epicTask, SubTask subTask) {    //delete subtask by id EpicTask + id SubTask
        EpicTask eTask;
        eTask = epicTasksStore.get(epicTask.toInt());
        if (eTask == null) return;

        eTask.removeReferenceToSubTask(subTask.toInt());
        subTasksStore.remove(subTask.toInt());

        ArrayList<Integer> subTasks = eTask.getSubTasksIDs();
        if (!subTasks.isEmpty()) update(getTask(subTasks.get(0)));
    }

    public void deleteSubTask(EpicTask epicTask, final int indexSubTask) {  //delete subtask by index
        EpicTask eTask;
        eTask = epicTasksStore.get(epicTask.toInt());
        if (eTask == null) return;

        int indSubTask = eTask.getSubTasksIDs().get(indexSubTask - 1);
        eTask.deleteSubTask(indexSubTask);
        subTasksStore.remove(indSubTask);

        ArrayList<Integer> subTasks = eTask.getSubTasksIDs();
        if (!subTasks.isEmpty()) update(getTask(subTasks.get(0)));
    }

    private void setStatusEpic(final int id, final TaskType status) {
        EpicTask eTask = epicTasksStore.get(id);
        eTask.setStatus(status);

        if (status == TaskType.DONE) {
            for (int i : eTask.getSubTasksIDs()) {
                SubTask subTask = subTasksStore.get(i);
                subTask.setStatus(TaskType.DONE);
            }
            return;
        }
        if (status == TaskType.NEW) {
            for (int i : eTask.getSubTasksIDs()) {
                SubTask subTask = subTasksStore.get(i);
                subTask.setStatus(TaskType.NEW);
            }
            return;
        }
    }

    private void setStatusSubTask(final int id, final TaskType status) {
        SubTask sTask = subTasksStore.get(id);
        sTask.setStatus(status);

        if (status == TaskType.DONE) {
            EpicTask eTask = epicTasksStore.get(sTask.getEpicTaskID());
            if (!isEachStatusSubTasksThis(eTask, TaskType.DONE)) eTask.setStatus(TaskType.IN_PROGRESS);
            else eTask.setStatus(TaskType.DONE);
        }

        if (status == TaskType.NEW) {
            EpicTask eTask = epicTasksStore.get(sTask.getEpicTaskID());
            if (!isEachStatusSubTasksThis(eTask, TaskType.NEW)) eTask.setStatus(TaskType.IN_PROGRESS);
            else eTask.setStatus(TaskType.NEW);
        }

    }

    public void update(Task task) {
        if (epicTasksStore.values().contains(task)) {
            EpicTask eTask = epicTasksStore.get(task.toInt());
            eTask.update(task);
            setStatusEpic(eTask.toInt(), task.getStatus());
        }
        if (subTasksStore.values().contains(task)) {
            SubTask sTask = subTasksStore.get(task.toInt());
            sTask.update(task);
            setStatusSubTask(sTask.toInt(), task.getStatus());
        }
        if (tasksStore.values().contains(task)) {
            Task simpleTask = tasksStore.get(task.toInt());
            simpleTask.update(task);
        }
    }

    private boolean isEachStatusSubTasksThis(EpicTask eTask, final TaskType status) {
        for (int i : eTask.getSubTasksIDs()) {
            TaskType ss = subTasksStore.get(i).getStatus();
            if (subTasksStore.get(i).getStatus() != status) {
                return false;
            }
        }
        return true;
    }

}