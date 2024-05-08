package hu.modeldriven.openapi;

import astah.AstahRuntimeException;
import com.change_vision.jude.api.inf.model.IBlock;
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

        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            if (entry.getValue() instanceof ObjectSchema) {
                schemaObjects.put(entry.getKey(), new SchemaObject(entry.getValue()));
            } else if (entry.getValue() instanceof ArraySchema) {
                this.schemaArrays.put(entry.getKey(), new SchemaArray(entry.getValue()));
            } else {
                logger.info("[ComponentSchemas] Schema type not supported {}", entry.getValue().getClass());
            }
        }

    }

    public void build(BuildInstruction instruction) throws ModelBuildingException {

        try {
            instruction.astah().beginTransaction();

            // First we create the schema objects

            // order entries by resolvability
            Map<String, SchemaObject> orderedSchemaObjects = orderByResolvability(schemaObjects);

            Map<String, IBlock> modelElements = new LinkedHashMap<>();

            // create model representation
            for (Map.Entry<String, SchemaObject> entry : orderedSchemaObjects.entrySet()) {

                logger.info("[ComponentSchemas.class] Building schema: {}", entry.getKey());

                // Create frame as a SysML block

                IBlock block = instruction.astah().createBlock(instruction.targetPackage(), entry.getKey());
                modelElements.put(entry.getKey(), block);

                // Create inner parts of the block, like fields

                SchemaObject schemaObject = entry.getValue();
                schemaObject.build(block, instruction, modelElements);
            }

            // Then we create the schema arrays that are referencing the previously created
            // schema objects

            for (Map.Entry<String, SchemaArray> entry : schemaArrays.entrySet()) {
                // FIXME TO BE implemented
                logger.info("Creation of schema arrays is not yet implemented");
            }

            instruction.astah().commitTransaction();
        } catch (Exception e){
            instruction.astah().abortTransaction();
            throw new AstahRuntimeException(e);
        }

    }

    private Map<String, SchemaObject> orderByResolvability(Map<String, SchemaObject> schemaObjects) throws ModelBuildingException {

        Map<String, SchemaObject> orderedSchemaObjects = new LinkedHashMap<>();

        int infiniteLoopCounter = 0;

        do {

            for (Map.Entry<String, SchemaObject> entry : schemaObjects.entrySet()) {

                String schemaName = entry.getKey();
                SchemaObject schemaObject = entry.getValue();

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
