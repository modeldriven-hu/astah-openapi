package hu.modeldriven.openapi.impl;

import hu.modeldriven.openapi.ModelAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;
import java.util.Set;

public class OpenAPISchema {

    private final Schema schema;

    public OpenAPISchema(Schema schema) {
        this.schema = schema;
    }

    public boolean isResolvable(Map<String, OpenAPISchema> resolvedSchemas) throws OpenAPIParseException {

        Set<Map.Entry<String, Schema>> entrySet = schema.getProperties().entrySet();

        for (Map.Entry<String, Schema> property : entrySet) {

            Schema propertyType = property.getValue();

            if (propertyType instanceof ArraySchema) {

                ArraySchema arraySchema = (ArraySchema) propertyType;
                if (!isReferenceResolvable(arraySchema.getItems(), resolvedSchemas)) {
                    return false;
                }

            } else if (!isReferenceResolvable(propertyType, resolvedSchemas)) {
                return false;
            }
        }

        return true;
    }

    private boolean isReferenceResolvable(Schema schema, Map<String, OpenAPISchema> resolvedItems) throws OpenAPIParseException{

        if (schema.get$ref() != null) {
            String schemaName = new SchemaReference(schema.get$ref()).getName();

            if (schemaName != null) {
                return resolvedItems.keySet().contains(schemaName);
            } else {
                throw new OpenAPIParseException("Wrong schema reference format:  " + schema.get$ref());
            }
        }

        return true;
    }

    public void build(ModelAPI modelAPI) {
    }
}
