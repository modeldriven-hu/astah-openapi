package hu.modeldriven.astah.core;

import com.change_vision.jude.api.inf.editor.BlockDefinitionDiagramEditor;
import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AstahRepresentation {

    private final ProjectAccessor projectAccessor;
    private final SysmlModelEditor modelEditor;
    private final BlockDefinitionDiagramEditor blockDefinitionDiagramEditor;

    public AstahRepresentation(ProjectAccessor projectAccessor) {
        try {
            this.projectAccessor = projectAccessor;
            this.blockDefinitionDiagramEditor = projectAccessor.getDiagramEditorFactory().getBlockDefinitionDiagramEditor();
            this.modelEditor = projectAccessor.getModelEditorFactory().getSysmlModelEditor();
        } catch (InvalidEditingException | InvalidUsingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public void setMultiplicity(IAttribute attribute, int value) {
        try {
            attribute.setMultiplicity(new int[][]{{value}});
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public void setMultiplicity(IAttribute attribute, int min, int max) {
        try {
            attribute.setMultiplicity(new int[][]{{min, max}});
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IBlock createBlock(IPackage parent, String name) {
        try {
            return this.modelEditor.createBlock(parent, name);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IAttribute createPartRelationship(IBlock block, String name, IBlock child) {
        try {
            return this.modelEditor.createPart(block, name, child);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IAttribute createValueAttribute(IClass owner, String name, IClass type) {
        try {
            return modelEditor.createValueAttribute(owner, name, type);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IAttribute createValueAttribute(IClass owner, String name, IClass type, String definition) {
        try {
            var attribute = modelEditor.createValueAttribute(owner, name, type);
            attribute.setDefinition(definition);
            return attribute;
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }


    public <T extends INamedElement> T findElementByPath(String path, String name, Class<T> typeClass) {

        IPackage rootPackage = this.findPackage(path);

        for (INamedElement element : rootPackage.getOwnedElements()) {
            if (name.equals(element.getName()) && typeClass.isInstance(element)) {
                return typeClass.cast(element);
            }
        }

        return null;
    }

    public IPackage selectedPackageInTree() {
        try {
            var entities = projectAccessor.getViewManager().getProjectViewManager().getSelectedEntities();

            return Stream.of(entities)
                    .filter(e -> e instanceof IPackage)
                    .map(IPackage.class::cast)
                    .findFirst()
                    .orElse(null);

        } catch (InvalidUsingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IPackage findPackage(String path) {
        try {
            IPackage root = projectAccessor.getProject();
            return findPackage(root, path);
        } catch (ProjectNotFoundException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IPackage findPackage(IPackage rootPackage, String path) {

        String[] pathElements = path.split("/");

        IPackage lastMatchingPackage = rootPackage;

        for (String currentSubPathName : pathElements) {

            lastMatchingPackage = Arrays.stream(lastMatchingPackage.getOwnedElements())
                    .filter(IPackage.class::isInstance)
                    .map(IPackage.class::cast)
                    .filter(p -> currentSubPathName.equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            if (lastMatchingPackage == null) {
                throw new AstahRuntimeException("Package not found in path " + path + ", subpath " + currentSubPathName);
            }
        }

        return lastMatchingPackage;
    }

    public void beginTransaction() {
        projectAccessor.getTransactionManager().beginTransaction();
    }

    public void commitTransaction() {
        projectAccessor.getTransactionManager().endTransaction();
    }

    public void abortTransaction() {
        projectAccessor.getTransactionManager().abortTransaction();
    }

    public IPackage findOrCreatePackage(String path) {
        try {
            IPackage rootPackage = projectAccessor.getProject();

            // Split the package path into individual elements
            String[] packageElements = path.split("\\.");

            // Iterate through each element in the package path
            IPackage currentPackage = rootPackage;

            for (String packageElement : packageElements) {
                // Check if the package exists, if not, create it
                IPackage childPackage = findOrCreatePackage(currentPackage, packageElement);
                currentPackage = childPackage;
            }

            return currentPackage;

        } catch (ProjectNotFoundException e) {
            throw new AstahRuntimeException(e);
        }
    }

    private IPackage findOrCreatePackage(IPackage parentPackage, String packageName) {

        // Check if the child package exists
        for (INamedElement element : parentPackage.getOwnedElements()) {
            if (element instanceof IPackage childPackage) {
                if (childPackage.getName().equals(packageName)) {
                    return childPackage;
                }
            }
        }

        try {
            return modelEditor.createPackage(parentPackage, packageName);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public void createValueType(IPackage parentPackage, String name) {
        try {
            modelEditor.createValueType(parentPackage, name);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IEnumerationValueType createEnumeration(IPackage parentPackage, String name, List<String> values) {
        try {
            var enumeration = modelEditor.createEnumeration(parentPackage, name);

            for (var value : values) {
                enumeration.createEnumerationLiteral(value);
            }

            return enumeration;
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IOperation createOperation(IClass parentClass, String name, IClass parameter, IClass returnType, String definition) {
        try {
            var operation = modelEditor.createOperation(parentClass, name, returnType);

            if (definition != null) {
                operation.setDefinition(definition);
            }

            modelEditor.createParameter(operation, "request", parameter);

            return operation;
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IInterfaceBlock findOrCreateInterfaceBlock(IPackage parentPackage, String name) {
        try {

            for (var element : parentPackage.getOwnedElements()) {
                if (name.equals(element.getName()) && element instanceof IInterfaceBlock) {
                    return (IInterfaceBlock) element;
                }
            }

            return modelEditor.createInterfaceBlock(parentPackage, name);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public IBlockDefinitionDiagram createBlockDiagram(IPackage parentPackage, String name) {
        try {
            return blockDefinitionDiagramEditor.createBlockDefinitionDiagram(parentPackage, name);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public INodePresentation addToDiagram(IBlockDefinitionDiagram diagram, IElement model, Point2D location) {
        try {
            blockDefinitionDiagramEditor.setDiagram(diagram);
            return blockDefinitionDiagramEditor.createNodePresentation(model, location);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

    public void addStereotype(INamedElement namedElement, String stereotype) {
        try {
            namedElement.addStereotype(stereotype);
        } catch (InvalidEditingException e) {
            throw new AstahRuntimeException(e);
        }
    }

}
