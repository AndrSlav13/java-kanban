/*
NEW         - the task is just created
IN_PROGRESS - the task is considered
DONE        - the task is solved
**/
package types;

public enum TaskType {
    NEW(0), IN_PROGRESS(1), DONE(2);
    private final int value;

    TaskType(int value) {
        this.value = value;
    }

    public static TaskType getType(int id) {
        for (TaskType type : values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}