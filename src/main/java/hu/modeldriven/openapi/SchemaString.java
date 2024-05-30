package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IEnumerationValueType;
import io.swagger.v3.oas.models.media.StringSchema;

public class SchemaString {

    private final String name;
    private final StringSchema schema;

    public SchemaString(String name, StringSchema schema) {
        this.name = name;
        this.schema = schema;
    }

    public IEnumerationValueType build(BuildContext context) {
        var enumeration = context.astah().createEnumeration(context.targetPackage(), name, schema.getEnum());
        return enumeration;
    }

}