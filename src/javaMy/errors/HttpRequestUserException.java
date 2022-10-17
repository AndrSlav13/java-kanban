package errors;

public class HttpRequestUserException extends RuntimeException {
    private final int code;

    public HttpRequestUserException(int code, String str) {
        super("" + code + " : " + str);
        this.code = code;
    }

    public HttpRequestUserException(String str) {
        this(400, str);
    }

    public int getCode() {
        return code;
    }
}