package hu.modeldriven.astah.openapi.transform.model.schema;

import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelAPI;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Map;

public class OpenAPISchemas {

    private final Map<String, Schema> schemas;

    public OpenAPISchemas(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    public Map<String, OpenAPISchema> build(ModelAPI modelAPI) throws ModelBuildingException {

        Map<String, OpenAPISchema> builtSchemas = new HashMap<>();

        int infiniteLoopCounter = 0;

        do {
            for (Map.Entry<String, Schema> schema : schemas.entrySet()) {

                if (!builtSchemas.containsKey(schema.getKey())) {
                    OpenAPISchema openAPISchema = new OpenAPISchema(schema.getKey(), schema.getValue());

                    if (openAPISchema.isResolvable(builtSchemas)) {
                        openAPISchema.build(modelAPI, builtSchemas);
                        builtSchemas.put(schema.getKey(), openAPISchema);
                    }
                }
            }

            infiniteLoopCounter++;

        } while (builtSchemas.size() != schemas.size() && infiniteLoopCounter < schemas.size());

        if (infiniteLoopCounter == schemas.size()) {
            throw new ModelBuildingException("Infinite loop found for schema " + schemas);
        }

        return builtSchemas;
    }

}
