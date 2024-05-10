package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPIObject {

    private final ComponentsObject components;
    private final PathsObject paths;

    public OpenAPIObject(OpenAPI openAPI) {
        this.components = new ComponentsObject(openAPI.getComponents());
        this.paths = new PathsObject(openAPI.getPaths());
    }

    public void build(BuildContext context) throws ModelBuildingException {
        try {
            context.astah().beginTransaction();

            this.components.build(context);
            this.paths.build(context);

            context.astah().commitTransaction();
        } catch (Exception e){
            context.astah().abortTransaction();
            throw new AstahRuntimeException(e);
        }
    }


}
