package tasks;

import java.util.Objects;

public class Task {
    private Integer id;     //Unique id
    private TaskStatus status;
    private String title;
    private String description;

    public Task(final String title, final String description) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
    }

    public int toInt() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(final TaskStatus status) {
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

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(", ");
        sb.append("TASK").append(", ");
        sb.append(title).append(", ");
        sb.append(status.toString()).append(", ");
        sb.append(description).append(", ");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}