package tasks;/*
NEW         - the task is just created
IN_PROGRESS - the task is considered
DONE        - the task is solved
**/

public enum TaskStatus {
    NEW(0), IN_PROGRESS(1), DONE(2);
    private static final String[] names = {"NEW", "IN_PROGRESS", "DONE"};
    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    public static TaskStatus getType(int id) {
        for (TaskStatus type : values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return names[this.value];
    }
}