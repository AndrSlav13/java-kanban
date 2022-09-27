import interfaces.TaskManager;
import managers.Managers;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Варианты менеджера
        //TaskManager mng = Managers.getDefault();                                      //Без сохранения в файл
        TaskManager mng = Managers.getFileBackedWithInitialData("qwe.txt");        //Начальные данные из файла, сохранение в него же
        //TaskManager mng = Managers.getFileBacked("qwe.txt");                          //Сохранение в файл без начальных данных

//Перед добавлением subtask необходимо добавить epic-task

        Task task1 = new Task("Выучить джава", "");
        mng.addTask(task1);
        Task task2 = new Task("Сделать спринт", "");
        mng.addTask(task2);

        EpicTask epicTask1 = new EpicTask("Дела перед уходом", "Не забыть сделать перед уходом на работу");
        mng.addEpicTask(epicTask1);
        SubTask sTask1 = new SubTask("Выключить свет", "", epicTask1);
        SubTask sTask2 = new SubTask("Покормить кота", "У него диета", epicTask1);
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

        out = mng.getHistory();
        for (Task task : out) System.out.println(task);

        System.out.println("Конец");

    }
}

