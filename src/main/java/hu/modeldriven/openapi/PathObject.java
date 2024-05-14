package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IInterfaceBlock;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PathObject {

    private static final Logger logger = LoggerFactory.getLogger(PathObject.class);

    private final PathItem item;

    public PathObject(PathItem item) {
        this.item = item;
    }

    public void build(String path, BuildContext context) {

        BiFunction<HttpAction, Operation , Void> invoker = (action, operation) -> {
            if (operation != null) {
                createOperation(path, action, operation, context);
            }
            return null; // Functional methods typically return a value, null here indicates success
        };

        invoker.apply(HttpAction.GET, item.getGet());
        invoker.apply(HttpAction.POST, item.getPost());
        invoker.apply(HttpAction.PUT, item.getPut());
        invoker.apply(HttpAction.DELETE, item.getDelete());
        invoker.apply(HttpAction.PATCH, item.getPatch());
    }

    private void createOperation(String path, HttpAction action, Operation operation, BuildContext context) {
        var interfaceBlock = findOrCreateInterfaceBlock(path, operation, context);

        var request = createRequest(operation, context);
        var response = createResponse(operation, context);

        var op = context.astah().createOperation(interfaceBlock, operation.getOperationId(), request, response,
                operation.getDescription());

        context.astah().addStereotype(op, StringUtils.capitalize(action.name().toLowerCase()));

        context.store().put(interfaceBlock.getName(), interfaceBlock);
        context.store().put(request.getName(), request);
        context.store().put(response.getName(), response);
    }

    private IClass createRequest(Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Request");
        var request = context.astah().createBlock(context.targetPackage(), name);

        context.astah().addStereotype(request, "HTTP Request");

        for (var parameter : emptyIfNull(operation.getParameters())) {
            createParameter(request, parameter, context);
        }

        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            createBody(request, operation.getRequestBody().getContent(), "body", context);
        }

        return request;
    }

    private void createParameter(IBlock owner, Parameter parameter, BuildContext context) {
        var name = parameter.getName();
        var type = context.typeResolver().getOrCreate(owner, name, parameter.getSchema());
        var attribute = context.astah().createValueAttribute(owner, name, type, parameter.getDescription());

        context.astah().setMultiplicity(attribute, !parameter.getRequired() ? 0 : 1, 1);

        // FIXME create stereotype based on parameter.getIn()
    }

    private IClass createResponse(Operation operation, BuildContext context) {
        var name = StringUtils.capitalize(operation.getOperationId() + "Response");
        var response = context.astah().createBlock(context.targetPackage(), name);

        context.astah().addStereotype(response, "HTTP Response");

        // FIXME support for multiple response types
        if (operation.getResponses().containsKey("200") && operation.getResponses().get("200").getContent() != null) {
            var content = operation.getResponses().get("200").getContent();
            createBody(response, content, "_200_ok", context);
        }

        return response;
    }

    private void createBody(IBlock owner, Content content, String connectionName, BuildContext context) {
        var jsonBody = content.get("application/json");

        // FIXME handle not only application/json but also application/xml as well

        if (jsonBody != null) {
            var bodyType = context.typeResolver().getOrCreate(
                    owner,
                    connectionName,
                    jsonBody.getSchema());

            if (bodyType instanceof IBlock bodyTypeAsBlock) {
                context.astah().createPartRelationship(owner, connectionName, bodyTypeAsBlock);
            }
        }
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

    private <T> List<T> emptyIfNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

}
