package hu.modeldriven.astah.openapi.transform.model.schema;

import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelAPI;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public class OpenAPISchema {

    private final String name;
    private final Schema<?> schema;

    public OpenAPISchema(String name, Schema<?> schema) {
        this.name = name;
        this.schema = schema;
    }

    public boolean isResolvable(Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {

        for (Schema<?> type : schema.getProperties().values()) {
            if (isReferenceNotResolvable(type, resolvedSchemas)) {
                return false;
            }
        }

        return true;
    }

    private boolean isReferenceNotResolvable(Schema<?> type, Map<String, OpenAPISchema> resolvedItems) throws ModelBuildingException {

        Schema<?> schema = (type instanceof ArraySchema) ? type.getItems() : type;

        if (schema.get$ref() != null) {
            String schemaName = new SchemaReference(schema.get$ref()).getName();

            if (schemaName != null) {
                return !resolvedItems.containsKey(schemaName);
            } else {
                throw new ModelBuildingException("Wrong schema reference format:  " + schema.get$ref());
            }
        }

        return false;
    }

    public void build(ModelAPI modelAPI) throws ModelBuildingException {
        modelAPI.createModelType(this.name, this.schema);
    }
}
