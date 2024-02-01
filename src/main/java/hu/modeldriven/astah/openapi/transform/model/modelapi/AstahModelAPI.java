package hu.modeldriven.astah.openapi.transform.model.modelapi;

import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueAttribute;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeNotFoundException;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISchema;
import hu.modeldriven.astah.openapi.transform.model.schema.SchemaReference;
import hu.modeldriven.astah.openapi.transform.model.type.OpenAPIType;
import io.swagger.v3.oas.models.media.Schema;

import java.util.List;
import java.util.Map;

public class AstahModelAPI implements ModelAPI {

    private final IPackage targetPackage;
    private final SysmlModelEditor editor;

    private final List<OpenAPIType> openAPITypes;

    public AstahModelAPI(IPackage targetPackage, SysmlModelEditor editor, List<OpenAPIType> openAPITypes) {
        this.targetPackage = targetPackage;
        this.editor = editor;
        this.openAPITypes = openAPITypes;
    }

    @Override
    public INamedElement createModelType(String modelName, Schema<?> schema, Map<String, OpenAPISchema> createdElements) throws ModelBuildingException {

        try {
            System.err.println("Creating model type: " + modelName);

            IBlock block = editor.createBlock(targetPackage, modelName);

            // Create attributes

            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {

                String attributeName = property.getKey();
                Schema<?> attributeType = property.getValue();

                System.err.println("Searching type for attribute: " + attributeName);

                handleBasicTypes(block, schema, attributeName, attributeType);

                System.err.println("Searching reference for attribute: " + attributeName);

                handleReference(block, createdElements, attributeType);

            }

            return block;

        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }
    }

    private void handleBasicTypes(IBlock owner, Schema<?> ownerSchema, String attributeName, Schema<?> attributeType) throws InvalidEditingException, TypeNotFoundException {

        for (OpenAPIType openAPIType : openAPITypes) {

            if (openAPIType.appliesTo(attributeType)) {

                System.err.println("Found basic type for attribute: " + attributeName);

                AstahModelElement modelElement = openAPIType.create(ownerSchema, attributeType);
                IValueAttribute attribute = editor.createValueAttribute(owner, attributeName, modelElement.getType());
                modelElement.getConstraint().apply(attribute);
                break;
            }
        }
    }

    private void handleReference(IBlock owner, Map<String, OpenAPISchema> createdElements, Schema<?> attributeType) {
        if (attributeType.get$ref() != null) {
            SchemaReference reference = new SchemaReference(attributeType.get$ref());

            if (createdElements.containsKey(reference.getName())) {
                INamedElement associatedElement = createdElements.get(reference.getName()).getElement();
                System.out.println(associatedElement);
            } else {
                System.err.println("Reference type " + attributeType + " not found.");
            }
        }

    }

}
