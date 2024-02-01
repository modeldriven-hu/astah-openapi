package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.type.resolver.TypeNotFoundException;
import hu.modeldriven.astah.openapi.transform.model.type.resolver.TypeResolver;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Schema;

public class BooleanType implements OpenAPIType {

    private final TypeResolver resolver;

    public BooleanType(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof BooleanSchema;
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws TypeNotFoundException {
        return new DefaultAstahModelElement(
                resolver.findByName(TypeResolver.CoreType.BOOLEAN),
                new NoConstraint());
    }
}
