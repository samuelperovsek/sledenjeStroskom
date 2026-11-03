package si.feri.stroski.exception;

public class StrosekNotFoundException extends RuntimeException {
    public StrosekNotFoundException(Long id) {
        super("Strošek z id " + id + " ne obstaja");
    }
}
