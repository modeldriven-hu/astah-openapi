package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import io.swagger.v3.oas.models.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SchemaObject {

    private static final Logger logger = LoggerFactory.getLogger(SchemaObject.class);

    private final Schema<?> schema;
    private final Map<String, SchemaProperty> schemaProperties;

    public SchemaObject(Schema<?> schema) {
        this.schema = schema;

        this.schemaProperties = schema.getProperties().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new SchemaProperty(entry.getValue())));
    }

    public boolean isResolvable(Set<String> resolvedSchemaNames) {

        for (var property : schemaProperties.values()) {
            if (!property.isResolvable(resolvedSchemaNames)) {
                return false;
            }
        }

        return true;
    }

    public void build(IBlock owner, BuildInstruction instruction, ModelElementsStore store) {
        // it depends on the type of the property of what kind of connection has to be built

        for (var entry : schemaProperties.entrySet()) {

            logger.info("\t[SchemaObject] Building property: {}", entry.getKey());

            SchemaProperty schemaProperty = entry.getValue();
            schemaProperty.build(entry.getKey(), schema, owner, instruction, store);

            logger.info("\t[SchemaObject] Building Completed");
        }
    }


}
