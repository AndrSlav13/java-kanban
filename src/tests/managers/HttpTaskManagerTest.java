package managers;

import org.junit.jupiter.api.AfterEach;
import servers.kvServer.KVServer;

import java.net.URI;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private String path = "http://localhost:8078";
    private KVServer kvServer = new KVServer();

    protected HttpTaskManager createMng() {
        kvServer.start();
        URI url = URI.create(path);
        return new HttpTaskManager(url);
    }

    @AfterEach
    protected void aft() {
        kvServer.stop();
    }
}