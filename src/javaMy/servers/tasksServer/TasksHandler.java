package servers.tasksServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import errors.HttpRequestFormatException;
import errors.HttpRequestUserException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import static util.AdaptersAndFormat.gson;

class TasksHandler extends TasksHandlerBase implements HttpHandler {
    public TasksHandler(String context, Function<String, Object> post,
                        Function<Integer, Object> delete,
                        Function<Integer, Object> get,
                        Supplier<Object> getAll,
                        Supplier<Object> deleteAll) {
        super(context);
        this.post = post;
        this.delete = delete;
        this.get = get;
        this.getAll = getAll;
        this.deleteAll = deleteAll;
    }

    @Override
    protected String isGET(String path, HttpExchange httpExchange) {
        Matcher matchId = pattId.matcher(path);
        Matcher match = patt.matcher(path);
        int id;
        if (match.find()) {
            if (getAll == null) throw new HttpRequestUserException("The request is not allowed");
            return gson.toJson(getAll.get());
        }
        if (matchId.find()) {
            if (get == null) throw new HttpRequestUserException("The request is not allowed");
            id = getId(path, matchId);
            return gson.toJson(get.apply(id));
        }
        throw new HttpRequestFormatException("check request '" + path + "' format");
    }

    @Override
    protected String isPOST(String path, HttpExchange httpExchange) throws IOException {
        Matcher match = patt.matcher(path);
        if (match.find()) {
            if (post == null) throw new HttpRequestUserException("The request is not allowed");
            InputStream is = httpExchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return gson.toJson(post.apply(body));
        }
        throw new HttpRequestFormatException("check request '" + path + "' format");
    }

    @Override
    protected String isDELETE(String path, HttpExchange httpExchange) {
        Matcher matchId = pattId.matcher(path);
        Matcher match = patt.matcher(path);
        int id;
        if (match.find()) {
            if (deleteAll == null) throw new HttpRequestUserException("The request is not allowed");
            return gson.toJson(deleteAll.get());
        }
        if (matchId.find()) {
            if (delete == null) throw new HttpRequestUserException("The request is not allowed");
            id = getId(path, matchId);
            return gson.toJson(delete.apply(id));
        }
        throw new HttpRequestFormatException("check request '" + path + "' format");
    }
}