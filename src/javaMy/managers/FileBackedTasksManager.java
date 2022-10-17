package managers;

import errors.FileFormatException;
import errors.ManagerSaveException;
import interfaces.HistoryManager;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static String standardTitle = "id,type,name,status,description,datetime,duration,epicReference";
    private String inputFile;

    protected FileBackedTasksManager() {
    }

    public FileBackedTasksManager(String file) {
        try {
            Path path = Paths.get(file);
            if (!Files.exists(path)) {
                path = Files.createFile(path);
                Files.writeString(path, standardTitle, StandardCharsets.UTF_8);
                Files.writeString(path, System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                Files.writeString(path, System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
            inputFile = file;
        } catch (IOException ex) {
            inputFile = null;
            throw new FileFormatException("Exception while file loading");
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> listHistory = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listHistory.size(); ++i) {
            sb.append(listHistory.get(i).toInt());
            if (i != listHistory.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        String[] massive = value.split(",");
        LinkedList<Integer> list = new LinkedList<>();
        for (String s : massive)
            list.addFirst(Integer.valueOf(s.trim()));
        return list;
    }

    private static Task fromString(String input) {
        String[] str = input.split(",");
        int id = Integer.valueOf(str[0]);
        String type = str[1].trim();
        String title = str[2].trim();
        TaskStatus status = TaskStatus.valueOf(str[3].trim());
        String description = str[4].trim();
        String duration = str[6].trim();
        String zoneTime = str[5].trim();
        Integer idEpic = null;
        if (str[7].trim().length() != 0) idEpic = Integer.valueOf(str[7].trim());
        Task task = null;
        switch (TaskType.valueOf(type)) {
            case TASK:
                task = new Task(title, description);
                break;
            case EPICTASK:
                task = new EpicTask(title, description);
                break;
            case SUBTASK:
                if (idEpic == null) throw new FileFormatException("Subtask is to contain reference to epictask");
                task = new SubTask(title, description, idEpic);
                break;
            default:
                throw new FileFormatException("wrong format");
        }
        task.setID(id);
        task.setStatus(status);
        if (duration.length() != 0 && zoneTime.length() != 0) {
            task.setStartTime(zoneTime);
            task.setDuration(Duration.parse(duration).toMinutes());
        }
        return task;
    }

    protected void save() {
        try (BufferedWriter outFile = new BufferedWriter(new FileWriter(inputFile, StandardCharsets.UTF_8))) {
            outFile.write(standardTitle);
            outFile.newLine();
            for (Task task : getTasks()) {
                outFile.write(task.toString());
                outFile.newLine();
            }
            for (Task task : getEpicTasks()) {
                outFile.write(task.toString());
                outFile.newLine();
            }
            for (Task task : getSubTasks()) {
                outFile.write(task.toString());
                outFile.newLine();
            }

            outFile.newLine();
            outFile.write(historyToString(historyManager));
        } catch (IOException ex) {
            throw new ManagerSaveException("Exception while file storage");
        }
    }

    private void loadTask(String input) throws FileFormatException {
        Task task = fromString(input);
        if (task instanceof EpicTask) {
            super.addEpicTask((EpicTask) task);
            return;
        }
        if (task instanceof SubTask) {
            super.addSubTask((SubTask) task);
            return;
        }
        if (task instanceof Task) {
            super.addTask(task);
            return;
        }
    }

    protected void load(String file) throws FileFormatException {
        try (BufferedReader inFile = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String str = inFile.readLine();
            while (inFile.ready()) {    //load tasks
                str = inFile.readLine();
                if (str.trim().length() == 0) break;
                loadTask(str);
            }

            //load history
            str = inFile.readLine();
            if (!inFile.ready() && (str == null || str.trim().length() == 0)) return;
            for (int i : FileBackedTasksManager.historyFromString(str)) {
                super.getTask(i);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new FileFormatException("Error in load function");
        }
    }

    @Override
    public Task getTask(final int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void addTask(Task task) {   //Simple-task insertion
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask eTask) {   //Epic-task insertion
        super.addEpicTask(eTask);
        save();
    }

    @Override
    public void addSubTask(SubTask sTask) { //Subtask insertion
        super.addSubTask(sTask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpicTasks() {
        super.deleteEpicTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteTask(final int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

}
