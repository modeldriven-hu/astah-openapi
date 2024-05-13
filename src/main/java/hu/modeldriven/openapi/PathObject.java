package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IInterfaceBlock;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PathObject {

    private static final Logger logger = LoggerFactory.getLogger(PathObject.class);

    private final PathItem item;

    public PathObject(PathItem item){
        this.item = item;
    }

    public void build(String path, BuildContext context){
        if (item.getGet() != null) {
            createOperation(path, HttpAction.GET, item.getGet(), context);
        }

        if (item.getPost() != null) {
            createOperation(path, HttpAction.POST, item.getPost(), context);
        }

        if (item.getPut() != null) {
            createOperation(path, HttpAction.PUT, item.getPut(), context);
        }

        if (item.getDelete() != null) {
            createOperation(path, HttpAction.DELETE, item.getDelete(), context);
        }

        if (item.getPatch() != null) {
            createOperation(path, HttpAction.PATCH, item.getPatch(), context);
        }
    }

    private void createOperation(String path, HttpAction action, Operation operation, BuildContext context){
        var interfaceBlock = findOrCreateInterfaceBlock(path, operation.getTags(), context);

        var request = createRequest(action, operation, context);
        var response = createResponse(action, operation, context);

        context.astah().createOperation(interfaceBlock, operation.getOperationId(), request, response);
    }

    private IClass createRequest(HttpAction action, Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Request");
        var request = context.astah().createBlock(context.targetPackage(), name);

        // FIXME add parameters

        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null){

            var jsonBody = operation.getRequestBody().getContent().get("application/json");

            if (jsonBody != null){
                var bodyType = context.typeResolver().getOrCreate(
                        "body",
                        request,
                        jsonBody.getSchema(),
                        context.store());

                if (bodyType instanceof IBlock bodyTypeAsBlock){
                    context.astah().createPartRelationship(request, "body", bodyTypeAsBlock);
                } else {
                    logger.info("Cannot set type for operation request: {} type is not " +
                            " IBlock but {}", operation.getOperationId(), bodyType.getClass().getName());
                }

            }
        }

        return request;
    }

    private IClass createResponse(HttpAction action, Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Response");
        var response = context.astah().createBlock(context.targetPackage(), name);
        // FIXME add parameters
        return response;
    }

    private IInterfaceBlock findOrCreateInterfaceBlock(String path, List<String> tags, BuildContext context) {
        String name;

        if (!tags.isEmpty()) {
            name = StringUtils.capitalize(tags.get(0) + "Service");
        } else {
            name = path.startsWith("/") ? path.substring(1) : path;
        }

        return context.astah().findOrCreateInterfaceBlock(context.targetPackage(), name);
    }

}
