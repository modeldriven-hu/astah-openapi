package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueType;
import hu.modeldriven.astah.core.AstahLogger;
import hu.modeldriven.astah.core.AstahRepresentation;
import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
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

        Stream.of("DateTime", "String", "Boolean", "Integer", "Number", "UUID", "Map", "Email", "Password")
                .forEach(name -> {
                    if (findByTypeName(astah, name) == null) {
                        astah.createValueType(typePackage, name);
                    }
                });
    }

    public IClass getOrCreate(IBlock owner, String fieldName, Schema<?> schema) {

        if (isReference(schema)) {
            return store.get(getReferenceTypeName(schema));
        }

        if (isArray(schema)) {
            return handleArraySchema(schema);
        }

        if (isEnum(schema)) {
            return handleEnumSchema(owner, fieldName, schema);
        }

        if (isObjectSchema(schema)) {
            return handleObjectSchema(owner, fieldName, schema);
        }

        return resolveCoreType(schema);
    }

    private IClass handleArraySchema(Schema<?> schema) {
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

    private IClass handleEnumSchema(IBlock owner, String fieldName, Schema<?> schema) {
        var enumName = StringUtils.capitalize(owner.getName()) + StringUtils.capitalize(fieldName) + "Enum";

        return store.computeIfAbsent(enumName, name ->
                astah.createEnumeration((IPackage) owner.getOwner(), name, ((StringSchema) schema).getEnum()));
    }

    private IClass handleObjectSchema(IBlock owner, String fieldName, Schema<?> schema) {
        var recordName = StringUtils.capitalize(owner.getName()) + StringUtils.capitalize(fieldName) + "Record";

        var iClass = store.computeIfAbsent(recordName, name ->
                astah.createBlock((IPackage) owner.getOwner(), name));

        var block = (IBlock) iClass;

        if (schema.getProperties() != null){
            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {
                var name = property.getKey();
                var type = getOrCreate(block, property.getKey(), property.getValue());

                if (type instanceof IBlock typeBlock) {
                    astah.createPartRelationship(block, name, typeBlock);
                } else {
                    astah.createValueAttribute(block, name, type);
                }
            }
        }

        return block;
    }

    public IClass createGlobalEnum(IPackage parentPackage, String enumName, Schema<?> schema) {
        if (isEnum(schema)) {
            return store.computeIfAbsent(enumName, name ->
                    astah.createEnumeration(parentPackage, name, ((StringSchema) schema).getEnum()));
        }

        return resolveCoreType(schema);
    }

    private IClass resolveCoreType(Schema<?> schema) {

        return switch (schema) {
            case EmailSchema e -> findByTypeName(astah, "Email");
            case PasswordSchema p -> findByTypeName(astah, "Password");
            case MapSchema m -> findByTypeName(astah, "Map");
            case StringSchema s -> findByTypeName(astah, "String");
            case UUIDSchema u -> findByTypeName(astah, "UUID");
            case DateTimeSchema d -> findByTypeName(astah, "DateTime");
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

    private boolean isObjectSchema(Schema<?> schema) {
        return schema instanceof ObjectSchema;
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
