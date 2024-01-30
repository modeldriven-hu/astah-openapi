package hu.modeldriven.openapi.impl;

import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;

public class OpenAPISchemas {

    private Map<String, Schema> schemas;

    public OpenAPISchemas(Map<String, Schema> schemas){
        this.schemas = schemas;
    }

    public Map<String, OpenAPISchema> build() throws OpenAPIParseException {

        Map<String, OpenAPISchema> completedSchemas = new HashMap<>();

        int infiniteLoopCounter = 0;

        do {
            for (Map.Entry<String,Schema> schema : schemas.entrySet()){
                OpenAPISchema openAPISchema = new OpenAPISchema(schema.getValue());

                if (openAPISchema.isBuildable(completedSchemas)){
                    openAPISchema.build();
                    completedSchemas.put(schema.getKey(), openAPISchema);
                }
            }

            infiniteLoopCounter++;

        } while (completedSchemas.size() != schemas.size() && infiniteLoopCounter < schemas.size());

        if (infiniteLoopCounter == schemas.size()){
            throw new OpenAPIParseException("Infinite loop found for schema " + schemas.toString());
        }

        return completedSchemas;
    }

}
