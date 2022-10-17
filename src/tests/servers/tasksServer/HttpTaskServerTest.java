package servers.tasksServer;

import interfaces.TaskManager;
import managers.HttpTaskManager;
import org.junit.jupiter.api.*;
import servers.kvServer.KVServer;
import servers.testServer.clientForTests;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.AdaptersAndFormat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.AdaptersAndFormat.gson;

class HttpTaskServerTest {
    String path = "http://localhost:8078";
    String pathReq = "http://localhost:8080";
    clientForTests client = new clientForTests(pathReq);
    clientForTests clientKV = new clientForTests(path);
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
    public void setResources() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        URI url = URI.create(path);
        mng = new HttpTaskManager(url);
        httpTaskServer = new HttpTaskServer(mng);
        httpTaskServer.start();

        etask = new EpicTask("Выучить джава", "");
        mng.addEpicTask(etask);
        subtask = new SubTask("Выучить джава", "", etask.toInt(), 12, "30.02.2023 | 20:30 | Asia/Dubai | +04:00");
        mng.addSubTask(subtask);
        task1 = new Task("Выучить джава", "", 12, "20.02.2022 | 20:30 | Asia/Dubai | +04:00");
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
    public class Servers {
        @Test
        public void checkCodes() {
            HttpResponse<String> response = client.load("/tasks/epic");
            assertEquals(200, response.statusCode());
            response = client.load("/tasks/epicus");
            assertEquals(400, response.statusCode());
            response = clientKV.load("/register");
            long reg = Long.parseLong(response.body());
            List<Task> list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.taskListType);
            assertEquals(4, list.size());

            URI url = URI.create(path);
            mng = new HttpTaskManager(url);
            httpTaskServer.stop();
            httpTaskServer = new HttpTaskServer(mng);
            httpTaskServer.start();

            list = gson.fromJson(client.load("/tasks").body(), AdaptersAndFormat.taskListType);
            assertEquals(4, list.size());
        }
    }

}