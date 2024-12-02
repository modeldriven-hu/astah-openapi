package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class SchemaObject implements BuildableSchema{

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

    @Override
    public void buildSchema(String name, BuildContext context) {

        AstahLogger.log("[SchemaObject.class] Building schema: " + name);

        var block = context.astah().createBlock(context.targetPackage(), name);
        context.store().put(name, block);
    }

    @Override
    public void buildProperties(String name, BuildContext context) {

        var schemaBlock = context.store().get(name);

        if (!(schemaBlock instanceof IBlock block)) {
            AstahLogger.log("\t[SchemaBlock] element was not a block: " + schemaBlock);
            return;
        }

        for (var entry : schemaProperties.entrySet()) {

            AstahLogger.log("\t[SchemaObject] Building property: " + entry.getKey());

            SchemaProperty schemaProperty = entry.getValue();
            schemaProperty.build(entry.getKey(), schema, block, context);

            AstahLogger.log("\t[SchemaObject] Building Completed");
        }
     }

}
