package errors;

public class HttpRequestFormatException extends HttpRequestUserException {
    public HttpRequestFormatException(String str) {
        super(str);
    }
}