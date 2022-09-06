package tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTasks = new ArrayList<>();

    public EpicTask(final String title, final String description) {
        super(title, description);
    }

    public EpicTask(final String title) {
        this(title, "");
    }

    public List<Integer> getSubTasksIDs() {
        return new ArrayList<Integer>(subTasks);
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

    /*
    equals(Object o) определен в родительском классе на основе сравнения id
    id для всех задач разный - другие поля использовать нет смысла
    Изначально hashCode() возвращал просто id - уникальное значение, после спринта решил,что лучше пусть будет id.hashCode() -
    чтобы генерировался хэш-код (возможно в алгоритмах будет эффективнее выделяться память или id преобразуется к более удобному внутреннему виду - не знаю)
    Но будет ли разница генерировать хэш на основе уникального id или для эпика id + subTasks - не уверен, но кажется лишним.
     */

    @Override
    public String toString() {
        String out = super.toString();
        return out;
    }
}
