package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentSchemas {

    private final Map<String, SchemaObject> schemaObjects;
    private final Map<String, SchemaArray> schemaArrays;
    private final Map<String, SchemaString> schemaStrings;

    public ComponentSchemas(Map<String, Schema> schemas) {

        this.schemaObjects = new LinkedHashMap<>();
        this.schemaArrays = new LinkedHashMap<>();
        this.schemaStrings = new LinkedHashMap<>();

        for (var entry : schemas.entrySet()) {

            AstahLogger.log("Parsing " + entry.getKey());

            switch (entry.getValue()) {
                case ObjectSchema schema -> this.schemaObjects.put(entry.getKey(), new SchemaObject(schema));
                case ArraySchema arraySchema -> this.schemaArrays.put(entry.getKey(), new SchemaArray(arraySchema));
                case StringSchema schema when schema.getEnum() != null ->
                        this.schemaStrings.put(entry.getKey(), new SchemaString(entry.getKey(), schema));
                case null, default ->
                        AstahLogger.log("[ComponentSchemas] Schema type not supported " + entry.getValue().getClass());
            }
        }

    }

    public void build(BuildContext context) {

        // build enums from schema strings
        for (var entry : schemaStrings.entrySet()) {
            entry.getValue().build(context);
        }

        // create schema objects first in order to ensure that
        // all references are resolved
        for (var entry : schemaObjects.entrySet()) {
            entry.getValue().buildSchema(entry.getKey(), context);
        }

        // create schema properties
        for (var entry : schemaObjects.entrySet()) {
            entry.getValue().buildProperties(entry.getKey(), context);
        }

        // Then we create the schema arrays that are referencing the previously created
        // schema objects

        for (Map.Entry<String, SchemaArray> entry : schemaArrays.entrySet()) {
            // FIXME TO BE implemented
            AstahLogger.log("Creation of schema arrays is not yet implemented");
        }

    }

}
