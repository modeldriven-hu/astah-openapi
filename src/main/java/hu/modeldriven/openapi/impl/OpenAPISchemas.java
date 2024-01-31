package hu.modeldriven.openapi.impl;

import hu.modeldriven.openapi.ModelAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;

public class OpenAPISchemas {

    private Map<String, Schema> schemas;

    public OpenAPISchemas(Map<String, Schema> schemas){
        this.schemas = schemas;
    }

    public Map<String, OpenAPISchema> build(ModelAPI modelAPI) throws OpenAPIParseException {

        Map<String, OpenAPISchema> builtSchemas = new HashMap<>();

        int infiniteLoopCounter = 0;

        do {
            for (Map.Entry<String,Schema> schema : schemas.entrySet()){
                OpenAPISchema openAPISchema = new OpenAPISchema(schema.getValue());

                if (openAPISchema.isResolvable(builtSchemas)){
                    openAPISchema.build(modelAPI);
                    builtSchemas.put(schema.getKey(), openAPISchema);
                }
            }

            infiniteLoopCounter++;

        } while (builtSchemas.size() != schemas.size() && infiniteLoopCounter < schemas.size());

        if (infiniteLoopCounter == schemas.size()){
            throw new OpenAPIParseException("Infinite loop found for schema " + schemas.toString());
        }

        return builtSchemas;
    }

}
