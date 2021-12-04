package fluent;

public class UpliftNotPossibleException extends RuntimeException {

    public UpliftNotPossibleException() {
        super("Origin already reached. Make sure there is no redundant 'xxxThenBack' methods calls");
    }
}
