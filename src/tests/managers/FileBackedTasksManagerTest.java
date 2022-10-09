package managers;

import errors.FileFormatException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final String titleFile = "FileBackedTasksManagerTest.txt";

    protected FileBackedTasksManager createMng() {
        return new FileBackedTasksManager(titleFile);
    }

    @AfterEach
    public void deleteFile() {
        Path path = Paths.get(titleFile);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new FileFormatException("");
        }
    }

    @Nested
    public class HistoryLoadTest {
        @Test
        public void checkLoadTasks() {
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

            FileBackedTasksManager mng2 = FileBackedTasksManager.loadFromFile("FileBackedTasksManagerTest.txt");
            List<Task> mas = mng2.getHistory();

            assertEquals("Выключить свет", mas.get(3).getTitle());
            assertEquals("Выучить джава", mas.get(2).getTitle());
            assertEquals("title", mas.get(1).getTitle());
            assertEquals("subTask2", mas.get(0).getTitle());

            assertEquals(mng.getTask(epic.toInt()).getTitle(), mng2.getTask(epic.toInt()).getTitle());
            assertEquals(mng.getTask(sTask2.toInt()).getTitle(), mng2.getTask(sTask2.toInt()).getTitle());
        }

        @Test
        public void checkNullHistory() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            SubTask sTask1 = new SubTask("Выключить свет", "", epic.toInt(), 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask1);
            SubTask sTask2 = new SubTask("subTask2", "qwe", epic.toInt(), 132, "24.02.1023 | 22:30 | Asia/Dubai | +04:00");
            mng.addSubTask(sTask2);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            FileBackedTasksManager mng2 = FileBackedTasksManager.loadFromFile("FileBackedTasksManagerTest.txt");
            List<Task> mas = mng2.getHistory();

            assertEquals(0, mas.size());
        }

        @Test
        public void checkNoTasks() {

            FileBackedTasksManager mng2 = FileBackedTasksManager.loadFromFile("FileBackedTasksManagerTest.txt");
            List<Task> mas = mng2.getHistory();

            assertEquals(0, mas.size());
            assertEquals(0, mng2.getAllTasks().size());
        }

        @Test
        public void checkEpicNoSubTask() {
            EpicTask epic = new EpicTask("title", "description");
            mng.addEpicTask(epic);
            Task task1 = new Task("Выучить джава", "zxc", 12, "28.02.0123 | 20:30 | Asia/Dubai | +04:00");
            mng.addTask(task1);

            FileBackedTasksManager mng2 = FileBackedTasksManager.loadFromFile("FileBackedTasksManagerTest.txt");

            assertEquals(0, mng2.getSubTasks().size());
        }
    }
}