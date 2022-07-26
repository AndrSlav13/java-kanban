import interfaces.TaskManager;
import managers.HttpTaskManager;
import servers.kvServer.KVServer;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.net.URI;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String path = "http://localhost:8078";
        KVServer kvServer = new KVServer();
        kvServer.start();
        URI url = URI.create(path);
        TaskManager mng = new HttpTaskManager(url);

        Task task1 = new Task("Выучить джава", "", 12, "20.02.1023 | 20:30 | Asia/Dubai | +04:00");
        mng.addTask(task1);
        Task task2 = new Task("Сделать спринт", "");
        mng.addTask(task2);

        EpicTask epicTask1 = new EpicTask("Дела перед уходом", "Не забыть сделать перед уходом на работу");
        mng.addEpicTask(epicTask1);
        SubTask sTask1 = new SubTask("Выключить свет", "", epicTask1, 12, "21.02.1023 | 20:30 | Asia/Dubai | +04:00");
        SubTask sTask2 = new SubTask("Покормить кота", "У него диета", epicTask1, 12, "28.02.1023 | 02:20 | Asia/Dubai | +04:00");
        SubTask sTask3 = new SubTask("Взять ключи", "И от машины", epicTask1);
        mng.addSubTask(sTask1);
        mng.addSubTask(sTask2);
        mng.addSubTask(sTask3);

        EpicTask epicTask2 = new EpicTask("Эпик без подзадач", "без описания");
        mng.addEpicTask(epicTask2);

        ////////////////////////////
        //ИЗМЕНЕНИЕ СТАТУСА ЗАДАЧИ И ОБНОВЛЕНИЕ
        ////////////////////////////
        sTask1.setStatus(TaskStatus.DONE);
        sTask2.setStatus(TaskStatus.NEW);
        sTask3.setStatus(TaskStatus.IN_PROGRESS);

        mng.update(sTask1);
        mng.update(sTask2);
        mng.update(sTask3);

        System.out.println("////////////////////////////");
        System.out.println("ВЫВОД ИСТОРИИ");
        System.out.println("////////////////////////////");

        mng.getTask(task1.toInt());
        mng.getTask(sTask1.toInt());
        mng.getTask(task2.toInt());
        mng.getTask(sTask1.toInt());
        mng.getTask(epicTask1.toInt());
        mng.getTask(sTask2.toInt());
        mng.getTask(sTask3.toInt());
        mng.getTask(sTask1.toInt());
        mng.getTask(epicTask2.toInt());
        mng.getTask(epicTask2.toInt());

        List<Task> out = mng.getHistory();
        for (Task task : out) System.out.println(task);

        System.out.println("////////////////////////////");
        System.out.println("ВЫВОД ИСТОРИИ ПОСЛЕ УДАЛЕНИЯ ЧАСТИ ЗАДАЧ");
        System.out.println("////////////////////////////");

        mng.deleteEpicTasks();

        out = mng.getHistory();
        for (Task task : out) System.out.println(task);

        System.out.println("ВЫВОД ЗАДАЧ ПО ПРИОРИТЕТУ");
        out = mng.getPrioritizedTasks();
        for (Task task : out) System.out.println(task);

        System.out.println("Конец");
    }
}

