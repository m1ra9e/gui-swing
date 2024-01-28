package home.gui.exception;

public final class SaveAsToSameFileException extends RuntimeException {

    private static final long serialVersionUID = -4526765533335630589L;

    public SaveAsToSameFileException(String message) {
        super(message);
    }
}
