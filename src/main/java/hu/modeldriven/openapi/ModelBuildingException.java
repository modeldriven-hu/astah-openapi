package hu.modeldriven.openapi;

public class ModelBuildingException extends Exception {

    public ModelBuildingException(String message) {
        super(message);
    }

    public ModelBuildingException(Exception e){
        super(e);
    }

}
