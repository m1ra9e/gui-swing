package home.gui.exception;

public final class SaveAsCancelException extends RuntimeException {

    private static final long serialVersionUID = -8283136952627268107L;

    public SaveAsCancelException(String message) {
        super(message);
    }
}
