package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            if (entry.getValue() instanceof ObjectSchema schema) {
                this.schemaObjects.put(entry.getKey(), new SchemaObject(schema));
            } else if (entry.getValue() instanceof ArraySchema) {
                this.schemaArrays.put(entry.getKey(), new SchemaArray(entry.getValue()));
            } else if (entry.getValue() instanceof StringSchema schema && schema.getEnum() != null) {
                this.schemaStrings.put(entry.getKey(), new SchemaString(entry.getKey(), schema));
            }else {
                AstahLogger.log("[ComponentSchemas] Schema type not supported " + entry.getValue().getClass());
            }
        }

    }

    public void build(BuildContext context) {

        try {

            // build enums from schema strings
            for (var entry: schemaStrings.entrySet()){
                var enumeration = entry.getValue().build(context);
                context.store().put(entry.getKey(), enumeration);
            }

            // order entries by resolvability
            var orderedSchemaObjects = orderByResolvability(schemaObjects);

            // create model representation
            for (var entry : orderedSchemaObjects.entrySet()) {

                AstahLogger.log("[ComponentSchemas.class] Building schema: " + entry.getKey());

                // Create frame as a SysML block, FIXME: this is the responsibility of the entry

                var block = context.astah().createBlock(context.targetPackage(), entry.getKey());
                context.store().put(entry.getKey(), block);

                // Create inner parts of the block, like fields

                var schemaObject = entry.getValue();
                schemaObject.build(block, context);
            }

            // Then we create the schema arrays that are referencing the previously created
            // schema objects

            for (Map.Entry<String, SchemaArray> entry : schemaArrays.entrySet()) {
                // FIXME TO BE implemented
                AstahLogger.log("Creation of schema arrays is not yet implemented");
            }

        } catch (ModelBuildingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    private Map<String, SchemaObject> orderByResolvability(Map<String, SchemaObject> schemaObjects) throws ModelBuildingException {

        var orderedSchemaObjects = new LinkedHashMap<String, SchemaObject>();

        var infiniteLoopCounter = 0;

        do {
            for (var entry : schemaObjects.entrySet()) {

                var schemaName = entry.getKey();
                var schemaObject = entry.getValue();

                if (!orderedSchemaObjects.containsKey(schemaName)) {

                    if (schemaObject.isResolvable(orderedSchemaObjects.keySet())) {
                        orderedSchemaObjects.put(schemaName, schemaObject);
                    } else {
                        AstahLogger.log("[ComponentSchemas.class] Cannot resolve " + schemaName);
                    }

                }
            }

            infiniteLoopCounter++;

        } while (orderedSchemaObjects.size() != schemaObjects.size() && infiniteLoopCounter < schemaObjects.size());

        if (orderedSchemaObjects.size() != schemaObjects.size()) {
            throw new ModelBuildingException("Infinite reference loop found in schema");
        }

        return orderedSchemaObjects;
    }

}
