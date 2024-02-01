package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeNotFoundException;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeResolver;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;

public class DateTimeType implements OpenAPIType {

    private final TypeResolver resolver;

    public DateTimeType(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof DateTimeSchema && "date-time".equals(schema.getFormat());
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws TypeNotFoundException {
        return new DefaultAstahModelElement(
                resolver.findCoreTypeByName(TypeResolver.CoreType.DATETIME),
                new NoConstraint());
    }
}
