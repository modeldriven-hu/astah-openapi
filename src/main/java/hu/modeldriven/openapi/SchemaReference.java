package hu.modeldriven.openapi;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Arrays;

public class SchemaReference {

    private final String reference;

    public SchemaReference(Schema<?> schema) {
        if (schema.get$ref() == null) {
            throw new ModelBuildingRuntimeException("Schema reference cannot be null!");
        }
        this.reference = schema.get$ref();
    }

    public String getName() {
        return Arrays.stream(reference.split("/"))
                .reduce((first, second) -> second)
                .orElse(null);
    }
}
