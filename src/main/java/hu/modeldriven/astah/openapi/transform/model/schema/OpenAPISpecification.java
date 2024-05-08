package hu.modeldriven.astah.openapi.transform.model.schema;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.model.IPackage;
import hu.modeldriven.astah.openapi.transform.model.modelapi.AstahModelAPI;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelAPI;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.type.TypeLibrary;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class OpenAPISpecification {

    private final String url;
    private final ModelAPI modelAPI;

    private final TypeLibrary typeLibrary;

    public OpenAPISpecification(String url, IPackage targetPackage) {
        this.url = url;
        this.typeLibrary = new TypeLibrary();
        this.modelAPI = new AstahModelAPI(targetPackage, getSysmlModelEditor(), typeLibrary.getTypes());
    }

    private SysmlModelEditor getSysmlModelEditor() {
        try {
            return AstahAPI.getAstahAPI().getProjectAccessor().getModelEditorFactory().getSysmlModelEditor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void build() throws ModelBuildingException {

        try {
            OpenAPIParser parser = new OpenAPIParser();
            SwaggerParseResult result = parser.readLocation(url, null, null);

            Components components = result.getOpenAPI().getComponents();

            ITransactionManager transactionManager = AstahAPI.getAstahAPI().getProjectAccessor().getTransactionManager();

            try {
                transactionManager.beginTransaction();

                OpenAPISchemas schemas = new OpenAPISchemas(components.getSchemas());
                schemas.build(modelAPI);

                transactionManager.endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                transactionManager.abortTransaction();
                throw new Exception(e);
            }

        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }
    }

}
