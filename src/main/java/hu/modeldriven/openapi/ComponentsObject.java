package hu.modeldriven.openapi;

import io.swagger.v3.oas.models.Components;

public class ComponentsObject {

    private final ComponentSchemas componentSchemas;

    public ComponentsObject(Components components) {
        this.componentSchemas = new ComponentSchemas(components.getSchemas());
    }

    void build(BuildInstruction instruction) throws ModelBuildingException {
        this.componentSchemas.build(instruction);
    }

}
