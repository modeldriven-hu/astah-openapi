package hu.modeldriven.astah.openapi.transform.model.modelapi;

import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueAttribute;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
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
    public String createModelType(String modelName, Schema<?> schema) throws ModelBuildingException {

        try {
            System.err.println("Creating model type: " + modelName);

            IBlock block = editor.createBlock(targetPackage, modelName);

            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {

                String name = property.getKey();
                Schema<?> type = property.getValue();

                System.err.println("Searching type for attribute: " + name);

                boolean found = false;

                // Handle basic types

                for (OpenAPIType openAPIType : openAPITypes) {

                    if (openAPIType.appliesTo(type)) {

                        System.err.println("Found basic type for attribute: " + name);

                        AstahModelElement modelElement = openAPIType.create(schema, type);
                        IValueAttribute attribute = editor.createValueAttribute(block, name, modelElement.getType());
                        modelElement.getConstraint().apply(attribute);
                        found = true;
                        break;
                    }
                }

                // Handle reference


                // Handle array

                if (!found) {
                    System.err.println("Type " + type + " not implemented.");
                }
            }

            return block.getId();

        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }
    }

}
