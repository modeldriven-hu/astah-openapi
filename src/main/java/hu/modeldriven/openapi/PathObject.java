package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IInterfaceBlock;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathObject {

    private static final Logger logger = LoggerFactory.getLogger(PathObject.class);

    private final PathItem item;

    public PathObject(PathItem item) {
        this.item = item;
    }

    public void build(String path, BuildContext context) {
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

    private void createOperation(String path, HttpAction action, Operation operation, BuildContext context) {
        var interfaceBlock = findOrCreateInterfaceBlock(path, operation, context);

        var request = createRequest(action, operation, context);
        var response = createResponse(action, operation, context);

        context.astah().createOperation(interfaceBlock, operation.getOperationId(), request, response);
    }

    private void createBody(IBlock owner, Content content, String connectionName, BuildContext context) {
        var jsonBody = content.get("application/json");

        // FIXME handle not only application/json but also application/xml as well

        if (jsonBody != null) {
            var bodyType = context.typeResolver().getOrCreate(
                    connectionName,
                    owner,
                    jsonBody.getSchema(),
                    context.store());

            if (bodyType instanceof IBlock bodyTypeAsBlock) {
                context.astah().createPartRelationship(owner, connectionName, bodyTypeAsBlock);
            }
        }
    }

    private IClass createRequest(HttpAction action, Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Request");
        var request = context.astah().createBlock(context.targetPackage(), name);

        // FIXME add parameters

        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            createBody(request, operation.getRequestBody().getContent(), "body", context);
        }

        return request;
    }

    private IClass createResponse(HttpAction action, Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Response");
        var response = context.astah().createBlock(context.targetPackage(), name);

        // FIXME support for multiple response types
        if (operation.getResponses().containsKey("200") && operation.getResponses().get("200").getContent() != null) {
            var content = operation.getResponses().get("200").getContent();
            createBody(response, content, "_200_ok", context);
        }

        return response;
    }

    private IInterfaceBlock findOrCreateInterfaceBlock(String path, Operation operation, BuildContext context) {
        String name;

        if (!operation.getTags().isEmpty()) {
            name = StringUtils.capitalize(operation.getTags().get(0) + "Service");
        } else {
            name = path.startsWith("/") ? path.substring(1) : path;
        }

        return context.astah().findOrCreateInterfaceBlock(context.targetPackage(), name);
    }

}
