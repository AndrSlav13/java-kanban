package tasks;

import errors.FunctionParameterException;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm | VV | ZZZZZ");
    protected Optional<Duration> duration;
    protected Optional<ZonedDateTime> startTime;
    private Integer id;     //Unique id
    private TaskStatus status;
    private String title;
    private String description;

    public Task(String title, String description) {
        if (title == null || description == null) throw new FunctionParameterException("");

        this.id = null;
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        this.duration = Optional.empty();
        this.startTime = Optional.empty();
    }

    public Task(final String title, final String description, long duration, String startTime) {
        this(title, description);
        if (startTime != null && duration >= 0) {
            this.duration = Optional.of(Duration.ofMinutes(duration));
            this.startTime = Optional.of(ZonedDateTime.parse(startTime, formatter));
        }
    }

    public int toInt() {
        if (id == null) throw new RuntimeException("incorrect id variable value");
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
        if (title == null) throw new FunctionParameterException("");
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) throw new FunctionParameterException("");
        this.description = description;
    }

    public Optional<Duration> getDuration() {
        if (duration.isPresent()) return Optional.of(duration.get());
        else return Optional.empty();
    }

    public void setDuration(long duration) {
        if (duration >= 0)
            this.duration = Optional.of(Duration.ofMinutes(duration));
    }

    public Optional<ZonedDateTime> getStartTime() {
        if (startTime.isPresent()) return Optional.of(startTime.get());
        else return Optional.empty();
    }

    public void setStartTime(String startTime) {
        if (startTime != null)
            this.startTime = Optional.of(ZonedDateTime.parse(startTime, formatter));
    }

    public void update(Task task) {
        if (task == null) throw new FunctionParameterException("null parameter is incorrect");
        this.description = task.description;
        this.title = task.title;
        setStatus(task.status);
        if (task.duration.isPresent())
            duration = Optional.of(task.duration.get());
        if (task.startTime.isPresent())
            startTime = Optional.of(task.startTime.get());
    }

    public Optional<ZonedDateTime> getEndTime() {
        if (startTime.isPresent()) return Optional.of(startTime.get().plus(duration.get()));
        else return Optional.empty();
    }

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        if (id == null) throw new FunctionParameterException("");
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(", ");
        sb.append("TASK").append(", ");
        sb.append(title).append(", ");
        sb.append(status.toString()).append(", ");
        sb.append(description).append(", ");

        if (startTime.isPresent()) sb.append(startTime.get().format(formatter));
        sb.append(", ");
        if (duration.isPresent()) sb.append(duration.get());
        sb.append(", ");
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
        if (id == null) throw new FunctionParameterException("");
        return id.hashCode();
    }
}