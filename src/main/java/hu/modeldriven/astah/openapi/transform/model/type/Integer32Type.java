package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.type.resolver.TypeNotFoundException;
import hu.modeldriven.astah.openapi.transform.model.type.resolver.TypeResolver;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;

public class Integer32Type implements OpenAPIType {

    private final TypeResolver resolver;

    public Integer32Type(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof IntegerSchema && "int32".equals(schema.getFormat());
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws TypeNotFoundException {
        return new DefaultAstahModelElement(
                resolver.findByName(TypeResolver.CoreType.INTEGER),
                new NoConstraint());
    }
}
