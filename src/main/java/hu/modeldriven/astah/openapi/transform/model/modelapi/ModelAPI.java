package hu.modeldriven.astah.openapi.transform.model.modelapi;

import com.change_vision.jude.api.inf.model.INamedElement;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public interface ModelAPI {
    INamedElement createModelType(String name, Schema<?> schema, Map<String, OpenAPISchema> createdElements) throws ModelBuildingException;

}
