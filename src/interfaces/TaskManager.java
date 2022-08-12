package interfaces;/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;


public interface TaskManager {
    Task getTask(final int id);

    List<Task> getHistory();

    ArrayList<Task> getAllTasks();

    ArrayList<Task> getEpicTasks();

    ArrayList<Task> getTasks();

    ArrayList<Task> getSubTasks();

    ArrayList<Task> getSubTasks(EpicTask eTask);

    void addTask(Task task);

    void addEpicTask(EpicTask eTask);

    void addSubTask(SubTask sTask);

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubTasks();

    void deleteTask(final int id);

    void deleteSubTask(EpicTask epicTask, SubTask subTask);

    void deleteSubTask(EpicTask epicTask, final int indexSubTask);

    void update(Task task);

}