package managers;

public class TaskManagerInMemoryTest extends TaskManagerTest<InMemoryTaskManager> {
    protected InMemoryTaskManager createMng() {
        return new InMemoryTaskManager();
    }

    ;
}
