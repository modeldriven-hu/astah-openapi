package hu.modeldriven.astah.openapi.transform.model.modelapi;

import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueAttribute;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISchema;
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
    public String createModelType(String modelName, Schema<?> schema, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {

        try {

            System.err.println("Create block in package " +
                    targetPackage.getId() + ", " +
                    targetPackage.getName());

            System.err.println("Model name was: " + modelName);

            IBlock block = editor.createBlock(targetPackage, modelName);

            System.err.println("Block was created: " + block.getId());

            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {

                String name = property.getKey();
                Schema<?> type = property.getValue();

                boolean found = false;

                for (OpenAPIType openAPIType : openAPITypes) {

                    if (openAPIType.appliesTo(type)) {

                        System.err.println("Creating attribute for name: " + name);

                        AstahModelElement modelElement = openAPIType.create(schema, type);
                        IValueAttribute attribute = editor.createValueAttribute(block, name, modelElement.getType());
                        modelElement.getConstraint().apply(attribute);
                        found = true;
                        break;
                    }
                }

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
