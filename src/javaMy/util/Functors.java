package util;

import interfaces.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.function.Function;
import java.util.function.Supplier;

import static util.AdaptersAndFormat.gson;

public class Functors {
    private TaskManager mng;
    //----------------------------Tasks
    //Add task
    public Function<String, Object> addTask = str -> {
        Task task = gson.fromJson(str, Task.class);
        int id = task.toInt();
        mng.addTask(task);
        task.setID(id); //При добавлении id был сгенерирован другой!
        return "task added";
    };
    //Update task
    public Function<String, Object> putTask = str -> {
        Task task = gson.fromJson(str, Task.class);
        int id = task.toInt();
        mng.getTask(id).update(task);
        return "task updated";
    };
    //Delete task
    public Function<Integer, Object> deleteTask = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != Task.class) throw new RuntimeException("No task with id=" + i);
        mng.deleteTask(i);
        return "task deleted";
    };
    //Get task
    public Function<Integer, Object> getTask = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != Task.class) throw new RuntimeException("No task with id=" + i);
        return task;
    };
    //Get all tasks
    public Supplier<Object> getAllTasks = () -> mng.getTasks();
    //Delete all tasks
    public Supplier<Object> deleteAllTasks = () -> {
        mng.deleteTasks();
        return "tasks deleted";
    };
    //----------------------------EpicTasks
    //Add task
    public Function<String, Object> addEpic = str -> {
        EpicTask task = gson.fromJson(str, EpicTask.class);
        int id = task.toInt();
        mng.addEpicTask(task);
        task.setID(id);
        return "epic added";
    };
    //Update task
    public Function<String, Object> putEpic = str -> {
        EpicTask task = gson.fromJson(str, EpicTask.class);
        int id = task.toInt();
        mng.getTask(id).update(task);
        return "epic updated";
    };
    //Delete task
    public Function<Integer, Object> deleteEpic = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != EpicTask.class) throw new RuntimeException("No epic with id=" + i);
        mng.deleteTask(i);
        return "epic deleted";
    };
    //Get task
    public Function<Integer, Object> getEpic = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != EpicTask.class) throw new RuntimeException("No epic with id=" + i);
        return task;
    };
    //Get all tasks
    public Supplier<Object> getAllEpics = () -> mng.getEpicTasks();
    //Delete all tasks
    public Supplier<Object> deleteAllEpics = () -> {
        mng.deleteEpicTasks();
        return "epics deleted";
    };
    //----------------------------SubTasks
    //Add task
    public Function<String, Object> addSub = str -> {
        SubTask task = gson.fromJson(str, SubTask.class);
        int id = task.toInt();
        mng.addSubTask(task);
        task.setID(id);
        return "subtask added";
    };
    //Update task
    public Function<String, Object> putSubTask = str -> {
        SubTask task = gson.fromJson(str, SubTask.class);
        int id = task.toInt();
        mng.getTask(id).update(task);
        return "subtask updated";
    };
    //Delete task
    public Function<Integer, Object> deleteSub = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != SubTask.class) throw new RuntimeException("No subtask with id=" + i);
        mng.deleteTask(i);
        return "subtask deleted";
    };
    //Get task
    public Function<Integer, Object> getSub = i -> {
        Task task = mng.getTask(i);
        if (task.getClass() != SubTask.class) throw new RuntimeException("No subtask with id=" + i);
        return task;
    };
    //Get all tasks
    public Supplier<Object> getAllSubs = () -> mng.getSubTasks();
    //Delete all tasks
    public Supplier<Object> deleteAllSubs = () -> {
        mng.deleteSubTasks();
        return "subtasks deleted";
    };
    //----------------------------AllTasks
    //Get all tasks
    public Supplier<Object> getAll = () -> mng.getAllTasks();
    //Delete all tasks
    public Supplier<Object> deleteAll = () -> {
        mng.deleteTasks();
        mng.deleteEpicTasks();
        mng.deleteSubTasks();
        return "tasks/epics/subtasks deleted";
    };
    //----------------------------History
    //Get all tasks
    public Supplier<Object> getAllHistory = () -> mng.getHistory();
    //----------------------------Prioritised tasks
    //Get all tasks
    public Supplier<Object> getAllPrioritised = () -> mng.getPrioritizedTasks();

    public Functors(TaskManager mng) {
        this.mng = mng;
    }
}
