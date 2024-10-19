package hu.modeldriven.openapi;

import io.swagger.v3.oas.models.media.StringSchema;

public class SchemaString implements BuildableSchema {

    private final StringSchema schema;

    public SchemaString(StringSchema schema) {
        this.schema = schema;
    }

    @Override
    public void buildSchema(String name, BuildContext context) {
        context.typeResolver().createGlobalEnum(context.targetPackage(), name, schema);
    }

    @Override
    public void buildProperties(String name, BuildContext context) {

    }
}