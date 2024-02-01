package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import io.swagger.v3.oas.models.media.Schema;

public interface OpenAPIType {

    boolean appliesTo(Schema schema);

    AstahModelElement create(Schema parent, Schema element) throws ModelBuildingException;

}
