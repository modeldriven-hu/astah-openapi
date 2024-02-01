package hu.modeldriven.astah.openapi;


import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.astah.core.AstahModel;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISpecification;

public class TemplateAction implements IPluginActionDelegate {

    public Object run(IWindow window) {

        try {

            AstahModel model = new AstahModel();

            IPackage root = model.findPackage("MyPackage");

            OpenAPISpecification specification = new OpenAPISpecification(
                    "https://petstore3.swagger.io/api/v3/openapi.json",
                    root);

            specification.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
