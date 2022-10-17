package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.HttpTaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdaptersAndFormat {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm | VV | ZZZZZ");
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(AdaptersAndFormat.structAllTasks.class, new AdaptersAndFormat.ToManagerSerializer())
            .registerTypeAdapter(Task.class, new AdaptersAndFormat.TaskAdapter())
            .registerTypeAdapter(EpicTask.class, new AdaptersAndFormat.EpicTaskAdapter())
            .registerTypeAdapter(SubTask.class, new AdaptersAndFormat.SubTaskAdapter())
            .registerTypeAdapter(HttpTaskManager.class, new AdaptersAndFormat.ManagerSerializer())
            .create();
    public static Type taskListType = new TypeToken<List<Task>>() {
    }.getType();
    public static Type epicListType = new TypeToken<List<EpicTask>>() {
    }.getType();
    public static Type subTaskListType = new TypeToken<List<SubTask>>() {
    }.getType();

    public static class structAllTasks {
        public List<Task> tasks;
        public List<EpicTask> epics;
        public List<SubTask> subTasks;

        public structAllTasks() {
            tasks = null;
            epics = null;
            subTasks = null;
        }
    }

    public static class LocalDateAdapter extends TypeAdapter<ZonedDateTime> {
        public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm | VV | ZZZZZ");

        @Override
        public void write(final JsonWriter jsonWriter, final ZonedDateTime localDate) throws IOException {
            jsonWriter.value(localDate.format(formatter));
        }

        @Override
        public ZonedDateTime read(final JsonReader jsonReader) throws IOException {
            return ZonedDateTime.parse(jsonReader.nextString(), formatter);
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(duration.toString());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.parse(jsonReader.nextString());
        }
    }

    public static class TaskAdapter extends TypeAdapter<Task> {
        @Override
        public void write(final JsonWriter jsonWriter, final Task task) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(task.toInt());
            jsonWriter.name("type").value("TASK");
            jsonWriter.name("title").value(task.getTitle());
            jsonWriter.name("description").value(task.getDescription());
            jsonWriter.name("status").value(task.getStatus().toString());
            if (task.getStartTime().isPresent())
                jsonWriter.name("start_time").value(task.getStartTime().get().format(LocalDateAdapter.formatter));
            if (task.getDuration().isPresent())
                jsonWriter.name("duration").value(task.getDuration().get().toMinutes());
            jsonWriter.endObject();
        }

        @Override
        public Task read(final JsonReader jsonReader) throws IOException {
            Task task = new Task("", "");
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "id":
                        task.setID(Integer.parseInt(jsonReader.nextString()));
                        break;
                    case "title":
                        task.setTitle(jsonReader.nextString());
                        break;
                    case "description":
                        task.setDescription(jsonReader.nextString());
                        break;
                    case "status":
                        task.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                        break;
                    case "start_time":
                        task.setStartTime(jsonReader.nextString());
                        break;
                    case "duration":
                        String ss = jsonReader.nextString();
                        task.setDuration(Integer.parseInt(ss));
                        break;
                    default:
                        jsonReader.skipValue();
                        break;
                }
            }
            jsonReader.endObject();
            return task;
        }
    }

    public static class EpicTaskAdapter extends TypeAdapter<EpicTask> {
        @Override
        public void write(final JsonWriter jsonWriter, final EpicTask task) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(task.toInt());
            jsonWriter.name("type").value("EPICTASK");
            jsonWriter.name("title").value(task.getTitle());
            jsonWriter.name("description").value(task.getDescription());
            jsonWriter.name("status").value(task.getStatus().toString());
            if (task.getStartTime().isPresent())
                jsonWriter.name("start_time").value(task.getStartTime().get().format(LocalDateAdapter.formatter));
            if (task.getDuration().isPresent())
                jsonWriter.name("duration").value(task.getDuration().get().toMinutes());
            jsonWriter.endObject();
        }

        @Override
        public EpicTask read(final JsonReader jsonReader) throws IOException {
            EpicTask task = new EpicTask("", "");
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "id":
                        task.setID(Integer.parseInt(jsonReader.nextString()));
                        break;
                    case "title":
                        task.setTitle(jsonReader.nextString());
                        break;
                    case "description":
                        task.setDescription(jsonReader.nextString());
                        break;
                    case "status":
                        task.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                        break;
                    case "start_time":
                        task.setStartTime(jsonReader.nextString());
                        break;
                    case "duration":
                        String ss = jsonReader.nextString();
                        task.setDuration(Integer.parseInt(ss));
                        break;
                    default:
                        jsonReader.skipValue();
                        break;
                }
            }
            jsonReader.endObject();
            return task;
        }
    }

    public static class SubTaskAdapter extends TypeAdapter<SubTask> {
        @Override
        public void write(final JsonWriter jsonWriter, final SubTask task) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(task.toInt());
            jsonWriter.name("idEpic").value(task.getEpicTaskID());
            jsonWriter.name("type").value("SUBTASK");
            jsonWriter.name("title").value(task.getTitle());
            jsonWriter.name("description").value(task.getDescription());
            jsonWriter.name("status").value(task.getStatus().toString());
            if (task.getStartTime().isPresent())
                jsonWriter.name("start_time").value(task.getStartTime().get().format(LocalDateAdapter.formatter));
            if (task.getDuration().isPresent())
                jsonWriter.name("duration").value(task.getDuration().get().toMinutes());
            jsonWriter.endObject();
        }

        @Override
        public SubTask read(final JsonReader jsonReader) throws IOException {
            SubTask task = new SubTask("", "", 0);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "id":
                        task.setID(Integer.parseInt(jsonReader.nextString()));
                        break;
                    case "idEpic":
                        task.setEpicTaskID(Integer.parseInt(jsonReader.nextString()));
                        break;
                    case "title":
                        task.setTitle(jsonReader.nextString());
                        break;
                    case "description":
                        task.setDescription(jsonReader.nextString());
                        break;
                    case "status":
                        task.setStatus(TaskStatus.valueOf(jsonReader.nextString()));
                        break;
                    case "start_time":
                        task.setStartTime(jsonReader.nextString());
                        break;
                    case "duration":
                        String ss = jsonReader.nextString();
                        task.setDuration(Integer.parseInt(ss));
                        break;
                    default:
                        jsonReader.skipValue();
                        break;
                }
            }
            jsonReader.endObject();
            return task;
        }
    }

    public static class ManagerSerializer implements JsonSerializer<HttpTaskManager> {
        @Override
        public JsonElement serialize(HttpTaskManager mng, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject el = new JsonObject();
            el.add("listOfTasks", context.serialize(mng.getTasks(), taskListType));
            el.add("listOfEpics", context.serialize(mng.getEpicTasks(), epicListType));
            el.add("listOfSubTasks", context.serialize(mng.getSubTasks(), subTaskListType));
            return el;
        }
    }


    public static class ToManagerSerializer implements JsonDeserializer<structAllTasks> {
        @Override
        public structAllTasks deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            structAllTasks struct = new structAllTasks();
            JsonObject data = json.getAsJsonObject();

            struct.subTasks = context.deserialize(data.getAsJsonArray("listOfSubTasks"), subTaskListType);
            struct.epics = context.deserialize(data.getAsJsonArray("listOfEpics"), epicListType);
            struct.tasks = context.deserialize(data.getAsJsonArray("listOfTasks"), taskListType);

            return struct;
        }
    }


}
