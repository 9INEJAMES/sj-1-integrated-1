package int221.integrated1backend.exceptions;

public class UnauthenticatedException extends NotFoundException {
    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException() {
        super("You are not authenticated to perform this action");
    }
}
