package tasks;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpicTask extends Task {

    private List<Integer> subTasks = new ArrayList<>();

    private Optional<ZonedDateTime> endTime = Optional.empty();

    public EpicTask(final String title, final String description) {
        super(title, description);
    }

    public List<Integer> getSubTasksIDs() {
        return new ArrayList<Integer>(subTasks);
    }

    public Optional<ZonedDateTime> getEndTime() {
        if (endTime.isPresent()) return Optional.of(endTime.get());
        else return Optional.empty();
    }

    public void setEndTime(Optional<ZonedDateTime> endTime) {
        this.endTime = endTime;
    }

    public void removeReferenceToSubTask(Integer id) {
        subTasks.remove(id);
    }

    public void removeReferences() {
        subTasks.clear();
    }

    @Override
    public void update(Task task) {
        this.setDescription(task.getDescription());
        this.setTitle(task.getTitle());
    }

    public void addSubTaskID(int i) {
        subTasks.add(i);
    }

    public boolean containsSubTasks() {
        return !subTasks.isEmpty();
    }

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        int i1 = sb.indexOf(",");
        int i2 = sb.indexOf(",", i1 + 1);
        sb.replace(i1 + 1, i2, "EPICTASK");
        return sb.toString();
    }
}
