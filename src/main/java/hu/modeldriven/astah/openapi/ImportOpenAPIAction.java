package hu.modeldriven.astah.openapi;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.openapi.BuildContext;
import hu.modeldriven.openapi.ModelBuildingException;
import hu.modeldriven.openapi.OpenAPIObject;
import io.swagger.parser.OpenAPIParser;

import javax.swing.*;
import java.io.File;

public class ImportOpenAPIAction implements IPluginActionDelegate {

    public Object run(IWindow window) {

        try {
            var astah = new AstahRepresentation(AstahAPI.getAstahAPI().getProjectAccessor());

            var targetPackage = astah.selectedPackageInTree();

            if (targetPackage == null) {
                JOptionPane.showMessageDialog(window.getParent(), "Please select a package");
                return null;
            }

            var fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            var fileSelectionResult = fileChooser.showOpenDialog(window.getParent());

            if (fileSelectionResult == JFileChooser.APPROVE_OPTION) {
                var selectedFile = fileChooser.getSelectedFile();
                var parser = new OpenAPIParser();
                var result = parser.readLocation(selectedFile.getAbsolutePath(), null, null);
                var openAPIObject = new OpenAPIObject(result.getOpenAPI());

                openAPIObject.build(new BuildContext(targetPackage, astah));
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return null;
    }
}
