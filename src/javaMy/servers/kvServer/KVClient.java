package servers.kvServer;

import errors.HttpRequestUserException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private final URI uriToKVServer;
    private Long token = null;

    public KVClient(URI uri) {
        this.uriToKVServer = uri;
        token = loadToken();
    }

    public void put(String key, String json) {
        try {
            URI uri = URI.create(uriToKVServer.toString() + "/save/" + key + "?API_TOKEN=" + token);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.ofString(json))    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();
            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            // отправляем запрос и получаем ответ от сервера
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() >= 400)
                throw new HttpRequestUserException(response.statusCode(), response.body());
        } catch (HttpRequestUserException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            throw new HttpRequestUserException(ex.getMessage());
        }
    }

    public String load(String key) {
        HttpResponse<String> response = null;
        try {
            URI uri = URI.create(uriToKVServer.toString() + "/load/" + key + "?API_TOKEN=" + token);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .GET()    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();

            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

            // отправляем запрос и получаем ответ от сервера
            response = client.send(request, handler);
            if (response.statusCode() >= 400)
                throw new HttpRequestUserException(response.statusCode(), response.body());
        } catch (HttpRequestUserException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        } catch (Exception ex) {
            throw new HttpRequestUserException(ex.getMessage());
        }
        return response.body();
    }

    public long getToken() {
        if (token == null) throw new HttpRequestUserException("illegal token value");
        return token;
    }

    private Long loadToken() {
        try {
            URI uri = URI.create(uriToKVServer.toString() + "/register");
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .GET()    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                    .header("Accept", "application/json") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
            HttpClient client = HttpClient.newHttpClient();
            // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            // отправляем запрос и получаем ответ от сервера
            HttpResponse<String> response = client.send(request, handler);
            long reg = Long.parseLong(response.body());
            return reg;
        } catch (Exception ex) {
            System.out.println("Unable to get token for KVServer access");
        }
        return null;
    }

}

