package types;

/*
NEW         - the task is just created
IN_PROGRESS - the task is considered
DONE        - the task is solved
**/
public enum TaskType {
    NEW(0), IN_PROGRESS(1), DONE(2);
    private final int value;

    TaskType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}