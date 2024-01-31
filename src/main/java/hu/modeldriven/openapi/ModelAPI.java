package hu.modeldriven.openapi;

import hu.modeldriven.openapi.impl.OpenAPISchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public interface ModelAPI {
    String createSchema(Schema schema, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException;

}
