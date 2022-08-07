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
    private HashMap<Integer, EpicTask> tasksStore = new HashMap<>();  //A task to be staged
    private HashMap<Integer, SubTask> subTasksStore = new HashMap<>();    //Stages to do

    //По ТЗ конкретный формат хранения задач не указан -
    // используются 2 HashMap для хранения эпик-тасков и подзадач. Простая задача рассматривается
    //как эпик-таск без подзадач. Иначе возникает несоответствие:
    //если добавил эпик-таск без подзадач, то формально он является не эпик-таском
    //Т.е. класс Task здесь используется только как абстрактный предок.
    //"tasksStore" - общая коллекция для тасков и эпик-тасков, различаются по наличию подзадач

    public Task getTask(final int id) {
        if (tasksStore.containsKey(id)) return tasksStore.get(id);
        if (subTasksStore.containsKey(id)) return subTasksStore.get(id);
        return null;
    }

    private int genID(Task task) {
        int id = Objects.hash(task.getTitle(), task.getDescription());
        while (getTask(id) != null || id == 0) ++id; //id==0 is invalid
        return id;
    }

    public ArrayList<Task> getEpicTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (EpicTask eTask : tasksStore.values())
            if (eTask.containsSubTasks())
                out.add(eTask);
        return out;
    }

    public ArrayList<Task> getNotStagedTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (EpicTask eTask : tasksStore.values())
            if (!eTask.containsSubTasks())
                out.add(eTask);
        return out;
    }

    public ArrayList<Task> getSubTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (SubTask subTask : subTasksStore.values())
            out.add(subTask);
        return out;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> out = new ArrayList<>();
        for (EpicTask eTask : tasksStore.values()) {
            out.add(eTask);
            for (int i : eTask.getSubTasksIDs())
                out.add(subTasksStore.get(i));
        }
        return out;
    }

    public ArrayList<Task> getSubTasks(EpicTask eTask) {
        ArrayList<Task> out = new ArrayList<>();

        EpicTask epicTask = tasksStore.get(eTask.toInt());
        if (epicTask == null) return out;
        for (int i : epicTask.getSubTasksIDs())
            out.add(subTasksStore.get(i));

        return out;
    }

    public void addTask(EpicTask eTask) {   //Epic-task insertion
        eTask.setID(genID(eTask));
        EpicTask epicTask = new EpicTask(eTask.getTitle(), eTask.getDescription());
        epicTask.setID(eTask.toInt());
        tasksStore.put(eTask.toInt(), epicTask);
    }

    public void addSubTask(SubTask sTask) { //Subtask insertion
        int idEpic = sTask.getEpicTaskID();
        if (!tasksStore.containsKey(idEpic)) return;
        EpicTask eTask = tasksStore.get(idEpic);
        sTask.setID(genID(sTask));
        SubTask subTask = new SubTask(sTask.getTitle(), sTask.getDescription(), idEpic);
        subTask.setID(sTask.toInt());
        subTasksStore.put(sTask.toInt(), subTask);
        eTask.addSubTaskID(subTask.toInt());
        update(subTask);
    }

    public void deleteNotStagedTasks() {
        ArrayList<Integer> del = new ArrayList<>();
        for (int i : tasksStore.keySet()) {
            if (!tasksStore.get(i).containsSubTasks())
                del.add(i);
        }
        for (int i : del) tasksStore.remove(i);
    }

    public void deleteEpicTasks() {
        ArrayList<Integer> del = new ArrayList<>();
        for (int i : tasksStore.keySet()) {
            EpicTask eTask = tasksStore.get(i);
            if (!eTask.containsSubTasks()) continue;
            for (int j : eTask.getSubTasksIDs())
                subTasksStore.remove(j);
            eTask.removeReferences();
            del.add(i);
        }
        for (int i : del) tasksStore.remove(i);
    }

    public void deleteSubTasks() {
        subTasksStore.clear();
        for (EpicTask eTask : tasksStore.values())
            eTask.removeReferences();
    }

    public void deleteAllTasks() {
        subTasksStore.clear();
        for (EpicTask eTask : tasksStore.values())
            eTask.removeReferences();
        tasksStore.clear();
    }

    public void deleteTask(final int id) {
        EpicTask eTask;
        SubTask sTask;
        eTask = tasksStore.get(id);
        if (eTask != null) {
            for (int i : eTask.getSubTasksIDs())
                subTasksStore.remove(i);
            eTask.removeReferences();
            tasksStore.remove(id);
        }
        sTask = subTasksStore.get(id);
        if (sTask != null) {
            eTask = tasksStore.get(sTask.getEpicTaskID());
            eTask.removeReferenceToSubTask(sTask.getEpicTaskID());
            subTasksStore.remove(id);
        }
    }

    public void deleteSubTask(EpicTask epicTask, SubTask subTask) {    //delete subtask by id EpicTask + id SubTask
        EpicTask eTask;
        eTask = tasksStore.get(epicTask.toInt());
        if (eTask == null) return;

        eTask.removeReferenceToSubTask(subTask.toInt());
        subTasksStore.remove(subTask.toInt());

        ArrayList<Integer> subTasks = eTask.getSubTasksIDs();
        if (!subTasks.isEmpty()) update(getTask(subTasks.get(0)));
    }

    public void deleteSubTask(EpicTask epicTask, final int indexSubTask) {  //delete subtask by index
        EpicTask eTask;
        eTask = tasksStore.get(epicTask.toInt());
        if (eTask == null) return;

        int indSubTask = eTask.getSubTasksIDs().get(indexSubTask - 1);
        eTask.deleteSubTask(indexSubTask);
        subTasksStore.remove(indSubTask);

        ArrayList<Integer> subTasks = eTask.getSubTasksIDs();
        if (!subTasks.isEmpty()) update(getTask(subTasks.get(0)));
    }

    private void setStatusEpic(final int id, final TaskType status) {
        EpicTask eTask = tasksStore.get(id);
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
            EpicTask eTask = tasksStore.get(sTask.getEpicTaskID());
            if (!isEachStatusSubTasksThis(eTask, TaskType.DONE)) eTask.setStatus(TaskType.IN_PROGRESS);
            else eTask.setStatus(TaskType.DONE);
        }

        if (status == TaskType.NEW) {
            EpicTask eTask = tasksStore.get(sTask.getEpicTaskID());
            if (!isEachStatusSubTasksThis(eTask, TaskType.NEW)) eTask.setStatus(TaskType.IN_PROGRESS);
            else eTask.setStatus(TaskType.NEW);
        }

    }

    public void update(Task task) {
        if (tasksStore.values().contains(task)) {
            EpicTask eTask = tasksStore.get(task.toInt());
            eTask.update(task);
            setStatusEpic(eTask.toInt(), task.getStatus());
        }
        if (subTasksStore.values().contains(task)) {
            SubTask sTask = subTasksStore.get(task.toInt());
            sTask.update(task);
            setStatusSubTask(sTask.toInt(), task.getStatus());
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