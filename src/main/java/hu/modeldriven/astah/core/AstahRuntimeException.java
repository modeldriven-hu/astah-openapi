package hu.modeldriven.astah.core;

public class AstahRuntimeException extends RuntimeException {

    public AstahRuntimeException(Exception e) {
        super(e);
    }

    public AstahRuntimeException(String message) {
        super(message);
    }

}
