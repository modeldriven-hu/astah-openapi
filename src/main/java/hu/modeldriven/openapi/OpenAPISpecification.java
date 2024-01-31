package hu.modeldriven.openapi;

import io.swagger.models.Model;

import java.io.File;

public interface OpenAPISpecification {

    void build(ModelAPI modelAPI);

}
