package hu.modeldriven.astah.openapi.transform.model;

public class ModelBuildingException extends Exception {

    public ModelBuildingException(String message) {
        super(message);
    }

    public ModelBuildingException(Exception e){
        super(e);
    }

}
