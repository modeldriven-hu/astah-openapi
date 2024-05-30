package hu.modeldriven.openapi;
import io.swagger.v3.oas.models.media.StringSchema;

public class SchemaString {

    private final String name;
    private final StringSchema schema;

    public SchemaString(String name, StringSchema schema) {
        this.name = name;
        this.schema = schema;
    }

    public void build(BuildContext context) {
        context.typeResolver().createGlobalEnum(context.targetPackage(), name, schema);
    }

}