package misc;

public class ParseException extends RuntimeException {
    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(Exception e) {
        super(e);
    }
}
