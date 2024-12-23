package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Set;

public class SchemaProperty {

    private final Schema<?> schema;

    public SchemaProperty(Schema<?> schema) {
        this.schema = schema;
    }

    public boolean isResolvable(Set<String> resolvedSchemaNames) {

        Schema<?> result = (this.schema instanceof ArraySchema) ? this.schema.getItems() : this.schema;

        if (result.get$ref() != null) {
            String schemaName = new SchemaReference(result).getName();

            if (schemaName != null) {
                return resolvedSchemaNames.contains(schemaName);
            }
        }

        return true;
    }

    public void build(String name, Schema<?> blockSchema, IBlock owner, BuildContext context) {
        var astah = context.astah();
        var type = context.typeResolver().getOrCreate(owner, name, schema);

        if (type == null) {
            context.typeResolver().getOrCreate(owner, name, schema);
            AstahLogger.log("***** [SchemaProperty.class] Type not supported: " + schema + " ,skipping....");
            return;
        }

        // based on the type, we are adding an attribute or a part relationship

        final IAttribute attribute;

        if (type instanceof IBlock block) {
            attribute = astah.createPartRelationship(owner, name, block);
        } else {
            attribute = astah.createValueAttribute(owner, name, type);
        }

        context.schemaPropertyMetadata().forEach(m -> m.applyTo(name, schema, blockSchema, attribute));
    }

}
