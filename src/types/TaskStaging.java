package types;

/*
NOT_STAGED - tasks(epic-tasks) with no subtasks
STAGED     - tasks with subtasks
SUBTASKS   - subtasks
ALL        - all tasks
**/
public enum TaskStaging {
    NOT_STAGED(0), STAGED(1), SUBTASKS(2), ALL(3);
    private final int value;

    TaskStaging(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
