package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        try {

            // build enums from schema strings
            for (var entry : schemaStrings.entrySet()) {
                entry.getValue().build(context);
            }

            // order entries by resolvability
            var orderedSchemaObjects = orderByResolvability(schemaObjects, schemaStrings);

            // create model representation
            for (var entry : orderedSchemaObjects.entrySet()) {
                entry.getValue().build(entry.getKey(), context);
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

    private Map<String, SchemaObject> orderByResolvability(Map<String, SchemaObject> schemaObjects, Map<String, SchemaString> schemaStrings) throws ModelBuildingException {

        var orderedSchemaObjects = new LinkedHashMap<String, SchemaObject>();

        var infiniteLoopCounter = 0;

        do {
            resolveCycle(schemaObjects, schemaStrings, orderedSchemaObjects);
            infiniteLoopCounter++;
        } while (orderedSchemaObjects.size() != schemaObjects.size() && infiniteLoopCounter < schemaObjects.size());

        if (orderedSchemaObjects.size() != schemaObjects.size()) {
            throw new ModelBuildingException("Infinite reference loop found in schema, the following items remained: "
                    + calculateDifference(schemaObjects, orderedSchemaObjects));
        }

        AstahLogger.log("All schemas are resolved, continuing...");

        return orderedSchemaObjects;
    }

    private void resolveCycle(Map<String, SchemaObject> schemaObjects, Map<String, SchemaString> schemaStrings, LinkedHashMap<String, SchemaObject> orderedSchemaObjects) {
        for (var entry : schemaObjects.entrySet()) {

            var schemaName = entry.getKey();
            var schemaObject = entry.getValue();

            if (!orderedSchemaObjects.containsKey(schemaName)) {

                var mergedKeys = Stream.concat(orderedSchemaObjects.keySet().stream(), schemaStrings.keySet().stream())
                        .collect(Collectors.toSet());

                if (schemaObject.isResolvable(mergedKeys)) {
                    orderedSchemaObjects.put(schemaName, schemaObject);
                } else {
                    AstahLogger.log("[ComponentSchemas.class] Cannot resolve " + schemaName);
                }

            }
        }
    }

    private String calculateDifference(Map<String, SchemaObject> schemaObjects, LinkedHashMap<String, SchemaObject> orderedSchemaObjects) {
        var set1 = new HashSet<>(schemaObjects.keySet());
        var set2 = new HashSet<>(orderedSchemaObjects.keySet());

        var uniqueToSet1 = schemaObjects.keySet().stream()
                .filter(s -> !set2.contains(s))
                .collect(Collectors.toSet());

        var uniqueToSet2 = orderedSchemaObjects.keySet().stream()
                .filter(s -> !set1.contains(s))
                .collect(Collectors.toSet());

        return Stream.concat(uniqueToSet1.stream(), uniqueToSet2.stream())
                .collect(Collectors.joining(", "));
    }

}
