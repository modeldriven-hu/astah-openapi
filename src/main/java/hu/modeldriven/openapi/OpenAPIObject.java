package hu.modeldriven.openapi;

import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPIObject {

    private final ComponentsObject components;

    public OpenAPIObject(OpenAPI openAPI) {
        this.components = new ComponentsObject(openAPI.getComponents());
    }

    public void build(BuildContext instruction) throws ModelBuildingException {
        this.components.build(instruction);
    }


}
