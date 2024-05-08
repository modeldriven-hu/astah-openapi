package hu.modeldriven.astah.openapi;

import javax.swing.*;

import astah.AstahRepresentation;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.openapi.BuildInstruction;
import hu.modeldriven.openapi.ModelBuildingException;
import hu.modeldriven.openapi.OpenAPIObject;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class TemplateAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		JOptionPane.showMessageDialog(null, "Starting creating of openapi elements");

		OpenAPIParser parser = new OpenAPIParser();

		SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);
		OpenAPIObject openAPIObject = new OpenAPIObject(result.getOpenAPI());
		try {
			AstahRepresentation representation = new AstahRepresentation();
			var targetPackage = representation.findPackage("api");
			openAPIObject.build(new BuildInstruction(targetPackage));
		} catch (ModelBuildingException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
        return null;
    }
}
