package hu.modeldriven.astah.openapi.transform.model;

import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public interface ModelAPI {
    String createModelType(Schema<?> schema, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException;

}
