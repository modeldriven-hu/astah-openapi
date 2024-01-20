package hu.modeldriven.astah.openapi;


import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class TemplateAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		OpenAPIParser parser = new OpenAPIParser();

		SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

		Components components = result.getOpenAPI().getComponents();

		components.getSchemas().keySet().forEach(System.err::println);

		return null;
	}


}
