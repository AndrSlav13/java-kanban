package managers;/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import errors.FunctionParameterException;
import errors.TaskIntersectionException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.util.*;

import static util.AdaptersAndFormat.formatter;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> tasksStore = new HashMap<>();  //Not Staged task
    private final HashMap<Integer, EpicTask> epicTasksStore = new HashMap<>();  //A task to be staged
    private final HashMap<Integer, SubTask> subTasksStore = new HashMap<>();    //Stages to do
    private final String separator = ",";
    private Comparator<Task> comparator = new Comparator<Task>() {
        //isPresent() должен возвращать true!
        private boolean isIntersection(Task task1, Task task2) {
            if (task1.getStartTime().get().isBefore(task2.getStartTime().get()) &&
                    task1.getEndTime().get().isAfter(task2.getStartTime().get())) return true;
            if (task1.getStartTime().get().isBefore(task2.getEndTime().get()) &&
                    task1.getEndTime().get().isAfter(task2.getEndTime().get())) return true;
            if ((task1.getStartTime().get().isAfter(task2.getStartTime().get()) || task1.getStartTime().get().isEqual(task2.getStartTime().get())) &&
                    (task1.getEndTime().get().isBefore(task2.getEndTime().get()) || task1.getEndTime().get().isEqual(task2.getEndTime().get())))
                return true;
            return false;
        }

        @Override
        public int compare(Task task1, Task task2) {
            if (task1.toInt() == task2.toInt()) return 0;
            if (!task1.getStartTime().isPresent() && !task2.getStartTime().isPresent())
                return task1.toInt() - task2.toInt();
            if (task1.getStartTime().isPresent() && !task2.getStartTime().isPresent()) return -1;
            if (!task1.getStartTime().isPresent() && task2.getStartTime().isPresent()) return 1;
            if (isIntersection(task1, task2)) return 0;
            if (task1.getStartTime().get().isBefore(task2.getStartTime().get())) return -1;
            return 1;
        }
    };
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addHistoryTask(Task task) {
        historyManager.addHistoryTask(task);
    }

    @Override
    public void removeHistoryTask(int id) {

        historyManager.removeHistoryTask(id);
        prioritizedTasks.remove(getTaskCheckHistoryInserted(id, false));
    }

    private Task getTaskCheckHistoryInserted(final int id, boolean ins) {
        Task task;
        if (tasksStore.containsKey(id)) {
            task = tasksStore.get(id);
            if (ins) historyManager.addHistoryTask(task);
            return (task);
        }
        if (epicTasksStore.containsKey(id)) {
            task = epicTasksStore.get(id);
            if (ins) historyManager.addHistoryTask(task);
            return task;
        }
        if (subTasksStore.containsKey(id)) {
            task = subTasksStore.get(id);
            if (ins) historyManager.addHistoryTask(task);
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
        if (task == null) throw new FunctionParameterException("");
        if (task.getTitle().contains(separator) ||
                task.getDescription().contains(separator)) throw new FunctionParameterException("");
        task.setID(genID(task));
        if (!prioritizedTasks.add(task)) throw new TaskIntersectionException("Tasks are to be solved one by one");
        tasksStore.put(task.toInt(), task);
    }

    @Override
    public void addEpicTask(EpicTask eTask) {   //Epic-task insertion
        if (eTask == null) throw new NullPointerException();
        eTask.setID(genID(eTask));
        epicTasksStore.put(eTask.toInt(), eTask);
    }

    @Override
    public void addSubTask(SubTask sTask) { //Subtask insertion
        if (sTask == null) throw new NullPointerException();
        int idEpic = sTask.getEpicTaskID();
        if (!epicTasksStore.containsKey(idEpic)) return;
        EpicTask eTask = epicTasksStore.get(idEpic);
        sTask.setID(genID(sTask));

        if (!prioritizedTasks.add(sTask)) throw new TaskIntersectionException("Tasks are to be solved one by one");
        subTasksStore.put(sTask.toInt(), sTask);
        eTask.addSubTaskID(sTask.toInt());
        update(eTask);
    }

    @Override
    public void deleteTasks() {
        for (int id : tasksStore.keySet()) {
            removeHistoryTask(id);
        }
        tasksStore.clear();
    }

    @Override
    public void deleteEpicTasks() {
        for (int id : subTasksStore.keySet()) {
            removeHistoryTask(id);
        }
        for (int id : epicTasksStore.keySet()) {
            removeHistoryTask(id);
        }
        subTasksStore.clear();
        epicTasksStore.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (int id : subTasksStore.keySet()) {
            removeHistoryTask(id);
        }
        subTasksStore.clear();
        for (EpicTask eTask : epicTasksStore.values()) {
            eTask.removeReferences();
            update(eTask);
        }
    }

    @Override
    public void deleteTask(final int id) {
        removeHistoryTask(id);

        Task simpleTask;
        EpicTask eTask;
        SubTask sTask;
        eTask = epicTasksStore.get(id);
        if (eTask != null) {
            for (int i : eTask.getSubTasksIDs())
                deleteTask(i);
            eTask.removeReferences();
            epicTasksStore.remove(id);
            return;
        }
        sTask = subTasksStore.get(id);
        if (sTask != null) {
            eTask = epicTasksStore.get(sTask.getEpicTaskID());
            eTask.removeReferenceToSubTask(sTask.toInt());
            subTasksStore.remove(id);
            update(eTask);
            return;
        }
        simpleTask = tasksStore.get(id);
        if (simpleTask != null) {
            tasksStore.remove(id);
            return;
        }
        throw new FunctionParameterException("wrong param");
    }

    private void updateStatusEpicTask(final int id) {
        EpicTask eTask = epicTasksStore.get(id);
        if (eTask == null) return;
        if (!eTask.containsSubTasks()) {
            eTask.setStatus(TaskStatus.NEW);
            return;
        }
        int length = eTask.getSubTasksIDs().size();
        int numTypes = TaskStatus.values().length;
        int[] masType = new int[numTypes];

        for (int i : eTask.getSubTasksIDs()) {
            masType[subTasksStore.get(i).getStatus().getValue()] += 1;
        }

        for (int i = 0; i < numTypes; ++i) {
            if (masType[i] == length) {
                eTask.setStatus(TaskStatus.getType(i));
                return;
            }
        }
        eTask.setStatus(TaskStatus.IN_PROGRESS);
    }

    private void updateDateTimeEpicTask(final int id) {
        EpicTask eTask = epicTasksStore.get(id);
        if (eTask == null) return;
        for (int t : eTask.getSubTasksIDs()) {
            if (subTasksStore.get(t).getStartTime().isEmpty()) continue;
            if (!eTask.getStartTime().isPresent() ||
                    (subTasksStore.get(t).getStartTime().isPresent() &&
                            eTask.getStartTime().get().isAfter(subTasksStore.get(t).getStartTime().get()))
            )
                eTask.setStartTime(subTasksStore.get(t).getStartTime().get().format(formatter));
            if (!eTask.getEndTime().isPresent() ||
                    (subTasksStore.get(t).getEndTime().isPresent() &&
                            eTask.getEndTime().get().isBefore(subTasksStore.get(t).getEndTime().get()))
            )
                eTask.setEndTime(subTasksStore.get(t).getEndTime());
            if (eTask.getStartTime().isPresent() && eTask.getEndTime().isPresent())
                eTask.setDuration((Duration.between(eTask.getStartTime().get(), eTask.getEndTime().get())).toMinutes());
        }
    }

    @Override
    public void update(Task task) {
        if (task == null) throw new FunctionParameterException("null parameter is incorrect");
        if (epicTasksStore.containsKey(task.toInt())) {
            EpicTask eTask = epicTasksStore.get(task.toInt());
            eTask.update(task);
            updateStatusEpicTask(task.toInt());
            updateDateTimeEpicTask(task.toInt());
            return;
        }
        if (subTasksStore.containsKey(task.toInt())) {
            SubTask sTask = subTasksStore.get(task.toInt());
            sTask.update(task);
            update(epicTasksStore.get(sTask.getEpicTaskID()));
            return;
        }
        if (tasksStore.containsKey(task.toInt())) {
            Task simpleTask = tasksStore.get(task.toInt());
            simpleTask.update(task);
            return;
        }
        throw new FunctionParameterException("id " + task.toInt() + " is incorrect");
    }

}
