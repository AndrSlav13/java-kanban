package tasks;

public class SubTask extends Task {

    private int idEpic;

    public SubTask(String title, String description, EpicTask eTask) {
        super(title, description);
        idEpic = eTask.toInt();
    }

    public SubTask(String title, String description, int idEpic) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public int getEpicTaskID() {
        return idEpic;
    }

    public void setEpicTaskID(int i) {
        idEpic = i;
    }

    @Override
    public String toString() {
        return "\t" + super.toString(); //Indention for subtasks
    }
}
