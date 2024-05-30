package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueType;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public class TypeResolver {

    private static final String OPEN_API_PATH = "OpenAPI";

    private final AstahRepresentation astah;
    private final ModelElementsStore store;

    public TypeResolver(AstahRepresentation astah, ModelElementsStore store) {
        this.astah = astah;
        this.store = store;
    }

    public void createTypesIfNotExists() {
        var typePackage = astah.findOrCreatePackage(OPEN_API_PATH);

        Stream.of("DateTime", "String", "Boolean", "Integer", "Number")
                .forEach(name -> {
                    if (findByTypeName(astah, name) == null) {
                        astah.createValueType(typePackage, name);
                    }
                });
    }

    public IClass getOrCreate(IBlock owner, String fieldName, Schema<?> schema) {

        if (isArray(schema)) {
            if (hasArrayReference(schema)) {
                return store.get(getReferenceTypeNameOfArray(schema));
            } else if (hasArrayObject(schema)) {
                // FIXME not implemented
                AstahLogger.log("Arrays containing objects not implemented");
                return null;
            } else if (hasArrayArray(schema)) {
                // FIXME not implemented
                AstahLogger.log("Arrays containing arrays not implemented");
                return null;
            } else {
                return getCoreTypeOfArray(schema);
            }
        }

        if (isReference(schema)) {
            return store.get(getReferenceTypeName(schema));
        }

        if (isEnum(schema)) {

            var enumName = StringUtils.capitalize(owner.getName()) + StringUtils.capitalize(fieldName) + "Enum";

            return store.computeIfAbsent(enumName, name ->
                    astah.createEnumeration((IPackage) owner.getOwner(), name, ((StringSchema) schema).getEnum()));
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
