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
            if (ins) historyManager.add(task);
            return task;
        }
        if (epicTasksStore.containsKey(id)) {
            task = epicTasksStore.get(id);
            if (ins) historyManager.add(task);
            return task;
        }
        if (subTasksStore.containsKey(id)) {
            task = subTasksStore.get(id);
            if (ins) historyManager.add(task);
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
        out.addAll(epicTasksStore.values());
        out.addAll(tasksStore.values());
        out.addAll(subTasksStore.values());
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
        updateStatusEpicTask(idEpic);
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
            updateStatusEpicTask(eTask.toInt());
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
            updateStatusEpicTask(eTask.toInt());
        }
        simpleTask = tasksStore.get(id);
        if (simpleTask != null) {
            tasksStore.remove(id);
        }
    }

    private void updateStatusEpicTask(final int id) {
        EpicTask eTask = epicTasksStore.get(id);
        if(eTask == null) return;
        if (!eTask.containsSubTasks()) {
            eTask.setStatus(TaskType.NEW);
            return;
        }
        int length = eTask.getSubTasksIDs().size();
        int numTypes = TaskType.values().length;
        int[] masType = new int[numTypes];

        for (int i : eTask.getSubTasksIDs()) {
            masType[subTasksStore.get(i).getStatus().getValue()] += 1;
        }

        for (int i = 0; i < numTypes; ++i) {
            if (masType[i] == length) {
                eTask.setStatus(TaskType.getType(i));
                return;
            }
        }
        eTask.setStatus(TaskType.IN_PROGRESS);
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
            updateStatusEpicTask(epicTasksStore.get(sTask.getEpicTaskID()).toInt());
        }
        if (tasksStore.containsKey(task.toInt())) {
            Task simpleTask = tasksStore.get(task.toInt());
            simpleTask.update(task);
        }
    }

}
