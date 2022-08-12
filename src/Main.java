import interfaces.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.TaskType;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager mng = Managers.getDefault();

//Перед добавлением subtask необходимо добавить epic-task

        EpicTask epicTask = new EpicTask("Дела перед уходом", "То, что надо не забыть сделать перед уходом на работу");
        mng.addEpicTask(epicTask);
        SubTask sTask1 = new SubTask("Выключить свет", "", epicTask);
        SubTask sTask2 = new SubTask("Покормить кота", "У него диета", epicTask);
        SubTask sTask3 = new SubTask("Взять ключи", "И от машины", epicTask);
        mng.addSubTask(sTask1);
        mng.addSubTask(sTask2);
        mng.addSubTask(sTask3);

        ////////////////////////////
        System.out.println("ИЗМЕНЕНИЕ СТАТУСА ЗАДАЧИ И ОБНОВЛЕНИЕ");
        ////////////////////////////
        sTask1.setStatus(TaskType.DONE);
        sTask2.setStatus(TaskType.NEW);
        sTask3.setStatus(TaskType.IN_PROGRESS);

        mng.update(sTask1);
        mng.update(sTask2);
        mng.update(sTask3);

        ////////////////////////////
        System.out.println("ВЫВОД ИСТОРИИ");
        ////////////////////////////

        mng.getTask(sTask1.toInt());
        mng.getTask(sTask2.toInt());
        mng.getTask(sTask3.toInt());

        List<Task> out = mng.getHistory();
        System.out.println(out);

        System.out.println("Конец");

    }
}
