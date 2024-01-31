package hu.modeldriven.openapi.impl.types;

import hu.modeldriven.openapi.impl.AstahModelElement;
import io.swagger.v3.oas.models.media.Schema;

public interface OpenAPIType {

    boolean appliesTo(Schema schema);

    AstahModelElement create(Schema parent, Schema element);

}
