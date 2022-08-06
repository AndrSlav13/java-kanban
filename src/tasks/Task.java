package tasks;

import types.TaskType;

public abstract class Task {
    private int id;     //Unique id
    private TaskType status;
    private String title;
    private String description;

    public Task(final String title, final String description) {
        this.title = title;
        this.description = description;
        status = TaskType.NEW;
        id = 0; //id==0 is not valid
    }

    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        status = task.status;
        id = task.id;
    }

    public int toInt() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public TaskType getStatus() {
        return status;
    }

    public void setStatus(final TaskType status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void update(Task task) {
        this.description = task.description;
        this.title = task.title;
        setStatus(task.status);
    }

    @Override
    public String toString() {
        return "-- " + title + "  /" + description + "/" + " -- STATUS: " + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}