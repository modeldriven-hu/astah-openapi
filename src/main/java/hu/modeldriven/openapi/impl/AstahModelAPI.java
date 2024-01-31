package hu.modeldriven.openapi.impl;

import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueAttribute;
import hu.modeldriven.openapi.ModelAPI;
import hu.modeldriven.openapi.ModelBuildingException;
import hu.modeldriven.openapi.impl.types.OpenAPIType;
import io.swagger.v3.oas.models.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public String createSchema(Schema schema, Map<String, OpenAPISchema> resolvedSchemas) throws ModelBuildingException {

        try {
            IBlock block = editor.createBlock(rootPackage, schema.getName());
            block.setDefinition(schema.getDescription());

            Set<Map.Entry<String, Schema>> properties = schema.getProperties().entrySet();

            for (Map.Entry<String, Schema> property : properties) {

                String name = property.getKey();
                Schema type = property.getValue();

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
