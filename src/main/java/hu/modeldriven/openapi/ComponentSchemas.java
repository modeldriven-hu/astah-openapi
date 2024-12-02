package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentSchemas {

    private final Map<String, BuildableSchema> schemaMap;

    public ComponentSchemas(Map<String, Schema> schemas) {

        this.schemaMap = new LinkedHashMap<>();

        for (var entry : schemas.entrySet()) {

            AstahLogger.log("Parsing " + entry.getKey());

            switch (entry.getValue()) {
                case ObjectSchema schema -> this.schemaMap.put(entry.getKey(), new SchemaObject(schema));
                case ArraySchema arraySchema -> this.schemaMap.put(entry.getKey(), new SchemaArray(arraySchema));
                case StringSchema schema when schema.getEnum() != null ->
                        this.schemaMap.put(entry.getKey(), new SchemaString(schema));
                case ComposedSchema composedSchema -> this.schemaMap.put(entry.getKey(), new SchemaComposed(composedSchema));
                case null, default ->
                        AstahLogger.log("[ComponentSchemas] Schema type not supported " + entry.getValue().getClass());
            }
        }

    }

    public void build(BuildContext context) {

        // First create the schemas without properties so that referencing will be possible
        for (var entry : schemaMap.entrySet()) {
            entry.getValue().buildSchema(entry.getKey(), context);
        }

        // Then create schema properties after the basic elements are already in place
        for (var entry : schemaMap.entrySet()) {
            entry.getValue().buildProperties(entry.getKey(), context);
        }
    }

}
