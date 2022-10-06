package managers;

import errors.FunctionParameterException;
import errors.TaskIntersectionException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager & HistoryManager> {
    T mng;

    protected abstract T createMng();

    @BeforeEach
    void setMng() {
        mng = createMng();
    }

    @Nested     //Проверка изменения статусов
    public class EpicTaskStatusTest {

        @Test
        public void checkStatusEmptySubTaskList() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            assertEquals(TaskStatus.NEW, mng.getTask(epic.toInt()).getStatus());
        }

        @Test
        public void checkStatusNewSubTasksList() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask subtask1 = new SubTask("titleSub1", "descriptionSub1", epic.toInt());
            subtask1.setStatus(TaskStatus.NEW);
            SubTask subtask2 = new SubTask("titleSub2", "descriptionSub2", epic.toInt());
            subtask2.setStatus(TaskStatus.NEW);
            mng.addSubTask(subtask1);
            mng.addSubTask(subtask2);
            assertEquals(TaskStatus.NEW, mng.getTask(epic.toInt()).getStatus());
        }

        @Test
        public void checkStatusDoneSubTasksList() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask subtask1 = new SubTask("titleSub1", "descriptionSub1", epic.toInt());
            subtask1.setStatus(TaskStatus.DONE);
            SubTask subtask2 = new SubTask("titleSub2", "descriptionSub2", epic.toInt());
            subtask2.setStatus(TaskStatus.DONE);
            mng.addSubTask(subtask1);
            mng.addSubTask(subtask2);
            assertEquals(TaskStatus.DONE, mng.getTask(subtask1.toInt()).getStatus());
            assertEquals(TaskStatus.DONE, mng.getTask(epic.toInt()).getStatus());
        }

        @Test
        public void checkStatusNewDoneSubTasksList() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask subtask1 = new SubTask("titleSub1", "descriptionSub1", epic.toInt());
            subtask1.setStatus(TaskStatus.NEW);
            SubTask subtask2 = new SubTask("titleSub2", "descriptionSub2", epic.toInt());
            subtask2.setStatus(TaskStatus.DONE);
            mng.addSubTask(subtask1);
            mng.addSubTask(subtask2);
            assertEquals(TaskStatus.IN_PROGRESS, mng.getTask(epic.toInt()).getStatus());
        }

        @Test
        public void checkStatusInProgressSubTasksList() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask subtask1 = new SubTask("titleSub1", "descriptionSub1", epic.toInt());
            subtask1.setStatus(TaskStatus.IN_PROGRESS);
            SubTask subtask2 = new SubTask("titleSub2", "descriptionSub2", epic.toInt());
            subtask2.setStatus(TaskStatus.IN_PROGRESS);
            mng.addSubTask(subtask1);
            mng.addSubTask(subtask2);
            assertEquals(TaskStatus.IN_PROGRESS, mng.getTask(epic.toInt()).getStatus());
        }
    }

    @Nested
    public class UpdateParamsTest {
        @Test
        public void checkStatusTitleEpic() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            //Моделирование отправки обновления на сервер - через новый объект (через старый объект-epic произойдет изменение по ссылке)
            EpicTask epicUpdate = new EpicTask(epic.getTitle(), epic.getDescription());
            epicUpdate.setID(epic.toInt());
            epicUpdate.setStatus(TaskStatus.DONE);
            epicUpdate.setTitle("qwerty");
            mng.update(epicUpdate);
            assertEquals(TaskStatus.NEW, mng.getTask(epic.toInt()).getStatus());
            assertEquals("qwerty", mng.getTask(epic.toInt()).getTitle());
        }

        @Test
        public void checkStatusTitleSub() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            //Моделирование отправки обновления на сервер - через новый объект (через старый объект-epic произойдет изменение по ссылке)
            SubTask subTaskUpdate = new SubTask(sTask1.getTitle(), sTask1.getDescription(), epic.toInt());
            subTaskUpdate.setID(sTask1.toInt());
            subTaskUpdate.setStatus(TaskStatus.DONE);
            subTaskUpdate.setTitle("qwerty");
            subTaskUpdate.setStartTime("13.05.2023 | 22:33 | Asia/Dubai | +04:00");
            subTaskUpdate.setDuration(123);
            mng.update(subTaskUpdate);
            assertEquals(TaskStatus.DONE, mng.getTask(sTask1.toInt()).getStatus());
            assertEquals("qwerty", mng.getTask(sTask1.toInt()).getTitle());
            assertEquals("13.05.2023 | 22:33 | Asia/Dubai | +04:00", mng.getTask(sTask1.toInt()).getStartTime().get().format(Task.formatter));
            assertEquals(123, mng.getTask(sTask1.toInt()).getDuration().get().toMinutes());
        }

        @Test
        public void checkThrowingUpdateNullId() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            //Моделирование отправки обновления на сервер - через новый объект (через старый объект-epic произойдет изменение по ссылке)
            EpicTask epicUpdate = null;

            FunctionParameterException ex =
                    assertThrows(
                            FunctionParameterException.class,
                            () -> mng.update(epicUpdate)
                    );
            assertEquals("null parameter is incorrect", ex.getMessage());
        }

        @Test
        public void checkThrowingUpdateIncorrectId() {
            EpicTask epic = new EpicTask("title", "description");
            epic.setID(12);

            FunctionParameterException ex =
                    assertThrows(
                            FunctionParameterException.class,
                            () -> mng.update(epic)
                    );
            assertEquals("id 12 is incorrect", ex.getMessage());
        }
    }

    @Nested
    public class DeletionsGetTest {
        @Test
        public void checkDeleteTask() {
            EpicTask epic = new EpicTask("title", "description");
            EpicTask epic2 = new EpicTask("title2", "description2");
            mng.addEpicTask(epic);
            mng.addEpicTask(epic2);

            mng.deleteTask(epic.toInt());
            assertNull(mng.getTask(epic.toInt()));
            assertEquals("title2", mng.getTask(epic2.toInt()).getTitle());
        }

        @Test
        public void checkDeleteSubTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("Выключить свет2", "", epic.toInt(), 152, "28.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);

            assertEquals(2, mng.getSubTasks().size());
            mng.deleteSubTasks();
            assertEquals(0, mng.getSubTasks().size());
        }

        @Test
        public void checkDeleteEpicTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            EpicTask epic2 = new EpicTask("title2", "description2");
            mng.addEpicTask(epic2);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);

            assertEquals(2, mng.getEpicTasks().size());
            assertEquals(1, mng.getSubTasks().size());
            mng.deleteEpicTasks();
            assertEquals(0, mng.getSubTasks().size());
            assertEquals(0, mng.getEpicTasks().size());
            assertEquals(0, mng.getTasks().size());
        }

        @Test
        public void checkDeleteTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            Task task1 = new Task("Выучить джава", "", 12, "20.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);
            Task task2 = new Task("Сделать спринт", "");
            mng.addTask(task2);

            assertEquals(1, mng.getEpicTasks().size());
            assertEquals(1, mng.getSubTasks().size());
            assertEquals(2, mng.getTasks().size());
            mng.deleteTasks();
            assertEquals(1, mng.getSubTasks().size());
            assertEquals(1, mng.getEpicTasks().size());
            assertEquals(0, mng.getTasks().size());
        }

        @Test
        public void checkGetAllTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            Task task1 = new Task("Выучить джава", "", 12, "20.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);
            Task task2 = new Task("Сделать спринт", "");
            mng.addTask(task2);

            assertEquals(4, mng.getAllTasks().size());
            mng.deleteTasks();
            mng.deleteSubTasks();
            assertEquals("title", mng.getAllTasks().get(0).getTitle());
        }

        @Test
        public void checkGetSubTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            Task task1 = new Task("Выучить джава", "", 12, "20.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);
            Task task2 = new Task("Сделать спринт", "");
            mng.addTask(task2);

            assertEquals(1, mng.getSubTasks().size());
            assertEquals("Выключить свет", mng.getSubTasks().get(0).getTitle());
        }

        @Test
        public void checkThrowingDeleteWrongId() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);

            FunctionParameterException ex =
                    assertThrows(
                            FunctionParameterException.class,
                            () -> mng.deleteTask(0)
                    );
            assertEquals("wrong param", ex.getMessage());
        }
    }

    @Nested
    public class HistoryTest {
        @Test
        public void checkHistoryAddTask() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            List<Task> mas = mng.getHistory();

            assertEquals("Выключить свет", mas.get(3).getTitle());
            assertEquals("Выучить джава", mas.get(2).getTitle());
            assertEquals("title", mas.get(1).getTitle());
            assertEquals("subTask2", mas.get(0).getTitle());
        }

        @Test
        public void checkHistoryAddTaskNotOnce() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            mng.getTask(sTask1.toInt());
            mng.getTask(epic.toInt());
            List<Task> mas = mng.getHistory();

            assertEquals("Выучить джава", mas.get(3).getTitle());
            assertEquals("subTask2", mas.get(2).getTitle());
            assertEquals("Выключить свет", mas.get(1).getTitle());
            assertEquals("title", mas.get(0).getTitle());
        }

        @Test
        public void checkHistoryDeleteEpic() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            mng.getTask(sTask1.toInt());
            mng.getTask(epic.toInt());
            mng.deleteTask(epic.toInt());
            List<Task> mas = mng.getHistory();

            assertEquals(1, mas.size());
            assertEquals("Выучить джава", mas.get(0).getTitle());
        }

        @Test
        public void checkHistoryDeleteSubTasks() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            mng.getTask(sTask1.toInt());
            mng.getTask(epic.toInt());
            mng.deleteSubTasks();
            List<Task> mas = mng.getHistory();

            assertEquals(2, mas.size());
            assertEquals("Выучить джава", mas.get(1).getTitle());
            assertEquals("title", mas.get(0).getTitle());
        }

        @Test
        public void checkHistoryRemoveHistoryTask() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            mng.getTask(sTask1.toInt());
            mng.getTask(epic.toInt());
            mng.removeHistoryTask(epic.toInt());
            mng.removeHistoryTask(epic.toInt());
            mng.removeHistoryTask(sTask2.toInt());
            mng.removeHistoryTask(task1.toInt());
            List<Task> mas = mng.getHistory();

            assertEquals("Выключить свет", mas.get(0).getTitle());
            assertEquals(1, mas.size());
        }

        @Test
        public void checkHistoryAddHistoryTask() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            mng.getTask(sTask1.toInt());
            mng.getTask(task1.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(epic.toInt());
            mng.getTask(sTask2.toInt());
            mng.getTask(sTask1.toInt());
            mng.getTask(epic.toInt());
            mng.addHistoryTask(task1);
            List<Task> mas = mng.getHistory();

            assertEquals("subTask2", mas.get(3).getTitle());
            assertEquals("Выключить свет", mas.get(2).getTitle());
            assertEquals("title", mas.get(1).getTitle());
            assertEquals("Выучить джава", mas.get(0).getTitle());
            assertEquals(4, mas.size());
        }
    }

    @Nested
    public class PrioritisedTasksGetTest {
        @Test
        public void checkPrioritisedTask() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);
            SubTask sTask3 = new SubTask("subTask3", "asd", epic.toInt());
            mng.addSubTask(sTask3);
            List<Task> mas = mng.getPrioritizedTasks();

            assertEquals(4, mas.size());
            assertEquals("Выучить джава", mas.get(0).getTitle());
            assertEquals("Выключить свет", mas.get(1).getTitle());
            assertEquals("subTask2", mas.get(2).getTitle());
            assertEquals("subTask3", mas.get(3).getTitle());
        }

        @Test
        public void checkThrowingIntersectionPrioritised() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "21.02.1023 | 19:30 | Asia/Dubai | +04:00");

            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);
            SubTask sTask3 = new SubTask("subTask3", "asd", epic.toInt());
            mng.addSubTask(sTask3);

            TaskIntersectionException ex =
                    assertThrows(
                            TaskIntersectionException.class,
                            () -> mng.addSubTask(sTask2)
                    );
            assertEquals("Tasks are to be solved one by one", ex.getMessage());
        }
    }
}
