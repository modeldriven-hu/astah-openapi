package hu.modeldriven.astah.openapi;

import javax.swing.*;

import astah.AstahRepresentation;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.openapi.BuildInstruction;
import hu.modeldriven.openapi.ModelBuildingException;
import hu.modeldriven.openapi.OpenAPIObject;
import io.swagger.parser.OpenAPIParser;

import java.io.File;

public class ImportOpenAPIAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		JOptionPane.showMessageDialog(null, "Starting creating of openapi elements");

		var fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

		var fileSelectionResult = fileChooser.showOpenDialog(window.getParent());

		if (fileSelectionResult == JFileChooser.APPROVE_OPTION) {
			var selectedFile = fileChooser.getSelectedFile();

			JOptionPane.showMessageDialog(null, "Reading file: " + selectedFile.getAbsolutePath());

			var parser = new OpenAPIParser();
			var result = parser.readLocation(selectedFile.getAbsolutePath(), null, null);
			var openAPIObject = new OpenAPIObject(result.getOpenAPI());

			try {
				var representation = new AstahRepresentation();
				var targetPackage = representation.findPackage("api");
				openAPIObject.build(new BuildInstruction(targetPackage));
			} catch (ModelBuildingException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

		}

        return null;
    }
}
