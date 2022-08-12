package interfaces;/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;


public interface TaskManager {
    Task getTask(final int id);

    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Task> getEpicTasks();

    List<Task> getTasks();

    List<Task> getSubTasks();

    List<Task> getSubTasks(int idEpic);

    void addTask(Task task);

    void addEpicTask(EpicTask eTask);

    void addSubTask(SubTask sTask);

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubTasks();

    void deleteTask(final int id);

    void update(Task task);

}