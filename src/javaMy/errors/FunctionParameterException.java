package errors;

public class FunctionParameterException extends RuntimeException {
    public FunctionParameterException(String str) {
        super(str);
    }
}