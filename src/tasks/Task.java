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
        //Task создается вне менеджера, а значение id ему присваивается только после выполнения его загрузки
        //в менеджер функцией addTask/addSubTask.
        //Значение id==0 недопустимо в менеджере, поэтому по умолчанию присваивается полю.
        //В настоящей реализации программы это не важно - при вызове указанных функций присваивается новое значение,
        //так сделано только для минимизации возможных ошибок в будущем.
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