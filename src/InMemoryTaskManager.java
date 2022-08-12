/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import interfaces.HistoryManager;
import interfaces.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    private final HashMap<Integer, Task> tasksStore = new HashMap<>();  //Not Staged task
    private final HashMap<Integer, EpicTask> epicTasksStore = new HashMap<>();  //A task to be staged
    private final HashMap<Integer, SubTask> subTasksStore = new HashMap<>();    //Stages to do

    private final HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void add(Task task) {
        historyManager.add(task);
    }

    private Task getTaskCheckHistoryInserted(final int id, boolean ins) {
        Task task;
        if (tasksStore.containsKey(id)) {
            task = tasksStore.get(id);
            if (ins == true) historyManager.add(task);
            return task;
        }
        if (epicTasksStore.containsKey(id)) {
            task = epicTasksStore.get(id);
            if (ins == true) historyManager.add(task);
            return task;
        }
        if (subTasksStore.containsKey(id)) {
            task = subTasksStore.get(id);
            if (ins == true) historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Task getTask(final int id) {
        return getTaskCheckHistoryInserted(id, true);
    }

    private int genID(Task task) {
        int id = Objects.hash(task.getTitle(), task.getDescription());
        while (getTaskCheckHistoryInserted(id, false) != null || id == 0) ++id; //id==0 is invalid
        return id;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> out = new ArrayList<>();
        for (EpicTask eTask : epicTasksStore.values()) {
            out.add(eTask);
            for (int i : eTask.getSubTasksIDs())
                out.add(subTasksStore.get(i));
        }
        out.addAll(getTasks());
        return out;
    }

    @Override
    public List<Task> getEpicTasks() {
        return new ArrayList<>(epicTasksStore.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksStore.values());
    }

    @Override
    public List<Task> getSubTasks() {
        return new ArrayList<>(subTasksStore.values());
    }

    @Override
    public List<Task> getSubTasks(int idEpic) {
        List<Task> out = new ArrayList<>();

        EpicTask epicTask = epicTasksStore.get(idEpic);
        if (epicTask == null) return out;
        for (int i : epicTask.getSubTasksIDs())
            out.add(subTasksStore.get(i));

        return out;
    }

    @Override
    public void addTask(Task task) {   //Simple-task insertion
        task.setID(genID(task));
        tasksStore.put(task.toInt(), task);
    }

    @Override
    public void addEpicTask(EpicTask eTask) {   //Epic-task insertion
        eTask.setID(genID(eTask));
        epicTasksStore.put(eTask.toInt(), eTask);
    }

    @Override
    public void addSubTask(SubTask sTask) { //Subtask insertion
        int idEpic = sTask.getEpicTaskID();
        if (!epicTasksStore.containsKey(idEpic)) return;
        EpicTask eTask = epicTasksStore.get(idEpic);
        sTask.setID(genID(sTask));
        subTasksStore.put(sTask.toInt(), sTask);
        eTask.addSubTaskID(sTask.toInt());
        update(getTaskCheckHistoryInserted(sTask.toInt(), false));
    }

    @Override
    public void deleteTasks() {
        tasksStore.clear();
    }

    @Override
    public void deleteEpicTasks() {
        subTasksStore.clear();
        epicTasksStore.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasksStore.clear();
        for (EpicTask eTask : epicTasksStore.values()) {
            eTask.removeReferences();
            eTask.setStatus(TaskType.NEW);
        }
    }

    @Override
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
            eTask.removeReferenceToSubTask(sTask.toInt());
            subTasksStore.remove(id);
            List<Integer> subTasks = eTask.getSubTasksIDs();
            if (!subTasks.isEmpty()) update(getTaskCheckHistoryInserted(subTasks.get(0), false));
            else eTask.setStatus(TaskType.NEW);
        }
        simpleTask = tasksStore.get(id);
        if (simpleTask != null) {
            tasksStore.remove(id);
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

    @Override
    public void update(Task task) {
        if (epicTasksStore.containsKey(task.toInt())) {
            EpicTask eTask = epicTasksStore.get(task.toInt());
            eTask.update(task);
        }
        if (subTasksStore.containsKey(task.toInt())) {
            SubTask sTask = subTasksStore.get(task.toInt());
            sTask.update(task);
            setStatusSubTask(sTask.toInt(), task.getStatus());
        }
        if (tasksStore.containsKey(task.toInt())) {
            Task simpleTask = tasksStore.get(task.toInt());
            simpleTask.update(task);
        }
    }

    private boolean isEachStatusSubTasksThis(EpicTask eTask, final TaskType status) {
        for (int i : eTask.getSubTasksIDs()) {
            if (subTasksStore.get(i).getStatus() != status) {
                return false;
            }
        }
        return true;
    }

}
