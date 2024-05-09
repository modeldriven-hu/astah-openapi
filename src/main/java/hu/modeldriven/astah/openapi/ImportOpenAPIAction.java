package hu.modeldriven.astah.openapi;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.openapi.BuildInstruction;
import hu.modeldriven.openapi.ModelBuildingException;
import hu.modeldriven.openapi.OpenAPIObject;
import io.swagger.parser.OpenAPIParser;

import javax.swing.*;
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
                var astah = new AstahRepresentation();
                var targetPackage = astah.findPackage("api");
                openAPIObject.build(new BuildInstruction(targetPackage, astah));
            } catch (ModelBuildingException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }

        }

        return null;
    }
}
