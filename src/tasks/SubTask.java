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

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        int i1 = sb.indexOf(",");
        int i2 = sb.indexOf(",", i1 + 1);
        sb.replace(i1 + 1, i2, "SUBTASK");
        sb.append(this.getEpicTaskID());
        return sb.toString();
    }
}
