package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeNotFoundException;
import io.swagger.v3.oas.models.media.Schema;

public interface OpenAPIType {

    boolean appliesTo(Schema schema);

    AstahModelElement create(Schema parent, Schema element) throws TypeNotFoundException;

}
