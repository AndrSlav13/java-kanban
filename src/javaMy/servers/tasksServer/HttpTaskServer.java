package servers.tasksServer;

import com.sun.net.httpserver.HttpServer;
import interfaces.TaskManager;
import util.Functors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager mng;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager mng) {
        this.mng = mng;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Functors f = new Functors(mng);
        List<TasksHandlerBase> listH = List.of(
                new TasksHandler("/tasks", null, null, null, f.getAll, f.deleteAll, null),
                new TasksHandler("/tasks/task", f.addTask, f.deleteTask, f.getTask, f.getAllTasks, f.deleteAllTasks, f.putTask),
                new TasksHandler("/tasks/subtask", f.addSub, f.deleteSub, f.getSub, f.getAllSubs, f.deleteAllSubs, f.putSubTask),
                new TasksHandler("/tasks/epic", f.addEpic, f.deleteEpic, f.getEpic, f.getAllEpics, f.deleteAllEpics, f.putEpic),
                new TasksHandler("/tasks/history", null, null, null, f.getAllHistory, null, null),
                new TasksHandler("/tasks/priority", null, null, null, f.getAllPrioritised, null, null)
        );

        for (TasksHandlerBase h : listH)
            httpServer.createContext(h.getContext(), h);

    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер на порту " + PORT + " остановлен");
        httpServer.stop(0);
    }
}