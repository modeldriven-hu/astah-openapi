package hu.modeldriven.astah.openapi.transform.model.modelapi;

import io.swagger.v3.oas.models.media.Schema;

public interface ModelAPI {
    String createModelType(String name, Schema<?> schema) throws ModelBuildingException;

}
