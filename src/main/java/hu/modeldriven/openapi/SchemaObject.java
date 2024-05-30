package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SchemaObject {

    private final ObjectSchema schema;
    private final Map<String, SchemaProperty> schemaProperties;

    public SchemaObject(ObjectSchema schema) {
        this.schema = schema;

        this.schemaProperties = (schema.getProperties() == null) ?
                Collections.emptyMap() :
                schema.getProperties().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new SchemaProperty(entry.getValue())
                        ));
    }

    public boolean isResolvable(Set<String> resolvedSchemaNames) {
        return schemaProperties.values().stream().allMatch(property -> property.isResolvable(resolvedSchemaNames));
    }

    public void build(IBlock owner, BuildContext context) {
        // it depends on the type of the property of what kind of connection has to be built

        for (var entry : schemaProperties.entrySet()) {

            AstahLogger.log("\t[SchemaObject] Building property: " + entry.getKey());

            SchemaProperty schemaProperty = entry.getValue();
            schemaProperty.build(entry.getKey(), schema, owner, context);

            AstahLogger.log("\t[SchemaObject] Building Completed");
        }
    }

}
