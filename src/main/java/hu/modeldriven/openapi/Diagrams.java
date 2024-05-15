package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IBlockDefinitionDiagram;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IInterfaceBlock;
import com.change_vision.jude.api.inf.model.IOperation;
import org.apache.commons.lang3.stream.Streams;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Diagrams {

    public void build(BuildContext context) {
        for (var interfaceBlock : findInterfaceBlocks(context)) {
            for (var operation : interfaceBlock.getOperations()) {
                var diagram = context.astah().createBlockDiagram(context.targetPackage(), operation.getName());
                createDiagram(diagram, interfaceBlock, operation, context);
            }
        }
    }

    private void createDiagram(IBlockDefinitionDiagram diagram, IInterfaceBlock interfaceBlock, IOperation operation, BuildContext context) {
        var interfaceBlockNode = context.astah().addToDiagram(diagram, interfaceBlock, new Point2D.Double(116, 77));

        var top = interfaceBlockNode.getHeight() + interfaceBlockNode.getLocation().getY() + 20;

        requestType(operation).ifPresent(type ->
                addToDiagram(diagram, type, new Point2D.Double(116, top), context));

        responseType(operation).ifPresent(type -> addToDiagram(diagram, type,
                new Point2D.Double(500, top), context));
    }

    private void addToDiagram(IBlockDefinitionDiagram diagram, IClass type, Point2D location, BuildContext context) {
        var node = context.astah().addToDiagram(diagram, type, location);

        try {
            // FIXME figure out how to display the values
            node.setProperty("block_values_visibility", "true");
        } catch (InvalidEditingException e) {
            e.printStackTrace();
        }
    }

    private Optional<IClass> requestType(IOperation operation) {
        return Streams.of(operation.getParameters())
                .filter(p -> "in".equalsIgnoreCase(p.getDirection()))
                .map(p -> p.getType())
                .findFirst();
    }

    private Optional<IClass> responseType(IOperation operation) {
        return Optional.ofNullable(operation.getReturnType());
    }

    private List<IInterfaceBlock> findInterfaceBlocks(BuildContext context) {
        return context.store().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(IInterfaceBlock.class::isInstance)
                .map(IInterfaceBlock.class::cast)
                .toList();
    }

}
