package hu.modeldriven.openapi.impl;

import hu.modeldriven.openapi.ModelAPI;
import hu.modeldriven.openapi.ModelBuildingException;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;
import java.util.Set;

public class OpenAPISchema {

    private final Schema schema;

    public OpenAPISchema(Schema schema) {
        this.schema = schema;
    }

    public boolean isResolvable(Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {

        Set<Map.Entry<String, Schema>> properties = schema.getProperties().entrySet();

        for (Map.Entry<String, Schema> property : properties) {

            Schema type = property.getValue();

            if (type instanceof ArraySchema) {

                ArraySchema arraySchema = (ArraySchema) type;
                if (!isReferenceResolvable(arraySchema.getItems(), resolvedSchemas)) {
                    return false;
                }

            } else if (!isReferenceResolvable(type, resolvedSchemas)) {
                return false;
            }
        }

        return true;
    }

    private boolean isReferenceResolvable(Schema schema, Map<String, OpenAPISchema> resolvedItems) throws ModelBuildingException{

        if (schema.get$ref() != null) {
            String schemaName = new SchemaReference(schema.get$ref()).getName();

            if (schemaName != null) {
                return resolvedItems.keySet().contains(schemaName);
            } else {
                throw new ModelBuildingException("Wrong schema reference format:  " + schema.get$ref());
            }
        }

        return true;
    }

    public void build(ModelAPI modelAPI, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {
        modelAPI.createSchema(this.schema, resolvedSchemas);
    }
}
