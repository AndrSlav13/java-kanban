/*
tasksStore    - epic-tasks: tasks staged or considered as stand alone (not staged)
subTasksStore - subtasks as stages of epic-tasks
**/

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.TaskStaging;
import types.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {
    private HashMap<Integer, EpicTask> tasksStore = new HashMap<>();  //A task to be staged
    private HashMap<Integer, SubTask> subTasksStore = new HashMap<>();    //Stages to do

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

    public ArrayList<Task> getTasks(TaskStaging type) {
        ArrayList<Task> out = new ArrayList<>();

        switch (type) {
            case STAGED:
                for (EpicTask eTask : tasksStore.values()) {
                    if (eTask.containsSubTasks()) out.add(eTask);
                }
                break;
            case NOT_STAGED:
                for (EpicTask eTask : tasksStore.values()) {
                    if (!eTask.containsSubTasks()) out.add(eTask);
                }
                break;
            case SUBTASKS:
                for (SubTask subTask : subTasksStore.values()) {
                    out.add(subTask);
                }
                break;
            case ALL:
                for (EpicTask eTask : tasksStore.values()) {
                    out.add(eTask);
                    for (int i : eTask.getSubTasksIDs())
                        out.add(subTasksStore.get(i));
                }
                break;
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
        EpicTask epicTask = new EpicTask(eTask);
        tasksStore.put(eTask.toInt(), epicTask);
        for (int i : eTask.getSubTasksIDs())
            if (i != 0 && subTasksStore.containsKey(i)) {
                epicTask.addSubTaskID(i);
                SubTask subTask = subTasksStore.get(i);
                subTask.addEpicTaskID(epicTask.toInt());
            }
    }

    public void addSubTask(SubTask sTask) { //Subtask insertion
        if (sTask.toInt() == 0) {
            sTask.setID(genID(sTask));
            SubTask subTask = new SubTask(sTask);
            subTasksStore.put(sTask.toInt(), subTask);
        }
        SubTask subTask = subTasksStore.get(sTask.toInt());
        for (int i : sTask.getEpicTasksIDs())
            if (i != 0 && tasksStore.containsKey(i)) {
                subTask.addEpicTaskID(i);
                EpicTask epicTask = tasksStore.get(i);
                epicTask.addSubTaskID(subTask.toInt());
            }
        update(subTask);
    }

    public void deleteTasks(TaskStaging type) {
        ArrayList<Integer> del = new ArrayList<>();
        switch (type) {
            case NOT_STAGED:
                for (int i : tasksStore.keySet()) {
                    if (!tasksStore.get(i).containsSubTasks())
                        del.add(i);
                }
                for (int i : del) tasksStore.remove(i);
                break;
            case STAGED:
                for (int i : tasksStore.keySet()) {
                    EpicTask eTask = tasksStore.get(i);
                    if (eTask.containsSubTasks())
                        for (int j : eTask.getSubTasksIDs())
                            subTasksStore.get(j).removeReferenceToEpicTask(i);
                    eTask.removeReferences();
                    del.add(i);
                }
                for (int i : del) tasksStore.remove(i);
                break;
            case SUBTASKS:
                for (int i : subTasksStore.keySet()) {
                    SubTask sTask = subTasksStore.get(i);
                    if (sTask.containsEpicTasks())
                        for (int j : sTask.getEpicTasksIDs())
                            tasksStore.get(j).removeReferenceToSubTask(i);
                    sTask.removeReferences();
                    del.add(i);
                }
                for (int i : del) subTasksStore.remove(i);
                break;
            case ALL:
                for (int i : subTasksStore.keySet())
                    subTasksStore.get(i).removeReferences();
                subTasksStore.clear();
                for (int i : tasksStore.keySet())
                    tasksStore.get(i).removeReferences();
                tasksStore.clear();
                break;
        }
    }

    public void deleteTask(final int id) {
        EpicTask eTask;
        SubTask sTask;
        eTask = tasksStore.get(id);
        if (eTask != null)
            for (int i : eTask.getSubTasksIDs()) {
                sTask = subTasksStore.get(i);
                sTask.removeReferenceToEpicTask(id);
            }
        tasksStore.remove(id);
        sTask = subTasksStore.get(id);
        if (sTask != null)
            for (int i : sTask.getEpicTasksIDs()) {
                eTask = tasksStore.get(i);
                eTask.removeReferenceToSubTask(id);
            }
        subTasksStore.remove(id);
    }

    public void deleteSubTask(EpicTask epicTask, SubTask subTask) {    //delete subtask by id EpicTask + id SubTask
        EpicTask eTask;
        eTask = tasksStore.get(epicTask.toInt());
        if (eTask == null) return;
        //Mutual referencing problem solving
        eTask.removeReferenceToSubTask(subTask.toInt());
        SubTask sTask = subTasksStore.get(subTask.toInt());
        sTask.removeReferenceToEpicTask(epicTask.toInt());

        ArrayList<Integer> subTasks = eTask.getSubTasksIDs();
        if (!subTasks.isEmpty()) update(getTask(subTasks.get(0)));
    }

    public void deleteSubTask(EpicTask epicTask, final int indexSubTask) {  //delete subtask by index
        EpicTask eTask;
        eTask = tasksStore.get(epicTask.toInt());
        if (eTask == null) return;
        //Mutual referencing problem solving
        int indSubTask = eTask.getSubTasksIDs().get(indexSubTask - 1);
        SubTask sTask = subTasksStore.get(indSubTask);
        sTask.removeReferenceToEpicTask(epicTask.toInt());
        eTask.deleteSubTask(indexSubTask);

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
            for (int i : sTask.getEpicTasksIDs()) {
                EpicTask eTask = tasksStore.get(i);
                if (!isEachStatusSubTasksThis(i, TaskType.DONE)) {
                    eTask.setStatus(TaskType.IN_PROGRESS);
                    continue;
                }
                eTask.setStatus(TaskType.DONE);
            }
        }

        if (status == TaskType.NEW) {
            for (int i : sTask.getEpicTasksIDs()) {
                EpicTask eTask = tasksStore.get(i);
                if (!isEachStatusSubTasksThis(i, TaskType.NEW)) {
                    eTask.setStatus(TaskType.IN_PROGRESS);
                    continue;
                }
                eTask.setStatus(TaskType.NEW);
            }
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

    private boolean isEachStatusSubTasksThis(final int id, final TaskType status) {
        EpicTask eTask = tasksStore.get(id);
        for (int i : eTask.getSubTasksIDs()) {
            TaskType ss = subTasksStore.get(i).getStatus();
            if (subTasksStore.get(i).getStatus() != status) {
                return false;
            }
        }
        return true;
    }

}