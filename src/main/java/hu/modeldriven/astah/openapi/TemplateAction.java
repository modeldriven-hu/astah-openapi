package hu.modeldriven.astah.openapi;


import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.astah.core.AstahModel;
import hu.modeldriven.astah.openapi.transform.model.resolver.AstahTypeResolver;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISpecification;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

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
