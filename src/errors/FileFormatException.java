package errors;

public class FileFormatException extends RuntimeException {
    public FileFormatException(String str) {
        super(str);
    }
}
