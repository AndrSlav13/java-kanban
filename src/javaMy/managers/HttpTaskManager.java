package managers;

import interfaces.TaskManager;
import servers.kvServer.KVClient;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.AdaptersAndFormat;

import java.net.URI;

import static util.AdaptersAndFormat.gson;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {
    private final KVClient client;

    public HttpTaskManager(final URI uri) {
        client = new KVClient(uri);
        load(null);  //Try to load initial data from default storage
    }

    public String getKey() {
        return String.valueOf(client.getToken());
    }

    @Override
    protected void save() {
        String data = gson.toJson(this);
        client.put(String.valueOf(client.getToken()), data);
    }

    @Override
    protected void load(String keyLoad) {
        try {
            String key = keyLoad;
            if (keyLoad == null) key = getKey();

            AdaptersAndFormat.structAllTasks data;
            data = gson.fromJson(client.load(key), AdaptersAndFormat.structAllTasks.class);

            for (EpicTask task : data.epics)
                addEpicTask(task);
            for (SubTask task : data.subTasks)
                addSubTask(task);
            for (Task task : data.tasks)
                addTask(task);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
