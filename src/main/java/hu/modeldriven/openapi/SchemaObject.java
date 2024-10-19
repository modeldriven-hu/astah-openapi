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

    public void buildSchema(String name, BuildContext context) {

        AstahLogger.log("[ComponentSchemas.class] Building schema: " + name);

        var block = context.astah().createBlock(context.targetPackage(), name);
        context.store().put(name, block);
    }

    public void buildProperties(String name, BuildContext context) {

        var schemaBlock = context.store().get(name);

        if (schemaBlock instanceof IBlock block) {

            for (var entry : schemaProperties.entrySet()) {

                AstahLogger.log("\t[SchemaObject] Building property: " + entry.getKey());

                SchemaProperty schemaProperty = entry.getValue();
                schemaProperty.build(entry.getKey(), schema, block, context);

                AstahLogger.log("\t[SchemaObject] Building Completed");
            }

        } else {
            AstahLogger.log("\t[SchemaBlock] element was not a block: " + schemaBlock);
        }
    }

}
