package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahRuntimeException;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPIObject {

    private final ComponentsObject components;
    private final PathsObject paths;
    private final Diagrams diagrams;

    public OpenAPIObject(OpenAPI openAPI) {
        this.components = new ComponentsObject(openAPI.getComponents());
        this.paths = new PathsObject(openAPI.getPaths());
        this.diagrams = new Diagrams();
    }

    public void build(BuildContext context) throws ModelBuildingException {
        try {
            context.astah().beginTransaction();

            context.typeResolver().createTypesIfNotExists();

            this.components.build(context);
            this.paths.build(context);
            this.diagrams.build(context);

            context.astah().commitTransaction();
        } catch (Exception e) {
            context.astah().abortTransaction();
            throw new AstahRuntimeException(e);
        }
    }


}
