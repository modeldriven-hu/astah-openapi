package hu.modeldriven.astah.openapi.transform.model;

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

    private final IPackage rootPackage;
    private final SysmlModelEditor editor;

    private final List<OpenAPIType> openAPITypes;

    public AstahModelAPI(IPackage rootPackage, SysmlModelEditor editor, List<OpenAPIType> openAPITypes) {
        this.rootPackage = rootPackage;
        this.editor = editor;
        this.openAPITypes = openAPITypes;
    }

    @Override
    public String createModelType(Schema<?> schema, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {

        try {
            IBlock block = editor.createBlock(rootPackage, schema.getName());
            block.setDefinition(schema.getDescription());

            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {

                String name = property.getKey();
                Schema<?> type = property.getValue();

                for (OpenAPIType openAPIType : openAPITypes) {

                    if (openAPIType.appliesTo(type)) {
                        AstahModelElement modelElement = openAPIType.create(schema, type);
                        IValueAttribute attribute = editor.createValueAttribute(block, name, modelElement.getType());
                        modelElement.getConstraint().apply(attribute);
                        break;
                    }

                }
            }

            return block.getId();

        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }
    }

}
