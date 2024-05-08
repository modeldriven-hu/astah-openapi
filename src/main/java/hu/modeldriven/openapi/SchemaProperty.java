package hu.modeldriven.openapi;

import astah.AstahRepresentation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class SchemaProperty {

    private static final Logger logger = LoggerFactory.getLogger(SchemaProperty.class);

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

    public void build(String name, Schema<?> blockSchema, IBlock owner, BuildInstruction instruction, Map<String, IBlock> modelElements) {
        AstahRepresentation astah = instruction.astah();

        IClass type = instruction.typeResolver().resolve(schema, modelElements);

        if (type == null && !(schema instanceof ObjectSchema || schema instanceof ArraySchema)) {
            logger.info("***** [SchemaProperty.class] Type not supported: {}", schema);
        }

        // based on the type, we are adding an attribute or a part relationship

        final IAttribute attribute;

        if (type instanceof IBlock) {
            attribute = astah.createPartRelationship(owner, name, (IBlock) type);
        } else {
            attribute = astah.createValueAttribute(owner, name, type);
        }

        instruction.schemaPropertyMetadata().forEach(m -> m.applyTo(name, schema, blockSchema, attribute));
    }

}