package hu.modeldriven.openapi.impl;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;
import java.util.Set;

public class OpenAPISchema {

    private final Schema schema;

    public OpenAPISchema(Schema schema) {
        this.schema = schema;
    }

    public boolean isBuildable(Map<String, OpenAPISchema> builtSchemas) {

        Set<Map.Entry<String, Schema>> entrySet = schema.getProperties().entrySet();

        for (Map.Entry<String, Schema> property : entrySet){
            if (isReferenceProperty(property.getValue())){
                // FIXME handle reference property
                // look the name up in builtSchemas
                System.err.println("Ooops");
            }
        }

        return true;
    }

    private boolean isReferenceProperty(Schema schema){
        return schema.get$ref() != null;
    }

    public void build() {
    }
}
