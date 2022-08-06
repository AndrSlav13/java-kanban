import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.TaskStaging;
import types.TaskType;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Manager mng = new Manager();
        ArrayList<Task> out = new ArrayList<>();


//Перед добавлением subtask необходимо добавить epic-task

        EpicTask epicTask = new EpicTask("Дела перед уходом", "То, что надо не забыть сделать перед уходом на работу");
        mng.addTask(epicTask);                                                                                          //2.4
        SubTask sTask1 = new SubTask("Выключить свет", "", epicTask);
        SubTask sTask2 = new SubTask("Покормить кота", "У него диета", epicTask);
        SubTask sTask3 = new SubTask("Взять ключи", "И от машины", epicTask);
        mng.addSubTask(sTask1);
        mng.addSubTask(sTask2);
        mng.addSubTask(sTask3);

        EpicTask epicTask2 = new EpicTask("Экономить электричество", "");
        mng.addTask(epicTask2);
        SubTask sTask4 = new SubTask(sTask1, epicTask2);
        SubTask sTask5 = new SubTask("Купить диодные лампы", "Или люминесцентные", epicTask2);
        SubTask sTask6 = new SubTask("Починить проводку", "", epicTask2);
        mng.addSubTask(sTask4);
        mng.addSubTask(sTask5);
        mng.addSubTask(sTask6);

        EpicTask epicTask3 = new EpicTask("Сделать дела", "");
        mng.addTask(epicTask3);
        SubTask sTask7 = new SubTask("Дело 1", "", epicTask3);
        SubTask sTask8 = new SubTask(sTask2, epicTask3);
        mng.addSubTask(sTask7);
        mng.addSubTask(sTask8);

        ////////////////////////////
        System.out.println("ИСХОДНЫЕ ДАННЫЕ");
        ////////////////////////////
        out = mng.getTasks(TaskStaging.ALL);                                                                            //2.1
        output(mng, out);
        scanner.nextLine();
        ////////////////////////////
        System.out.println("ИЗМЕНЕНИЕ СТАТУСА ЗАДАЧИ И ОБНОВЛЕНИЕ");
        ////////////////////////////
        sTask1.setStatus(TaskType.DONE);                                                                                //4
        sTask2.setStatus(TaskType.DONE);
        mng.update(sTask1);                                                                                             //2.5
        mng.update(sTask2);
        out = mng.getTasks(TaskStaging.ALL);                                                                            //2.1
        output(mng, out);
        scanner.nextLine();
        ////////////////////////////
        System.out.println("ПОЛУЧЕНИЕ ПО ИДЕНТИФИКАТОРУ");
        ////////////////////////////
        System.out.println(mng.getTask(sTask2.toInt()));                                                                //2.3
        scanner.nextLine();
        ////////////////////////////
        System.out.println("УДАЛЕНИЕ ПО ИДЕНТИФИКАТОРУ");
        ////////////////////////////
        mng.deleteSubTask(epicTask3, sTask8);                                                                           //2.6
        mng.deleteSubTask(epicTask, 1);
        out = mng.getTasks(TaskStaging.ALL);                                                                            //2.1
        output(mng, out);
        scanner.nextLine();
        ////////////////////////////
        System.out.println("ПОЛУЧЕНИЕ ПОДЗАДАЧ ЭПИКА");
        ////////////////////////////
        out = mng.getSubTasks(epicTask);                                                                                //3
        output(mng, out);
        scanner.nextLine();
        ////////////////////////////
        System.out.println("УДАЛЕНИЕ ВСЕХ ЗАДАЧ (ПОДЗАДАЧ)");
        ////////////////////////////
        mng.deleteTasks(TaskStaging.SUBTASKS);                                                                          //2.2
        out = mng.getTasks(TaskStaging.ALL);                                                                            //2.1
        output(mng, out);
        scanner.nextLine();

        System.out.println("Конец");

    }

    public static void output(Manager mng, ArrayList<Task> list) {
        for (Task task : list)
            System.out.println(task);
    }
}
