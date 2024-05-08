package hu.modeldriven.openapi;

public class ModelBuildingRuntimeException extends RuntimeException {

    public ModelBuildingRuntimeException(String message) {
        super(message);
    }

    public ModelBuildingRuntimeException(Exception e) {
        super(e);
    }

}
