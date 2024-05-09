package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentSchemas {

    private static final Logger logger = LoggerFactory.getLogger(ComponentSchemas.class);

    private final Map<String, SchemaObject> schemaObjects;
    private final Map<String, SchemaArray> schemaArrays;

    public ComponentSchemas(Map<String, Schema> schemas) {

        this.schemaObjects = new LinkedHashMap<>();
        this.schemaArrays = new LinkedHashMap<>();

        for (var entry : schemas.entrySet()) {
            if (entry.getValue() instanceof ObjectSchema) {
                schemaObjects.put(entry.getKey(), new SchemaObject(entry.getValue()));
            } else if (entry.getValue() instanceof ArraySchema) {
                this.schemaArrays.put(entry.getKey(), new SchemaArray(entry.getValue()));
            } else {
                logger.info("[ComponentSchemas] Schema type not supported {}", entry.getValue().getClass());
            }
        }

    }

    public void build(BuildInstruction instruction) {

        try {
            // Start a transaction
            instruction.astah().beginTransaction();

            // order entries by resolvability
            var orderedSchemaObjects = orderByResolvability(schemaObjects);
            var store = new ModelElementsStore();

            // create model representation
            for (var entry : orderedSchemaObjects.entrySet()) {

                logger.info("[ComponentSchemas.class] Building schema: {}", entry.getKey());

                // Create frame as a SysML block

                var block = instruction.astah().createBlock(instruction.targetPackage(), entry.getKey());
                store.put(entry.getKey(), block);

                // Create inner parts of the block, like fields

                var schemaObject = entry.getValue();
                schemaObject.build(block, instruction, store);
            }

            // Then we create the schema arrays that are referencing the previously created
            // schema objects

            for (Map.Entry<String, SchemaArray> entry : schemaArrays.entrySet()) {
                // FIXME TO BE implemented
                logger.info("Creation of schema arrays is not yet implemented");
            }

            instruction.astah().commitTransaction();
        } catch (Exception e) {
            instruction.astah().abortTransaction();
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
                        logger.info("[ComponentSchemas.class] Cannot resolve {}", schemaName);
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
