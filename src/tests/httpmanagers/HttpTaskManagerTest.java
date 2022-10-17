package httpmanagers;

import interfaces.TaskManager;
import managers.HttpTaskManager;
import org.junit.jupiter.api.*;
import servers.kvServer.KVServer;
import servers.tasksServer.HttpTaskServer;
import servers.testServer.clientForTests;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.AdaptersAndFormat;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.AdaptersAndFormat.formatter;
import static util.AdaptersAndFormat.gson;

public class HttpTaskManagerTest {
    String path = "http://localhost:8078";
    String pathReq = "http://localhost:8080";
    clientForTests client = new clientForTests(pathReq);
    TaskManager mng;
    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    EpicTask etask;
    SubTask subtask;
    Task task1;
    Task task2;

    @BeforeAll
    public static void setData() {
    }

    @BeforeEach
    public void setResources() {
        kvServer = new KVServer();
        kvServer.start();
        URI url = URI.create(path);
        mng = new HttpTaskManager(url);
        httpTaskServer = new HttpTaskServer(mng);
        httpTaskServer.start();

        etask = new EpicTask("Вспомнить всё", "");
        mng.addEpicTask(etask);
        subtask = new SubTask("Выучить джава", "", etask.toInt(), 12, "28.02.2023 | 20:30 | Asia/Dubai | +04:00");
        mng.addSubTask(subtask);
        task1 = new Task("Забыть джава", "", 12, "20.02.2022 | 20:30 | Asia/Dubai | +04:00");
        mng.addTask(task1);
        task2 = new Task("Сделать спринт", "");
        mng.addTask(task2);

        mng.getTask(subtask.toInt());
        mng.getTask(task2.toInt());
        mng.getTask(etask.toInt());
    }

    @AfterEach
    public void freeResources() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Nested
    public class EpicTasks {
        @Test
        public void checkGetAllEpicTasks() {
            List<EpicTask> list = gson.fromJson(client.load("/tasks/epic").body(), AdaptersAndFormat.epicListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(etask.getTitle(), list.get(0).getTitle());
        }

        @Test
        public void checkGetAll() {
            client.delete("/tasks/task?id=" + task2.toInt());
            client.delete("/tasks/task?id=" + task1.toInt());
            client.delete("/tasks/subtask?id=" + subtask.toInt());
            List<EpicTask> list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.epicListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(etask.getTitle(), list.get(0).getTitle());
            assertEquals(1, list.size());
        }

        @Test
        public void checkGetIdEpicTasks() {
            EpicTask ep = gson.fromJson(client.load("/tasks/epic" + "?id=" + etask.toInt()).body(), EpicTask.class);
            assertEquals(etask.getTitle(), ep.getTitle());
        }
    }

    @Nested
    public class Tasks {
        @Test
        public void checkGetAllTasks() {
            List<Task> list = gson.fromJson(client.load("/tasks/task").body(), AdaptersAndFormat.epicListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(2, list.size());
        }

        @Test
        public void checkGetAll() {
            client.delete("/tasks/epic?id=" + etask.toInt());
            client.delete("/tasks/task?id=" + task1.toInt());
            client.delete("/tasks/subtask?id=" + subtask.toInt());
            List<Task> list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.taskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(task2.getTitle(), list.get(0).getTitle());
            assertEquals(1, list.size());
        }

        @Test
        public void checkGetIdTasks() {
            Task ep = gson.fromJson(client.load("/tasks/task" + "?id=" + task2.toInt()).body(), Task.class);
            assertEquals(task2.getTitle(), ep.getTitle());
        }
    }

    @Nested
    public class SubTasks {
        @Test
        public void checkGetAllSubTasks() {
            List<SubTask> list = gson.fromJson(client.load("/tasks/subtask").body(), AdaptersAndFormat.subTaskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(subtask.getTitle(), list.get(0).getTitle());
        }

        @Test
        public void checkGetAll() {
            client.delete("/tasks/epic?id=" + etask.toInt());
            client.delete("/tasks/task?id=" + task1.toInt());
            client.delete("/tasks/task?id=" + task2.toInt());
            List<SubTask> list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.subTaskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(0, list.size());
        }

        @Test
        public void checkGetIdSubTasks() {
            Task ep = gson.fromJson(client.load("/tasks/subtask" + "?id=" + subtask.toInt()).body(), SubTask.class);
            assertEquals(subtask.getTitle(), ep.getTitle());
        }
    }

    @Nested
    public class History {
        @Test
        public void checkGetHistory() {
            List<Task> list = gson.fromJson(client.load("/tasks/history").body(), AdaptersAndFormat.taskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(list.size(), 3);
            assertEquals(etask.getTitle(), list.get(0).getTitle());
            assertEquals(task2.getTitle(), list.get(1).getTitle());
            assertEquals(subtask.getTitle(), list.get(2).getTitle());
        }

        @Test
        public void checkDeleteIdHistory() {
            client.delete("/tasks/epic?id=" + etask.toInt());
            //client.delete("/tasks/task?id=" + task2.toInt());
            List<Task> list = gson.fromJson(client.load("/tasks/history").body(), AdaptersAndFormat.taskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(1, list.size());
            assertEquals(task2.getTitle(), list.get(0).getTitle());
        }

        @Test
        public void checkGetPrioritized() {
            List<Task> list = gson.fromJson(client.load("/tasks/priority").body(), AdaptersAndFormat.taskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(3, list.size());
            assertEquals(task1.getTitle(), list.get(0).getTitle());
            assertEquals(subtask.getTitle(), list.get(1).getTitle());
            assertEquals(task2.getTitle(), list.get(2).getTitle());
        }

        @Test
        public void checkRemoveTasksHistory() {
            client.delete("/tasks/task");
            List<Task> list = gson.fromJson(client.load("/tasks/priority").body(), AdaptersAndFormat.taskListType);
            System.out.println("list");
            System.out.println(list);
            assertEquals(1, list.size());
            assertEquals(subtask.getTitle(), list.get(0).getTitle());
        }
    }


    @Nested
    public class RestoreFromKVServer {
        @Test
        public void checkGetHistory() {

            URI url = URI.create(path);
            mng = new HttpTaskManager(url);
            httpTaskServer.stop();
            httpTaskServer = new HttpTaskServer(mng);
            httpTaskServer.start();

            List<Task> list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.taskListType);
            assertEquals(4, list.size());
            list = gson.fromJson(client.load("/tasks/epic").body(), AdaptersAndFormat.taskListType);
            assertEquals(1, list.size());
            assertEquals("Вспомнить всё", list.get(0).getTitle());
            assertEquals("28.02.2023 | 20:30 | Asia/Dubai | +04:00", list.get(0).getStartTime().get().format(formatter));

        }

    }

    @Nested
    public class CheckUpdate {
        @Test
        public void checkUpdate() {
            assertEquals("Вспомнить всё", etask.getTitle());
            assertEquals("28.02.2023 | 20:30 | Asia/Dubai | +04:00", subtask.getStartTime().get().format(formatter));
            assertEquals("Сделать спринт", task2.getTitle());

            EpicTask epUp = new EpicTask("", "");
            epUp.setID(etask.toInt());
            epUp.setStatus(etask.getStatus());
            epUp.setTitle("qwerty");
            epUp.setDescription(etask.getDescription());
            epUp.setDuration(etask.getDuration().get().toMinutes());
            epUp.setStartTime(etask.getStartTime().get().format(formatter));
            SubTask sub = new SubTask("", "", 0);
            sub.setID(subtask.toInt());
            sub.setStatus(subtask.getStatus());
            sub.setTitle(subtask.getTitle());
            sub.setDescription(subtask.getDescription());
            sub.setDuration(subtask.getDuration().get().toMinutes());
            sub.setStartTime(subtask.getStartTime().get().minusDays(3).format(formatter));
            Task tas = new Task("", "");
            tas.setID(task2.toInt());
            tas.setStatus(task2.getStatus());
            tas.setTitle("www");
            tas.setDescription(task2.getDescription());

            client.put("/tasks/epic", gson.toJson(epUp));
            client.put("/tasks/subtask", gson.toJson(sub));
            client.put("/tasks/task", gson.toJson(tas));

            EpicTask eGot = gson.fromJson(client.load("/tasks/epic?id=" + etask.toInt()).body(), EpicTask.class);
            SubTask sGot = gson.fromJson(client.load("/tasks/subtask?id=" + subtask.toInt()).body(), SubTask.class);
            Task tGot = gson.fromJson(client.load("/tasks/task?id=" + task2.toInt()).body(), Task.class);
            assertEquals("qwerty", eGot.getTitle());
            assertEquals("25.02.2023 | 20:30 | Asia/Dubai | +04:00", sGot.getStartTime().get().format(formatter));
            assertEquals("www", tGot.getTitle());
        }
    }

    @Nested
    public class checkPutClient {
        @Test
        public void checkPutEpic() {
            client.delete("/tasks/epic");
            client.delete("/tasks/task");
            client.delete("/tasks/subtask");
            client.post("/tasks/epic", gson.toJson(etask, EpicTask.class));
            client.post("/tasks/task", gson.toJson(task2, Task.class));
            subtask.setEpicTaskID(etask.toInt());
            client.post("/tasks/subtask", gson.toJson(subtask, SubTask.class));
            List<SubTask> list = gson.fromJson(client.load("/tasks/subtask").body(), AdaptersAndFormat.subTaskListType);
            assertEquals(1, list.size());
            assertEquals(etask.toInt(), list.get(0).getEpicTaskID());
            List<Task> list2 = gson.fromJson(client.load("/tasks/task").body(), AdaptersAndFormat.taskListType);
            assertEquals(1, list2.size());
            assertEquals(task2.getTitle(), list2.get(0).getTitle());
            List<EpicTask> list3 = gson.fromJson(client.load("/tasks/epic").body(), AdaptersAndFormat.epicListType);
            assertEquals(1, list3.size());
            assertEquals(etask.getTitle(), list3.get(0).getTitle());
        }
    }

}