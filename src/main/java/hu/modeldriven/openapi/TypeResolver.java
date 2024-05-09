package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IValueType;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.media.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class TypeResolver {

    private static final Logger logger = LoggerFactory.getLogger(TypeResolver.class);

    private static final String OPEN_API_PATH = "OpenAPI";

    private final AstahRepresentation astah;

    public TypeResolver(AstahRepresentation astah) {
        this.astah = astah;
        createTypesIfNotExists();
    }

    private void createTypesIfNotExists() {
        try {
            astah.beginTransaction();

            var typePackage = astah.findOrCreatePackage(OPEN_API_PATH);

            Stream.of("DateTime", "String", "Boolean", "Integer", "Number")
                    .forEach(name -> {
                        if (findByTypeName(astah, name) == null) {
                            astah.createValueType(typePackage, name);
                        }
                    });

            astah.commitTransaction();
        } catch (Exception e) {
            astah.abortTransaction();
            throw new AstahRuntimeException(e);
        }
    }

    public IClass resolve(Schema<?> schema, ModelElementsStore store) {

        if (isArray(schema)) {
            if (hasArrayReference(schema)) {
                var name = getReferenceTypeNameOfArray(schema);
                return store.get(name);
            } else if (hasArrayObject(schema)) {
                // FIXME not implemented
                logger.info("Arrays containing objects not implemented");
                return null;
            } else if (hasArrayArray(schema)) {
                // FIXME not implemented
                logger.info("Arrays containing arrays not implemented");
                return null;
            } else {
                return getCoreTypeOfArray(schema);
            }
        }

        if (isReference(schema)) {
            String name = getReferenceTypeName(schema);
            return store.get(name);
        }

        if (isEnum(schema)) {
            String name = getReferenceTypeName(schema);
            return store.get(name + "Enum");
        }

        return resolveCoreType(schema);
    }

    private IClass resolveCoreType(Schema<?> schema) {

        return switch (schema) {
            case DateTimeSchema d -> findByTypeName(astah, "DateTime");
            case StringSchema s -> findByTypeName(astah, "String");
            case BooleanSchema b -> findByTypeName(astah, "Boolean");
            case IntegerSchema i -> findByTypeName(astah, "Integer");
            case NumberSchema n -> findByTypeName(astah, "Number");
            default -> null;
        };

    }

    private IValueType findByTypeName(AstahRepresentation astah, String typeName) {
        return astah.findElementByPath(OPEN_API_PATH, typeName, IValueType.class);
    }

    private boolean isEnum(Schema<?> schema) {
        return schema instanceof StringSchema && ((StringSchema) schema).getEnum() != null;
    }

    private boolean isArray(Schema<?> schema) {
        return schema instanceof ArraySchema;
    }

    private boolean hasArrayReference(Schema<?> schema) {
        return schema.getItems().get$ref() != null;
    }

    private boolean hasArrayObject(Schema<?> schema) {
        return schema.getItems() instanceof ObjectSchema;
    }

    private boolean hasArrayArray(Schema<?> schema) {
        return schema.getItems() instanceof ArraySchema;
    }

    private IClass getCoreTypeOfArray(Schema<?> schema) {
        return resolveCoreType(schema.getItems());
    }

    private String getReferenceTypeNameOfArray(Schema<?> schema) {
        var items = schema.getItems();

        if (items.get$ref() != null) {
            return new SchemaReference(items).getName();
        }

        throw new ModelBuildingRuntimeException("[TypeResolver.class] Array does not contain a reference");
    }

    private boolean isReference(Schema<?> schema) {
        return schema.get$ref() != null;
    }

    private String getReferenceTypeName(Schema<?> schema) {
        return new SchemaReference(schema).getName();
    }

}
