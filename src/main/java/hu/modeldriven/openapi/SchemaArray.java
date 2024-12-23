package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.Schema;

public class SchemaArray implements BuildableSchema{

    private final Schema<?> schema;

    public SchemaArray(Schema<?> schema) {
        this.schema = schema;
    }

    @Override
    public void buildSchema(String name, BuildContext context) {
        AstahLogger.log("[SchemaArray.class] Building schema: " + name);

        var block = context.astah().createBlock(context.targetPackage(), name);
        context.store().put(name, block);
    }

    @Override
    public void buildProperties(String name, BuildContext context) {
        // FIXME implement missing feature
    }
}
