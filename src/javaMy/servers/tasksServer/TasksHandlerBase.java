package servers.tasksServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import errors.HttpRequestFormatException;
import errors.HttpRequestUserException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TasksHandlerBase implements HttpHandler {
    protected static final String regExGetId = "\\?id=-?\\d+$";     //id в строке параметров
    protected final String context;
    protected final Pattern patt;
    protected final Pattern pattId;

    //Функторы для обработки запросов
    protected Function<String, Object> post = null;
    protected Function<Integer, Object> delete = null;
    protected Function<Integer, Object> get = null;
    protected Supplier<Object> getAll = null;
    protected Supplier<Object> deleteAll = null;
    protected Function<String, Object> put = null;


    protected TasksHandlerBase(String context) {
        this.context = context;
        patt = Pattern.compile("^" + context + "$");
        pattId = Pattern.compile("^" + context + regExGetId);
    }

    protected String isGET(String path, HttpExchange httpExchange) {
        throw new HttpRequestUserException("the request isn't allowed");
    }

    protected String isPOST(String path, HttpExchange httpExchange) throws IOException {
        throw new HttpRequestUserException("the request isn't allowed");
    }

    protected String isPUT(String path, HttpExchange httpExchange) throws IOException {
        throw new HttpRequestUserException("the request isn't allowed");
    }

    protected String isDELETE(String path, HttpExchange httpExchange) {
        throw new HttpRequestUserException("the request isn't allowed");
    }

    protected String getContext() {
        return context;
    }

    protected int getId(String path, Matcher matchId) {
        return Integer.parseInt(path.substring(matchId.start() + context.length() + 4, matchId.end()));
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().toString();
            String rezult = null;
            int codeRespond = 400;

            switch (method) {
                case "GET":
                    rezult = isGET(path, httpExchange);
                    if (rezult == null) throw new HttpRequestFormatException("The body of GET request can't be absent");
                    codeRespond = 200;
                    break;
                case "POST":
                    rezult = isPOST(path, httpExchange);
                    codeRespond = 201;
                    break;
                case "DELETE":
                    rezult = isDELETE(path, httpExchange);
                    codeRespond = 200;
                    break;
                case "PUT":
                    rezult = isPUT(path, httpExchange);
                    codeRespond = 200;
                    break;
                default:
                    throw new HttpRequestUserException("The method '" + method + "'" + " is not implemented");
            }

            httpExchange.sendResponseHeaders(codeRespond, 0);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(rezult.getBytes());
            }
        } catch (HttpRequestUserException ex) {
            httpExchange.sendResponseHeaders(ex.getCode(), 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(ex.getMessage().getBytes());
            }
        } catch (Exception ex) {
            httpExchange.sendResponseHeaders(400, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(ex.getMessage().getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }
}